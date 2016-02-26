using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Runtime.InteropServices.Automation;
using System.ComponentModel;
using System.Threading;
using System.Json;

namespace Com.Aote.ObjectTools
{
    //通用写卡对象
    public class NewGeneralICCard : CustomTypeHelper, IAsyncObject
    {
        #region 阶梯气价

        //阶梯单价1
        public string stairprice1;
        public string Stairprice1
        {
            get { return stairprice1; }
            set
            {
                this.stairprice1 = value;
                OnPropertyChanged("Stairprice1");
            }
        }

        //阶梯气量1 
        public string stairgas1;
        public string Stairgas1
        {
            get { return stairgas1; }
            set
            {
                this.stairgas1 = value;
                OnPropertyChanged("Stairgas1");
            }
        }
        //阶梯单价2
        public string stairprice2;
        public string Stairprice2
        {
            get { return stairprice2; }
            set
            {
                this.stairprice2 = value;
                OnPropertyChanged("Stairprice2");
            }
        }
        //阶梯气量2 
        public string stairgas2;
        public string Stairgas2
        {
            get { return stairgas2; }
            set
            {
                this.stairgas2 = value;
                OnPropertyChanged("Stairgas2");
            }
        }
        //阶梯单价3
        public string stairprice3;
        public string Stairprice3
        {
            get { return stairprice3; }
            set
            {
                this.stairprice3 = value;
                OnPropertyChanged("Stairprice3");
            }
        }

        //阶梯气量3
        public string stairgas3;
        public string Stairgas3
        {
            get { return stairgas3; }
            set
            {
                this.stairgas3 = value;
                OnPropertyChanged("Stairgas3");
            }
        }
        #endregion

        #region CardId 卡号
        //卡号
        private string cardid;
        public string CardId
        {
            get { return cardid; }
            set
            {
                this.cardid = value;
                OnPropertyChanged("CardId");
            }
        }
        #endregion

        #region MeterId 表号
        //单价生效标记 
        public string meterid;
        public string MeterId
        {
            get { return meterid; }
            set
            {
                this.meterid = value;
                OnPropertyChanged("MeterId");
            }
        }
        #endregion

        #region Gas 卡内气量
        //卡内气量
        private double gas;
        public double Gas
        {
            get { return gas; }
            set
            {
                this.gas = value;
                OnPropertyChanged("Gas");
            }
        }
        #endregion

        #region Scql 上次购气量
        //卡内气量
        private double scql;
        public double Scql
        {
            get { return scql; }
            set
            {
                this.scql = value;
                OnPropertyChanged("Scql");
            }
        }
        #endregion

        #region Sscql 上上次购气量
        //卡内气量
        private double sscql;
        public double Sscql
        {
            get { return sscql; }
            set
            {
                this.sscql = value;
                OnPropertyChanged("Sscql");
            }
        }
        #endregion

        #region BuyTimes 购气次数
        //购气次数
        private int buytimes;
        public int BuyTimes
        {
            get { return buytimes; }
            set
            {
                this.buytimes = value;
                OnPropertyChanged("BuyTimes");
            }
        }
        #endregion

        #region Factory 表厂
        //表厂
        public string factory;
        public string Factory
        {
            get { return factory; }
            set
            {
                this.factory = value;
                OnPropertyChanged("Factory");
            }
        }
        #endregion

        #region Klx 卡类型
        //卡类型
        public int klx;
        public int Klx
        {
            get { return klx; }
            set
            {
                this.klx = value;
                OnPropertyChanged("Klx");
            }
        }
        #endregion

        #region Kzt 卡状态
        //卡状态
        public int kzt;
        public int Kzt
        {
            get { return kzt; }
            set
            {
                this.kzt = value;
                OnPropertyChanged("Kzt");
            }
        }
        #endregion

        #region Dqdm 地区代码
        //地区代码
        public string dqdm;
        public string Dqdm
        {
            get { return dqdm; }
            set
            {
                this.dqdm = value;
                OnPropertyChanged("Dqdm");
            }
        }
        #endregion

