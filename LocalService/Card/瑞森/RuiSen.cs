using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace Card
{
    public class RuiSen : ICard
    {
        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //打开卡
        [DllImport("TJmh.dll", EntryPoint = "OpenIC", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 OpenIC(Int16 com);

        //关闭卡
        [DllImport("TJmh.dll", EntryPoint = "CloseIC", CallingConvention = CallingConvention.StdCall)]
        public static extern bool CloseIC();

        //格式化卡
        [DllImport("TJmh.dll", EntryPoint = "FormatCard", CallingConvention = CallingConvention.StdCall)]
        public static extern bool FormatCard();

        //初始化卡
        [DllImport("TJmh.dll", EntryPoint = "InitInduCard", CallingConvention = CallingConvention.StdCall)]
        public static extern bool InitInduCard(byte[] CardNo, Int32 GasV, Int32 BuyTimes, Int16 CardT);

        //购气
        [DllImport("TJmh.dll", EntryPoint = "SellInduGas", CallingConvention = CallingConvention.StdCall)]
        public static extern bool SellInduGas(byte[] CardNo, Int32 GasV, Int32 BuyTimes);

        //读卡
        [DllImport("TJmh.dll", EntryPoint = "ReadInduCardInfo", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadInduCardInfo(byte[] CardNo, ref Int32 GasV, ref Int32 BuyTimes);

        //退气
        [DllImport("TJmh.dll", EntryPoint = "ReadInduCardInfo", CallingConvention = CallingConvention.StdCall)]
        public static extern bool GasRollBack();

        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
	        //打开串口
	        int ret = OpenIC(com);
            if (ret == 6 || ret == 10)
	        {
		        CloseIC();
		        return 0;
	        }
	        CloseIC();
	        return -1;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
	        //打开串口
	        int result = OpenIC(com);
            if (result < 0)
            {
                return -1;
            }
	        bool ret = FormatCard();
            if (!ret)
            {
                CloseIC();
                return -1;
            }
	        CloseIC();
	        return 0;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
	        int result = OpenIC(com);
	        if (result<0)
	        {
		        return -1;
	        }
	        Int32 Gasv = 0;
	        Int32 Times = 0;
            byte[] cardNO = new byte[100];
	        result = ReadInduCardInfo(cardNO, ref Gasv, ref Times);
            ql = Gasv / 10;
            cs = (short)Times;
            if (result != 6)
            {
                CloseIC();
                return -1;
            }
	        CloseIC();
            //卡号转换成字符串
            cardNO[8] = 10;
            kh = Encoding.ASCII.GetString(cardNO, 0, 10);
	        return 0;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //打开串口
            int result = OpenIC(com);
	        if (result <0)
	        {
		        return -1;
	        }
            //卡号转换
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
	        bool ret;
            if (ql != 0)
            {
                ret = SellInduGas(cardNO, ql * 10, cs);
            }
            else
            {
                ret = GasRollBack();
            }
	        //返回
	        if(!ret)
	        {
		        CloseIC();
		        return -1;
	        }
	        CloseIC();
	        return 0;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
	        int result = OpenIC(com);
	        if (result < 0)
	        {
		        return -1;
	        }
            //卡号转换
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
	        bool ret;
            if (kzt == 0)
            {
                ret = InitInduCard(cardNO, ql * 10, cs, 0);
            }
            else
            {
                ret = InitInduCard(cardNO, ql, cs, 1);
            }
            if (!ret)
            {
                CloseIC();
                return -1;
            }
	        CloseIC();
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
