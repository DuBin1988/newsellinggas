using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

using System.IO.Ports;

namespace QwIcDazhuDemo
{
    public partial class DemoFrm : Form
    {
        public DemoFrm()
        {
            InitializeComponent();
        }

        private void btnReadCard_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                short com = Convert.ToInt16(port.PortNum);
                int baud = Convert.ToInt32(cboBaud.SelectedItem);
               
                short klx;          //卡类型
                short kzt;          //卡状态(不支持该参数,输出-1)
                string kh;          //用户卡号
                string dqdm;        //地区代码(不支持该参数,输出null)
                string yhh;         //用户号
                string tm;          //表条码表号
                int ql;             //气量
                short cs;           //购气次数
                int ljgql;          //表累计购气量
                short bkcs;         //补卡次数(不支持该参数,输出-1)
                int ljyql;          //表内累计用气量(不支持该参数,输出-1)
                int syql;           //表内剩余气量
                int bjql;           //表内报警气量(不支持该参数,输出-1)
                int czsx;           //表内充值上限(不支持该参数,输出-1)
                int tzed;           //表内透支额度(不支持该参数,输出-1)
                string sqrq;        //售气日期 格式：20121212
                string sxrq;        //单价生效日期(不支持该参数,输出null)
                string sxbj;        //单价生效标记(不支持该参数,输出null)

                QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                int result = obj.ReadGasCard(com, baud, null, out klx, out kzt, out kh, out dqdm, out yhh, out tm, out ql, out cs, out ljgql, out bkcs, out ljyql, out syql, out bjql, out czsx, out tzed, out sqrq, -1, -1, out sxrq, out sxbj);
                if (result == 0)
                {
                    if (klx > 0)
                    {
                        txtCompanyCode.Text = "重庆前卫克罗姆公司";
                    }
                    else
                    {
                        txtCompanyCode.Text = string.Empty;
                    }

                    switch (klx)
                    {
                        case 0:
                            txtCardType.Text = "无效卡";
                            break;
                        case 1:
                            txtCardType.Text = "新卡";
                            break;
                        case 2:
                            txtCardType.Text = "开户卡";
                            break;
                        case 3:
                            txtCardType.Text = "用户卡";
                            break;
                        case 4:
                            txtCardType.Text = "工具卡";
                            break;
                        default:
                            txtCardType.Text = klx.ToString()+" 未知";
                            break;
                    }
                    txtMeterID.Text = tm;
                    txtUserID.Text = kh;
                    txtCardID.Text = yhh;
                    //if (data.GetReadState())
                    //{
                    //    txtReadState.Text = "表已读卡";
                    //}
                    //else
                    //{
                    //    txtReadState.Text = "表未读卡";
                    //}
                    txtBuyGasVal.Text = ql.ToString();
                    txtBuyGasCount.Text = cs.ToString();
                    txtBuyGasTime.Text = sqrq;
                    txtBuyGasRemain.Text = syql.ToString();
                    txtBuyGasTotal.Text = ljgql.ToString();
                    //if (data.GetRepairState())
                    //{
                    //    txtRepairState.Text = "是补卡";
                    //}
                    //else
                    //{
                    //    txtRepairState.Text = "非补卡";
                    //}
                }
                else
                {
                    ClearIcData();
                    lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnMakeCard_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    short com = Convert.ToInt16(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);

                    string kmm = null;       //卡密码(不使用)
                    short klx = -1;          //卡类型(不使用)
                    short kzt = -1;          //卡状态(不使用)
                    string kh = txtUserID.Text.Trim();                        //用户卡号
                    string dqdm = null;      //地区代码(不使用)
                    string yhh = txtCardID.Text.Trim();                       //用户号
                    string tm = txtMeterID.Text.Trim();                       //表条码表号
                    int ql = int.Parse(txtBuyGasVal.Text.Trim());             //气量

                    short cs = -1;           //购气次数(不使用)
                    int ljgql = -1;          //表累计购气量(不使用)
                    short bkcs = -1;         //补卡次数(不使用)
                    int ljyql = -1;          //表内累计用气量(不使用)
                    
                    int bjql = -1;           //表内报警气量(不使用)
                    int czsx = -1;           //表内充值上限(不使用)
                    int tzed = -1;           //表内透支额度(不使用)
                    string sqrq = null;      //售气日期(不使用)
                    int oldprice = -1;       //现在单价(不使用)
                    int newprice = -1;       //新单价(不使用)
                    string sxrq = null;      //单价生效日期(不使用)
                    string sxbj = null;      //单价生效标记(不使用)

                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int result = obj.WriteNewCard(com, baud, kmm, klx, kzt, kh, dqdm, yhh, tm, ql, cs, ljgql, bkcs, ljyql, bjql, czsx, tzed, sqrq, oldprice, newprice, sxrq, sxbj);
                    if (result == 0)
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "写新卡成功");
                    }
                    else
                    {  
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                    }
                    ClearIcData();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnWriteOrders_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    short com = Convert.ToInt16(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);

