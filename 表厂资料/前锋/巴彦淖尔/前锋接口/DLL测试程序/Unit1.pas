unit Unit1;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls,ZhengTai, Buttons, ComCtrls;

type
  TForm1 = class(TForm)
    Button1: TButton;
    Button2: TButton;
    Edit1: TEdit;
    Edit2: TEdit;
    Edit3: TEdit;
    Edit4: TEdit;
    Label1: TLabel;
    Label2: TLabel;
    Label3: TLabel;
    Label4: TLabel;
    Edit5: TEdit;
    Edit6: TEdit;
    Label5: TLabel;
    Label6: TLabel;
    Button3: TButton;
    Button4: TButton;
    Button5: TButton;
    Button6: TButton;
    Edit7: TEdit;
    Label7: TLabel;
    Button7: TButton;
    Label8: TLabel;
    Edit8: TEdit;
    BitBtn1: TBitBtn;
    Label9: TLabel;
    Edit9: TEdit;
    Button8: TButton;
    Button9: TButton;
    Button10: TButton;
    Button11: TButton;
    Button12: TButton;
    Button13: TButton;
    Button14: TButton;
    Edit10: TEdit;
    Label10: TLabel;
    Label11: TLabel;
    Edit11: TEdit;
    Label12: TLabel;
    Edit12: TEdit;
    Label13: TLabel;
    Edit13: TEdit;
    Label14: TLabel;
    Edit14: TEdit;
    Label15: TLabel;
    Edit15: TEdit;
    Label16: TLabel;
    Edit16: TEdit;
    Label17: TLabel;
    Edit17: TEdit;
    Label18: TLabel;
    Edit18: TEdit;
    Label19: TLabel;
    Edit19: TEdit;
    Label20: TLabel;
    Edit20: TEdit;
    Label21: TLabel;
    Edit21: TEdit;
    Label22: TLabel;
    Edit22: TEdit;
    Label23: TLabel;
    Button15: TButton;
    Label24: TLabel;
    Edit23: TEdit;
    Label25: TLabel;
    Label26: TLabel;
    Label27: TLabel;
    Label30: TLabel;
    Label31: TLabel;
    Label32: TLabel;
    Label33: TLabel;
    Label36: TLabel;
    Label37: TLabel;
    Label38: TLabel;
    Label39: TLabel;
    Button16: TButton;
    ComboBox1: TComboBox;
    Label40: TLabel;
    Edit24: TEdit;
    Label41: TLabel;
    Edit25: TEdit;
    Label42: TLabel;
    Edit26: TEdit;
    Label43: TLabel;
    Label44: TLabel;
    Edit27: TEdit;
    Label45: TLabel;
    Label46: TLabel;
    Edit28: TEdit;
    Label47: TLabel;
    Label48: TLabel;
    Edit29: TEdit;
    Label49: TLabel;
    Label50: TLabel;
    Edit30: TEdit;
    Label52: TLabel;
    Edit31: TEdit;
    Label51: TLabel;
    Label53: TLabel;
    ComboBox2: TComboBox;
    Label28: TLabel;
    ComboBox3: TComboBox;
    Label29: TLabel;
    Edit32: TEdit;
    Label34: TLabel;
    GroupBox1: TGroupBox;
    CheckBox1: TCheckBox;
    CheckBox2: TCheckBox;
    CheckBox3: TCheckBox;
    CheckBox4: TCheckBox;
    CheckBox5: TCheckBox;
    CheckBox6: TCheckBox;
    GroupBox2: TGroupBox;
    GroupBox3: TGroupBox;
    Memo1: TMemo;
    Memo2: TMemo;
    Label35: TLabel;
    Edit33: TEdit;
    Label54: TLabel;
    Label55: TLabel;
    procedure Button2Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
    procedure Button5Click(Sender: TObject);
    procedure Button7Click(Sender: TObject);
    procedure Button6Click(Sender: TObject);
    procedure BitBtn1Click(Sender: TObject);
    procedure Button8Click(Sender: TObject);
    procedure Button9Click(Sender: TObject);
    procedure Button10Click(Sender: TObject);
    procedure Button12Click(Sender: TObject);
    procedure Button15Click(Sender: TObject);
    procedure Button11Click(Sender: TObject);
    procedure Button13Click(Sender: TObject);
    procedure Button14Click(Sender: TObject);
    procedure FormActivate(Sender: TObject);
    procedure Button16Click(Sender: TObject);
  private
    { Private declarations }
  public
  function QF_MakeICCard(MakeCard:CN_MakeCardInfo):CN_MakeCardInfo;
  function QF_ReadICCard(ReadInfo : cardinfo):cardinfo;
    { Public declarations }
  end;

var
  Form1: TForm1;

  Qf_CardId:array[0..8]of char;
  Qf_CardSort:byte;
  Qf_BuyCnt:longint;
  Qf_PEnableGas:longint;
  Qf_CumGas:longint;
  Qf_lastGas:longint;
  Qf_AlarmGas:longint;
  Qf_BigMeter:byte;
  Qf_MeterStu:array[0..39]of char;
  Qf_fg:longint;

  g_Port:longint;
  n_pass:byte;
implementation


{$R *.dfm}

function bytetobit(b: byte): string;
var
    s:string;
begin
    s:='';
    s := s + inttostr((b and 128) div 128);
    s := s + inttostr((b and 64) div 64);
    s := s + inttostr((b and 32) div 32);
    s := s + inttostr((b and 16) div 16);
    s := s + inttostr((b and 8) div 8);
    s := s + inttostr((b and 4) div 4);
    s := s + inttostr((b and 2) div 2);
    s := s + inttostr(b and 1);
    Result:=s;
end;

function Card(var ReadInfo:  array of longint): integer;
var
    i:integer;
