using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class XianFeng : ICard,IVerbose
    {
        private static Log Log = Log.GetInstance("Card.XianFeng");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //读卡函数

        [DllImport("WRwCard.dll", EntryPoint = "ReadCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public unsafe static extern int ReadCard(int com,out byte* cardNo, int* BuyGasNum, int* UpdateFlag);

        //写卡函数
        [DllImport("WRwCard.dll", EntryPoint = "WriteUser", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteUser(int com,byte[] cardNo, byte[] OldcardNo, int BuyGasNum, int UpdateFlag);
        //清卡
        [DllImport("WRwCard.dll", EntryPoint = "FormatCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int FormatCard(int com);
        //判卡函数
        [DllImport("WRwCard.dll", EntryPoint = "ReadCompany", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCompany(int com);
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
            int i = FormatCard(com+1);
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
            int i = ReadCompany(com+1);
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
           int i=10;
           unsafe
           {
               byte* cardNo;
               int quantity;
               int flag;
             
               i = ReadCard(com+1,out cardNo, &quantity, &flag);
               Log.Debug("ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs)=" +
                 "(" + com + "," + baud + "," + kh + "," + ql
                 + money + "," + cs + "," + bkcs + ")=" + i);
               while (*cardNo != 0)
               {
                   kh += Convert.ToChar(*cardNo++);
               }
               ql = quantity;
           }
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
            int i = 10;
            byte[] cardNo1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            unsafe
            {

                int flag = 0;
                int quantity = ql;
                if (ql > 0)
                { 
                    i = WriteUser(com+1,cardNo1, cardNo1, quantity, flag);
                Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + i);
                }
                else {
                    byte* cardNo;
                    int quantity1;
                    Log.Debug("XF ReadCard start ");
                    i = ReadCard(com+1,out cardNo, &quantity, &flag);
                    Log.Debug("XF ReadCard end and i is " + i + " quantity is " + quantity + " flag is " + flag + "ql is " + ql);
                    while (*cardNo != 0)
                    {
                        kh += Convert.ToChar(*cardNo++);
                    }

                    flag = 0;
                    quantity1 = quantity + ql;
                    i = WriteUser(com+1,cardNo1, cardNo1, quantity1, flag);
                   Log.Debug("WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                   + com + "," + baud + "," + kmm + "," + kh
                   + dqdm + "," + ql + "," + csql + "," + ccsql
                   + cs + "," + ljgql + "," + bjql + "," + czsx
                   + tzed + "," + sqrq + "," + cssqrq + "," + oldprice
                   + newprice + "," + sxrq + "," + sxbj
                   + ")=" + i);
                }
          
             
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
            Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                  + com + "," + baud + "," + kmm + "," + kzt
                  + kh + "," + dqdm + "," + yhh + "," + tm
                  + ql + "," + csql + "," + ccsql + "," + cs
                  + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                  + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                  + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                  + ")");
            FormatCard(com);
            int i = 10;
            byte[] cardNo1 = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

            unsafe
            {

                int flag = 12;
                int quantity = ql;
                i=WriteUser(com+1,cardNo1, cardNo1, quantity, flag);
                Log.Debug("WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)=("
                    + com + "," + baud + "," + kmm + "," + kzt
                    + kh + "," + dqdm + "," + yhh + "," + tm
                    + ql + "," + csql + "," + ccsql + "," + cs
                    + ljgql + "," + bkcs + "," + ljyql + "," + bjql
                    + czsx + "," + tzed + "," + sqrq + "," + cssqrq
                    + oldprice + "," + newprice + "," + sxrq + "," + sxbj
                    + ")=" + i);
            }

            return i;
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
        public XianFeng()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(-100, "判卡失败。");
            Errors.Add(-101, "串口打开失败。");
            Errors.Add(-102, "卡片加电失败。");
            Errors.Add(-103, "重新操作。");
            Errors.Add(-104, "读卡器中没有插卡。");
            Errors.Add(-105, "不是SLE4442卡。");
            Errors.Add(-106, "清卡失败。");
            Errors.Add(-107, "读卡失败。");
            Errors.Add(-110, "读取卡片数据失败。");
            Errors.Add(-111, "数据转换失败。");
            Errors.Add(-112, "密码校验失败。");
            Errors.Add(-113, "写卡出错。");
            Errors.Add(-114, "公司代码不匹配。");
            Errors.Add(-115, "不是空白卡。");
            Errors.Add(-116, "密码修改出错。");
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
