using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class HuaQing : ICard
    {
        private static Log Log = Log.GetInstance("Card.HuaQing");

        public string Test()
        {
            return "华青";
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
        //16进制转换
        [DllImport("Mwic_32.dll", EntryPoint = "hex_asc", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 hex_asc(byte[] data_buffer, byte[] data_buffer1, Int16 length);
        [DllImport("Mwic_32.dll", EntryPoint = "asc_hex", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 asc_hex(byte[] data_buffer, byte[] data_buffer1, Int16 length);
        #endregion

        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
            int icdev = ic_init(com, baud);
            byte[] useridasc = new byte[20];
            byte[] useridhex = new byte[20];
            //读卡号
            //读0xA1开始7个位置 用户编号为14位
            string UserID = "";
            Log.Debug("清卡开始：");
            Int16 str = srd_4442(icdev, 0xA1, 7, useridhex);
            if (str == 0)
            {
                str = hex_asc(useridhex, useridasc, 16);
                UserID = Encoding.ASCII.GetString(useridasc, 0, 14);        //读出卡号 （用户编号）14位  ok

            }
            int p1 = int.Parse(UserID.Substring(0, 4)) + int.Parse(UserID.Substring(4, 4));
            int p2 = int.Parse(UserID.Substring(8, 4)) + int.Parse(UserID.Substring(12, 2));
            int p3 = int.Parse(UserID.Substring(1, 4)) + int.Parse(UserID.Substring(9, 4));
            string ps = p1.ToString() + p2.ToString() + p3.ToString();
            Int32 X = Int32.Parse(ps);
            byte[] passwd1 = new byte[3];
            int x111 = X / 1000;
            int x211 = x111 / 256;
            int x311 = x211 / 256;
            int x411 = x311 % 256;
            passwd1[0] = byte.Parse(x411.ToString());
            int x511 = X / 1000;
            int x611 = x511 / 256;
            int x711 = x611 % 256;
            passwd1[1] = Byte.Parse(x711.ToString());
            int x811 = X / 1000;
            int x911 = x811 % 256;
            passwd1[2] = Byte.Parse(x911.ToString());
            //  byte[] passhex = new byte[3];
            // int aaa = asc_hex(passwd1, passhex, 3);
            int z = 0;
            //    csc_4442(icdev, 3, passwd);
            ////修改密码
            z = csc_4442(icdev, 3, passwd1);
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
                 int icdev = ic_init(com, baud);
               //读0x54位置 作为判卡依据

                 byte[] useridhex = new byte[20];
                 byte[] useridasc = new byte[20];
                 string kh= "";
                 Log.Debug("Icdev is " + icdev);
                 Int16 str = srd_4442(icdev, 0xA1, 7, useridhex);
                 Log.Debug("Checkend and ret is  " + str);
                 int ret = -1;
                 if (str == 0)
                 {
                     str = hex_asc(useridhex, useridasc, 16);
                    kh = Encoding.ASCII.GetString(useridasc, 0, 14);        //读出卡号 （用户编号）14位  ok
                    Log.Debug("kh is " + kh);
                 }
                 if (kh.Substring(0, 6).Equals("010001")) {
                     ret = 0;
                 }
                 ic_exit(icdev);
                 return ret;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
           int icdev = ic_init(com, baud);
           int len = 0;
           byte[] check = new byte[1];
           //检查卡
           int ret = srd_4442(icdev, 0xF8, 1, check);
           if (ret < 0)
           {
               //此卡已坏,请更换
           }
           byte[] useridhex = new byte[20];
           byte[] useridasc = new byte[20];
           //for (int i = 0; i < 20; i++)
           //    useridhex[i] = 0;
           //for (int j = 0; j < 20; j++)
           //    useridasc[j] = 0;
           //读卡号
           //读0xA1开始7个位置 用户编号为14位
           Int16 str = srd_4442(icdev, 0xA1, 7, useridhex);
           if (str == 0)
           {
               str = hex_asc(useridhex, useridasc, 16);
                kh = Encoding.ASCII.GetString(useridasc, 0, 14);        //读出卡号 （用户编号）14位  ok

           }
            //设置系统中卡号为后4位  其余在封装中处理
           Log.Debug("Read kh 1  is " + kh);
           kh = kh.Substring(10,4);
           Log.Debug("Read kh 2 is " + kh);
           //读购气日期
           byte[] buydatehex = new byte[20];
           byte[] buydateasc = new byte[20];
           //读 0x50开始4个位置
           str = srd_4442(icdev, 0x50, 4, buydatehex);
           //hex 转asc
           int st = hex_asc(buydatehex, buydateasc, 4);
           string buydate = Encoding.ASCII.GetString(buydateasc, 0, 8);

           //读出上次购气日期
           buydatehex = new byte[20];
           buydateasc = new byte[20];
           //读 0x50开始4个位置
           str = srd_4442(icdev, 0x55, 4, buydatehex);
           //hex 转asc
           st = hex_asc(buydatehex, buydateasc, 4);
           string lastbuydate = Encoding.ASCII.GetString(buydateasc, 0, 8);


           //读出购气量
           byte[] databuffql = new byte[100];
           byte[] databuffqlasc = new byte[100];
           Int16 stql = srd_4442(icdev, 0x20, 64, databuffql);      //'*****取位到4F*****
           st = hex_asc(databuffql, databuffqlasc, 64);  //转成ASCII码

           byte[] bql = new byte[8] { databuffqlasc[12], databuffqlasc[13], databuffqlasc[0], databuffqlasc[1], databuffqlasc[4], databuffqlasc[5] 
            ,databuffqlasc[10], databuffqlasc[11]};
           string sql = System.Text.Encoding.ASCII.GetString(bql);
           int c = Int32.Parse(sql, System.Globalization.NumberStyles.HexNumber);
           //一方气读出00 0F 42 40  ok
           //读0x54位置 作为判卡依据
           byte[] testin = new byte[20];

           //读 0x50开始4个位置
            str = srd_4442(icdev, 0x23, 6, testin);
            Log.Debug("testin is " + testin[0].ToString());
            if (testin[0].ToString().Equals("0") | testin[0].ToString().Equals("00"))
            {
                ql = 0;
            }
            else {
                ql = c / 1000000;
            }
           
           //读出写卡次数
           byte[] times = new byte[20];
           byte[] times1 = new byte[20];
           //for (int i = 0; i < 20; i++)
           //    useridhex[i] = 0;
           //for (int j = 0; j < 20; j++)
           //    useridasc[j] = 0;
           //读写卡次数
           str = srd_4442(icdev, 0xBE, 2, times);
          
           if (str == 0)
           {
               str = hex_asc(times, times1, 2);
               //for (int h = 0; h < times1.Length; h++)
               //{
               //    if (times1[h] == 0)
               //    {
               //        len = h;
               //        break;
               //    }
               //}

               cs = short.Parse(Int32.Parse(Encoding.ASCII.GetString(times1, 0, 2).ToString(), System.Globalization.NumberStyles.HexNumber).ToString());      

           }

           ic_exit(icdev);
           return str;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            //打开串口

            int icdev = ic_init(com, baud);
            byte[] useridasc = new byte[20];
            byte[] useridhex = new byte[20];
            //for (int i = 0; i < 20; i++)
            //    useridhex[i] = 0;
            //for (int j = 0; j < 20; j++)
            //    useridasc[j] = 0;
            //读卡号
            //读0xA1开始7个位置 用户编号为14位
            string UserID = "";
            Int16 str = srd_4442(icdev, 0xA1, 7, useridhex);
            if (str == 0)
            {
                str = hex_asc(useridhex, useridasc, 16);
                UserID = Encoding.ASCII.GetString(useridasc, 0, 14);        //读出卡号 （用户编号）14位  ok

            }
            int p1 = int.Parse(UserID.Substring(0, 4)) + int.Parse(UserID.Substring(4, 4));
            int p2 = int.Parse(UserID.Substring(8, 4)) + int.Parse(UserID.Substring(12, 2));
            int p3 = int.Parse(UserID.Substring(1, 4)) + int.Parse(UserID.Substring(9, 4));
            string ps = p1.ToString() + p2.ToString() + p3.ToString();
            Int32 X = Int32.Parse(ps);
            byte[] passwd1 = new byte[3];
            int x111 = X / 1000;
            int x211 = x111 / 256;
            int x311 = x211 / 256;
            int x411 = x311 % 256;
            passwd1[0] = byte.Parse(x411.ToString());
            int x511 = X / 1000;
            int x611 = x511 / 256;
            int x711 = x611 % 256;
            passwd1[1] = Byte.Parse(x711.ToString());
            int x811 = X / 1000;
            int x911 = x811 % 256;
            passwd1[2] = Byte.Parse(x911.ToString());
            //  byte[] passhex = new byte[3];
            // int aaa = asc_hex(passwd1, passhex, 3);
            int z = 0;
            //    csc_4442(icdev, 3, passwd);
            ////修改密码
            z = csc_4442(icdev, 3, passwd1);

            //购气 现读卡核对密码
            //写入购气量
            //写入购气日期//写入上次购气日期  //写入新系统标识
            byte[] tempDataasc = new byte[9];
            byte[] tempDatahex = new byte[9];
            tempDataasc[0] = 0x20;
            tempDataasc[1] = 0x15;
            tempDataasc[2] = 0x11;
            tempDataasc[3] = 0x24;
            tempDataasc[4] = 0xee;
            tempDataasc[5] = 0x20;
            tempDataasc[6] = 0x15;
            tempDataasc[7] = 0x11;
            tempDataasc[8] = 0x24;



            int st = swr_4442(icdev, 0x50, 9, tempDatahex);


            //写入累计购气量
            //写入本次购气量
            if (ql < 0) {

                ql = 0;
            }

            byte[] gasasc = new byte[10];
            byte[] gashex = new byte[10];




            int T = ql * 1000000;
            int x5 = T / 256;
            int x6 = x5 / 256;
            int x7 = x6 % 256;
            gasasc[0] = Byte.Parse(x7.ToString());


            int x1 = X / 1000;
            int x2 = x1 / 256;
            int x3 = x2 / 256;
            int x4 = x3 % 256;
            gasasc[1] = byte.Parse(x4.ToString());
            int x8 = T / 256;
            int x9 = x8 % 256;
            gasasc[2] = Byte.Parse(x9.ToString());
            gasasc[3] = 50;
            x5 = X / 1000;
            x6 = x5 / 256;
            x7 = x6 % 256;
            gasasc[4] = Byte.Parse(x7.ToString());
            gasasc[5] = Byte.Parse((T % 256).ToString());

            x1 = T / 256;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            gasasc[6] = byte.Parse(x4.ToString());
            x8 = X / 1000;
            x9 = x8 % 256;
            gasasc[7] = Byte.Parse(x9.ToString());
            x5 = 10000000 / 256;
            x6 = x5 / 256;
            x7 = x6 % 256;
            gasasc[8] = Byte.Parse(x7.ToString());
            x1 = 10000000 / 256;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            gasasc[9] = byte.Parse(x4.ToString());
            st = swr_4442(icdev, 0x20, 10, gasasc);

            //读出0xBE
            byte[] temp = new byte[2];
            st = srd_4442(icdev, 0xBE, 2, temp);
            byte[] byte_flag = new byte[7];

            byte_flag[0] = temp[0];
            byte_flag[1] = temp[1];
            byte_flag[0] = byte.Parse((int.Parse(temp[0].ToString()) + 1).ToString());  //购气次数
            if (byte_flag[0] > 255)
            {
                byte_flag[0] = byte.Parse((int.Parse(byte_flag[0].ToString()) - 256).ToString());
                byte_flag[1] = byte.Parse((int.Parse(temp[1].ToString()) + 1).ToString());
            }

            T = 25;  //累计购气量
            byte_flag[2] = Byte.Parse((T % 256).ToString());
            x8 = T / 256;
            x9 = x8 % 256;
            byte_flag[3] = Byte.Parse(x9.ToString());
            x5 = T / 256;
            x6 = x5 / 256;
            x7 = x6 % 256;
            byte_flag[4] = Byte.Parse(x7.ToString());
            x1 = T / 256;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            byte_flag[5] = byte.Parse(x4.ToString());
            byte_flag[6] = 250;
            st = swr_4442(icdev, 0xBE, 6, byte_flag); //写入购气次数跟累计购气量
            byte[] temp1 = new byte[1] { 0xFA };
            st = swr_4442(icdev, 0xCD, 1, temp1);

            ic_exit(icdev);
            return st;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            //产生随机数
            Random example = new Random();
            byte[] Test1 = new byte[256];
            for (int i = 32; i <= 255; i++)
            {
                Test1[i] = byte.Parse(example.Next(1, 200).ToString());

            }
            String UserID = "0100010000"+kh;
            //读卡判断是否为新卡  读23位置  省略
            Log.Debug("HuaQing Write NewCard UserID is " + UserID);
            //上次购气日期本次购气日期未处理
            Test1[80] = 0x20;
            Test1[81] = 0x15;
            Test1[82] = 0x11;
            Test1[83] = 0x24;
            Test1[84] = 0xee;
            Test1[85] = 0x20;
            Test1[86] = 0x15;
            Test1[87] = 0x11;
            Test1[88] = 0x24;
            Test1[95] = 0x11;
            Test1[137] = byte.Parse((int.Parse(UserID.Substring(2, 1)) * 16 + int.Parse(UserID.Substring(3, 1))).ToString());
            Test1[138] = byte.Parse((int.Parse(UserID.Substring(4, 1)) * 16 + int.Parse(UserID.Substring(5, 1))).ToString());
            Test1[139] = byte.Parse((int.Parse(UserID.Substring(6, 1)) * 16 + int.Parse(UserID.Substring(7, 1))).ToString());

            Test1[140] = byte.Parse((int.Parse(UserID.Substring(8, 1)) * 16 + int.Parse(UserID.Substring(9, 1))).ToString());
            Test1[141] = byte.Parse((int.Parse(UserID.Substring(10, 1)) * 16 + int.Parse(UserID.Substring(11, 1))).ToString());
            Test1[142] = byte.Parse((int.Parse(UserID.Substring(12, 1)) * 16 + int.Parse(UserID.Substring(13, 1))).ToString());

            Test1[143] = byte.Parse((int.Parse(UserID.Substring(0, 1)) * 16 + int.Parse(UserID.Substring(1, 1))).ToString());
            Test1[161] = byte.Parse((int.Parse(UserID.Substring(0, 1)) * 16 + int.Parse(UserID.Substring(1, 1))).ToString());
            Test1[162] = byte.Parse((int.Parse(UserID.Substring(2, 1)) * 16 + int.Parse(UserID.Substring(3, 1))).ToString());

            Test1[163] = byte.Parse((int.Parse(UserID.Substring(4, 1)) * 16 + int.Parse(UserID.Substring(5, 1))).ToString());
            Test1[164] = byte.Parse((int.Parse(UserID.Substring(6, 1)) * 16 + int.Parse(UserID.Substring(7, 1))).ToString());
            Test1[165] = byte.Parse((int.Parse(UserID.Substring(8, 1)) * 16 + int.Parse(UserID.Substring(9, 1))).ToString());


            Test1[166] = byte.Parse((int.Parse(UserID.Substring(10, 1)) * 16 + int.Parse(UserID.Substring(11, 1))).ToString());
            Test1[167] = byte.Parse((int.Parse(UserID.Substring(12, 1)) * 16 + int.Parse(UserID.Substring(13, 1))).ToString());
            Test1[176] = 104;
            Test1[177] = 113;
            Test1[178] = 152;
            Test1[180] = byte.Parse((int.Parse(UserID.Substring(6, 1)) * 16 + int.Parse(UserID.Substring(7, 1))).ToString());
            Test1[181] = byte.Parse((int.Parse(UserID.Substring(8, 1)) * 16 + int.Parse(UserID.Substring(9, 1))).ToString());

            Test1[182] = byte.Parse((int.Parse(UserID.Substring(1, 1)) * 16 + int.Parse(UserID.Substring(5, 1))).ToString());
            Test1[183] = byte.Parse((int.Parse(UserID.Substring(10, 1)) * 16 + int.Parse(UserID.Substring(11, 1))).ToString());
            Test1[184] = byte.Parse((int.Parse(UserID.Substring(12, 1)) * 16 + int.Parse(UserID.Substring(13, 1))).ToString());
            Test1[186] = 255;
            Test1[187] = 255;
            Test1[188] = 255;
            Test1[190] = byte.Parse((cs % 256).ToString()); //写卡次数
            int tt = 1 % 256;
            tt = tt % 256;
            //  Test1[191]=byte.Parse(tt.ToString());
            Test1[191] = 0;
           
            //   Test1[192]=byte.Parse((ql%256).ToString());
            //Test1[193]=byte.Parse((Test1[192]%256).ToString());
            //   Test1[194]=byte.Parse((Test1[193]%256).ToString());
            //   Test1[195]=byte.Parse((Test1[194]%256).ToString());
            Int32 Z = ql;


            int x11 = Z / 256;
            int x21 = x11 / 256;
            int x31 = x21 / 256;
            int x41 = x31 % 256;
            Test1[195] = byte.Parse(x41.ToString());
            int x51 = Z / 256;
            int x61 = x51 / 256;
            int x71 = x61 % 256;
            Test1[194] = Byte.Parse(x71.ToString());
            int x81 = Z / 256;
            int x91 = x81 % 256;
            Test1[193] = Byte.Parse(x91.ToString());
            Test1[192] = Byte.Parse((ql % 256).ToString());
            Test1[205] = 1;
            Test1[206] = 177;
            Test1[96] = 104;
            Test1[97] = 113;
            Test1[98] = 152;

            int p1 = int.Parse(UserID.Substring(0, 4)) + int.Parse(UserID.Substring(4, 4));
            int p2 = int.Parse(UserID.Substring(8, 4)) + int.Parse(UserID.Substring(12, 2));
            int p3 = int.Parse(UserID.Substring(1, 4)) + int.Parse(UserID.Substring(9, 4));
            string ps = p1.ToString() + p2.ToString() + p3.ToString();
            Int32 X = Int32.Parse(ps);


            int x1 = X / 1000;
            int x2 = x1 / 256;
            int x3 = x2 / 256;
            int x4 = x3 % 256;
            Test1[106] = byte.Parse(x4.ToString());
            int x5 = X / 1000;
            int x6 = x5 / 256;
            int x7 = x6 % 256;
            Test1[108] = Byte.Parse(x7.ToString());
            int x8 = X / 1000;
            int x9 = x8 % 256;
            Test1[107] = Byte.Parse(x9.ToString());


            int T = ql * 1000000;
            x5 = T / 256;
            x6 = x5 / 256;
            x7 = x6 % 256;
            Test1[32] = Byte.Parse(x7.ToString());


            x1 = X / 1000;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            Test1[33] = byte.Parse(x4.ToString());
            x8 = T / 256;
            x9 = x8 % 256;
            Test1[34] = Byte.Parse(x9.ToString());
            Test1[35] = 104;
            x5 = X / 1000;
            x6 = x5 / 256;
            x7 = x6 % 256;
            Test1[36] = Byte.Parse(x7.ToString());
            Test1[37] = Byte.Parse((T % 256).ToString());

            x1 = T / 256;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            Test1[38] = byte.Parse(x4.ToString());
            x8 = X / 1000;
            x9 = x8 % 256;
            Test1[39] = Byte.Parse(x9.ToString());
            x5 = 10000000 / 256;
            x6 = x5 / 256;
            x7 = x6 % 256;
            Test1[40] = Byte.Parse(x7.ToString());
            x1 = 10000000 / 256;
            x2 = x1 / 256;
            x3 = x2 / 256;
            x4 = x3 % 256;
            Test1[41] = byte.Parse(x4.ToString());


            x5 = T / 256;
            x6 = x5 / 256;
            x7 = x6 % 256;
            byte[] temp = new byte[256];
         
            for (int i = 0; i <= 223; i++)
            {

                temp[i] = Test1[i + 32];
            }
            temp[224] = 0;
            byte[] Data1 = new byte[224];
            int icdev = ic_init(com, baud);

            byte[] passwd = new byte[3] { 0xff, 0xff, 0xff };


            int p11 = int.Parse(UserID.Substring(0, 4)) + int.Parse(UserID.Substring(4, 4));
            int p21 = int.Parse(UserID.Substring(8, 4)) + int.Parse(UserID.Substring(12, 2));
            int p31 = int.Parse(UserID.Substring(1, 4)) + int.Parse(UserID.Substring(9, 4));
            string ps1 = p11.ToString() + p21.ToString() + p31.ToString();
            Int32 XX = Int32.Parse(ps1);

            byte[] passwd1 = new byte[3];
            int x111 = XX / 1000;
            int x211 = x111 / 256;
            int x311 = x211 / 256;
            int x411 = x311 % 256;
            passwd1[0] = byte.Parse(x411.ToString());
            int x511 = XX / 1000;
            int x611 = x511 / 256;
            int x711 = x611 % 256;
            passwd1[1] = Byte.Parse(x711.ToString());
            int x811 = XX / 1000;
            int x911 = x811 % 256;
            passwd1[2] = Byte.Parse(x911.ToString());
            //  byte[] passhex = new byte[3];
            // int aaa = asc_hex(passwd1, passhex, 3);
            int z = 0;
            //    csc_4442(icdev, 3, passwd);
            ////修改密码
            z = csc_4442(icdev, 3, passwd);
            Log.Debug("HuaQing CheckPass end and z is " + z);
            int bbb = wsc_4442(icdev, 3, passwd1);


            Log.Debug("HuaQing ModifyPass Word end and z is " +bbb);



            int zzz = swr_4442(icdev, 0x20, 224, temp);
            Log.Debug("HuaQing WriteNewCard end and z is " + zzz);
            ic_exit(icdev);
            return zzz;
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
                return "HuaQing";
            }
        }

        #endregion
    }
}
