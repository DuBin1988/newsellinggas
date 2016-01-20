object Form2: TForm2
  Left = 363
  Top = 169
  Width = 501
  Height = 328
  Caption = #27979#35797
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  Position = poScreenCenter
  PixelsPerInch = 96
  TextHeight = 13
  object Label11: TLabel
    Left = 15
    Top = 18
    Width = 48
    Height = 13
    Caption = #20018#21475#21495#65306
  end
  object Label16: TLabel
    Left = 150
    Top = 16
    Width = 48
    Height = 13
    Caption = #27874#29305#29575#65306
  end
  object Label3: TLabel
    Left = 16
    Top = 256
    Width = 393
    Height = 13
    AutoSize = False
    Caption = #20889#26631#24535#65306'WriteFlag=2'#20026#21806#27668#65292'WriteFlag=1'#20026#34917#21345#65292'WriteFlag=0'#20026#24320#25143
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clNavy
    Font.Height = -11
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    ParentFont = False
  end
  object Label6: TLabel
    Left = 16
    Top = 277
    Width = 353
    Height = 13
    AutoSize = False
    Caption = #21345#31867#21035#65306'tpsw='#39'0'#39#20026#29992#25143#21345#65292'tpsw='#39'1'#39#20026#24320#25143#21345#65292'tpsw='#39'2'#39#26032#21345
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clNavy
    Font.Height = -11
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    ParentFont = False
  end
  object ComboBox1: TComboBox
    Left = 63
    Top = 13
    Width = 81
    Height = 21
    Style = csDropDownList
    ItemHeight = 13
    TabOrder = 0
    Items.Strings = (
      'com1'
      'com2'
      'com3'
      'com4')
  end
  object Button2: TButton
    Left = 401
    Top = 13
    Width = 75
    Height = 25
    Caption = #21021#22987#21270#21345
    TabOrder = 1
    OnClick = Button2Click
  end
  object GroupBox1: TGroupBox
    Left = 16
    Top = 48
    Width = 465
    Height = 201
    Caption = #29992#25143#21345#35835#20889#25805#20316
    TabOrder = 2
    object Label1: TLabel
      Left = 8
      Top = 32
      Width = 36
      Height = 13
      Caption = #21345#21495#65306
    end
    object Label2: TLabel
      Left = 240
      Top = 29
      Width = 48
      Height = 13
      Caption = #29992#25143#21495#65306
    end
    object Label4: TLabel
      Left = 240
      Top = 79
      Width = 48
      Height = 13
      Caption = #36141#20080#37327#65306
    end
    object Label5: TLabel
      Left = 8
      Top = 79
      Width = 77
      Height = 13
      Caption = #36141'/'#20805#20080#27425#25968#65306
    end
    object Label7: TLabel
      Left = 8
      Top = 125
      Width = 89
      Height = 13
      Caption = #20889#26631#24535'/'#21345#31867#21035#65306
    end
    object Label8: TLabel
      Left = 240
      Top = 125
      Width = 48
      Height = 13
      Caption = #21345#26631#35782#65306
    end
    object Edit1: TEdit
      Left = 101
      Top = 28
      Width = 113
      Height = 21
      TabOrder = 0
      Text = 'Edit1'
    end
    object Edit2: TEdit
      Left = 328
      Top = 25
      Width = 121
      Height = 21
      TabOrder = 1
      Text = 'Edit2'
    end
    object Edit4: TEdit
      Left = 328
      Top = 75
      Width = 121
      Height = 21
      TabOrder = 2
      Text = 'Edit4'
    end
    object Edit5: TEdit
      Left = 101
      Top = 75
      Width = 113
      Height = 21
      TabOrder = 3
      Text = 'Edit5'
    end
    object Edit7: TEdit
      Left = 101
      Top = 121
      Width = 113
      Height = 21
      TabOrder = 4
      Text = 'Edit7'
    end
    object Edit8: TEdit
      Left = 328
      Top = 121
      Width = 121
      Height = 21
      TabOrder = 5
      Text = 'Edit8'
    end
    object Button1: TButton
      Left = 97
      Top = 159
      Width = 75
      Height = 25
      Caption = #35835#29992#25143#21345
      TabOrder = 6
      OnClick = Button1Click
    end
    object Button3: TButton
      Left = 176
      Top = 159
      Width = 121
      Height = 25
      Caption = #24320#21345'('#23494#38053#20256#36755#21345')'
      TabOrder = 7
      OnClick = Button3Click
    end
    object Button5: TButton
      Left = 306
      Top = 160
      Width = 75
      Height = 25
      Caption = #34917#29992#25143#21345
      TabOrder = 8
      OnClick = Button5Click
    end
    object Button4: TButton
      Left = 385
      Top = 159
      Width = 75
      Height = 25
      Caption = #36141#20080#20889#21345
      TabOrder = 9
      OnClick = Button4Click
    end
    object Button7: TButton
      Left = 16
      Top = 159
      Width = 75
      Height = 25
      Caption = #26032#21345#27979#35797
      TabOrder = 10
      OnClick = Button7Click
    end
  end
  object Button6: TButton
    Left = 288
    Top = 12
    Width = 105
    Height = 25
    Caption = #22825#20449'CPU'#21345#21028#26029
    TabOrder = 3
    OnClick = Button6Click
  end
  object ComboBox2: TComboBox
    Left = 198
    Top = 13
    Width = 73
    Height = 21
    ItemHeight = 13
    ItemIndex = 0
    TabOrder = 4
    Text = '9600'
    Items.Strings = (
      '9600')
  end
end
