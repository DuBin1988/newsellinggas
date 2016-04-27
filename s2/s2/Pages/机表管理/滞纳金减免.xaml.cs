using Com.Aote.ObjectTools;
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
using System.Linq;
using Com.Aote.Utils;
using Com.Aote.Controls;
using System.Json;

namespace Com.Aote.Pages
{
	public partial class 滞纳金减免 : UserControl
	{
        public 滞纳金减免()
		{
			// Required to initialize variables
			InitializeComponent();
		}
         
      //选中新用户后的处理过程
        private void userfiles_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            //取当前选中项的用户编号，传递到后台取数据
            GeneralObject go = userfiles.SelectedItem as GeneralObject;
            if (go == null)
            {
                return;
            }
            kbsellgasbusy.IsBusy = true;
            busy.IsBusy = true;
            string f_userid = go.GetPropertyValue("f_userid").ToString();
            WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
            string uri = wci.BaseAddress + "/sell/bill/" + f_userid + "?uuid=" + System.Guid.NewGuid().ToString();
            WebClient client = new WebClient();
            client.DownloadStringCompleted += userfiles_DownloadStringCompleted;
            client.DownloadStringAsync(new Uri(uri));
        }

        void userfiles_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            kbsellgasbusy.IsBusy = false;
            busy.IsBusy = false;
            if (e.Error != null)
            {
                MessageBox.Show("未找到用户的表具信息，请去表具建档！");
                return;
            }
            //把数据转换成JSON
            JsonObject item = JsonValue.Parse(e.Result) as JsonObject;
            //把用户数据写到交费界面上
            ui_username.Text = (string)item["f_username"];
            ui_usertype.Text = (String)item["f_usertype"];
            ui_districtname.Text = (String)item["f_districtname"];
            ui_gasproperties.Text = (String)item["f_gasproperties"];
            ui_stairpricetype.Text = (String)item["f_stairtype"];
           // zhye.Text = item["f_zhye"].ToString();
            ui_address.Text = (String)item["f_address"];
            //ui_gaspricetype.Text = (String)item["f_gaspricetype"];
            ui_userid.Text = item["infoid"].ToString();
            //zhe.Text=item["f_zherownum"].ToString();
            //ui_dibaohu.IsChecked = item["f_dibaohu"].ToString().Equals("1");
            //ui_userstate.Text = (String)item["f_userstate"];
           // ui_paytype.Text = (String)item["f_payment"];
            // ui_gasprice.Text = item["f_gasprice"].ToString();

            //把欠费数据插入到欠费表中
            BaseObjectList list = dataGrid1.ItemsSource as BaseObjectList;
            if (list != null)
            {
                list.Clear();
            }

            // 当前正在处理的表号
            String currentId = "";
            // 总的上期指数
            decimal lastnum = 0;
            // 总气量
            decimal gasSum = 0;
            // 总气费
            decimal feeSum = 0;
            //总的滞纳金
            decimal zhinajinAll = 0;
            //余额
            decimal f_zhye = decimal.Parse(item["f_zhye"].ToString());
            JsonArray bills = item["f_hands"] as JsonArray;
            foreach (JsonObject json in bills)
            {
                GeneralObject go = new GeneralObject();
                go.EntityType = "t_handplan";

                //默认选中
                go.SetPropertyValue("IsChecked", true, false);

                //上期指数
                decimal lastinputgasnum = (decimal)json["lastinputgasnum"];
                go.SetPropertyValue("lastinputgasnum", lastinputgasnum, false);
                string f_userid = (string)json["f_userid"];
                go.SetPropertyValue("f_userid", f_userid, false);
                // 如果表号变了
                if (!f_userid.Equals(currentId))
                {
                    currentId = f_userid;
                    lastnum += lastinputgasnum;
                }

                //计算总金额
                decimal oughtfee = (decimal)json["oughtfee"];
                go.SetPropertyValue("oughtfee", oughtfee, false);
                feeSum += oughtfee;
                // 计算总气量
                decimal oughtamount = (decimal)json["oughtamount"];
                gasSum += oughtamount;
                go.SetPropertyValue("oughtamount", oughtamount, false);
                //计算总滞纳金
                decimal f_zhinajin = (decimal)json["f_zhinajin"];
                zhinajinAll += f_zhinajin;
                go.SetPropertyValue("f_zhinajin", f_zhinajin, false);
                int id = Int32.Parse(json["id"] + "");
                go.SetPropertyValue("id", id, false);

                go.SetPropertyValue("lastinputdate", DateTime.Parse(json["lastinputdate"]), false);
                go.SetPropertyValue("lastrecord", (decimal)json["lastrecord"], false);
                go.SetPropertyValue("f_endjfdate", DateTime.Parse(json["f_endjfdate"]), false);
                go.SetPropertyValue("f_zhinajintianshu", (int)json["days"], false);
                go.SetPropertyValue("f_network", (string)json["f_network"], false);
                go.SetPropertyValue("f_operator", (string)json["f_operator"], false);
                go.SetPropertyValue("f_inputdate", DateTime.Parse(json["f_inputdate"]), false);
                go.SetPropertyValue("f_userid", (string)json["f_userid"], false);

                go.SetPropertyValue("f_stair1amount", (decimal)json["f_stair1amount"], false);
                go.SetPropertyValue("f_stair1price", (decimal)json["f_stair1price"], false);
                go.SetPropertyValue("f_stair1fee", (decimal)json["f_stair1fee"], false);

                go.SetPropertyValue("f_stair2amount", (decimal)json["f_stair2amount"], false);
                go.SetPropertyValue("f_stair2price", (decimal)json["f_stair2price"], false);
                go.SetPropertyValue("f_stair2fee", (decimal)json["f_stair2fee"], false);

                go.SetPropertyValue("f_stair3amount", (decimal)json["f_stair3amount"], false);
                go.SetPropertyValue("f_stair3price", (decimal)json["f_stair3price"], false);
                go.SetPropertyValue("f_stair3fee", (decimal)json["f_stair3fee"], false);

                list.Add(go);
            }

           
            }
        }

    
}