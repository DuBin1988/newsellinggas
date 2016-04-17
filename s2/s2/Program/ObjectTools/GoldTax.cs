using Com.Aote.Logs;
using s2;
using System;
using System.ComponentModel;
using System.Net;
using System.Runtime.InteropServices.Automation;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.ObjectTools
{
    //金税盘
    public class GoldTax : CustomTypeHelper, IName
    {
        private static Log Log = Log.GetInstance("Com.Aote.ObjectTools.GoldTax");
        public string Name { get; set; }

        dynamic obj;

        private string ip = "192.168.1.205:8001";
        public void Init()
        {
            try
            {
                Log.Debug("开始初始化金税盘");
                App pub = Application.Current as App;
                if (pub.GoldTax == null)
                {
                    obj = AutomationFactory.CreateObject("TaxCardX.GoldTax");
                    //开启
                    obj.CertPassWord = "0";
                    obj.OpenCard();
                    obj.CertPassWord = ip;
                    obj.OpenCard();
                    if (obj.RetCode != 1011)
                    {
                        Log.Debug("初始化金税盘失败-" + obj.RetCode + obj.RetMsg);
                        MessageBox.Show(obj.RetMsg);
                    }
                    else
                    {
                        Log.Debug("初始化金税盘成功-" + obj.RetCode);
                        MessageBox.Show("初始化金税盘成功");
                    }
                    pub.GoldTax = obj;
                }
                else 
                {
                    obj = pub.GoldTax;
                }
            }
            catch (Exception ee)
            {
                Log.Debug("初始化金税盘异常-" + ee.Message);
                MessageBox.Show("初始化金税盘异常");
            }
           
        }

        private bool isinit =false;
        //是否初始化
        public bool IsInit 
        {
            get { return this.isinit; }
            set
            {
                this.isinit = value;
                if (isinit)
                {
                    //初始化金税盘，开启金税盘
                    Init(); 
                }
                
            }
        }

        /// <summary>
        /// 查询库存发票
        /// </summary>
        public bool GetInfo()
        {
            //没有初始化,不予执行
            if (!IsInit) return true;
            try
            {
                Log.Debug("查询库存发票");
                //查询库存发票
                obj.InfoKind = 2;
                obj.GetInfo();
                if (obj.RetCode != 3011)
                {
                    Log.Debug("查询库存发票失败-" + obj.RetCode + obj.RetMsg);
                    MessageBox.Show(obj.RetMsg);
                    return false;
                }
                else
                {

                    //十位发票代码
                    InfoTypeCode = obj.InfoTypeCode;
                    //发票号码
                    InfoNumber = obj.InfoNumber;
                    Log.Debug("查询库存发票成功-(十位发票代码)InfoTypeCode=" + InfoTypeCode);
                    Log.Debug("查询库存发票成功-(发票号码)InfoNumber=" + InfoNumber);
                }
            }
            catch (Exception ee)
            {
                Log.Debug("查询库存发票异常-" + ee.Message);
                return false;
            }
            return true;
        }
        /// <summary>
        /// 开具发票,打印
        /// </summary>
        public void Invoice()
        {
            //没有初始化,不予执行
            if (!IsInit) return;
            this.State = State.Start;
            Log.Debug("开始开具发票");
            Log.Debug("InfoClientName(开票名称)-" + InfoClientName + ",InfoClientAddressPhone(开票地址，电话)-"
                + InfoClientAddressPhone + ",InfoTaxRate(税率)-" + InfoTaxRate + ",ListGoodsName(服务名称)-" 
                + ListGoodsName + ",ListAmount(金额)-" + ListAmount + ",ListPrice(单价)-" + ListPrice
                + ",ListUnit(单位)-" + ListUnit + ",ListNumber(数量)-" + ListNumber + ",InfoCashier(收款人)-" 
                + InfoCashier + ",InfoChecker(复核人)-" + InfoChecker + ",InfoNotes(备注)-" + InfoNotes);
            obj.InvInfoInit();
            //增值税普通发票
            obj.InfoKind = 2;
            //开票名称
            obj.InfoClientName = InfoClientName;
            //开票地址，电话
            obj.InfoClientAddressPhone = InfoClientAddressPhone;
            //税率
            obj.InfoTaxRate = InfoTaxRate;
            char[] c = new char[] { '|' };
            //服务名称
            string[] names = ListGoodsName.Split(c);
            //金额
            string[] amounts = ListAmount.Split(c);
            string[] prices = ListPrice.Split(c);
            //单位
            string[] units = ListUnit.Split(c);
            //数量
            string[] numbers = ListNumber.Split(c);
            obj.ClearInvList();
            for (int i = 0; i < names.Length; i++)
            {   
                //设置开票内容
                obj.InvListInit();
                //服务名称
                obj.ListGoodsName = names[i];
                double amount = double.Parse(amounts[i]) / (InfoTaxRate *0.01 + 1);
                obj.ListAmount = Math.Round(amount, 2);
                obj.ListTaxAmount = amount * InfoTaxRate * 0.01;
                obj.ListPrice = double.Parse(prices[i]);
                obj.ListUnit = units[i];
                obj.ListNumber = double.Parse(numbers[i]);
                obj.AddInvList();
            }
            //收款人
            obj.InfoCashier = InfoCashier;
            //复核人
            obj.InfoChecker = InfoChecker;
            //开票人
            obj.InfoInvoicer = InfoChecker;
            //备注
            obj.InfoNotes = InfoNotes;
            obj.Invoice();
            if (obj.RetCode != 4011)
            {
                Log.Debug("开具发票失败-" + obj.RetCode + obj.RegMsg);
                MessageBox.Show(obj.RetMsg);
            }
            else
            {

                Log.Debug("开具发票成功-开始打印");
                //打印
                obj.PrintInv();
                State = State.End;
                OnCompleted(null);
            }
        }

        /// <summary>
        /// 工作完成事件
        /// </summary>
        public event AsyncCompletedEventHandler Completed;
        public void OnCompleted(AsyncCompletedEventArgs args)
        {
            if (Completed != null)
            {
                Completed(this, args);
            }
        }
        /// <summary>
        /// 名称
        /// </summary>
        public string InfoClientName { get; set; }
        /// <summary>
        /// 电话地址
        /// </summary>
        public string InfoClientAddressPhone { get; set; }
        /// <summary>
        /// 收款人
        /// </summary>
        public string InfoCashier { get; set; }
        /// <summary>
        /// 复核人
        /// </summary>
        public string InfoChecker { get; set; }
        /// <summary>
        /// 税率，（5%传5）
        /// </summary>
        public int InfoTaxRate { get; set; }

        private string typecode;
        /// <summary>
        /// 十位发票代码
        /// </summary>
        public string InfoTypeCode 
        {
            get { return this.typecode; }
            set
            {
                this.typecode = value;
                OnPropertyChanged("InfoTypeCode");
            }
        }

        private int infonumber;
        /// <summary>
        /// 发票号码
        /// </summary>
        public int InfoNumber
        {
            get { return this.infonumber; }
            set
            {
                this.infonumber = value;
                OnPropertyChanged("InfoNumber");
            }
        }
        /// <summary>
        /// 服务名称，可以设置多个，用"|"分隔开
        /// </summary>
        public string ListGoodsName { get; set; }
        /// <summary>
        /// 金额（不含税），可以设置多个，用"|"分隔开
        /// </summary>
        public string ListAmount { get; set; }
        /// <summary>
        /// 单价，可以设置多个，用"|"分隔开
        /// </summary>
        public string ListPrice { get; set; }
        /// <summary>
        /// 单位，可以设置多个，用"|"分隔开
        /// </summary>
        public string ListUnit { get; set; }
        /// <summary>
        /// 数量，可以设置多个，用"|"分隔开
        /// </summary>
        public string ListNumber { get; set; }
        /// <summary>
        /// 备注信息
        /// </summary>
        public string InfoNotes { get; set; }

        #region State 卡状态
        public static readonly DependencyProperty StateProperty =
            DependencyProperty.Register("State", typeof(State), typeof(GoldTax), null);

        public State State
        {
            get { return (State)GetValue(StateProperty); }
            set
            {
                SetValue(StateProperty, value);
            }
        }
        #endregion
    }
}