begin
    for i :=0 to 2 do
    begin
        ReadInfo[i] := (i+1) *  ReadInfo[i];
        //showmessage(inttostr(ReadInfo[i]));
    end;
    Result:=0;
end;

function TForm1.QF_ReadICCard(ReadInfo: cardinfo): cardinfo;
var
  i:integer;
begin
  i:=99;
  try
    i:=TestCard(ReadInfo.Port,ReadInfo.Baud,
                ReadInfo.IndustryorCivil,ReadInfo.CardCode,
                ReadInfo.boolcard,ReadInfo.userID,
                ReadInfo.CardId,ReadInfo.EnableUseGas,
                ReadInfo.returngas,ReadInfo.BuyGasNumber,
                ReadInfo.AlarmGas,ReadInfo.CardNumber,ReadInfo.RandomData);
    Result := ReadInfo;
    Result.ReturnResult := i;
  except  
    Result.ReturnResult := i;
  end;

end;

function TForm1.QF_MakeICCard(MakeCard: CN_MakeCardInfo): CN_MakeCardInfo;
var
  i:integer;
begin
  i:=99;
  try
    i:=AddCustomerCard(MakeCard.Port,MakeCard.Baud,
                MakeCard.userid,MakeCard.CardID,
                MakeCard.BuyGas,MakeCard.MeterAlarmGas,
                MakeCard.RandomData,MakeCard.CardPassword,
                MakeCard.CheckErrorNumber);
    Result.RandomData := MakeCard.RandomData;
    Result.CardPassword := MakeCard.CardPassword;
    Result.CheckErrorNumber := MakeCard.CheckErrorNumber;
    Result.ReturnResult := i;
  except
    Result.ReturnResult := i;
  end;
end;


procedure TForm1.Button2Click(Sender: TObject);
var
  i: integer;
  CN_MakeCard: CN_MakeCardInfo;
begin
  CN_MakeCard.Port := 0;
  CN_MakeCard.Baud := 9600;
  for i:= 1  to  10  do
    CN_MakeCard.UserID[i-1] := trim(Edit1.Text)[i];
  for i:=1 to 8 do
    CN_MakeCard.CardId[i-1] := trim(Edit2.Text)[i];
  CN_MakeCard.BuyGas := strtoint(Edit5.Text);;
  CN_MakeCard.MeterAlarmGas := 5;
  CN_MakeCard.CardPassword := 'FFFFFF';
  CN_MakeCard.RandomData := 0;
  CN_MakeCard.CheckErrorNumber := 0;

  CN_MakeCard := QF_MakeICCard(CN_MakeCard);
  if CN_MakeCard.ReturnResult =0 then
  begin
    Application.MessageBox(PChar('制卡成功!'),'IC卡',MB_OK);
    Edit8.Text :=  inttostr(CN_MakeCard.RandomData );
    Edit9.Text := CN_MakeCard.CardPassword;
  end 
  else
   Application.MessageBox(PChar('制卡失败!'),'IC卡理',MB_OK);
end;

procedure TForm1.Button1Click(Sender: TObject);
var
  ReadInfo:CardInfo;
begin
  ReadInfo.Port:=0;
  ReadInfo.Baud:=9600;
  ReadInfo :=QF_ReadICCard(ReadInfo);
  showmessage('返回='+inttostr(readInfo.ReturnResult));
  if readInfo.ReturnResult =0 then
  begin
    Edit1.Text := ReadInfo.UserID;
    Edit2.Text := ReadInfo.CardId;
    Edit3.Text := inttostr(ReadInfo.CardCode);
    Edit4.Text := inttostr(ReadInfo.RandomData);
    Edit5.Text := inttostr(ReadInfo.EnableUseGas);
    Edit6.Text := inttostr(ReadInfo.BuyGasNumber);
    Edit7.Text := inttostr(ReadInfo.CardNumber);
  end
  else
  begin
    showmessage('空卡!');
    Edit1.Text := '';
    Edit2.Text := '';
    Edit3.Text := '';
    Edit4.Text := '';
    Edit5.Text := '';
    Edit6.Text := '';
    Edit7.Text := '';
  end;

end;

procedure TForm1.Button3Click(Sender: TObject);
var
  r:smallint;
begin
 r :=QF_EraseCard(0,9600);
 if r =0 then
   Application.MessageBox(PChar('恢复成功!'),'IC卡',MB_OK);
end;

procedure TForm1.Button4Click(Sender: TObject);
var
  EreseInfo:CN_EreseInfo;
  i,r : integer;
  e: Smallint	;
begin
  EreseInfo.Port := 0;
  EreseInfo.Baud := 9600;
  EreseInfo.CardPassword:='ffffff';

  for i:= 1  to  10  do
    EreseInfo.UserID[i-1] := trim(Edit1.Text)[i];
  for i:=1 to 8 do
    EreseInfo.CardId[i-1] := trim(Edit2.Text)[i];

  //EreseInfo.CardCircleNumber := 1;
  EreseInfo.CardCircleNumber := strtoint(Edit7.Text);
  EreseInfo.CardPassword := 'ffffff';
  EreseInfo.CardBuyGasCircleNumber :=  strtoint(Edit6.Text);
  //EreseInfo.RandomData := 0;//strtoint(Edit4.text) ;
  EreseInfo.RandomData := strtoint(Edit4.text) ; ///此随机数为加密随机数（擦卡成功返回明文随机数）
   r := CardNoGasOther(EreseInfo.Port,
                  EreseInfo.Baud,
                  EreseInfo.UserID,
                  EreseInfo.CardId,
                  EreseInfo.CardCircleNumber,
                  EreseInfo.CardBuyGasCircleNumber,
                  EreseInfo.RandomData,
                  EreseInfo.CardPassword,EreseInfo.CheckErrorNumber );
   //showmessage('返回='+inttostr(r));你那里是这个屏蔽了出错吗?
  if r = 0 then
  begin
    showmessage('擦除成功!');
    edit9.Text:=EreseInfo.CardPassword;
  end
  else
    showmessage(' 擦除失败:'+inttostr(r));


