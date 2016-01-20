using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;
using System.Linq;
using System.Net;

namespace Com.Aote.Pages
{
	public partial class 售气收费 : UserControl
	{
        PagedList listwh = new PagedList();
		public 售气收费()
		{
			InitializeComponent();
		}

        private void NewGeneralICCard_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {

            busy.IsBusy = true;
            NewGeneralICCard card = (from p in loader.Res where p.Name.Equals("card") select p).First() as NewGeneralICCard;
            if (card.State == State.LoadError)
            {
                //WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                //string str = wci.BaseAddress + "returns" + f_userid.Text;
                //Uri uri = new Uri(str);
               // WebClient client = new WebClient();
             //   client.DownloadStringCompleted += client_DownloadStringCompleted;
                //client.DownloadStringAsync(uri);
                MessageBox.Show("写卡失败,请读卡核对气量!");
            }
            else
            {
                print.Print();
             
            }
            busy.IsBusy = false;     
        }
        private void ui_pregas_LostFocus(object sender, RoutedEventArgs e)
        {
            string userid = f_userid.Text;
            if (userid.Equals(""))
            {
                return;
            }
            string sql = "select f_userid,f_pregas,f_deliverydate,f_usertype from t_sellinggas where f_userid = '" + userid + "'";
            listwh.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            listwh.LoadOnPathChanged = false;
            listwh.Path = "sql";
            listwh.HQL = sql;
            listwh.PageSize = 1;
            listwh.DataLoaded += listwh_DataLoaded;
            listwh.PageIndex = 0;
        }

        private void listwh_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            //产生现在月份
            Int32 thisMonth = Int32.Parse(ui_deliverydate.Text.Substring(5, 2));
            GeneralObject kbfee = kbfee1.DataContext as GeneralObject;
            GeneralObject ljql1 = (GeneralObject)(from r in loader.Res where r.Name.Equals("ljql1") select r).First();
            GeneralObject ljql2 = (GeneralObject)(from r in loader.Res where r.Name.Equals("ljql2") select r).First();
            GeneralObject dbhjtqj1 = (GeneralObject)(from r in loader.Res where r.Name.Equals("dbhjtqj1") select r).First();
            GeneralObject dbhjtqj2 = (GeneralObject)(from r in loader.Res where r.Name.Equals("dbhjtqj2") select r).First();
            GeneralObject dbhjtqj3 = (GeneralObject)(from r in loader.Res where r.Name.Equals("dbhjtqj3") select r).First();
            GeneralObject jtqj1 = (GeneralObject)(from r in loader.Res where r.Name.Equals("jtqj1") select r).First();
            GeneralObject jtqj2 = (GeneralObject)(from r in loader.Res where r.Name.Equals("jtqj2") select r).First();
            GeneralObject jtqj3 = (GeneralObject)(from r in loader.Res where r.Name.Equals("jtqj3") select r).First();
            GeneralObject fmyljql1 = (GeneralObject)(from r in loader.Res where r.Name.Equals("fmyljql1") select r).First();
            GeneralObject fmyljql2 = (GeneralObject)(from r in loader.Res where r.Name.Equals("fmyljql2") select r).First();
            GeneralObject fmyjtqj1 = (GeneralObject)(from r in loader.Res where r.Name.Equals("fmyjtqj1") select r).First();
            GeneralObject fmyjtqj2 = (GeneralObject)(from r in loader.Res where r.Name.Equals("fmyjtqj2") select r).First();
            GeneralObject fmyjtqj3 = (GeneralObject)(from r in loader.Res where r.Name.Equals("fmyjtqj3") select r).First();
            GeneralObject xiangys = (GeneralObject)(from r in loader.Res where r.Name.Equals("xiangys") select r).First();
            //临界气量1
            double cs_ljql1 = double.Parse(ljql1.GetPropertyValue("value").ToString());
            //临界气量2
            double cs_ljql2 = double.Parse(ljql2.GetPropertyValue("value").ToString());
            //低保户阶梯气价1
            double cs_dbhjtqj1 = double.Parse(dbhjtqj1.GetPropertyValue("value").ToString());
            //低保户阶梯气价2
            double cs_dbhjtqj2 = double.Parse(dbhjtqj2.GetPropertyValue("value").ToString());
            //低保户阶梯气价3
            double cs_dbhjtqj3 = double.Parse(dbhjtqj3.GetPropertyValue("value").ToString());
            //阶梯气价1
            double cs_jtqj1 = double.Parse(jtqj1.GetPropertyValue("value").ToString());
            //阶梯气价2
            double cs_jtqj2 = double.Parse(jtqj2.GetPropertyValue("value").ToString());
            //阶梯气价3
            double cs_jtqj3 = double.Parse(jtqj3.GetPropertyValue("value").ToString());
            //非民用临界气量1
            double cs_fmyljql1 = double.Parse(fmyljql1.GetPropertyValue("value").ToString());
            //非民用临界气量2
            double cs_fmyljql2 = double.Parse(fmyljql2.GetPropertyValue("value").ToString());
            //非民用阶梯气价1
            double cs_fmyjtqj1 = double.Parse(fmyjtqj1.GetPropertyValue("value").ToString());
            //非民用阶梯气价2
            double cs_fmyjtqj2 = double.Parse(fmyjtqj2.GetPropertyValue("value").ToString());
            //非民用阶梯气价3
            double cs_fmyjtqj3 = double.Parse(fmyjtqj3.GetPropertyValue("value").ToString());
            //限购月数
            double cs_xiangys = double.Parse(xiangys.GetPropertyValue("value").ToString());
            //将预购气量转换成为double
            string pgas = ui_pregas.Text.ToString();
            double pregas = 0;

