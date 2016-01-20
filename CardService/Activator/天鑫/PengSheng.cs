using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class PengSheng : ICard
    {
        private static Log Log = Log.GetInstance("Card.PengSheng");

        public string Test()
        {
            return "ruisen";
        }

        #region 鹏胜动态库导入
        //读卡，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSTestCard", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSTestCard(int Port, int Baud, ref int PintCardCode, ref short PboolCard,
            StringBuilder PstrCustomer16Id, StringBuilder PstrCardId, ref int PlongEnableUseGas, ref int PlongReturnGas,
            ref int PlongBuyGasNumber, ref int PlongAlarmGas, ref int PlongCardNumber, StringBuilder PstrOtherCardId,
            StringBuilder PstrOperatorId, int longCompanyId);
        //开户，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSAddCustomerCard", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSAddCustomerCard(int Port, int Baud, StringBuilder PstrCustomer16Id,
            StringBuilder PstrCardId, int longBuyGas, ref int PlongMeterAlarmGas, int PlongCompanyId,
            StringBuilder PstrCardPassword, ref int PintCheckErrorNumber);
        //测卡，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "GetError", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int GetError(int Errorinfo, ref byte[] data_buff);
        //格式化卡，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSClearAllCard", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSClearAllCard(int Port, int Baud, StringBuilder PstrCardPassword, ref int PintCheckErrorNumber);
        //写购气卡，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSCustomerCardBuyGas", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSCustomerCardBuyGas(int Port, int Baud, StringBuilder PstrCustomer16Id, StringBuilder PstrCardId,
            int longBuyGas, ref int PlongReturnGas, ref int PlongCustomerCircleNumber, ref int PlongMeterAlarmGas,
            int longReplenishCardCircleNumber, int longCompanyId, StringBuilder PstrCardPassword, ref int PintCheckErrorNumber);
        //补卡，标准接口
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSReplenishCustomerCard", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSReplenishCustomerCard(int Port, int Baud, StringBuilder PstrCustomer16Id, StringBuilder PstrCardId,
                int longEnableUseGas, int longCustomerCircleNumber, ref int PlongAlarmGas, ref int PlongReplenishCardCircleNumber,
                int longCompanyId, StringBuilder PstrCardPassword, ref int PintCheckErrorNumber);
        //改密
        [DllImport("PSDLL_Self.dll", EntryPoint = "PSModifyCardPassword", CallingConvention = CallingConvention.StdCall, CharSet = CharSet.Ansi)]
        public static extern int PSModifyCardPassword(int Port, int Baud, StringBuilder PstrPrimevalCardPassword, StringBuilder PstrNewCardPassword,
            ref int PintCheckErrorNumber);
        #endregion

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            int PintCardCode = 0;
            short PboolCard = 0;
            StringBuilder PstrCustomer16Id = new StringBuilder(16);
            StringBuilder PstrCardId = new StringBuilder(8);
            StringBuilder PstrOtherCardId = new StringBuilder(8);
            StringBuilder PstrOperatorId = new StringBuilder(8);
            int PlongEnableUseGas = 0;
            int PlongReturnGas = 0;
            int PlongBuyGasNumber = 0;
            int PlongAlarmGas = 0;
            int PlongCardNumber = 0;


            Log.Debug("进入鹏胜的CheckCard");
            int rs = PSTestCard(com , baud, ref PintCardCode, ref PboolCard, PstrCustomer16Id, PstrCardId, ref PlongEnableUseGas,
                ref PlongReturnGas, ref PlongBuyGasNumber, ref PlongAlarmGas, ref PlongCardNumber, PstrOtherCardId,
                 PstrOperatorId, 88888888);
            return rs;
        }


        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
           
            int PintCheckErrorNumber = 0;
            StringBuilder kmm1 = new StringBuilder("888888");
            StringBuilder newkmm = new StringBuilder("ffffff");
            Log.Debug("PengSheng PSModifyCardPassword start and kmm1 is " + kmm1 + " newkmm is " + newkmm);
            int ret = PSModifyCardPassword(com - 1, baud, kmm1, newkmm, ref PintCheckErrorNumber);
            Log.Debug("PengSheng PSModifyCardPassword end and ret is " + ret
                );
            Log.Debug("PengSheng ClearCard start");
            int rs = PSClearAllCard(com, baud, newkmm, ref PintCheckErrorNumber);
            Log.Debug("PengSheng ClearCard end and rs is "+rs);
            return rs;
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
            int PintCardCode = 0;
            short PboolCard = 0;
            StringBuilder PstrCustomer16Id = new StringBuilder(16);
            StringBuilder PstrCardId = new StringBuilder(8);
            StringBuilder PstrOtherCardId = new StringBuilder(8);
            StringBuilder PstrOperatorId = new StringBuilder(8);
            int PlongEnableUseGas = 0;
            int PlongReturnGas = 0;
            int PlongBuyGasNumber = 0;
            int PlongAlarmGas = 0;
            int PlongCardNumber = 0;

            Log.Debug("PengSheng ReadCard Start");
            int rs = PSTestCard(com , baud, ref PintCardCode, ref PboolCard, PstrCustomer16Id, PstrCardId, ref PlongEnableUseGas,
                ref PlongReturnGas, ref PlongBuyGasNumber, ref PlongAlarmGas, ref PlongCardNumber, PstrOtherCardId,
                 PstrOperatorId, 88888888);
            Log.Debug("PengSheng ReadCard end and rs is "+rs);
            kh = PstrCardId.ToString();
            cs = (short)PlongBuyGasNumber;
            ql = PlongEnableUseGas;
            bkcs = (short)PlongCardNumber;
            return rs;
        }
        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj) {
            int PintCardCode = 0;
            short PboolCard = 0;
            StringBuilder PstrCustomer16Id = new StringBuilder(16);
            StringBuilder PstrCardId = new StringBuilder(8);
            StringBuilder PstrOtherCardId = new StringBuilder(8);
            StringBuilder PstrOperatorId = new StringBuilder(8);
            int PlongEnableUseGas = 0;
            int PlongReturnGas = 0;
            int PlongBuyGasNumber = 0;
            int PlongAlarmGas = 0;
            int PlongCardNumber = 0;

            int rs = PSTestCard(com , baud, ref PintCardCode, ref PboolCard, PstrCustomer16Id, PstrCardId, ref PlongEnableUseGas,
                    ref PlongReturnGas, ref PlongBuyGasNumber, ref PlongAlarmGas, ref PlongCardNumber, PstrOtherCardId,
                     PstrOperatorId, 88888888);

            int lrccn = PlongBuyGasNumber + 1;
            int PintCheckErrorNumber = 0;

            int pccn = PlongCardNumber;
            StringBuilder kmm1 = new StringBuilder("888888");
            StringBuilder yhh = PstrCustomer16Id;
            StringBuilder kh1 = new StringBuilder(kh);
            if (ql > 0)
            {
                Log.Debug("sellgas ql=" + ql + ",bkcs=" + pccn);
                rs = PSCustomerCardBuyGas(com , baud, yhh, kh1, ql, ref PlongReturnGas, ref lrccn, ref bjql, pccn, 88888888, kmm1, ref PintCheckErrorNumber);
                Log.Debug("sell gas end rs=" + rs + ",err=" + PintCheckErrorNumber);
                return rs;
            }
            else
            {
                rs = PSCustomerCardBuyGas(com, baud, yhh, kh1, -PlongEnableUseGas, ref PlongReturnGas, ref lrccn, ref bjql, pccn, 88888888, kmm1, ref PintCheckErrorNumber);
                return rs;
            }
        }


        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("进入鹏胜的WriteNewCard,卡状态是"+kzt);
            StringBuilder oldkmm = new StringBuilder("ffffff");
            StringBuilder kmm1 = new StringBuilder("888888");
            StringBuilder yhh1 = new StringBuilder(yhh);
            StringBuilder kh1 = new StringBuilder(kh);
            int PintCheckErrorNumber = 0;
            int ret = PSModifyCardPassword(com, baud, oldkmm, kmm1, ref PintCheckErrorNumber);
            if (kzt == 0)
            {
                int rs = PSAddCustomerCard(com , baud, yhh1, kh1, ql, ref bjql, 88888888, kmm1, ref PintCheckErrorNumber);
                return rs;
            }
            else 
            {
                int bkcs1 = bkcs;
                int rs = PSReplenishCustomerCard(com, baud, yhh1, kh1, ql, cs, ref bjql, ref bkcs1, 88888888, kmm1, ref PintCheckErrorNumber);
                return rs;
            }
        }

        public string Name
        {
            get
            {
                return "PengSheng";
            }
        }

        #endregion
    }
}
