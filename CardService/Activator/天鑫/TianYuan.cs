using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class TianYuan : ICard
    {
        private static Log Log = Log.GetInstance("Card.TianYuan");

        public string Test()
        {
            return "天源";
        }


        #region 天源动态库导入

        //清卡
        [DllImport("PTianXin.dll", EntryPoint = "InitCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticInitCardInfo(Int32 port);

        //购气
        [DllImport("PTianXin.dll", EntryPoint = "WriteICCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteCardInfo(int SystemCode, int CardID, double ICCSpare, int ICCCount, int ICNum, int OPType, int ComID);

        //读卡
        [DllImport("PTianXin.dll", EntryPoint = "ReadICCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadICCard(int[] SystemCode, int[] CardID, double[] ICCSpare, double[] ICMSpare, double[] ICCTotal, int[] ICCCount, int[] ICNum, int[] ICType, int[] ICStatus, int[] ICMStatus, int ComID);
        //public static extern int StaticReadICCard(ref int SystemCode, ref int CardID, ref double ICCSpare, ref double ICMSpare, ref double ICCTotal, ref int ICCCount, ref int ICNum, ref int ICType, ref int ICStatus, ref int ICMStatus, int ComID);

        #endregion


        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
           
            int ret = StaticInitCardInfo(com);
         
            return ret;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){
                 int[] SystemCode = new int[1] { 11111111 };
                 int[] CardID = new int[1] { 0 };
                 double[] ICCSpare = new double[1] { 0 };
                 double[] ICMSpare = new double[1] { 0 };
                 double[] ICCTotal = new double[1] { 0 };
                 int[] ICCCount = new int[1] { 0 };
                 int[] ICNum = new int[1] { 0 };
                 int[] ICType = new int[1] { 0 };
                 int[] ICStatus = new int[1] { 1 };
                 int[] ICMStatus = new int[1] { 0 };
                 Log.Debug("TIanYuan CheckGasCard Start");
                 int ret = StaticReadICCard(SystemCode, CardID, ICCSpare, ICMSpare, ICCTotal, ICCCount, ICNum, ICType, ICStatus, ICMStatus, com);
                 Log.Debug("TIanYuan CheckGasCard end and ret is "+ret);      
           return ret;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
           int[] SystemCode = new int[1] { 11111111 };
           int[] CardID = new int[1] { 0 };
           double[] ICCSpare = new double[1] { 0 };
           double[] ICMSpare = new double[1] { 0 };
           double[] ICCTotal = new double[1] { 0 };
           int[] ICCCount = new int[1] { 0 };
           int[] ICNum = new int[1] { 0 };
           int[] ICType = new int[1] { 0 };
           int[] ICStatus = new int[1] { 1 };
           int[] ICMStatus = new int[1] { 0 };
           Log.Debug("TianYuan ReadGasCard Start");
           int ret = StaticReadICCard(SystemCode, CardID, ICCSpare, ICMSpare, ICCTotal, ICCCount, ICNum, ICType, ICStatus, ICMStatus, com);
           Log.Debug("TianYuan ReadGasCard End and ret is " + ret + "  ql is " + ICCSpare[0].ToString()+"  kh is "+ CardID[0].ToString() + " cs is "+ ICCCount[0].ToString());
            kh = CardID[0].ToString(); ;
           ql =int.Parse( ICCSpare[0].ToString());
           cs = (short)ICCCount[0];
           //if (0 == ret)
           //{
           //    MessageBox.Show("读卡成功" + "\n" +
           //            "卡号：" + CardID[0] + "\n" +
           //            "气量：" + ICCSpare[0] + "\n" +
           //            "表内返回：" + ICMSpare[0] + "\n" +
           //            "总购买量：" + ICCTotal[0] + "\n" +
           //            "购买次数：" + ICCCount[0] + "\n" +
           //            "发卡次数：" + ICNum[0] + "\n" +
           //            "卡类型 1民用 2工业：" + ICType[0] + "\n" +
           //            "卡状态 0开卡 1用卡：" + ICStatus[0] + "\n" +
           //            "插卡后仪表状态返回：" + ICMStatus[0]);
           //}
           //else
           //{
           //    MessageBox.Show("读卡失败" + ret);
           //}
           return ret;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int OPType = 1;
            int ret = 0;
            Log.Debug("TianYuan WriteGasCard start and kh is "+kh +" and ql is "+ql+" and cs is "+cs+" OpType is "+OPType);
            if (ql > 0)
            {
                Log.Debug("TIanYuan BuyGas start ");
                 ret = StaticWriteCardInfo(11111111, int.Parse(kh), ql, cs, 1, OPType, com);
                 Log.Debug("TianYuan BuyGas end and i is " + ret);
            }
            else {
                Log.Debug("TIanYuan ReSetGas start");
                cs =short.Parse(cs.ToString());
                ql = 0;
                ret = StaticWriteCardInfo(11111111, int.Parse(kh), ql, cs, 1, OPType, com);
                Log.Debug("TianYuan ReSetGas end and i is " + ret);
            }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            //开户前先读卡
            int[] SystemCode = new int[1] { 11111111 };
            int[] CardID = new int[1] { 0 };
            double[] ICCSpare = new double[1] { 0 };
            double[] ICMSpare = new double[1] { 0 };
            double[] ICCTotal = new double[1] { 0 };
            int[] ICCCount = new int[1] { 0 };
            int[] ICNum = new int[1] { 0 };
            int[] ICType = new int[1] { 0 };
            int[] ICStatus = new int[1] { 1 };
            int[] ICMStatus = new int[1] { 0 };
            Log.Debug("TianYuan ReadGasCard Start");
            int ret = StaticReadICCard(SystemCode, CardID, ICCSpare, ICMSpare, ICCTotal, ICCCount, ICNum, ICType, ICStatus, ICMStatus, com);
            Log.Debug("TianYuan ReadGasCard End and ret is"+ret);
            int OPType = 0;
            if (kzt == 1) {
                Log.Debug("TianYuan ReNewCard Start set OpType=2");
                OPType = 2;
            }
            Log.Debug("TianYuan WriteNewCard Start and kh is " + kh + " OpType is " + OPType +" and KZT is "+kzt);
            ret = StaticWriteCardInfo(11111111, int.Parse(kh), ql, cs, 1, OPType, com);
            Log.Debug("TianYuan WriteNewCard End and i is "+ret);
           
            return ret;
        }
        /// <summary>
        /// 航天卡实现，其他不用lihan
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
                return "TianYuan";
            }
        }

        #endregion
    }
}
