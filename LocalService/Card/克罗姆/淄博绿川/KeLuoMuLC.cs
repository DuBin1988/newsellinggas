using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.Aote.Logs;
using System.Runtime.InteropServices;

namespace Card
{
    public class KeLuoMu : ICard
    {
        //引入动态库中标准读卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 ReadGasCard(Int16 com, byte[] kh, byte[] yhh, ref Int32 ql, ref Int32 cs, ref Int32 ljgql, ref Int32 syql, ref Int32 klx);
        //引入动态库中标准发卡，补卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "MakeGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeGasCard(Int16 com, byte[] cardNo, byte[] customeNo, Int16 orderNum, Int32 orderamount);
        //引入动态库中标准清卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, byte[] customeNo);
        //引入动态库中标准判卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, byte[] Istrue);
        //引入动态库中标准买气函数接口
        [DllImport("qw_card.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 StaticWriteGasCard(Int16 com, byte[] cardNo, byte[] customeNo, Int32 orderNum, Int32 orderamount);
        //引入动态库中标准检测函数接口
        [DllImport("qw_card.dll", EntryPoint = "rd_Company", CallingConvention = CallingConvention.StdCall)]
        public static extern int Staticrd_Company(Int16 com, byte[] Istrue);
        public static Log Log = Log.GetInstance("Card.KeLuoMu");
        public string Test()
        {
            Log.Debug("KeLuoMu start work!");
            return "KeLuoMu";
        }
        #region 卡操作
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs)
        {
            Int32 ljgql = 0; //累计购气量
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[100];
            byte[] customeNo = new byte[100];
            Log.Debug("read keluomu card start!");
            Int16 result = ReadGasCard(com, cardid, customeNo, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            ql = orderamount;
            cs = (short)orderNum;
            string yhh = "";
            //读取卡号
            int pos = -1;
            for (int i = 0; i < cardid.Length; i++)
            {
                if (cardid[i] == 0)
                {
                    pos = i;
                    break;
                }
            }
            kh = Encoding.ASCII.GetString(cardid, 0, pos);
            //读取用户号
            int index = -1;
            for (int i = 0; i < customeNo.Length; i++)
            {
                if (customeNo[i] == 0)
                {
                    index = i;
                    break;
                }
            }
            yhh = Encoding.ASCII.GetString(customeNo, 0, index);
            Log.Debug("read card end!yhh is:" + yhh);
            Log.Debug("read card end!kh is:" + kh);
            Log.Debug("read card end!result is:" + result);

            return result;
        }
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh,  string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] yh = System.Text.Encoding.GetEncoding(1252).GetBytes(yhh);
            int result;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("clear keluomu card start, is:!" + cs);
            int clearresult = StaticFormatGasCard(com, yh);
            Log.Debug("clead keluomu card end!" + clearresult);
            result = MakeGasCard(com, cardNo, yh, cs, ql);
            Log.Debug("repair keluomu card end!");
            return result;
        }
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read keluomu card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read keluomu card end!" + readresult);
            byte[] customeNo = customeid;
            int result = StaticWriteGasCard(com, cardNo, customeNo, cs, ql);

            Log.Debug("write gas card result=" + result);
            return result;

        }
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 ljgql = 0;
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read keluomu card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read keluomu card end!" + readresult);
            byte[] customeNo = customeid;
            int result = StaticFormatGasCard(com, customeNo);
            return result;
        }
        public int CheckGasCard(Int16 com, Int32 baud)
        {
            byte[] Istrue = new byte[10];
            Log.Debug("check keluomu start:");
            int result = Staticrd_Company(com, Istrue);
            Log.Debug("check keluomu end:");
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 ljgql = 0;
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read keluomu card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read keluomu card end!" + readresult);
            if (cardid[0] == 0)
            {
                return -1;
            }
            else
            {
                string kh = Encoding.ASCII.GetString(Istrue, 0, 3);
                int istrue = int.Parse(kh);
                return istrue;
            }

        }
        public string Name
        {
            get
            {
                return "keluomu";
            }
        }
        #endregion
    }
}