                    string kmm = null;                      //卡密码(不使用)
                    short klx = -1;                         //卡类型(不使用)
                    //short kzt = -1;                       //卡状态(不使用)
                    string kh = txtUserID.Text.Trim();                           //用户卡号
                    string dqdm = null;                     //地区代码(不使用)
                    //string yhh = txtCardID.Text.Trim();   //用户号(不使用)
                    //string tm = txtMeterID.Text.Trim();   //表条码表号(不使用)
                    int ql = int.Parse(txtBuyGasVal.Text.Trim());                //气量

                    short cs = short.Parse(txtBuyGasCount.Text.Trim());          //购气次数
                    int ljgql = -1;                         //表累计购气量(不使用)
                    //short bkcs = -1;                      //补卡次数(不使用)
                    //int ljyql = -1;                       //表内累计用气量(不使用)

                    int bjql = -1;                          //表内报警气量(不使用)
                    int czsx = -1;                          //表内充值上限(不使用)
                    int tzed = -1;                          //表内透支额度(不使用)
                    string sqrq = null;                     //售气日期(不使用)
                    int oldprice = -1;                      //现在单价(不使用)
                    int newprice = -1;                      //新单价(不使用)
                    string sxrq = null;                     //单价生效日期(不使用)
                    string sxbj = null;                     //单价生效标记(不使用)

                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int result = obj.WriteGasCard(com, baud, kmm, klx,kh,dqdm,ql,cs, ljgql,bjql, czsx, tzed, sqrq, oldprice, newprice, sxrq, sxbj);
                    if (result == 0)
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "写卡成功");
                    }
                    else
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                    }
                    ClearIcData();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnClearCard_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    short com = Convert.ToInt16(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);

                    string kmm = null;                      //卡密码(不使用)
                    short klx = -1;                         //卡类型(不使用)
                    string kh = txtUserID.Text.Trim();                           //用户卡号
                    string dqdm = null;                     //地区代码(不使用)
        
                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int result = obj.FormatGasCard(com, baud, kmm, klx, kh, dqdm);
                    if (result == 0)
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "销卡成功");
                    }
                    else
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                    }
                    ClearIcData();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnIsNewCard_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    short com = Convert.ToInt16(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);

                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int result = obj.CheckGasCard(com, baud);//, buyGasCount);
                    if (result >= 0)
                    {
                        switch (result)
                        {
                            case 0:
                                txtCardType.Text = "无效卡";
                                break;
                            case 1:
                                txtCardType.Text = "新卡";
                                break;
                            case 2:
                                txtCardType.Text = "开户卡";
                                break;
                            case 3:
                                txtCardType.Text = "用户卡";
                                break;
                            case 4:
                                txtCardType.Text = "工具卡";
                                break;
                            default:
                                txtCardType.Text = result.ToString() + " 未知";
                                break;
                        }
                    }
                    else
                    {
                        ClearIcData();
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                    }
                    
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnRepairCard_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    int portNum = Convert.ToInt32(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);
                    string userID = txtUserID.Text.Trim();
                    string cardID = txtCardID.Text.Trim();
                    string meterID = txtMeterID.Text.Trim();

                    int buyGasCount = int.Parse(txtBuyGasCount.Text.Trim());
                    int buyGasTotal = int.Parse(txtBuyGasTotal.Text.Trim());


                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int flag = obj.RepairCard(portNum, baud, userID, cardID, meterID, buyGasCount, buyGasTotal);
                    MessageBox.Show(flag + "");
                    //if (flag)
                    //{
                    //    ClearIcData();
                    //    lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "补卡成功，请先插表再进行日常购气");
                    //}
                }
                catch (Exception ex)
                {
                    string str = ex.Message;
                    //QwCardICLib.QwICException icEx = ex as QwCardICLib.QwICException;
                    //if (icEx != null)
                    //{
                    //    lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + " 信息号:" + icEx.IcErrCode.ToString() + " 内容:" + icEx.IcErrMessage);
                    //}
                    //else
                    //{
                    //    MessageBox.Show(ex.Message);
                    //}
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void btnExit_Click(object sender, EventArgs e)
        {
            this.Close();
            Application.Exit();
        }

        private void DemoFrm_Load(object sender, EventArgs e)
        {
            cboBaud.SelectedIndex = 0;

            string[] comArr = SerialPort.GetPortNames();
            if (comArr != null)
            {
                cboPort.Items.Clear();
                for (int i = 0; i < comArr.Length; i++)
                {
                    ComItem item = new ComItem(comArr[i]);
                    cboPort.Items.Add(item);
                }
            }
            cboPort.SelectedIndex = 0;
        }

        //private void LoadIcData(QwCardICLib.QwICardData data)
        //{
        //    if (data != null)
        //    {
        //        txtCompanyCode.Text = data.GetCompanyName();
        //        txtCardType.Text = data.GetCardTypeName();
        //        txtMeterID.Text = data.MeterID;
        //        txtUserID.Text = data.UserID;
        //        txtCardID.Text = data.CardID;
        //        if (data.GetReadState())
        //        {
        //            txtReadState.Text = "表已读卡";
        //        }
        //        else
        //        {
        //            txtReadState.Text = "表未读卡";
        //        }
        //        txtBuyGasVal.Text = data.BuyGasVal.ToString();
        //        txtBuyGasCount.Text = data.BuyGasCount.ToString();
        //        txtBuyGasTime.Text = data.BuyGasTime;
        //        txtBuyGasRemain.Text = data.BuyGasRemain.ToString();
        //        txtBuyGasTotal.Text = data.BuyGasTotal.ToString();
        //        if (data.GetRepairState())
        //        {
        //            txtRepairState.Text = "是补卡";
        //        }
        //        else
        //        {
        //            txtRepairState.Text = "非补卡";
        //        }
        //    }
        //}

        private void ClearIcData()
        {
            txtCompanyCode.Text = "";
            txtCardType.Text = "";
            txtMeterID.Text = "";
            txtUserID.Text = "";
            txtCardID.Text = "";
            txtReadState.Text = "";
            txtBuyGasVal.Text = "";
            txtBuyGasCount.Text = "";
            txtBuyGasTime.Text = "";
            txtBuyGasRemain.Text = "";
            txtBuyGasTotal.Text = "";
            txtRepairState.Text = "";
        }

        private void btnReadCard_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = false;
            txtUserID.Enabled = false;
            txtCardID.Enabled = false;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = false;
            txtBuyGasCount.Enabled = false;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = false;
            txtRepairState.Enabled = false;
        }

        private void btnMakeCard_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = true;
            txtUserID.Enabled = true;
            txtCardID.Enabled = true;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = true;
            txtBuyGasCount.Enabled = false;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = false;
            txtRepairState.Enabled = false;
        }

        private void btnWriteOrders_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = false;//true;
            txtUserID.Enabled = true;
            txtCardID.Enabled = false;//true;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = true;
            txtBuyGasCount.Enabled = true;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = false;
            txtRepairState.Enabled = false;
        }

        private void btnRepairCard_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = true;
            txtUserID.Enabled = true;
            txtCardID.Enabled = true;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = false;
            txtBuyGasCount.Enabled = true;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = true;
            txtRepairState.Enabled = false;
        }

        private void btnClearCard_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = false;
            txtUserID.Enabled = true;
            txtCardID.Enabled = false;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = false;
            txtBuyGasCount.Enabled = false;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = false;
            txtRepairState.Enabled = false;
        }

        private void btnIsNewCard_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = false;
            txtUserID.Enabled = false;
            txtCardID.Enabled = false;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = false;
            txtBuyGasCount.Enabled = false;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = false;
            txtRepairState.Enabled = false;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            ComItem port = cboPort.SelectedItem as ComItem;
            if (port != null)
            {
                try
                {
                    int portNum = Convert.ToInt32(port.PortNum);
                    int baud = Convert.ToInt32(cboBaud.SelectedItem);
                    string userID = txtUserID.Text.Trim();
                    string cardID = txtCardID.Text.Trim();
                    string meterID = txtMeterID.Text.Trim();

                    int buyGasCount = int.Parse(txtBuyGasCount.Text.Trim());
                    int buyGasTotal = int.Parse(txtBuyGasTotal.Text.Trim());


                    QwCardICLib.MWOperate4442 obj = QwCardICLib.MWOperate4442.GetInstance();
                    int result = obj.ReturnCard(portNum, baud, "1", "12345678", "1", buyGasCount, buyGasTotal);
                    if (result == 0)
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "退气成功，请进行日常购气");
                    }
                    else
                    {
                        lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + "  信息号:" + result.ToString());
                    }
                    ClearIcData();
                }
                catch (Exception ex)
                {
                    string str = ex.Message;
                    //QwCardICLib.QwICException icEx = ex as QwCardICLib.QwICException;
                    //if (icEx != null)
                    //{
                    //    lbMsg.Items.Add(DateTime.Now.ToString("MM-dd hh:mm:ss") + " 信息号:" + icEx.IcErrCode.ToString() + " 内容:" + icEx.IcErrMessage);
                    //}
                    //else
                    //{
                    //    MessageBox.Show(ex.Message);
                    //}
                }
            }
            else
            {
                MessageBox.Show("请选择合适的端口");
            }
        }

        private void button1_MouseEnter(object sender, EventArgs e)
        {
            txtCompanyCode.Enabled = false;
            txtCardType.Enabled = false;
            txtMeterID.Enabled = true;
            txtUserID.Enabled = true;
            txtCardID.Enabled = true;
            txtReadState.Enabled = false;
            txtBuyGasVal.Enabled = false;
            txtBuyGasCount.Enabled = true;
            txtBuyGasTime.Enabled = false;
            txtBuyGasRemain.Enabled = false;
            txtBuyGasTotal.Enabled = true;
            txtRepairState.Enabled = false;
        }

    }

    class ComItem
    {
        private int portNum;
        private string portName;
        public ComItem(string name)
        {
            portName = name;
            string temp = portName.Replace("COM", "");
            portNum = int.Parse(temp);
        }

        public int PortNum
        {
            get { return portNum; }
            set { portNum = value; }
        }

        public string PortName
        {
            get { return portName; }
            set { portName = value; }
        }
        public override string ToString()
        {
            return this.portName;
        }
    }
}
