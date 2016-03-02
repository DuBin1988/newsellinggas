using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public class TianSkyXin : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.TianSkyXin");
        //开卡函数
        [DllImport("Tancy_IC.dll", EntryPoint = "OpenCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int OpenCard(int port, int baud, StringBuilder meterFlag, StringBuilder userNo, StringBuilder userType, int alarmGas, int overLimit, int inputValue, int LargeFlowControl);
        //清卡函数
        [DllImport("Tancy_IC.dll", EntryPoint = "InitCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int InitCard(int port, int baud);
        //获取用户卡参数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetUserParam", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetUserParam(int port, int baud, StringBuilder meterFlag, StringBuilder userNo, StringBuilder userType, ref int alarmGas, ref int overLimit, ref int inputValue, ref int largeFlowControl);
        //设置用户参数
        [DllImport("Tancy_IC.dll", EntryPoint = "SetUserParam", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int SetUserParam(int port, int baud, StringBuilder meterFlag, StringBuilder userNo, StringBuilder userType, int alarmGas, int overLimit, int inputValue, int largeFlowControl);
        //读取卡ID函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetCardID", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetCardID(int port, int baud, StringBuilder cardID);
        //读取卡余额函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetCardGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetCardGas(int port, int baud, ref int cardGas);
        //读取卡充值次数函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetBuyTimes", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetBuyTimes(int port, int baud, ref int buyTimes);
        //读取DLL版本信息函数
        [DllImport("Tancy_IC.dll", EntryPoint = "GetVer", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetVer(StringBuilder verInfo);
        //购气函数
        [DllImport("Tancy_IC.dll", EntryPoint = "SaleGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int SaleGas(int port, int baud, int gas, int buyTimes);
        [DllImport("Tancy_IC.dll", EntryPoint = "GetError", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int GetErrors(int ret,ref StringBuilder errInfo);
      
        #region
        public string Test()
        {
            return "TianSkyXin";
        }
        public string Name
        {
            get { return "TianSkyXin"; }
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            //读卡号
            int port = Convert.ToInt32(com);
            StringBuilder cardID = new StringBuilder(10);
            StringBuilder meterFlag = new StringBuilder(16);
            StringBuilder userNo = new StringBuilder(12);
            StringBuilder userType = new StringBuilder(2);
            int alarmGas = 0;
            int overLimit = 0;
            int inputValue = 0;
            int largeFlowControl = 0;
            int result = GetUserParam(port, baud, meterFlag, userNo, userType, ref alarmGas, ref overLimit, ref inputValue, ref largeFlowControl);          
            Log.Debug("读卡参数的结果：" + result);
            if(0 == result)
            {
                kh = userNo.ToString();
            }
            //读卡气量
            int cardGas = 0;
           
            result =  GetCardGas(port,baud, ref cardGas);
            Log.Debug("读卡气量的结果：" + result);
            //读购气次数
            int buyTimes = 0;
            result = GetBuyTimes(port, baud, ref buyTimes);
            Log.Debug("读购气次数的结果：" + result);
            ql = cardGas;
            cs = Convert.ToInt16(buyTimes);
            Log.Debug("com:"+com+","+"baud:"+baud+","+"kh:"+ kh+","+"ql:"+ ql+","+"money:"+money+","+"cs"+cs+","+"bkcs:"+bkcs+","+"yhh:"+ yhh +","+"result:"+ result + ")");
            return result;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                              "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                              "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                              "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                              "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                              "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                              "klx:" + klx + "," + "meterid:" + meterid);
            int port = Convert.ToInt32(com);
            StringBuilder meterFlag = new StringBuilder("1000000000000001");
            StringBuilder userNo = new StringBuilder(kh);
            StringBuilder userType = new StringBuilder("10");
            int alarmGas = 50;
            int overLimit = 5000;
            int inputValue = 0;
            int LargeFlowControl = 99;
            
            int ret = InitCard(port, baud);
            Log.Debug("清卡结束：" + ret);
            if(0 == kzt)
            {
                Log.Debug("发卡开始，参数:卡号:" + kh + "--气量：" + ql + "--次数:" + cs + "卡类别：" + klx);
                int result = OpenCard(port, baud, meterFlag, userNo, userType, alarmGas, overLimit, inputValue, LargeFlowControl);
                Log.Debug("开卡--com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                             "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                             "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                             "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                             "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                             "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                             "klx:" + klx + "," + "meterid:" + meterid);
                Log.Debug("发卡结束：" + result);
                result = SaleGas(port, baud, ql, 1);
                Log.Debug("开卡---购气---com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                             "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                             "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                             "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                             "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                             "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                             "klx:" + klx + "," + "meterid:" + meterid+","+"result:"+result);
                return result;
            }
            else {
                Log.Debug("补卡开始,参数：卡号：" + kh  + "--次数:" + cs + "--气量：" + ql + "卡类别：" + klx );
                int result = OpenCard(port, baud, meterFlag, userNo, userType, alarmGas, overLimit, inputValue, LargeFlowControl);
                Log.Debug("补卡--购气--com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                            "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                            "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                            "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                            "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                            "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                            "klx:" + klx + "," + "meterid:" + meterid);
                Log.Debug("补卡结束：" + result);
                result = SaleGas(port, baud, ql, cs);
                Log.Debug("补卡--购气--com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                             "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                             "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                             "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                             "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                             "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                             "klx:" + klx + "," + "meterid:" + meterid+","+"result:"+result);
                return result;
            }
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," +
                      "kh:" + kh + "," + "dqdm:" + dqdm + "," + "ql:" + ql + "," +
                      "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                      "ljgql:" + ljgql + "," + "bjql:" + bjql + "," + "czsx:" + czsx + "," +
                      "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                      "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj" + sxbj);
            int port = Convert.ToInt32(com);
            if (ql > 0)
            {
                int result = SaleGas(port, baud, ql, cs);
                Log.Debug("购气结果: "+result);
                Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," +
                      "kh:" + kh + "," + "dqdm:" + dqdm + "," + "ql:" + ql + "," +
                      "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                      "ljgql:" + ljgql + "," + "bjql:" + bjql + "," + "czsx:" + czsx + "," +
                      "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                      "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj" + sxbj);
                return result;
            }
            else {
                int result = SaleGas(port, baud, 0, cs);
                Log.Debug("退气结果：" + result);
                Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," +
                      "kh:" + kh + "," + "dqdm:" + dqdm + "," + "ql:" + ql + "," +
                      "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                      "ljgql:" + ljgql + "," + "bjql:" + bjql + "," + "czsx:" + czsx + "," +
                      "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                      "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj" + sxbj);
                return result; 
            }
        }
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int port = Convert.ToInt32(com);
            int result = InitCard(port, baud);
            if (0 == result)
                Log.Debug("清卡成功！");
            else
                Log.Debug("清卡失败！");
            return result;
        }

        public int CheckGasCard(short com, int baud)
        {
            StringBuilder CardID = new StringBuilder();
            int result = GetCardID(com, baud, CardID);
            if (0 == result)
            {
                Log.Debug("这是天信的卡！");
            }
            else
            {
                Log.Debug("这不是天信的卡！");
            }
            return result;
        }
        public int OpenCard(short com, int baud)
        {
            return -1;
        }
        /// <summary>
        /// 根据错误码，该错误码不在GenericService的Errors数组中，数组中的错误，统一处理，此处只返回不在错误列表中的错误信息
        /// </summary>
        /// <param name="errCode">错误代码</param>
        /// <returns></returns>
        public string GetError(int errCode)
        {
            try
            {
                //如果改代码不存在，会引发异常
                StringBuilder errmessages = new StringBuilder(50);
                GetErrors(errCode, ref errmessages);
                return errmessages.ToString();
            }
            catch (Exception)
            {
                return null;
            }
        }
        #endregion
    }
}