        #region Yhh 用户号
        //用户号
        public string yhh;
        public string Yhh
        {
            get { return yhh; }
            set
            {
                this.yhh = value;
                OnPropertyChanged("Yhh");
            }
        }
        #endregion

        #region Tm 表条码
        //表条码
        public string tm;
        public string Tm
        {
            get { return tm; }
            set
            {
                this.tm = value;
                OnPropertyChanged("Tm");
            }
        }
        #endregion

        #region Ljgql 累计购气量
        //累计购气量
        public double ljgql;
        public double Ljgql
        {
            get { return ljgql; }
            set
            {
                this.ljgql = value;
                OnPropertyChanged("Ljgql");
            }
        }
        #endregion

        #region Bkcs 补卡次数
        //补卡次数
        public int bkcs;
        public int Bkcs
        {
            get { return bkcs; }
            set
            {
                this.bkcs = value;
                OnPropertyChanged("Bkcs");
            }
        }
        #endregion

        #region Ljyql 累计用气量
        //累计用气量
        public double ljyql;
        public double Ljyql
        {
            get { return ljyql; }
            set
            {
                this.ljyql = value;
                OnPropertyChanged("Ljyql");
            }
        }
        #endregion

        #region Syql 剩余气量
        //剩余气量
        public double syql;
        public double Syql
        {
            get { return syql; }
            set
            {
                this.syql = value;
                OnPropertyChanged("Syql");
            }
        }
        #endregion

        #region Bjql 报警气量
        //报警气量
        public int bjql;
        public int Bjql
        {
            get { return bjql; }
            set
            {
                this.bjql = value;
                OnPropertyChanged("Bjql");
            }
        }
        #endregion

        #region Czsx 充值上限
        //充值上限
        public int czsx;
        public int Czsx
        {
            get { return czsx; }
            set
            {
                this.czsx = value;
                OnPropertyChanged("Czsx");
            }
        }
        #endregion

        #region Tzed 透支额度
        //透支额度
        public int tzed;
        public int Tzed
        {
            get { return tzed; }
            set
            {
                this.tzed = value;
                OnPropertyChanged("Tzed");
            }
        }
        #endregion

        #region Kmm 卡密码
        //卡密码
        public string kmm;
        public string Kmm
        {
            get { return kmm; }
            set
            {
                this.kmm = value;
                OnPropertyChanged("Kmm");
            }
        }


        #endregion

        #region Sqrq 售气日期
        //售气日期 
        public string sqrq;
        public string Sqrq
        {
            get { return sqrq; }
            set
            {
                this.sqrq = value;
                OnPropertyChanged("Sqrq");
            }
        }
        #endregion

        #region Scsqrq 上次售气日期
        //售气日期 
        public string scsqrq;
        public string Scsqrq
        {
            get { return scsqrq; }
            set
            {
                this.scsqrq = value;
                OnPropertyChanged("Scsqrq");
            }
        }
        #endregion

        #region OldPrice 现在单价
        //现在单价
        public double oldprice;
        public double OldPrice
        {
            get { return oldprice; }
            set
            {
                this.oldprice = value;
                OnPropertyChanged("OldPrice");
            }
        }
        #endregion

        #region NewPrice 新单价
        //新单价
        public double newprice;
        public double NewPrice
        {
            get { return newprice; }
            set
            {
                this.newprice = value;
                OnPropertyChanged("NewPrice");
            }
        }
        #endregion

        #region Sxrq 单价生效日期
        //单价生效日期 
        public string sxrq;
        public string Sxrq
        {
            get { return sxrq; }
            set
            {
                this.sxrq = value;
                OnPropertyChanged("Sxrq");
            }
        }
        #endregion

        #region Sxbj 单价生效标记
        //单价生效标记 
        public string sxbj;
        public string Sxbj
        {
            get { return sxbj; }
            set
            {
                this.sxbj = value;
                OnPropertyChanged("Sxbj");
            }
        }
        #endregion

        #region Money 卡上的金额
        //单价生效标记 
        public double money;
        public double Money
        {
            get { return money; }
            set
            {
                this.money = value;
                OnPropertyChanged("Money");
            }
        }
        #endregion

        private bool init = false;
        public bool Init
        {
            get { return init; }
            set
            {
                this.init = value;
            }
        }

