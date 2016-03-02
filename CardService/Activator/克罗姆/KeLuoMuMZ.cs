using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.Aote.Logs;
using System.Runtime.InteropServices;
using ICard;

namespace Card
{
    /// <summary>
    /// 对卡操作的描述及操作时需要特别注意的问题的描述
    /// </summary>
    public class KeLuoMuMZ : ICard,IVerbose
    {
        /// <summary>
        /// 得到日志实例。代码中的写法为了兼顾以前卡的封装
        /// 对于新卡，按照下行的方式来写
        /// <code>
        /// private static log4net.ILog Log = log4net.LogManager.GetLogger(typeof(KeLuoMuMZ));
        /// </code>
        /// </summary>
        public static Log Log = Log.GetInstance("Card.KeLuoMuMZ");
        //引入动态库中标准读卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "readCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ReadGasCard(Int16 com, byte[] kh, byte[] yhh, ref Int32 ql, ref Int32 cs, ref Int32 ljgql, ref Int32 syql, ref Int32 klx);
        //引入动态库中标准发卡，补卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "makeCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeGasCard(Int16 com, byte[] cardNo, byte[] customeNo, Int16 orderNum, Int32 orderamount);
        //引入动态库中标准清卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "clearCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, byte[] customeNo);
        //引入动态库中标准判卡函数接口
        [DllImport("qw_card.dll", EntryPoint = "NewCardCheck", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, byte[] Istrue);
        //引入动态库中标准买气函数接口
        [DllImport("qw_card.dll", EntryPoint = "writeOrders", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 StaticWriteGasCard(Int16 com, byte[] cardNo, byte[] customeNo, Int32 orderNum, Int32 orderamount);
        //引入动态库中标准检测函数接口
        [DllImport("qw_card.dll", EntryPoint = "rdCompany", CallingConvention = CallingConvention.StdCall)]
        public static extern int Staticrd_Company(Int16 com, byte[] Istrue);
       
        public string Test()
        {
            return "绵竹";
        }
        #region 卡操作
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
        /// <param name="dqdm">地区代码</param>
        /// <returns></returns>
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
             Int32 ljgql = 0; //累计购气量
             Int32 syql = 0;//当前表剩余气量
             Int32 klx = 0; //卡类型
             Int32 orderamount = 0;
             Int32 orderNum = 0;

             byte[] cardid = new byte[100];
             byte[] customeNo = new byte[100];
             Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=("
               + com + "," + baud + "," + kh + "," + ql
               + money + "," + cs + "," + bkcs + ")");
             Int32 result = ReadGasCard(com, cardid, customeNo, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
             ql = orderamount;
             cs = (short)orderNum;
             string yhh = "";
            //读取卡号
             int pos = -1;
             for (int i = 0; i < cardid.Length; i++)
             {
                 if (cardid[i] == 0)
                {
                    pos = i;
                    break;
                }
             }
             kh = Encoding.ASCII.GetString(cardid, 0, pos);
            //读取用户号
             int index = -1;
             for (int i = 0; i < customeNo.Length; i++)
             {
                 if (customeNo[i] == 0)
                 {
                     index = i;
                     break;
                 }
             }
             yhh = Encoding.ASCII.GetString(customeNo, 0, index);
             Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)=" +
                 "(" + com + "," + baud + "," + kh + "," + ql
                 + money + "," + cs + "," + bkcs + "," + yhh + ")=" + result);
          
            return result;
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
            byte []  cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] yh = System.Text.Encoding.GetEncoding(1252).GetBytes(yhh);
            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20]; 
            int result = -1;
            //写新卡前先格式化卡
            //格式化卡之前先判断是否为新卡
           byte[] Istrue =new byte[20];
            int i = StaticCheckGasCard(com, Istrue);
            int istrue1 =int.Parse(Encoding.ASCII.GetString(Istrue, 0, 1));
            Log.Debug("CheckGasCardiSNew and i  is" + i + " and isTrue is " + istrue1);
            //判卡成功
           
