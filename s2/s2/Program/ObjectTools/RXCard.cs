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
using System.IO;
using System.Text;

namespace Com.Aote.ObjectTools
{
    public class RXCard : CustomTypeHelper, IAsyncObject
    {
        /// <summary>
        /// 端口
        /// </summary>
        public int Com { get; set; }
        /// <summary>
        /// 波特率
        /// </summary>
        public int Baud { get; set; }
        /// <summary>
        /// 卡号
        /// </summary>
        public string KH { get; set; }
        /// <summary>
        /// 地区代码
        /// </summary>
        public string Dqdm { get; set; }
        //
        public string Tm { get; set; }
        /// <summary>
        /// 气量
        /// </summary>
        public int Ql { get; set; }
        /// <summary>
        /// 报警气量
        /// </summary>
        public int Bjql { get; set; }
        /// <summary>
        /// 卡类型
        /// </summary>
        public short Klx { get; set; }
        /// <summary>
        /// 卡状态
        /// </summary>
        public short Kzt { get; set; }
        /// <summary>
        /// 用户号
        /// </summary>
        public string Yhh { get; set; }
        /// <summary>
        /// 次数
        /// </summary>
        public int Cs { get; set; }
        /// <summary>
        /// 累计购气量
        /// </summary>
        public int Ljgql { get; set; }
        /// <summary>
        /// 表内累计用气量
        /// </summary>
        public int Ljyql { get; set; }
        /// <summary>
        /// 表内剩余气量
        /// </summary>
        public int Syql { get; set; }
        /// <summary>
        /// 卡上所有数据
        /// </summary>
        public string CardData { get; set; }

        public RXCard(bool flag)
        {
            this.Init = flag;
        }


        //荣鑫读卡控件
        dynamic obj;

        private bool init = false;
        public bool Init
        {
            get { return init; }
            set
            {
                this.init = value;
                if (init)
                {
                    obj = AutomationFactory.CreateObject("RXCOM.IMCOM");
                    //读取端口和波特率
                    StreamReader sr = new StreamReader("C:/gas.ini");
                    string s;
                    if ((s = sr.ReadLine()) != null)
                    {
                         char[] c = new char[] { ' ' };
                        string[] str = s.Split(c);
                        Com = Int32.Parse(str[0]);
                        Baud = Int32.Parse(str[1]);
                    }
                }
            }
        }

        //写卡
        public void WriteNewCard()
        {
            State = State.Start;
            IsBusy = true;
            int re = obj.WriteNewCard(Com,Baud,KH,Dqdm,Tm,Ql,Bjql);
            if (re == 0)
            {
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "写卡不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }

        //读卡
        public void ReadGasCard()
        {
            short klx;
            short kzt;
            string kh;
            string dqdm;
            string yhh;
            string tm;
            int ql;
            short cs;
            int ljgql;
            int ljyql;
            int syql;
            int bjql;
            State = State.Start;
            IsBusy = true;
            int re = obj.ReadGasCard(Com,Baud,out klx,out kzt,out kh,out dqdm,out yhh,out tm,out ql,out cs,out ljgql,out ljyql,out syql,out bjql);
            if (re == 0)
            {
                Klx = klx; Kzt = kzt; KH = kh; Dqdm = dqdm; Yhh = yhh; Tm = tm; Ql = ql; Cs = cs; Ljgql = ljgql; Ljyql = ljyql; Syql = syql; Bjql = bjql;
                State = State.Loaded;
            }
            else
            {
                State = State.LoadError;
                Error = "读卡不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }

        //售气
        public void WriteGasCard()
        {
            State = State.Start;
            IsBusy = true;
            int re = obj.WriteGasCard(Com,Baud,KH,Ql,Cs);
            if (re == 0)
            {
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "售气不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }

        //格式化卡
        public void FormatGasCard()
        {
            State = State.Start;
            IsBusy = true;
            int re = obj.FormatGasCard(Com,Baud,KH);
            if (re == 0)
            {
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "格式化卡不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }

        //判断卡
        public void CheckGasCard()
        {
            State = State.Start;
            IsBusy = true;
            int re = obj.CheckGasCard(Com,Baud);
            if (re == 0)
            {
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "判断卡不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }
       

        //读取所有卡上数据,保存到数据库，以供补卡使用
        public void ReadAllBytes()
        {
            string cardData;
            State = State.Start;
            IsBusy = true;
            int re = obj.ReadAllBytes(Com, Baud, out cardData);
            if (re == 0)
            {
                CardData = cardData;
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "读取卡所有数据不成功,错误代码:" + re;
            }
            IsBusy = false;
            OnCompleted(null);
        }

        //把上一次保存的卡全部信息，写到卡上，可以用于补卡
        public void RecoveryCard()
        {
            string pMSG;
            State = State.Start;
            IsBusy = true;
            int re = obj.RecoveryCard(Com, Baud, CardData,out pMSG);
            if (re == 0)
            {
                State = State.End;
            }
            else
            {
                State = State.Error;
                Error = "写卡所有数据不成功,错误数据:" + pMSG;
            }
            IsBusy = false;
            OnCompleted(null);
        }

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

        #region State 卡状态
        public static readonly DependencyProperty StateProperty =
            DependencyProperty.Register("State", typeof(State), typeof(RXCard), null);

        public State State
        {
            get { return (State)GetValue(StateProperty); }
            set
            {
                SetValue(StateProperty, value);
            }
        }
        #endregion

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


        public event System.ComponentModel.AsyncCompletedEventHandler Completed;

        public void OnCompleted(System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (Completed != null)
            {
                Completed(this, e);
            }
        }

        public string Name { get; set; }
    }
}
