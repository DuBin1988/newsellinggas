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
        public 阶梯气价批量变更()
        {
            InitializeComponent();
            ui_SearchUser.DataContext = search;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (ui_SearchUserList.Count < 1 || CoboxStair.SelectedValue == null || "".Equals(CoboxStair.SelectedValue))
            {
                MessageBox.Show("请选择要修改用户和阶梯气价");
                return;
            }
            ui_searchBusy.IsBusy = true;
            search.Search();

            string sql = "update t_userfiles set f_stairtype='" + CoboxStair.SelectedValue + "', f_stair1amount=" + ui_stair1amount.Text + " , " +
                        "f_stair1price=" + ui_stair1price.Text + " , f_stair2amount='" + ui_stair2amount.Text + "', f_stair2price=" + ui_stair2price.Text + ",f_stair3amount='" + ui_stair3amount.Text + "', f_stair3price=" + ui_stair3price.Text + ", f_stair4price=" + ui_stair4price.Text + " ,f_stairmonths=" + ui_stairmonths.Text + " where " + search.Condition + "";
            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "t_userfiles";
            action.Completed += action_Completed;
            action.Invoke();

        }

        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
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
                if (ui_gaspricetype.SelectedValue != null)
                {
                    go.SetPropertyValue("f_gasmeterstyle", ui_gaspricetype.SelectedValue, false);
                }
                go.Name = "t_changestairprice";
                go.Completed += obj_Completed;
                go.Save();
            }
            else 
            {
                MessageBox.Show("请输入阶梯气价类型！");
                ui_searchBusy.IsBusy = false;
                return;
            }
        }

        private void obj_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_searchBusy.IsBusy = false;
        }
    }
}
