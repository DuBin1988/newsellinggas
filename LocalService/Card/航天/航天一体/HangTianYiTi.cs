using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class HangTianYiTi : ICard
    {
        private static Log Log = Log.GetInstance("Card.HangTianYiTi");

        public string Test()
        {
            return "ruisen";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            HtDll.HTDL card = new HtDll.HTDL();

            //调用判卡，抛异常认为非本厂卡
            try
            {
                string meter = card.ReadMeter(com, baud);
                if (meter == "-1")
                {
                    return 0;
                }
                return -1;
            }
            catch (COMException e)
            {
                return -1;
            }
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            HtDll.HTDL card = new HtDll.HTDL();
            int result = card.ClearCard(com, baud); 
            return result;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
            HtDll.HTDL card = new HtDll.HTDL();
            //读卡号
            kh = card.ReadIC(com, baud);
            //读状态
            string state = card.ReadCard(com, baud, 1);
            state = state.Substring(39, 2);
            if (state == "00")
            {
                ql = 0;
            }
            else
            {
                //读气量
                string gas = card.ReadGas(com, baud);
                ql = int.Parse(gas.Substring(2));
            }
            //读次数
            string times = card.ReadAmountTimes(com, baud);
            cs = short.Parse(times);
            return 0;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            HtDll.HTDL card = new HtDll.HTDL();
            //调用购气
	        int result = 0;
	        if(ql != 0)
	        {
		        result = card.BuyGasM(com, baud, ql, 1, bjql, 1);
	        }
	        //退气
	        else
	        {
                result = card.BuyGasM(com, baud, ql, 1, bjql, 3);
	        }
	        Log.Debug("write gas card result=" + result);
            return result;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //清卡，新卡，卡上有内容
            MingHua.ClearCard(com, baud);

            HtDll.HTDL card = new HtDll.HTDL();
            //用户卡清卡
            card.ClearCard(com, baud);

            int result;
            //发卡
	        if(kzt==0)
	        {
		        //调用发卡
		        result = card.NewCard(com, baud, 1, ql, kh, bjql); 
	        }
	        else
	        {
		        //调用补卡
                result = card.CardInit(com, baud, 1, ql, kh, bjql, 5, "20131008", 0, cs);
	        }
            Log.Debug("write card end ret=" + result);
            return result;
        }

        public string Name
        {
            get
            {
                return "hangtianyiti";
            }
        }

        #endregion
    }
}
