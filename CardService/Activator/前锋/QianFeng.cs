using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class QianFeng : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.QianFeng");

        public string Test()
        {
            return "ruisen";
        }

        #region 前锋动态库导入
        //读卡
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_TestCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_TestCard(int Port, Int32 Baud, byte[] PstrCardId, ref byte CardSort, ref UInt32 BuyCnt, ref UInt32 PEnableGas, ref UInt32 CumGas, ref UInt32 lastGas, ref UInt32 AlarmGas, ref byte BigMeter, byte[] MeterStu, ref uint fg);
       
        //写新卡
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_NewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_NewCard(int Port, Int32 Baud, byte[] PstrCardId, UInt32 BuyGas, UInt32 AlarmGas, byte BigMeter, byte Passn, uint fg);
        
        //购气写卡函数
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_BuyGas", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_BuyGas(int Port, Int32 Baud, byte[] PstrCardId, UInt32 BuyCnt, UInt32 BuyGas, UInt32 AlarmGas, uint fg, byte BigMeter);
        
        //清卡函数
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_ClearCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_ClearCard(int Port, Int32 Baud);
        
        //补卡
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_MendCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_MendCard(int Port, Int32 Baud, byte[] PstrCardId, UInt32 BuyCnt, UInt32 BuyGas, UInt32 AlarmGas, byte BigMeter, byte Passn, uint fg);
        
        //写工具卡函数
        [DllImport("QFIC_316B.dll", EntryPoint = "QF_BulidCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int QF_BulidCard(int Port, Int32 Baud, byte[] PstrCardId, byte CardSort, UInt32 GasNum, UInt32 AlarmGas, byte Passn, byte Passn_New, uint fg);
        #endregion

        #region ICard Members

        /// <summary>
        /// 检测卡，检查是不是前锋卡(由于没有测卡函数，所以用读卡函数来实现)
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <returns>返回0表示测卡成功</returns>
        public int CheckGasCard(short com, int baud)
        {
            Log.Debug("QianFeng CheckGasCard Start");
            byte[] PstrCardId = new byte[9];
            byte CardSort = 0;
            UInt32 BuyCnt = 0;
            UInt32 PEnableGas = 0;
            UInt32 CumGas = 0;
            UInt32 lastGas = 0;
            UInt32 AlarmGas = 0;
            byte BigMeter = 0;
            byte[] MeterStu = new byte[3];
            uint fg = 0;
            string kh;
            string bzt;
            int i = QF_TestCard(com, baud, PstrCardId, ref CardSort, ref BuyCnt, ref PEnableGas, ref CumGas, ref lastGas, ref AlarmGas, ref BigMeter, MeterStu, ref fg);
            Log.Debug("QianFeng CheckGasCard end, return:"+i);
            PstrCardId[8] = 0;
            MeterStu[2] = 0;
            kh = Encoding.ASCII.GetString(PstrCardId, 0, 8);
            bzt = Encoding.ASCII.GetString(MeterStu, 0, 2);
        
            if (i == 0)
                Log.Debug("此卡是前锋卡！");
            else
                Log.Debug("此卡不是前锋卡！");
            return i;
        }

        /// <summary>
        /// 格式化卡（即擦卡）
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kmm">卡密码，写卡后返回新密码（未用，可以随便传值）</param>
        /// <param name="kh">卡号（未用）</param>
        /// <param name="dqdm">地区代码，从气表管理里取（未用）</param>
        /// <returns>返回0表示格式化卡成功，即擦卡成功</returns>
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Log.Debug("QianFeng FormatGasCard Start");
            int ret = QF_ClearCard(com, baud);
            Log.Debug("QianFeng FormatGasCard end, return:"+ret);
            if (ret == 0)
                Log.Debug("格式化卡成功！");
            else
                Log.Debug("格式化卡失败！");
            return ret;
        }
        public int OpenCard(Int16 com, Int32 baud)
        {
            return -1;
        }
        /// <summary>
        /// 写卡（即购气或冲正）
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kmm">卡密码，写卡后返回新密码（未用,可以随便传个值）</param>
        /// <param name="kh">卡号</param>
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
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("QianFeng WriteGasCard Start");
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            UInt32 BuyCnt = (UInt32)cs;
            UInt32 BuyGas = (UInt32)ql;
            UInt32 AlarmGas = (UInt32)bjql;
            uint fg = 4;
            byte BigMeter = 0x81;
            int ret = QF_BuyGas(com, baud, cardNO, BuyCnt, BuyGas, AlarmGas, fg, BigMeter);
            Log.Debug("QianFeng WriteGasCard end, return:" + ret);
            if (ret == 0)
                Log.Debug("购气成功！");
            else
                Log.Debug("购气失败！");
            return ret;
        }

        /// <summary>
        /// 写新卡（即开户）
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kmm">卡密码，写卡后返回新密码（未用，可以随便传个值）</param>
        /// <param name="kzt">卡状态，0开户卡，1用户卡（未用）</param>
        /// <param name="kh">卡号</param>
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
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            //发卡前先格式化卡
            UInt32 gql = (UInt32)ql;
            UInt32 bjql1 =(UInt32)bjql;
            byte bigmeter = 0x81;
            byte mmpc = 0;
            uint qxbz = 4;
            int ret = 1;
            Log.Debug("QianFeng FormatGasCard Start");
            try
            {
                 ret = QF_ClearCard(com, baud);
                Log.Debug("QianFeng FormatGasCard end, return:" + ret);
                if (ret == 0)
                    Log.Debug("清卡成功！");
                else
                    Log.Debug("清卡失败！");
                if (0 == kzt)
                {
                    Log.Debug("QianFeng WriteNewCard Start");
                    ret = QF_NewCard(com, baud, cardNO, gql, bjql1, bigmeter, mmpc, qxbz);
                    Log.Debug("QianFeng WriteNewCard end, return:" + ret);
                    if (ret == 0)
                        Log.Debug("写新用户卡成功！");
                    else
                        Log.Debug("写新用户卡失败！");
                }
                else
                {
                    UInt32 BuyCnt = (uint)cs;
                    Log.Debug("补卡开始");
                    ret = QF_MendCard(com, baud, cardNO, BuyCnt, gql, bjql1, bigmeter, mmpc, qxbz);
                    if (ret == 0)
                        Log.Debug("补卡成功！");
                    else
                        Log.Debug("补卡失败！");
                }
            }catch(Exception e)
            {
                Log.Debug("制新卡异常："+e.Message+"---"+e.StackTrace);
            }
            return ret;
        }

        public string Name
        {
            get
            {
                return "QianFeng";
            }
        }
      
        /// <summary>
        /// 读卡
        /// </summary>
        /// <param name="com">串口号，从0开始</param>
        /// <param name="baud">波特率</param>
        /// <param name="kh">卡号，根据卡号及厂家取档案</param>
        /// <param name="ql">气量，卡上有气不能购气</param>
        /// <param name="money">金额，按金额计算的卡，卡上有金额不能购气（未用）</param>
        /// <param name="cs">购气次数，以卡上购气次数为准。如果读不出，返回-1。返回-1后以数据库里为准</param>
        /// <param name="bkcs">补卡次数，以卡上补卡次数为准。如果读不出，返回-1。返回-1后以数据库里为准（未用）</param>
        /// <param name="dqdm">地区代码（未用）</param>
        /// <returns>返回0表示读卡成功</returns>
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
            Log.Debug("QianFeng ReadGasCard Start");
            byte[] PstrCardId = new byte[9];
            byte CardSort = 0;
            UInt32 BuyCnt = 0;
            UInt32 PEnableGas = 0;
            UInt32 CumGas = 0;
            UInt32 lastGas = 0;
            UInt32 AlarmGas = 0;
            byte BigMeter = 0;
            byte[] MeterStu = new byte[3];
            uint fg = 0;
            string bzt;
            int i = QF_TestCard(com, baud, PstrCardId, ref CardSort, ref BuyCnt, ref PEnableGas, ref CumGas, ref lastGas, ref AlarmGas, ref BigMeter, MeterStu, ref fg);
            Log.Debug("QianFeng ReadGasCard end, return:"+i);
            PstrCardId[8] = 0;
            MeterStu[2] = 0;
            kh = Encoding.ASCII.GetString(PstrCardId, 0, 8);
            bzt = Encoding.ASCII.GetString(MeterStu, 0, 2);
            ql = (Int32)PEnableGas;
            cs = (Int16)BuyCnt;
            if (i == 0)
            {
                Log.Debug("读卡成功！");
                Log.Debug("卡编号:" + kh + "    卡类型标识：" + CardSort + "    购气次数:" + cs + "    购气量:" + ql + "   气表累计购气量:" + CumGas + "    表内累计用气量:" + lastGas + "    报警气量:" + AlarmGas + "   大表标志:" + BigMeter + "    表状态:" + bzt + "    旗县标志:" + fg);
            }
            else
                Log.Debug("读卡失败！");
            return i;
        }
        #endregion
        #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public QianFeng()
        {
            ///<code>
            ///
            Errors.Add(1, "未检测到USB—KEY 。");
            Errors.Add(2, "购气量过大或者错误。");
            Errors.Add(3, "连接读卡器失败。");
            Errors.Add(4, "读卡器中无卡。");
            Errors.Add(5, "卡型不正确。");
            Errors.Add(6, "不是新卡。");
            Errors.Add(7, "校验卡密码失败。");
            Errors.Add(8, "读卡失败。");
            Errors.Add(9, "写卡失败。");
            Errors.Add(10, "写卡数据校验失败。");
            Errors.Add(11, "修改卡密码失败。");
            ///Else：系统出错
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
