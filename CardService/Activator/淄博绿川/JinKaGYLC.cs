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
    /// 对卡操作的描述及操作时需要特别注意的问题的描述
    /// </summary>
    public class JinKaGY : ICard, IVerbose
    {
        /// <summary>
        /// 得到日志实例。代码中的写法为了兼顾以前卡的封装
        /// 对于新卡，按照下行的方式来写
        /// <code>
        /// private static log4net.ILog Log = log4net.LogManager.GetLogger(typeof(JinKaGY));
        /// </code>
        /// </summary>
        private static Log Log = Log.GetInstance("Card.JinKaGY");

        /// <summary>
        /// 本方法为测试方法
        /// </summary>
        /// <returns>返回卡中文名</returns>
        public string Test()
        {
            return "工业金卡";
        }
        
        #region 金卡动态库导入  动态库导入部分，根据具体厂家提供的动态库如实对应调用声明
        /// <summary>
        /// 读卡， 
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="klx">卡类型</param>
        /// <param name="kzt">卡状态</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="yhh">用户号</param>
        /// <param name="tm">条码</param>
        /// <param name="ql">气量</param>
        /// <param name="cs">次数</param>
        /// <param name="ljgql">累购气量</param>
        /// <param name="bkcs">补卡次数</param>
        /// <param name="ljyql">累计用气量</param>
        /// <param name="syql">使用气量</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限</param>
        /// <param name="tzed">透支额度</param>
        /// <param name="sqrq">售气日期</param>
        /// <param name="oldprice">老价格</param>
        /// <param name="newprice">新价格</param>
        /// <param name="sxrq">生效日期</param>
        /// <param name="sxbj">生效标记</param>
        /// <returns>成功:0,失败：非0</returns>
        [DllImport("goldcard.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadGasCard(Int16 com, Int32 baut,byte[] kmm, ref Int16 klx, ref Int16 kzt,  
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, ref Int32 ql, ref Int16 cs, ref Int32 ljgql, ref Int16 bkcs, 
            ref Int32 ljyql, ref Int32 syql, ref Int32 bjql, ref Int32 czsx, ref Int32 tzed,byte[] sqrq ,ref Int32 oldprice, 
            ref Int32 newprice, byte[] sxrq, byte[] sxbj);

        /// <summary>
        /// 写新卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="klx">卡类型</param>
        /// <param name="kzt">卡状态</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="yhh">用户号</param>
        /// <param name="tm">条码</param>
        /// <param name="ql">气量</param>
        /// <param name="cs">次数</param>
        /// <param name="ljgql">累购气量</param>
        /// <param name="bkcs">补卡次数</param>
        /// <param name="ljyql">累计用气量</param>
        /// <param name="syql">使用气量</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限</param>
        /// <param name="tzed">透支额度</param>
        /// <param name="sqrq">售气日期</param>
        /// <param name="oldprice">老价格</param>
        /// <param name="newprice">新价格</param>
        /// <param name="sxrq">生效日期</param>
        /// <param name="sxbj">生效标记</param>
        /// <returns>成功:0,失败：非0</returns>
        [DllImport("goldcard.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteNewCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, Int32 ql, Int16 cs, Int32 ljgql, Int16 bkcs, Int32 ljyql,
            Int32 bjql, Int32 czsx, Int32 tzed, byte[] sqrq, ref Int32 oldprice, ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        
        /// <summary>
        /// 判断是否工业金卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        [DllImport("goldcard.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, Int32 baut);

        /// <summary>
        /// 格式化卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="klx">卡类型</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <returns>成功:0,失败：非0</returns>
        [DllImport("goldcard.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, byte[] dqdm);

        /// <summary>
        /// 购气写卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="klx">卡类型</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="ql">气量</param>
        /// <param name="cs">次数</param>
        /// <param name="ljgql">累购气量</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限</param>
        /// <param name="tzed">透支额度</param>
        /// <param name="sqrq">售气日期</param>
        /// <param name="oldprice">老价格</param>
        /// <param name="newprice">新价格</param>
        /// <param name="sxrq">售气日期</param>
        /// <param name="sxbj">生效标记</param>
        /// <returns>成功:0,失败：非0</returns>
        [DllImport("goldcard.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, 
            byte[] dqdm, Int32 ql, Int16 cs, Int32 ljgql, Int32 bjql, Int32 czsx, Int32 tzed,
            byte[] sqrq ,ref Int32 oldprice, ref Int32 newprice, byte[] sxrq, byte[] sxbj);
        #endregion

        #region ICard Members

        /// <summary>
        /// 判卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int CheckGasCard(short com, int baud)
        {
           Log.Debug("CheckGasCard(short com, int baud)=(" + com + "," + baud +")");
           int ret = StaticCheckGasCard(com, baud);
           Log.Debug("StaticCheckGasCard(com, baud)=" + ret);
           if (ret == 0)
           {
                    byte[] cardNO = new byte[100];
                    byte[] kmm = new byte[100];
                    byte[] dqdm = new byte[100];
                    byte[] yhh = new byte[100];
                    byte[] sqrq = new byte[100];
                    byte[] sxrq = new byte[100];
                    byte[] sxbj = new byte[100];
                    byte[] tm = new byte[100];
                    short klx = 0;
                    short kzt = 0;
                    int ljgql = 0;
                    int ljyql = 0;
                    int ql = 0;
                    short cs = 0;
                    short bkcs = 0;
                    int syql = 0;
                    int bjql = 0;
                    int czsx = 0;
                    int tzed = 0;
                    int oldprice = 0;
                    int newprice = 0;

                    ret = StaticReadGasCard(0, baud, kmm, ref klx, ref kzt, cardNO, dqdm, yhh, tm,
                    ref ql, ref cs, ref ljgql, ref bkcs, ref ljyql, ref syql, ref bjql, ref czsx,
                    ref tzed, sqrq, ref oldprice, ref newprice, sxrq, sxbj);
                    Log.Debug("CheckGasCard->StaticReadGasCard=" + kmm + ",kzt=" + kzt + ",klx=" + klx + ",kh=" + cardNO + ",dqdm=" + dqdm + ",ret=" + ret);
                    if (klx == 2)
                    {
                        Log.Debug("CheckGasCard=0");
                        return 0;
                    }
                    Log.Debug("CheckGasCard=-1");
                    return -1;
                }
           Log.Debug("CheckGasCard=-1");
           return -1;
        }

        /// <summary>
        /// 格式化卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="klx">卡类型</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <returns>成功:0,失败：非0</returns>
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=(" + com + "," + baud + "," + kmm + "," + kh + "," + dqdm + ")");
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");
            int r =  StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm);
            Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=" + r);
            return r;
        }

        /// <summary>
        /// 读卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kh">卡号</param>
        /// <param name="ql">气量</param>
        /// <param name="money">金额</param>
        /// <param name="cs">次数</param>
        /// <param name="bkcs">补卡次数</param>
        /// <param name="yhh">用户号</param>
        /// <returns></returns>
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)
        {
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)=(" 
                + com + "," + baud + "," + kh + "," + ql
                + money + "," + cs + "," + bkcs + "," + yhh + ")");
            byte[] cardNO = new byte[100];
            byte[] kmm = new byte[100];
            byte[] dqdm = new byte[100];
            byte[] yhh2 = new byte[100];
            byte[] sqrq = new byte[100];
            byte[] sxrq = new byte[100];
            byte[] sxbj = new byte[100];
            byte[] tm = new byte[100];
            short klx = 0;
            short kzt = 0;
            int ljgql = 0;
            int ljyql = 0;
            int syql = 0;
            int bjql = 0;
            int czsx = 0;
            int tzed = 0;
            int oldprice = 0;
            int newprice = 0;
            short bkcs2 =1;
            int ret = StaticReadGasCard(0, baud, kmm, ref klx, ref kzt, cardNO, dqdm, yhh2, tm, 
                ref ql, ref cs, ref ljgql, ref bkcs2, ref ljyql, ref syql, ref bjql, ref czsx, 
                ref tzed, sqrq, ref oldprice, ref newprice, sxrq, sxbj);
            //卡号转换成字符串
            cardNO[8] = 0;
            kh = Encoding.ASCII.GetString(cardNO, 0, 8);
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)=" + 
                "(" + com + "," + baud + "," + kh + "," + ql
                + money + "," + cs + "," + bkcs + "," + yhh + ")=" + ret);
            return ret;
        }

        /// <summary>
        /// 售气写卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="ql">气量</param>
        /// <param name="csql">上次气量</param>
        /// <param name="ccsql">上上次气量</param>
        /// <param name="cs">次数</param>
        /// <param name="ljgql">累购气量</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限</param>
        /// <param name="tzed">透支额度</param>
        /// <param name="sqrq">售气日期</param>
        /// <param name="cssqrq">上次售气日期</param>
        /// <param name="oldprice">老价格</param>
        /// <param name="newprice">新价格</param>
        /// <param name="sxrq">售气日期</param>
        /// <param name="sxbj">生效标记</param>
        /// <returns>成功:0,失败：非0</returns>
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kh
                + dqdm + "," + ql + "," + csql + "," + ccsql
                + cs + "," + ljgql + "," + bjql + "," + czsx
                + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                + newprice + "," + sxrq + "," + sxbj 
                + ")");
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");
            byte[] bsqrq = new byte[10];
            byte[] bsxrq = new byte[10];
            byte[] bsxbj = new byte[10];
            Int32 boldprice = 0;
            Int32 bnewprice = 0;

            int ret = StaticWriteGasCard(com, baud, mm, 2, cardNO, bdqdm, ql, 
                cs, ljgql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj);
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kh
                + dqdm + "," + ql + "," + csql + "," + ccsql
                + cs + "," + ljgql + "," + bjql + "," + czsx
                + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                + newprice + "," + sxrq + "," + sxbj
                + ")=" + ret);
            return ret;
        }

        /// <summary>
        /// 写新卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="kzt">卡状态</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="yhh">用户号</param>
        /// <param name="tm">条码</param>
        /// <param name="ql">气量</param>
        /// <param name="csql">上次气量</param>
        /// <param name="ccsql">上上次气量</param>
        /// <param name="cs">次数</param>
        /// <param name="ljgql">累购气量</param>
        /// <param name="bkcs">补卡次数</param>
        /// <param name="ljyql">累计用气量</param>
        /// <param name="bjql">报警气量</param>
        /// <param name="czsx">充值上限</param>
        /// <param name="tzed">透支额度</param>
        /// <param name="sqrq">售气日期</param>
        /// <param name="cssqrq">上次售气日期</param>
        /// <param name="oldprice">老价格</param>
        /// <param name="newprice">新价格</param>
        /// <param name="sxrq">生效日期</param>
        /// <param name="sxbj">生效标记</param>
        /// <returns>成功:0,失败：非0</returns>
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kzt
                + kh + "," + dqdm + "," + yhh + "," + tm
                + ql + "," + csql + "," + ccsql + "," + cs
                + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                + ")");
            byte[] mm = new byte[10];
            byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes("0577");
            byte[] byhh = System.Text.Encoding.GetEncoding(1252).GetBytes("0000000001");
            byte[] btm = new byte[10];
            byte[] bsqrq = new byte[10];
            byte[] bsxrq = new byte[10];
            byte[] bsxbj = new byte[10];
            Int32 boldprice = 0;
            Int32 bnewprice = 0;
            Log.Debug("WriteNewCard->StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm)=(" + com + "," + baud + "," + mm + "," + 2 + "," + cardNO + "," + dqdm + ")");
            //发卡前先格式化卡
            int ret = StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm);
            Log.Debug("WriteNewCard->StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm)=(" + com + "," + baud + "," + mm + "," + 2 + "," + cardNO + "," + dqdm + ")=" + ret);
            if(ret != 0)
            {
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=" + ret);
                return ret;
            }
            Log.Debug("WriteNewCard->StaticWriteNewCard(com, baud, mm, 2, kzt, cardNO, bdqdm, byhh, btm, " +
               " ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj)=(" 
               + com + "," + baud + "," + mm + "," + 2 + "," + cardNO + "," + dqdm
               + byhh + "," + btm + "," + ql + "," + cs + "," + ljgql + "," + bkcs
               + ljyql + "," + bjql + "," + czsx + "," + tzed + "," + bsqrq + "," + boldprice
               + bnewprice + "," + bsxrq + "," + bsxbj 
               + ")");
            ret = StaticWriteNewCard(com, baud, mm, 2, kzt, cardNO, bdqdm, byhh, btm, 
                ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj);
            Log.Debug("WriteNewCard->StaticWriteNewCard(com, baud, mm, 2, kzt, cardNO, bdqdm, byhh, btm, " +
               " ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, ref boldprice, ref bnewprice, bsxrq, bsxbj)=("
               + com + "," + baud + "," + mm + "," + 2 + "," + cardNO + "," + dqdm
               + byhh + "," + btm + "," + ql + "," + cs + "," + ljgql + "," + bkcs
               + ljyql + "," + bjql + "," + czsx + "," + tzed + "," + bsqrq + "," + boldprice
               + bnewprice + "," + bsxrq + "," + bsxbj
               + ")=" + ret);
            if (ret != 0)
            {
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=" + ret);
                return ret;
            }
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kzt
                + kh + "," + dqdm + "," + yhh + "," + tm
                + ql + "," + csql + "," + ccsql + "," + cs
                + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                + ")=" + ret);
            return ret;
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

        /// <summary>
        /// 返回值应该和类名相同
        /// </summary>
        public string Name
        {
            get
            {
                return "JinKaGY";
            }
        }

        #endregion

        #region IVerbose 实现 在实现IVerbose接口时使用下面代码，如果不实现则不使用

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public JinKaGY()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
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
    }
}