            if (pgas != null && !pgas.Equals(""))
            {
                pregas = double.Parse(ui_pregas.Text.ToString());

            }
            //周期起始月
            double zqqsy = Math.Round((thisMonth / cs_xiangys), 0) * cs_xiangys + 1;
            //周期截止月
            double zqjzy = Math.Round((thisMonth / cs_xiangys), 0) * cs_xiangys + cs_xiangys;
            // 民用
            if (kbfee.GetPropertyValue("f_usertype").ToString().Equals("民用"))
            {
                //定义本阶梯够气量
                double bjtgql = 0;
                //IEnumerable<int> scoreQuery =from score in list where thisMonth==getMonth select score;
                foreach (GeneralObject go in listwh)
                {
                    //取出交费日期
                    var f_deliverydate = go.GetPropertyValue("f_deliverydate");
                    //取出每一条数据的交费月份
                    Int32 getMonth = Int32.Parse(f_deliverydate.ToString().Substring(5, 2));
                    if (getMonth >= zqqsy && getMonth <= zqjzy)
                    {
                        if (go.GetPropertyValue("f_pregas").Equals(""))
                        {
                            bjtgql = 0;
                        }
                        else
                        {
                            bjtgql += double.Parse(go.GetPropertyValue("f_pregas").ToString());
                        }
                        MessageBox.Show("bjtgql是" + bjtgql);
                    }
                    else
                    {
                        bjtgql = 0;
                    }
                }
                //如果不是低保户
                if (!kbfee.GetPropertyValue("f_dibaohu").ToString().Equals("0"))
                {
                    //阶梯气价1
                    if (bjtgql + pregas <= cs_ljql1)
                    {
                        ui_preamount.Text = (cs_jtqj1 * (bjtgql + pregas - bjtgql)).ToString();
                    }
                    //阶梯气价2
                    else if (bjtgql + pregas <= cs_ljql2)
                    {
                        ui_preamount.Text = ((cs_jtqj2 * (bjtgql + pregas - cs_ljql1)) + cs_jtqj1 * (cs_ljql2 - (bjtgql + cs_ljql1))).ToString();
                    }
                    //阶梯气价3
                    else
                    {
                        ui_preamount.Text = ((cs_jtqj3 * (bjtgql + pregas - cs_ljql2)) + cs_jtqj2 * (bjtgql + cs_ljql2)).ToString();
                    }
                }
                //是低保户
                else
                {
                    //低保户阶梯气价1
                    if (bjtgql + pregas <= cs_ljql1)
                    {
                        ui_preamount.Text = (cs_dbhjtqj1 * (bjtgql + pregas - bjtgql)).ToString();
                    }
                    //低保户阶梯气价2
                    else if (bjtgql + pregas <= cs_ljql2)
                    {
                        ui_preamount.Text = ((cs_dbhjtqj2 * (bjtgql + pregas - cs_ljql1)) + cs_dbhjtqj1 * (cs_ljql2 - (bjtgql + pregas))).ToString();
                    }
                    //低保户阶梯气价3
                    else
                    {
                        ui_preamount.Text = ((cs_dbhjtqj3 * (bjtgql + pregas - cs_ljql1)) + cs_dbhjtqj2 * (cs_ljql2 - (bjtgql + pregas))).ToString();
                    }
                }
            }
            else   // 非民用
            {
                //定义本阶梯够气量
                double fmybjtgql = 0;
                //IEnumerable<int> scoreQuery =from score in list where thisMonth==getMonth select score;
                foreach (GeneralObject go in listwh)
                {
                    //取出交费日期
                    var f_deliverydate = go.GetPropertyValue("f_deliverydate");
                    //取出每一条数据的交费月份
                    Int32 getMonth = Int32.Parse(f_deliverydate.ToString().Substring(5, 2));
                    if (getMonth >= zqqsy && getMonth <= zqjzy)
                    {
                        if (go.GetPropertyValue("f_pregas").Equals(""))
                        {
                            fmybjtgql = 0;
                        }
                        else
                        {
                            fmybjtgql += double.Parse(go.GetPropertyValue("f_pregas").ToString());
                        }
                        MessageBox.Show("fmybjtgql是" + fmybjtgql);
                    }
                    else
                    {
                        fmybjtgql = 0;
                    }
                    //非民用阶梯气价1
                    if (fmybjtgql + pregas <= cs_fmyljql1)
                    {
                        ui_preamount.Text = (cs_fmyjtqj1 * (fmybjtgql + pregas - fmybjtgql)).ToString();
                    }
                    //非民用阶梯气价2
                    else if (fmybjtgql + pregas <= cs_fmyljql2)
                    {
                        ui_preamount.Text = ((cs_fmyjtqj2 * (fmybjtgql + pregas - cs_fmyljql1)) + cs_jtqj1 * (cs_fmyljql2 - (fmybjtgql + pregas))).ToString();
                    }
                    //非民用阶梯气价3
                    else
                    {
                        ui_preamount.Text = ((cs_fmyjtqj3 * (fmybjtgql + pregas - cs_fmyljql1)) + cs_jtqj2 * (cs_fmyljql2 - (fmybjtgql + pregas))).ToString();
                    }
                }
            }

        }
 
          
    }
}