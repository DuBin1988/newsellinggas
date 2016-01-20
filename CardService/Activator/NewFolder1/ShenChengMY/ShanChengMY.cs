using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class ShanChengMY : ICard,IVerbose
    {
        private static Log Log = Log.GetInstance("Card.ShanChengMY");

        public string Test()
        {
            return "ShanChengMY";
        }

        #region 动态库导入
        //读卡函数

        [DllImport("MwMydll.dll", EntryPoint = "Read102", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public  static extern int ReadCard(short com, byte [] keycode, byte[] cardNo, byte[] customerno, ref Int64 BuyGasNum, ref Int32 times, ref Int64 remainnum, byte[] fgs,byte[] glz);
        //开户卡函数
        [DllImport("MwMydll.dll", EntryPoint = "Init102", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteNewUser(short com, byte[] keycode, byte[] cardNo, byte[] customerno, Int64 BuyGasNum, Int32 times, Int64 sumnum, byte[] buydate, byte[] fgs, byte[] glz);
          //写卡函数
        [DllImport("MwMydll.dll", EntryPoint = "Add102", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteUser(short com, byte[] keycode, Int64 BuyGasNum, Int32 times, Int64 sumnum, byte[] buydate,short UpdateFlag);
        //清卡
        [DllImport("MwMydll.dll", EntryPoint = "clearCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int FormatCard(Int32 icDev, byte[] keycode);
        //判卡函数
        [DllImport("MwMydll.dll", EntryPoint = "Check102", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCompany(short com, byte[] keycode);
          //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);
        #endregion

        #region ICard Members


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
        public int FormatGasCard(Int16 com,          //串口号，从0开始
            Int32 baud,         //波特率
            string kmm,         //卡密码，写卡后返回新密码
            string kh,          //卡号
            string dqdm)
        {
            
            Log.Debug("FormatGasCard(short com)=(" + com + "," + baud +")");
            byte[] keycode = System.Text.Encoding.GetEncoding(1252).GetBytes("shancheng");
            int i = FormatCard(0, keycode);
            Log.Debug("FormatGasCard(short com)=(" + com + "," + baud + ")="+i);
            return i;
        }
        /// <summary>
        /// 判卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int CheckGasCard( Int16 com,  Int32 baud  ) {
            Log.Debug("CheckGasCard(short com, int baud)=(" + com + "," + baud + ")");
            byte[] keycode = System.Text.Encoding.GetEncoding(1252).GetBytes("shancheng");
            int i = ReadCompany(com, keycode);
            Log.Debug("StaticCheckGasCard(com, baud)=" + i);
            return i;
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
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=("
               + com + "," + baud + "," + kh + "," + ql
               + money + "," + cs + "," + bkcs + "," + ")");
           byte[] keycode = new byte[20];
           byte[] cardNo = new byte[10];
            byte[] customerno = new byte[10];
            byte[] fgs = System.Text.Encoding.GetEncoding(1252).GetBytes("0000");
            byte[] glz = System.Text.Encoding.GetEncoding(1252).GetBytes("0000");
            Int32 times = 0;
            Int64 BuyGasNum = 0;
            Int64 remainnum = 0;
            int i = ReadCard(com, keycode, cardNo, customerno, ref BuyGasNum, ref times, ref remainnum, fgs, glz);
            kh = Encoding.ASCII.GetString(cardNo, 0, 10);
            string yhh = Encoding.ASCII.GetString(customerno, 0, 10);
            ql = (int)BuyGasNum/100;
            cs = (short)times;

            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref string yhh)=" +
                 "(" + com + "," + baud + "," + kh + "," + ql
                 + money + "," + cs + "," + yhh + ")=" + i);             
            return i;
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
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kh
                + dqdm + "," + ql + "," + csql + "," + ccsql
                + cs + "," + ljgql + "," + bjql + "," + czsx
                + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                + newprice + "," + sxrq + "," + sxbj
                + ")");
            byte[] keycode = System.Text.Encoding.GetEncoding(1252).GetBytes("shancheng");
          
            byte[] buydate = System.Text.Encoding.GetEncoding(1252).GetBytes(sqrq);
            Int64 ljgql1 = 0;
            Int16 falgs = 0;
           int sellret = -1;
                if (ql > 0)
                { 
                    sellret = WriteUser(com, keycode, (Int64)ql * 100,(Int32) cs, ljgql1, buydate, falgs);
                     Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + sellret);
                  
                }
                else {
                   Log.Debug("退气开始："+"次数:"+cs);
                   sellret = WriteUser(com, keycode, 0,(Int32) cs, ljgql1, buydate, falgs);;
                   Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + sellret);
                }
        
            return sellret;
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
            byte[] keycode = System.Text.Encoding.GetEncoding(1252).GetBytes("shancheng");
            FormatCard(0, keycode);
            int i = 10;
            byte[] cardNo1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] customerno = System.Text.Encoding.GetEncoding(1252).GetBytes("1111111111");
            byte[] fgs = System.Text.Encoding.GetEncoding(1252).GetBytes("0000");
            byte[] glz = System.Text.Encoding.GetEncoding(1252).GetBytes("0000");
            if(0 == kzt)
            {
                cs = 0;
            }
            Int64 sumnum = 0;
            byte[] buydate = System.Text.Encoding.GetEncoding(1252).GetBytes(sqrq);
            i = WriteNewUser(com, keycode, cardNo1, customerno, (Int64)ql * 100, (Int32)cs, sumnum, buydate, fgs, glz);
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                    + com + "," + baud + "," + kmm + "," + kzt
                    + kh + "," + dqdm + "," + yhh + "," + tm
                    + ql + "," + csql + "," + ccsql + "," + cs
                    + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                    + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                    + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                    + ")=" + i);               
            return i;
        }

        public string Name
        {
            get
            {
                return "ShanChengMY";
            }
        }

        #endregion
             #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public ShanChengMY()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(128, "读错误。");
            Errors.Add(129, "写错误。");
            Errors.Add(130, "命令错误。");
            Errors.Add(131, "密码错误。");
            Errors.Add(132, "超时错误。");
            Errors.Add(133, "测卡错误");
            Errors.Add(134, "无卡错误。");
            Errors.Add(135, "超值错误。");
            Errors.Add(136, "通讯错误。");
            Errors.Add(137, "卡型错误。");
            Errors.Add(138, "校验和错误。");
            Errors.Add(140, "非法拔卡。");
            Errors.Add(141, "通用错误。");
            Errors.Add(142, "命令头错误。");
            Errors.Add(144, "地址错误。");
            Errors.Add(145, "长度错误。");
            Errors.Add(149, "串口占用。");
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
