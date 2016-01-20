using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
namespace tianx
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void read_Click(object sender, EventArgs e)
        {
            //StringBuilder ICId = new StringBuilder(16);
            //StringBuilder ICMark = new StringBuilder(12);
            string ICId;
            string ICMark;
            int COMID;
            int GasCount;
            int ICCSpare;
            double ICUsed;
            int ICSum;
            double ICConsume;
            int ICPrice;
            int ICType;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;
            result1 = tianxMoney.CardTX.User_ReadICCard(COMID, out  ICId, out  ICMark, out GasCount, out ICCSpare, out ICUsed, out ICSum, out ICConsume, out ICPrice, out ICType);
            if ((result1 == 0) || (result1 == 10))
            {
                textBox1.Text = ICId;
                textBox2.Text = ICMark;
                textBox3.Text = Convert.ToString(GasCount);
                textBox4.Text = Convert.ToString(ICCSpare);
                textBox6.Text = Convert.ToString(ICUsed);
                textBox7.Text = Convert.ToString(ICSum);
                textBox8.Text = Convert.ToString(ICConsume);
                textBox9.Text = Convert.ToString(ICPrice);
                textBox10.Text = Convert.ToString(ICType);
            }
            else
            {

                textBox1.Text = "";
                textBox2.Text = "";
                textBox3.Text = "";
                textBox4.Text = "";
                textBox6.Text = "";
                textBox7.Text = "";
                textBox8.Text = "";
                textBox9.Text = "";
                textBox10.Text = "";
            }
            textBox14.Text = Convert.ToString(result1);
           
        }

        private void write_Click(object sender, EventArgs e)
        {
            StringBuilder ICId = new StringBuilder(16);
            StringBuilder ICMark = new StringBuilder(12);
            //string ICId;
            //string ICMark;
            int COMID;
            int GasCount;
            int ICCSpare;
            //double ICUsed;
            //int ICSum;
            //double ICConsume;
            int ICPrice;
            //int ICType;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;
            //ICId = (System.Text.StringBuilder)textBox1.Text;
            foreach (char c in textBox1.Text) { ICId.Append(c); }

            //ICMark = System.Text.StringBuilder(textBox2.Text);
            foreach (char c in textBox2.Text) { ICMark.Append(c); }

            ICCSpare = Convert.ToInt32(textBox4.Text);
            GasCount = Convert.ToInt16(textBox3.Text);
            ICPrice = Convert.ToInt32(textBox9.Text);

            result1 = tianxMoney.CardTX.WriteNewICCard(ICId, ICMark, ICCSpare, GasCount, ICPrice, COMID);
            textBox14.Text = Convert.ToString(result1);


        }

        private void tools_Click(object sender, EventArgs e)
        {
            //StringBuilder ICId = new StringBuilder(16);
            //StringBuilder ICMark = new StringBuilder(12);
            //string ICId;
            //string ICMark;
            int COMID;
            int GasCount;
            int ICCSpare;
            //double ICUsed;
            //int ICSum;
            //double ICConsume;
            //int ICPrice;
            //int ICType;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;

            ICCSpare = Convert.ToInt32(textBox4.Text);
            GasCount = Convert.ToInt16(textBox3.Text);
            //ICPrice = Convert.ToInt32(textBox9.Text);

            result1 = tianxMoney.CardTX.BuyGas(ICCSpare, GasCount, COMID);
            textBox14.Text = Convert.ToString(result1);
        }

        private void label5_Click(object sender, EventArgs e)
        {
            string Price_T;
            string Effective_T;
            int COMID;
            int Price;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;
            result1 = tianxMoney.CardTX.regulator_ReadICCard(COMID, out Price, out Price_T, out Effective_T);


            if (result1 == 0)
            {
                textBox12.Text = Price_T;
                textBox11.Text = Effective_T;
                textBox13.Text = Convert.ToString(Price);
            }
            else
            {
                textBox12.Text = "";
                textBox11.Text = "";
                textBox13.Text = "";
            }
            textBox15.Text = Convert.ToString(result1);

        }

        private void read_Click_1(object sender, EventArgs e)
        {

            StringBuilder Price_T = new StringBuilder(12);
            StringBuilder Effective_T = new StringBuilder(12);
            int COMID;
            int Price;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;
            foreach (char c in textBox12.Text) { Price_T.Append(c); }

            foreach (char c in textBox11.Text) { Effective_T.Append(c); }

            Price = Convert.ToInt32(textBox13.Text);

            result1 = tianxMoney.CardTX.regulator_WriteICCard(Price, Price_T, Effective_T, COMID);
            textBox15.Text = Convert.ToString(result1);
        }

        private void button1_Click(object sender, EventArgs e)
        {

            StringBuilder ICId = new StringBuilder(16);
            StringBuilder ICMark = new StringBuilder(12);
            //string ICId;
            //string ICMark;
            int COMID;
            int GasCount;
            int ICCSpare;
            //double ICUsed;
            //int ICSum;
            //double ICConsume;
            int ICPrice;
            //int ICType;
            COMID = Convert.ToInt32(textBox5.Text);
            int result1;
            //ICId = (System.Text.StringBuilder)textBox1.Text;
            foreach (char c in textBox1.Text) { ICId.Append(c); }

            //ICMark = System.Text.StringBuilder(textBox2.Text);
            foreach (char c in textBox2.Text) { ICMark.Append(c); }

            ICCSpare = Convert.ToInt32(textBox4.Text);
            GasCount = Convert.ToInt16(textBox3.Text);
            ICPrice = Convert.ToInt32(textBox9.Text);

            result1 = tianxMoney.CardTX.WriteOldICCard(ICId, ICMark, ICCSpare, GasCount, ICPrice, COMID);
            textBox14.Text = Convert.ToString(result1);
        }

        private void button4_Click(object sender, EventArgs e)
        {
            int result1;
            int COMID;
            COMID = Convert.ToInt16(textBox5.Text);
            result1 = tianxMoney.CardTX.User_ClearICCard(COMID);
            textBox14.Text = Convert.ToString(result1);
        }

    }
}
