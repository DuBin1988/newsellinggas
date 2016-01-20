using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;

namespace Card
{
    public class YcTj : ICard
    {
        private static Log Log = Log.GetInstance("Card.YcTj");

        public string Test()
        {
            return "tianjia";
        }
      


        #region ICard Members
        YCTJ.YCTJICCard tj = new YCTJ.YCTJICCard();
        public int CheckGasCard(short com, int baud)
        {
            return tj.CheckOwnCard(com, baud);
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            return tj.ClearAllCard(com, baud);
        }

        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs,ref string dm)
        {
            Log.Debug("ReadGasCard tianjia start");
            int ICType = 0;
            double ICCSpare = 0;
            int GASCOUNT = 0;
            int CusType = 0;
            double ICUsed = 0;
            double ICMSpare = 0;
            int ICNum = 0;
            string ICMark = "";
            string ICMUType = "";
            int rs = tj.ReadICCard(com, baud, out kh, out ICType, out ICCSpare, out GASCOUNT, out CusType, out ICUsed, out ICMSpare, out ICNum, out ICMark, out ICMUType);
            cs = (short) GASCOUNT;
            ql = (int)ICCSpare;
            
            return rs;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            Log.Debug("WriteGasCard tianjia start");
            int rs = tj.WriteICCard(com, baud,
                kh, //卡号
                6, //操作类型
                cs, //购气次数
                ql, //购气量
                0, //卡类型
                null);  //卡号为11位*/
            return rs;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, Int32 bjql, Int32 czsx, Int32 tzed, string sqrq, string cssqrq, Int32 oldprice, Int32 newprice, string sxrq, string sxbj)
        {
            Log.Debug("WriteNewCard tianjia start");
            int rs = tj.WriteICCard(com, baud,
                kh, //卡号
                7, //操作类型
                cs, //购气次数
                ql, //购气量
                0, //卡类型
                null);  //卡号为11位*/
            return rs;
        }

        public string Name
        {
            get
            {
                return "tianjia";
            }
        }

        #endregion
    }
}

