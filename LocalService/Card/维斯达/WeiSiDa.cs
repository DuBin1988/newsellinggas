using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class WeiSiDa : ICard
    {
        private static Log Log = Log.GetInstance("Card.WeiSiDa");

        public string Test()
        {
            return "ruisen";
        }

        #region 动态库导入
        //初始化设备函数
        [DllImport("CardMan2012.dll", EntryPoint = "Init", CallingConvention = CallingConvention.StdCall)]
        public static extern int Init(ref int icdev, int com, int baut);

        //释放设备函数
        [DllImport("CardMan2012.dll", EntryPoint = "Finish", CallingConvention = CallingConvention.StdCall)]
        public static extern int Finish(ref int icdev);

        //卡型识别函数
        [DllImport("CardMan2012.dll", EntryPoint = "TestCardKind", CallingConvention = CallingConvention.StdCall)]
        public static extern int TestCardKind(int icdev, int cityID);

        //开户或制开户卡函数
        [DllImport("CardMan2012.dll", EntryPoint = "MakeOpenUserCardNew", CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeOpenUserCardNew(int icdev, ref long ID, ref double BuyedGasSum, ref int BuyedGasCount,
            ref int CloseTapeCircle, ref bool OverCurrentProtectStartFlag, int cityID);

        //制购气卡函数
        [DllImport("CardMan2012.dll", EntryPoint = "MakeBuyGasCardNew", CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeBuyGasCardNew(int icdev, ref long ID, ref double BuyedGasSum, ref int BuyedGasCount,
            ref int CloseTapeCircle, ref bool OverCurrentProtectStartFlag, int cityID);

        //制工具卡函数，可以制新卡
        [DllImport("CardMan2012.dll", EntryPoint = "MakeToolCardNew", CallingConvention = CallingConvention.StdCall)]
        public static extern int MakeToolCardNew(int icdev, int Kind, ref double BuyedGasSum, int cityID);
        
        //购气函数
        [DllImport("CardMan2012.dll", EntryPoint = "BuyGasNew", CallingConvention = CallingConvention.StdCall)]
        public static extern int BuyGasNew(int icdev, ref long ID, ref double BuyedGasSum, ref int BuyedGasCount,
            ref int CloseTapeCircle, ref bool OverCurrentProtectStartFlag, int cityID);

        //读普通卡函数: 开户卡或购气卡
        [DllImport("CardMan2012.dll", EntryPoint = "ReadCardNew", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadCardNew(int icdev, int Kind, ref long ID, ref double BuyedGasSum, ref int BuyedGasCount,
            ref int CloseTapeCircle, ref bool OverCurrentProtectStartFlag, ref bool ReturnReadFlag, int cityID);

        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {            
            int icdev = 0;
            int ret = Init(ref icdev, com, baud);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            ret = TestCardKind(icdev, 0);
            //1：开户卡 2：购气卡 3：购气返回卡
            if (ret == 1 || ret == 2 || ret == 3)
            {
                ret = 0;
            }
            else
            {
                ret = -1;
            }
            Finish(ref icdev);
            return ret;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int icdev = 0;
            int ret = Init(ref icdev, com, baud);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            double gas = 0;
            ret = MakeToolCardNew(icdev, 19, ref gas, 0);
            if (ret != -1)
            {
                ret = 0;
            }
            Finish(ref icdev);
            return ret;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
            int icdev = 0;
            int ret = Init(ref icdev, com, baud);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            long ID = 0;
            double BuyedGasSum = 0;
            int BuyedGasCount = 0;
            int CloseTapeCircle = 0;
            bool OverCurrentProtectStartFlag = false;
            bool ReturnReadFlag = false;
            //调用测卡函数，将测卡函数的卡类型返回值，传递给读卡函数
            int kind = TestCardKind(icdev, 0);
            ret = ReadCardNew(icdev, kind, ref ID, ref BuyedGasSum, ref BuyedGasCount,
                ref CloseTapeCircle, ref OverCurrentProtectStartFlag, ref ReturnReadFlag, 0);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            //卡号
            kh = ID.ToString("D8");
            //气量，是回读，气量为零
            if (ReturnReadFlag)
            {
                ql = 0;
            }
            else
            {
                ql = (int)BuyedGasSum;
            }
            cs = (short)BuyedGasCount;
            Finish(ref icdev);
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int icdev = 0;
            int ret = Init(ref icdev, com, baud);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            long id = long.Parse(kh); //卡号ID
            double gas = ql;    //购气量,只保留两位小数
            int count = cs;      //购气次数
            int close = 0;      //关阀周期,0,不关阀
            bool over = false;       //过流保护启用标志，0，无过流保护功能
            //0代表退气，直接买0方气
            ret = BuyGasNew(icdev, ref id, ref gas, ref count, ref close, ref over, 0);
            Finish(ref icdev);
            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int icdev = 0;
            int ret = Init(ref icdev, com, baud);
            if (ret == -1)
            {
                Finish(ref icdev);
                return ret;
            }
            long id = long.Parse(kh); //卡号ID
            double gas = ql;    //购气量,只保留两位小数
            int count = cs;      //购气次数
            int close = 0;      //关阀周期,0,不关阀
            bool over = false;       //过流保护启用标志，0，无过流保护功能
            //制卡前先清卡
            MakeToolCardNew(icdev, 19, ref gas, 0);
            //kzt=0，写新卡，kzt=1，补卡
            if (kzt == 0)
            {
                ret = MakeOpenUserCardNew(icdev, ref id, ref gas, ref count, ref close, ref over, 0);
            }
            else
            {
                ret = MakeBuyGasCardNew(icdev, ref id, ref gas, ref count, ref close, ref over, 0);
            }
            Finish(ref icdev);
            return ret;
        }

        public string Name
        {
            get
            {
                return "dandongminyong";
            }
        }

        #endregion
    }
}
