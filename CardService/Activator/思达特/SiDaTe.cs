using Com.Aote.Logs;
using ICard;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace Card
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class SiDaTe : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.SiDaTe");
        #region  动态库引入
        [DllImport("Mwic_32.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int auto_init(Int16 Port, UInt32 Baud);
        [DllImport("Mwic_32.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern Int16 ic_exit(int icdev);

        //检查卡类型
        [DllImport("icrw.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int DoCheckCard(int icdev);
        //开户写卡
        [DllImport("icrw.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int DoInitCard2(int icdev, StringBuilder MeterNo, Double SumofCharge, Int32 BuyTimes, Double StartVolume,Double CurrPrice, Double SetPrice, StringBuilder PriceSetTime, Int16 CardState, Int16 DevT,Double AlarmMoney, Double MaxOverMoney);
        //充值写卡
        [DllImport("icrw.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int DoSellGas2(int icdev, StringBuilder MeterNo, Double SumCharge, Int32 BuyTimes, Int16 DevT,Double CurrPrice, Double SetPrice, StringBuilder PriceSetTime,Double AlarmMoney, Double MaxOverMoney);
        //读卡
        [DllImport("icrw.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int DoReadUserCard2(int icdev, StringBuilder MeterNo, ref Double SumCharge, ref Int32 BuyTimes,ref Int16 CardState, ref Int16 DevT, ref Int16 IsInsert);
        //清卡
        [DllImport("icrw.dll", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi, SetLastError = true, ExactSpelling = true)]
        private static extern int DoClearCard(int icdev);
        #endregion

        public string Test()
        {
            return "思达特";
        }

        public string Name
        {
            get { return "思达特"; }
        }

        #region ICard Members
        /// <summary>
        /// 读卡
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kh">设备编号，10位字符</param>
        /// <param name="ql">气量，卡上有气不能购气</param>
        /// <param name="money">金额，按金额计算的卡，卡上有金额不能购气（未用）</param>
        /// <param name="cs">购气次数，以卡上购气次数为准。如果读不出，返回-1。返回-1后以数据库里为准</param>
        /// <param name="bkcs">补卡次数，以卡上补卡次数为准。如果读不出，返回-1。返回-1后以数据库里为准（未用）</param>
        /// <param name="dqdm">地区代码（未用）</param>
        /// <returns>返回0表示读卡成功</returns>
        /// 
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kh" + kh + "," + "ql:" + ql + "," + "money:" + money + "," + "cs:" + cs + "," + "bkcs:" + bkcs + "," + "yhh:" + yhh);
            kh = "";
            ql = 0;
            int BuyTimes = 0;
            Int16 CardState = 0;
            Int16 DevT = 0;
            Int16 IsInsert = 0;
            int icdev = auto_init(com, (UInt32)baud);  //打开设备
            if (icdev <= 0)
            {
                return -10;
            }
            StringBuilder sMeterNo = new StringBuilder(11);
            double dSumCharge = 0;
            int ret = DoReadUserCard2(icdev, sMeterNo, ref dSumCharge, ref BuyTimes, ref CardState, ref DevT, ref IsInsert);
            kh = sMeterNo.ToString().Trim();
            ql = (int)dSumCharge;
            cs = Convert.ToInt16(BuyTimes);
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kh" + kh + "," + "ql:" + ql + "," + "money:" + money + "," + "cs:" + cs + "," + "bkcs:" + bkcs + "," + "yhh:" + yhh);
            ic_exit(icdev);
            return ret;
        }
        /// <summary>
        /// 写新卡（即开户）
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kmm">卡密码，写卡后返回新密码（未用，可以随便传个值）</param>
        /// <param name="kzt">卡状态，0开户卡，1用户卡（未用）</param>s
        /// <param name="kh">设备编号，10位字符</param>
        /// <param name="dqdm">地区代码，从气表管理里取（未用）</param>
        /// <param name="yhh">用户号，档案中自己输入（未用）</param>
        /// <param name="tm">条码，传用户档案里的条码（未用）</param>
        /// <param name="ql">气量</param>
        /// <param name="csql">上次购气量，有些表需要传（未用）</param>
        /// <param name="ccsql">上上次购气量，有些表需要传（未用）</param>
        /// <param name="cs">购气次数（未用）</param>
        /// <param name="ljgql">当前表累计购气量（未用）</param>
        /// <param name="bkcs">补卡次数，用户档案里保存补卡次数（未用）</param>
        /// <param name="ljyql">累计用气量，有些表要累加原来用气量（未用）</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限，可以在气表管理中设置（未用）</param>
        /// <param name="tzed">透支额度，可以在气表管理中设置（未用）</param>
        /// <param name="sqrq">售气日期，格式为YYYYMMDD（未用）</param>
        /// <param name="cssqrq">上次售气日期，格式为YYYYMMDD（未用）</param>
        /// <param name="oldprice">旧单价，价格管理中取（未用）</param>
        /// <param name="newprice">新单价，价格管理中取（未用）</param>
        /// <param name="sxrq">生效日期，价格管理中取（未用）</param>
        /// <param name="sxbj">生效标记，0不生效，1生效，价格管理中取（未用）</param>
        /// <returns>返回0表示写新卡成功</returns>
        /// 
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," + "kzt:" + kzt + "," +
                       "kh:" + kh + "," + "dqdm:" + dqdm + "," + "yhh:" + yhh + "," + "tm:" + tm + "," +
                       "ql:" + ql + "," + "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                       "ljgql:" + ljgql + "," + "bkcs:" + bkcs + "," + "ljyql:" + ljyql + "," + "bjql:" + bjql + "," +
                       "czsx:" + czsx + "," + "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                       "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj:" + sxbj + "," +
                       "klx:" + klx + "," + "meterid:" + meterid);
            int icdev = auto_init(com, (UInt32)baud);  //打开设备
            if (icdev <= 0)
            {
                return -10;
            }
            StringBuilder MeterNo = new StringBuilder(kh);
            Double SumofCharge = 0;
            double dStartV = 0;
            double dCurrPrice = oldprice;
            double dSetPrice = newprice;
            double dAlarmMoney = bjql;
            double dMaxOverMoney = 0;
            int BuyTimes =0;
            StringBuilder sPriceSetTime = new StringBuilder(sxrq);
            Int16 DevT = Convert.ToInt16(dqdm);
            Int16 CardState = 0;
            int ret = DoInitCard2(icdev, MeterNo, SumofCharge, BuyTimes, dStartV, dCurrPrice, dSetPrice, sPriceSetTime, CardState, DevT, dAlarmMoney, dMaxOverMoney);
            kh = MeterNo.ToString();
            ljgql = Convert.ToInt32(SumofCharge);
            cs = Convert.ToInt16(BuyTimes);
            oldprice =(Int32)dCurrPrice;
            newprice = Convert.ToInt32(dSetPrice);
            sxrq = sPriceSetTime.ToString();
            kzt = Convert.ToInt16(CardState);
            bjql = (Int32)dAlarmMoney;
            Log.Debug("com:"+com+","+"baud:"+baud+","+"kmm:"+ kmm+","+"kzt:"+ kzt+","+
                       "kh:"+ kh+","+"dqdm:"+ dqdm+","+"yhh:"+ yhh+","+"tm:"+tm+","+
                       "ql:"+ ql+","+ "csql:"+csql+","+"ccsql:"+ccsql+","+"cs:"+ cs+","+
                       "ljgql:"+ljgql+","+"bkcs:"+bkcs+","+"ljyql:"+ ljyql+","+"bjql:"+bjql+","+
                       "czsx:"+czsx+","+"tzed:"+tzed+","+"sqrq:"+sqrq+","+"cssqrq:"+cssqrq+","+
                       "oldprice:"+oldprice+","+"newprice:"+newprice+","+"sxrq:"+sxrq+","+"sxbj:"+sxbj+","+
                       "klx:" + klx + "," + "meterid:" + meterid);
            ic_exit(icdev);
            return ret;
        }
        /// <summary>
        /// 写卡（即购气或冲正）
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kmm">卡密码，写卡后返回新密码（未用,可以随便传个值）</param>
        /// <param name="kh">设备编号，10位字符</param>
        /// <param name="dqdm">地区代码，从气表管理里取（未用）</param>
        /// <param name="ql">气量</param>
        /// <param name="csql">上次购气量，有些表需要传（未用）</param>
        /// <param name="ccsql">上上次购气量，有些表需要传（未用）</param>
        /// <param name="cs">购气次数</param>
        /// <param name="ljgql">当前表累计购气量（未用）</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限，可以在气表管理中设置（未用）</param>
        /// <param name="tzed">透支额度，可以在气表管理中设置（未用）</param>
        /// <param name="sqrq">售气日期，格式为YYYYMMDD（未用）</param>
        /// <param name="cssqrq">上次售气日期，格式为YYYYMMDD（未用）</param>
        /// <param name="oldprice">旧单价，价格管理中取（未用）</param>
        /// <param name="newprice">新单价，价格管理中取（未用）</param>
        /// <param name="sxrq">生效日期，价格管理中取（未用）</param>
        /// <param name="sxbj">生效标记，0不生效，1生效，价格管理中取（未用）</param>
        /// <returns>返回0表示写卡（即购气）成功</returns>
        /// <summary>
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," +
                      "kh:" + kh + "," + "dqdm:" + dqdm + "," + "ql:" + ql + "," +
                      "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                      "ljgql:" + ljgql + "," + "bjql:" + bjql + "," + "czsx:" + czsx + "," +
                      "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                      "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj" + sxbj);
            int icdev = auto_init(com, (UInt32)baud);  //打开设备
            if (icdev <= 0)
            {
                return -10;
            }
            StringBuilder sMeterNo = new StringBuilder(kh);
            StringBuilder sPriceSetTime = new StringBuilder(sxrq);
            double dSumofCharge = (double)(ljgql);
            double dCurrPrice = (double)oldprice;
            double dSetPrice = (double)newprice;
            double dAlarmMoney = (double)bjql;
            double dMaxOverMoney = 0;
            Int16 BuyTimes = 0;
            Int16 DevT = Convert.ToInt16(dqdm);
            int ret = DoSellGas2(icdev, sMeterNo, dSumofCharge, BuyTimes, DevT, dCurrPrice, dSetPrice, sPriceSetTime, dAlarmMoney, dMaxOverMoney);
            kh = sMeterNo.ToString();
            ljgql = Convert.ToInt32(dSumofCharge);
            cs = BuyTimes;
            oldprice = Convert.ToInt32(dCurrPrice);
            newprice = Convert.ToInt32(dSetPrice);
            sxrq = sPriceSetTime.ToString();
            bjql = Convert.ToInt32(dAlarmMoney);       
            Log.Debug("com:" + com + "," + "baud:" + baud + "," + "kmm:" + kmm + "," +
                     "kh:" + kh + "," + "dqdm:" + dqdm + "," + "ql:" + ql + "," +
                     "csql:" + csql + "," + "ccsql:" + ccsql + "," + "cs:" + cs + "," +
                     "ljgql:" + ljgql + "," + "bjql:" + bjql + "," + "czsx:" + czsx + "," +
                     "tzed:" + tzed + "," + "sqrq:" + sqrq + "," + "cssqrq:" + cssqrq + "," +
                     "oldprice:" + oldprice + "," + "newprice:" + newprice + "," + "sxrq:" + sxrq + "," + "sxbj" + sxbj);
            ic_exit(icdev);
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int icdev = auto_init(com, (UInt32)baud);  //打开设备
            if (icdev <= 0)
            {
                return -10;
            }
            int ret = DoClearCard(icdev);
            if (0 == ret)
            {
                Log.Debug("清卡成功！！！！");
            }
            else
            {
                Log.Debug("清卡失败！！！！");
            }
            ic_exit(icdev);
            return ret;
        }
        public int CheckGasCard(short com, int baud)
        {
            int icdev = auto_init(com, (UInt32)baud);  //打开设备
            if (icdev <= 0)
            {
                return -10;
            }
            int ret = DoCheckCard(icdev);
            if (0 == ret)
            {
                Log.Debug("是思达特卡！！！！");
            }
            else {
                Log.Debug("不是思达特卡！！！！");
            }
            ic_exit(icdev);
            return ret;
        }

        public int OpenCard(short com, int baud)
        {
            throw new NotImplementedException();
        }
        #endregion
        #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public SiDaTe()
        {
            ///<code>
            ///
            Errors.Add(-1, "不是本厂家的卡。");
            Errors.Add(-2, "读卡失败。");
            Errors.Add(-3, "写卡失败。");
            Errors.Add(-4, "核对密码错误。");
            Errors.Add(-5, "参数错误。");
            Errors.Add(-6, "需要插入新卡(空白卡)。");
            Errors.Add(-7, "需要插入用户卡。");
            Errors.Add(-8, "卡号不匹配。");
            Errors.Add(-9, "设备类型不匹配。");
            Errors.Add(-10, "打开设备失败。");
            Errors.Add(-11, "卡类型不匹配。");
            Errors.Add(-99, "未知错误。");

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
            catch (Exception)
            {
                return null;
            }
        }
        #endregion
    }
}
