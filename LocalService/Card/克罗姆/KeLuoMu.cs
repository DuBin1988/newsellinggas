using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class KeLuoMu : ICard
    {
        private static Log Log = Log.GetInstance("Card.KeLuoMu");
        
        public string Test()
        {
            Log.Debug("KeLuoMu in do work");
            return "ruisen";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            Log.Debug("Check KeLuoMu start");
            QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
            int result = obj.CheckGasCard(com, baud);
            Log.Debug("Check KeLuoMu card result=" + result);
            //1-本厂新卡,2-开户卡，3-用户卡
            if (result==1 || result == 2 || result == 3)
            {
                
                return 0;
            }
            return -1;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
            int result = obj.FormatGasCard(com, baud, kmm, -1, kh, dqdm);
            return result;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dqdm)
        {
            short klx;          //卡类型
            short kzt;          //卡状态(不支持该参数,输出-1)
            string cardNO;          //用户卡号
            string dm;        //地区代码(不支持该参数,输出null)
            string yhh;         //用户号
            string tm;          //表条码表号
            int gas;             //气量
            short times;           //购气次数
            int ljgql;          //表累计购气量
            short renewTimes;         //补卡次数(不支持该参数,输出-1)
            int ljyql;          //表内累计用气量(不支持该参数,输出-1)
            int syql;           //表内剩余气量
            int bjql;           //表内报警气量(不支持该参数,输出-1)
            int czsx;           //表内充值上限(不支持该参数,输出-1)
            int tzed;           //表内透支额度(不支持该参数,输出-1)
            string sqrq;        //售气日期 格式：20121212
            string sxrq;        //单价生效日期(不支持该参数,输出null)
            string sxbj;        //单价生效标记(不支持该参数,输出null)

            QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
            int result = obj.ReadGasCard(com, baud, null, out klx, out kzt, out cardNO, out dm, out yhh, out tm, out gas, out times, out ljgql, out renewTimes, out ljyql, out syql, out bjql, out czsx, out tzed, out sqrq, -1, -1, out sxrq, out sxbj);
            Log.Debug("read card result=" + result);
            if (result == 0)
            {
                kh = cardNO;
                ql = gas;
                money = 0;
                cs = times;
                bkcs = renewTimes;
            }
            return result;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
            Log.Debug("ql="+ql);
            if (ql > 0)
            {
                int result = obj.WriteGasCard(com, baud, kmm, -1, kh, dqdm, ql, cs, ljgql, bjql, czsx, tzed, sqrq, oldprice, newprice, sxrq, sxbj);
                
                Log.Debug("write gas card result=" + result);
                return result;
            }
            else 
            {
                int result = obj.ReturnCard(com, baud, "1", kh, "1", cs, ljgql);
                Log.Debug("return gas card result="+result);
                return result;
            }
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            //读出卡号，以便清卡
            short klx;          //卡类型
            short rkzt;          //卡状态(不支持该参数,输出-1)
            string cardNO;          //用户卡号
            string rdqdm;        //地区代码(不支持该参数,输出null)
            string ryhh;         //用户号
            string rtm;          //表条码表号
            int gas;             //气量
            short times;           //购气次数
            int rljgql;          //表累计购气量
            short renewTimes;         //补卡次数(不支持该参数,输出-1)
            int rljyql;          //表内累计用气量(不支持该参数,输出-1)
            int syql;           //表内剩余气量
            int rbjql;           //表内报警气量(不支持该参数,输出-1)
            int rczsx;           //表内充值上限(不支持该参数,输出-1)
            int rtzed;           //表内透支额度(不支持该参数,输出-1)
            string rsqrq;        //售气日期 格式：20121212
            string rsxrq;        //单价生效日期(不支持该参数,输出null)
            string rsxbj;        //单价生效标记(不支持该参数,输出null)

            QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
            int result = obj.ReadGasCard(com, baud, null, out klx, out rkzt, out cardNO, out rdqdm, out ryhh, out rtm, out gas, out times, out rljgql, out renewTimes, out rljyql, out syql, out rbjql, out rczsx, out rtzed, out rsqrq, -1, -1, out rsxrq, out rsxbj);
            Log.Debug("read card when write result=" + result);
            //是用户卡，清卡
            if (result == 0 && (klx == 2 || klx == 3))
            {
                //发卡前先格式化卡，格式化卡时，需要原卡卡号
                result = obj.FormatGasCard(com, baud, kmm, -1, cardNO, dqdm);
                Log.Debug("clear when write result=" + result);
            }
            if (kzt == 0)
            {
                //发卡
                result = obj.WriteNewCard(com, baud, kmm, -1, kzt, kh, dqdm, "1", "1", ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, sqrq, oldprice, newprice, sxrq, sxbj);
                Log.Debug("write card end ret=" + result);
                return result;
            }
            else 
            {
                Log.Debug("rewrite card start kh="+kh);
                result = obj.RepairCard(com, baud, kh, "1", "1", cs, ljgql);
                Log.Debug("rewrite card end ret="+result);
                return result;
            }
        }

        public string Name
        {
            get
            {
                return "keluomu";
            }
        }

        #endregion
    }
}
