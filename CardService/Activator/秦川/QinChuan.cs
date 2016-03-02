using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class QinChuan : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.QinChuan");

        #region 秦川动态库导入

        //发卡/补卡
        [DllImport("WriteCardInterface.dll", EntryPoint = "OpenCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticOpenCard(int niComHandle, int niCompanyCode, byte[] nilicense, int niOperateType,
            int niMeterType, long nsMeterID, byte[] nCardCode, int niBuyGasOne, int niBuyGasTwo, int niBuyGasThree, int niBuyTimes);

        //判卡
        [DllImport("WriteCardInterface.dll", EntryPoint = "CheckCompany", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticIsOurCard(int niComHandle, ref int isTrue);

        //清卡
        [DllImport("WriteCardInterface.dll", EntryPoint = "InitCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticClearCard(int niComHandle, int niCompanyCode, byte[] nilicense);


        //读卡
        [DllImport("WriteCardInterface.dll", EntryPoint = "ReadData", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadCard(int niComHandle, int niCompanyCode, byte[] nilicense, ref long nsMeterID,
            byte[] nCardCode, ref int niBuyGas, ref int niBuyTimes);

        //购气
        [DllImport("WriteCardInterface.dll", EntryPoint = "BuyGas", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticBuyGas(int niComHandle, int niCompanyCode, byte[] nilicense,
            byte[] nCardCode, int niBuyGasOne, int niBuyGasTwo, int niBuyGasThree, int niBuyTimes);

        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);
        #endregion

        public string Test()
        {
            return "QinChuan";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int result = -1;
            try
            {
                int ComHandle = ic_init(com, baud);

                int istrue = -1;
                result = StaticIsOurCard(ComHandle, ref istrue);
                Log.Debug("判卡结果：" + result);
                ic_exit(ComHandle);
            }
            catch (Exception e)
            {
                Log.Debug("判卡异常：" + e.Message + "---" + e.StackTrace);
            }
            return result;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int result = -1;
            try
            {
                int ComHandle = ic_init(com, baud);
                byte[] license = System.Text.Encoding.GetEncoding(1252).GetBytes("55873939513160119");
                result = StaticClearCard(ComHandle, 0, license);
                ic_exit(ComHandle);
                Log.Debug("clear card end:" + result);
            }
            catch (Exception e)
            {
                Log.Debug("擦卡异常:" + e.Message + "---" + e.StackTrace);
            }
            return result;
        }

        public int OpenCard(short com, int baud)
        {
            return -1;
        }


        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            int ret = -1;
            try
            {
                int ComHandle = ic_init(com, baud);
                byte[] cardno = new byte[8];
                byte[] license = System.Text.Encoding.GetEncoding(1252).GetBytes("55873939513160119");
                long meterid = 0;
                int css = (int)cs;
                ret = StaticReadCard(ComHandle, 0, license, ref meterid, cardno, ref ql, ref css);
                ic_exit(ComHandle);
                //卡号转换成字符串
                kh = Encoding.ASCII.GetString(cardno, 0, 8);
                cs = (short)css;
                yhh = meterid + "";
                Log.Debug("卡号：" + kh + "---气量:" + ql + "--次数：" + cs + "---表号：" + meterid);
            }
            catch (Exception e)
            {
                Log.Debug("读卡异常:" + e.StackTrace + "----" + e.Message);
            }

            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = -1;
            try
            {
                int ComHandle = ic_init(com, baud);
                byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                byte[] license = System.Text.Encoding.GetEncoding(1252).GetBytes("55873939513160119");

                int css = (int)cs;
                Log.Debug("写卡参数：" + "卡号：" + kh + "--本次气量：" + ql + "---上次气量：" + csql + "---上上次气量：" + ccsql + "--次数：" + css);
                if (ql > 0)
                {
                    ret = StaticBuyGas(ComHandle, 0, license, cardno, ql, csql, ccsql, css);
                    ic_exit(ComHandle);
                    Log.Debug("购气结果：" + ret);
                }
                else
                {
                    ret = StaticBuyGas(ComHandle, 0, license, cardno, 0, csql, ccsql, css);
                    ic_exit(ComHandle);
                    Log.Debug("退气结果：" + ret);
                }
            }
            catch (Exception e)
            {
                Log.Debug("购气异常:" + e.Message + "----" + e.StackTrace);
            }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            int ret = -1;
            try
            {
                int ComHandle = ic_init(com, baud);

                byte[] license = System.Text.Encoding.GetEncoding(1252).GetBytes("55873939513160119");
                ret = StaticClearCard(ComHandle, 0, license);
                ic_exit(ComHandle);
                Log.Debug("清卡结束：" + ret + "kzt:" +kzt);

                byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

                if (0 == kzt)
                {
                    int ComHandle1 = ic_init(com, baud);
                    Log.Debug("发卡开始，参数:卡号:" + kh + "--气量：" + ql + "--次数:" + cs + "卡类别：" + klx + "串口句柄：" + ComHandle1);
                    ret = StaticOpenCard(ComHandle1, 0, license, 0, klx, 0, cardno, ql, 0, 0, cs);
                    ic_exit(ComHandle1);

                    Log.Debug("发卡结束:" + ret);
                }
                else
                {
                    int bh;
                    if (0 == klx)
                    {
                        meterid = "0";
                        csql = 0;
                        ccsql = 0;
                    }
                    bh = int.Parse(meterid);
                    int ComHandle2 = ic_init(com, baud);
                    Log.Debug("补卡开始,参数：卡号：" + kh + "--表号:" + bh + "--次数:" + cs + "--气量：" + ql + "卡类别：" + klx + "串口句柄：" + ComHandle2);
                    ret = StaticOpenCard(ComHandle2, 0, license, 1, klx, bh, cardno, ql, csql, ccsql, cs);
                    ic_exit(ComHandle2);
                    Log.Debug("补卡结束" + ret);
                }
            }
            catch (Exception e)
            {
                Log.Debug("操作新卡异常：" + e.Message + e.StackTrace);
            }

            return ret;
        }

        public string Name
        {
            get
            {
                return "QinChuan";
            }
        }

        #endregion
         
          #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public QinChuan()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(0, "成功。");
            Errors.Add(1, "串口句柄无效。");
            Errors.Add(2, "读卡器错误。");
            Errors.Add(3, "无卡。");
            Errors.Add(4, "IC卡类型错误或卡插反。");
            Errors.Add(5, "新卡。");
            Errors.Add(6, "工具卡。");
            Errors.Add(7, "卡已使用。");
            Errors.Add(8, "不是本厂卡。");
            Errors.Add(9, "读卡错误。");
            Errors.Add(10, "写卡错误。");
            Errors.Add(11, "核对密码错误。");
            Errors.Add(12, "修改密码错误。");
            Errors.Add(13, "参数传入错误或卡坏。");
            Errors.Add(14, "数据转换错误。");
            Errors.Add(15, "数据转换错误。");
            Errors.Add(16, "写卡数据校验错误。");
            Errors.Add(17, "未注册。");
            Errors.Add(18, "注册时间到期。");
            Errors.Add(19, "注册信息有误。");
            Errors.Add(20, "使用日期不对。");
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
