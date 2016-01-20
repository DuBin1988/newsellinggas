Attribute VB_Name = "EntontechModule"
Public Type Usta_old
    Name         As String * 8 '����
    Code         As String * 8 '���
    Times        As String * 10 'ʱ��
    Tel          As String * 11 '�绰
    UAddr        As String * 19 '��ַ
    UInt(3)      As Byte        'Ԥ��
    mode         As String * 1  '��ʽ
    purpose      As String * 1  '��;
    fraction     As String * 1  'С��
End Type
Public Type Usta_New
    Names        As String * 8   '����
    Dates(3)     As Byte         'ʱ��
    Tele         As String * 12  '�绰
    addr         As String * 25  '��ַ
    ridgepole    As String * 3   '��
    door         As String * 1   '��
    antrum       As String * 3   '��
    Flag(7)      As Byte         'Ԥ��
End Type
Public Type ICinfo
    Name         As String * 8           '����
    Code         As String * 8           '���
    Times        As String * 10          '��������
    Tel          As String * 11          '��ϵ�绰
    Add          As String * 23          '��ϵ��ַ
    Types        As String * 1           '0 ��ͨ[102] 1 ��ͨ[153] 2 ��ͨ[153+] 3 ��ͨ[153++] 4 �°�[102]
    mode         As String * 1           '��ʽ        0 д��   1 д��
    purpose      As String * 1           '��;        0 ����   1 ����  2������
    fraction     As String * 1           'С��
    Datas        As Currency             '������
End Type
Public Type Usta
    'Stringo  As String * 64
    '�˴���Ϊ �ֽڶ�������
    Stringo(63)   As Byte
    Uversion As Byte
    Code     As Long
    amt      As Long
End Type

'���ؿ������ļ�·��
Public Declare Function Get_Ini_Path Lib "Entontech" () As String

'���ļ��汾��Ϣ
Public Declare Function Get_DLL_Version Lib "Entontech" () As String
'��ȡ����
Public Declare Function GetInis Lib "Entontech" () As Integer
'���ش�����Ϣ
Public Declare Function GetErrorMsg Lib "Entontech" (ByVal Errlist As Long, ByVal Errstr As String) As Integer

'���ö˿�
Public Declare Function Set_IC_Com Lib "Entontech" (ByVal Coms As Long) As Integer

'����
Public Declare Function Read_Data Lib "Entontech" (ByRef ICinfo As Usta) As Integer
'д��
Public Declare Function Write_Data Lib "Entontech" (ByRef ICinfo As Usta) As Integer
'�޸�
Public Declare Function Modify_Info Lib "Entontech" (ByRef ICinfo As Usta) As Integer
'�ƿ�
Public Declare Function Write_Info Lib "Entontech" (ByRef ICinfo As Usta) As Integer

'���ؿ�Ƭ�Ƿ�����ͨ��
Public Declare Function Get_is_Entontech Lib "Entontech" () As Integer


Public ICinfos As Usta
Public Cardinfo As ICinfo
Public Declare Sub CopyMemory Lib "kernel32" Alias "RtlMoveMemory" (Destination As Any, Source As Any, ByVal length As Long)
Public Function ReadData(Xl As Usta_old, nl As Usta_New, X As Currency, Datainfo As Currency) As String
On Error Resume Next
Dim I As Long
Dim Temp As String
If X = 0 Then
     If Xl.fraction = "A" Then
         I = 1: Temp = "########.0" 'С��
     ElseIf Xl.fraction = "B" Then
         I = 2: Temp = "########.00"
     ElseIf Xl.fraction = "C" Then
         I = 3: Temp = "########.000"
     ElseIf Xl.fraction = "D" Then
         I = 0: Temp = "########"
     Else
         I = 2: Temp = "########.00"
     End If
     ReadData = Format((Format(Datainfo, "00000000") / 10 ^ I), Temp)
Else
     If nl.Flag(6) = 65 Then
         I = 1: Temp = "########.0" 'С��
     ElseIf nl.Flag(6) = 66 Then
         I = 2: Temp = "########.00"
     ElseIf nl.Flag(6) = 67 Then
         I = 3: Temp = "########.000"
     ElseIf nl.Flag(6) = 68 Then
         I = 0: Temp = "########"
     Else
         I = 2: Temp = "########.00"
     End If
     ReadData = Format((Format(Datainfo, "00000000") / 10 ^ I), Temp)
End If
End Function
Public Function WriteData(X As ICinfo) As String
On Error Resume Next
    Dim I As Long
    If X.fraction = 0 Then I = 1
    If X.fraction = 1 Then I = 10
    If X.fraction = 2 Then I = 100
    If X.fraction = 3 Then I = 1000
    WriteData = Format(X.Datas * I, "00000000")