                //是新卡，则不需清卡  if (Istrue == 0)
             
                //不是新卡，清卡  ，先读卡
            if (istrue1 == 1)
            {
                    Log.Debug("克罗姆写新卡非新卡清卡");
                    Log.Debug("WriteNewCard->StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm)=(" + com + "," + baud + "," + kmm + "," + 2 + "," + cardNo + "," + dqdm + ")");
                    Int32 syql = 0;//当前表剩余气量
                    Int32 klxs = 0; //卡类型
                    Int32 ljgql1 = 0;
                    Int32 orderamount = 0;
                    Int32 orderNum = 0;
                    byte[] cardid1 = new byte[20];
                    byte[] customeid1 = new byte[20];
                    //清卡前先读卡 确保传入卡中的卡号等参数正确
                    Log.Debug("ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx" + com + "," + cardid + "," +
                            customeid + "," + orderamount + "," + orderNum + "," + ljgql + "," + syql + "," + klx);
                    int readresult = ReadGasCard(com, cardid1, customeid1, ref orderamount, ref orderNum, ref ljgql1, ref syql, ref klx);
                    Log.Debug("ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx)" + readresult);
                    byte[] customeNo = customeid1;
                    string yhhq = Encoding.ASCII.GetString(customeNo, 0, 10);
                    Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=(" + com + "," + baud + "," + kmm + "," + kh + "," + dqdm + ")");
                    //清卡需要传入端口号 用户号
                    result = StaticFormatGasCard(com, customeNo);
                    Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=" + result);
                    //清卡失败
                    if (result != 0)
                    {
                        Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=" + result);
                        return result;
                    }
            }
           
           
            Log.Debug("WriteNewCard->StaticFormatGasCard(com, baud, mm, 2, cardNO, bdqdm)=(" + com + "," + baud + "," + kmm + "," + 2 + "," + cardNo + "," + dqdm + ")=" + result);
            //kzt==0  表示新开户 写卡
            if (0 == kzt)
            {
                result = MakeGasCard(com, cardNo, yh, 1, ql);
            }
            //kzt==1  表示补卡 写卡
            else if(1 == kzt)
            {
                result = MakeGasCard(com, cardNo, yh, cs, ql);
            }
                
                    String buf;
                    Log.Debug("发卡后读卡格式:");
                    MingHua.GetSnapShot(com, baud, out buf);
                    Log.Debug(buf);
                    Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                          + com + "," + baud + "," + kmm + "," + kzt
                          + kh + "," + dqdm + "," + yhh + "," + tm
                          + ql + "," + csql + "," + ccsql + "," + cs
                          + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                          + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                          + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                          + ")=" + result);
                    //转换错误代码
                   
