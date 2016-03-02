using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public partial class ShanChengGY : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.ShanChengGY");
        public string Test()
        {
            return "ShanChengGY";
        }
        #region 动态库导入
        //读卡函数

        [DllImport("xj102.dll", EntryPoint = "readCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int readGasCard(short PortNo, byte[]cardNo, ref Int64 GasV,ref Int32 Times);
        //制作开户卡或补卡
        [DllImport("xj102.dll", EntryPoint = "MandGasCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int MandGasCard(short PortNo, byte[]cardno, Int64 m_gas, short m_num);
        //制作购气卡
        [DllImport("xj102.dll", EntryPoint = "MakeUserCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeUserCard(short PortNo, byte[] cardno, Int64 m_gas, short m_num);

        #endregion
        public string Name
        {
            get { throw new NotImplementedException(); }
        }
        #region ICard Members
        public int OpenCard(short com, int baud)
        {
            return -1;
        }
        public int CheckGasCard(short com, int baud)
        {
            Log.Debug("check card start!");
            byte[] byteKH = new byte[10];
            Int64 GasV = 0;
            Int32 Times = 0;
            int result = readGasCard(com, byteKH, ref GasV, ref Times);
            Log.Debug("check card end!");
            return result;
        }
        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            return -1;
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
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)=" + "(" + com + "," + baud + "," + kh + "," + ql + "," + money + "," + cs + "," + bkcs + "," + yhh + ")");
            byte[] byteKH = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Int64 GasV = 0;
            Int32 Times = 0;
            int result = readGasCard(com, byteKH, ref GasV, ref Times);
            kh = Encoding.ASCII.GetString(byteKH, 0, 10);
            ql = (int)GasV;
            cs = (short)Times;
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)=" + "(" + com + "," + baud + "," + byteKH + "," + ql + "," + money + "," + cs + "," + bkcs + "," + yhh + ")=" + result);
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
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)=" +
                "(" + com +","+ baud +","+ kmm +","
                    + kzt + "," + kh + "," + dqdm + ","
                    + yhh +","+ tm +","+ ql +","
                    + csql +","+ ccsql +","+ cs +","
                    + ljgql +","+ bkcs +","+ ljyql +","
                    + bjql +","+ czsx +","+ tzed +","
                    + cssqrq +","+ oldprice +","+ oldprice +","
                    + newprice +","+ sxrq +","+ sxbj +","
                    + klx +","+ meterid + ")");
            int result = -1;
            if (0 == kzt)
            {
                byte[] byteKH = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                result = MandGasCard(com, byteKH, ql, 1);
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)=" +
                   "(" + com + "," + baud + "," + kmm + ","
                       + kzt + "," + byteKH + "," + dqdm + ","
                       + yhh + "," + tm + "," + ql + ","
                       + csql + "," + ccsql + "," + cs + ","
                       + ljgql + "," + bkcs + "," + ljyql + ","
                       + bjql + "," + czsx + "," + tzed + ","
                       + cssqrq + "," + oldprice + "," + oldprice + ","
                       + newprice + "," + sxrq + "," + sxbj + ","
                       + klx + "," + meterid + ")=" + result);
                return result;
            }
            else {
                byte[] byteKH = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                result = MandGasCard(com, byteKH, ql, cs);
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)=" +
                   "(" + com + "," + baud + "," + kmm + ","
                       + kzt + "," + byteKH + "," + dqdm + ","
                       + yhh + "," + tm + "," + ql + ","
                       + csql + "," + ccsql + "," + cs + ","
                       + ljgql + "," + bkcs + "," + ljyql + ","
                       + bjql + "," + czsx + "," + tzed + ","
                       + cssqrq + "," + oldprice + "," + oldprice + ","
                       + newprice + "," + sxrq + "," + sxbj + ","
                       + klx + "," + meterid + ")=" + result);
                return result;
            }
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
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)" + 
                "(" + com + "," + baud + "," + kmm 
                    + "," + kh + "," + dqdm + "," +ql 
                    + "," +csql + "," + ccsql + "," + cs 
                    + "," + ljgql + "," + bjql + "," + czsx 
                    + "," + tzed + "," + sqrq + "," + cssqrq 
                    + "," + oldprice + "," + newprice + "," + sxrq 
                    + "," + sxbj+")");
            int result = -1;
            if (ql > 0)
            {
                byte[] byteKH = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                result = MakeUserCard(com, byteKH, ql * 100, cs);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)" +
               "(" + com + "," + baud + "," + kmm
                   + "," + byteKH + "," + dqdm + "," + ql
                   + "," + csql + "," + ccsql + "," + cs
                   + "," + ljgql + "," + bjql + "," + czsx
                   + "," + tzed + "," + sqrq + "," + cssqrq
                   + "," + oldprice + "," + newprice + "," + sxrq
                   + "," + sxbj +","+"result:"+result+")");
            }
            else
            {
                Log.Debug("退气开始：" + "次数:" + cs);
                byte[] byteKH = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                result = MakeUserCard(com, byteKH, 0, cs);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)" +
               "(" + com + "," + baud + "," + kmm
                   + "," + byteKH + "," + dqdm + "," + ql
                   + "," + csql + "," + ccsql + "," + cs
                   + "," + ljgql + "," + bjql + "," + czsx
                   + "," + tzed + "," + sqrq + "," + cssqrq
                   + "," + oldprice + "," + newprice + "," + sxrq
                   + "," + sxbj +","+"result:"+result+ ")");
            }
            return result;
        }
      #endregion

      #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public ShanChengGY()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(1, "操作失败。");
            Errors.Add(2, "卡上有气。");
            Errors.Add(3, "未知卡或不是用户卡。");
            Errors.Add(4, "未知的卡号。");
            Errors.Add(134, "读写器中无卡。");
            Errors.Add(136, "通讯错误。");
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
