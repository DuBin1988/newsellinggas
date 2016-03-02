using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.IO.IsolatedStorage;
using System.IO;
using System.Text;
using System.Runtime.InteropServices;
using Com.Aote.Logs;
using System.Threading.Tasks;
//using System.Windows.Forms;

namespace Card
{
    public class ShuMa : ICard
    {
        private static Log Log = Log.GetInstance("Card.ShuMa");
        public string Test()
        {
            return "ShuMa";
        }

        #region ICard Members
        public int CheckGasCard(short com, int baud)
        {
            return -1;
        }

        public int FormatGasCard(short com, int baud, string kmm, string kh, string dqdm)
        {
            return -1;
        }



        public int WriteGasCard(short com, int baud, ref string kmm, string kh, string dqdm, int ql, int csql, int ccsql, short cs, int ljgql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = 1;
            try
            {
                Log.Debug("sell gas start:" + "--kh--" + kh + "--ql--" + ql+"--cs--"+cs);

              string result =   (Encrypt(Convert.ToInt32(kh), (cs) * 1000 + ql) + "").PadLeft(8, '0');
              //MessageBox.Show("购气代码为：" + result);
              kmm = result;
             if(result.Equals(null))
             {
                 ret =  -1;
             }
            else
             {
                 ret = 0;
             } 
            }
            catch (Exception e)
            {
                Log.Debug("sell gas exption is:" + e.Message + e.StackTrace);
            }

            return ret;
        }

        public int WriteNewCard(short com, int baud, ref string kmm, short kzt, string kh, string dqdm, string yhh, string tm, int ql, int csql, int ccsql, short cs, int ljgql, short bkcs, int ljyql, int bjql, int czsx, int tzed, string sqrq, string cssqrq, int oldprice, int newprice, string sxrq, string sxbj)
        {
            int ret = 1;
            try
            {
                Log.Debug("生成设置码开始： kh is:" + kh);
                if (0 == kzt)
                {
                    string kh1=kh.Substring(kh.Length - 6, 6);
                    string result = (Encrypt(525252, Convert.ToInt32(kh1)) + "").PadLeft(8, '0');
                   // MessageBox.Show("设置码："+result);
                    kmm = result;
                    Log.Debug("--kmm--" + kmm);
                    if(result.Equals(null))
                    {
                        ret = -1;
                    }
                    else
                    {
                        ret = 0;
                    }
                }
                else if(2 == kzt)
                {
                        string result1 = (Encrypt(Convert.ToInt32(kh), 0) + "").PadLeft(8, '0');//生成清零码
                        string result2 = (Encrypt(Convert.ToInt32(kh), (cs) * 1000 + ql) + "").PadLeft(8, '0');//剩余气量吗
                        string result3 = (Encrypt(525252, Convert.ToInt32(kh)) + "").PadLeft(8, '0');//设置码
                        kmm = result1 + "|" + result3 + "|" + result2;
                       // MessageBox.Show("清零码：" + result1 + "---" + "设置码：" + result3 + "---" + "剩余气量码:" + result2);
                        ret = 0;
                }
                else    //如果气量等于0，补卡
                {
                    Log.Debug("生成解锁码开始：");
                    int mc = Convert.ToInt32(kh);
                    String codes = "";
                   // cs = (short)(cs - 1);
                    for (int i = cs - 1; i >= 0; i--)
                    {
                        codes = (Encrypt(mc, 1000 * (i + 1) + mc % 1000) + "").PadLeft(8, '0') + "|" + codes;
                    }
                    string opencodes = codes.TrimEnd(new Char[] { '|' });
                    //MessageBox.Show("解锁码："+opencodes);
                    kmm = opencodes;
                    if (codes.Equals(null))
                    {
                        ret = -1;
                    }
                    else
                    {
                        ret = 0;
                    }
                }
            }
            catch (Exception e)
            {
                Log.Debug("make card end:" + e.Message + "--stack--:" + e.StackTrace);
            }
            return ret;
        }

