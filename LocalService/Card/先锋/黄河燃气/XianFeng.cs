using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class XianFeng : ICard
    {
        private static Log Log = Log.GetInstance("Card.XianFeng");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //读卡函数

        [DllImport("WRwCard.dll", EntryPoint = "ReadCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public unsafe static extern int ReadCard(out byte* cardNo, int* BuyGasNum, int* UpdateFlag);

        //写卡函数
        [DllImport("WRwCard.dll", EntryPoint = "WriteUser", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteUser(byte[] cardNo, byte[] OldcardNo, int BuyGasNum, int UpdateFlag);
        //清卡
        [DllImport("WRwCard.dll", EntryPoint = "FormatCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int FormatCard();
        //判卡函数
        [DllImport("WRwCard.dll", EntryPoint = "ReadCompany", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
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
            int i = FormatCard();
            return i;
        }
        //判卡
        public int CheckGasCard( Int16 com,  Int32 baud  ) {
            Log.Debug("XF ReadCompany start");
            int i = ReadCompany();
            Log.Debug("XF ReadCompany end and i is"+i);
            return i;
        }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
           int i=10;
           unsafe
           {
               byte* cardNo;
               int quantity;
               int flag;
               Log.Debug("XF ReadCard start ");
               i = ReadCard(out cardNo, &quantity, &flag);
               Log.Debug("XF ReadCard end and i is " + i + " quantity is " + quantity + " flag is " + flag);
               while (*cardNo != 0)
               {
                   kh += Convert.ToChar(*cardNo++);
               }
               ql = quantity;
           }
            return i;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
           
            int i = 10;
            byte[] cardNo1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            unsafe
            {

                int flag = 0;
                int quantity = ql;
                Log.Debug("XF WriteUser as writeGas start and cardid is" + cardNo1 + " quantity is" + quantity + " flag is " + flag);
             i=   WriteUser(cardNo1, cardNo1, quantity, flag);
             Log.Debug("XF WriteUser as writeGas end  and i is" + i);
            }
          
            return i;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            FormatCard();
            int i = 10;
            byte[] cardNo1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            unsafe
            {

                int flag = 12;
                int quantity = ql;
                Log.Debug("XF WriteUser as writeNew start and cardid is" + cardNo1 + " quantity is" + quantity + " flag is " + flag);
                i=WriteUser(cardNo1, cardNo1, quantity, flag);
                Log.Debug("XF WriteUser as writeNew end and i is "+i);
            }

            return i;
        }

        public string Name
        {
            get
            {
                return "XianFeng";
            }
        }

        #endregion
    }
}
