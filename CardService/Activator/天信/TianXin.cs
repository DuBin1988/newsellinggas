using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class TianXin : ICard
    {
        private static Log Log = Log.GetInstance("Card.TianXin");

        public string Test()
        {
            return "天鑫";
        }


        #region 天源动态库导入

        #region 动态库导入
        //检查卡函数--以读取卡号 做判卡依据


        //读卡号函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetCardID", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetCardID(int com, int baud, StringBuilder CardID);
        //读卡内气量函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetCardGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetCardGas(int com, int baud, out int CardGas);
        //读写卡次数函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetBuyTimes", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetBuyTimes(int com, int baud, out int buyTimes);
        //开户函数
        [DllImport("Tancy_IC.dll", EntryPoint = "OpenCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int OpenCard(int port, int baud, StringBuilder meterFlag,
             StringBuilder userNo, StringBuilder userType, int alarmGas, int
            overLimit, int inputValue, int largeFlowControl);
        //清卡
        [DllImport("Tancy_IC.dll", EntryPoint = "InitCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int InitCard(int port, int baud);
        //购气函数
        [DllImport("Tancy_IC.dll", EntryPoint = "SaleGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int SaleGas(int port, int band, int gas, int buyTimes);
        #endregion
        #endregion


        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {

            Log.Debug("TianXin Clear Card start");
            int ret = InitCard(com, baud);
            Log.Debug("TianXin clear card end and ret is  " + ret);
            return ret;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){
              StringBuilder CardID=new StringBuilder();
              Log.Debug("ReadCardID instead Check start");
              int ret= GetCardID( com,  baud, CardID);
              Log.Debug("ReadCardID instead Check End and ret is " + ret);
              return ret;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
          //读取卡号

           StringBuilder CardId = new StringBuilder();
           int ret = GetCardID(com, baud, CardId);
           Log.Debug("GetCardID end and CardID  is " + CardId + " and ret is " + ret);
          //读取气量
            ret= GetCardGas( com, baud , out  ql);
            Log.Debug("GetCardGas end and ql  is " + ql + " and ret is " + ret);
          //读取充值次数
            int cs1 = 0;
          ret=  GetBuyTimes( com,  baud, out  cs1);
          Log.Debug("GetBuyTimes end and cs1  is " + cs1 + " and ret is " + ret);
          cs = (short)cs1;
          return ret;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("TianXin Sale Gas Start and com is " + com + "ql is " + ql + " cs is " + cs);
           int i= SaleGas( com, baud, ql, cs);
           Log.Debug("TianXin Sale Gas end and i is "+i);
            return i;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            StringBuilder meterFlag=new StringBuilder("1000000000000001");
            //卡号设置为12位
            StringBuilder userNo=new StringBuilder(kh);
            StringBuilder userType=new StringBuilder("民用");
         
            int alarmGas=50;
            int overLimit=50000;
            int  inputValue=0;
            int largeFlowControl=99;
            int i = OpenCard(com, baud, meterFlag,
userNo, userType, alarmGas,
overLimit, inputValue, largeFlowControl);

            return i;
//           port 读卡器在用端口号
//baud 波特率，一般默认都是 9600
//meterFlag 燃气表号
//userNo 用户号
//userType 用户类型
//alarmGase 报警气量
//overLimit 透支限额
//inputValue 充值限额
//largeFlowControl 大流量门限
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
                return "TianXin";
            }
        }

        #endregion
    }
}
