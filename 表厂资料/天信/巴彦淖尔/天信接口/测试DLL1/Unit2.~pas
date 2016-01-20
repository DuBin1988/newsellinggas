unit Unit2;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

type
  TForm2 = class(TForm)
    Label11: TLabel;
    ComboBox1: TComboBox;
    Button2: TButton;
    GroupBox1: TGroupBox;
    Label1: TLabel;
    Edit1:  TEdit;
    Label2: TLabel;
    Edit2: TEdit;
    Label4: TLabel;
    Edit4: TEdit;
    Label5: TLabel;
    Edit5: TEdit;
    Label7: TLabel;
    Edit7: TEdit;
    Label8: TLabel;
    Edit8: TEdit;
    Button6: TButton;
    Label16: TLabel;
    ComboBox2: TComboBox;
    Button1: TButton;
    Button3: TButton;
    Button5: TButton;
    Button4: TButton;
    Button7: TButton;
    Label3: TLabel;
    Label6: TLabel;
    procedure Button2Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
    procedure Button5Click(Sender: TObject);
    procedure Button6Click(Sender: TObject);
    procedure Button7Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form2: TForm2;

//本DLL适合北京握奇CRW系列读卡器 ，写密钥传输卡和写用户卡及购气都用同一函数WriteUCard。 
//各函数返回值说明:0---函数成功执行;1---串口初始化错误;2---保留;3---用户卡复位错误;4---用户卡操作错误;5---PSAM卡复位错误;6---PSAM卡操作错误
//                                7---写标志错误;8---卡号过长;9---单位号过长;10---购气量过大;11---函数的单位号或卡号参数赋值同此用户卡不匹配

function IsSelf(port,rate:integer;pFlag,pErr:pchar):integer;stdcall;far;external 'Tancy_Card.DLL' name 'IsSelf';
//参数说明    串口号 波特率   本厂卡标志 错误信息         // pFlag='1'为本厂卡,0则否

function ReadUCard(Port,rate:integer;id,ickh,Gasvalue,fc,tpsw,pErr,ICMark:pchar):integer;stdcall;far;external 'Tancy_Card.DLL' name 'ReadUCard';
//参数说明       串口号 波特率    卡标识 卡号 气量值 充值次数 卡别 错误信息 用户号   //tpsw='0'为用户卡，tpsw='1'为开户卡（密钥传递卡），tpsw='2'新卡

function NewCardCheck(port,rate:integer;pFlag,pErr:pchar):integer;stdcall;far;external 'Tancy_Card.DLL' name 'NewCardCheck';
//参数说明          串口号 波特率   新卡标志 错误信息           pFlag='1'为新卡,0则否

function WriteUCard(Port,rate:integer;ICId,icpsw:pchar;ICCSpare:double;fc:integer;pErr,ICMark:pchar;WriteFlag:integer):integer;stdcall;far;external 'Tancy_Card.DLL' name 'WriteUCard';
//参数说明         串口号 波特率     卡号  密码       购气量   已充值次数  错误 用户号        写标志//WriteFlag=2为售气，1为补卡，0为开户（密钥传递卡）

function InitCard(Port,Rate:integer;icpsw,pErr:pchar):integer;stdcall;far;external 'Tancy_Card.DLL' name 'InitCard';
//参数说明      串口号 波特率 卡密码(未用) 错误信息

implementation

{$R *.dfm}

procedure TForm2.Button2Click(Sender: TObject);
var
  icpsw  :     array[0..5]of char;
  pErr:     array[0..30]of char;
begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  if InitCard(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),icpsw,pErr)=0 then
    begin
      showmessage('初始化成功！');
    end
  else
    begin
      showmessage(perr);
    end;
end;


procedure TForm2.Button1Click(Sender: TObject);
var
  id    :     array[0..20]of char;
  fc    :     array[0..8]of char;
  ickh  :     array[0..16]of char;
  userId:     array[0..12]of char;
//  czcs  :     array[0..8]of char;
//  sum:        array[0..20]of char;
  Gasvalue:   array[0..10]of char;
  tpsw  :     array[0..1]of char;
  perr  :     array[0..30]of char;

begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  if ReadUCard(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),id,ickh,Gasvalue,fc,tpsw,pErr,userId)<>0 then showmessage(pErr)
  else begin
         edit1.Text:=ickh;
         edit2.Text:=userid;
         edit5.Text:=fc;
         edit4.Text:=Gasvalue;
         //edit3.Text:=czcs;
         //edit6.Text:=sum;
         edit7.Text:=tpsw;
         edit8.Text:=id;
         showmessage('读卡操作成功！');
       end;
end;

procedure TForm2.Button3Click(Sender: TObject);
var
  ICId  :     array[0..16]of char;
  icpsw :     array[0..8]of char;
  userId:     array[0..12]of char;
  perr  :     array[0..30]of char;
  ICCSpare:   double;
  fc    :     integer;
  //GASCOUNT:   integer;
  //orderTotal: Longword;

begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  StrPCopy(ICId,trim(edit1.Text));
  StrPCopy(userId,trim(edit2.Text));
  //orderTotal:=strtoint64(trim(edit6.Text));
  fc:=strtoint(trim(edit5.Text));
//  fc:=strtoint(trim(edit3.Text));
  iccspare:=strtofloat(trim(edit4.Text));
  if WriteUCard(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),ICId,icpsw,ICCSpare,fc,pErr,userId,0)<>0 then showmessage(pErr)
  else begin
         showmessage('开户操作成功！');
       end;
end;


procedure TForm2.Button4Click(Sender: TObject);
var
  ICId  :     array[0..16]of char;
  icpsw :     array[0..8]of char;
  userId:     array[0..12]of char;
  perr  :     array[0..30]of char;
  ICCSpare:   double;
  fc    :     integer;
  //GASCOUNT:   integer;
  //orderTotal: Longword;

begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  StrPCopy(ICId,trim(edit1.Text));
  StrPCopy(userId,trim(edit2.Text));
  //orderTotal:=strtoint64(trim(edit6.Text));
  fc:=strtoint(trim(edit5.Text));
  //fc:=strtoint(trim(edit3.Text));
  iccspare:=strtofloat(trim(edit4.Text));
  if WriteUCard(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),ICId,icpsw,ICCSpare,fc,pErr,userId,2)<>0 then showmessage(pErr)
  else begin
         showmessage('售气操作成功！');
       end;
end;

procedure TForm2.Button5Click(Sender: TObject);
var
  ICId  :     array[0..16]of char;
  icpsw :     array[0..8]of char;
  userId:     array[0..12]of char;
  perr  :     array[0..30]of char;
  ICCSpare:   double;
  fc    :     integer;
  //GASCOUNT:   integer;
  //orderTotal: Longword;

begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  StrPCopy(ICId,trim(edit1.Text));
  StrPCopy(userId,trim(edit2.Text));
  //orderTotal:=strtoint64(trim(edit6.Text));
  fc:=strtoint(trim(edit5.Text));
  //fc:=strtoint(trim(edit3.Text));
  iccspare:=strtofloat(trim(edit4.Text));
  if WriteUCard(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),ICId,icpsw,ICCSpare,fc,pErr,userId,1)<>0 then showmessage(pErr)
  else begin
         showmessage('补卡操作成功！');
       end;
end;

procedure TForm2.Button6Click(Sender: TObject);
var
  pFlag  :     array[0..1]of char;
  pErr:     array[0..30]of char;
begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  if IsSelf(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),pFlag,pErr)=0 then
    begin
      if pFlag='1' then showmessage('天信CPU卡！') 
      else  showmessage('非天信CPU卡！');
    end
  else
    begin
      showmessage(perr);
    end;
end;

procedure TForm2.Button7Click(Sender: TObject);
var
  pFlag  :     array[0..1]of char;
  pErr:     array[0..30]of char;
begin
  if combobox1.ItemIndex<0 then begin showmessage('请选择串口后再操作！');exit;  end;
  if combobox2.ItemIndex<0 then begin showmessage('请选择通信波特率后再操作！');exit;  end;
  if NewCardCheck(combobox1.ItemIndex,strtoint(trim(combobox2.Text)),pFlag,pErr)=0 then   
    begin
      if pFlag='1' then showmessage('新卡！') else
        showmessage('非新卡！');
    end
  else
    begin
      showmessage(perr);
    end;
end;

end.