end;

procedure TForm1.Button5Click(Sender: TObject);
var
  ReplenishCardInfo:CN_ReplenishCardInfo;
  i,r:integer;
begin
  if Edit1.Text = '' then
  begin
    showmessage('请输入完整的信息！');
    exit;
  end;
  ReplenishCardInfo.Port := 0;
  ReplenishCardInfo.Baud := 9600;

   for i:= 1  to  10  do
    ReplenishCardInfo.UserID[i-1] := trim(Edit1.Text)[i];
  for i:=1 to 8 do
    ReplenishCardInfo.CardId[i-1] := trim(Edit2.Text)[i];
  //showmessage(ReplenishCardInfo.UserID);showmessage(ReplenishCardInfo.CardId);
  //ReplenishCardInfo.UserID:='1122334455';
  //ReplenishCardInfo.CardId:='12345678' ;
  //ReplenishCardInfo.EnableUseGas := 0;
  ReplenishCardInfo.EnableUseGas := strtoint(Edit5.Text);
  ReplenishCardInfo.CustomerCircleNumber := strtoint(Edit6.Text);
  ReplenishCardInfo.MeterAlarmGas := 5;
  ReplenishCardInfo.ReplenishCardCircleNumber :=strtoint(Edit7.Text);
  ReplenishCardInfo.RandomData := 0;
  ReplenishCardInfo.CardPassword := 'ffffff';
  r := ReplenishCustomerCard(ReplenishCardInfo.Port,
                             ReplenishCardInfo.Baud,
                             ReplenishCardInfo.UserID,
                             ReplenishCardInfo.CardId,
                             ReplenishCardInfo.EnableUseGas,
                             ReplenishCardInfo.CustomerCircleNumber,
                             ReplenishCardInfo.MeterAlarmGas,
                             ReplenishCardInfo.ReplenishCardCircleNumber,
                             ReplenishCardInfo.RandomData,
                             ReplenishCardInfo.CardPassword,
                             ReplenishCardInfo.CheckErrorNumber);
  if r = 0 then
  begin
     showmessage('补卡成功!') ;
     Edit8.Text :=  inttostr(ReplenishCardInfo.RandomData );
     Edit9.Text :=  ReplenishCardInfo.CardPassword;
  end
  else
     showmessage('补卡失败:'+inttostr(r));

end;

procedure TForm1.Button7Click(Sender: TObject);
begin
  combobox1.ItemIndex:=-1;
  combobox2.ItemIndex:=-1;
  Edit1.Text := '';
  Edit2.Text := '';
  Edit3.Text := '';
  Edit4.Text := '';
  Edit5.Text := '';
  Edit6.Text := '';
  Edit7.Text := '';
  Edit8.Text := '';
  Edit9.Text := '';
  Edit10.Text := '';
  Edit11.Text := '';
  Edit12.Text := '';
  Edit13.Text := '';
  Edit14.Text := '';
  Edit15.Text := '';
  Edit16.Text := '';
  Edit17.Text := '';
  Edit18.Text := '';
  Edit19.Text := '';
  Edit20.Text := '';
  Edit21.Text := '';
  Edit22.Text := '';
  Edit23.Text := '';
  Edit24.Text := '';
  Edit25.Text := '';
  Edit26.Text := '';
  Edit27.Text := '';
  Edit28.Text := '';
  Edit29.Text := '';
  Edit30.Text := '';
  Edit31.Text := '';
  Edit33.Text := '';
  CheckBox1.Checked:=false;
  CheckBox2.Checked:=false;
  CheckBox3.Checked:=false;
  CheckBox4.Checked:=false;
  CheckBox5.Checked:=false;
  CheckBox6.Checked:=false;
  Memo1.Text:='';
  Memo2.Text:='';
  edit11.ShowHint:=false;
  edit12.ShowHint:=false;
  edit1.SetFocus;
end;

procedure TForm1.Button6Click(Sender: TObject);
var
  Sale_Info :CN_SaleInfo;
  i,r:integer;
begin
  Sale_Info.Port := 0;
  Sale_Info.Baud := 9600;

  for i:= 1  to  10  do
    Sale_Info.UserID[i-1] := trim(Edit1.Text)[i];
  for i:=1 to 8 do
    Sale_Info.CardId [i-1] := trim(Edit2.Text)[i];
  Sale_Info.BuyGas := strtoint(Edit5.Text);
  Sale_Info.ReturnGas := 0;
  sale_Info.CustomerCircleNumber := strtoint(Edit6.Text);
  Sale_Info.MeterAlarmGas := 5;
  Sale_Info.ReplenishCardCircleNumber := strtoint(Edit7.Text);
  Sale_Info.RandomData := strtoint(Edit4.Text); //此随机数为加密随机数（购气成功返回明文随机数）
  Sale_Info.CardPassword := 'FFFFFF';
  r := CustomerCardBuyGas(Sale_Info.Port,
                     Sale_Info.Baud,
                     Sale_Info.UserID,
                     Sale_Info.CardId,
                     Sale_Info.BuyGas,
                     Sale_Info.ReturnGas,
                     Sale_Info.CustomerCircleNumber,
                     Sale_Info.MeterAlarmGas,
                     Sale_Info.ReplenishCardCircleNumber,
                     Sale_Info.RandomData,
                     Sale_Info.CardPassword,
                     Sale_Info.CheckErrorNumber
                     );
  if r=0 then
  begin
    showmessage('购气成功!');
    Edit4.Text   :=  inttostr(Sale_Info.RandomData);
    Edit9.Text := Sale_Info.CardPassword;
  end
  else
    showmessage('购气失败: '+ inttostr(r));
