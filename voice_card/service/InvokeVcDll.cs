using System;
using System.Runtime.InteropServices;
//语音卡函数调用注册类
public class InvokeVcDll
{
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int LoadDRV();
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern ushort CheckValidCh();
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern ushort CheckChTypeNew(int cn);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int EnableCard(ushort wUsedCh, long wFileBufLen);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void FreeDRV();
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern bool RingDetect(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void PUSH_PLAY();
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void FeedSigFunc();
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void ResetCallerIDBuffer(ushort wChnlNo);

    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern ushort GetCallerIDStr(ushort wChnlNo, byte[] idStr);

    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void FeedRing(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void FeedPower(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void FeedRealRing(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern bool OffHookDetect(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int SetLink(ushort wOne, ushort wAnother);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void StartPlaySignal(ushort wchnlNo, ushort SigType);
    [DllImport("NewSig.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void Sig_ResetCheck(ushort wchNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void HangUp(ushort wchnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void StartSigCheck(ushort wchnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void InitDtmfBuf(ushort wchnlNo);
   
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern short GetDtmfCode(ushort wchnlNo);
    
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int ClearLink(ushort wOne,ushort wAother);
    [DllImport("NewSig.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int Sig_CheckBusy(ushort wChNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void StopPlayFile(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void OffHook(ushort wChnlNo);
    [DllImport("NewSig.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int Sig_Init(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern bool RingDetect_Ex(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern bool CheckRingEnd(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void StartHangUpDetect(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern ushort HangUpDetect(ushort wChnlNo);
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void DisableCard();
    //开始录音函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi)]
    public static extern bool StartRecordFile(ushort wChnlNo,string FileName ,long len);
   //停止录音函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi)]
    public static extern void StopRecordFile(ushort wChnlNo);  
    //检查录音是否结束
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi)]
    public static extern int CheckRecordEnd(ushort wChnlNo);  
    //
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi)]
    public static extern bool StartRecordFileNew_Ex(ushort wChnlNo, string FileName, long len);
   
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int CheckSilence(ushort lineNum,int checkNum);
    

    //监听函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int D_Delay_LinkOneToAnother(ushort one, ushort another,long lazytime);

    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int D_Delay_ClearOneFromAnother(ushort one);

    //监听函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int LinkOneToAnother(ushort one, ushort another);
    //监听函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern int ClearOneFromAnother(ushort one, ushort another);


    //播放录音文件函数
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern bool StartPlayFile(ushort num, string fileAdd, ushort index);
    //检查播放是否完成
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern bool CheckPlayEnd(ushort num);


    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern bool DtmfHit(ushort num);


    //外拨
    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern void SendDtmfBuf(ushort num,string number);

    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern void NewSendDtmfBuf(int num, string number);


    [DllImport("Tc08a32.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern bool CheckSendEnd(ushort num);

    [DllImport("NewSig.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern int Sig_StartDial(ushort num, char[] number, string pnumber, ushort word);

    //Sig_CheckDial
    [DllImport("NewSig.dll", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
    public static extern int Sig_CheckDial(ushort num);
}
