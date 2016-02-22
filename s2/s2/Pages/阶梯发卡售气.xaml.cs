using Com.Aote.ObjectTools;
using System;
using System.Json;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using Com.Aote.Utils;
using Com.Aote.Behaviors;

namespace Com.Aote.Pages
{
    public partial class 阶梯发卡售气 : UserControl
    {
        public 阶梯发卡售气()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        string userid = "";
        private void ui_pregas_LostFocus(object sender, RoutedEventArgs e)
        {

            ui_chargeBusy.IsBusy = true;
            string userid = f_userid.Text;
            string pregas = ui_pregas.Text;
            string usertype = f_usertype.Text;
            if (userid.Equals(""))
            {
                MessageBox.Show("请先读卡！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            else if (pregas.Equals(""))
            {
                MessageBox.Show("请输入预购气量！");
                ui_chargeBusy.IsBusy = false;
                return;
            }
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["priceserver"];
            string str = wci.BaseAddress + "/num/" + "1" + "/" + userid + "/" + pregas + "/" + usertype + "?uuid=" + System.Guid.NewGuid().ToString();
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);

        }

        private void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            ui_chargeBusy.IsBusy = false;
            if (e.Error == null)
            {
                JsonObject items = JsonValue.Parse(e.Result) as JsonObject;
                ui_stair1amont.Text = items["f_stair1amount"].ToString();
                ui_stair2amont.Text = items["f_stair2amount"].ToString();
                ui_stair3amont.Text = items["f_stair3amount"].ToString();
                ui_stair4amont.Text = items["f_stair4amount"].ToString();
                ui_stair1fee.Text = items["f_stair1fee"].ToString();
                ui_stair2fee.Text = items["f_stair2fee"].ToString();
                ui_stair3fee.Text = items["f_stair3fee"].ToString();
                ui_stair4fee.Text = items["f_stair4fee"].ToString();
                ui_stair1price.Text = items["f_stair1price"].ToString();
                ui_stair2price.Text = items["f_stair2price"].ToString();
                ui_stair3price.Text = items["f_stair3price"].ToString();
                ui_stair4price.Text = items["f_stair4price"].ToString();
                ui_allamont.Text = items["f_allamont"].ToString();
                ui_OrdinaryNum.Text = items["f_allamont"].ToString();
                if (items["f_stardate"] == null)
                {
                    items["f_stardate"] = "2050-12-12";
                }
                if (items["f_enddate"] == null)
                {
                    items["f_enddate"] = "2050-12-12";
                }
                ui_stardate.Text = items["f_stardate"].ToString().Substring(1, 10);
                ui_enddate.Text = items["f_enddate"].ToString().Substring(1, 10);
                ui_grossproceeds.Text = items["f_totalcost"].ToString();
                ui_preamount.Text = Math.Round(double.Parse(items["f_chargenum"].ToString()), 2).ToString();
                ui_totalcost.Text = Math.Round(double.Parse(items["f_totalcost"].ToString()), 2).ToString();

                //给收费对象赋值

                GeneralObject loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");
                if (loginUser == null)
                {
                    MessageBox.Show("无法获取当前登陆用户信息,请重新登陆后操作!");
                    return;
                }
                string orgpathstr = loginUser.GetPropertyValue("orgpathstr").ToString();
                GeneralObject go = (GeneralObject)(from r in loader.Res where r.Name.Equals("sellgasobj") select r).First();
                go.SetPropertyValue("f_OrgStr", orgpathstr, false);
                go.SetPropertyValue("f_stair1amount", items["f_stair1amount"].ToString(), false);
                go.SetPropertyValue("f_stair2amount", items["f_stair2amount"].ToString(), false);
                go.SetPropertyValue("f_stair3amount", items["f_stair3amount"].ToString(), false);
                go.SetPropertyValue("f_stair4amount", items["f_stair4amount"].ToString(), false);
                go.SetPropertyValue("f_stair1fee", items["f_stair1fee"].ToString(), false);
                go.SetPropertyValue("f_stair2fee", items["f_stair2fee"].ToString(), false);
                go.SetPropertyValue("f_stair3fee", items["f_stair3fee"].ToString(), false);
                go.SetPropertyValue("f_stair4fee", items["f_stair4fee"].ToString(), false);
                go.SetPropertyValue("f_stair1price", items["f_stair1price"].ToString(), false);
                go.SetPropertyValue("f_stair2price", items["f_stair2price"].ToString(), false);
                go.SetPropertyValue("f_stair3price", items["f_stair3price"].ToString(), false);
                go.SetPropertyValue("f_stair4price", items["f_stair4price"].ToString(), false);
                go.SetPropertyValue("f_totalcost", Math.Round(double.Parse(items["f_totalcost"].ToString()), 2).ToString(), false);
                go.SetPropertyValue("f_preamount", Math.Round(double.Parse(items["f_chargenum"].ToString()), 2).ToString(), false);
                go.SetPropertyValue("f_grossproceeds", items["f_totalcost"].ToString(), false);

                //给发卡对象赋值userfilego
                GeneralObject faka = (GeneralObject)(from r in loader.Res where r.Name.Equals("userfilego") select r).First();
                faka.SetPropertyValue("f_OrgStr", orgpathstr, false);
                faka.SetPropertyValue("f_stair1amount", items["f_stair1amount"].ToString(), false);
                faka.SetPropertyValue("f_stair2amount", items["f_stair2amount"].ToString(), false);
                faka.SetPropertyValue("f_stair3amount", items["f_stair3amount"].ToString(), false);
                faka.SetPropertyValue("f_stair4amount", items["f_stair4amount"].ToString(), false);
                faka.SetPropertyValue("f_stair1fee", items["f_stair1fee"].ToString(), false);
                faka.SetPropertyValue("f_stair2fee", items["f_stair2fee"].ToString(), false);
                faka.SetPropertyValue("f_stair3fee", items["f_stair3fee"].ToString(), false);
                faka.SetPropertyValue("f_stair4fee", items["f_stair4fee"].ToString(), false);
                faka.SetPropertyValue("f_stair1price", items["f_stair1price"].ToString(), false);
                faka.SetPropertyValue("f_stair2price", items["f_stair2price"].ToString(), false);
                faka.SetPropertyValue("f_stair3price", items["f_stair3price"].ToString(), false);
                faka.SetPropertyValue("f_stair4price", items["f_stair4price"].ToString(), false);
                faka.SetPropertyValue("f_totalcost", Math.Round(double.Parse(items["f_totalcost"].ToString()), 2).ToString(), false);
                faka.SetPropertyValue("f_preamount", Math.Round(double.Parse(items["f_chargenum"].ToString()), 2).ToString(), false);
                faka.SetPropertyValue("f_grossproceeds", items["f_totalcost"].ToString(), false);
            }
            else
            {
                ui_chargeBusy.IsBusy = false;
                MessageBox.Show(e.Error.Message);
            }
        }
        private void DisplayPopup(object sender, RoutedEventArgs e)
        {
            if (myPopup.IsOpen == false)
            {
                myPopup.IsOpen = true;
            }
            else
            {
                myPopup.IsOpen = false;
            }
        }



