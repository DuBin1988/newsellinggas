using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using ICard;

namespace Card
{
    public class LunLi : ICard, IVerbose
    {
        private static Log Log = Log.GetInstance("Card.LunLi");

        #region 伦力动态库导入

        //发卡/补卡
        [DllImport("SmartCard.dll", EntryPoint = "ic_writeorder", CallingConvention = CallingConvention.StdCall)]
        public unsafe static extern int StaticOpenCard(int com, byte[] dllpsw, byte[] s_companycode, int niOperateType,
            byte[] userid, byte[] MeterID, byte[] orderlsh, byte[] keycode, double price, double totalmoney, int pricesign);
        //读卡
        [DllImport("SmartCard.dll", EntryPoint = "ic_readcard", CallingConvention = CallingConvention.StdCall)]
        public unsafe static extern int StaticReadCard(int com, byte[] s_companycode, ref int cardtype, byte[] keycode,
           out byte* userid, out byte* MeterID, byte[] orderlsh, ref double price, ref double totalmoney, ref int readflag);

        //清卡
        [DllImport("SmartCard.dll", EntryPoint = "ic_tool", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticClearCard(int niComHandle, int niCompanyCode, byte[] nilicense);

        //购气
        [DllImport("SmartCard.dll", EntryPoint = "ic_writeorder", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticBuyGas(int com, byte[] dllpsw, byte[] s_companycode, int niOperateType,
            byte[] userid, byte[] MeterID, byte[] orderlsh, byte[] keycode, double price, double totalmoney, int pricesign);

        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);
        #endregion

        public string Test()
        {
            return "LunLi";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int ret = -1;
            try
            {
                unsafe
                {                
                    byte[] s_companycode = new byte[10];
                    int cardtype = 0;
                    byte[] keycode = new byte[20];
                    byte* MeterID;
                    byte[] orderlsh = new byte[16];
                    double price = 0;
                    double totalmoney = 0;
                    int readflag = 0;
                    byte* userid;
                    ret = StaticReadCard((int)com, s_companycode, ref cardtype, keycode, out userid, out MeterID, orderlsh, ref price, ref totalmoney, ref readflag);
                }
                Log.Debug("判卡结果：" + ret);
            }
            catch (Exception e)
            {
                Log.Debug("判卡异常：" + e.Message + "---" + e.StackTrace);
            }
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int result = -1;
            try
            {
               
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
                unsafe
                {                
                    byte[] s_companycode = new byte[10];
                    int cardtype = 0;
                    byte[] keycode = new byte[20];
                    byte* MeterID;
                    byte[] orderlsh = new byte[16];
                    double price = 0;
                    double totalmoney = 0;
                    int readflag = 0;
                    byte* userid;
                    int result = StaticReadCard((int)com, s_companycode, ref cardtype, keycode, out userid, out MeterID, orderlsh, ref price, ref totalmoney, ref readflag);
                   
                    string meterid = "";
                    while (*userid != 0)
                    {
                        kh += Convert.ToChar(*userid++);
                    }

                    while (*MeterID != 0)
                    {
                        meterid += Convert.ToChar(*MeterID++);

                    }
                    Log.Debug("卡号：" + kh + "---金额:" + totalmoney + "--表型：" + cardtype + "---表号：" + meterid);
                }
               
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
               
                byte[] s_companycode = new byte[10];
                int cardtype = 0;
                byte[] keycode = new byte[20];
                byte[] dllpsw = System.Text.Encoding.GetEncoding(1252).GetBytes("20D1DA9B8399C77D"); ;
                byte[] userid = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                byte[] meterid = System.Text.Encoding.GetEncoding(1252).GetBytes("7654321");
                byte[] orderid = System.Text.Encoding.GetEncoding(1252).GetBytes("0120141120000013");
                byte[] orderlsh = new byte[16];
                double price = (double)newprice;
                double totalmoney = price * ql;
                int readflag = 0;
               
                int css = (int)cs;
                Log.Debug("写卡参数：" + "卡号：" + kh + "--本次气量：" + ql);
                if (ql > 0)
                {
                    ret = StaticBuyGas(com, dllpsw, s_companycode, 1, userid, meterid, orderid, keycode, price, totalmoney, 0);
                    Log.Debug("购气结果：" + ret);
                }
                else
                {
                    ret = StaticBuyGas(com, dllpsw, s_companycode, 1, userid, meterid, orderid, keycode, price, 0, 0);
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
                
               //清卡
                Log.Debug("清卡结束：" + ret + "kzt:" +kzt);

                byte[] cardno = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                
                    Log.Debug("制卡开始，参数:卡号:" + kh + "--气量：" + ql);
                    byte[] s_companycode = new byte[10];
                    int cardtype = 0;
                    byte[] keycode = new byte[20];
                    byte[] dllpsw = System.Text.Encoding.GetEncoding(1252).GetBytes("20D1DA9B8399C77D"); ;
                    byte[] userid = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                    byte[] meterno = System.Text.Encoding.GetEncoding(1252).GetBytes("7654321");
                    byte[] orderid = System.Text.Encoding.GetEncoding(1252).GetBytes("0120141120000013");
                    byte[] orderlsh = new byte[16];
                    double price = (double)newprice;
                    double totalmoney = ql * price;
                    int readflag = 0;
                    ret = StaticOpenCard((int)com, dllpsw, s_companycode, 0, userid, meterno, orderid, keycode, price, totalmoney, 0);
                    Log.Debug("制卡结束:" + ret+"---买气开始:");
                    ret = StaticOpenCard((int)com, dllpsw, s_companycode, 1, userid, meterno, orderid, keycode, price, totalmoney, 0);
                    Log.Debug("售气结束:" + ret);              
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
                return "LunLi";
            }
        }

        #endregion
         
          #region IVerbose 实现 在此实现错误代码与提示信息的对应关系

        /// <summary>
        /// 存错误码不在GenericService的Errors数组中的错误错误码和错误信息，通常是表厂自己的错误信息。
        /// </summary>
        private Dictionary<int, string> Errors = new Dictionary<int, string>();
        public LunLi()
        {
            ///<code>
            ///Errors.Add(-9999, "未知错误。");
            ///
            Errors.Add(99, "参数错。");
            Errors.Add(129, "写数据时出错。");
            Errors.Add(131, "IC卡密码错误。");
            Errors.Add(134, "读写器中无卡。");
            Errors.Add(137, "卡型错误（非4442卡，卡插反）。");
            Errors.Add(136, "通讯错误，可能是串口设置、读写器连接有问题。");
            Errors.Add(160, "不是新卡（开户时必须是新卡。");
            Errors.Add(161, "用户编号不符。");
            Errors.Add(162, "卡已报废。");
            Errors.Add(164, "非本系统卡，公司代码错。");
            Errors.Add(999, "未知错误。");
           
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
