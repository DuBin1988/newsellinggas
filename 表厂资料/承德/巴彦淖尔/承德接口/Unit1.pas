unit Unit1;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

type
  TForm1 = class(TForm)
    Button1: TButton;
    Button2: TButton;
    Button3: TButton;
    Button4: TButton;
    Button5: TButton;
    Button6: TButton;
    ComboBox1: TComboBox;
    Button7: TButton;
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
    procedure Button5Click(Sender: TObject);
    procedure Button6Click(Sender: TObject);
    procedure Button7Click(Sender: TObject);
    procedure Button8Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

{$R *.dfm}

type
  TDBUserInfo = packed record
    UserCode: array[0..16] of Char;
    UserName: array[0..63] of Char;
    Address: array[0..255] of Char;
    MeterCode: array[0..15] of Char;
    UseTypeName: array[0..31] of Char;
    TotalBuyAmount: Double;
    LimitedBuyAmount: Double;
    CardAmount: Double;
    Price: Double;
  end;
  PDBUserInfo = ^TDBUserInfo;

///{$DEFINE SHANXILIULIN}
//{$DEFINE CGMB}
//{$DEFINE MULITKEY}

const
{$IFDEF SHANXILIULIN}
  DLLNAME = 'Mwbjic_32.dll';
{$ELSE}
  DLLNAME = 'MWIC_32.dll';
{$ENDIF}
  TEST_KEY_ID = 0;

///////////��������
///function ic_init(port: Integer; baud: Integer): Integer; stdcall; far; external DLLNAME name 'auto_init';
///function ic_exit(icDev: Integer): Integer; stdcall; far; external DLLNAME name 'ic_exit';
procedure initial(COM: Integer); stdcall; far; external 'W32ICC.DLL' index 8;//name 'Initial';
procedure CloseHdl(); stdcall; far; external 'W32ICC.DLL' index 4;///name 'CloseHdl';

function ic_init(port: Integer; baud: Integer): Integer;
begin
  initial(port);
  Result := port;
end;

function ic_exit(icDev: Integer): Integer;
begin
  CloseHdl;
  Result := 0;
end;
/////////////////////////////////////////////
function rdcompany(icDev: Integer; isTrue: PChar): Integer; stdcall; far; external 'BGCard.dll' name 'rdcompany';
function readCard(icDev: Integer; userCode: PChar; cardAmount, meterAmount, testAmount: PSingle; inserted: PByte{$IFDEF CGMB}; saveInfo: PChar{$ENDIF}; UsedNumber: PInteger): Integer; stdcall; far; external 'BGCard.dll' name 'readCard';
//function makeCard(icDev: Integer; userCode: PChar; Amount: Single; KeyID: Integer; saveInfo: PChar): Integer; stdcall;
function makeCard(icDev: Integer; userCode: PChar; Amount: Single; {$IFDEF MULITKEY}KeyID: Integer; {$ENDIF}saveInfo: PChar; mark: Byte{$IFDEF CGMB}; meterType: Integer{$ENDIF}; LimiteAmount: Single; AlarmAmount: Single): Integer; stdcall; far; external 'BGCard.dll' name 'makeCard';
function writeCard(icDev: Integer; userCode: PChar; Amount: Single; {$IFDEF MULITKEY}KeyID: Integer; {$ENDIF}saveInfo: PChar; LimiteAmount: Single; AlarmAmount: Single): Integer; stdcall; far; external 'BGCard.dll' name 'writeCard';
function clearCard(icDev: Integer; userCode: PChar{$IFDEF MULITKEY}; KeyID: Integer{$ENDIF}): Integer; stdcall; far; external 'BGCard.dll' name 'clearCard';
function writeInfoCard(icDev: Integer{$IFDEF MULITKEY}; KeyID: Integer{$ENDIF}): Integer; stdcall; far; external 'BGCard.dll' name 'writeToolCard';
{$IFDEF CGMB}
function getUserCode(icDev: Integer; userCode: PChar): Integer; stdcall; far; external 'BGCard.dll' name 'getUserCode';
{$ENDIF}
function writeToolCard(icDev: Integer; {$IFDEF MULITKEY}KeyID: Integer; {$ENDIF}WriteType: Integer; TestAmount: Single; TestTimes: Integer): Integer; stdcall; far; external 'BGCard.dll' name 'writeToolCard';


////���淵����Ϣ
procedure WriteInfo(s: string);
var
  f: TextFile;
begin
  AssignFile(f, 'c:\saveinf.txt');
  ReWrite(f);
  WriteLn(f, s);
  CloseFile(f);
end;

////�����ϴ���Ϣ
function ReadInfo: string;
var
  f: TextFile;
begin
  Result := '';
  AssignFile(f, 'c:\saveinf.txt');
  Reset(f);
  ReadLn(f, Result);
  CloseFile(f);
  while Length(Result) < 16 do
    Result := Result + '0';
end;