        #region CanInitCard 是否可发初始化卡
        //是否可发初始化卡
        private bool canInitCard = false;
        public bool CanInitCard
        {
            get { return canInitCard; }
            set
            {
                this.canInitCard = value;
                if (canInitCard)
                {
                    this.ReInitCard();
                    this.canInitCard = false;
                }
            }
        }
        #endregion

        #region CanSellGas 是否可以售气
        //是否可以售气，售气完成后，要自动转换成否
        private bool canSellGas = false;
        public bool CanSellGas
        {
            get { return canSellGas; }
            set
            {
                if (this.canSellGas == value)
                {
                    return;
                }
                this.canSellGas = value;
                if (this.canSellGas)
                {
                    SellGas();
                    this.canSellGas = false;
                }
            }
        }
        #endregion

        #region CanRewriteCard 是否可以擦卡后写初始化卡
        private bool canRewriteCard = false;
        public bool CanRewriteCard
        {
            get { return canRewriteCard; }
            set
            {
                if (this.canRewriteCard == value)
                {
                    return;
                }
                this.canRewriteCard = value;
                if (this.canRewriteCard)
                {
                    ReWriteCard();
                    this.canRewriteCard = false;
                }
            }
        }
        #endregion

        public NewGeneralICCard()
        {
        }

        #region ReadCard 读卡
        /// <summary>
        /// 读卡
        /// </summary>
        public void ReadCard()
        {
            IsBusy = true;
            Error = "";
            State = State.StartLoad;
            //卡上信息初始化
            CardId = "";
            Factory = "";
            Gas = 0;
            BuyTimes = 0;
            Klx = -1;
            Kzt = -1;
            Dqdm = "";
            Yhh = "";
            Tm = "";
            Ljgql = 0;
            Bkcs = 0;
            Ljyql = 0;
            Syql = 0;
            Bjql = 0;
            Czsx = 0;
            Tzed = 0;
            Sqrq = "";
            OldPrice = 0;
            NewPrice = 0;
            Sxbj = "";
            //读卡
            WebClient client = new WebClient();
            client.DownloadStringCompleted += new DownloadStringCompletedEventHandler(ReadCard_DownloadStringCompleted);
            client.DownloadStringAsync(new Uri("http://127.0.0.1:8000/ReadCard"));
        }

        void ReadCard_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            IsBusy = false;
            //通讯错误
            if (e.Error != null)
            {
                Error = "通讯错误：" + e.Error.Message;
                State = State.LoadError;
            }
            else
            {
                //更新数据
                JsonObject item = JsonValue.Parse(e.Result) as JsonObject;
                GeneralObject go = new GeneralObject();
                go.FromJson(item);
                string exception = (string)go.GetPropertyValue("Exception");
                string err = (string)go.GetPropertyValue("Err");
                //如果后台有异常
                if (exception != null)
                {
                    Error = "系统异常：" + exception;
                    State = State.LoadError;
                }
                //读卡错误
                else if (err != null)
                {
                    Error = "读卡错误：" + err;
                    State = State.LoadError;
                }
                else
                {
                    //获取卡上内容
                    Factory = (string)go.GetPropertyValue("Factory");
                    CardId = (string)go.GetPropertyValue("CardID");
                    Gas = double.Parse(go.GetPropertyValue("Gas").ToString());
                    Money = double.Parse(go.GetPropertyValue("Money").ToString());
                    BuyTimes = int.Parse(go.GetPropertyValue("Times").ToString());
                    Bkcs = int.Parse(go.GetPropertyValue("RenewTimes").ToString());
                    Dqdm = (string)go.GetPropertyValue("Dqdm");
                    State = State.Loaded;
                }
            }
            //通知读卡完成
            OnReadCompleted(null);
        }
        #endregion

