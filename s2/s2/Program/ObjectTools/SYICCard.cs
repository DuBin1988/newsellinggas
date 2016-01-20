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
using System.Threading;

namespace Com.Aote.ObjectTools
{
    public class SYICCard:GeneralICCard
    {
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
                }
            }
        }
        #endregion

        #region CanSellGas 是否可以售气
        public static readonly DependencyProperty CanSellGasProperty =
            DependencyProperty.Register("CanSellGas", typeof(bool), typeof(SYICCard),
            new PropertyMetadata(new PropertyChangedCallback(OnCanSellGasChanged)));
        public bool CanSellGas
        {
            get { return (bool)GetValue(CanSellGasProperty); }
            set { SetValue(CanSellGasProperty, value); }
        }

        //如果可以售气，调用售气过程
        private static void OnCanSellGasChanged(DependencyObject dp, DependencyPropertyChangedEventArgs e)
        {
            SYICCard card = (SYICCard)dp;
            if (card.CanSellGas)
            {
                card.SellGas();
            }
        }
        #endregion

        #region CanRewriteCard 是否可以擦卡后写初始化卡
        public static readonly DependencyProperty CanRewriteCardProperty =
            DependencyProperty.Register("CanRewriteCard", typeof(bool), typeof(SYICCard),
            new PropertyMetadata(new PropertyChangedCallback(OnCanRewriteCardChanged)));
        public bool CanRewriteCard
        {
            get { return (bool)GetValue(CanRewriteCardProperty); }
            set { SetValue(CanRewriteCardProperty, value); }
        }

        private static void OnCanRewriteCardChanged(DependencyObject dp, DependencyPropertyChangedEventArgs e)
        {
            SYICCard card = (SYICCard)dp;
            if (card.CanRewriteCard)
            {
                card.ReWriteCard();
            }
        }
        #endregion
        //读卡
        public void ReadCard()
        {
            this.IsBusy = true;
            RXCard r = new RXCard(true);
            r.CheckGasCard();
            if (r.State==State.End)
            {
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
                r.ReadGasCard();
                if (r.State == State.Loaded)
                {
                    this.Klx = r.Klx;
                    this.Kzt = r.Kzt;
                    this.CardId = r.KH;
                    this.Dqdm = r.Dqdm;
                    this.Yhh = r.Yhh;
                    this.Tm = r.Tm;
                    this.Gas = r.Ql;
                    this.BuyTimes = r.Cs;
                    this.Ljgql = r.Ljgql;
                    this.Ljyql = r.Ljyql;
                    this.Syql = r.Syql;
                    this.Bjql = r.Bjql;
                    this.State = r.State;
                    this.Factory = "RXIC";
                }
                else
                {
                    this.State = r.State;
                    this.Error = r.Error;
                }
                OnReadCompleted(null);
            }
            else
            {
                base.ReadCard();
            }
            this.IsBusy = false;
        }

        //售气
        public void SellGas()
        {
            this.IsBusy = true;
            if (Factory == "RXIC")
            {
                RXCard r = new RXCard(true);
                r.KH = CardId;
                r.Ql = Int32.Parse(Gas+"");
                r.Cs = BuyTimes;
                r.WriteGasCard();
                State = r.State;
                Error = r.Error;
            }
            else
            {
                base.SellGas();
            }
            this.IsBusy = false;
        }

        //发卡
        public void ReInitCard()
        {
            this.IsBusy = true;
            if (Factory == "RXIC")
            {
                RXCard r = new RXCard(true);
                    r.KH = CardId;
                    r.Dqdm = Dqdm;
                    r.Tm = Tm;
                    r.Ql = Int32.Parse(Gas + "");
                    r.Bjql = Bjql;
                    r.WriteNewCard();
                    State = r.State;
                    Error = r.Error;
            }
            else
            {
                base.ReInitCard();
            }
            this.IsBusy = false;
        }

        //格式化卡
        public void MakeNewCard()
        {
            this.IsBusy = true;
            if (Factory == "RXIC")
            {
                RXCard r = new RXCard(true);
                r.KH = CardId;
                r.FormatGasCard();
                State = r.State;
                Error = r.Error;
            }
            else
            {
                base.MakeNewCard();
            }
            this.IsBusy = false;
        }

        //荣鑫卡读取所有卡上数据,保存到数据库，以供补卡使用
        #region CardData 卡上数据
        //购气次数
        private string carddata;
        public string CardData
        {
            get { return carddata; }
            set
            {
                this.carddata = value;
                OnPropertyChanged("CardData");
            }
        }
        #endregion
        public void ReadAllBytes()
        {
            if (Factory == "RXIC")
            {
                RXCard r = new RXCard(true);
                r.ReadAllBytes();
                CardData = r.CardData;
                State = r.State;
                Error = r.Error;
            }
        }

        //荣鑫卡，把上一次保存的卡全部信息，写到卡上，可以用于补卡
        public void RecoveryCard()
        {
            if (Factory == "RXIC")
            {
                RXCard r = new RXCard(true);
                r.CardData = CardData;
                r.RecoveryCard();
                State = r.State;
                Error = r.Error;
            }
        }
    }
}
