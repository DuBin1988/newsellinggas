using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class XianFengBYNE : ICard
    {
        private static Log Log = Log.GetInstance("Card.XianFengBYNE");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //读卡，标准接口
        [DllImport("WRwCard.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadGasCard(Int16 com, Int32 baut, byte[] kmm, ref Int16 klx, ref Int16 kzt,  
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, ref Int32 ql, ref Int16 cs, ref Int32 ljgql, ref Int16 bkcs, 
            ref Int32 ljyql, ref Int32 syql, ref Int32 bjql, ref Int32 czsx, ref Int32 tzed,byte[] sqrq ,ref Int32 oldprice, 
            ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        //写新卡，标准接口
        [DllImport("WRwCard.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteNewCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, Int32 ql, Int16 cs, Int32 ljgql, Int16 bkcs, Int32 ljyql,
            Int32 bjql, Int32 czsx, Int32 tzed, byte[] sqrq, Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);
        //测卡，标准接口
        [DllImport("WRwCard.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, Int32 baut);
        //格式化卡，标准接口
        [DllImport("WRwCard.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, byte[] dqdm);
        //写购气卡，标准接口
        [DllImport("WRwCard.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, 
            byte[] dqdm, Int32 ql, Int16 cs, Int32 ljgql, Int32 bjql, Int32 czsx, Int32 tzed,
            byte[] sqrq ,Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            return StaticCheckGasCard(com, baud);
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            byte[] mm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            return StaticFormatGasCard(com, baud, mm, 0, cardNO, bdqdm);
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
            byte[] cardNO = new byte[9]{0, 0, 0, 0, 0, 0, 0, 0, 0};
            byte[] kmm = new byte[2]{0, 0};
            byte[] dqdm = new byte[2]{0, 0};
            byte[] yhh = new byte[2]{0, 0};
            byte[] sqrq = new byte[2]{0, 0};
            byte[] sxrq = new byte[2]{0, 0};
            byte[] sxbj = new byte[2]{0, 0};
            byte[] tm = new byte[2]{0, 0};
            short klx = 0;
            short kzt = 0;
            int ljgql = 0;
            int ljyql = 0;
            int syql = 0;
            int bjql = 0;
            int czsx = 0;
            int tzed = 0;
            int oldprice = 0;
            int newprice = 0;
            int ret = StaticReadGasCard(0, baud, kmm, ref klx, ref kzt, cardNO, dqdm, yhh, tm, 
                ref ql, ref cs, ref ljgql, ref bkcs, ref ljyql, ref syql, ref bjql, ref czsx, 
                ref tzed, sqrq, ref oldprice, ref newprice, sxrq, sxbj);
            //卡号转换成字符串
            cardNO[8] = 0;
            kh = Encoding.ASCII.GetString(cardNO, 0, 8);
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] byhh = new byte[10];
            byte[] btm = new byte[8];
            byte[] bsqrq = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bsxrq = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bsxbj = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            int ret = StaticWriteGasCard(com, baud, mm, 0, cardNO, bdqdm, ql, 
                cs, ljgql, bjql, czsx, tzed, bsqrq, 0, 0, bsxrq, bsxbj);
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] byhh = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] btm = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bsqrq = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bsxrq = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            byte[] bsxbj = System.Text.Encoding.GetEncoding(1252).GetBytes("");
            Log.Debug("start write new card");
            //发卡前先格式化卡
            int ret = StaticFormatGasCard(com, baud, mm, 0, cardNO, bdqdm);
            Log.Debug("format card end ret=" + ret);
            ret = StaticWriteNewCard(com, baud, mm, 1, kzt, cardNO, bdqdm, byhh, btm, 
                ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, 0, 0, bsxrq, bsxbj);
            Log.Debug("write card end ret=" + ret);
            return ret;
        }

        public string Name
        {
            get
            {
                return "XianFengBYNE";
            }
        }

        #endregion
    }
}