        public string Name
        {
            get
            {
                return "ShuMa";
            }
        }
        public int ReadGasCard(short com, int baud, ref string kh, ref int ql, ref decimal money, ref short cs, ref short bkcs, ref string dqdm)
        {
            return -1;
        }
        #endregion
        #region Resources
        private static int Encrypt(int intMkey, int intOriginal)
        {
            int[] array = new int[10];
            int[] array2 = new int[10];
            short[] array3 = new short[7];
            short[] array4 = new short[7];
            short[] array5 = new short[7];
            short[] array6 = new short[12];
            checked
            {
                int result;
                try
                {
                    array3[0] = (short)(intMkey / 4096);
                    array4[0] = (short)(intMkey % 4096);
                    array5[0] = 0;
                    int num = 1;
                    do
                    {
                        array5[num] = array3[num - 1];
                        array3[num] = array4[num - 1];
                        array4[num] = (short)SecretKey((int)array3[num - 1], (int)(array4[num - 1] ^ array5[num - 1]));
                        array6[2 * num - 2] = (short)(array4[num] % 64);
                        array6[2 * num - 1] = (short)(array4[num] / 64);
                        num++;
                    }
                    while (num <= 6);
                    array[0] = intOriginal / 4096;
                    array2[0] = intOriginal % 4096;
                    array[1] = (array[0] ^ (int)(array6[4] * 64 + array6[5]));
                    array2[1] = (array2[0] ^ (int)(array6[6] * 64 + array6[7]));
                    array[2] = array[1];
                    array2[2] = (array2[1] ^ array[1]);
                    num = 3;
                    do
                    {
                        array[num] = array2[num - 1];
                        array2[num] = (array[num - 1] ^ ftnEncrypt(array2[num - 1], (int)array6[num - 3]));
                        num++;
                    }
                    while (num <= 6);
                    array2[7] = array2[6];
                    array[7] = (array[6] ^ array2[6]);
                    array2[8] = (array2[7] ^ (int)(array6[8] * 64 + array6[9]));
                    array[8] = (array[7] ^ (int)(array6[10] * 64 + array6[11]));
                    int num2 = 525252;
                    array2[9] = (array2[8] ^ num2 / 4096);
                    array[9] = (array[8] ^ num2 % 4096);
                    result = array[9] * 4096 + array2[9];
                }
                catch (Exception e)
                {
                    result = 0;
                }
                return result;
            }
        }

        private static int ftnEncrypt(int intX1, int intX2)
        {
            short[] array = new short[4];
            short[] array2 = new short[3];
            short[] array3 = new short[4];
            checked
            {
                array[3] = (short)(intX1 % 8);
                intX1 /= 8;
                array[2] = (short)(intX1 % 8);
                intX1 /= 8;
                array[1] = (short)(intX1 % 8);
                intX1 /= 8;
                array[0] = (short)(intX1 % 8);
                intX1 /= 8;
                array2[1] = (short)(intX2 % 8);
                intX2 /= 8;
                array2[0] = (short)(intX2 % 8);
                intX2 /= 8;
                short intX3 = (short)(array[1] ^ array2[0] ^ array[0]);
                short num = (short)(array[2] ^ array2[1] ^ array[3]);
                array3[1] = Movs3(intX3, num, 1);
                array3[2] = Movs3(num, array3[1], 0);
                array3[3] = Movs3(array[3], array3[1], 1);
                array3[0] = Movs3(array[0], array3[1], 0);
                return (int)Math.Round(unchecked((double)array3[0] * 512.0 + (double)array3[1] * 64.0 + (double)array3[2] * 8.0 + (double)array3[3]));
            }
        }

        private static int SecretKey(int intA, int intB)
        {
            checked
            {
                short num = (short)(intA % 8);
                intA /= 8;
                short num2 = (short)(intA % 8);
                intA /= 8;
                short num3 = (short)(intA % 8);
                intA /= 8;
                short num4 = (short)(intA % 8);
                intA /= 8;
                short num5 = (short)(intB % 8);
                intB /= 8;
                short num6 = (short)(intB % 8);
                intB /= 8;
                short num7 = (short)(intB % 8);
                intB /= 8;
                short num8 = (short)(intB % 8);
                intB /= 8;
                short intX = (short)(num3 ^ num4);
                short num9 = (short)(num ^ num2);
                short num10 = (short)(Movs3((short)intX, (short)(num9 ^ num8), 1));
                short num11 = (short)(Movs3((short)num9, (short)(num10 ^ num7), 0));
                short num12 = (short)(Movs3((short)num4, (short)(num10 ^ num6), 0));
                short num13 = (short)(Movs3((short)num, (short)(num11 ^ num5), 1));
                return (int)Math.Round(unchecked((double)num12 * 512.0 + (double)num10 * 64.0 + (double)num11 * 8.0 + (double)num13));
            }
        }
        private static short Movs3(short intX1, short intX2, short intδ)
        {
            short num = (short)((intX1 + intX2 + intδ) % 8);
            checked
            {
                short num2 = (short)(num / 2 + num % 2 * 4);
                return (short)(num2 / 2 + num2 % 2 * 4);
            }
        }
        #endregion
    }
}