End Function
Public Sub DLLtype_AppType(DllToApp As Boolean)
On Error Resume Next
Dim TempOld As Usta_old
Dim TempNew  As Usta_New
Dim d(3) As String * 2
Dim U As String
    If DllToApp Then
        With Cardinfo
            If ICinfos.Uversion = 0 Then .Types = 0
            If ICinfos.Uversion = 5 Then .Types = 1
            If ICinfos.Uversion = 6 Then .Types = 2
            If ICinfos.Uversion = 7 Then .Types = 3
            If ICinfos.Uversion <> 7 Then
                CopyMemory TempOld, ICinfos.Stringo(0), ByVal 64
                .Datas = ReadData(TempOld, TempNew, 0, Val(ICinfos.amt))
                .Name = Trim(TempOld.Name)
                .Code = Format(ICinfos.Code, "00000000")
                .Times = TempOld.Times
                .Tel = Trim(TempOld.Tel)
                .Add = Trim(TempOld.UAddr)
                If TempOld.mode = "F" Then .mode = 1 Else .mode = 0
                 If TempOld.purpose = "I" Then
                     .purpose = 0
                ElseIf TempOld.purpose = "L" Then
                    .purpose = 2
                 ElseIf TempOld.purpose = "Z" Then
                    .purpose = 3
                Else
                    .purpose = 1
                End If
                If TempOld.fraction = "A" Then
                    .fraction = 1
                ElseIf TempOld.fraction = "B" Then
                    .fraction = 2
                ElseIf TempOld.fraction = "C" Then
                    .fraction = 3
                ElseIf TempOld.fraction = "D" Then
                    .fraction = 0
                Else
                    .fraction = 2
                End If
            Else
                CopyMemory TempNew, ICinfos.Stringo(0), ByVal 64
                .Code = Format(ICinfos.Code, "00000000")
                .Datas = ReadData(TempOld, TempNew, 1, Val(ICinfos.amt))
                .Name = Trim(TempNew.Names)
                .Tel = Trim(TempNew.Tele)
                .Add = Trim(TempNew.addr)
                If TempNew.Flag(4) = 70 Then .mode = 1 Else .mode = 0
                If TempNew.Flag(5) = 73 Then
                    .purpose = 0
                ElseIf TempNew.Flag(5) = 76 Then 'l
                    .purpose = 2
                ElseIf TempNew.Flag(5) = 90 Then
                    .purpose = 3
                Else
                    .purpose = 1
                End If
                If TempNew.Flag(6) = 65 Then
                    .fraction = 1
                ElseIf TempNew.Flag(6) = 66 Then
                    .fraction = 2
                ElseIf TempNew.Flag(6) = 67 Then
                    .fraction = 3
                ElseIf TempNew.Flag(6) = 68 Then
                    .fraction = 0
                Else
                    .fraction = 2
                End If
                
                d(0) = Format(TempNew.Dates(0), "00")
                d(1) = Format(TempNew.Dates(1), "00")
                d(2) = Format(TempNew.Dates(2), "00")
                d(3) = Format(TempNew.Dates(3), "00")
                U = d(0) & d(1) & "-" & d(2) & "-" & d(3)
                .Times = (Format(U, "YYYY-MM-DD"))
            End If
        End With
    Else
        ICinfos.Code = Cardinfo.Code
        ICinfos.amt = WriteData(Cardinfo)
        U = Format(Cardinfo.Times, "YYYY-MM-DD")
        
        If Cardinfo.Types = 3 Then
            With TempNew
                .Names = Cardinfo.Name
                .Dates(0) = Mid(U, 1, 2)
                .Dates(1) = Mid(U, 3, 2)
                .Dates(2) = Mid(U, 6, 2)
                .Dates(3) = Mid(U, 9, 2)
                .Tele = Val(Cardinfo.Tel)
                .addr = Trim(Cardinfo.Add)
                If Cardinfo.fraction = 1 Then
                    .Flag(6) = 65
                ElseIf Cardinfo.fraction = 2 Then
                    .Flag(6) = 66
                ElseIf Cardinfo.fraction = 3 Then
                    .Flag(6) = 67
                ElseIf Cardinfo.fraction = 0 Then
                    .Flag(6) = 68
                End If
                If Cardinfo.mode = 1 Then .Flag(4) = "70" Else .Flag(4) = "71"
                If Cardinfo.purpose = 0 Then
                    .Flag(5) = 73
                ElseIf Cardinfo.purpose = 2 Then
                    .Flag(5) = 76
                ElseIf Cardinfo.purpose = 3 Then
                    .Flag(5) = 90
                Else
                    .Flag(5) = 67
                End If
            End With
            CopyMemory ICinfos.Stringo(0), TempNew, ByVal 64
        Else
            With TempOld
                .Name = Cardinfo.Name
                .Code = Cardinfo.Code
                .Times = Cardinfo.Times
                .Tel = Cardinfo.Tel
                .UAddr = Cardinfo.Add
                If Cardinfo.mode = 1 Then .mode = "F" Else .mode = "G"
                If Cardinfo.purpose = 0 Then
                    .purpose = "I"
                ElseIf Cardinfo.purpose = 2 Then
                    .purpose = "L"
                 ElseIf Cardinfo.purpose = 3 Then
                    .purpose = "Z"
                Else
                    .purpose = "C"
                End If
                If Cardinfo.fraction = 1 Then
                    .fraction = "A"
                ElseIf Cardinfo.fraction = 2 Then
                    .fraction = "B"
                ElseIf Cardinfo.fraction = 3 Then
                    .fraction = "C"
                ElseIf Cardinfo.fraction = 0 Then
                    .fraction = "D"
                End If
            End With
            CopyMemory ICinfos.Stringo(0), TempOld, ByVal 64
        End If
        If Cardinfo.Types = 0 Then ICinfos.Uversion = 0
        If Cardinfo.Types = 1 Then ICinfos.Uversion = 5
        If Cardinfo.Types = 2 Then ICinfos.Uversion = 6
        If Cardinfo.Types = 3 Then ICinfos.Uversion = 7
    End If
End Sub
