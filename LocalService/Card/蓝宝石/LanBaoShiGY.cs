using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class LanBaoShiGY : ICard
    {
        private static Log Log = Log.GetInstance("Card.LanBaoShiGY");

        public string Test()
        {
            return "ruisen";
        }

        #region 蓝宝石动态库导入
        //测卡，标准接口
        [DllImport("LtLBSHIC2013.dll", EntryPoint = "CheckLibrary", CallingConvention = CallingConvention.StdCall)]
        public static extern int CheckLibrary(ref PLtCardReader pcr, ref PUserCard puc, ref int IsTrue);
        //读卡，标准接口
        [DllImport("LtLBSHIC2013.dll", EntryPoint = "ReadUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadUserCard(ref PLtCardReader pcr, ref PUserCard puc, int pti);
        //开户，标准接口
        [DllImport("LtLBSHIC2013.dll", EntryPoint = "IssueUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int IssueUserCard(ref PLtCardReader pcr, ref PUserCard puc, int pti);
        //购气，标准接口
        [DllImport("LtLBSHIC2013.dll", EntryPoint = "RechargeUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int RechargeUserCard(ref PLtCardReader pcr, ref PUserCard puc, int pti);
        //清卡，标准接口
        [DllImport("LtLBSHIC2013.dll", EntryPoint = "RecycleUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int RecycleUserCard(ref PLtCardReader pcr, ref PUserCard puc, int pti);
        #endregion

        #region 结构体声明
        public unsafe struct PLtCardReader
        {

            public int dwDevType;	// 0: 明华明泰TZX-R2x系列;  1: 明华明泰URD-R310系列; 2: 明华RD系列; 3: 明华澳汉URD-N系列
            public int dwPort;				// 端口号
            public int dwBaud;				// 波特率
            public int dv_beep;
            public fixed byte Reserved[4 + 24 + 3076];

        }

        public struct PLtMeterStruct
        {
            public int cbSize;						// 本结构的长度
            public int WriteBack;					// 是否有回写，0：无，非0：有
            public long TotalAmount;				// 表累计使用量
            public long TotalMoney;				// 保留
            public long RemainAmount;				// 表剩余量
            public long RemainMoney;				// 保留
            public int RechargeTimes;				// 已充值次数
            public int OverflowTimes;				// 过流次数
            public int MagnetismInterferedTimes;	// 磁干扰次数
            public int Valve;						// 阀门状态
            public int Battery;						// 电池状态
        }

        public struct PAdvancedSettingsStruct
        {
            public int cbSize;						// 本结构的长度
            public long WarnAmount;				// 告警量
            public long WarnMoney;					// 保留
            public long AllowedOverdraftAmount;	// 可透支量
            public long AllowedOverdraftMoney;	// 保留
            public int ValveOffOnIdle;				// 保留
            public int ValveOffOnWarning;			// 保留
            public int ValveOffOnLowPower;			// 保留            
        }



        public unsafe struct PUserCard
        {
            public int cbSize;					// 本结构的长度
            public fixed byte CardNO[40];				// 卡号（表号）
            public fixed byte UserNO[40];				// 区域号
            public int RechargeTimes;			// 充值次数
            public long RechargeAmount;		// 充值数量
            public long RechargeMoney;		// 保留
            public int StepPrices;				// 保留
            public int IssueTimes;				// 保留
            public int Reserved;					// 保留
            public int MecMeterType;			// 0-民用表，1-工业表
            public fixed byte ExtraData[256];		// 保留
            public PAdvancedSettingsStruct pass;
            public PLtMeterStruct Meter;		// 卡表回写信息        
        }

        //将string型转换成byte*
        private unsafe void FillBytes(byte* cn, String s)
        {
            for (int i = 0; i < s.Length; i++)
                *cn++ = (byte)s[i];
            *cn++ = 0;
        }
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int rs = -1;
            try 
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com,
                    dwBaud = baud,
                    dv_beep = 0
                };

                PUserCard puc1 = new PUserCard();
                int istrue = 0;
                Log.Debug("LanBaoShiGY CheckGasCard start");
                rs = CheckLibrary(ref pcr1, ref puc1, ref istrue);
                Log.Debug("LanBaoShiGY CheckGasCard end,return:" + rs);
                if (1 == istrue)
                {
                    int pti = 0;
                    Log.Debug("LanBaoShiGY ReadGasCard start");
                    rs = ReadUserCard(ref pcr1, ref puc1, pti);
                    Log.Debug("LanBaoShiGY ReadGasCard end,return:" + rs);
                    if (puc1.MecMeterType == 1)
                    {
                        Log.Debug("此卡是蓝宝石工业卡！");
                        return 0;
                    }
                    Log.Debug("此卡不是蓝宝石工业卡！");
                    return -1;
                }
                Log.Debug("此卡不是蓝宝石工业卡！");
                return -1;
            }
            catch(Exception e)
            {
                Log.Debug("蓝宝石工业判卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int rs = -1;
            try
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com,
                    dwBaud = baud,
                    dv_beep = 0
                };

                PUserCard puc1 = new PUserCard();
                int pti = 0;
                Log.Debug("LanBaoShiGY FormatGasCard start");
                String buf;
                String buf1;
                Log.Debug("擦卡前读卡格式:");
                MingHua.GetSnapShot(com, baud, out buf);
                rs = RecycleUserCard(ref pcr1, ref puc1, pti);
                Log.Debug("擦卡后读卡格式:");
                MingHua.GetSnapShot(com, baud, out buf1);
                Log.Debug(buf);
                Log.Debug(buf1);
                rs = RecycleUserCard(ref pcr1, ref puc1, pti);
                Log.Debug("LanBaoShiGY FormatGasCard end,return:" + rs);
                if (rs == 0)
                {
                    Log.Debug("蓝宝石工业擦卡成功！");
                    return 0;
                }
                else
                {
                    Log.Debug("蓝宝石工业擦卡失败！");
                    return -1;
                }
            }
            catch (Exception e)
            {
                Log.Debug("蓝宝石工业擦卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        }

        public unsafe int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int rs = -1;
            try
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com,
                    dwBaud = baud,
                    dv_beep = 0
                };
                PLtMeterStruct pms1 = new PLtMeterStruct()
                {
                    cbSize = 0,
                    WriteBack = 0,
                    TotalAmount = 0,
                    TotalMoney = 0,
                    RemainAmount = 0,
                    RemainMoney = 0,
                    RechargeTimes = 0,
                    OverflowTimes = 0,
                    MagnetismInterferedTimes = 0,
                    Valve = 0,
                    Battery = 0
                };

                PAdvancedSettingsStruct past = new PAdvancedSettingsStruct()
                {
                    cbSize = 0,
                    WarnAmount = 10,
                    WarnMoney = 0,
                    AllowedOverdraftAmount = 0,
                    AllowedOverdraftMoney = 0,
                    ValveOffOnIdle = 0,
                    ValveOffOnLowPower = 0,
                    ValveOffOnWarning = 0
                };

                PUserCard puc1 = new PUserCard()
                {
                    cbSize = 0,
                    RechargeTimes = cs,
                    RechargeMoney = 0,
                    MecMeterType = 1
                };
                FillBytes(puc1.CardNO, kh);
                FillBytes(puc1.UserNO, dqdm);

                int pti = 0;
                if (ql != 0)
                {
                    puc1.RechargeAmount = ql * 10000;
                    String buf;
                    String buf1;
                    Log.Debug("购气前读卡格式:");
                    MingHua.GetSnapShot(com, baud, out buf);
                    rs = RechargeUserCard(ref pcr1, ref puc1, pti);
                    Log.Debug("购气后读卡格式:");
                    MingHua.GetSnapShot(com, baud, out buf1);
                    Log.Debug(buf);
                    Log.Debug(buf1);
                    if (rs == 0)
                    {
                        Log.Debug("蓝宝石工业购气成功！");
                        return 0;
                    }
                    else
                    {
                        Log.Debug("蓝宝石工业购气失败！");
                        return -1;
                    }
                }
                else
                {
                    int rt = ReadUserCard(ref pcr1, ref puc1, pti);
                    int gas = (int)puc1.RechargeAmount;
                    puc1.RechargeAmount = -gas;
                    String buf;
                    String buf1;
                    Log.Debug("退气前读卡格式:");
                    MingHua.GetSnapShot(com, baud, out buf);
                    rs = RechargeUserCard(ref pcr1, ref puc1, pti);
                    Log.Debug("退气后读卡格式:");
                    MingHua.GetSnapShot(com, baud, out buf1);
                    Log.Debug(buf);
                    Log.Debug(buf1);
                    if (rs == 0)
                    {
                        Log.Debug("蓝宝石工业冲正成功！");
                        return 0;
                    }
                    else
                    {
                        Log.Debug("蓝宝石工业冲正失败！");
                        return -1;
                    }
                }
            }
            catch (Exception e)
            {
                Log.Debug("蓝宝石工业购气卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        }

        public string Name
        {
            get
            {
                return "lanbaoshigy";
            }
        }

        public unsafe int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
            int rs = -1;
            try
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com,
                    dwBaud = baud,
                    dv_beep = 0
                };

                PUserCard puc1 = new PUserCard();

                int pti = 0;
                Log.Debug("LanBaoShi ReadGasCard start");
                rs = ReadUserCard(ref pcr1, ref puc1, pti);
                Log.Debug("LanBaoShi ReadGasCard end,return:" + rs);
                cs = (short)puc1.RechargeTimes;
                ql = (int)puc1.RechargeAmount / 10000;
                dqdm = new string((sbyte*)puc1.UserNO);
                string cardid = new string((sbyte*)puc1.CardNO);
                string str = cardid.Substring(0, 2);
                if (str != "??")
                {
                    kh = cardid;
                }
                else
                {
                    kh = cardid.Remove(0, 2);
                }
                if (rs == 0)
                {
                    Log.Debug("蓝宝石工业读卡成功！");
                    return 0;
                }
                else
                {
                    Log.Debug("蓝宝石工业读卡失败！");
                    return -1;
                }
            }
            catch (Exception e)
            {
                Log.Debug("蓝宝石工业读卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        }

        public unsafe int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int rs = -1;
            try
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com,
                    dwBaud = baud,
                    dv_beep = 0
                };
                PLtMeterStruct pms1 = new PLtMeterStruct()
                {
                    cbSize = 0,
                    WriteBack = 0,
                    TotalAmount = 0,
                    TotalMoney = 0,
                    RemainAmount = 0,
                    RemainMoney = 0,
                    RechargeTimes = 0,
                    OverflowTimes = 0,
                    MagnetismInterferedTimes = 0,
                    Valve = 0,
                    Battery = 0
                };

                PAdvancedSettingsStruct past = new PAdvancedSettingsStruct()
                {
                    cbSize = 0,
                    WarnAmount = 10,
                    WarnMoney = 0,
                    AllowedOverdraftAmount = 0,
                    AllowedOverdraftMoney = 0,
                    ValveOffOnIdle = 0,
                    ValveOffOnLowPower = 0,
                    ValveOffOnWarning = 0
                };

                PUserCard puc1 = new PUserCard()
                {
                    cbSize = 0,
//                    RechargeTimes = cs,
                    RechargeAmount = ql * 10000,
                    RechargeMoney = 0,
                    MecMeterType = 1
                };

                //发卡购气
                if (kzt == 0)
                {
                    //次数一定是1
                    puc1.RechargeTimes = 1;
                }
                //换表补气
                else
                {
                    puc1.RechargeTimes = cs;
                }

                FillBytes(puc1.CardNO, kh);
                FillBytes(puc1.UserNO, dqdm);
                //写卡之前先格式化卡
                Log.Debug("write card ql=" + ql + ",kagas=" + puc1.RechargeAmount);
                int pti = 0;
                rs = RecycleUserCard(ref pcr1, ref puc1, pti);
                Log.Debug("clear card rs=" + rs);
                Log.Debug("LanBaiShiGY WriteNewCard start"); String buf;
                String buf1;
                Log.Debug("发卡前读卡格式:");
                MingHua.GetSnapShot(com, baud, out buf);
                rs = IssueUserCard(ref pcr1, ref puc1, pti);
                Log.Debug("发卡后读卡格式:");
                MingHua.GetSnapShot(com, baud, out buf1);
                Log.Debug(buf);
                Log.Debug(buf1);
                Log.Debug("LanBaiShiGY WriteNewCard end, return:" + rs);
                if (rs == 0)
                {
                    Log.Debug("蓝宝石工业卡写新卡成功！");
                    return 0;
                }
                else
                {
                    Log.Debug("蓝宝石工业卡写新卡失败！");
                    return -1;
                }
            }
            catch (Exception e)
            {
                Log.Debug("蓝宝石工业写新卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        }
        #endregion      
    }
}
