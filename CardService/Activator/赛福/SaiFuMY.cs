using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class SaiFuMY : ICard
    {
        private static Log Log = Log.GetInstance("Card.SaiFuMY");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //判断用户类型
        [DllImport("listenPDADll.dll", EntryPoint = "chkFac", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ChCusType(int com);

        //读民用卡
        [DllImport("listenPDADll.dll", EntryPoint = "WReadCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WReadCard(int portNo, ref StringBuilder cardNo, ref long gasVal, ref int times, ref StringBuilder citycode);
        //购气函数
        [DllImport("listenPDADll.dll", EntryPoint = "Gold_Buycard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Buycard(int com, int baud, byte[] kh, int ql, int cs);
        //清卡？
        [DllImport("listenPDADll.dll", EntryPoint = "Gold_Formatcard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Formatcard(int com, int baud, byte[] kh);
        //判卡函数
        [DllImport("listenPDADll.dll", EntryPoint = "ReadCompany", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCompany();
        #endregion

        #region ICard Members

          public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {

            return 1;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){

                 return 1;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
            StringBuilder cardNo=new StringBuilder();
            long gasVal=0;
            int times=0;
            StringBuilder citycode=new StringBuilder();
            int ret =WReadCard(com, ref cardNo, ref  gasVal, ref  times, ref  citycode);

           return 1;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            return 1;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            return 1;
           
        }


        public string Name
        {
            get
            {
                return "SaiFuMY";
            }
        }

        #endregion
    }
}
