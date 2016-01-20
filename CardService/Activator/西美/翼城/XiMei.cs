using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class XiMei : ICard
    {
        private static Log Log = Log.GetInstance("Card.XiMei");

        public string Test()
        {
            return "ruisen";
        }
        //定义结构体
        public struct CICinf
        {


            public int cardnum;//卡号

            public int cardntime;//次数

            public int gas;//气量

            public int data1;

            public int data2;

            public int data3;

            public int data4;

            public int data5;

            public int data6;

            public int data7;

            public int data8;

            public int data9;

            public int data10;

            public int data11;

            public int data12;

            public int data13;

            public int data14;

            public int data15;

            public int data16;

            public int data17;

            public int data18;

            public int data19;

            public int dat20;

            public int data21;

            public int data22;

            public int data23;

            public int data24;

            public int data25;

            public int data26;

            public int data27;



        }

        #region 动态库导入
        //初始化串口
        [DllImport("XiMei4442.dll", EntryPoint = "ICXiMInit", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ICXiMInit(int com, int baud);
        //关闭串口
        [DllImport("XiMei4442.dll", EntryPoint = "ICXiMExit", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ICXiMExit(int icdev);

        //读民用卡
        [DllImport("XiMei4442.dll", EntryPoint = "ICReadXiMCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ICReadXiMCard(ref int type, ref int flag, ref CICinf ic);
        //发卡
        [DllImport("XiMei4442.dll", EntryPoint = "Sle4442_MakeCard_Consumer", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Sle4442_MakeCard_Consumer(int CardNo, int times, int ql);
        //清卡
        [DllImport("XiMei4442.dll", EntryPoint = "Sle4442_ClearCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Sle4442_ClearCard();
        //购气
        [DllImport("XiMei4442.dll", EntryPoint = "Sle4442_WriteCard_Consumer", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Sle4442_WriteCard_Consumer(int Order, int QAmount);
        
        #endregion

        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            string kmm,         //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm)
        {
            int icdev = ICXiMInit(com, baud);
            Log.Debug("XiMei Clear card start");
            int i = Sle4442_ClearCard();
            Log.Debug("XiMei Cleat card end  and ret  is" + i);
          
            return i;
        }
        //判卡
        public int CheckGasCard( Int16 com,  Int32 baud  ) {
            Log.Debug("XiMei ReadCompany start");
           
            Log.Debug("XiMei ReadCompany end ");
            return 0;
        }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {

            int icdev= ICXiMInit( com,  baud);
            int type = 0;
            int flag = 0;
            CICinf cf = new CICinf();
            Log.Debug("XiMei ReadCard start ");
            int i = ICReadXiMCard(ref type, ref  flag, ref  cf);
            Log.Debug("XiMei ReadCard end and i is " + i + " quantity is " + cf.gas + " flag is " + flag+" cardid  is "+cf.cardnum);
            kh = cf.cardnum.ToString();
            cs = short.Parse(cf.cardntime.ToString());
            ql = cf.gas/10;
           
            return i;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int icdev = ICXiMInit(com, baud);
            int type = 0;
            int flag = 0;
            CICinf cf = new CICinf();
            Log.Debug("XiMei ReadCard start ");
            int i = ICReadXiMCard(ref type, ref  flag, ref  cf);
            Log.Debug("XiMei ReadCard end and i is " + i + " quantity is " + cf.gas + " flag is " + flag + " cardid  is " + cf.cardnum);
            int times = cs;
            Log.Debug("XiMei WriteGasCard Start and  cardNo is " + kh + " cs is" + cs + " ql is " + ql);
            int ret = Sle4442_WriteCard_Consumer(cs , ql*10);
            Log.Debug("XiMei WriteGasCard end and ret is "+ret);
          
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int icdev = ICXiMInit(com, baud);
            Sle4442_ClearCard();
            int cardNo = int.Parse(kh);
            int times = cs;
            Log.Debug("XiMei WriteNewCard Start and  cardNo is " + kh + " cs is" + cs + " ql is " + ql);
            int i = Sle4442_MakeCard_Consumer(cardNo, times, ql * 10);
            Log.Debug("XiMei WriteNewCard end and i is " + i);
         
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
                return "XiMei";
            }
        }

        #endregion
    }
}
