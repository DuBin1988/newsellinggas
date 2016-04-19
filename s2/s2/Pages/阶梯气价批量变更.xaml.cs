using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.Pages
{
    public partial class 阶梯气价批量变更 : UserControl
    {
        SearchObject search = new SearchObject();
        String f_userids = "";

        public 阶梯气价批量变更()
        {
            InitializeComponent();
            ui_SearchUser.DataContext = search;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if ((userfiles.ItemsSource as PagedList).Count < 1 || CoboxStair.SelectedValue == null || "".Equals(CoboxStair.SelectedValue))
            {
                MessageBox.Show("请选择要修改用户和阶梯气价");
                return;
            }
            ui_searchBusy.IsBusy = true;
            search.Search();
            if (!"1=1".Equals(search.Condition.Trim()) && null != search.Condition)
            {
                if (ui_stairmonths.Text != "" || ui_stair1amount.Text != "" || ui_stair1price.Text != "" || ui_stair2amount.Text != "" || ui_stair2price.Text != "" || ui_stair3amount.Text != "" || ui_stair3price.Text != "" || CoboxStair.SelectedValue != null)
                {
                    //提交更改
                    string sql = "update t_userinfo set f_stairtype='" + CoboxStair.SelectedValue + "', f_stair1amount=" + ui_stair1amount.Text + " , " +
                                    "f_stair1price=" + ui_stair1price.Text + " , f_stair2amount='" + ui_stair2amount.Text + "', f_stair2price=" + ui_stair2price.Text + ",f_stair3amount='" + ui_stair3amount.Text + "', f_stair3price=" + ui_stair3price.Text + ", f_stair4price=" + ui_stair4price.Text + " ,f_stairmonths=" + ui_stairmonths.Text + " where " + search.Condition + "";
                    HQLAction action = new HQLAction();
                    action.HQL = sql;
                    action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    action.Name = "t_userfiles";
                    action.Completed += action_Completed;
                    action.Invoke();
                }
            }
            else {
                MessageBox.Show("请先按照条件筛选出变更的用户，再确认变更！");
                ui_searchBusy.IsBusy = false;
            }

        }

        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_searchBusy.IsBusy = false;
            HQLAction action = sender as HQLAction;
            action.Completed -= action_Completed;

            if (CoboxStair.SelectedValue.ToString() != "")
            {
                GeneralObject go = new GeneralObject();
                go.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                go.EntityType = "t_changestairprice";
                go.SetPropertyValue("f_stairtype", CoboxStair.SelectedValue, false);
                go.SetPropertyValue("f_stair1amount", ui_stair1amount.Text, false);
                go.SetPropertyValue("f_stair2amount", ui_stair2amount.Text, false);
                go.SetPropertyValue("f_stair3amount", ui_stair3amount.Text, false);
                go.SetPropertyValue("f_stair1price", ui_stair1price.Text, false);
                go.SetPropertyValue("f_stair2price", ui_stair2price.Text, false);
                go.SetPropertyValue("f_stair3price", ui_stair3price.Text, false);
                go.SetPropertyValue("f_stair4price", ui_stair4price.Text, false);
                go.SetPropertyValue("f_stairmonths", ui_stairmonths.Text, false);
                go.SetPropertyValue("f_operator", ui_operator.Text, false);
                go.SetPropertyValue("f_operdate", ui_operdate.SelectedDate, false);
                go.SetPropertyValue("f_counts", ui_counts.Text, false);
                if (ui_usertype.SelectedValue != null)
                {
                    go.SetPropertyValue("f_usertype", ui_usertype.SelectedValue, false);
                }
                if (ui_gasmeterstyle.SelectedValue != null)
                {
                    go.SetPropertyValue("f_gasmeterstyle", ui_gasmeterstyle.SelectedValue, false);
                }
                go.Name = "t_changestairprice";
                go.Completed += obj_Completed;
                go.Save();

                string json = "[";
                if (ui_stairmonths.Text != "" || ui_stair1amount.Text != "" || ui_stair1price.Text != "" || ui_stair2amount.Text != "" || ui_stair2price.Text != "" || ui_stair3amount.Text != "" || ui_stair3price.Text != "" || CoboxStair.SelectedValue != null)
                {
                    //产生要发送后台的JSON串
                    json += ("{type:\"1\",price_type:\"" + CoboxStair.SelectedValue +
                                      "\",money1:\"" + ui_stair1price.Text +
                                      "\",limit1:\"" + ui_stair1amount.Text +
                                      "\",money2:\"" + ui_stair2price.Text +
                                      "\",limit2:\"" + ui_stair2amount.Text +
                                      "\",money3:\"" + ui_stair3price.Text +
                                      "\",limit3:\"" + ui_stair3amount.Text +
                                      "\",money4:\"" + ui_stair4price.Text +
                                      "\",limit4:\"999999999" +
                                      "\",money5:\"" + ui_stair4price.Text +
                                      "\",limit5:\"999999999" +
                                      "\",cycle:\"" + ui_stairmonths.Text +
                                      "\",idlist:[{search:\"" + search.Condition + "\"}]}");
                    json += "]";
                    //将产生的json串送后台服务进行处理
                    WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                    string uri = wci.BaseAddress + "/iesgas/gasdj/comand";
                    WebClient client = new WebClient();
                    client.UploadStringCompleted += client_UploadStringCompleted;
                    client.UploadStringAsync(new Uri(uri), json);
                }
                MessageBox.Show("阶梯变更完成！");

            }
            else
            {
                MessageBox.Show("请输入阶梯气价类型！");
                return;
            }
        }

        private void obj_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_searchBusy.IsBusy = false;
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_searchBusy.IsBusy = false;
            if (e.Error == null)
            {
                //弹出错误信息
                if (e.Result != "")
                {
                    // MessageBox.Show(e.Result);
                }

            }
        }
    }
}
