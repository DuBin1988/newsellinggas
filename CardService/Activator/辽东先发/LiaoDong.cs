using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class LiaoDong : ICard,IVerbose
    {
        private static Log Log = Log.GetInstance("Card.LiaoDong");

        public string Test()
        {
            return "LiaoDong";
        }

        #region 动态库导入
        //读卡函数

        [DllImport("DR_Soft.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 ReadGasCard(Int16 com, Int32 baut, byte[] kh, ref Int32 ql, ref Int32 ljgql, ref Int32 syql);

        [DllImport("DR_Soft.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 WriteGasCard(Int16 com, Int32 baut, byte[] kh, Int32 ql, byte[] gqrq);

        [DllImport("DR_Soft.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 WriteNewCard(Int16 com, Int32 baut, byte[] kh, Int32 ql, byte[] gqrq);
        /*
            Com：串口号 com1为1,com2为2。
            Baut：串口通讯波特率（1200－115200）。
            Kh：用户卡号（00000000－99999999）。
            ql ：气量。
            sqrq：8位字符串如“20140808”
         */
        [DllImport("DR_Soft.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 FormatGasCard(Int16 com, Int32 baut);

        [DllImport("DR_Soft.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern Int32 CheckCard(Int16 com, Int32 baut);
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
            Log.Debug("FormatGasCard(short com)=(" + com + "," + baud + ")");
            int i = FormatGasCard(com, baud);
            Log.Debug("FormatGasCard(short com)=(" + com + "," + baud + ")="+i);
            return i;
        }
        public int OpenCard(Int16 com, Int32 baud)
        {
            throw new NotImplementedException();
        }
        /// <summary>
        /// 判卡
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int CheckGasCard( Int16 com,  Int32 baud  ) {
            Log.Debug("CheckGasCard(short com, int baud)=(" + com + "," + baud + ")");
            int i = -1;
            try
            {
                i = CheckCard(com, baud);
            }
            catch (Exception ee)
            {
                Log.Debug("检测卡异常:"+ee.StackTrace+"---"+ee.Message);
            }
            
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
            byte[] cardno = new byte[8];          
            Int32 ljgql = 0;
            Int32 syql = 0;
            int result = -1;
            try
            {
                result = ReadGasCard(com, baud, cardno, ref ql, ref ljgql, ref syql);
                kh = Encoding.ASCII.GetString(cardno, 0, 8);
            }
            catch (Exception ee)
            {
                Log.Debug("读卡异常:"+ee.Message+"----"+ee.StackTrace);
            }
           
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=" +
                 "(" + com + "," + baud + "," + kh + "," + ql + ")");
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
            int result = -1;
            if (ql < 0)
            {
                ql = 0;
            }
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                + com + "," + baud + "," + kmm + "," + kh
                + dqdm + "," + ql + "," + csql + "," + ccsql
                + cs + "," + ljgql + "," + bjql + "," + czsx
                + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                + newprice + "," + sxrq + "," + sxbj
                + ")");          
            try 
	            {	        
		             byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                     byte[] gasdate = System.Text.Encoding.GetEncoding(1252).GetBytes(sqrq);
                     result = WriteGasCard(com, baud, cardno, ql, gasdate);
	            }
	            catch (Exception ee)
	            {
		            Log.Debug("写卡异常："+ee.Message+"----"+ee.StackTrace);
	            }
           
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + result);               
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
            int result = -1;
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                  + com + "," + baud + "," + kmm + "," + kzt
                  + kh + "," + dqdm + "," + yhh + "," + tm
                  + ql + "," + csql + "," + ccsql + "," + cs
                  + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                  + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                  + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                  + ")");
           result = FormatGasCard(com, baud);
           Log.Debug("格式化卡结果:"+result);
           byte[] cardNo = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            byte[] gasdate = System.Text.Encoding.GetEncoding(1252).GetBytes(sqrq);
           if(0 == kzt)
           {
               cs = 1;
           }
           result = WriteNewCard(com, baud, cardNo, ql, gasdate);
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                    + com + "," + baud + "," + kmm + "," + kzt
                    + kh + "," + dqdm + "," + yhh + "," + tm
                    + ql + "," + csql + "," + ccsql + "," + cs
                    + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                    + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                    + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                    + ")=" + result);
            return result;
        }

        public string Name
        {
            get
            {
                return "XianFeng";
            }
        }

        #endregion
             #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public LiaoDong()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
           
            Errors.Add(-2, "串口打开失败。");
            Errors.Add(-3, "设备状态失败。");
            
            Errors.Add(-4, "读卡器中没有插卡。");
            Errors.Add(-5, "读卡密码次数失败");
            Errors.Add(-6, "该卡已经损坏。");
            Errors.Add(-7, "读卡失败。");
            Errors.Add(-8, "不是用户卡。");
           
            Errors.Add(-9, "密码校验失败。");
            Errors.Add(-10, "写卡出错。");
            Errors.Add(-12, "关闭通讯串口失败。");
            Errors.Add(-13, "该卡可能是新卡或插反。");
            Errors.Add(-14, "该卡非本系统卡。");
            Errors.Add(-15, "该卡不是新卡。");
            Errors.Add(-16, "用户卡号与卡内的值不匹配。");
            Errors.Add(-17, "清卡失败。");
            Errors.Add(-18, "气量超限。");
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
