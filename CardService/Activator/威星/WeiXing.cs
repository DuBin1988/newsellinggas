using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;
using Newtonsoft.Json.Linq;


namespace Card
{
    public class WeiXing : ICard, IVerbose, INewParameters
    {
        //打开串口
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasInitPort", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ZJWX_GasInitPort(Int16 iPort, Int32 lBaud, byte[]results);
        //关闭串口
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasExitPort", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ZJWX_GasExitPort(Int32 lcdev);
        //读卡函数
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasReadCardInfo", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ZJWX_GasReadCardInfo(Int32 lcdev, byte[]PamaInfo, byte[] LadderInfo, byte[] results);
        //开户/写新卡
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasMakeCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ZJWX_GasMakeCard(Int32 lcdev,int iMeterType,int iCardType,byte[] CardNo,byte[]results);
        //写卡
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasWriteCardInfo", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ZJWX_GasWriteCardInfo(Int32 lcdev,byte[]PamaInfo,byte[]LadderInfo,byte[]results);
        //清卡
        [DllImport("ZJWXGas.dll", EntryPoint = "ZJWX_GasClearCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ZJWX_GasClearCard(Int32 lcdev, byte[] results);

        private string f_stairtype;
        private double f_stair1amount;//本次买气第一阶梯气量
        private double f_stair1price;
        private double f_stair2amount;//本次买气第二阶梯气量
        private double f_stair2price;
        private double f_stair3amount;//本次买气第三阶梯气量
        private double f_stair3price;
        private double money;  //金额
        private double stair1amount;//第一阶梯上限
        private double stair2amount;//第二阶梯上限
        private double stair3amount;//第三阶梯上限
        public string Test()
        {
            return "威星";
        }

        public string Name
        {
            get { return "威星"; }
        }
        private static Log Log = Log.GetInstance("Card.WeiXing");
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=("
              + com + "," + baud + "," + kh + "," + ql
              + money + "," + cs + "," + bkcs + "," + ")");

            Int16 iPort = 0;
            Int32 lBaud = 9600;
            byte[]PamaInfo = new byte[9];
            byte[] LadderInfo = new byte[13]; 
            byte[] results = new byte[20];
            Int32 lcdev = ZJWX_GasInitPort(iPort, lBaud, results);
            Log.Debug("ReadGasCard---句柄：" + lcdev);
            int ret = ZJWX_GasReadCardInfo(lcdev, PamaInfo,  LadderInfo,  results);
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=("
             + com + "," + baud + "," + kh + "," + ql
             + money + "," + cs + "," + bkcs + "," + ")");
            Log.Debug("ReadGasCard---读卡结果：" + ret); 
            ZJWX_GasExitPort(lcdev);
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
               + com + "," + baud + "," + kmm + "," + kzt
               + kh + "," + dqdm + "," + yhh + "," + tm
               + ql + "," + csql + "," + ccsql + "," + cs
               + ljgql + "," + bkcs + "," + ljyql + "," + bjql
               + czsx + "," + tzed + "," + sqrq + "," + cssqrq
               + oldprice + "," + newprice + "," + sxrq + "," + sxbj
               + ")");
            Int16 iPort = 0;
            Int32 lBaud = 9600;
            int iMeterType = 2; //  1 代表气量表；  2 代表金额表；
            byte[]results = new byte[20];
            Int32 lcdev = ZJWX_GasInitPort(iPort, lBaud, results);
            Log.Debug("WriteNewCard---句柄：" + lcdev);
            byte[] CardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            int ret1 = ZJWX_GasClearCard(lcdev, results);
            Log.Debug("WriteNewCard---清卡:" + ret1);
            if (0 == kzt)
            {
                int ret = ZJWX_GasMakeCard(lcdev, iMeterType, klx, CardNo, results);
                Log.Debug("WriteNewCard---制卡："+ret);

                byte[] PamaInfoo = new byte[9];
                byte[] LadderInfoo = new byte[13]; 
                int rett = ZJWX_GasReadCardInfo(lcdev, PamaInfoo, LadderInfoo, results);
                string message = PamaInfoo[0] + "|" + klx + "|" + kh + "|" + ql + "|" + 9999 + "|" + tzed + "|" + bjql + "|" + 99;
                byte[] PamaInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(message);
                //金额表
                if (2 == PamaInfo[0])
               {
                   string TimeStart = "";
                   string TimeEnd = "";
                   double PriceOne = 0;
                   int GasOne = 0;
                   double PriceTwo = 0;
                   int GasTwo = 0;
                   double PriceThree = 0;
                   int GasThree = 0;
                   double PriceFour = 0;
                   int GasFour = 0;
                   double PriceFive = 0;
                   int GasFive = 0;
                   string messages = 1 + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                   byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                   ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                   Log.Debug("金额表写卡结果："+ret);
                }
                //气量表
                if (1 == PamaInfo[0])
                {
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = 1 + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    Log.Debug("气量表写卡结果：" + ret);
                }
                ZJWX_GasExitPort(lcdev);
                return ret;
            }
            else {
                int ret = ZJWX_GasMakeCard(lcdev, iMeterType, klx, CardNo, results);
                Log.Debug("WriteNewCard---制卡：" + ret);

                byte[] PamaInfoo = new byte[9];
                byte[] LadderInfoo = new byte[13];
                int rett = ZJWX_GasReadCardInfo(lcdev, PamaInfoo, LadderInfoo, results);
                string message = PamaInfoo[0] + "|" + klx + "|" + kh + "|" + ql + "|" + 9999 + "|" + tzed + "|" + bjql + "|" + 99;
                byte[] PamaInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(message);
                //金额表
                if (2 == PamaInfo[0])
                {
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = cs + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    Log.Debug("金额表写卡结果：" + ret);
                }
                //气量表
                if (1 == PamaInfo[0])
                {
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = cs + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    Log.Debug("气量表写卡结果：" + ret);
                }
                ZJWX_GasExitPort(lcdev);
                return ret;
            }
           
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
           + com + "," + baud + "," + kmm + "," + kh
           + dqdm + "," + ql + "," + csql + "," + ccsql
           + cs + "," + ljgql + "," + bjql + "," + czsx
           + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
           + newprice + "," + sxrq + "," + sxbj
           + ")");
            Int16 iPort = 0;
            Int32 lBaud = 9600;
            byte[] results = new byte[20];
            Int32 lcdev = ZJWX_GasInitPort(iPort, lBaud, results);
            Log.Debug("WriteGasCard---句柄：" + lcdev);
            byte[] PamaInfoo = new byte[9];
            byte[] LadderInfoo = new byte[13];
            int rett = ZJWX_GasReadCardInfo(lcdev, PamaInfoo, LadderInfoo, results);
            Log.Debug("WriteGasCard---读卡结果：" + rett);
            byte[] CardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            int ret = -1;
            //充值
            
