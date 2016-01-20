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

        #region 克罗姆动态库引入
        //测卡，标准接口
        [DllImport("QwICCard.dll", EntryPoint = "CheckGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticCheckGasCard(Int16 com, Int32 baut);

        //读卡，标准接口
        [DllImport("QwICCard.dll", EntryPoint = "ReadGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadGasCard(Int16 com, Int32 baut, byte[] kmm, ref Int16 klx, ref Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, ref Int32 ql, ref Int16 cs, ref Int32 ljgql, ref Int16 bkcs,
            ref Int32 ljyql, ref Int32 syql, ref Int32 bjql, ref Int32 czsx, ref Int32 tzed, byte[] sqrq, ref Int32 oldprice,
            ref Int32 newprice, byte[] sxrq, byte[] sxbj);

        //写新卡，标准接口
        [DllImport("QwICCard.dll", EntryPoint = "WriteNewCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteNewCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, Int16 kzt,
            byte[] kh, byte[] dqdm, byte[] yhh, byte[] tm, Int32 ql, Int16 cs, Int32 ljgql, Int16 bkcs, Int32 ljyql,
            Int32 bjql, Int32 czsx, Int32 tzed, byte[] sqrq, Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);

        //写购气卡，标准接口
        [DllImport("QwICCard.dll", EntryPoint = "WriteGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticWriteGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh,
            byte[] dqdm, byte[] yhh, Int32 ql, Int16 cs, Int32 ljgql, Int32 bjql, Int32 czsx, Int32 tzed,
            byte[] sqrq, Int32 oldprice, Int32 newprice, byte[] sxrq, byte[] sxbj);

        //格式化卡，标准接口
        [DllImport("QwICCard.dll", EntryPoint = "FormatGasCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int StaticFormatGasCard(Int16 com, Int32 baut, byte[] kmm, Int16 klx, byte[] kh, byte[] dqdm, byte[] yhh);

        #endregion

        public string Test()
        {
            return "ruisen";
        }

        #region ICard Members

        public int CheckGasCard(short com, int baud)
        {
            return StaticCheckGasCard(com, baud);
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            int result = -1;
            try
            {
                byte[] omm = new byte[16] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                short oklx = 0;
                short okzt = 0;
                byte[] ocardNO = new byte[100];
                byte[] odqdm = new byte[100];
                byte[] osqrq = new byte[100];
                byte[] osxrq = new byte[100];
                byte[] osxbj = new byte[100];
                byte[] otm = new byte[100];
                int oql = 0;
                short ocs = 0;
                int oljgql = 0;
                int oljyql = 0;
                int osyql = 0;
                int objql = 0;
                int oczsx = 0;
                int otzed = 0;
                short obkcs = 0;
                int ooldprice = 0;
                int onewprice = 0;
                //读出卡中用户号
                byte[] yhh = new byte[100];
                Log.Debug("擦卡读卡开始：");
                int ret = ReadGasCard(com, baud, omm, ref oklx, ref okzt, ocardNO, odqdm, yhh, otm,
                    ref oql, ref ocs, ref oljgql, ref obkcs, ref oljyql, ref osyql, ref objql, ref oczsx,
                    ref otzed, osqrq, ref ooldprice, ref onewprice, osxrq, osxbj);
                Log.Debug("读卡结束：" + ret);
                string cardNO1 = Encoding.ASCII.GetString(ocardNO, 0, 8);
                string yhh1 = Encoding.ASCII.GetString(yhh, 0, 10);
                Log.Debug("擦卡开始:kh:" + cardNO1 + "--yhh:" + yhh1);
                result = StaticFormatGasCard(com, baud, omm, oklx, ocardNO, odqdm, yhh);
                Log.Debug("clear card end:" + result);
            }
            catch (Exception e)
            {
                Log.Debug("擦卡异常:"+e.Message+"---"+e.StackTrace);
            }
           
            return result;
        }

        public int OpenCard(short com, int baud)
        {
            return -1;
        }


        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string yhh)
        {
            int ret = -1;
            try
            {
                byte[] cardNO = new byte[100];
                byte[] kmm = new byte[100];
                byte[] dqdm = new byte[100];
                byte[] yh = new byte[100];
                byte[] sqrq = new byte[100];
                byte[] sxrq = new byte[100];
                byte[] sxbj = new byte[100];
                byte[] tm = new byte[100];
                short klx = 0;
                short kzt = 0;
                int ljgql = 0;
                int ljyql = 0;
                int syql = 0;
                int bjql = 0;
                int czsx = 0;
                int tzed = 0;
                int oldprice = 0;
                int newprice = 0;
                ret = ReadGasCard(com, baud, kmm, ref klx, ref kzt, cardNO, dqdm, yh, tm,
                    ref ql, ref cs, ref ljgql, ref bkcs, ref ljyql, ref syql, ref bjql, ref czsx,
                    ref tzed, sqrq, ref oldprice, ref newprice, sxrq, sxbj);
                //卡号转换成字符串
                cardNO[8] = 0;
                kh = Encoding.ASCII.GetString(cardNO, 0, 8);
                Log.Debug("yh is:" + yh[0] + "," + yh[1]);
                int pos = -1;
                for (int i = 0; i < yh.Length; i++)
                {
                    if (yh[i] == 0)
                    {
                        pos = i;
                        break;
                    }
                }


                yhh = Encoding.ASCII.GetString(yh, 0, pos);
                Log.Debug("用户号：" + yhh);
            }
            catch (Exception e)
            {
                Log.Debug("读卡异常:"+e.StackTrace+"----"+e.Message);
            }
          
            return ret;
        }

        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            String buf;
            MingHua.GetSnapShot(com, baud, out buf);

            int ret = -1;
            try
            {
                byte[] omm = new byte[16] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                short oklx = 0;
                short okzt = 0;
                byte[] ocardNO = new byte[100];
                byte[] odqdm = new byte[100];
                byte[] osqrq = new byte[100];
                byte[] osxrq = new byte[100];
                byte[] osxbj = new byte[100];
                byte[] otm = new byte[100];
                int oql = 0;
                short ocs = 0;
                int oljgql = 0;
                int oljyql = 0;
                int osyql = 0;
                int objql = 0;
                int oczsx = 0;
                int otzed = 0;
                short obkcs = 0;
                int ooldprice = 0;
                int onewprice = 0;
                //读出卡中用户号
                byte[] yhh = new byte[100];
                Log.Debug("read before sell gas:");
                ret = ReadGasCard(com, baud, omm, ref oklx, ref okzt, ocardNO, odqdm, yhh, otm,
                    ref oql, ref ocs, ref oljgql, ref obkcs, ref oljyql, ref osyql, ref objql, ref oczsx,
                    ref otzed, osqrq, ref ooldprice, ref onewprice, osxrq, osxbj);
                Log.Debug("read end before sell gas:" + ret);
                //用户号10位
                yhh[10] = 0;
                string yhhh = Encoding.ASCII.GetString(yhh, 0, 10);
                Log.Debug("yhh is:" + yhhh);
                byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes(dqdm);
                byte[] btm = new byte[8];
                byte[] bsqrq = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
                byte[] bsxrq = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
                byte[] bsxbj = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
                Log.Debug("买气开始,卡号:" + kh + "--dqdm:" + dqdm + "--yhh:" + yhhh + "--cs" + cs);
                ret = StaticWriteGasCard(com, baud, omm, 1, cardNO, bdqdm, yhh, ql,
                    cs, ljgql, bjql, czsx, tzed, bsqrq, oldprice, newprice, bsxrq, bsxbj);
                Log.Debug("sell gas end:" + ret);
                int pos = -1;
                for (int i = 0; i < omm.Length; i++)
                {
                    if (omm[i] == 0)
                    {
                        pos = i;
                        break;
                    }
                }
                kmm = Encoding.ASCII.GetString(omm, 0, pos);
            }
            catch (Exception e)
            {
               Log.Debug("购气异常:"+e.Message+"----"+e.StackTrace);
            }

            String buf2;
            MingHua.GetSnapShot(com, baud, out buf2);
            Log.Debug(buf);
            Log.Debug(buf2);

            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            String buf;
            MingHua.GetSnapShot(com, baud, out buf);

            int ret = -1;
            try
            { 
                        byte[] mm = new byte[16] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                       // byte[] omm = System.Text.Encoding.GetEncoding(1252).GetBytes(kmm);
                        byte[] cardNO = System.Text.Encoding.GetEncoding(1252).GetBytes(kh);
                        byte[] bdqdm = System.Text.Encoding.GetEncoding(1252).GetBytes(dqdm);
                       byte[] byhh1 = System.Text.Encoding.GetEncoding(1252).GetBytes(yhh);
                        short oklx = 1;
                        byte[] btm = new byte[8];
                        byte[] bsqrq = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
                        byte[] bsxrq = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
                        byte[] bsxbj = new byte[8] { 0, 0, 0, 0, 0, 0, 0, 0 };
           
                        short okzt = 0;
                        byte[] ocardNO = new byte[100];
                        byte[] odqdm = new byte[100];
                        byte[] osqrq = new byte[100];
                        byte[] osxrq = new byte[100];
                        byte[] osxbj = new byte[100];
                        byte[] otm = new byte[100];
                        int oql = 0;
                        short ocs = 0;
                        int oljgql = 0;
                        int oljyql = 0;
                        int osyql = 0;
                        int objql = 0;
                        int oczsx = 0;
                        int otzed = 0;
                        short obkcs = 0;
                        int ooldprice = 0;
                        int onewprice = 0;
                        //读出卡中用户号
                        byte[] yhh1 = new byte[100];
                        Log.Debug("发卡或者补卡前读卡:");
                        ret = ReadGasCard(com, baud, mm, ref oklx, ref okzt, ocardNO, odqdm, yhh1, otm,
                            ref oql, ref ocs, ref oljgql, ref obkcs, ref oljyql, ref osyql, ref objql, ref oczsx,
                            ref otzed, osqrq, ref ooldprice, ref onewprice, osxrq, osxbj);
                        Log.Debug("读卡结果:" + ret);
                        if (0 == ret)
                        {
                            Log.Debug("读卡结果为0，则进行擦卡：");
                            kh = Encoding.ASCII.GetString(ocardNO, 0, 8);
                            yhh = Encoding.ASCII.GetString(yhh1, 0, 10);
                            
                            Log.Debug("擦卡开始parevalues is:" + "--kh" + kh + "--yhh" + yhh);
                            Int32 ret1 = StaticFormatGasCard(com, baud, mm, oklx, ocardNO, odqdm, yhh1);
                            Log.Debug("擦卡结束：" + ret1);
                        } 
                        //int ret = 1;
        
                    if (0 == kzt)
                    {
                        Log.Debug("发卡开始，参数:卡号:"+kh+"--气量："+ql+"--次数:"+cs+"--用户号:"+yhh);
                        ret = StaticWriteNewCard(com, baud, mm, 1, kzt, cardNO, bdqdm, byhh1, btm,
                              ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, 0, 0, bsxrq, bsxbj);
                      
                        Log.Debug("发卡结束:" + ret);
                    }
                    else
                    {
                        Log.Debug("补卡开始,参数：卡号："+kh+"--用户号:"+yhh+"--次数:"+cs);
                        ret = StaticWriteNewCard(com, baud, mm, 1, kzt, cardNO, bdqdm, byhh1, btm,
                            ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, bsqrq, 0, 0, bsxrq, bsxbj);
                        
                      //  Log.Debug("kmm is:"+kmm);
                        Log.Debug("补卡结束" + ret);
                    }
            }catch(Exception e)
            {
                Log.Debug("操作新卡异常："+e.Message+e.StackTrace);
            }

            String buf2;
            MingHua.GetSnapShot(com, baud, out buf2);
            Log.Debug(buf);
            Log.Debug(buf2);

            return ret;
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
