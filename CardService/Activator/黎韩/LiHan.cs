using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class LiHan : ICard
    {
        private static Log Log = Log.GetInstance("Card.LiHan");

        public string Test()
        {
            return "黎韩";
        }


        #region 明华动态库导入
        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baud);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);
        //读4442卡
        [DllImport("Mwic_32.dll", EntryPoint = "srd_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 srd_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //写4442卡
        [DllImport("Mwic_32.dll", EntryPoint = "swr_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 swr_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //读出密码
        //校验密码int csc_4442(int icdev, int len, unsigned char* p_string)
        [DllImport("Mwic_32.dll", EntryPoint = "csc_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 csc_4442(int icdev, Int16 len, byte[] data_buffer);
        [DllImport("Mwic_32.dll", EntryPoint = "wsc_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 wsc_4442(int icdev, Int16 len, byte[] data_buffer);
        #endregion

        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
            //打开串口
           int icdev  = ic_init(com, baud);
           //读出卡号 根据卡号计算出密码
           //读出卡号
           byte[] cardno = new byte[100];
           int ret = srd_4442(icdev, 0x35, 2, cardno);
           int len = 0;
           for (int ii = 0; ii < cardno.Length; ii++)
           {
               if (cardno[ii] == 0)
               {
                   len = ii;
                   break;
               }
           }
           string a = cardno[0].ToString("X2");
           string b = cardno[1].ToString("X2");
           //16进制转10进制
           int c = Int32.Parse((a + b), System.Globalization.NumberStyles.HexNumber);
           //购气
           //密码校验规则   530  直接转16进制为212
           int cardno22 = c;
           byte[] pass222 = new byte[3];

           pass222[1] = Byte.Parse((cardno22 / 256).ToString());
           pass222[0] = 0;
           pass222[2] = Byte.Parse((cardno22 % 256).ToString());


           //核对密码
           int i = 0;

           int z = 0;
           ////修改密码
           z = csc_4442(icdev, 3, pass222);
           byte[] passwd = new byte[3] { 0xff, 0xff, 0xff };
            z = wsc_4442(icdev, 3, passwd);
            //清卡
           byte[] data_buffer = new byte[0xe0];
           for (int iii = 0; iii < data_buffer.Length; iii++)
           {
               data_buffer[iii] = 0xff;
           }
          z= swr_4442(icdev, 0x20, 0xe0, data_buffer);
          ic_exit(icdev);
            return z;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){
                 ////打开串口
                 //int icdev = ic_init(com, baud);
                 //int i = HHChkFac(icdev);
                 //Log.Debug("HH CheckCard end and i is" + i);
                 //ic_exit(icdev);
                 //if (i == 6) {
                 //    i = 0;
                 //}
                 return 0;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
         int  icdev = ic_init(com, baud);
      
           //读出卡号
           byte[] cardno = new byte[100];
           int ret = srd_4442(icdev, 0x35, 2, cardno);
           int len = 0;
           for (int i = 0; i < cardno.Length; i++)
           {
               if (cardno[i] == 0)
               {
                   len = i;
                   break;
               }
           }
           string a = cardno[0].ToString("X2");
           string b = cardno[1].ToString("X2");
           //16进制转10进制
           int c = Int32.Parse((a + b), System.Globalization.NumberStyles.HexNumber);
           //读出购气次数
           kh = c.ToString();
           byte[] cs1 = new byte[100];
           ret = srd_4442(icdev, 0x28, 2, cs1);
           len = 0;
           for (int i = 0; i < cs1.Length; i++)
           {
               if (cs1[i] == 0)
               {
                   len = i;
                   break;
               }
           }
           a = cs1[0].ToString("X2");
           b = cs1[1].ToString("X2");

           c = Int32.Parse((a + b), System.Globalization.NumberStyles.HexNumber);
           cs = (short)c;
           //读出是否接表
           
           byte[] isin = new byte[100];
           ret = srd_4442(icdev, 0x34, 1, isin);
           len = 0;
           for (int i = 0; i < isin.Length; i++)
           {
               if (isin[i] == 0)
               {
                   len = i;
                   break;
               }
           }
           a = isin[0].ToString("X2");

           //16进制存储气量 * 10
           //读出卡内气量
           byte[] ql1 = new byte[100];
           ret = srd_4442(icdev, 0x45, 3, ql1);
           len = 0;
            Log.Debug("Read Lihan and ql[0] is "+ql1[0].ToString()+" ql[1] is "+ql1[1].ToString());
           for (int i = 0; i < ql1.Length; i++)
           {
               if (ql1[i] == 0)
               {
                   len = i;
                   break;
               }
           }
           a = ql1[0].ToString("X2");
           b = ql1[1].ToString("X2");
           Log.Debug("Lihan read ql and a is " + a + " and b  is " + b);
           String ab = ql1[0].ToString("X2")+ql1[1].ToString("X2")+ql1[2].ToString("X2");
           Log.Debug("LiHan read card gas f and ab is " + ab);
           ql = ( Int32.Parse((ab), System.Globalization.NumberStyles.HexNumber))/10;
           Log.Debug("Lihan Read gas end and ql is in 10 " + ql);
           int close = ic_exit(icdev);
           return ret;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            //打开串口
            int icdev = ic_init(com, baud);

            //读出卡号 根据卡号计算出密码
            //读出卡号
            byte[] cardno = new byte[100];
            int ret = srd_4442(icdev, 0x35, 2, cardno);
            int len = 0;
            for (int ii = 0; ii < cardno.Length; ii++)
            {
                if (cardno[ii] == 0)
                {
                    len = ii;
                    break;
                }
            }
            string a = cardno[0].ToString("X2");
            string b = cardno[1].ToString("X2");
            //16进制转10进制
            int c = Int32.Parse((a + b), System.Globalization.NumberStyles.HexNumber);
            Log.Debug("LiHan BuyCard Read Cardid   end and z is " +c+" and kh is "+c.ToString());
            //购气
            //密码校验规则   530  直接转16进制为212
            int cardno22 = c;
            byte[] pass222 = new byte[3];

            pass222[1] = Byte.Parse((cardno22 / 256).ToString());
            pass222[0] = 0;
            pass222[2] = Byte.Parse((cardno22 % 256).ToString());


            //核对密码
            int i = 0;
         
            int z = 0;
            ////修改密码
            z = csc_4442(icdev, 3, pass222);
            Log.Debug("LiHan BuyCard  CheckPass end and z is " + z);
            //0x28 位置写购气次数
            //string s = i.ToString("X"); //转16进制
            //j = int.Parse(s,System.Globalization.NumberStyles.AllowHexSpecifier);//16转10
            byte[] times = new byte[2];
            Log.Debug("LiHan 1");
            if (cs >= 10) {
                cs = 9;
            }
            int time1 = cs;
            Log.Debug("LiHan 2");
            int time2 = 0;
            Log.Debug("LiHan 3");
            string s = time1.ToString("X2");
            Log.Debug("LiHan 4");
            string s1 = time2.ToString("X2");
            Log.Debug("LiHan 5");
            times[0] = Convert.ToByte(s1);
            Log.Debug("LiHan 6");
            times[1] = Convert.ToByte(s);
            Log.Debug("LiHan 7");
            i = swr_4442(icdev, 0x28, 2, times);
            Log.Debug("LiHan BuyCard Write Cs end and z is " + i);
            //0x30写卡类型 01
            byte[] klxs = new byte[1];
            byte klx1 = 1;



            klxs[0] = klx1;
            i = swr_4442(icdev, 0x30, 1, klxs);
            Log.Debug("LiHan BuyCard  Write Klx end and z is " + i);
            //0x35写卡号 02 0b 00 02 0b类型  五个长度
            //   swr_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
            int cardno11 = c;
            byte[] cardno111 = new byte[5];
            // 写入卡号
            if (cardno11 > 9999)
            {

                cardno111[0] = Byte.Parse((cardno11 / 10000 / 256).ToString());
                cardno111[1] = Byte.Parse((cardno11 % 10000 % 256).ToString());
                cardno111[2] = Byte.Parse((cardno11 % 10000 / 256).ToString());
            }
            else
            {


                cardno111[1] = Byte.Parse((cardno11 % 256).ToString());
                cardno111[0] = Byte.Parse((cardno11 / 256).ToString());
                cardno111[2] = 0;
                cardno111[4] = Byte.Parse((cardno11 % 256).ToString());
                cardno111[3] = Byte.Parse((cardno11 / 256).ToString());
            }
            i = swr_4442(icdev, 0x35, 5, cardno111);
            Log.Debug("LiHan BuyCard  Write CardId end and z is " + i);
            //0x34写是否接表的标识   购气后写入ca  接表后读出34
            byte[] jbbs = new byte[1];
            string jbbs1 = "ca";
            byte jbbs2 = (byte)Convert.ToInt32(jbbs1, 16);
            jbbs[0] = jbbs2;
            i = swr_4442(icdev, 0x34, 1, jbbs);
            //0x45写气量 00 00 64 16进制*10的数字
            //写10方气
            if (ql > 0)
            {
                int qqqqqql = ql;
                qqqqqql = qqqqqql * 10;
                byte[] ql1 = new byte[3];
                if (qqqqqql > 255)
                {

                    ql1[0] = 0;
                    ql1[1] = Byte.Parse((qqqqqql % 10000 / 256).ToString());
                    ql1[2] = Byte.Parse((qqqqqql % 10000 % 256).ToString());
                }
                else
                {


                    ql1[2] = Byte.Parse((qqqqqql % 256).ToString());
                    ql1[0] = 0;
                    ql1[1] = 0;

                }

                i = swr_4442(icdev, 0x45, 3, ql1);
                Log.Debug("LiHan BuyCard  Add Gas end and z is " + i);

            }
                //退气
            else {
                int qqqqqql = 0;
                qqqqqql = qqqqqql * 10;
                byte[] ql1 = new byte[3];
                if (qqqqqql > 255)
                {

                    ql1[0] = 0;
                    ql1[1] = Byte.Parse((qqqqqql % 10000 / 256).ToString());
                    ql1[2] = Byte.Parse((qqqqqql % 10000 % 256).ToString());
                }
                else
                {


                    ql1[2] = Byte.Parse((qqqqqql % 256).ToString());
                    ql1[0] = 0;
                    ql1[1] = 0;

                }

                i = swr_4442(icdev, 0x45, 3, ql1);
                Log.Debug("LiHan BuyCard  Return Gas  end and z is " + i);
            }
            //0x48写000000
            byte[] weizhi = new byte[3] { 00, 00, 00 };



            i = swr_4442(icdev, 0x48, 3, weizhi);
            //0x30写卡类型 01
          



          

            ic_exit(icdev);
            return i;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            //写新卡  修改卡的原始密码 根据卡号转16进制计算。
            //打开串口
            int icdev = ic_init(com, baud);
            //密码校验规则   530  直接转16进制为212
            int cardno22 = int.Parse(kh);
            byte[] pass222 = new byte[3];

            pass222[1] = Byte.Parse((cardno22 / 256).ToString());
            pass222[0] = 0;
            pass222[2] = Byte.Parse((cardno22 % 256).ToString());


            //核对密码
            int i = 0;
            byte[] passwd = new byte[3] { 0xff, 0xff, 0xff };
            int z = csc_4442(icdev, 3, passwd);
            Log.Debug("LiHan NewCard Check old end and z is "+z);
            ////修改密码
            z = wsc_4442(icdev, 3, pass222);
            Log.Debug("LiHan NewCard ModifyPass  end and z is " + z);
            //0x28 位置写购气次数
            //string s = i.ToString("X"); //转16进制
            //j = int.Parse(s,System.Globalization.NumberStyles.AllowHexSpecifier);//16转10
            byte[] times = new byte[2];
            if (cs >= 10)
            {
                cs = 9;
            }
            int time1 = cs;
            int time2 = 0;
            string s = time1.ToString("X2");
            string s1 = time2.ToString("X2");
            times[0] = Convert.ToByte(s1);
            times[1] = Convert.ToByte(s);
            i = swr_4442(icdev, 0x28, 2, times);
            Log.Debug("LiHan NewCard Write Cs end and z is " + i);
            //0x30写卡类型 01
            byte[] klxs = new byte[1];
            byte klx1 = 1;



            klxs[0] = klx1;
            i = swr_4442(icdev, 0x30, 1, klxs);
            Log.Debug("LiHan NewCard Write Klx   end and z is " + i);
            //0x35写卡号 02 0b 00 02 0b类型  五个长度
            //   swr_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
            int cardno11 = int.Parse(kh);
            byte[] cardno111 = new byte[5];
            // 写入卡号
            if (cardno11 > 9999)
            {

                cardno111[0] = Byte.Parse((cardno11 / 10000 / 256).ToString());
                cardno111[1] = Byte.Parse((cardno11 % 10000 % 256).ToString());
                cardno111[2] = Byte.Parse((cardno11 % 10000 / 256).ToString());
            }
            else
            {


                cardno111[1] = Byte.Parse((cardno11 % 256).ToString());
                cardno111[0] = Byte.Parse((cardno11 / 256).ToString());
                cardno111[2] = 0;
                cardno111[4] = Byte.Parse((cardno11 % 256).ToString());
                cardno111[3] = Byte.Parse((cardno11 / 256).ToString());
            }
            i = swr_4442(icdev, 0x35, 5, cardno111);
            Log.Debug("LiHan NewCard Write Cardid  end and z is " + i);
            ////卡号传入之后  转16进制，转完之后从后面两位开始截取 放入数组中。
            //string cardid = (555).ToString("X2");
            //string cardid1 = cardid.Substring(1, 2);
            //string cardid2 = cardid.Substring(0, 1);
            //string cardid3 = "00";
            ////16进制转回10进制放入byte数组中
            //byte cardid4 = (byte)Convert.ToInt32(cardid1, 16);
            //cardno[0] = Convert.ToByte(cardid2);
            //cardno[1] = Convert.ToByte(cardid4);
            //cardno[2] = Convert.ToByte(cardid3);
            //cardno[3] = Convert.ToByte(cardid2);
            //cardno[4] = Convert.ToByte(cardid4);

            //times[0] = Convert.ToByte(s1);
            //times[1] = Convert.ToByte(s);
            //int i = swr_4442(icdev, 0x28, 2, times);
            //0x34写是否接表的标识   购气后写入ca  接表后读出34
            byte[] jbbs = new byte[1];
            string jbbs1 = "ca";
            byte jbbs2 = (byte)Convert.ToInt32(jbbs1, 16);
            jbbs[0] = jbbs2;
            i = swr_4442(icdev, 0x34, 1, jbbs);
            Log.Debug("LiHan NewCard Write Flag end and z is " + i);
            //0x45写气量 00 00 64 16进制*10的数字
            //写10方气
            int qqqqqql = ql;
            qqqqqql = qqqqqql * 10;
            byte[] ql1 = new byte[3];
            if (qqqqqql > 255)
            {

                ql1[0] = 0;
                ql1[1] = Byte.Parse((qqqqqql % 10000 / 256).ToString());
                ql1[2] = Byte.Parse((qqqqqql % 10000 % 256).ToString());
            }
            else
            {


                ql1[2] = Byte.Parse((qqqqqql % 256).ToString());
                ql1[0] = 0;
                ql1[1] = 0;

            }

            i = swr_4442(icdev, 0x45, 3, ql1);
            Log.Debug("LiHan NewCard Write Gas end and z is " + i);
            //0x48写000000
            byte[] weizhi = new byte[3] { 00, 00, 00 };



            i = swr_4442(icdev, 0x48, 3, weizhi);
            //0x30写卡类型 01
            ic_exit(icdev);


            return i;
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
                return "LiHan";
            }
        }

        #endregion
    }
}
