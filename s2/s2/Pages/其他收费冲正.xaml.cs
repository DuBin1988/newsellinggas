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
    public partial class 其他收费冲正 : UserControl
    {
        // search对象
        SearchObject ortherSearch = new SearchObject();

        // 选中的表格对象
        PagedList ortherList = new PagedList();
        public 其他收费冲正()
        {
            InitializeComponent();

            // 给界面上的search部分，设置数据
            pipelinesearch.DataContext = ortherSearch;

            // 给两个表格对象挂接数据
            pipelines.ItemsSource = ortherList;
            ortherList.DataLoaded += ortherList_DataLoaded;
        }
        int pageIndex = 0;
        private void searchbutton_Click(object sender, RoutedEventArgs e)
        {
            ui_userBusy.IsBusy = true;
            ortherSearch.Search();

            // 生成sql语句
            string sql = "select * from t_otherfee where " + ortherSearch.Condition + " and f_payfeevalid!='无效' order by id";
            ortherList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            ortherList.LoadOnPathChanged = false;
            ortherList.Path = "sql";
            ortherList.SumHQL = "select * from t_otherfee where " + ortherSearch.Condition + "";
            ortherList.HQL = sql;
            ortherList.PageSize = ui_pager.PageSize;
            ortherList.SumNames = "id";
            ortherList.PageIndex = pageIndex;
            ortherList.Load();
        }

        private void ortherList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_userBusy.IsBusy = false;
        }

        private void ui_pager_PageIndexChanged(object sender, EventArgs e)
        {
            ortherList.PageIndex = ui_pager.PageIndex;
        }

        private void pipelines_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            pipeline.DataContext = pipelines.SelectedItem;
        }

        private void save_Click(object sender, RoutedEventArgs e)
        {
            ui_userBusy1.IsBusy = true;
            string sql = "update t_otherfee set f_conclenote='" + ui_f_conclenote.Text + "',f_payfeevalid='无效' where id=" + ui_id.Text;
            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "t_otherfee";
            action.Completed += action_Completed;
            action.Invoke();
        }

        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_id.Text = "";
            ui_f_userid.Text = "";
            ui_f_username.Text = "";
            ui_f_usertype.Text = "";
            ui_f_gaswatchbrand.Text = "";
            ui_f_metertype.Text = "";
            ui_f_address.Text = "";
            ui_f_menpai.Text = "";
            ui_f_phone.Text = "";
            ui_f_cnote.Text = "";
            ui_f_parentname4.Text = "";
            ui_f_cmoperator.Text = "";
            ui_f_cmdate.Text = "";
            ui_f_filiale.Text ="";
            ui_f_feetype.Text = "";
            ui_f_fee.Text = "";
            ui_f_cardid.Text = "";
            ui_f_conclenote.Text = "";
            ui_userBusy1.IsBusy = false;
            ortherList.IsOld = true;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            ui_id.Text = "";
            ui_f_userid.Text = "";
            ui_f_username.Text = "";
            ui_f_usertype.Text = "";
            ui_f_gaswatchbrand.Text = "";
            ui_f_metertype.Text = "";
            ui_f_address.Text = "";
            ui_f_menpai.Text = "";
            ui_f_phone.Text = "";
            ui_f_cnote.Text = "";
            ui_f_parentname4.Text = "";
            ui_f_cmoperator.Text = "";
            ui_f_cmdate.Text = "";
            ui_f_filiale.Text = "";
            ui_f_feetype.Text = "";
            ui_f_fee.Text = "";
            ui_f_cardid.Text = "";
            ui_f_conclenote.Text = "";
        }
}
    }
