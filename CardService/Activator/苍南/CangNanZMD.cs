using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class CangNan : ICard
    {
        private static Log Log = Log.GetInstance("Card.CangNan");

        public string Test()
        {
            return "CangNan";
        }

        #region 苍南动态库导入

        //读卡函数
        [DllImport("CNDLL.dll", EntryPoint = "ReadCard", SetLastError = true,
            CharSet = CharSet.None, ExactSpelling = false,
            CallingConvention = CallingConvention.StdCall)]
        public unsafe static extern int readCard(int PortNo, int Baud, out byte* cardNO, ref int CardSpare, ref int UsedGas, ref int BoughtGas,
              ref int GasCount, byte[] MeterType, ref int CardType, ref int AlarmValue, ref Int64 InputValue, ref int OverValue,
                out byte* CardCount, byte[] Remarks, byte[] Regist, ref double Price, ref int Err);
        //初始化函数
        [DllImport("CNDLL.dll", EntryPoint = "InitCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int InitCard(Int32 port, Int32 baud, ref Int32 err);
        //写卡，标准接口
        [DllImport("CNDLL.dll", EntryPoint = "WriteCard", CharSet = CharSet.Ansi)]
        public static extern int WriteCard(int Port, int Baud, string CardId, int CardSpare, short CardCount, double Price,
              string Regist, int AlarmValue, int InputValue, int OverValue, int BuyGasCount, string MeterType,
              int CardType, string Remarks, ref int Err, int OPCode);
        //擦卡，标准接口
        [DllImport("CNDLL.dll", EntryPoint = "ClearCard", CharSet = CharSet.Ansi)]
        public static extern int ClearCard(Int32 port, Int32 baud, ref Int32 err);

        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int ql = 0;
            int bnql = 0;
            int boughtgas = 0;
            int cs = 0;
            byte[] metertype = new byte[5];
            int klx = 0;
            int alarmgas = 0;
            Int64 inputvalue = 99999;
            int overvalue = 0;

            byte[] dqdm = new byte[5];
            byte[] regist = new byte[10];
            double price = 1.5;
            int err = 0;

            int ret = -1;
            try
            {
                unsafe
                {
                    byte* bkcs;
                    byte* cardNo;
                    Log.Debug("判卡开始:");
                    ret = readCard(com, baud, out cardNo, ref ql, ref bnql, ref boughtgas, ref cs, metertype,
                        ref klx, ref alarmgas, ref inputvalue, ref overvalue,
                       out bkcs, dqdm, regist, ref price, ref err);

                    Log.Debug("判卡结束：" + ret);
                }
                if (0 != ret)
                {
                    return -1;
                }

            }
            catch (Exception ex)
            {
                Log.Debug("判卡异常：" + ex.Message + "---" + ex.StackTrace);
            }
            return 0;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int result = -1;
            Int32 Err = 0;
            try
            {
                Log.Debug("格式化卡开始：");
                result = ClearCard(com, baud, ref Err);
                Log.Debug("格式化卡结束：" + result);
            }
            catch (Exception e)
            {
                Log.Debug("格式化卡异常：" + e.Message + "---" + e.StackTrace);
            }
            return result;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref Int16 bkcs, ref string yhh)
        {
            int ret = -1;
            int bnql = 0;
            int boughtgas = 0;
            int css = (int)cs;
            byte[] metertype = new byte[5];
            int klx = 0;
            int alarmgas = 0;
            Int64 inputvalue = 99999;
            int overvalue = 0;

            byte[] dqdm = new byte[5];
            byte[] regist = new byte[10];
            double price = 1.5;
            int err = 0;
            try
            {
                unsafe
                {
                    byte* bkcss;
                    byte* cardNo;
                    Log.Debug("读卡开始：");
                    ret = readCard(com, baud, out cardNo, ref ql, ref bnql, ref boughtgas, ref css, metertype,
                        ref klx, ref alarmgas, ref inputvalue, ref overvalue,
                       out bkcss, dqdm, regist, ref price, ref err);
                    Log.Debug("读卡结束:" + ret);
                    while (*cardNo != 0)
                    {
                        kh += Convert.ToChar(*cardNo++);
                    }
                    cs = (short)css;
                    // int a = (*bkcss - 48);
                    int a = (*(bkcss + sizeof(byte)) - 48);
                    bkcs = (short)a;
                }
            }
            catch (Exception ex)
            {
                Log.Debug("读卡异常：" + ex.Message + "---" + ex.StackTrace);
            }
            Log.Debug("ql:" + ql + "----结果：" + err + "---卡号:" + kh + "---cs:" + cs + "---补卡次数：" + bkcs);

            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = -1;
            Int32 Err = 0;
            try
            {
                int bnql = 0;
                int boughtgas = 0;
                int css = 0;
                int csss = (int)cs;
                byte[] metertype = new byte[5];
                int klx = 0;
                int alarmgas = 0;
                Int64 inputvalue = 99999;
                int overvalue = 0;
                int Cardcount = 0;
                byte[] dqdmm = new byte[5];
                byte[] regist = new byte[10];
                double price = 1.5;
                int err = 0;
                int cardspare = 0;
                short cardcs; //补卡次数
                unsafe
                {
                    byte* bkcss;
                    byte* cardNo;
                    Log.Debug("读卡开始：");
                    ret = readCard(com, baud, out cardNo, ref cardspare, ref bnql, ref boughtgas, ref css, metertype,
                        ref klx, ref alarmgas, ref inputvalue, ref overvalue,
                       out bkcss, dqdmm, regist, ref price, ref err);
                    Log.Debug("读卡结束:" + ret);
                    // Cardcount = (*bkcss - 48);
                    // Cardcount += (*(bkcss + sizeof(byte)) - 49);
                    Cardcount = (*(bkcss + sizeof(byte)) - 48);
                    Log.Debug("补卡次数：" + Cardcount);
                    cardcs = (short)Cardcount;
                }
                if (ql > 0)
                {
                    Log.Debug("购气开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + csss + "---补卡次数：" + cardcs);
                    ret = WriteCard(com, baud, kh, ql, cardcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref Err, 258);
                    Log.Debug("购气结果：" + ret + "---错误代码：" + Err);
                }
                else
                {
                    Log.Debug("退气开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + csss + "---补卡次数：" + cardcs);
                    ret = WriteCard(com, baud, kh, 0, cardcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref Err, 1282);
                    Log.Debug("退气结果：" + ret);
                }
            }
            catch (Exception e)
            {
                Log.Debug("写卡异常：" + e.Message + "---" + e.StackTrace);
            }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            int ret = -1;
            try
            {
                Int32 Err = 0;
                int csss = (int)cs;
                int clearresult = ClearCard(com, baud, ref Err);
                Log.Debug("清卡结果:" + clearresult);
                //先初始化
                ret = InitCard(com, baud, ref Err);
                Log.Debug("初始化卡结束:" + ret);
                //写新卡
                if (0 == kzt)
                {
                    
                    int flag = 1;
                    if(0 == ql)
                    {
                        flag = 0;
                    }
                    Log.Debug("发卡开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + flag + "---补卡次数：" + 0);
                    ret = WriteCard(com, baud, kh, ql, 0, 1.6, "00000000", 10, 99999, 0, flag, "02", 0, "0000", ref Err, 511);
                    Log.Debug("发卡结束：" + ret);
                }
                else
                {
                    Log.Debug("补卡开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + cs + "---补卡次数：" + bkcs);
                    ret = WriteCard(com, baud, kh, ql, bkcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref Err, 1023);
                    Log.Debug("补卡结束：" + ret);
                }
            }
            catch (Exception e)
            {
                Log.Debug("写新卡异常：" + e.Message + "---" + e.StackTrace);
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
                return "CangNan";
            }
        }

        #endregion
    }
}
