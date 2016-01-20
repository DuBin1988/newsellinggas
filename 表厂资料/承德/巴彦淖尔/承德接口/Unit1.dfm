object Form1: TForm1
  Left = 379
  Top = 221
  BorderStyle = bsDialog
  Caption = #21338#20896#35835#20889#21345'Demo'
  ClientHeight = 295
  ClientWidth = 227
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
  object Button1: TButton
    Left = 40
    Top = 64
    Width = 75
    Height = 25
    Caption = #21457#21345
    TabOrder = 0
    OnClick = Button1Click
  end
  object Button2: TButton
    Left = 40
    Top = 104
    Width = 75
    Height = 25
    Caption = #35835#21345
    TabOrder = 1
    OnClick = Button2Click
  end
  object Button3: TButton
    Left = 40
    Top = 144
    Width = 75
    Height = 25
    Caption = #36141#27668
    TabOrder = 2
    OnClick = Button3Click
  end
  object Button4: TButton
    Left = 40
    Top = 176
    Width = 75
    Height = 25
    Caption = #28165#21345
    TabOrder = 3
    OnClick = Button4Click
  end
  object Button5: TButton
    Left = 40
    Top = 216
    Width = 75
    Height = 25
    Caption = #34917#21345
    TabOrder = 4
    OnClick = Button5Click
  end
  object Button6: TButton
    Left = 40
    Top = 256
    Width = 75
    Height = 25
    Caption = #39564#21345
    TabOrder = 5
    OnClick = Button6Click
  end
  object ComboBox1: TComboBox
    Left = 24
    Top = 24
    Width = 145
    Height = 21
    Style = csDropDownList
    ItemHeight = 13
    ItemIndex = 0
    TabOrder = 6
    Text = 'COM1'
    Items.Strings = (
      'COM1'
      'COM2'
      'COM3'
      'COM4'
      'COM5'
      'COM6'
      'COM7'
      'COM8'
      'COM9')
  end
  object Button7: TButton
    Left = 136
    Top = 72
    Width = 75
    Height = 25
    Caption = #20889#24037#20855#21345
    TabOrder = 7
    OnClick = Button7Click
  end
end
