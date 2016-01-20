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

        #region 动态库导入
        //读卡函数
        [DllImport("goldcard_hh.dll", EntryPoint = "Gold_Readcard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Readcard(int com, int baud, byte[] kh, ref int ql, ref int cs, ref int type);

        //写卡函数
        [DllImport("goldcard_hh.dll", EntryPoint = "Gold_Writecard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Writecard(int com, int baud, byte[] kh, int ql, int cs, int type);
        //购气函数
        [DllImport("goldcard_hh.dll", EntryPoint = "Gold_Buycard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Buycard(int com, int baud, byte[] kh, int ql, int cs);
        //清卡？
        [DllImport("goldcard_hh.dll", EntryPoint = "Gold_Formatcard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Formatcard(int com, int baud, byte[] kh);
        //判卡函数
        [DllImport("goldcard_hh.dll", EntryPoint = "ReadCompany", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCompany();
        #endregion

        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
            byte[] kh1 = new byte[8];
            kh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Log.Debug("MY Gold_Formatcard start ");
            int i = Gold_Formatcard(com, baud, kh1);
            Log.Debug("MY Gold_Formatcard end and i is "+i);
            return i;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){
                 int com1 = com;
                 int type = 0;
                 int ql = 0;
                 int cs1 = 0;
                 int a = 10;
                 byte[] kh1 = new byte[8];
                 Log.Debug("MY Gold_Readcard as CHECK start");
                 int i = Gold_Readcard(com1, baud, kh1, ref ql, ref cs1, ref type);
                 Log.Debug("MY Gold_Readcard as CHECK end and i is" + i + " ql is " + ql + " cs is " + cs1 + " type is " + type);
                 if (type == 1) {
                     a = 0;
                 }
                 return a;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
           int com1 = com;
           int cs1 = cs;
            int type = 0;
            byte[] kh1 = new byte[8];
            Log.Debug("MY Gold_Readcard start ");
            int i = Gold_Readcard(com1, baud, kh1, ref ql, ref cs1, ref type);
           
            kh= Encoding.ASCII.GetString(kh1, 0, 8);
            Log.Debug("MY Gold_Readcard end and i is " + i + " kh is "+kh+" ql is " + ql + " cs1 is " + cs1 + " type is" + type);
            cs = (short)cs1;
            return i;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int com1 = com;
            byte[] kh1 = new byte[8];
            kh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Log.Debug("MY Gold_Buycard start and kh is " + kh + " ql is " + ql + " cs is" + cs);
            int i = Gold_Buycard(com1, baud, kh1, ql, cs);
            Log.Debug("MY Gold_Buycard end and i is"+i);
            return i;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            int com1 = com;
            int cs1 = 0;
            int type = 0;
            byte[] kh2 = new byte[8];
            int ql2 = 0;
            int type1 = 0;
            Gold_Readcard(com1, baud, kh2, ref ql2, ref cs1, ref type);
            Gold_Formatcard(com, baud, kh2);
            int klx = 1;//代表民用
            byte[] kh1 = new byte[8];
          
            kh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Log.Debug("MY Gold_Writecard start and kh is " + kh1 + " ql is " + ql + " cs is" + cs);
            int i = Gold_Writecard(com, baud, kh1, ql, cs, klx);
            Log.Debug("MY Gold_Writecard end and i is " + i);
            return i;
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
