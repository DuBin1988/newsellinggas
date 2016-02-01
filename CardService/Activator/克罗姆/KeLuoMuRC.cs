using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.Aote.Logs;
using System.Runtime.InteropServices;

namespace Card
{
    public class KeLuoMuRC : ICard
    {
        //引入动态库中标准读卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ReadGasCard(Int16 com, byte[] kh, byte[] yhh, ref Int32 ql, ref Int32 cs, ref Int32 ljgql, ref Int32 syql, ref Int32 klx);
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
        public static Log Log = Log.GetInstance("Card.KeLuoMuRC");
        public string Test()
        {
            Log.Debug("KeLuoMuRC start work!");
            return "KeLuoMuRC";
        }
        #region 卡操作
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
            Int32 ljgql = 0; //累计购气量
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[100];
            byte[] customeNo = new byte[100];
            Log.Debug("read KeLuoMuRC card start!");
            Int32 result = ReadGasCard(com, cardid, customeNo, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
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
            result = ChangeResult(result);
            return result;
        }
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] yh = System.Text.Encoding.GetEncoding(1252).GetBytes(yhh);
            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            int result = -1;
            Log.Debug("clear KeLuoMuRC card start, is:!" + yhh);
            int clearresult = StaticFormatGasCard(com, yh);
            Log.Debug("clead KeLuoMuRC card end!" + clearresult);
            if (0 == kzt)
            {
                Log.Debug("make new card start:com:" + com + "--cardno:" + kh + "--yhh:" + yhh + "--ql:" + ql);
                result = MakeGasCard(com, cardNo, yh, 1, ql);
                Log.Debug("make new KeLuoMuRC card end!" + result);
            }
            else if (1 == kzt)
            {
                Log.Debug("repair new card start:com:" + com + "--cardno:" + kh + "--yhh:" + yhh + "--cs" + cs + "--ql:" + ql);
                result = MakeGasCard(com, cardNo, yh, cs, ql);
                Log.Debug("repair KeLuoMuRC card end!" + result);
            }

            String buf;
            Log.Debug("发卡后读卡格式:");
            MingHua.GetSnapShot(com, baud, out buf);
            Log.Debug(buf);
            result = ChangeResult(result);
            return result;
        }
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            String buf;
            Log.Debug("买气前读卡格式:");
            MingHua.GetSnapShot(com, baud, out buf);

            byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read KeLuoMuRC card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read KeLuoMuRC card end!" + readresult);
            byte[] customeNo = customeid;
            string khq = Encoding.ASCII.GetString(cardNo, 0, 8);
            string yhhq = Encoding.ASCII.GetString(customeNo, 0, 10);
            Log.Debug("sell gas start:" + "kh:" + khq + "--yhh:" + yhhq + "--cs:" + cs + "--ql:" + ql);
            int result = StaticWriteGasCard(com, cardNo, customeNo, cs, ql);

            Log.Debug("write gas card result=" + result);
            Log.Debug("买气后读卡格式:");
            String buf2;
            MingHua.GetSnapShot(com, baud, out buf2);
            Log.Debug(buf);
            Log.Debug(buf2);
            result = ChangeResult(result);
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
            Log.Debug("read KeLuoMuRC card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read KeLuoMuRC card end!" + readresult);
            byte[] customeNo = customeid;
            string yhhq = Encoding.ASCII.GetString(customeNo, 0, 10);
            Log.Debug("clear KeLuoMuRC card start!" + yhhq);
            int result = StaticFormatGasCard(com, customeNo);
            result = ChangeResult(result);
            return result;
        }
        public int CheckGasCard(Int16 com, Int32 baud)
        {
            byte[] Istrue = new byte[10];
            Log.Debug("check KeLuoMuRC start:");
            int result = Staticrd_Company(com, Istrue);
            Log.Debug("check KeLuoMuRC end:");
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 ljgql = 0;
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read KeLuoMuRC card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read KeLuoMuRC card end!" + readresult);
            if (cardid[0] == 0)
            {
                return -1;
            }
            else
            {
                string kh = Encoding.ASCII.GetString(Istrue, 0, 3);
                int istrue = int.Parse(kh);
                istrue = ChangeResult(istrue);
                return istrue;
            }

        }
        public string Name
        {
            get
            {
                return "KeLuoMuRC";
            }
        }

        public Int32 ChangeResult(Int32 ret)
        {
            if (-128 == ret) ret = -7;
            if (-129 == ret) ret = -10;
            if (-130 == ret) ret = -9;
            if (-134 == ret) ret = -4;
            if (-136 == ret) ret = -3;
            if (-137 == ret) ret = -7;
            if (-138 == ret) ret = -3;
            if (-145 == ret) ret = -20;
            if (-160 == ret) ret = -15;
            if (-161 == ret) ret = -16;
            if (-162 == ret) ret = -6;
            if (-163 == ret) ret = -11;
            if (-170 == ret) ret = -6;
            return ret;
        }
        #endregion
    }
}