        private void cardid1_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {

            BatchExcuteAction saves = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            GeneralObject go = (GeneralObject)(from r in loader.Res where r.Name.Equals("sellgasobj") select r).First();
            GeneralObject faka = (GeneralObject)(from r in loader.Res where r.Name.Equals("userfilego") select r).First();
            if (go.GetPropertyValue("f_preamount").ToString() == null || go.GetPropertyValue("f_preamount").ToString() == "" ||
                go.GetPropertyValue("f_grossproceeds").ToString() == null || go.GetPropertyValue("f_grossproceeds").ToString() == "" ||
                go.GetPropertyValue("f_totalcost").ToString() == null || go.GetPropertyValue("f_totalcost").ToString() == "" ||
                go.GetPropertyValue("f_cardid").ToString() == null || go.GetPropertyValue("f_cardid").ToString() == "")
            {
                MessageBox.Show("无法获取到阶梯信息，请重新操作!");
                return;
            }
            else
            {
                //saves.Completed += save_Completed;
                saves.Invoke();

                //CanSave="{m:Exp Str='cardid1.State \=\= $Loaded$ and sellgasobj.f_cardid !\= null and userfiles1.f_cardid !\=null and userfilego.f_cardidjia !\=null and sellgasobj.f_cardid !\= $$ and userfiles1.f_cardid !\=$$'}"
            }

        }

        //void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        //{
        //    BatchExcuteAction saves = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
        //    saves.State = State.End;
        //}
    }
}