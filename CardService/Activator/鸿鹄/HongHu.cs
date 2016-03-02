using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class HongHu : ICard
    {
        private static Log Log = Log.GetInstance("Card.HongHu");

        public string Test()
        {
            return "鸿鹄";
        }


        #region 动态库导入
        //引入动态库  明华
        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);
        //引入动态库  鸿鹄
        //清卡
        [DllImport("HH53IC.dll", EntryPoint = "HHClearCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHClearCard(int icdev);
        //检查卡
        [DllImport("HH53IC.dll", EntryPoint = "HHChkFac", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHChkFac(int icdev);
        //读卡
        [DllImport("HH53IC.dll", EntryPoint = "HHReadCardInfo", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHReadCardInfo(int icdev, StringBuilder CardNo, ref int GasV, ref int Times, ref int RemainGas);
        //制用户卡
        [DllImport("HH53IC.dll", EntryPoint = "HHInitCard", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHInitCard(int icdev, StringBuilder CardNo, int GasV, int BuyTimes, int CardT);
        //购气
        [DllImport("HH53IC.dll", EntryPoint = "HHSellGas", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHSellGas(int icdev, StringBuilder CardNo, int GasV, int BuyTimes);

        //设置校验密码方式
        [DllImport("HH53IC.dll", EntryPoint = "HHSetCardPasswordType", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHSetCardPasswordType(int pwd);
        //修改区域代码
        [DllImport("HH53IC.dll", EntryPoint = "HHEditUserCardSet", CharSet = CharSet.Ansi, CallingConvention = CallingConvention.StdCall)]
        public static extern int HHEditUserCardSet(int icdev,string areacode,int alertvalue,int Overflow,int maxflow,int test);
        #endregion

        #region ICard Members

       
        //格式化卡
        public int FormatGasCard(Int16 com,          //串口号，从0开始
           Int32 baud,         //波特率
           string kmm,         //卡密码，写卡后返回新密码
           string kh,          //卡号
           string dqdm)
        {
            //打开串口
            int icdev  = ic_init(com, baud);
            //设置密码校验方式
            HHSetCardPasswordType(0);
            Log.Debug("打开串口号为:" + icdev);
            int i = HHClearCard(icdev);
            //关闭串口
            Log.Debug("清卡结果为：" + i);
            ic_exit(icdev);
            if (i == 3) {
                i = 0;
            }
            return i;
        }
     
       public  int CheckGasCard(

             Int16 com,          //串口号，从0开始
             Int32 baud          //波特率
             ){
                 //打开串口
                 int icdev = ic_init(com, baud);
                 Log.Debug("检查卡开始：");
                 string kh = "00000000";
                 int i = HHChkFac(icdev);
                 if (i == 6) {
                     try
                     {
                         //检查为6之后去读卡号
                         StringBuilder cardNo = new StringBuilder();
                         int times = 0;
                         int gas = 0;
                         int gasremain = 0;
                         Log.Debug("检查卡--读卡开始：");
                         i = HHReadCardInfo(icdev, cardNo, ref gas, ref times, ref gasremain);
                         Log.Debug("检查卡--读卡结束：" + i);

                         kh = cardNo.ToString().Substring(0, 8);

                     }
                     catch (Exception ex){ 
                        
                     }
                 }
                 Log.Debug("截取卡号为" + kh.Substring(0, 4));
                 if (!kh.Substring(0, 4).Equals("0000") && !kh.Substring(0, 4).Equals("8000") && !kh.Substring(0, 4).Equals("0200"))
                 {
                     i = 0;
                 }
                 Log.Debug("检查卡结束：" + i);
                 ic_exit(icdev);
               
                 return i;
       }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
       {
           //打开串口
           int icdev = ic_init(com, baud);
           StringBuilder cardNo = new StringBuilder();
           int times = 0;
           int gas = 0;
           int gasremain = 0;
           Log.Debug("读卡开始：");
           int i = HHReadCardInfo(icdev, cardNo, ref gas, ref times, ref gasremain);
           Log.Debug("读卡结束："+i);
           ic_exit(icdev);
            
         
           if (i == 4 | i==1 |i==3 |i==2) {
               kh = cardNo.ToString().Substring(0, 8);
               ql = gas/100;
               cs = (short)times;
               i = 0;
           }
            return i;
            
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {

            //打开串口
            int icdev = ic_init(com, baud);
            StringBuilder cardId = new StringBuilder(kh);
            int gas = ql*100;
            int buytimes = cs;
            int i = 0;
            HHSetCardPasswordType(0);
            Log.Debug("购气参数为：卡号+" + kh + " 购气次数+" + cs + " 气量+" + ql);
            //卡内有气继续购气修改
            //首先判断购气或者退气
            //该种卡气量写入为覆盖，卡内有气购气跟传入负数退气  都需要读出气量 两个值相加  再写入卡中 因此不用判断传入气量的正负
            if (ql > 0)
            {
                Log.Debug("购气开始：");
                 i = HHSellGas(icdev, cardId, gas, buytimes);
                 Log.Debug("购气结束：" + i);
            }
            else
            {
                gas = 0;
                Log.Debug("退气开始：");
                 i = HHSellGas(icdev, cardId, gas, buytimes);
                 Log.Debug("退气结束：" + i);
            }
            //修改卡上区域代码
            Log.Debug("修改区域代码开始：");
            String AreaCode = "0a0a0a";
            i = HHEditUserCardSet(icdev, AreaCode, 5, 0, 99, 99);
            Log.Debug("修改区域代码结束：" + i);    
            ic_exit(icdev);
            if (i == 3) {
                i = 0;
            }
            return i;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj, int klx, string meterid)
        {
            //打开串口
            int icdev = ic_init(com, baud);
            HHSetCardPasswordType(0);
            Log.Debug("清卡开始：");
          int   i = HHClearCard(icdev);
          Log.Debug("清卡结束：" + i);
            StringBuilder cardId = new StringBuilder(kh);

            int gas = ql*100;
            int buytimes = cs;
            int cardT = 0;
            if (kzt == 1) {
                 cardT = 1;
            }

            Log.Debug("发卡或补卡开始：");
            i = HHInitCard(icdev, cardId, gas, buytimes, cardT);
             Log.Debug("发卡或补卡结束：" + i);
            //修改卡上区域代码
             Log.Debug("修改区域代码开始：");
            String AreaCode="0a0a0a";
            i = HHEditUserCardSet(icdev, AreaCode, 5, 0, 99, 99);
            Log.Debug("修改区域代码结束："+i);          
            if (i == 3) {
                i = 0;
            }
            return i;
        }
        /// <summary>
        /// 航天卡实现，其他不用
        /// </summary>
        /// <param name="com">串口号，0代表串口1</param>
        /// <param name="baut">波特率</param>
        /// <returns>成功:0,失败：非0</returns>
        public int OpenCard(Int16 com, Int32 baud)
        {
            throw new NotImplementedException();
        }
        public string Name
        {
            get
            {
                return "HongHu";
            }
        }

        #endregion
    }
}