            if (ql >0)
            {
                string message = PamaInfoo[0] + "|" + PamaInfoo[1] + "|" + kh + "|" + ql + "|" + 9999 + "|" + tzed + "|" + bjql + "|" + 99;
                byte[] PamaInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(message);
                //金额表
                if (2 == PamaInfoo[0])
                {
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = ((int)cs+1) + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    Log.Debug("金额表：WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
           + com + "," + baud + "," + kmm + "," + PamaInfo[2]
           + dqdm + "," + PamaInfo[3] + "," + csql + "," + ccsql
           + LadderInfo[0] + "," + ljgql + "," + PamaInfo[7] + "," + czsx
           + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
           + newprice + "," + sxrq + "," + sxbj
           + ")");
                }
                //气量表
                if (1 == PamaInfoo[0])
                {
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = ((int)cs + 1) + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    Log.Debug("气量表：WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
           + com + "," + baud + "," + kmm + "," + PamaInfo[2]
           + dqdm + "," + PamaInfo[3] + "," + csql + "," + ccsql
           + LadderInfo[0] + "," + ljgql + "," + PamaInfo[7] + "," + czsx
           + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
           + newprice + "," + sxrq + "," + sxbj
           + ")");
                }
                ZJWX_GasExitPort(lcdev);
               return ret;
            }
                //退气
            else
            {
                //金额表
                if (2 == PamaInfoo[0])
                {
                    string message = PamaInfoo[0] + "|" + PamaInfoo[1] + "|" + kh + "|" + 0 + "|" + 9999 + "|" + tzed + "|" + bjql + "|" + 99;
                    byte[] PamaInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(message);
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = ((int)cs - 1) + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    return ret;
                }
                //气量表
                if (1 == PamaInfoo[0])
                {
                    string message = PamaInfoo[0] + "|" + PamaInfoo[1] + "|" + kh + "|" + 0 + "|" + 9999 + "|" + tzed + "|" + bjql + "|" + 99;
                    byte[] PamaInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(message);
                    string TimeStart = "";
                    string TimeEnd = "";
                    double PriceOne = 0;
                    int GasOne = 0;
                    double PriceTwo = 0;
                    int GasTwo = 0;
                    double PriceThree = 0;
                    int GasThree = 0;
                    double PriceFour = 0;
                    int GasFour = 0;
                    double PriceFive = 0;
                    int GasFive = 0;
                    string messages = ((int)cs - 1) + "|" + TimeStart + "|" + TimeEnd + "|" + PriceOne + "|" + GasOne + "|" + PriceTwo + "|" + GasTwo + "|" + PriceThree + "|" + GasThree + "|" + PriceFour + "|" + GasFour + "|" + PriceFive + "|" + GasFive;
                    byte[] LadderInfo = System.Text.Encoding.GetEncoding(1252).GetBytes(messages);
                    ret = ZJWX_GasWriteCardInfo(lcdev, PamaInfo, LadderInfo, results);
                    return ret;
                }
                ZJWX_GasExitPort(lcdev);
                 return ret;
            }
            return 0;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Int16 iPort = 0;
            Int32 lBaud = 9600;
            byte []results = new byte[10];
            Int32 lcdev = ZJWX_GasInitPort(iPort, lBaud, results);
            Log.Debug("FormatGasCard---句柄：" + lcdev);
            int ret = ZJWX_GasClearCard(lcdev,results);
            Log.Debug("FormatGasCard---清卡结果：" + ret);
            ZJWX_GasExitPort(lcdev);
            return ret;
        }