                    return result;           
            }
        /// <summary>
        /// 售气写卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <param name="kmm">卡密码</param>
        /// <param name="kh">卡号</param>
        /// <param name="dqdm">地区代码</param>
        /// <param name="ql">气量，小于或等于0，表示退气，大于0表示购气。</param>
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
            String buf;
            Log.Debug("买气前读卡格式:");
            MingHua.GetSnapShot(com, baud, out buf);

            byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
           
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
             + com + "," + baud + "," + kmm + "," + kh
             + dqdm + "," + ql + "," + csql + "," + ccsql
             + cs + "," + ljgql + "," + bjql + "," + czsx
             + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
             + newprice + "," + sxrq + "," + sxbj
             + ")");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                       + com + "," + baud + "," + kmm + "," + kh
                       + dqdm + "," + ql + "," + csql + "," + ccsql
                       + cs + "," + ljgql + "," + bjql + "," + czsx
                       + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                       + newprice + "," + sxrq + "," + sxbj
                       + ")=" + readresult);
            byte[] customeNo = customeid;
            string khq = Encoding.ASCII.GetString(cardNo, 0, 8);
            string yhhq = Encoding.ASCII.GetString(customeNo, 0, 10);
            Log.Debug("sell gas start:"+"kh:"+khq+"--yhh:"+yhhq+"--cs:"+cs+"--ql:"+ql);
            int result = StaticWriteGasCard(com, cardNo, customeNo, cs, ql);

             Log.Debug("write gas card result=" + result);
             Log.Debug("买气后读卡格式:");
             String buf2;
             MingHua.GetSnapShot(com, baud, out buf2);
             Log.Debug(buf);
             Log.Debug(buf2);
             return result;
    
        }
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 ljgql = 0;
            Int32 orderamount = 0;
            Int32 orderNum = 0;
            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            //清卡前先读卡 确保传入卡中的卡号等参数正确
            Log.Debug("ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx" + com + "," + cardid+","+
                    customeid + "," + orderamount + "," + orderNum + "," + ljgql + "," + syql+","+klx);
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx)"+readresult);
            byte[] customeNo = customeid;
            string yhhq = Encoding.ASCII.GetString(customeNo, 0, 10);
            Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=(" + com + "," + baud + "," + kmm + "," + kh + "," + dqdm + ")");
            //清卡需要传入端口号 用户号
            int result = StaticFormatGasCard(com, customeNo);
            Log.Debug("FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)=" + result);
            return result;
        }
        /// <summary>
        /// 判卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int CheckGasCard(Int16 com, Int32 baud)
        {
            byte[] Istrue = new byte[10];
            Log.Debug("CheckGasCard(short com, int baud)=(" + com + "," + baud + ")");
            int result = Staticrd_Company(com, Istrue);
            Log.Debug("StaticCheckGasCard(com, baud)=" + result);
            Int32 syql = 0;//当前表剩余气量
            Int32 klx = 0; //卡类型
            Int32 ljgql = 0;
            Int32 orderamount = 0;
            Int32 orderNum = 0;

            byte[] cardid = new byte[20];
            byte[] customeid = new byte[20];
            Log.Debug("read keluomu card start!");
            int readresult = ReadGasCard(com, cardid, customeid, ref orderamount, ref orderNum, ref ljgql, ref syql, ref klx);
            Log.Debug("read keluomu card end!" + readresult);
            if (cardid[0] == 0)
            {
                return -1;
            }
            else
            {
                string kh = Encoding.ASCII.GetString(Istrue, 0, 3);
                int istrue = int.Parse(kh);
                return istrue;
            }
            
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
                return "keluomu";
            }
        }

        //public Int32 ChangeResult(Int32 ret)
        //{
        //    if (-128 == ret) ret = -7;
        //    if (-129 == ret) ret = -10;
        //    if (-130 == ret) ret = -9;
        //    if (-134 == ret) ret = -4;
        //    if (-136 == ret) ret = -3;
        //    if (-137 == ret) ret = -7;
        //    if (-138 == ret) ret = -3;
        //    if (-145 == ret) ret = -20;
        //    if (-160 == ret) ret = -15;
        //    if (-161 == ret) ret = -16;
        //    if (-162 == ret) ret = -6;
        //    if (-163 == ret) ret = -11;
        //    if (-170 == ret) ret = -6; 
        //    return ret;
        //}
        #endregion
          #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public KeLuoMuMZ()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(-128, "读数据出错。");
            Errors.Add(-129, "写数据出错。");
            Errors.Add(-130, "卡密码错误。");
            Errors.Add(-134, "卡片错误。");
            Errors.Add(-136, "通讯错误。");
            Errors.Add(-137, "卡型检查错误。");
            Errors.Add(-138, "读写器状态异常。");
            Errors.Add(-145, "参数错误。");
            Errors.Add(-160, "卡非新卡。");
            Errors.Add(-161, "用户编号不符。");
            Errors.Add(-162, "卡已报废。");
            Errors.Add(-163, "卡存气量。");
            Errors.Add(-170, "卡未绑定。");

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
