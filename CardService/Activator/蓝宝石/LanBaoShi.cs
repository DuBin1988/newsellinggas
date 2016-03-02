using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class LanBaoShi : ICard
    {
        private static Log Log = Log.GetInstance("Card.LanBaoShi");

        public string Test()
        {
            return "蓝宝石";
        }


     #region 蓝宝石动态库导入
        //测卡，标准接口
        [DllImport("LtLBSHIC.dll", EntryPoint = "CheckLibrary", CallingConvention = CallingConvention.StdCall)]
        public static extern int CheckLibrary(ref PLtCardReader pcr, ref PUserCard puc, ref int IsTrue);
        //读卡，标准接口
        [DllImport("LtLBSHIC.dll", EntryPoint = "ReadUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadUserCard(ref PLtCardReader pcr, ref PUserCard puc);
        //开户，标准接口
        [DllImport("LtLBSHIC.dll", EntryPoint = "IssueUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int IssueUserCard(ref PLtCardReader pcr, ref PUserCard puc);
        //购气，标准接口
        [DllImport("LtLBSHIC.dll", EntryPoint = "ChargeUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ChargeUserCard(ref PLtCardReader pcr, ref PUserCard puc);
        //清卡，标准接口
        [DllImport("LtLBSHIC.dll", EntryPoint = "RecycleUserCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int RecycleUserCard(ref PLtCardReader pcr, ref PUserCard puc);
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

            public int WriteBack;					// 是否有回写，0：无，非0：有
            public long Total;				// 表累计使用量
            public long TotalMoney;				// 保留
            public long Remain;				// 表剩余量
            // 保留
            public int ChargingTimes;				// 已充值次数
            public int OverflowTimes;				// 过流次数
            public int MagnetismInterferedTimes;	// 磁干扰次数
            public int Valve;						// 阀门状态
            public int Battery;						// 电池状态
            public int State1;					// 保留
            public int State2;
            public int State3;
            public int dwReserved;
            public double dblReserved;
        }

        public struct PAdvancedSettingsStruct
        {
            public int cbSize;						// 本结构的长度
            public Int32 WarnAmount;				// 告警量
            public Int32 WarnMoney;					// 保留
            public Int32 AllowedOverdraftAmount;	// 可透支量
            public Int32 AllowedOverdraftMoney;	// 保留
            public int ValveOffOnIdle;				// 保留
            public int ValveOffOnWarning;			// 保留
            public int ValveOffOnLowPower;			// 保留            
        }

          public unsafe struct PUserCard
        {
            public int cbSize;					// 本结构的长度
            public fixed byte LicenceCode[128];		// 保留
            public fixed byte CardNO[12];				// 卡号（表号）
            public fixed byte UserNO[12];				// 区域号
            public int ChargingTimes;			// 充值次数
            public double ChargingAmount;		// 充值数量
            public double Price;



            public int IssueTimes;				// 保留

            public int GasType;					// 保留
            public int BigMacType;			// 0-民用表，1-工业表

            public PLtMeterStruct Meter;		// 卡表回写信息        
            public fixed byte DllName[260];			// 保留
            public int ErrIgnoringLevel; 		// 保留


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

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
            PLtCardReader pcr1 = new PLtCardReader()
            {
                dwDevType = 2,
                dwPort = com+1,
                dwBaud = baud,
                dv_beep = 0
            };

            PUserCard puc1 = new PUserCard();
            int pti = 0;
            //string kh1 = new string((sbyte*)puc1.CardNO);
            //string dqdm1 = new string((sbyte*)puc1.UserNO);


            String buf;
            String buf1;
            Log.Debug("LanBaoShi Clear Card start");
            int rs = RecycleUserCard(ref pcr1, ref puc1);
            Log.Debug("LanBaoShi Clear Card end and i is "+rs);
          
            return rs;
        }
     
       public unsafe  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){

                 int rs = -1;
            try
            {
                PLtCardReader pcr1 = new PLtCardReader()
                {
                    dwDevType = 2,
                    dwPort = com+1,
                    dwBaud = baud,
                    dv_beep = 0
                };

                PUserCard puc1 = new PUserCard();
                //string kh = new string((sbyte*)puc1.CardNO);
                //string dqdm = new string((sbyte*)puc1.UserNO);
                int istrue = 0;
                
                Log.Debug("LanBaoShi CheckGasCard start;端口号：" + pcr1.dwPort + "波特率：" + pcr1.dwBaud);
                
                rs = CheckLibrary(ref pcr1, ref puc1, ref istrue);
                
                //string kh = new string((sbyte*)puc1.CardNO);
                //string dqdm = new string((sbyte*)puc1.UserNO);
                Log.Debug("LanBaoShi CheckGasCard end,返回值return:" + rs + " istrue：" + istrue);
                
                //是蓝宝石卡
                if (istrue == 1)
                {
                    int pti = 0;
                    Log.Debug("LanBaoShi check ReadGasCard start。");
                    
               //     rs = ReadUserCard(ref pcr1, ref puc1, pti);
                    
              
                    rs = ReadUserCard(ref pcr1, ref puc1);
                    string kh = new string((sbyte*)puc1.CardNO);
                    string dqdm = new string((sbyte*)puc1.UserNO);
                    Log.Debug("LanBaoShi check ReadGasCard end,返回值return:" + rs + "卡号：" + kh + "区域号：" + dqdm + "气量：" + puc1.ChargingAmount + "购气次数：" + puc1.ChargingTimes + "卡类型" + puc1.BigMacType);
                    if (kh.Substring(0, 4).Equals("0000") | kh.Substring(0, 4).Equals("8000") | kh.Substring(0, 4).Equals("0200"))
                    {
                        //是蓝宝石民用卡
                        if (puc1.BigMacType == 0)
                        {
                            Log.Debug("此卡是蓝宝石民用卡！");
                            return 0;
                        }
                        Log.Debug("此卡不是蓝宝石民用卡！");
                        return -1;
                    }
                    else {
                        Log.Debug("此卡不是蓝宝石卡");
                        return -1;
                    }
                }
                Log.Debug("此卡不是蓝宝石卡！");
                return -1;
            }
            catch (Exception e)
            {
                Log.Debug("蓝宝石民用判卡异常：" + e.Message + "--" + e.StackTrace);
            }
            return rs;
        
       }
        public unsafe int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
           PLtCardReader pcr1 = new PLtCardReader()
            {
                dwDevType = 2,
                dwPort = com+1,
                dwBaud = baud,
                dv_beep = 0
            };

            PUserCard puc1 = new PUserCard();

            int pti = 0;
            //string kh4 = new string((sbyte*)puc1.CardNO);
            //string dqdm4 = new string((sbyte*)puc1.UserNO)

            Log.Debug("LanBaoShi ReadGasCard Start");
          int  rs = ReadUserCard(ref pcr1, ref puc1);
              Log.Debug("LanBaoShi ReadGasCard end and i is "+rs);
             cs = (short)puc1.ChargingTimes;
             ql = (int)puc1.ChargingAmount;
             dm = new string((sbyte*)puc1.UserNO);
             kh = new string((sbyte*)puc1.CardNO);
            return rs;
}

        public unsafe int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int rs = -1;

            PLtCardReader pcr1 = new PLtCardReader()
            {
                dwDevType = 2,
                dwPort = com+1,
                dwBaud = baud,
                dv_beep = 0
            };
            PLtMeterStruct pms1 = new PLtMeterStruct()
            {

                WriteBack = 0,
                Total = 0,
                TotalMoney = 0,
                Remain = 0,

                ChargingTimes = 0,
                OverflowTimes = 0,
                MagnetismInterferedTimes = 0,
                Valve = 0,
                Battery = 0,
                State1 = 0,			// 保留
                State2 = 0,
                State3 = 0,
                dwReserved = 0,
                dblReserved = 0
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
                ChargingTimes = cs, 
                BigMacType = 0
            };
            //给卡号、用户号赋值
            FillBytes(puc1.CardNO, kh);
            //    FillBytes(puc1.UserNO, dqdm);
            //正常购气
            //气量

            Log.Debug("lanBaoshi writegascard start  and  cs  is"+cs+" + and ql is "+ql);
            if (ql > 0)
            {
                puc1.ChargingAmount = ql;
                rs = ChargeUserCard(ref pcr1, ref puc1);
            }
            else {

                puc1.ChargingAmount = ql;
                rs = ChargeUserCard(ref pcr1, ref puc1);
            }
            Log.Debug("LANBaoshi writegascard end and i is " + rs);
            return rs;
        }

        public unsafe int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            int rs = -1;

            PLtCardReader pcr1 = new PLtCardReader()
            {
                dwDevType = 2,
                dwPort = com+1,
                dwBaud = baud,
                dv_beep = 0
            };
            PLtMeterStruct pms1 = new PLtMeterStruct()
            {

                WriteBack = 0,
                Total = 0,
                TotalMoney = 0,
                Remain = 0,

                ChargingTimes = 0,
                OverflowTimes = 0,
                MagnetismInterferedTimes = 0,
                Valve = 0,
                Battery = 0,
                State1 = 0,			// 保留
                State2 = 0,
                State3 = 0,
                dwReserved = 0,
                dblReserved = 0
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
                ChargingAmount = ql,
                BigMacType = 0
            };
            puc1.ChargingTimes = cs;
            FillBytes(puc1.CardNO, kh);
          //写新卡前先清卡
            rs = RecycleUserCard(ref pcr1, ref puc1);
            Log.Debug("Lanbaoshi clear card end and rs is " + rs);
             rs = IssueUserCard(ref pcr1, ref puc1);
            Log.Debug("LanBaoshi WriteNewCard end and rs is " + rs);
            return rs;

        }
        /// <summary>
        /// 航天卡实现，其他不用
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int OpenCard(Int16 com, Int32 baud)
        {
            throw new NotImplementedException();
        }
        public string Name
        {
            get
            {
                return "LanBaoShi";
            }
        }

        #endregion
    }
}
