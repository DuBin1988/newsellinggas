using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class XinAo4442 : ICard
    {
        private static Log Log = Log.GetInstance("Card.XinAo4442");

        public string Test()
        {
            return "ruisen";
        }

        #region 新奥动态库导入
        //读卡，标准接口
        [DllImport("xinao.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadGasCard(Int16 com, Int32 baut,byte[] kmm, ref Int16 klx, ref Int16 kzt,  
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, ref Int32 ql, ref Int16 cs, ref Int32 ljgql, ref Int16 bkcs, 
            ref Int32 ljyql, ref Int32 syql, ref Int32 bjql, ref Int32 czsx, ref Int32 tzed,byte[] sqrq , Int32 oldprice, 
            Int32 newprice, byte[] sxrq, byte[] sxbj);
        //写新卡，标准接口
        [DllImport("xinao.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteNewCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, Int32 ql, Int16 cs, Int32 ljgql, Int16 bkcs, Int32 ljyql,
            Int32 bjql, Int32 czsx, Int32 tzed, byte[] sqrq, Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);
        //测卡，标准接口
        [DllImport("xinao.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, Int32 baut);
        //格式化卡，标准接口
        [DllImport("xinao.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, byte[] dqdm);
        //写购气卡，标准接口
        [DllImport("xinao.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, 
            byte[] dqdm, byte[] yhh, Int32 ql, Int16 cs, Int32 ljgql, Int32 bjql, Int32 czsx, Int32 tzed,
            byte[] sqrq ,Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int ret = StaticCheckGasCard(com, baud);
            if(ret != 0)
            {
                return ret;
            }
            //通过读卡看是否4442卡
            ret = MingHua.Is4442(com, baud);
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = new byte[4];
            return StaticFormatGasCard(com, baud, mm, 1, cardNO, bdqdm);
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
            byte[] cardNO = new byte[100];
            byte[] kmm = new byte[100];
            byte[] dqdm = new byte[100];
            byte[] yhh = new byte[100];
            byte[] sqrq = new byte[100];
            byte[] sxrq = new byte[100];
            byte[] sxbj = new byte[100];
            byte[] tm = new byte[100];
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
                ref tzed, sqrq, oldprice, newprice, sxrq, sxbj);
            //卡号转换成字符串
            cardNO[8] = 0;
            kh = Encoding.ASCII.GetString(cardNO, 0, 8);
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = new byte[4];
            byte[] byhh = new byte[10];
            byte[] btm = new byte[10];
            byte[] bsqrq = new byte[8];
            byte[] bsxrq = new byte[8];
            byte[] bsxbj = new byte[1];

            int ret = StaticWriteGasCard(com, baud, mm, 1, cardNO, bdqdm, byhh, ql, 
                cs, ljgql, bjql, czsx, tzed, bsqrq, oldprice, newprice, bsxrq, bsxbj);
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = new byte[4];
            byte[] byhh = new byte[10];
            byte[] btm = new byte[10];
            byte[] bsqrq = new byte[8];
            byte[] bsxrq = new byte[8];
            byte[] bsxbj = new byte[1];
            Log.Debug("start write new card");
            //清卡时需要读卡号
            string ckh = "";
            int cql = 0; ;
            decimal cmoney = 0;
            short ccs = 0;
            short cbkcs = 0;
            string dm = "";
            int ret = ReadGasCard(com, baud, ref ckh, ref cql, ref cmoney, ref ccs, ref cbkcs,ref dm);
            byte[] bckh = System.Text.Encoding.GetEncoding(1252).GetBytes(ckh);
            //发卡前先格式化卡
            if (ret == 0)
            {
                ret = StaticFormatGasCard(com, baud, mm, 1, bckh, bdqdm);
                Log.Debug("format card end ret=" + ret);
            }
            ret = StaticWriteNewCard(com, baud, mm, 1, 0, cardNO, bdqdm, byhh, btm, 
                ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, 0, 0, bsxrq, bsxbj);
            Log.Debug("write card end ret=" + ret);
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
