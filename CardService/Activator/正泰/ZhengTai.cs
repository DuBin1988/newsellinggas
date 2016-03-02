
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class ZhengTai : ICard
    {
        private static Log Log = Log.GetInstance("Card.ZhengTai");

        public string Test()
        {
            return "正泰";
        }
        #region
        //检查卡函数
        [DllImport("ChintICCard.dll", EntryPoint = "ISChint", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ISChint(int com, int baud);

        //读卡函数
        [DllImport("ChintICCard.dll", EntryPoint = "ReadCardInfo", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCardInfo(int com, int baud, ref int i_CardType, ref  double d_OvercurrentValue, ref double d_WarnValue, ref  double d_LimitValue, ref double d_OverdraftValue, ref  double d_BuyValue, ref  double d_LeaveValue, ref double d_TotalUsedValue, ref   bool b_ValvesOpenedFlag, ref  bool b_ValvesClosedFlag, ref   bool b_OvercurrentFlag, ref  bool b_CloseValvesErrFlag, ref   bool b_ReedBadFlag, ref bool b_DemolitionFlag, ref   bool b_MagneticFlag, ref    int i_FillCardCount, ref  int i_BuyGasCount,StringBuilder kh);
        //,ref  double  d_OvercurrentValue,ref double   d_WarnValue,ref  double  d_LimitValue,ref double  d_OverdraftValue, ref  double  d_BuyValue,ref  double  d_LeaveValue,  ref double d_TotalUsedValue, ref   bool  b_ValvesOpenedFlag,ref  bool  b_ValvesClosedFlag,   ref   bool b_OvercurrentFlag, ref  bool  b_CloseValvesErrFlag,ref   bool b_ReedBadFlag,   ref bool  b_DemolitionFlag, ref   bool  b_MagneticFlag,ref    int  i_FillCardCount,ref  int  i_BuyGasCount ,ref string kh
        //开户函数
        [DllImport("ChintICCard.dll", EntryPoint = "MakeUserCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeUserCard(int com, int baud, double d_buy,             //购气量
                           double d_OvercurrentValue,//过流保护值
                           double d_WarnValue,       //报警气量
                           double d_LimitValue,      //门限气量
                           double d_OverdraftValue,  //透支气量
                         byte[]_AccountDate,     //开户日期，年-月-日-时，比如15071514
                            StringBuilder s_CardNo,          //卡号
                           int i_count,              //购气次数
                           int i_FillCardCount,      //补卡次数
                           byte[] s_SecPass
);
        //清卡？
        [DllImport("ChintICCard.dll", EntryPoint = "ClearICCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int ClearICCard(int com, int baud);
        //购气函数
        [DllImport("ChintICCard.dll", EntryPoint = "BuyGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int BuyGas(int port, int baud,
                     int i_BuyGasCount,		    //购气次数
                     double d_BuyValue,         //购气量
                     double d_OvercurrentValue, //过流保护值
                     double d_WarnValue,        //报警气量
                     double d_LimitValue,       //门限气量
                     double d_OverdraftValue,   //透支气量
                    StringBuilder kh);
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
           
           
             Log.Debug("ZhengTai Format Card start ");
             int i = ClearICCard(com, baud);
             Log.Debug("ZhengTai Format Card end and i is " + i);
      
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
              Log.Debug("YinHe CheckGasCard Start ");
              int i = ISChint(com, baud);
                Log.Debug("YinHe CheckGasCard end and i is  "+i);

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

            int i_CardType = 0;        //卡型
            double d_OvercurrentValue = 0; //过流保护值
            double d_WarnValue = 0;       //报警气量
            double d_LimitValue = 0;    //门限气量
            double d_OverdraftValue = 0; //透支气量
            double d_BuyValue = 0;    //购气量，即卡内存量
            double d_LeaveValue = 0;     //表内剩余气量
            double d_TotalUsedValue = 0;  //总用气量
            bool b_ValvesOpenedFlag = false; //阀开标志
            bool b_ValvesClosedFlag = false; //阀关标志
            bool b_OvercurrentFlag = false;   //过流标志
            bool b_CloseValvesErrFlag = false;//关阀异常标志
            bool b_ReedBadFlag = false;    //簧管坏标志
            bool b_DemolitionFlag = false;  //拆标志
            bool b_MagneticFlage = false;   //磁标志
            int i_FillCardCount = 0;   //补卡次数
            int i_BuyGasCount = 0;
            StringBuilder kh1 = new StringBuilder();
            byte[] s_CardNo = new byte[8];            //卡号 8位
            Log.Debug("Zhengtai Read Card Start");
            int i = ReadCardInfo(com, baud, ref i_CardType, ref d_OvercurrentValue, ref d_WarnValue, ref d_LimitValue, ref d_OverdraftValue, ref d_BuyValue, ref d_LeaveValue, ref d_TotalUsedValue, ref b_ValvesOpenedFlag, ref b_ValvesClosedFlag, ref b_OvercurrentFlag, ref b_CloseValvesErrFlag, ref b_ReedBadFlag, ref b_DemolitionFlag, ref b_MagneticFlage, ref i_FillCardCount, ref i_BuyGasCount, kh1);
            Log.Debug("Zhengtai Read Card End  and Ret is "+i);
            kh = kh1.ToString();
           double ql1 =double.Parse(d_BuyValue.ToString());
           ql = (int)ql1;
            Log.Debug("ZhengTai read card to cs is " + i_BuyGasCount);
            cs =(short) i_BuyGasCount;
            
         
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
            int i_BuyGasCount = cs;		    //购气次数
            double d_BuyValue = 0;         //购气量
            double d_OvercurrentValue = 1000.0;//过流保护值
            double d_WarnValue = 10.0;       //报警气量
            double d_LimitValue = 9999.0;      //门限气量
            double d_OverdraftValue = 0.0;
            StringBuilder kh1 = new StringBuilder(kh);
            int i = 0;
            Log.Debug("ZhengTai WriteGas Started and ql is "+ql+"cs is "+cs+"kh is "+kh);
            //气量大于0代表购气
            if (ql > 0) {

              
                
       
               
                d_BuyValue = ql;
                Log.Debug("ZhengTai BuyGas Start");
                
                 i = BuyGas(com, baud, i_BuyGasCount, d_BuyValue, d_OvercurrentValue, d_WarnValue, d_LimitValue, d_OverdraftValue, kh1);
                Log.Debug("ZhengTai BuyGas end and i is "+i);
            }
            else
            {
                Log.Debug("ZhengTai ReturnGas Started");
                d_BuyValue = 0;
               //气量<=0代表冲正 读卡 读出卡上原有气量  前提条件为卡内有气不能购气
                i = BuyGas(com, baud, i_BuyGasCount, d_BuyValue, d_OvercurrentValue, d_WarnValue, d_LimitValue, d_OverdraftValue, kh1);
                Log.Debug("ZhengTai ReturnGas end and i is " + i);

            }
            Log.Debug("ZhengTai WriteGas End and i is " + i);
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

            double d_buy = ql;             //购气量
            double d_OvercurrentValue = 1000;//过流保护值
            double d_WarnValue = 10;       //报警气量
            double d_LimitValue = 9999;      //门限气量
            double d_OverdraftValue = 0;  //透支气量
            string s_AccountDate = "15071514";     //开户日期，年-月-日-时，比如15071514
            byte[] s_AccountDate1 = new byte[8];
            s_AccountDate1 = System.Text.Encoding.GetEncoding(1252).GetBytes(s_AccountDate);
            StringBuilder s_CardNo = new StringBuilder(kh);
            //卡号
            int i_count = cs;              //购气次数
            int i_FillCardCount = 0;      //补卡次数
            //新系统开户 或锡系统中卡户的卡不卡  旧系统中的卡补卡需要  读取数据库中存放的卡密码。
            string s_SecPass="";
            //补卡操作
            Log.Debug("ZhengTai Write New Card  and kmm is"+kmm+" and kzt is "+kzt);
            //1  区分发新卡还是补卡操作  如果为补卡操作则  需要区分是否旧系统用户卡
            //判断为补卡
            if (kzt == 1 ) { 
                //区分新旧系统用户
                if (int.Parse(kmm) != 0 && !kmm.Equals("") && kmm != null)
                {
                    //确认为旧系统用户补卡
                    Log.Debug("ZhengTai old Xitong  Buka  Password");
                    int AA = int.Parse(kmm);
                    string bb = AA.ToString("X2");
                    string cc = bb.PadLeft(6, '0');
                    string new1 = cc.Substring(4, 2);
                    string new2 = cc.Substring(0, 2);
                    string new3 = cc.Substring(2, 2);
                    s_SecPass = new1 + new3 + new2;
                    Log.Debug("ZhengTai Old Bu Ka kmm is " + s_SecPass);

                }
                else {
                    Log.Debug("ZhengTai New Xitong  Buka  Password");
                    s_SecPass = "FFFFFF";
                }
                //气量为0  密码根据传入计算
            

            }else{
                Log.Debug("Zhengtai Write New Card");
                s_SecPass = "FFFFFF";
            }
                 //密钥
            byte[] s_secpass1 = new byte[6];
            s_secpass1 = System.Text.Encoding.GetEncoding(1252).GetBytes(s_SecPass);
            Log.Debug("zhengtai writenewcard start and khis "+kh+" and cs is "+cs);
            int i = MakeUserCard(com, baud, d_buy, d_OvercurrentValue, d_WarnValue, d_LimitValue, d_OverdraftValue, s_AccountDate1, s_CardNo, i_count, i_FillCardCount, s_secpass1);
            Log.Debug("zhengtai writenewcard end and i is"+i);
   return i;
        }

        public string Name
        {
            
            get
            {
                return "ZhengTai";
            }
        }

        #endregion
    }
}
