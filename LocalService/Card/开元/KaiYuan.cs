using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class KaiYuan : ICard
    {
        private static Log Log = Log.GetInstance("Card.KaiYuan");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //读卡函数
        [DllImport("RD660.dll", EntryPoint = "ReadIcard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ReadIcard(Int32 com, Int32 baud, Int32 data);

        //写卡函数
        [DllImport("RD660.dll", EntryPoint = "WriteIcard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 WriteIcard(Int32 com, Int32 baud, Int32 data, Int32 IcNum, Int32 GasNum, Int32 IcardCont, Int32 IcardType);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            //读公司代码
            long ret = ReadIcard(com, baud, 3);
            if (ret != 37729)
	        {
		        return -1;
	        }
	        return 0;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
		    int ret = (int)WriteIcard(com, baud, 4, 0, 0, 125, 70);
            return ret;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
            //读气量
            long ret = ReadIcard(com, baud, 1);
            if (ret == 2 || ret == 4)
	        {
                ql = 0;
	        }
            else
	        {
                ql = (int)ReadIcard(com, baud, 2);
	        }
            //读卡号
            long CardNo = ReadIcard(com, baud, 0);
            if (CardNo < 0)
            {
                return -1;
            }
            kh = CardNo.ToString();
            return 0;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret;
            if (ql != 0)
            {
                ret = (int)WriteIcard(com, baud, 1, Int32.Parse(kh), (Int32)ql, 125, 70);
            }
            else
            {
                ret = (int)WriteIcard(com, baud, 3, Int32.Parse(kh), (Int32)ql, 125, 70);
            }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret;
            //发卡前先清卡
            WriteIcard(com, baud, 4, 0, 0, 125, 70);
            ret = (int)WriteIcard(com, baud, 0, Int32.Parse(kh), (Int32)ql, 125, 70);
            return ret;
        }

        public string Name
        {
            get
            {
                return "dandongminyong";
            }
        }

        #endregion
    }
}