        #region SellGas 售气 ,退气
        //气量大于0代表购气，气量等于0代表退气
        public void SellGas()
        {

            //通知写卡开始
            OnWriting(null);
            IsBusy = true;
            Error = "";
            State = State.Start;
            //调用写卡服务
            WebClient client = new WebClient();
            client.UploadStringCompleted += new UploadStringCompletedEventHandler(WriteCard_UploadStringCompleted);
            string p = "http://127.0.0.1:8001/WriteNewCard" +
                            "/" + Factory +     //厂家
                           "/" + Kmm +     //卡密码，写卡后返回新密码
                           "/" + CardId +          //卡号
                            "/" + Dqdm +        //地区代码，从气表管理里取
                            "/" + Gas +          //气量
                            "/" + Scql +         //上次购气量，有些表需要传
                            "/" + Sscql +        //上上次购气量，有些表需要传
                            "/" + BuyTimes +           //购气次数
                            "/" + Ljgql +        //当前表累计购气量
                            "/" + Bjql +         //报警气量
                            "/" + Czsx +         //充值上限，可以在气表管理中设置
                            "/" + Tzed +         //透支额度，可以在气表管理中设置
                            "/" + Sqrq +        //售气日期，格式为YYYYMMDD
                            "/" + Scsqrq +     //上次售气日期，格式为YYYYMMDD
                            "/" + OldPrice +     //旧单价，价格管理中取
                            "/" + NewPrice +     //新单价，价格管理中取
                            "/" + Sxrq +        //生效日期，价格管理中取
                            "/" + Sxbj;     //生效标记，0不生效，1生效，价格管理中取
            JsonObject jsonString = GetJsonString();
            client.UploadStringAsync(new Uri(p), jsonString.ToString());


        }
        #endregion

        #region ReInitCard 发初始化卡，或补卡
        //发初始化卡,或补卡 0：开户卡状态，1：用户卡状态。 卡状态
        public void ReInitCard()
        {
            //通知写卡开始
            OnWriting(null);
            IsBusy = true;
            Error = "";
            State = State.Start;
            //调用写卡服务
            WebClient client = new WebClient();
            client.UploadStringCompleted += new UploadStringCompletedEventHandler(WriteCard_UploadStringCompleted);
            string p = "http://127.0.0.1:8001/WriteNewCard" +
                                        "/" + Factory +     //厂家
                                        "/" + Kmm +     //卡密码，写卡后返回新密码
                                        "/" + Kzt +          //卡状态，0开户卡，1用户卡
                                        "/" + CardId +          //卡号
                                        "/" + Dqdm +        //地区代码，从气表管理里取
                                        "/" + Yhh +         //用户号，档案中自己输入
                                        "/" + Tm +          //条码，传用户档案里的条码
                                        "/" + Gas +          //气量
                                        "/" + Scql +         //上次购气量，有些表需要传
                                        "/" + Sscql +        //上上次购气量，有些表需要传
                                        "/" + BuyTimes +           //购气次数
                                        "/" + Ljgql +        //当前表累计购气量
                                        "/" + Bkcs +         //补卡次数，用户档案里保存补卡次数 改为卡类型
                                        "/" + Ljyql +        //累计用气量，有些表要累加原来用气量
                                        "/" + Bjql +         //报警气量
                                        "/" + Czsx +         //充值上限，可以在气表管理中设置
                                        "/" + Tzed +         //透支额度，可以在气表管理中设置
                                        "/" + Sqrq +        //售气日期，格式为YYYYMMDD
                                        "/" + Scsqrq +     //上次售气日期，格式为YYYYMMDD
                                        "/" + OldPrice +     //旧单价，价格管理中取
                                        "/" + NewPrice +     //新单价，价格管理中取
                                        "/" + Sxrq +        //生效日期，价格管理中取
                                        "/" + Sxbj +     //生效标记，0不生效，1生效，价格管理中取 
                                        "/" + Klx +       //卡类型
                                        "/" + MeterId;  //表号
            JsonObject jsonString = GetJsonString();
            client.UploadStringAsync(new Uri(p), jsonString.ToString());
        }
        #endregion

        #region ReWriteCard 重新初始化，直接调用初始化方法
        public void ReWriteCard()
        {
            ReInitCard();
        }
        #endregion