end;

procedure TForm1.BitBtn1Click(Sender: TObject);
var
    kk:array [0..7] of char;
    tt:array [0..7] of char;
    ss:array [0..7] of char;
    i,cn,cSum :integer;
begin
    kk[0] := '9'; kk[1] := '4'; kk[2] := '8'; kk[3] := '7';
    kk[4] := '1'; kk[5] := '4'; kk[6] := '6'; kk[7] := '5';
    cSum:=0;
    for i:=0 to 7 do
    begin
        tt[i]:=trim(edit4.Text)[i+1];
        if kk[i] < tt[i]  then
            cn :=strtoint(kk[i])+10
        else
            cn :=strtoint(kk[i]) ;
        ss[i] := chr(cn- strtoint(tt[i])+$30);
        cSum := cSum +  cn- strtoint(tt[i]);
    end;
    cn :=trunc((cSum-strtoint(ss[7]))/10);
    ss[7] := chr(cn+$30);
    Edit4.Text := ss;


end;

procedure TForm1.Button8Click(Sender: TObject);
var
    s_GradientCon:array[0..5]of longint;
    i:integer;
begin
    s_GradientCon[0]:=100;
    s_GradientCon[1]:=200;
    s_GradientCon[2]:=300;
    card(s_GradientCon[0]);
    for i :=0 to 2 do
    begin
        showmessage(inttostr(s_GradientCon[i]));
    end;
end;

procedure TForm1.Button9Click(Sender: TObject);
var
    i:longint;
    pso,n_n_pass:byte;
    GasNo:string ;
    Cdir,Cdir1:string;
    F: TextFile;
begin
    GasNo:=edit1.Text ;
    for i:= 1  to  8  do
        Qf_CardId[i-1] := GasNo[i];  //表号

    if combobox1.ItemIndex > 15 then
        Qf_CardSort :=  combobox1.ItemIndex + 5
    else
        Qf_CardSort :=  combobox1.ItemIndex + 1;
    Qf_BuyCnt       :=  strtoint(edit5.Text);   //次数
    Qf_PEnableGas   :=  strtoint(edit4.Text);   //购水金额 //测试用水金额
    Qf_AlarmGas     :=  strtoint(edit3.Text);  //报警水量金额
    n_n_pass        :=  combobox3.ItemIndex;    //新密钥
    pso             :=  n_pass;//当前密钥
    if combobox2.ItemIndex = 0 then
    begin
        Qf_BigMeter := $81;
        Qf_fg       := 1;
    end;
    if combobox2.ItemIndex = 1 then
    begin
        Qf_BigMeter := $81;
        Qf_fg       := 4;
    end;
    if combobox2.ItemIndex = 2 then
    begin
        Qf_BigMeter := $82;
        Qf_fg       := 1;
    end;
    if combobox2.ItemIndex = 3 then
    begin
        Qf_BigMeter := $82;
        Qf_fg       := 8;
    end;
    Qf_fg       := Qf_fg + strtoint(edit33.Text)*256;
    screen.Cursor:=crhourglass;
    case Qf_CardSort of
    1:
        i:=QF_NewCard(g_Port,9600,
                        Qf_CardId,
                        Qf_PEnableGas,
                        Qf_AlarmGas,
                        Qf_BigMeter,
                        pso,
                        Qf_fg);

    2:
        i:=QF_MendCard(g_Port,9600,
                        Qf_CardId,
                        Qf_BuyCnt,
                        Qf_PEnableGas,
                        Qf_AlarmGas,
                        Qf_BigMeter,
                        pso,
                        Qf_fg);

    13:
        i:=QF_NewCard30(g_Port,9600,
                        Qf_CardId,
                        Qf_PEnableGas,
                        Qf_AlarmGas,
                        Qf_BigMeter,
                        pso,
                        Qf_fg);
    else
        if(Qf_CardSort=16) then
        begin
            if(checkbox6.Checked=true) then
                pso := 0
            else
                pso := 1;
            n_n_pass := 0;
            if(checkbox1.Checked=true) then
                n_n_pass := 16;
            if(checkbox2.Checked=true) then
                n_n_pass := n_n_pass + 8;
            if(checkbox3.Checked=true) then
                n_n_pass := n_n_pass + 4;
            if(checkbox4.Checked=true) then
                n_n_pass := n_n_pass + 2;
            if(checkbox5.Checked=true) then
                n_n_pass := n_n_pass + 1;
        end;
        i:=QF_BulidCard(g_Port,9600,
                        Qf_CardId,
                        Qf_CardSort,
                        Qf_PEnableGas,
                        Qf_AlarmGas,
                        pso,
                        n_n_pass,
                        Qf_fg);
    end;
    if(i=0) and (Qf_CardSort=12) then
    begin
        n_pass := n_n_pass;
        edit32.Text:=inttostr(n_pass);
        Cdir:=ExtractFilePath(AppliCation.ExeName);
        Cdir1:=Cdir+'\pass.txt';
        AssignFile(F, Cdir1);
        Rewrite(F);
        Writeln(F, edit32.Text);
        closefile(F);
    end;
    edit8.Text:=CheckErr(i);
    screen.Cursor:=crdefault;
end;

procedure TForm1.Button10Click(Sender: TObject);
var
    i:longint;
begin
    screen.Cursor:=crhourglass;
    i:=QF_ClearCard(g_Port,9600);
    edit8.Text:=CheckErr(i);
    screen.Cursor:=crdefault;
end;

procedure TForm1.Button12Click(Sender: TObject);
var
    i,j:longint;
    dstr:string;
    CN_TestCard:CN_TestCardInfo ;
