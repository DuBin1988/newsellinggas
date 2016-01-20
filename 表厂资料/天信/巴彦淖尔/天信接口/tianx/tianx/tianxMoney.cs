using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Threading;
using System.Data;
using System.Windows.Forms;
using System.Reflection;

namespace tianx
{
    class tianxMoney
    {
      /*  interface ICInterfaceMoney : IDisposable
        {
             int CheckOwnCardSelf(int COMID);
            int ReadICCardSelf(int COMID, out string ICId, out  int ICType, out double ICCSpare, out int GASCOUNT, out int CusType, out double ICUsed
                , out double ICMSpare, out int ICNum, out  string ICMark, out string ICMUType, out bool RWMark, out string showError);

            int WriteICCardSelf(string custcode, int COMID, string ICId, int OPCODE, int GASCOUNT, double ICCSpare, int ICType, string ICMark, double CurrentPrice, double money);
            int WriteICCardNewSelf(string custcode, int COMID, string ICId, int OPCODE, int GASCOUNT, double ICCSpare, int ICType, string ICMark, double CurrentPrice, double money);
            int ClearAllCardSelf(int COMID);

            int MakeIniCardSelf(int COMID, double frontGas, double AlarmValue, double InputValue, double ControlValue);
        }*/



        public class CardTX  /// 
        {
            //private static bool toupdata = CardNewFactory.DownDrivefile(13, false);

          //  private const string TXDir = "";//"c:\\morevertTX\\";C:\\WINDOWS\\system32\\
 /*           public static string[] errorStr = {"0操作成功","1卡被更换（卡片核对不符）","2没有卡","3读写卡器配置不对","4读写卡器不工作","5dll内部故障","6卡类型错误（新卡为卡型号错误，老卡为卡型号错误及内容算法错误、密码错误等）"
            ,"7未知","8未知","9未知","10 新卡"};
            public static string returntostring(int errorcode)
            {
                if (errorcode == 98)
                    return "98串口初始化失败";
                else if (errorcode > -1 && errorcode < errorStr.Length)
                    return errorStr[errorcode];

                else return "其他未知";
            }

*/
            [DllImport("TancyCard_je.dll", EntryPoint = "User_ReadICCard", SetLastError = true, CharSet = CharSet.Auto, ExactSpelling = false, CallingConvention = CallingConvention.StdCall)]
            //            private static extern int ReadICCard(byte[] ICId, out double ICCSpare, out double ICUsed, out double ICMSpare
            //                , out int ICNUM, out int ICMark, StringBuilder ICRegist, out double ICMoney, StringBuilder ICMUType
            //                , out int ICType, out int ICErroy, int COMID, int COMHZ, out int GasCount, out int ICDECNUM, out  int cusType);
            private static extern int User_ReadICCard(byte[] ICId, byte[] ICMark, out int ICCSpare, out int ICSum, out int GASCOUNT, out int ICPrice, out double ICConsume, out double ICUsed
                , out int ICNUM, StringBuilder ICRegist, StringBuilder ICMUType, out int ICType, int COMID, int COMHZ, out int ICErroy);

            //            public static int ReadICCard(int COMID, out string ICId, out int GasCount, out double ICCSpare, out double ICUsed, out double ICMSpare, out int ICType)
            public static int User_ReadICCard(int COMID, out string ICId, out string ICMark, out int GasCount, out int ICCSpare, out double ICUsed, out int ICSum, out double ICConsume, out int ICPrice, out int ICType)
            {
                ///卡内余量   表内用气量	  表内余量	  余下的包装好,请使用
                int ICNUM;
                //int ICMark;
                StringBuilder ICRegist = new StringBuilder(100);
                //double ICMoney;
                StringBuilder ICMUType = new StringBuilder(100);
                byte[] a = ASCIIEncoding.ASCII.GetBytes("                ");
                byte[] b = ASCIIEncoding.ASCII.GetBytes("            ");
                int ICErroy;
                //int ICDECNUM;
                //int cusType;

                //                int pout = ReadICCard(a, out ICCSpare, out ICUsed, out ICMSpare, out ICNUM, out ICMark, ICRegist, out ICMoney
                //                 , ICMUType, out ICType, out ICErroy, COMID, 9600, out GasCount, out ICDECNUM, out cusType);
                int pout = User_ReadICCard(a, b, out ICCSpare, out ICSum, out GasCount, out ICPrice, out ICConsume, out ICUsed, out ICNUM
                    , ICRegist, ICMUType, out ICType, COMID, 9600, out ICErroy);



                ICId = ASCIIEncoding.ASCII.GetString(a);
                ICMark = ASCIIEncoding.ASCII.GetString(b);
                //ICMSpare = Math.Round(ICMSpare, 2);
                return pout;
            }



            [DllImport("TancyCard_je.dll", EntryPoint = "regulator_ReadICCard", SetLastError = true, CharSet = CharSet.Auto, ExactSpelling = false, CallingConvention = CallingConvention.StdCall)]
            private static extern int regulator_ReadICCard(out int Price, byte[] Price_T, byte[] Effective_T, int COMID, int COMHZ, out int ICErroy);


