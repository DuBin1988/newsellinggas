using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class YuXiangRC : ICard
    {
        private static Log Log = Log.GetInstance("Card.YuXiangRC");

        public string Test()
        {
            return "荣城宇翔";
        }

        #region 动态库导入
        //读卡函数
        [DllImport("goldcard_rc.dll", EntryPoint = "Gold_Readcard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Readcard(int com, int baud, byte[] kh, ref int ql, ref int cs, ref int type);

        //写卡函数
        [DllImport("goldcard_rc.dll", EntryPoint = "Gold_Writecard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Writecard(int com, int baud, byte[] kh, int ql, int cs, int type);
        //购气函数
        [DllImport("goldcard_rc.dll", EntryPoint = "Gold_Buycard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Buycard(int com, int baud, byte[] kh, int ql, int cs);
        //清卡？
        [DllImport("goldcard_rc.dll", EntryPoint = "Gold_Formatcard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int Gold_Formatcard(int com, int baud, byte[] kh);
        //判卡函数
        [DllImport("goldcard_rc.dll", EntryPoint = "ReadCompany", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCompany();
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
            int ql2=0;
            int cs1 = 0;
            int type = 0;
            byte[] kh1 = new byte[8];
            //清卡前先读卡，针对情况需传入卡号的封装
            Log.Debug("FormatGasCard-->ReadCard(short com, int baud, byte[] kh, ref int ql,ref int cs,ref int type)=(" + com + "," + baud + "," + kh1 + "," + ql2 + "," + cs1 + "," + type+  ")");

            int ret= Gold_Readcard(com, baud, kh1, ref ql2, ref cs1, ref type);
            Log.Debug("FormatGasCard-->ReadCard(short com, int baud, byte[] kh, ref int ql,ref int cs,ref int type)=" + ret );
            Log.Debug("FormatGasCard(short com, int baud, , string kh, string dqdm)=(" + com + "," + baud + "," + kh1  + ")");
            int i = Gold_Formatcard(com, baud, kh1);
            Log.Debug("FormatGasCard(short com, int baud, , string kh, string dqdm)=" + i);
            return i;
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
            int com1 = com;
            int type = 0;
            int ql = 0;
            int cs1 = 0;
            int a = 10;
            byte[] kh1 = new byte[8];
            Log.Debug("CheckGasCard(short com, int baud)=(" + com + "," + baud + ")");
            int i = Gold_Readcard(com1, baud, kh1, ref ql, ref cs1, ref type);
            Log.Debug("StaticCheckGasCard(com, baud)=" + i);
            //用读卡代替判卡 通过type判断是否是金卡民用 
            if (type == 1)
            {
                a = 0;
            }
            return a;
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
            int com1 = com;
            int cs1 = cs;
            int type = 0;
            byte[] kh1 = new byte[8];
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=("
                + com + "," + baud + "," + kh + "," + ql
                + money + "," + cs + "," + bkcs +  ")");
            int i = Gold_Readcard(com1, baud, kh1, ref ql, ref cs1, ref type);
            kh = Encoding.ASCII.GetString(kh1, 0, 8);
            Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)=" +
                "(" + com + "," + baud + "," + kh + "," + ql
                + money + "," + cs + "," + bkcs + ")=" + i);
            cs = (short)cs1;
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
            int com1 = com;
            byte[] kh1 = new byte[8];
            kh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
               + com + "," + baud + "," + kmm + "," + kh
               + dqdm + "," + ql + "," + csql + "," + ccsql
               + cs + "," + ljgql + "," + bjql + "," + czsx
               + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
               + newprice + "," + sxrq + "," + sxbj
               + ")");
            int i;
            //气量大于0代表购气
            if (ql > 0)
            {
                i = Gold_Buycard(com1, baud, kh1, ql, cs);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com1 + "," + baud + "," + kmm + "," + kh1
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + i);
            }
            else{
                i = Gold_Buycard(com1, baud, kh1, ql, cs);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + i);
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
        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            int com1 = com;
            int cs1 = 0;
            int type = 0;
            byte[] kh2 = new byte[8];
            int ql2 = 0;
            int type1 = 0;
            //写新卡之前先清卡 清卡之前先读卡
            Gold_Readcard(com1, baud, kh2, ref ql2, ref cs1, ref type);
            Gold_Formatcard(com, baud, kh2);
            int klx = 1;//代表民用
            byte[] kh1 = new byte[8];

            kh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
            Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
             + com + "," + baud + "," + kmm + "," + kh
             + dqdm + "," + ql + "," + csql + "," + ccsql
             + cs + "," + ljgql + "," + bjql + "," + czsx
             + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
             + newprice + "," + sxrq + "," + sxbj
             + ")");
            
          
              int  i = Gold_Writecard(com, baud, kh1, ql, cs, klx);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                 + com + "," + baud + "," + kmm + "," + kh
                 + dqdm + "," + ql + "," + csql + "," + ccsql
                 + cs + "," + ljgql + "," + bjql + "," + czsx
                 + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                 + newprice + "," + sxrq + "," + sxbj
                 + ")=" + i);
           
          
            return i;
        }

        public string Name
        {
            get
            {
                return "JinKaRC";
            }
        }

        #endregion
    }
}
