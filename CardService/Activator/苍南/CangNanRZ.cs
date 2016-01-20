using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class CangNanRZ : ICard
    {
        private static Log Log = Log.GetInstance("Card.CangNanRZ");

        public string Test()
        {
            return "CangNanRZ";
        }

        #region 苍南动态库导入
        //test
        [DllImport("CNDLL.dll", EntryPoint = "ReadCard",
         CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCard1(int port, int baud, ref StringBuilder CardId, ref long CardMoney, ref long UsedMoney,
                                          ref long BoughtMoney, ref int CardGasCount, ref StringBuilder MeterType, ref int CardType,
                                          ref long AlarmValue, ref long InputValue, ref long OverValue, ref string
 CardCount,
                                          ref StringBuilder AreaCode, ref StringBuilder Regist, ref double Price,
                                          ref int err);
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
            
            StringBuilder AreaCode = new StringBuilder(4 - 1);
            StringBuilder CardId = new StringBuilder(16 - 1);
            long CardMoney = 0;
            int CardGasCount = 0;
            long UsedMoney = 0;
            long BoughtMoney = 0;
            StringBuilder MeterType = new StringBuilder(2 - 1);
         //   StringBuilder CardCount = new StringBuilder(2 - 1);
          string CardCount ="";
            StringBuilder Regist = new StringBuilder(8 - 1);
            int CardType = 0;
            long AlarmValue = 100;
            long InputValue = 999999;
            long OverValue = 0;
            double Price = 0;
            int err = 0;
            Log.Debug("检查卡开始：");
            int i = ReadCard1(com, baud, ref CardId, ref CardMoney, ref UsedMoney,
                                 ref BoughtMoney, ref CardGasCount, ref MeterType, ref CardType,
                                 ref AlarmValue, ref InputValue, ref OverValue, ref CardCount,
                                 ref AreaCode, ref Regist, ref Price, ref err);
            Log.Debug("检查卡结束： " + i);
            return i;
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
          
            StringBuilder AreaCode = new StringBuilder(4 - 1);
            StringBuilder CardId = new StringBuilder(16 - 1);
            long CardMoney = 0;
            int CardGasCount = 0;
            long UsedMoney = 0;
            long BoughtMoney = 0;
            StringBuilder MeterType = new StringBuilder(2 - 1);
          //  StringBuilder CardCount = new StringBuilder(2 - 1);
            StringBuilder Regist = new StringBuilder(8 - 1);
            int CardType = 0;
            long AlarmValue = 100;
            long InputValue = 999999;
            long OverValue = 0;
            double Price = 0;
            int err = 0;
            string CardCount = "";
            Log.Debug("读卡开始：");
            int i = ReadCard1(com, baud, ref CardId, ref CardMoney, ref UsedMoney,

                                 ref BoughtMoney, ref CardGasCount, ref MeterType, ref CardType,
                                 ref AlarmValue, ref InputValue, ref OverValue, ref CardCount,
                                 ref AreaCode, ref Regist, ref Price, ref err);
            Log.Debug("读卡结束： 补卡次数+" + CardCount);
            kh = CardId.ToString();
            ql = int.Parse(CardMoney.ToString());
            bkcs = (short)(int.Parse(CardCount.ToString()));
            cs = (short)CardGasCount;
            Log.Debug("ql:" + ql + "----结果：" + err + "---卡号:" + kh + "---cs:" + cs + "---补卡次数：" + bkcs);

            return i;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = -1;
       
          
            StringBuilder AreaCode = new StringBuilder(4 - 1);
            StringBuilder CardId = new StringBuilder(16 - 1);
            long CardMoney = 0;
            int CardGasCount = 0;
            long UsedMoney = 0;
            long BoughtMoney = 0;
            StringBuilder MeterType = new StringBuilder(2 - 1);
       //     StringBuilder CardCount = new StringBuilder(2 - 1);
            string CardCount = "";
            StringBuilder Regist = new StringBuilder(8 - 1);
            int CardType = 0;
            long AlarmValue = 100;
            long InputValue = 999999;
            long OverValue = 0;
            double Price = 0;
            int err = 0;
            
            short cardcs=0;
            Log.Debug("读卡开始：");
             ret = ReadCard1(com, baud, ref CardId, ref CardMoney, ref UsedMoney,
                                 ref BoughtMoney, ref CardGasCount, ref MeterType, ref CardType,
                                 ref AlarmValue, ref InputValue, ref OverValue, ref CardCount,
                                 ref AreaCode, ref Regist, ref Price, ref err);
             Log.Debug("读卡结束:" + ret);
                    // Cardcount = (*bkcss - 48);
                    // Cardcount += (*(bkcss + sizeof(byte)) - 49);x
             if (int.Parse(CardCount.ToString()) > 10)
             {
                 //动态库补卡写入1的时候第一次读出的是11
                 cardcs = (short)(int.Parse(CardCount.ToString())-10);

             }
             else { 
             cardcs = (short)(int.Parse(CardCount.ToString()));
             }
                    //Cardcount =int.Parse(CardCount.ToString());
                    //Log.Debug("补卡次数：" + Cardcount);
                    //cardcs = (short)Cardcount;
               short csss=(short)cs;
               Log.Debug(" 购气前读卡的补卡次数+" + cardcs);
               if (ql > 0)
               {
                   Log.Debug("购气开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + cs + "---补卡次数：" + cardcs);
                   ret = WriteCard(com, baud, kh, ql, cardcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref err, 258);
                   Log.Debug("购气结果：" + ret + "---错误代码：" + err);
               }
               else
               {
                   Log.Debug("退气开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + cs + "---补卡次数：" + cardcs);
                   ret = WriteCard(com, baud, kh, 0, cardcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref err, 1282);
                   Log.Debug("退气结果：" + ret);
               }
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
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
                    Log.Debug("发卡开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + csss + "---补卡次数：" + 0);
                    ret = WriteCard(com, baud, kh, ql, 0, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref Err, 511);
                    Log.Debug("发卡结束：" + ret);
                }
                else
                {
                    Log.Debug("补卡开始：" + "卡号：" + kh + "--气量：" + ql + "--购气次数：" + cs + "---补卡次数：" + bkcs);
                    ret = WriteCard(com, baud, kh, 0, bkcs, 1.6, "00000000", 10, 99999, 0, csss, "02", 0, "0000", ref Err, 1023);
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