            public static int regulator_ReadICCard(int COMID, out int Price, out string Price_T, out string Effective_T)
            {
                ///卡内余量   表内用气量	  表内余量	  余下的包装好,请使用
                //int ICNUM;
                //int ICMark;
                StringBuilder ICRegist = new StringBuilder(100);
                //double ICMoney;
                StringBuilder ICMUType = new StringBuilder(100);
                byte[] a = ASCIIEncoding.ASCII.GetBytes("            ");
                byte[] b = ASCIIEncoding.ASCII.GetBytes("            ");
                int ICErroy;
                int pout = regulator_ReadICCard(out Price, a, b, COMID, 9600, out ICErroy);

                Price_T = ASCIIEncoding.ASCII.GetString(a);
                Effective_T = ASCIIEncoding.ASCII.GetString(b);
                return pout;
            }

            [DllImport("TancyCard_je.dll", EntryPoint = "regulator_WriteICCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
            private static extern int regulator_WriteICCard(int Price, StringBuilder Price_T, StringBuilder Effective_T, int COMID, int COMHZ, out int ICErroy);

            public static int regulator_WriteICCard(int Price, StringBuilder Price_T, StringBuilder Effective_T,  int COMID)
            {
                int ICErroy;

                return regulator_WriteICCard(Price, Price_T, Effective_T,  COMID, 9600, out  ICErroy);
            }

 
            [DllImport("TancyCard_je.dll", EntryPoint = "User_WriteICCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
//            private static extern int WriteICCard(StringBuilder ICId, double ICCSpare, int ICNUM, int ICMark
//                , StringBuilder ICRegist, int ICType, StringBuilder ICMUType, int ICDECNum
//                , out int ICErroy, int COMID, int COMHZ, int OPCode, int GasCount, int cusType);
            private static extern int User_WriteICCard(StringBuilder ICId, StringBuilder ICMark, int ICCSpare, int GasCount, int ICPrice,StringBuilder ICRegist,StringBuilder ICMUType
                ,int ICType, int ICNUM,int OPCode, int COMID, int COMHZ,out int ICErroy);
                


//            public static int WriteICCard(StringBuilder ICId, double ICCSpare, int GasCount, int ICType, int COMID, int OPCode)
            public static int User_WriteICCard(StringBuilder ICId, StringBuilder ICMark, int ICCSpare, int GASCOUNT, int ICPrice, int ICType, int COMID, int OPCODE)
            {
                int ICErroy;

//                return WriteICCard(ICId, ICCSpare, 1, 0, new StringBuilder("12345678")
//                   , ICType, new StringBuilder(100), 0
//                   , out  ICErroy, COMID, 9600, OPCode, GasCount, 1);
                return User_WriteICCard(ICId, ICMark, ICCSpare, GASCOUNT, ICPrice, new StringBuilder("12345678"), new StringBuilder(50), ICType, 1, OPCODE
                  , COMID, 9600, out  ICErroy);
            }



            public static int WriteOldICCard(StringBuilder ICId, StringBuilder ICMark, int ICCSpare, int GasCount, int ICPrice,  int COMID)
            {
                int OPCode = 8 | 1 | 2 | 256 | 128 | 32;

                return CardTX.User_WriteICCard(ICId, ICMark, ICCSpare, GasCount, ICPrice, 0, COMID, OPCode);
            }

            public static int WriteNewICCard(StringBuilder ICId, StringBuilder ICMark, int ICCSpare, int GasCount, int ICPrice, int COMID)///开户发卡时.
            {
                int OPCode = 8 | 1 | 2 | 256 | 128 | 32;

                return CardTX.User_WriteICCard(ICId, ICMark, ICCSpare, GasCount, ICPrice, 1, COMID, OPCode);
            }



            public static int BuyGas(int BuyGas, int GASCOUNT, int COMID)///购气时.
            {
                StringBuilder ICId = new StringBuilder(16);
                StringBuilder ICMark = new StringBuilder(12);
                int OPCode = 2 | 256;

                return CardTX.User_WriteICCard(ICId, ICMark, BuyGas, GASCOUNT, 0, 0,COMID, OPCode);
            }

            public static int User_ClearICCard(int COMID)//清卡
            {
              string ICId;
              string ICMark;
              StringBuilder ICId1 = new StringBuilder(16);
              StringBuilder ICMark1 = new StringBuilder(12);
              //int COMID;
              int GasCount;
              int ICCSpare;
              double ICUsed;
              int ICSum;
              double ICConsume;
              int ICPrice;
              int ICType;
              int result1;
              result1 = tianxMoney.CardTX.User_ReadICCard(COMID, out  ICId, out  ICMark, out GasCount, out ICCSpare, out ICUsed, out ICSum, out ICConsume, out ICPrice, out ICType);
              if ((result1 == 0) || (result1 == 10))
              {
                  foreach (char c in "000000000000") { ICMark1.Append(c); }
                  foreach (char c in "0000000000000000") { ICId1.Append(c); }
                  result1 = WriteOldICCard(ICId1, ICMark1, -1 * ICCSpare, 0, 0, COMID);
                 return result1;   

              }
              else return result1;
            }

        }

    }
}