        public int CheckGasCard(short com, int baud)
        {
            return -1; ;
        }

        public int OpenCard(short com, int baud)
        {
            return -1; ;
        }
          #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public WeiXing()
        {
            ///<code>
            ///
            ///
            Errors.Add(2, "充值量大于表存上限。");
            Errors.Add(3, "非法卡片 铜片。");
            Errors.Add(4, "未设置过插入非设置卡。");
            Errors.Add(5, "读卡未完成就拔出卡片。");
            Errors.Add(6, "卡号核对出错。");
            Errors.Add(7, "读取数据不完整。");
            Errors.Add(8, "读卡密码错误。");

            Errors.Add(20, "非本机卡片。");
            Errors.Add(21, "未知卡片。");
            Errors.Add(22, "用户卡卡号不对。");
            Errors.Add(23, "单价核对错误。");
            Errors.Add(24, "单价更新次数不对。");
            Errors.Add(25, "充值序号错误。");
            Errors.Add(26, "充值总量为0。");

            Errors.Add(27, "错误的加密方式。");
            Errors.Add(28, "错误的cmc。");
            Errors.Add(29, "换卡次数错误。");
            Errors.Add(30, "未清除检测卡。");
            Errors.Add(31, "转存卡无数据。");
            Errors.Add(32, "表中有数据，卡中也有数据（转存时）。");
            Errors.Add(33, "配置卡被锁。");

            Errors.Add(34, "错误的卡类型。");
            Errors.Add(35, "非新卡插入新表。");
            Errors.Add(36, "存入的价格信息已满。");
            Errors.Add(50, "未启用时反插卡。");
            Errors.Add(51, "正常使用反插卡。");
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
                return Errors[errCode];
            }
            catch(Exception)
            {
                return null;
            }
        }
        #endregion

        #region INewParameters 实现 增加新参数
        public void SetParam(JObject json)
        {
            try
            {
                f_stair1amount = double.Parse(json["stairgas1"].ToString());
                f_stair1price = double.Parse(json["stairprice1"].ToString());
                f_stair2amount = double.Parse(json["stairgas2"].ToString());
                f_stair2price = double.Parse(json["stairprice2"].ToString());
                f_stair3amount = double.Parse(json["stairgas3"].ToString());
                f_stair3price = double.Parse(json["stairprice3"].ToString());
            }
            catch (Exception)
            {
                
                throw;
            }
        }
        #endregion
    }
}