begin
    screen.Cursor:=crhourglass;
    Button7Click(Sender);
    application.ProcessMessages;

    i:=QF_TestCard( g_Port,
                    9600,
                Qf_CardId,
                Qf_CardSort,
                Qf_BuyCnt,
                Qf_PEnableGas,
                Qf_CumGas,
                Qf_lastGas,
                Qf_AlarmGas,
                Qf_BigMeter,
                Qf_MeterStu,
                Qf_fg);
    if(i<>0)then
    begin
        edit8.Text:=CheckErr(i);
        screen.Cursor:=crdefault;
        exit;
    end;
    edit8.Text:=CheckErr(i);
    edit1.Text:=Qf_CardId;
    if(Qf_CardSort>16) then
        combobox1.ItemIndex := Qf_CardSort-5
    else
        combobox1.ItemIndex:=  Qf_CardSort-1;

    edit3.Text:=inttostr(Qf_AlarmGas);
    edit4.Text:=inttostr(Qf_PEnableGas);
    edit5.Text:=inttostr(Qf_BuyCnt);
    edit6.Text:=inttostr(Qf_lastGas);
    edit9.Text:=inttostr(Qf_CumGas);
    edit33.Text:=inttostr(round((Qf_fg and $FF00)/256));
    if((Qf_fg and $FF) = 4) then   //4442卡
    begin
        combobox2.ItemIndex := 1;
        if(Qf_CardSort=12) then
        begin
            edit27.Text:='';//磁保护或外部欠压次数
            edit22.Text:='';//剪线次数
            edit10.Text:='';//开盖次数
            edit11.Text:='';//状态字1
            edit12.Text:='';//状态字2
            edit13.Text:='新密钥:'+inttostr(ord(Qf_MeterStu[0]))+' ;旧密钥:'+inttostr(ord(Qf_MeterStu[1]));//其他说明
            screen.Cursor:=crdefault;
            exit;
        end;
        if(Qf_CardSort=10) then
        begin
            if((ord(Qf_MeterStu[0]) and $8 ) = $8) then
                edit27.Text:='磁保护：0'//磁保护
            else
                edit27.Text:='磁保护：1';//磁保护
            if((ord(Qf_MeterStu[0]) and $20 )= $20) then
                edit11.Text:='阀异常：0'//阀异常
            else
                edit11.Text:='阀异常：1';//阀异常
            if((ord(Qf_MeterStu[0]) and 1 )= 1) then
                edit10.Text:='错误卡：0'//错误卡
            else
                edit10.Text:='错误卡：1';//错误卡

            edit22.Text:='';//欠压
            edit12.Text:='';//阀门状态
            edit13.Text:='表状态:'+inttohex(ord(Qf_MeterStu[1]),2);//其他说明（表状态）
        end
        else begin
            edit27.Text:='磁保护：'+ inttostr(ord(Qf_MeterStu[2]));//磁保护
            edit22.Text:='欠压: ' + inttostr(ord(Qf_MeterStu[3]));//欠压
            edit10.Text:='';//错误卡
            edit11.Text:='阀异常: ' + inttostr(ord(Qf_MeterStu[0]));//阀异常
            edit12.Text:='';//阀门状态
            edit13.Text:='';//关阀原因
        end;
    end;
    if((Qf_fg and $FF) = 8) then //射频卡
    begin
        combobox2.ItemIndex := 3;
        edit27.Text:=inttostr(ord(Qf_MeterStu[0]));//磁保护或外部欠压次数
        edit22.Text:=inttostr(ord(Qf_MeterStu[1]));//剪线次数
        edit10.Text:=inttostr(ord(Qf_MeterStu[2]));//开盖次数

        edit11.ShowHint:=true;
        edit11.Text:=inttohex(ord(Qf_MeterStu[3]),2)+', ';//状态字1
        edit11.Text:=edit11.Text+bytetobit(ord(Qf_MeterStu[3]));//状态字1 二进制码

        dstr:='流量线状态：'+ inttostr((ord(Qf_MeterStu[3]) and $80) div $80) ;
        dstr:=dstr+',顶盖状态：'+ inttostr((ord(Qf_MeterStu[3]) and $40) div $40) ;
        dstr:=dstr+',停气卡关阀：'+ inttostr((ord(Qf_MeterStu[3]) and $20) div $20) ;
        dstr:=dstr+',电源4.8v：'+ inttostr((ord(Qf_MeterStu[3]) and $10) div $10) ;
        dstr:=dstr+',电源掉电：'+ inttostr((ord(Qf_MeterStu[3]) and $8) div $8) ;
        dstr:=dstr+',磁保护/外部欠压：'+ inttostr((ord(Qf_MeterStu[3]) and $4) div $4) ;
        dstr:=dstr+',RF关阀：'+ inttostr((ord(Qf_MeterStu[3]) and $2) div $2) ;
        dstr:=dstr+',气量：'+ inttostr(ord(Qf_MeterStu[3]) and $1) ;
        edit11.Hint:=dstr;

        edit12.ShowHint:=true;
        edit12.Text:=inttohex(ord(Qf_MeterStu[4]),2)+', ';//状态字2
        edit12.Text:=edit12.Text+bytetobit(ord(Qf_MeterStu[4]));//状态字2 二进制码

        dstr:='磁保护到：'+ inttostr((ord(Qf_MeterStu[4]) and $80) div $80) ;
        dstr:=dstr+',天时间到：'+ inttostr((ord(Qf_MeterStu[4]) and $40) div $40) ;
        dstr:=dstr+',阀门当前状态：'+ inttostr((ord(Qf_MeterStu[4]) and $20) div $20) ;
        dstr:=dstr+',表类型：'+ inttostr((ord(Qf_MeterStu[4]) and $10) div $10) ;
        dstr:=dstr+',流量采样电平：'+ inttostr((ord(Qf_MeterStu[4]) and $8) div $8) ;
        dstr:=dstr+',磁保护电平：'+ inttostr((ord(Qf_MeterStu[4]) and $4) div $4) ;
        dstr:=dstr+',无线功能：'+ inttostr((ord(Qf_MeterStu[4]) and $2) div $2) ;
        dstr:=dstr+',开盖功能：'+ inttostr(ord(Qf_MeterStu[4]) and $1) ;
        edit12.Hint:=dstr;
        
        if(Qf_CardSort=10) then//查询卡
        begin
            memo1.Text:='';
            memo2.Text:='';
            
            for j:= 0 to 3 do
            begin
                memo1.Text:=memo1.Text + '次数：'+inttostr(ord(Qf_MeterStu[5+4*j])) +'，气量：'+
                            inttostr(strtoint('$'+inttohex(ord(Qf_MeterStu[5+4*j+1]),2) + inttohex(ord(Qf_MeterStu[5+4*j+2]),2)+inttohex(ord(Qf_MeterStu[5+4*j+3]),2)))+ '方'+#13#10;
            end;

            for j:= 0 to 7 do
            begin
                memo2.Text:=memo2.Text + '日用气量：'+
                            inttostr(strtoint('$'+inttohex(ord(Qf_MeterStu[21+2*j]),2) + inttohex(ord(Qf_MeterStu[21+2*j+1]),2)))+ '方'+#13#10;//回车换行符 #13#10，不能写出 #10#13
            end;
        end;
        if(Qf_CardSort=16) and (ord(Qf_MeterStu[5])=0) then//预制卡
        begin
            checkbox6.Checked:=true;
            checkbox1.Checked:=boolean((ord(Qf_MeterStu[6]) and $10) div $10);
            checkbox2.Checked:=boolean((ord(Qf_MeterStu[6]) and $8) div $8);
            checkbox3.Checked:=boolean((ord(Qf_MeterStu[6]) and $4) div $4);
            checkbox4.Checked:=boolean((ord(Qf_MeterStu[6]) and $2) div $2);
            checkbox5.Checked:=boolean(ord(Qf_MeterStu[6]) and $1);
        end;
        edit13.Text:='';//其他说明
    end;
    if((Qf_fg and $FF) = 1) then  //102卡
    begin
        if (Qf_BigMeter = $81)then
        begin
            combobox2.ItemIndex := 0;
            if((ord(Qf_MeterStu[0]) and $8 ) = $8) then
                edit27.Text:='磁保护：0'//磁保护
            else
                edit27.Text:='磁保护：1';//磁保护
            if((ord(Qf_MeterStu[0]) and $20 )= $20) then
                edit11.Text:='阀异常：0'//阀异常
            else
                edit11.Text:='阀异常：1';//阀异常
            if((ord(Qf_MeterStu[0]) and 1 )= 1) then
                edit10.Text:='错误卡：0'//错误卡
            else
                edit10.Text:='错误卡：1';//错误卡

            edit22.Text:='';//欠压
            edit12.Text:='';//阀门状态
            edit13.Text:='表状态:'+inttohex(ord(Qf_MeterStu[1]),2);//其他说明
        end
        else begin
            combobox2.ItemIndex := 2;
            {if((ord(Qf_MeterStu[0]) and $8 ) = $8) then
                edit27.Text:='0'//磁保护
            else
                edit27.Text:='1';//磁保护 }
            edit27.Text:='磁保护: '+ inttostr(ord(Qf_MeterStu[2]) shr 2);//磁保护
            if((ord(Qf_MeterStu[0]) and $20 )= $20) then
                edit11.Text:='阀异常:0'//阀异常
            else
                edit11.Text:='阀异常:1';//阀异常
            if((ord(Qf_MeterStu[0]) and 1 )= 1) then
                edit10.Text:='错误卡:0'//错误卡
            else
                edit10.Text:='错误卡:1';//错误卡

            edit22.Text:='';//欠压

            if((ord(Qf_MeterStu[2]) and $3 )= $3) then
                edit12.Text:='阀门状态:关。 原因：磁保护' //阀门状态
            else
                edit12.Text:='阀门状态:开';//阀门状态

            //edit12.Text:='';//阀门状态
            edit13.Text:='表状态:'+inttohex(ord(Qf_MeterStu[1]),2);//其他说明
        end;
    end;
                    
    screen.Cursor:=crdefault;
end;

procedure TForm1.Button15Click(Sender: TObject);
begin
  Edit1.Text := '81828384';
  combobox1.ItemIndex:=0;
  combobox2.ItemIndex:=1;
  Edit2.Text := '';
  Edit3.Text := '2000';
  Edit4.Text := '1350';
  Edit33.Text := '1';
  Edit5.Text := '1';
  Edit6.Text := '';
  Edit7.Text := '';
  Edit8.Text := '';
  Edit9.Text := '';
  Edit10.Text := '';
  Edit11.Text := '';
  Edit12.Text := '';
  Edit13.Text := '';
  Edit14.Text := '';
  Edit15.Text := '';
  Edit16.Text := '';
  Edit17.Text := '';
  Edit18.Text := '';
  Edit19.Text := '';
  Edit20.Text := '';
  Edit21.Text := '';
  Edit22.Text := '';
  Edit23.Text := '';
  Edit24.Text := '';
  Edit25.Text := '';
  Edit26.Text := '';
  Edit27.Text := '';
  Edit28.Text := '';
  Edit29.Text := '';
  Edit30.Text := '';
  Edit31.Text := '';
  CheckBox1.Checked:=false;
  CheckBox2.Checked:=false;
  CheckBox3.Checked:=false;
  CheckBox4.Checked:=false;
  CheckBox5.Checked:=false;
  CheckBox6.Checked:=false;
  Memo1.Text:='';
  Memo2.Text:='';
  edit11.ShowHint:=false;
  edit12.ShowHint:=false;
  edit1.SetFocus;
end;

procedure TForm1.Button11Click(Sender: TObject);
var
    i:longint;
    GasNo:string ;
    CN_MendCard:CN_MendCardInfo ;
begin
    GasNo:=edit1.Text ;
    CN_MendCard.s_Port:=g_Port;   //端口号
    for i:= 1  to  8  do
        CN_MendCard.s_PstrCardId[i-1] := GasNo[i];  //表号
    if length(edit5.Text) = 0 then
      CN_MendCard.s_BuyWaterNum:=1  //购气使用次数
    else
      CN_MendCard.s_BuyWaterNum:=strtoint(edit5.Text);  //购气使用次数
    CN_MendCard.s_BuyWaterMoney:=strtoint(edit4.Text);  //购水金额 //测试用水金额
    CN_MendCard.s_BuyMode:=1;
    CN_MendCard.s_AlarmMoney:=strtoint(edit3.Text);  //报警水量金额
    CN_MendCard.s_GradientInf[0]:=chr(strtoint(edit27.Text)); //阶梯调整次数
    CN_MendCard.s_GradientInf[1]:=chr(strtoint(edit22.Text)); //阶梯数量
    CN_MendCard.s_GradientInf[2]:=chr(strtoint('$'+edit24.Text)); //复位周期
    CN_MendCard.s_GradientInf[3]:=chr(strtoint('$'+edit25.Text));
    CN_MendCard.s_GradientInf[4]:=chr(strtoint('$'+edit26.Text));
    i:=strtoint(edit22.Text);

    case i of
    1:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
        end;
    2:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
            CN_MendCard.s_GradientCon[2]:=strtoint(edit12.Text)*10;
            CN_MendCard.s_GradientCon[3]:=round(strtofloat(edit13.Text)*100);
        end;
    3:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
            CN_MendCard.s_GradientCon[2]:=strtoint(edit12.Text)*10;
            CN_MendCard.s_GradientCon[3]:=round(strtofloat(edit13.Text)*100);
            CN_MendCard.s_GradientCon[4]:=strtoint(edit14.Text)*10;
            CN_MendCard.s_GradientCon[5]:=round(strtofloat(edit15.Text)*100);
        end;
    4:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
            CN_MendCard.s_GradientCon[2]:=strtoint(edit12.Text)*10;
            CN_MendCard.s_GradientCon[3]:=round(strtofloat(edit13.Text)*100);
            CN_MendCard.s_GradientCon[4]:=strtoint(edit14.Text)*10;
            CN_MendCard.s_GradientCon[5]:=round(strtofloat(edit15.Text)*100);
            CN_MendCard.s_GradientCon[6]:=strtoint(edit16.Text)*10;
            CN_MendCard.s_GradientCon[7]:=round(strtofloat(edit17.Text)*100);
        end;
    5:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
            CN_MendCard.s_GradientCon[2]:=strtoint(edit12.Text)*10;
            CN_MendCard.s_GradientCon[3]:=round(strtofloat(edit13.Text)*100);
            CN_MendCard.s_GradientCon[4]:=strtoint(edit14.Text)*10;
            CN_MendCard.s_GradientCon[5]:=round(strtofloat(edit15.Text)*100);
            CN_MendCard.s_GradientCon[6]:=strtoint(edit16.Text)*10;
            CN_MendCard.s_GradientCon[7]:=round(strtofloat(edit17.Text)*100);
            CN_MendCard.s_GradientCon[8]:=strtoint(edit18.Text)*10;
            CN_MendCard.s_GradientCon[9]:=round(strtofloat(edit19.Text)*100);
        end;
    6:
        begin
            CN_MendCard.s_GradientCon[0]:=strtoint(edit10.Text)*10;
            CN_MendCard.s_GradientCon[1]:=round(strtofloat(edit11.Text)*100);
            CN_MendCard.s_GradientCon[2]:=strtoint(edit12.Text)*10;
            CN_MendCard.s_GradientCon[3]:=round(strtofloat(edit13.Text)*100);
            CN_MendCard.s_GradientCon[4]:=strtoint(edit14.Text)*10;
            CN_MendCard.s_GradientCon[5]:=round(strtofloat(edit15.Text)*100);
            CN_MendCard.s_GradientCon[6]:=strtoint(edit16.Text)*10;
            CN_MendCard.s_GradientCon[7]:=round(strtofloat(edit17.Text)*100);
            CN_MendCard.s_GradientCon[8]:=strtoint(edit18.Text)*10;
            CN_MendCard.s_GradientCon[9]:=round(strtofloat(edit19.Text)*100);
            CN_MendCard.s_GradientCon[10]:=strtoint(edit20.Text)*10;
            CN_MendCard.s_GradientCon[11]:=round(strtofloat(edit21.Text)*100);
        end;
    end;
    
    screen.Cursor:=crhourglass;
    {i:=DC_MendCard(CN_MendCard.s_Port,
                    CN_MendCard.s_PstrCardId,
                    CN_MendCard.s_BuyWaterNum,
                    CN_MendCard.s_BuyWaterMoney,
                    CN_MendCard.s_BuyMode,
                    CN_MendCard.s_AlarmMoney,
                    CN_MendCard.s_GradientInf,
                    CN_MendCard.s_GradientCon); }
    edit8.Text:=CheckErr(i);
    screen.Cursor:=crdefault;

end;

procedure TForm1.Button13Click(Sender: TObject);
var
    i:longint;
    GasNo:string ;
    CN_BuyWater:CN_BuyWaterInfo ;
begin

    GasNo:=edit1.Text ;
    for i:= 1  to  8  do
        Qf_CardId[i-1] := GasNo[i];  //表号
    Qf_BuyCnt       :=  strtoint(edit5.Text);   //次数
    Qf_PEnableGas   :=  strtoint(edit4.Text);   //购水金额 //测试用水金额
    Qf_AlarmGas     :=  strtoint(edit3.Text);  //报警水量金额
    if combobox2.ItemIndex = 0 then
    begin
        Qf_BigMeter := $81;
        Qf_fg       := 1;
    end;
    if combobox2.ItemIndex = 1 then
    begin
        Qf_BigMeter := $81;
        Qf_fg       := 4;
    end;
    if combobox2.ItemIndex = 2 then
    begin
        Qf_BigMeter := $82;
        Qf_fg       := 1;
    end;
    if combobox2.ItemIndex = 3 then
    begin
        Qf_BigMeter := $82;
        Qf_fg       := 8;
    end;
    Qf_fg       := Qf_fg + strtoint(edit33.Text)*256;
    screen.Cursor:=crhourglass;
    i:=QF_BuyGas(g_Port,9600,
                    Qf_CardId,
                    Qf_BuyCnt,
                    Qf_PEnableGas,
                    Qf_AlarmGas,
                    Qf_fg,
                    Qf_BigMeter);
    edit8.Text:=CheckErr(i);
    screen.Cursor:=crdefault;

end;

procedure TForm1.Button14Click(Sender: TObject);
var
    i:longint;
    GasNo:string ;
    CN_ClearWater:CN_ClearWaterInfo ;
begin
   { GasNo:=edit1.Text ;
    CN_ClearWater.s_Port:=g_Port;   //端口号
    for i:= 1  to  12  do
        CN_ClearWater.s_PstrCardId[i-1] := GasNo[i];  //表号
    CN_ClearWater.s_BuyWaterNum:=strtoint(edit5.Text);  //购气使用次数
    CN_ClearWater.s_BuyWaterMoney:=strtoint(edit4.Text);  //购水金额 //测试用水金额

    screen.Cursor:=crhourglass;
    i:=DC_ClearWater(CN_ClearWater.s_Port,
                    CN_ClearWater.s_PstrCardId,
                    CN_ClearWater.s_BuyWaterNum,
                    CN_ClearWater.s_BuyWaterMoney);
    edit8.Text:=CheckErr(i);
    screen.Cursor:=crdefault; }
    
end;

procedure TForm1.FormActivate(Sender: TObject);
var
    Cdir,Cdir1:string;
    F: TextFile;

begin
    Cdir:=ExtractFilePath(AppliCation.ExeName);
    Cdir1:=Cdir+'\port.txt';
    AssignFile(F, Cdir1);
    Reset(F);
    Readln(F, Cdir1);
    closefile(F);
    if Cdir1 ='' then
        g_Port:=0
    else
        g_Port:=strtoint(Cdir1);
    Cdir1:=Cdir+'\pass.txt';
    AssignFile(F, Cdir1);
    Reset(F);
    Readln(F, Cdir1);
    closefile(F);
    if Cdir ='' then
        n_pass:=0
    else
        n_pass:=strtoint(Cdir1);
    edit32.Text:=inttostr(n_pass);


//               1：新用户卡（用户传递卡），2：旧用户卡，3：总初始化卡
//					    4：回收卡，5：初始化卡，6：1方气卡
//					    7：恢复卡，8：生产工具卡，9：清气量卡，10：查询卡，11：检测卡
//					    12：密钥卡，13：30新用户卡，14停气卡，15:转移卡，16：预置卡,2x:气量卡，x为1-9


    combobox1.Items.Add('新用户卡') ;
    combobox1.Items.Add('旧用户卡') ;
    combobox1.Items.Add('总初始化卡') ;
    combobox1.Items.Add('回收卡') ;
    combobox1.Items.Add('初始化卡') ;
    combobox1.Items.Add('1方气卡') ;
    combobox1.Items.Add('恢复卡') ;
    combobox1.Items.Add('生产工具卡') ;
    combobox1.Items.Add('清气量卡') ;
    combobox1.Items.Add('查询卡') ;

    combobox1.Items.Add('检测卡') ;
    combobox1.Items.Add('密钥卡') ;
    combobox1.Items.Add('30新用户卡') ;
    combobox1.Items.Add('停气卡') ;
    combobox1.Items.Add('转移卡') ;
    combobox1.Items.Add('预置卡') ;
    combobox1.Items.Add('气量卡0.1方') ;
    combobox1.Items.Add('气量卡0.2方') ;
    combobox1.Items.Add('气量卡0.3方') ;
    combobox1.Items.Add('气量卡0.4方') ;
    combobox1.Items.Add('气量卡0.5方') ;
    combobox1.Items.Add('气量卡0.6方') ;
    combobox1.Items.Add('气量卡0.7方') ;
    combobox1.Items.Add('气量卡0.8方') ;
    combobox1.Items.Add('气量卡0.9方') ;

    combobox1.ItemIndex:=0;

    combobox2.Items.Add('102民用表') ;
    combobox2.Items.Add('442民用表') ;
    combobox2.Items.Add('102工业表') ;
    combobox2.Items.Add('M1 工业表') ;
    combobox2.ItemIndex:=1;

    combobox3.Items.Add('0') ;
    combobox3.Items.Add('1') ;
    combobox3.Items.Add('2') ;
    combobox3.Items.Add('3') ;
    combobox3.Items.Add('4') ;
    combobox3.Items.Add('5') ;
    combobox3.Items.Add('6') ;
    combobox3.Items.Add('7') ;
    combobox3.Items.Add('8') ;
    combobox3.Items.Add('9') ;
    if n_pass = 9 then
      combobox3.ItemIndex:=0
    else
      combobox3.ItemIndex:=n_pass+1;

end;

procedure TForm1.Button16Click(Sender: TObject);
begin
  close;
end;

end.
