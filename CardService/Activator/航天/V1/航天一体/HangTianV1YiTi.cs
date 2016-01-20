using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class HangTianV1YiTi : ICard
    {
        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);


        //测试厂家
        [DllImport("ICControlDLL.dll", EntryPoint = "CheckFactory", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticCheckFactory(Int32 com, Int32 baut);
        //发卡
        [DllImport("ICControlDLL.dll", EntryPoint = "InitCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticInitCard(Int32 com, Int32 baut, byte[] cardno, double gas,
            double totalgas, Int32 buytimes, Int32 cardtype, Int32 metertype, byte[] password);
        //读卡
        [DllImport("ICControlDLL.dll", EntryPoint = "ReadCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticReadCard(Int32 com, Int32 baut, byte[] cardno, ref double gas,
            ref Int32 buytimes, ref double totalgas, ref Int32 metertype, byte[] password);
        //购气
        [DllImport("ICControlDLL.dll", EntryPoint = "SellGas", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticSellGas(Int32 com, Int32 baut, byte[] cardno, double gas,
            double totalgas, Int32 buytimes, Int32 metertype, byte[] password);
        //清卡
        [DllImport("ICControlDLL.dll", EntryPoint = "NewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticNewCard(Int32 com, Int32 baut);
        //解锁
        [DllImport("ICControlDLL.dll", EntryPoint = "OpenCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 StaticOpenCard(Int32 com, Int32 baut);


        private static Log Log = Log.GetInstance("Card.HangTianV1YiTi");

        public string Test()
        {
            Log.Debug("read gas card meter================================");
            return "ruisen";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            Log.Debug("read gas card meter=");
            //航天必须调用一次明华的打开、关闭串口，才能正常
            int icdev = ic_init(0, 9600);
            ic_exit(icdev);

            //调用判卡，抛异常认为非本厂卡
            try
            {
                Int32 ret;
                ret = StaticCheckFactory(com, baud);
                //不是航天卡
                if (ret != 0x100)
                {
                    return -1;
                }
                //读卡确定是否一体表
                byte[] cardno = new byte[100];
                double gas = 0;
                Int32 buytimes = 0;
                double totalgas = 0;
                Int32 metertye = 0;
                ret = StaticReadCard(com, baud, cardno, ref gas,
                    ref buytimes, ref totalgas, ref metertye, null);
                //0 --- 用户卡；1 --- 开户卡；3 --- 购气卡
                if(ret != 0 && ret != 1 && ret != 3)
                {
                    return -1;
                }
                //1：一体表；2 ； 分体表 ，3：工业表
                Log.Debug("   == "+metertye);
                if(metertye == 2)
                {
                    return -1;
                }
                return 0;
            }
            catch (COMException e)
            {
                return -1;
            }
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            //航天必须调用一次明华的打开、关闭串口，才能正常
            int icdev = ic_init(0, 9600);
            ic_exit(icdev);

            int result = StaticNewCard(com, baud); 
            return result;
        }

        public int OpenCard(short com, int baud)
        {
            //航天必须调用一次明华的打开、关闭串口，才能正常
            int icdev = ic_init(com, baud);
            ic_exit(icdev);

            int result = StaticOpenCard(com, baud);
            Log.Debug("Open Card result is:"+result);
            return result;
        }


        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            //航天必须调用一次明华的打开、关闭串口，才能正常
            int icdev = ic_init(com, baud);
            ic_exit(icdev);

            Int32 ret;

            //读卡
            byte[] cardno = new byte[100];
            double gas = 0;
            Int32 buytimes = 0;
            double totalgas = 0;
            Int32 metertye = 0;
            ret = StaticReadCard(com, baud, cardno, ref gas,
                ref buytimes, ref totalgas, ref metertye, null);
            Log.Debug("读卡结果："+ret);
            //卡号
            int pos = -1;
            for (int i = 0; i < cardno.Length; i++)
            {
                if (cardno[i] == 0)
                {
                    pos = i;
                    break;
                }
            }
            kh = Encoding.ASCII.GetString(cardno, 0, pos);
            //气量
            ql = (int)gas;
            //次数
            cs = (short)buytimes;
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //航天必须调用一次明华的打开、关闭串口，才能正常
            int icdev = ic_init(0, 9600);
            ic_exit(icdev);

            //调用购气
	        int result = 0;
            int re = 0;
	        if(ql != 0)
	        {
                byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                result = StaticSellGas(com, baud,
                    cardno, ql,
                    0, cs, 1, null);
                Log.Debug("sellgas  result=" + re);
	        }
	        //退气
	        else
	        {
                byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                Log.Debug("return gas start kh=" + kh + ", ql=" + 0 + ", cs=" + cs);
                result = StaticSellGas(com, baud,
                    cardno, 0,
                    0, cs, 1, null);
                Log.Debug("return gas end rs=" + result);
            }
	        Log.Debug("write gas card result=" + result);
            return result;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //清卡，新卡，卡上有内容
            MingHua.ClearCard(com, baud);
            //用户卡清卡
            StaticNewCard(com, baud);

            //卡号
            byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            int result;
            //发卡
	        if(kzt==0)
	        {
		        //调用发卡
                result = StaticInitCard(com, baud,
                    cardno, ql, 0, cs, 
                    1,  //初始化卡类型（1：发卡；3：补购气卡；0：补用户卡）
                    1,  //表型（1：一体表；2 ； 分体表 ，3：工业表）
                    null);
	        }
	        else
	        {
		        //调用补卡
                result = StaticInitCard(com, baud,
                    cardno, ql, 0, cs,
                    2,  //初始化卡类型（1：发卡；2：补购气卡；0：补用户卡）
                    1,  //表型（1：一体表；2 ； 分体表 ，3：工业表）
                    null);
            }
            Log.Debug("write new card end ret=" + result);
            return result;
        }

        public string Name
        {
            get
            {
                return "HangTianV1YiTi";
            }
        }

        #endregion
    }
}
