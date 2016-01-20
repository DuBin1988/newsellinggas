using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace Card
{
    public class RuiSenMinYong : ICard
    {
        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //打开卡
        [DllImport("TJmhmy.dll", EntryPoint = "_OpenIC@12", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 OpenRSIC(Int16 com, Int16 FacID, string CityCode);

        //关闭卡
        [DllImport("TJmhmy.dll", EntryPoint = "_CloseIC@0", CallingConvention = CallingConvention.StdCall)]
        public static extern bool CloseRSIC();

        //格式化卡
        [DllImport("TJmhmy.dll", EntryPoint = "_FormatCard@0", CallingConvention = CallingConvention.StdCall)]
        public static extern bool FormatRSCard();

        //初始化卡
        [DllImport("TJmhmy.dll", EntryPoint = "_InitCard@36", CallingConvention = CallingConvention.StdCall)]
        public static extern bool InitRSCard(string CardNo, Int32 GasV, Int32 BuyTimes, Int16 GasPrice, 
            string CityCode, Int16 FacID, Int16 CardT, ref Int16 UserInfo, Int16 UserTypeID);

        //购气
        [DllImport("TJmhmy.dll", EntryPoint = "_SellGas@24", CallingConvention = CallingConvention.StdCall)]
        public static extern bool SellRSGas(string CardNo, Int32 GasV, Int16 GasPrice, Int16 BuyTimes,
            ref Int16 UserInfo, Int16 userTyId);

        //读卡号
        [DllImport("TJmhmy.dll", EntryPoint = "_GetCardNum@0", CallingConvention = CallingConvention.StdCall)]
        public static extern string GetRSCardNum();

        //读气量
        [DllImport("TJmhmy.dll", EntryPoint = "_GetRemaingas@0", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 GetRSRemaingas();

        //读次数
        [DllImport("TJmhmy.dll", EntryPoint = "_GetBuyTimes@0", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 GetRSBuyTimes();

        //退气
        [DllImport("TJmhmy.dll", EntryPoint = "_GasRollBack@4", CallingConvention = CallingConvention.StdCall)]
        public static extern bool GasRSRollBack(Int16 RollSign);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            //中奕
            long renVal = OpenRSIC(com, 3, "1425");
            if (renVal == 1 || renVal == 9)
            {
                CloseRSIC();
                return 0;
            }
            CloseRSIC();
            return -1;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            OpenRSIC(com, 3, "1425");
            bool renVal = FormatRSCard();
            CloseRSIC();
            return renVal ? 0 : -1;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
            OpenRSIC(com, 3, "1425");
            Int32 Gas = GetRSRemaingas();
            ql = Gas / 100;
            cs = GetRSBuyTimes();
            kh = GetRSCardNum();
            CloseRSIC();
            return 0;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //打开串口
            OpenRSIC(com, 3, "1425");
            short userInfo = 0;
	        bool ret;
            if (ql != 0)
            {
                ret = SellRSGas(kh, ql * 100, 0, cs, ref userInfo, 0);
            }
            else
            {
                ret = GasRSRollBack(0);
            }
	        //返回
            if (!ret)
            {
                CloseRSIC();
                return -1;
            }
            CloseRSIC();
	        return 0;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            OpenRSIC(com, 3, "1425");
            //制新卡前先清卡
            FormatRSCard();
            short userInfo = 0;
            bool ret;
            if (kzt == 0)
            {
                ret = InitRSCard(kh, ql * 100, cs, 0, "1425", 3, 0, ref userInfo, 0);
            }
            else
            {
                ret = InitRSCard(kh, ql * 100, cs, 0, "1425", 3, 1, ref userInfo, 0);
            }
            if (!ret)
            {
                CloseRSIC();
                return -1;
            }
            CloseRSIC();
	        return 0;
        }

        public string Name
        {
            get
            {
                return "ruisen";
            }
        }

        #endregion
    }
}
