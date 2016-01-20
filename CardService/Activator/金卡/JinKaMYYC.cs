using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class JinKaMY : ICard
    {
        private static Log Log = Log.GetInstance("Card.JinKaMY");

        public string Test()
        {
            return "ruisen";
        }

        #region 金卡动态库导入
        //读卡，标准接口
        [DllImport("goldcard.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadGasCard(Int16 com, Int32 baut,byte[] kmm, ref Int16 klx, ref Int16 kzt,  
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, ref Int32 ql, ref Int16 cs, ref Int32 ljgql, ref Int16 bkcs, 
            ref Int32 ljyql, ref Int32 syql, ref Int32 bjql, ref Int32 czsx, ref Int32 tzed,byte[] sqrq ,ref Int32 oldprice, 
            ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        //写新卡，标准接口
        [DllImport("goldcard.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteNewCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, Int32 ql, Int16 cs, Int32 ljgql, Int16 bkcs, Int32 ljyql,
            Int32 bjql, Int32 czsx, Int32 tzed, byte[] sqrq, ref Int32 oldprice, ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        //测卡，标准接口
        [DllImport("goldcard.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, Int32 baut);
        //格式化卡，标准接口
        [DllImport("goldcard.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, byte[] dqdm);
        //写购气卡，标准接口
        [DllImport("goldcard.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, 
            byte[] dqdm, Int32 ql, Int16 cs, Int32 ljgql, Int32 bjql, Int32 czsx, Int32 tzed,
            byte[] sqrq ,ref Int32 oldprice, ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            try
            {
            int ret = StaticCheckGasCard(com, baud);
            Log.Debug("check gas card" + ret);
            return ret;
            }
            catch (Exception e)
            {
                Log.Debug(e.Message);
            }
            return -1;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");

            return StaticFormatGasCard(com, baud, mm, 1, cardNO, bdqdm);
        }
         
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)
        {
            byte[] cardNO = new byte[100];
            byte[] kmm = new byte[100];
            byte[] dqdm = new byte[100];
            byte[] yhh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(yhh);
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
            short Nbkcs = bkcs;
            int ret = StaticReadGasCard(0, baud, kmm, ref klx, ref kzt, cardNO, dqdm, yhh1, tm, 
                ref ql, ref cs, ref ljgql, ref Nbkcs, ref ljyql, ref syql, ref bjql, ref czsx, 
                ref tzed, sqrq, ref oldprice, ref newprice, sxrq, sxbj);
            Log.Debug("read card ret=" + ret);
            //卡号转换成字符串
            cardNO[8] = 0;
            kh = Encoding.ASCII.GetString(cardNO, 0, 8);
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");
            byte[] bsqrq = new byte[10];
            byte[] bsxrq = new byte[10];
            byte[] bsxbj = new byte[10];
            Int32 boldprice = 0;
            Int32 bnewprice = 0;

            int ret = StaticWriteGasCard(com, baud, mm, 1, cardNO, bdqdm, ql, 
                cs, ljgql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj);
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");
            byte[] byhh = System.Text.Encoding.GetEncoding(1252).GetBytes("0000000001");
            byte[] btm = new byte[10];
            byte[] bsqrq = new byte[10];
            byte[] bsxrq = new byte[10];
            byte[] bsxbj = new byte[10];
            Int32 boldprice = 0;
            Int32 bnewprice = 0;
            Log.Debug("start write new card");
            //发卡前先格式化卡
            int ret = StaticFormatGasCard(com, baud, mm, 1, cardNO, bdqdm);
            Log.Debug("format card end ret=" + ret);
            ret = StaticWriteNewCard(com, baud, mm, 1, kzt, cardNO, bdqdm, byhh, btm, 
                ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj);
            Log.Debug("write card end ret=" + ret);
            return ret;
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
                return "JinKaMY";
            }
        }

        #endregion
    }
}
