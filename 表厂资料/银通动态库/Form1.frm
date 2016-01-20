VERSION 5.00
Begin VB.Form Form1 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "银通库VB测试程序"
   ClientHeight    =   3960
   ClientLeft      =   45
   ClientTop       =   405
   ClientWidth     =   8295
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   3960
   ScaleWidth      =   8295
   StartUpPosition =   3  '窗口缺省
   Begin VB.CommandButton Command11 
      Caption         =   "设置端口"
      Height          =   375
      Left            =   5070
      TabIndex        =   33
      Top             =   75
      Width           =   1020
   End
   Begin VB.CommandButton Command10 
      Caption         =   "GetisEntontech"
      Height          =   375
      Left            =   3585
      TabIndex        =   32
      Top             =   75
      Width           =   1470
   End
   Begin VB.CommandButton Command9 
      Caption         =   "GetIniPath"
      Height          =   375
      Left            =   2610
      TabIndex        =   31
      Top             =   3015
      Width           =   1230
   End
   Begin VB.ComboBox purposeText 
      Height          =   300
      ItemData        =   "Form1.frx":0000
      Left            =   6600
      List            =   "Form1.frx":000D
      Style           =   2  'Dropdown List
      TabIndex        =   30
      Top             =   1995
      Width           =   1605
   End
   Begin VB.CommandButton Command8 
      Caption         =   "获取本机注册ID"
      Height          =   375
      Left            =   45
      TabIndex        =   29
      Top             =   2970
      Width           =   1815
   End
   Begin VB.CommandButton Command5 
      Caption         =   "读卡"
      Height          =   390
      Left            =   2415
      TabIndex        =   25
      Top             =   75
      Width           =   1155
   End
   Begin VB.CommandButton Command7 
      Caption         =   "库初始"
      Height          =   390
      Left            =   1215
      TabIndex        =   28
      Top             =   75
      Width           =   1215
   End
   Begin VB.CommandButton Command6 
      Caption         =   "获取详细错误"
      Height          =   390
      Left            =   7050
      TabIndex        =   27
      Top             =   60
      Width           =   1200
   End
   Begin VB.TextBox RspText 
      Height          =   330
      Left            =   6195
      TabIndex        =   26
      Top             =   105
      Width           =   810
   End
   Begin VB.CommandButton Command4 
      Caption         =   "修改"
      Height          =   375
      Left            =   6960
      TabIndex        =   15
      Top             =   2970
      Width           =   1260
   End
   Begin VB.CommandButton Command2 
      Caption         =   "制卡"
      Height          =   375
      Left            =   5715
      TabIndex        =   13
      Top             =   2970
      Width           =   1260
   End
   Begin VB.Frame Frame1 
      Caption         =   "卡片基础信息"
      Height          =   1995
      Left            =   60
      TabIndex        =   2
      Top             =   915
      Width           =   8190
      Begin VB.TextBox DataText 
         Height          =   315
         Left            =   6525
         TabIndex        =   24
         Top             =   1485
         Width           =   1590
      End
      Begin VB.TextBox fractionText 
         Height          =   315
         Left            =   3720
         TabIndex        =   23
         Top             =   1530
         Width           =   1590
      End
      Begin VB.TextBox TypeText 
         Height          =   315
         Left            =   960
         TabIndex        =   22
         Top             =   1485
         Width           =   1590
      End
      Begin VB.TextBox ModeText 
         Height          =   315
         Left            =   3705
         TabIndex        =   21
         Top             =   1110
         Width           =   1590
      End
      Begin VB.TextBox TelText 
         Height          =   315
         Left            =   945
         TabIndex        =   20
         Top             =   1110
         Width           =   1590
      End
      Begin VB.TextBox AddText 
         Height          =   315
         Left            =   945
         TabIndex        =   19
         Top             =   705
         Width           =   7155
      End
      Begin VB.TextBox DateText 
         Height          =   315
         Left            =   6510
         TabIndex        =   18
         Top             =   345
         Width           =   1590
      End
      Begin VB.TextBox NameText 
         Height          =   315
         Left            =   3660
         TabIndex        =   17
         Top             =   330
         Width           =   1590
      End
      Begin VB.TextBox CodeText 
         Height          =   315
         Left            =   960
         TabIndex        =   16
         Top             =   330
         Width           =   1590
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "卡片余量"
         Height          =   180
         Index           =   9
         Left            =   5535
         TabIndex        =   12
         Top             =   1605
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "卡表用途"
         Height          =   180
         Index           =   8
         Left            =   5505
         TabIndex        =   11
         Top             =   1200
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "卡表小数"
         Height          =   180
         Index           =   7
         Left            =   2730
         TabIndex        =   10
         Top             =   1590
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "卡片型号"
         Height          =   180
         Index           =   6
         Left            =   135
         TabIndex        =   9
         Top             =   1575
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "写卡方式"
         Height          =   180
         Index           =   5
         Left            =   2745
         TabIndex        =   8
         Top             =   1200
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "地址"
         Height          =   180
         Index           =   4
         Left            =   465
         TabIndex        =   7
         Top             =   765
         Width           =   360
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "电话"
         Height          =   180
         Index           =   3
         Left            =   465
         TabIndex        =   6
         Top             =   1170
         Width           =   360
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "制卡日期"
         Height          =   180
         Index           =   2
         Left            =   5565
         TabIndex        =   5
         Top             =   405
         Width           =   720
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "姓名"
         Height          =   180
         Index           =   1
         Left            =   2970
         TabIndex        =   4
         Top             =   405
         Width           =   360
      End
      Begin VB.Label Label1 
         AutoSize        =   -1  'True
         BackStyle       =   0  'Transparent
         Caption         =   "卡号"
         Height          =   180
         Index           =   0
         Left            =   480
         TabIndex        =   3
         Top             =   375
         Width           =   360
      End
   End
   Begin VB.TextBox DLLverText 
      Height          =   330
      Left            =   30
      TabIndex        =   1
      Top             =   495
      Width           =   8220
   End
   Begin VB.CommandButton Command1 
      Caption         =   "版本"
      Height          =   390
      Left            =   15
      TabIndex        =   0
      Top             =   75
      Width           =   1215
   End
   Begin VB.CommandButton Command3 
      Caption         =   "购气"
      Height          =   375
      Left            =   4470
      TabIndex        =   14
      Top             =   2970
      Width           =   1260
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Dim Rsp As Long, RspStr As String * 100
Private Sub Command1_Click()
    DLLverText.Text = Get_DLL_Version