procedure TForm1.Button1Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
  saveInfo: array[0..15] of Char;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try                    ////�û��ţ�   ������ ������Ϣ�����
    reVal := makeCard(icDev, '12345678', 10, {$IFDEF MULITKEY}TEST_KEY_ID,{$ENDIF} @saveInfo, 129, {$IFDEF CGMB}2,{$ENDIF} 1000, 5); ////129�����û��������
    if reVal = 0 then
    begin
      WriteInfo(saveInfo);
      MessageBox(Handle, 'д���ɹ�', '��Ϣ', MB_ICONINFORMATION);
    end else MessageBox(Handle, PChar('д��ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button2Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
  userCode: array[0..15] of Char;
  cardAmount, meterAmount, TestAmount: Single;
  inserted: Byte;
  times: Integer;
  saveInfo: array[0..15] of Char;
begin
  FillChar(userCode, 16, 0);
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try
    {$IFDEF CGMB}
    getUserCode(icDev, @userCode);
    Move(PChar(ReadInfo)^, saveInfo, 16);
    {$ENDIF}
                         ///�û��ţ�    ����������   �������� ��   ����������   �����������Ƿ����
    reVal := readCard(icDev, @userCode, @cardAmount, @meterAmount, @TestAmount, @inserted, {$IFDEF CGMB}@saveInfo,{$ENDIF} @times);
    if reVal = 0 then
      MessageBox(Handle, PChar('�����ɹ����û��ţ�' + userCode + ', ����������' + FormatFloat('0.0', cardAmount)), '��Ϣ', MB_ICONINFORMATION)
    else
      MessageBox(Handle, PChar('����ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
    ic_exit(icDev);
  finally

  end;
  
end;

procedure TForm1.Button3Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
  userCode: string;
  saveInfo: array[0..15] of Char;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try
    userCode := '12345678';
    Move(PChar(ReadInfo)^, saveInfo, 16);
                              ////�û��� ��   ������ ������Ϣ
    reVal := writeCard(icDev, PChar(userCode), 10, {$IFDEF MULITKEY}TEST_KEY_ID,{$ENDIF} @saveInfo, 1000, 5);
    if reVal = 0 then
    begin
      WriteInfo(saveInfo);
      MessageBox(Handle, 'д���ɹ���', '��Ϣ', MB_ICONINFORMATION)
    end else MessageBox(Handle, PChar('д��ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button4Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try               /////////  �û���
    reVal := clearCard(icDev, '12345678'{$IFDEF MULITKEY}, TEST_KEY_ID{$ENDIF});
    if reVal = 0 then
        MessageBox(Handle, '�忨�ɹ���', '��Ϣ', MB_ICONINFORMATION)
    else MessageBox(Handle, PChar('�忨ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button5Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
  saveInfo: array[0..15] of Char;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try
    Move(PChar(ReadInfo)^, saveInfo, 16);
                        /////�û��ţ�   ������ ������Ϣ�����
    reVal := makeCard(icDev, '12345678', 10, {$IFDEF MULITKEY}TEST_KEY_ID,{$ENDIF} @saveInfo, 1, {$IFDEF CGMB}2,{$ENDIF} 1000, 5); //mark=0 ���һ�ι���δ���뵽�����ڣ�mark=1 ���һ�ι��������뵽����
    if reVal = 0 then
    begin
      WriteInfo(saveInfo);
      MessageBox(Handle, 'д���ɹ�', '��Ϣ', MB_ICONINFORMATION);
    end else MessageBox(Handle, PChar('д��ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button6Click(Sender: TObject);
var
  icDev: Integer;
  isTrue: Byte;
  reVal: Integer;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try
    reVal := rdcompany(icDev, @isTrue);
    if reVal = 0 then
    begin
      if isTrue = 0 then
        MessageBox(Handle, '���ڿ�','��Ϣ', MB_ICONINFORMATION)
      else MessageBox(Handle, '�ǲ��ڿ�','��Ϣ', MB_ICONINFORMATION);
    end else MessageBox(Handle, PChar('�鿨ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button7Click(Sender: TObject);
var
  icDev: Integer;
begin
  icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try
    writeToolCard(icDev, {$IFDEF MULITKEY}TEST_KEY_ID,{$ENDIF} 5, 0, 0);//����˵����readme.txt
  finally
    ic_exit(icDev);
  end;
end;

procedure TForm1.Button8Click(Sender: TObject);
var
  icDev: Integer;
  reVal: Integer;
  userCode: array[0..7] of Char;
  cardAmount, meterAmount, TestAmount: Single;
  inserted: Byte;
  ui: TDBUserInfo;
begin
  {icDev := ic_init(ComboBox1.ItemIndex, 9600);
  if icDev < 1 then Exit;
  try                     ///�û��ţ�    ����������   �������� ��   ����������   �����������Ƿ����
    reVal := ReadCardInfo(icDev, @ui);
    if reVal = 0 then
      MessageBox(Handle, PChar('�����ɹ����û��ţ�' + ui.UserName + ', ����������' + FormatFloat('0.0', ui.CardAmount)
        + #13#10'�û���ַ:' + ui.Address), '��Ϣ', MB_ICONINFORMATION)
    else
      MessageBox(Handle, PChar('����ʧ�ܣ�����ţ�' + IntToStr(reVal)), '����', MB_ICONERROR);
  finally
    ic_exit(icDev);
  end;}
end;

end.
