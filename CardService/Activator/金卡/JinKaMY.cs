using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class JinKaMY : ICard
    {
        private static Log Log = Log.GetInstance("Card.JinKaMY");

        public string Test()
        {
            return "JinKaMY";
        }

        #region 金卡动态库导入

        //错误信息
        [DllImport("goldcard.dll", EntryPoint = "Error_message", CallingConvention = CallingConvention.StdCall)]
        public static extern void StaticError_message(Int32 ErrorCode, byte[] ErrorMsg);

        //判卡
        [DllImport("goldcard.dll", EntryPoint = "Gold_CheckCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticIsOurCard(Int32 port, Int32 baud);

        //清卡
        [DllImport("goldcard.dll", EntryPoint = "Gold_Formatcard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticClearCard(Int32 port, Int32 baud, byte[] CardNum);

        //开户
        [DllImport("goldcard.dll", EntryPoint = "Gold_Writecard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteUserCard(Int32 port, Int32 baud, byte[] CardNum, Int32 GasNum, Int32 Times, Int32 MeterType);

        //读卡
        [DllImport("goldcard.dll", EntryPoint = "Gold_Readcard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticReadCard(Int32 port, Int32 baud, byte[] CardNum, Int32[] GasNum, Int32[] Times, Int32[] MeterType);

        //购气
        [DllImport("goldcard.dll", EntryPoint = "Gold_Buycard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticBuyGas(Int32 port, Int32 baud, byte[] CardNum, Int32 GasNum, Int32 Times);

        //冲正(未使用)
        [DllImport("goldcard.dll", EntryPoint = "Gold_Clearcard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCleanMoney(Int32 port, Int32 baud, byte[] CardNum);

        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int ret = -1;
            try
            {
                ret = StaticIsOurCard(com, baud);
                Log.Debug("check gas card" + ret);
            }
            catch (Exception e)
            {
                Log.Debug("判卡异常："+e.Message+"---"+e.StackTrace);
            }
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int result = -1;
            try
            {
                byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);

                result = StaticClearCard(com, baud, cardNO);
                Log.Debug("格式化卡结束："+result);
            }
            catch (Exception e)
            {
                Log.Debug("格式化卡异常："+e.Message+"---"+e.StackTrace);
            }
            return result;
        }
         
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)
        {
            int ret = -1;
            try
            {
                byte[] CardNum = new byte[8];
                Int32[] GasNum = new Int32[1];
                Int32[] Times = new Int32[1];
                Int32[] MeterType = new Int32[1];
                ret = StaticReadCard(com, baud, CardNum, GasNum, Times, MeterType);
                kh = System.Text.Encoding.Default.GetString(CardNum).Replace("\0", "");
                ql = GasNum[0];
                cs = (short)Times[0];
                int metertype = MeterType[0];
                Log.Debug("read card ret=" + ret + "--卡号：" + kh + "---气量：" + ql + "---次数：" + cs + "---表类型：" + metertype);
            }
            catch (Exception e)
            {
                Log.Debug("读卡异常："+e.Message+"---"+e.StackTrace);
            } 
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = -1;
            try
            {
                byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                
                if (ql > 0)
                {
                    ret = StaticBuyGas(com, baud, cardNO, ql, cs);
                    Log.Debug("购气结果：" + ret);
                }
                else
                {
                    ret = StaticBuyGas(com, baud, cardNO, 0, cs);
                    Log.Debug("冲正结果：" + ret);
                }
            }
            catch (Exception e)
            {
                Log.Debug("写卡异常："+e.Message+"---"+e.StackTrace);
            }    
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            int ret = -1;
            try
            {
                byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                Log.Debug("start write new card");
                //发卡前先格式化卡
                byte[] CardNum = new byte[8];
                Int32[] GasNum = new Int32[1];
                Int32[] Times = new Int32[1];
                Int32[] MeterType = new Int32[1];
                ret = StaticReadCard(com, baud, CardNum, GasNum, Times, MeterType);
                Log.Debug("格式化前读卡结果：" + ret);
                if (0 == ret)
                {
                    Log.Debug("读卡正常，开始擦卡：");
                    kh = System.Text.Encoding.Default.GetString(CardNum).Replace("\0", "");
                    Log.Debug("擦卡卡号：" + kh);
                    int cret = StaticClearCard(com, baud, cardNO);
                    Log.Debug("擦卡结果：" + cret);
                }
                Log.Debug("开始写新卡:");
                ret = StaticWriteUserCard(com, baud, cardNO, ql, cs, 1);
                Log.Debug("write newcard end ret=" + ret);
            }
            catch (Exception e)
            {
                Log.Debug("写新卡异常："+e.Message+"---"+e.StackTrace);
            }   
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
            return -1;
        }
        public string Name
        {
            get
            {
                return "JinKaMY";
            }
        }

        #endregion
    }
}