End Sub

Private Sub Command10_Click()
   RspText.Text = Get_is_Entontech()
End Sub

Private Sub Command11_Click()
    If Len(RspText.Text) = 0 Then RspText.Text = 1
    RspText.Text = Set_IC_Com(Int(RspText.Text))
End Sub

Private Sub Command2_Click()
     On Error Resume Next
        With Cardinfo
            .Code = Format(CodeText.Text, "00000000") '编号必须
            .Name = NameText.Text
            .Times = DateText.Text
            .Add = AddText.Text
            .Tel = TelText.Text
            .mode = ModeText.Text
            .purpose = purposeText.ListIndex
            .Types = TypeText.Text
            .fraction = fractionText.Text
            .Datas = 0
        End With
        Call DLLtype_AppType(False)             '数据交换
        RspText.Text = Write_Info(ICinfos)
End Sub

Private Sub Command3_Click()
 On Error Resume Next
        Cardinfo.Datas = DataText.Text
        Call DLLtype_AppType(False)             '数据交换
        RspText.Text = Write_Data(ICinfos)
End Sub

Private Sub Command4_Click()
 On Error Resume Next
        With Cardinfo
            .Code = Format(CodeText.Text, "00000000")
            .Name = NameText.Text
            .Times = DateText.Text
            .Add = AddText.Text
            .Tel = TelText.Text
            .mode = ModeText.Text
            .purpose = purposeText.ListIndex
            .Types = TypeText.Text
            .fraction = fractionText.Text
            .Datas = 0
        End With
        Call DLLtype_AppType(False)             '数据交换
        RspText.Text = Modify_Info(ICinfos)
End Sub

Private Sub Command5_Click()
    CodeText.Text = ""
    NameText.Text = ""
    DateText.Text = ""
    AddText.Text = ""
    TelText.Text = ""
    ModeText.Text = ""
   purposeText.ListIndex = 0
    TypeText.Text = ""
    fractionText.Text = ""
    DataText.Text = ""
    Me.Refresh
    RspText.Text = Read_Data(ICinfos)
    Call DLLtype_AppType(True)              '数据交换
    With Cardinfo
        CodeText.Text = .Code
        NameText.Text = .Name
        DateText.Text = .Times
        AddText.Text = .Add
        TelText.Text = .Tel
        ModeText.Text = .mode
        purposeText.ListIndex = .purpose
        TypeText.Text = .Types
        fractionText.Text = .fraction
        DataText.Text = .Datas
    End With
End Sub

Private Sub Command6_Click()
    Rsp = RspText.Text
    RspStr = ""
    Rsp = GetErrorMsg(Rsp, RspStr)
    DLLverText.Text = Rsp & "->" & RspStr
End Sub

Private Sub Command7_Click()
    RspText.Text = GetInis
End Sub

Private Sub Command8_Click()
    RspStr = ""
    Rsp = GetErrorMsg(99, RspStr)
    DLLverText.Text = RspStr
End Sub

Private Sub Command9_Click()
    MsgBox Get_Ini_Path()
End Sub