        #region MakeNewCard 格式化卡
        //格式化卡
        public void MakeNewCard()
        {
            IsBusy = true;
            Error = "";
            State = State.Start;
            //执行写卡线程
            WebClient client = new WebClient();
            client.UploadStringCompleted += new UploadStringCompletedEventHandler(WriteCard_UploadStringCompleted);
            string p = "http://127.0.0.1:8001/WriteNewCard" +
                            "/" + Factory +     //厂家
                           "/" + Kmm +     //卡密码，写卡后返回新密码
                           "/" + CardId +          //卡号
                            "/" + Dqdm;        //地区代码，从气表管理里取

            JsonObject jsonString = GetJsonString();
            client.UploadStringAsync(new Uri(p), jsonString.ToString());
        }
        #endregion

        #region 累计金额
        //累计金额
        public string totalmoney;
        public string TotalMoney
        {
            get { return totalmoney; }
            set
            {
                this.totalmoney = value;
                OnPropertyChanged("TotalMoney");
            }
        }
        #endregion


        //把阶梯气价等属性组织成json串
        private JsonObject GetJsonString()
        {
            JsonObject obj = new JsonObject();
            obj.Add("stairprice1", Stairprice1);
            obj.Add("stairgas1", Stairgas1);
            obj.Add("stairprice2", Stairprice2);
            obj.Add("stairgas2", Stairgas2);
            obj.Add("stairprice3", Stairprice3);
            obj.Add("stairgas3", Stairgas3);
            obj.Add("money", Money);
            obj.Add("totalmoney", TotalMoney);
            return obj;
        }

        //所有写卡结束后的统一处理过程
        void WriteCard_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            (sender as WebClient).UploadStringCompleted -= WriteCard_UploadStringCompleted;

            IsBusy = false;
            //通讯错误
            if (e.Error != null)
            {
                Error = "通讯错误：" + e.Error.Message;
                State = State.LoadError;
            }
            else
            {
                //更新数据
                JsonObject item = JsonValue.Parse(e.Result) as JsonObject;
                GeneralObject go = new GeneralObject();
                go.FromJson(item);
                string exception = (string)go.GetPropertyValue("Exception");
                string err = (string)go.GetPropertyValue("Err");
                //如果后台有异常
                if (exception != null)
                {
                    Error = "系统异常：" + exception;
                    State = State.LoadError;
                }
                //写卡错误
                else if (err != null)
                {
                    Error = "写卡错误：" + err;
                    State = State.LoadError;
                }
                else
                {
                    Kmm = (string)go.GetPropertyValue("Kmm");
                    State = State.End;
                }
            }
            OnCompleted(null);
        }

        public string Name { get; set; }

        #region Error 卡错误
        public string error = "";
        public string Error
        {
            get { return error; }
            set
            {
                error = value;
                OnPropertyChanged("Error");
            }
        }
        #endregion

        #region State 卡状态
        public State state = State.Free;
        public State State
        {
            get { return state; }
            set
            {
                state = value;
                OnPropertyChanged("State");
            }
        }

        //public static readonly DependencyProperty StateProperty =
        //    DependencyProperty.Register("State", typeof(State), typeof(NewGeneralICCard), null);

        //public State State
        //{
        //    get { return (State)GetValue(StateProperty); }
        //    set
        //    {
        //        SetValue(StateProperty, value);
        //    }
        //}
        #endregion

        #region Writing 写卡前事件，在写卡之前触发
        public event System.ComponentModel.AsyncCompletedEventHandler Writing;
        public void OnWriting(System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (Writing != null)
            {
                Writing(this, e);
            }
        }
        #endregion

        #region ReadCompleted 读卡完成事件
        public event System.ComponentModel.AsyncCompletedEventHandler ReadCompleted;
        public void OnReadCompleted(System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (ReadCompleted != null)
            {
                ReadCompleted(this, e);
            }
        }
        #endregion

        #region Completed 写卡完成事件
        public event System.ComponentModel.AsyncCompletedEventHandler Completed;
        public void OnCompleted(System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (Completed != null)
            {
                Completed(this, e);
            }
        }
        #endregion

        #region IsBusy 是否忙
        public bool isBusy = false;
        public bool IsBusy
        {
            get { return isBusy; }
            set
            {
                isBusy = value;
                OnPropertyChanged("IsBusy");
            }
        }
        #endregion
    }

}
