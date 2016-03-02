
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class YinHe : ICard
    {
        private static Log Log = Log.GetInstance("Card.YinHe");

        public string Test()
        {
            return "银河";
        }
        #region
        //判卡函数
        [DllImport("JSYHDLL.dll", EntryPoint = "CheckGasCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int CheckGasCard(int com, int baud);

        //读卡函数
        [DllImport("JSYHDLL.dll", EntryPoint = "ReadGasCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadGasCard(int com, int baud, StringBuilder kmm,ref int klx,ref int kzt, StringBuilder kh, StringBuilder dqdm, StringBuilder yhh, StringBuilder tm,ref  int ql,ref  int cs,ref int ljgql,ref int bkcs,ref  int ljyql,ref  int syql,ref  int bjql, ref int czsx,ref  int tzed, StringBuilder sqrq);
        //开户函数
        [DllImport("JSYHDLL.dll", EntryPoint = "WriteNewCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteNewCard(int com, int baud, StringBuilder kmm, int klx, int kzt, StringBuilder kh, StringBuilder dqdm, StringBuilder yhh, StringBuilder tm,int ql, int cs, int ljgql, int bkcs, int ljyql, int bjql,int czsx, int tzed,StringBuilder sqrq);
        //清卡？
        [DllImport("JSYHDLL.dll", EntryPoint = "FormatGasCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int FormatGasCard(int com, int baud, StringBuilder kmm, int klx, StringBuilder kh, StringBuilder sdqdm);
        //购气函数
        //(ByVal com As Long, ByVal baud As Long, ByVal kmm As String, ByVal klx As Long, ByVal kh As String, ByVal dqdm As String, ByVal ql As Long, ByVal cs As Long, ByVal ljgql As Long, ByVal bjql As Long, ByVal czsx As Long, ByVal tzed As Long, ByVal sqrq As String) As Long
        [DllImport("JSYHDLL.dll", EntryPoint = "WriteGasCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteGasCard(int com, int baud, StringBuilder kmm, int klx, StringBuilder kh, StringBuilder dqdm,  int ql, int cs, int ljgql,  int bjql, int czsx, int tzed, StringBuilder sqrq);
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
           
            StringBuilder kh1 = new StringBuilder();
            int ql = 10;
            int cs = 1;
            int ljgql = 10;
            int ljyql = 0;
            int bkcs = 0;
            int syql = 0;
            int bjql = 0;
            int czsx = 0;
            int tzed = 0;
            int klx=0;
            int kzt=0;
            StringBuilder kmm1 = new StringBuilder("12345678");
            StringBuilder sqrq1 = new StringBuilder(DateTime.Now.ToString("yyyymmddhhmmss"));
            StringBuilder dqdm1 = new StringBuilder();
            StringBuilder yhh = new StringBuilder();
            StringBuilder tm = new StringBuilder();
            string sqrq = "";
            //清卡需传入卡号 读卡获取用户卡号
            Log.Debug("YinHe Read before  Format start");
            int i = ReadGasCard(com, baud, kmm1, ref klx, ref kzt, kh1, dqdm1, yhh, tm, ref ql, ref cs, ref ljgql, ref bkcs, ref ljyql, ref syql, ref  bjql, ref  czsx, ref  tzed, sqrq1);
            Log.Debug("YinHe Read Before Format end and i is " + i);
             Log.Debug("YinHe Format Card start and kh is "+kh1+"kmm is "+kmm1 );
             i = FormatGasCard(com, baud, kmm1, klx, kh1, dqdm1);
             Log.Debug("YinHe Format Card end and i is " + i);
      
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
        public int CheckGasCard(

              Int16 com,          //串口号，从0开始
              Int32 baud          //波特率
              )
        {
   
            Log.Debug("YinHe Check Card start");
            StringBuilder kh1 = new StringBuilder();
            int ql1 = 0;
            int cs1 = 0;
            int ljgql = 0;
            int ljyql = 0;
            int bkcs1 = 0;
            int syql = 0;
            int bjql = 0;
            int czsx = 0;
            int tzed = 0;
            int klx = 0;
            int kzt = 0;
            StringBuilder kmm = new StringBuilder("12345678");
            StringBuilder sqrq = new StringBuilder(DateTime.Now.ToString("yyyymmddhhmmss"));
            StringBuilder dqdm = new StringBuilder();
            StringBuilder yhh = new StringBuilder();
            StringBuilder tm = new StringBuilder();
            Log.Debug("YinHe Read Card start");
            int i = ReadGasCard(com, baud, kmm, ref klx, ref kzt, kh1, dqdm, yhh, tm, ref ql1, ref cs1, ref ljgql, ref bkcs1, ref ljyql, ref syql, ref  bjql, ref  czsx, ref  tzed, sqrq);
            Log.Debug("YinHe Read　Card  end  and kh is " + kh1 + " ql is " + ql1 + " cs is " + cs1 + " i is " + i);

            if (i == 2) {
                i = 0;
            }
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
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dm)
        {

            StringBuilder kh1 = new StringBuilder();
            int ql1 = 0;
            int cs1 = 0;
            int ljgql = 0;
            int ljyql = 0;
            int bkcs1 = 0;
            int syql = 0;
            int bjql = 0;
            int czsx = 0;
            int tzed = 0;
            int klx = 0;
            int kzt = 0;
            StringBuilder kmm = new StringBuilder("12345678");
            StringBuilder sqrq = new StringBuilder(DateTime.Now.ToString("yyyymmddhhmmss"));
            StringBuilder dqdm = new StringBuilder();
            StringBuilder yhh = new StringBuilder();
            StringBuilder tm = new StringBuilder();
            Log.Debug("YinHe Read Card start");
            int i = ReadGasCard(com, baud, kmm, ref klx, ref kzt, kh1, dqdm, yhh, tm, ref ql1, ref cs1, ref ljgql, ref bkcs1, ref ljyql, ref syql, ref  bjql, ref  czsx, ref  tzed, sqrq);
            Log.Debug("YinHe Read　Card  end  and kh is " + kh1 + " ql is " + ql1 + " cs is " + cs1 + " i is " + i);
            kh = kh1.ToString();
            ql = ql1/100;
            cs = (short)cs1;
            if (i == 2)
            {
                i = 0;
            } return i;

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
            StringBuilder kh1 = new StringBuilder(kh);
            int ljgql1 = 0;
            int ljyql1 = 0;
            int bkcs1 = 0;
            int syql1 = 0;
            int bjql1 = 0;
            int czsx1 = 0;
            int tzed1 = 0;
            int klx1 = 1;
            int kzt1 = 0;
            StringBuilder kmm1 = new StringBuilder("12345678");
            StringBuilder sqrq1 = new StringBuilder(DateTime.Now.ToString("yyyymmddhhmmss"));
            StringBuilder dqdm1
                               = new StringBuilder();
            StringBuilder yhh1 = new StringBuilder();
            StringBuilder tm1 = new StringBuilder();
            int i = 0;
            //气量大于0代表购气
            if (ql > 0) { 
             i = WriteGasCard(com, baud, kmm1, klx1, kh1, dqdm1, ql, cs, ljgql1, bjql1, czsx1, tzed1, sqrq1);
            }
            else
            {
               //气量<=0代表冲正 读卡 读出卡上原有气量  前提条件为卡内有气不能购气
                i = WriteGasCard(com, baud, kmm1, klx1, kh1, dqdm1, 0, cs, ljgql1, bjql1, czsx1, tzed1, sqrq1);

            }
          
            return i;
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

            StringBuilder kh1 = new StringBuilder(kh);
            ql = ql * 100;
            int ljgql1 = 0;
            int ljyql1 = 0;
            int bkcs1 = 0;
            int syql1 = 0;
            int bjql1 = 0;
            int czsx1 = 0;
            int tzed1 = 0;
            int klx1 = 2;
            int kzt1 = 0;
            StringBuilder kmm1 = new StringBuilder("12345678");
            StringBuilder sqrq1 = new StringBuilder(DateTime.Now.ToString("yyyymmddhhmmss"));
            StringBuilder dqdm1= new StringBuilder();
            StringBuilder yhh1 = new StringBuilder();
            StringBuilder tm1 = new StringBuilder();
            int i = WriteNewCard(com, baud, kmm1, klx1, kzt1, kh1, dqdm1, yhh1, tm1, ql, cs, ljgql1, bkcs1, ljyql1, bjql1, czsx1, tzed1, sqrq1);
            Log.Debug("YinHe WriteNewCard End and i is " + i);
          
            return i;
        }

        public string Name
        {
            get
            {
                return "YinHe";
            }
        }

        #endregion
    }
}
