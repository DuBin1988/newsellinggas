using Com.Aote.Logs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using ICard;

namespace Card
{
    public class CangNan : ICard,IVerbose
    {
       private static Log Log = Log.GetInstance("Card.CangNan");
       // private static log4net.ILog Log = log4net.LogManager.GetLogger(typeof(CangNan));
        public string Test()
        {
            return "CangNan";
        }

        public string Name
        {
            get
            {
                return "CangNan";
            }
        }

        #region 苍南动态库导入

        //读卡
        [DllImport("CNCPUCard.DLL", EntryPoint = "ReadCNCard_V2", SetLastError = true,
            CharSet = CharSet.Ansi, ExactSpelling = false,
            CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCard(int ReaderType, int port, int baud, ref StringBuilder AreaCode,
                                          ref StringBuilder CardId, ref StringBuilder UserId, ref int SaleWay,
                                          ref long CardMoney, ref int CardGasCount, ref long UsedMoney,
                                          ref long BoughtMoney, ref long MeterMoney,
                                          ref int MeterGasCount, ref StringBuilder CardCount,
                                          ref StringBuilder MeterType, ref int CardType, ref long AlarmValue,
                                          ref long InputValue, ref long OverValue, ref double Price,
                                          ref StringBuilder PriceStates, ref StringBuilder TimeTypeState,
                                          ref int StepCycle, ref StringBuilder PriceStartTime, ref long StepUseSpare1,
                                          ref double StepPrice1, ref long StepUseSpare2, ref double StepPrice2,
                                          ref StringBuilder AdjustTime1, ref int Duration1, ref double DurationPrice1,
                                          ref StringBuilder AdjustTime2, ref int Duration2, ref double DurationPrice2,
                                          ref StringBuilder PriceVersion, ref int err);

        //开户
        [DllImport("CNCPUCard.DLL", EntryPoint = "MadeCNCard_V2", SetLastError = true,
            CharSet = CharSet.Ansi, ExactSpelling = false,
            CallingConvention = CallingConvention.StdCall)]
        public static extern int MadeCard(int ReaderType, int port, int baud, StringBuilder AreaCode,
                                          StringBuilder CardId, StringBuilder UserId, int SaleWay,
                                          int CardMoney, int CardGasCount, int CardCount, StringBuilder MeterType,
                                          int AlarmValue, int InputValue,
                                          int OverValue, double Price, StringBuilder PriceStates,
                                          StringBuilder TimeTypeState, int StepCycle,
                                          StringBuilder PriceStartTime, int StepUseSpare1, double StepPrice1,
                                          int StepUseSpare2, double StepPrice2,
                                          StringBuilder AdjustTime1, int Duration1,
                                          double DurationPrice1, StringBuilder AdjustTime2, int Duration2,
                                          double DurationPrice2, StringBuilder PriceVersion, int opCode, ref int err);

        //购气
        [DllImport("CNCPUCard.DLL", EntryPoint = "WriteCNCard_V2", SetLastError = true,
         CharSet = CharSet.Ansi, ExactSpelling = false,
         CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteCard(int ReaderType, int port, int baud, StringBuilder AreaCode,
                                          StringBuilder CardId, StringBuilder UserId, int SaleWay,
                                          int CardMoney, int CardGasCount, int CardCount, StringBuilder MeterType,
                                          int AlarmValue, int InputValue,
                                          int OverValue, double Price, StringBuilder PriceStates,
                                          StringBuilder TimeTypeState, int StepCycle,
                                          StringBuilder PriceStartTime, int StepUseSpare1, double StepPrice1,
                                          int StepUseSpare2, double StepPrice2,
                                          StringBuilder AdjustTime1, int Duration1,
                                          double DurationPrice1, StringBuilder AdjustTime2, int Duration2,
                                          double DurationPrice2, StringBuilder PriceVersion, int opCode, ref int err);

        //清卡
        [DllImport("CNCPUCard.DLL", EntryPoint = "ClearCNCard_V2", SetLastError = true,
            CharSet = CharSet.Ansi, ExactSpelling = false,
            CallingConvention = CallingConvention.StdCall)]
        public static extern int ClearCard(int ReaderType, int port, int baud, ref   int err);

        //判卡
        [DllImport("CNCPUCard.DLL", EntryPoint = "IsCNCard_V2", SetLastError = true,
            CharSet = CharSet.Ansi, ExactSpelling = false,
            CallingConvention = CallingConvention.StdCall)]
        public static extern int IsCNCard(int ReaderType, Int32 port, Int32 baud, ref StringBuilder meterytpe, ref int cardtype, ref Int32 err);

        #endregion

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
            Int32 ret = -1;
            try
            {
                Log.Debug("CangNan ReadGasCard start:" + System.DateTime.Now);
                int port = (int)com;
                StringBuilder AreaCode = new StringBuilder(4 - 1);
                StringBuilder CardId = new StringBuilder(16 - 1);
                StringBuilder UserId = new StringBuilder(12 - 1);
                int SaleWay = 0;
                long CardMoney = 0;
                int CardGasCount = 0;
                long UsedMoney = 0;
                long BoughtMoney = 0;
                long MeterMoney = 0;
                int MeterGasCount = 0;
                StringBuilder MeterType = new StringBuilder(2 - 1);
                StringBuilder CardCount = new StringBuilder(2 - 1);
                int CardType = 0;
                long AlarmValue = 100;
                long InputValue = 999999;
                long OverValue = 0;
                double Price = 0;
                StringBuilder PriceStates = new StringBuilder(4 - 1);
                StringBuilder TimeTypeState = new StringBuilder(4 - 1);
                int StepCycle = 0;
                StringBuilder PriceStartTime = new StringBuilder(8 - 1);
                long StepUseSpare1 = 0;
                double StepPrice1 = 0;
                long StepUseSpare2 = 0;
                double StepPrice2 = 0;
                StringBuilder PriceVersion = new StringBuilder(4 - 1);
                StringBuilder AdjustTime1 = new StringBuilder(8 - 1);
                int Duration1 = 0;
                double DurationPrice1 = 0;
                StringBuilder AdjustTime2 = new StringBuilder(8 - 1);
                int Duration2 = 0;
                double DurationPrice2 = 0;
                int err = 0;
                ret = ReadCard(
                    4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                    port,   //端口号
                    baud, //波特率
                    ref AreaCode,   //区域代号
                    ref CardId,     //卡号
                    ref UserId,     //用户号
                    ref SaleWay,    //售气方式

                    ref CardMoney, //卡内余量
                    ref CardGasCount, //卡内购气次数
                    ref UsedMoney, //表内累计用气量
                    ref BoughtMoney, //表内累计购气量
                    ref MeterMoney, //表内剩余量

                    ref MeterGasCount, //表内购气次数
                    ref CardCount, //补卡次数
                    ref MeterType, //气表类型
                    ref CardType, //卡类型
                    ref AlarmValue, //报警气量

                    ref InputValue, //充值限

                    ref OverValue, ref Price, //透支量
                    ref PriceStates, //常规单价
                    ref TimeTypeState, //
                    ref StepCycle,

                    ref PriceStartTime,
                    ref StepUseSpare1,
                    ref StepPrice1,
                    ref StepUseSpare2,

                    ref StepPrice2,
                    ref AdjustTime1,
                    ref Duration1,
                    ref DurationPrice1,
                    ref AdjustTime2,

                    ref Duration2,
                    ref DurationPrice2,
                    ref PriceVersion,
                    ref err);
                if (err == 0)
                {
                    Log.Debug("CangNan读卡成功！");
                    kh = CardId.ToString();
                    ql = (int)CardMoney;
                    cs = (short)CardGasCount;
                    Log.Debug("kh: " + kh + "--ql: " + ql + "--cs: " + cs);
                }
                else
                    Log.Debug("CangNan读卡失败！");
            }
            catch (Exception e)
            {
                Log.Debug("CangNan读卡异常:" + e.Message + "--" + e.StackTrace);
            }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            Int32 ret = -1;
            try
            {
                Log.Debug("CangNan WriteNewCard start:" + System.DateTime.Now);
                Log.Debug("CangNan 发卡前清卡 start:" + System.DateTime.Now);
                int port = (int)com;
                int err = 0;
                ret = ClearCard(4, port, baud, ref err);
                if (0 == err)
                {
                    Log.Debug("CangNan发卡前清卡成功！");
                }
                else
                {
                    Log.Debug("CangNan发卡前清卡失败！");
                }
                Log.Debug("CangNan WriteNewCard 参数:" + "port:" + port + "--kh:" + kh + "--ql:" + ql + "--bkcs:" + bkcs + "--cs:" + cs);
                int flag = 1;
                if (0 == ql)
                {
                    flag = 0;
                }
                if (0 == kzt)
                {
                    err = 0;
                    ret = MadeCard(
                        4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                        port,   //端口号
                        baud, //波特率
                        new StringBuilder("0001"),   //区域代号, 4位，固定 
                        new StringBuilder(kh),     //卡号，16位
                        new StringBuilder("123456789012"),     //用户化，固定，12位
                        2,    //售气方式，2- 气量
                        ql,      //购气量
                        flag,   //购气次数，第一次为1
                        0,  //补卡次数，0-255次 
                        new StringBuilder("04"),  //气表类型，工业CPU卡表V2.1(金额)：03工业CPU卡表V2.0(气量)：04
                        5, //报警气量
                        999999, //充值上限
                        0,  //透支量
                        1,  //常规单价, 气量表没用
                        new StringBuilder("0000"),    //价格启动状态码, 气量表不用
                        new StringBuilder("2222"),  //时间单位状态码, 气量表不用
                        1,  //阶梯价周期时长, 气量表不用
                        new StringBuilder("13121314"),     //启用时间, , 气量表不用
                        1,  //阶梯用量1
                        1, //阶梯价格1
                        1, //阶梯用量2
                        1, //阶梯价格2
                        new StringBuilder("13121314"),    //调价时间1
                        1,      //时长1
                        1, //时长价格1
                        new StringBuilder("13121314"), //调价时间2
                        1, //时长2
                        1, //时长价格2
                        new StringBuilder("0001"), //价格体系版本号, 固定
                        16879,  //操作代码，固定
                        ref err);
                    if (0 == err)
                    {
                        Log.Debug("CangNan写新卡成功！");
                    }
                    else
                    {
                        Log.Debug("CangNan写新卡成失败！");
                    }
                }
                else
                {
                    err = 0;
                    ret = MadeCard(
                        4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                        port,   //端口号
                        baud, //波特率
                        new StringBuilder("0001"),   //区域代号, 4位，固定 
                        new StringBuilder(kh),     //卡号，16位
                        new StringBuilder("123456789012"),     //用户化，固定，12位
                        2,    //售气方式，2- 气量
                        ql,      //购气量
                        cs,   //购气次数，第一次为1
                        bkcs,  //补卡次数，0-255次 
                        new StringBuilder("04"),  //气表类型，工业CPU卡表V2.1(金额)：03工业CPU卡表V2.0(气量)：04
                        5, //报警气量
                        999999, //充值上限
                        0,  //透支量
                        1,  //常规单价, 气量表没用
                        new StringBuilder("0000"),    //价格启动状态码, 气量表不用
                        new StringBuilder("2222"),  //时间单位状态码, 气量表不用
                        1,  //阶梯价周期时长, 气量表不用
                        new StringBuilder("13121314"),     //启用时间, , 气量表不用
                        1,  //阶梯用量1
                        1, //阶梯价格1
                        1, //阶梯用量2
                        1, //阶梯价格2
                        new StringBuilder("13121314"),    //调价时间1
                        1,      //时长1
                        1, //时长价格1
                        new StringBuilder("13121314"), //调价时间2
                        1, //时长2
                        1, //时长价格2
                        new StringBuilder("0001"), //价格体系版本号, 固定
                        783,  //操作代码，固定
                        ref err);
                    if (0 == err)
                    {
                        Log.Debug("CangNan写新卡成功！");
                    }
                    else
                    {
                        Log.Debug("CangNan写新卡成失败！");
                    }
                }
            }
            catch (Exception e)
            {
                Log.Debug("CangNan写新卡异常:" + e.Message + "--" + e.StackTrace);
            }
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Int32 ret = -1;
            string bkcs = "0";
            try
            {
                int port = (int)com;
                Log.Debug("CangNan ReadGasCard start:" + System.DateTime.Now);
                StringBuilder AreaCode = new StringBuilder(4 - 1);
                StringBuilder CardId = new StringBuilder(16 - 1);
                StringBuilder UserId = new StringBuilder(12 - 1);
                int SaleWay = 0;
                long CardMoney = 0;
                int CardGasCount = 0;
                long UsedMoney = 0;
                long BoughtMoney = 0;
                long MeterMoney = 0;
                int MeterGasCount = 0;
                StringBuilder MeterType = new StringBuilder(2 - 1);
                StringBuilder CardCount = new StringBuilder(2 - 1);
                int CardType = 0;
                long AlarmValue = 100;
                long InputValue = 999999;
                long OverValue = 0;
                double Price = 0;
                StringBuilder PriceStates = new StringBuilder(4 - 1);
                StringBuilder TimeTypeState = new StringBuilder(4 - 1);
                int StepCycle = 0;
                StringBuilder PriceStartTime = new StringBuilder(8 - 1);
                long StepUseSpare1 = 0;
                double StepPrice1 = 0;
                long StepUseSpare2 = 0;
                double StepPrice2 = 0;
                StringBuilder PriceVersion = new StringBuilder(4 - 1);
                StringBuilder AdjustTime1 = new StringBuilder(8 - 1);
                int Duration1 = 0;
                double DurationPrice1 = 0;
                StringBuilder AdjustTime2 = new StringBuilder(8 - 1);
                int Duration2 = 0;
                double DurationPrice2 = 0;
                int err = 0;
                ret = ReadCard(
                    4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                    port,   //端口号
                    baud, //波特率
                    ref AreaCode,   //区域代号
                    ref CardId,     //卡号
                    ref UserId,     //用户号
                    ref SaleWay,    //售气方式

                    ref CardMoney, //卡内余量
                    ref CardGasCount, //卡内购气次数
                    ref UsedMoney, //表内累计用气量
                    ref BoughtMoney, //表内累计购气量
                    ref MeterMoney, //表内剩余量

                    ref MeterGasCount, //表内购气次数
                    ref CardCount, //补卡次数
                    ref MeterType, //气表类型
                    ref CardType, //卡类型
                    ref AlarmValue, //报警气量

                    ref InputValue, //充值限

                    ref OverValue, ref Price, //透支量
                    ref PriceStates, //常规单价
                    ref TimeTypeState, //
                    ref StepCycle,

                    ref PriceStartTime,
                    ref StepUseSpare1,
                    ref StepPrice1,
                    ref StepUseSpare2,

                    ref StepPrice2,
                    ref AdjustTime1,
                    ref Duration1,
                    ref DurationPrice1,
                    ref AdjustTime2,

                    ref Duration2,
                    ref DurationPrice2,
                    ref PriceVersion,
                    ref err);
                if (err == 0)
                {
                    Log.Debug("CangNan读卡成功！");
                    bkcs = CardCount.ToString();
                }
                else
                    Log.Debug("CangNan读卡失败！");
                if (ql < 0)
                {
                    Log.Debug("CangNan WriteGasCard start:" + System.DateTime.Now);
                    err = 0;
                    ret = WriteCard(
                        4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                        port,   //端口号
                        baud, //波特率
                        new StringBuilder("0001"),   //区域代号, 4位，固定 
                        new StringBuilder(kh),     //卡号，16位
                        new StringBuilder("123456789012"),     //用户化，固定，12位
                        2,    //售气方式，2- 气量
                        0,      //购气量
                        cs,   //购气次数，第一次为1
                        int.Parse(bkcs.Substring(1, bkcs.Length - 1)),  //补卡次数，0-255次 
                        new StringBuilder("04"),  //气表类型，工业CPU卡表V2.1(金额)：03工业CPU卡表V2.0(气量)：04
                        5, //报警气量
                        999999, //充值上限
                        0,  //透支量
                        1,  //常规单价, 气量表没用
                        new StringBuilder("0000"),    //价格启动状态码, 气量表不用
                        new StringBuilder("1111"),  //时间单位状态码, 气量表不用
                        1,  //阶梯价周期时长, 气量表不用
                        new StringBuilder("13121314"),     //启用时间, , 气量表不用
                        1,  //阶梯用量1
                        1, //阶梯价格1
                        1, //阶梯用量2
                        1, //阶梯价格2
                        new StringBuilder("13121314"),    //调价时间1
                        1,      //时长1
                        1, //时长价格1
                        new StringBuilder("13121314"), //调价时间2
                        1, //时长2
                        1, //时长价格2
                        new StringBuilder("0001"), //价格体系版本号, 固定
                        1282,  //操作代码，固定
                        ref err);
                    if (0 == err)
                    {
                        Log.Debug("CangNan退气成功！");
                    }
                    else
                    {
                        Log.Debug("CangNan退气失败！");
                    }
                }
                else
                {
                    Log.Debug("CangNan WriteGasCard start:" + System.DateTime.Now);
                    err = 0;
                    ret = WriteCard(
                        4,    //读卡器类型，1-握奇W1560，或者握奇W1564, 巴彦用的。 
                        port,   //端口号
                        baud, //波特率
                        new StringBuilder("0001"),   //区域代号, 4位，固定 
                        new StringBuilder(kh),     //卡号，16位
                        new StringBuilder("123456789012"),     //用户化，固定，12位
                        2,    //售气方式，2- 气量
                        ql,      //购气量
                        cs,   //购气次数，第一次为1
                        int.Parse(bkcs.Substring(1, bkcs.Length - 1)),  //补卡次数，0-255次 
                        new StringBuilder("04"),  //气表类型，工业CPU卡表V2.1(金额)：03工业CPU卡表V2.0(气量)：04
                        5, //报警气量
                        999999, //充值上限
                        0,  //透支量
                        1,  //常规单价, 气量表没用
                        new StringBuilder("0000"),    //价格启动状态码, 气量表不用
                        new StringBuilder("1111"),  //时间单位状态码, 气量表不用
                        1,  //阶梯价周期时长, 气量表不用
                        new StringBuilder("13121314"),     //启用时间, , 气量表不用
                        1,  //阶梯用量1
                        1, //阶梯价格1
                        1, //阶梯用量2
                        1, //阶梯价格2
                        new StringBuilder("13121314"),    //调价时间1
                        1,      //时长1
                        1, //时长价格1
                        new StringBuilder("13121314"), //调价时间2
                        1, //时长2
                        1, //时长价格2
                        new StringBuilder("0001"), //价格体系版本号, 固定
                        258,  //操作代码，固定
                        ref err);
                    if (0 == err)
                    {
                        Log.Debug("CangNan购气成功！");
                    }
                    else
                    {
                        Log.Debug("CangNan购气失败！");
                    }
                }
            }
            catch (Exception e)
            {
                Log.Debug("CangNan购气异常:" + e.Message + "--" + e.StackTrace);
            }
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Int32 ret = -1;
            try
            {
                Log.Debug("CangNan FormatGasCard start:" + System.DateTime.Now);
                int err = 0;
                int port = (int)com;
                ret = ClearCard(4, port, baud, ref err);
                if (0 == err)
                {
                    Log.Debug("CangNan格式化卡成功！");
                }
                else
                {
                    Log.Debug("CangNan格式化卡失败！");
                }
            }
            catch (Exception e)
            {
                Log.Debug("CangNan格式化卡异常:" + e.Message + "--" + e.StackTrace);
            }
            return ret;
        }
        public int OpenCard(Int16 com, Int32 baud)
        {
            throw new NotImplementedException();
        }
        public int CheckGasCard(short com, int baud)
        {
            StringBuilder MeterType = new StringBuilder("00");
            int CardType = 0;
            Int32 ret = -1;
            int err = 0;
            try
            {
                Log.Debug("CangNan CheckGasCard start:" + System.DateTime.Now);
                int port = (int)com;
                ret = IsCNCard(4, port, baud, ref MeterType, ref CardType, ref err);
                if (0 == err)
                {
                    Log.Debug("检查卡是苍南卡");
                }
                else
                {
                    Log.Debug("检查卡不是苍南卡");
                }
            }
            catch (Exception e)
            {
                Log.Debug("CangNan检查卡异常:" + e.Message + "--" + e.StackTrace);
            }
            return ret;
        }
         #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public CangNan()
        {
            
            Errors.Add(1, "卡片核对不符（卡号、用户号或者区域不匹配）。");
            Errors.Add(2, "没有卡。");
            Errors.Add(3, "读卡器配置不对。");
            Errors.Add(4, "读卡失败。");
            Errors.Add(5, "写卡失败。");
            Errors.Add(6, "非苍南仪表的卡或IC卡芯片插反了。");
            Errors.Add(7, "密码错误。");
            Errors.Add(8, "卡片报废。");
            Errors.Add(9, "新卡（全新的未使用过的卡）。");
            Errors.Add(10, "新用户卡（已制作成用户卡，但还未开户的卡）。");
            Errors.Add(11, "购气量超购气量上限或者不正确。");
            Errors.Add(12, "卡号、用户号超范围。");
            Errors.Add(13, "购气次数不对。");
            Errors.Add(14, "非用户卡（除用户卡以外的其他功能卡，如调价卡、清零卡等）。");
            Errors.Add(15, "退气时购气次数不对。");
            Errors.Add(16, "退气时卡内无气量可退。");
            Errors.Add(17, "气表类型MeterType不对。");
            Errors.Add(18, "报警量超范围。");
            Errors.Add(19, "补卡次数不对。");
            Errors.Add(20, "卡内还有气量。");
            Errors.Add(21, "单价不对。");
            Errors.Add(22, "售气方式不对。");
            Errors.Add(23, "充值限超范围。");
            Errors.Add(24, "透支限超范围。");
            Errors.Add(25, "不能连续退气。");
            Errors.Add(26, "用户号不对。");
            Errors.Add(27, "区域代号不对。");
            Errors.Add(28, "操作类型不对。"); 
            Errors.Add(29, "卡类型不正确。");
            Errors.Add(30, "该卡未注册，或注册信息与卡片不符。");
            Errors.Add(31, "刚补卡的卡不能退购。");
            Errors.Add(34, "老系统卡片。");
            Errors.Add(37, "参数解析不正确，或个数不匹配。");
            Errors.Add(40, "阶梯用量超范围。");
            Errors.Add(41, "阶梯价周期超范围。");
            Errors.Add(42, "时长超范围。");
            Errors.Add(43, "价格版本号超范围。");
            Errors.Add(44, "时间不正确。");
            Errors.Add(45, "");
            Errors.Add(46, "校验口令错误。");
            Errors.Add(47, "金额单位错误。");
            Errors.Add(98, "串口初始化失败。");
            Errors.Add(99, "其他。");
            Errors.Add(101, "注册码不存在。");
            Errors.Add(102, "注册码解密失败。");
            Errors.Add(103, "注册码与加密因子不匹配。");
            Errors.Add(106, "公司代号不匹配。");
            Errors.Add(107, "获取MAC地址出错。");
            Errors.Add(108, "已注册。");
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
