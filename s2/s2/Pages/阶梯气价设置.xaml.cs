using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using Com.Aote.Utils;
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
    public partial class 阶梯气价设置 : UserControl
    {
        //阶梯气价查询
        SearchObject stairSearch = new SearchObject();
        //阶梯气价信息列表
        PagedList stairList = new PagedList();
        private int count = 0;
        public 阶梯气价设置()
        {
            InitializeComponent();
            ui_StairSearch.DataContext = stairSearch;
            ui_stairDataGaid.ItemsSource = stairList;
            this.Loaded += 阶梯气价设置_Loaded;
            kbfee = (ObjectList)(from r in loader.Res where r.Name.Equals("SecondStairlist") select r).First();
            ThirdStairStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("ThirdStairStairlist") select r).First();
            FourthStairlist1 = (ObjectList)(from r in loader.Res where r.Name.Equals("FourthStairlist") select r).First();
        }

        void 阶梯气价设置_Loaded(object sender, RoutedEventArgs e)
        {
            loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");

            kbfee.DataLoaded += kbfee_DataLoaded;
            ThirdStairStairlist1.DataLoaded += ThirdStairStairlist1_DataLoaded;
            FourthStairlist1.DataLoaded += FourthStairlist1_DataLoaded;
        }
        ObjectList kbfee;
        ObjectList ThirdStairStairlist1;
        ObjectList FourthStairlist1;
        //ObjectList czylistnull1;
        GeneralObject loginUser;
        private void ui_StairSearchButton_Click(object sender, RoutedEventArgs e)
        {
            ui_searchBusy.IsBusy = true;
            string f_parentname = loginUser.GetPropertyValue("orgpathstr").ToString(); ;
            stairSearch.Search();
            string sql = "from t_stairprice where " + stairSearch.Condition + "and (f_OrgStr  like " + "'%" + f_parentname + "%'" + ")";
            stairList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            stairList.LoadOnPathChanged = false;
            stairList.Path = "hql";
            stairList.HQL = sql;
            stairList.PageSize = 100;
            stairList.DataLoaded += stairList_DataLoaded;
            stairList.PageIndex = 0;
        }

        private void stairList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            ui_searchBusy.IsBusy = false;
        }


        private void ui_SaveStairButton_Click(object sender, RoutedEventArgs e)
        {
            if (count == 1)
            {
                if (ui_stairtype.Text != "" || ui_stair1amount.Text != "" || ui_stair1price.Text != "" || ui_stair2amount.Text != "" || ui_stair2price.Text != "" || ui_stair3amount.Text != "" || ui_stair3price.Text != "" || ui_stairmonths.SelectedValue != null || ui_f_gasproperties.SelectedValue != null || f_OrgStr.Text != "")
                {
                    ui_searchBusy.IsBusy = true;
                    // 通过执行sql语句进行设置
                    string sql = "update t_stairprice set f_stairtype='" + ui_stairtype.Text + "', f_stair1amount=" + ui_stair1amount.Text + " , " +
                        "f_stair1price=" + ui_stair1price.Text + " , f_stair2amount='" + ui_stair2amount.Text + "', f_stair2price=" + ui_stair2price.Text + ",f_stair3amount='" + ui_stair3amount.Text + "', f_stair3price=" + ui_stair3price.Text + ", f_stair4price=" + ui_stair4price.Text + " ,f_stairmonths=" + ui_stairmonths.SelectedValue + "  ,f_gasproperties='" + ui_f_gasproperties.SelectedValue + "' ,SecondStair='" + SecondStair.SelectedValue + "' ,ThirdStair='" + ThirdStair.SelectedValue + "' ,FourthStair='" + FourthStair.SelectedValue + "' ,FirstStair= '" + FirstStair.Text + "' ,f_OrgStr= '" + f_OrgStr.Text + "'  where id=" + ui_id.Text;
                    HQLAction action = new HQLAction();
                    action.HQL = sql;
                    action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    action.Name = "t_stairprice";
                    action.Completed += action_Completed;
                    action.Invoke();
                }
                else
                {
                    MessageBox.Show("请输入完整信息！");
                    return;
                }
            }
            else
            {
                if (ui_stairtype.Text != "" || ui_stair1amount.Text != "" || ui_stair1price.Text != "" || ui_stair2amount.Text != "" || ui_stair2price.Text != "" || ui_stair3amount.Text != "" || ui_stair3price.Text != "" || ui_stairmonths.SelectedValue != null || ui_f_gasproperties.SelectedValue != null)
                {
                    ui_searchBusy.IsBusy = true;
                    GeneralObject obj = new GeneralObject();
                    obj.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                    obj.EntityType = "t_stairprice";
                    obj.SetPropertyValue("f_stairtype", ui_stairtype.Text, false);
                    obj.SetPropertyValue("f_stair1amount", ui_stair1amount.Text, false);
                    obj.SetPropertyValue("f_stair1price", ui_stair1price.Text, false);
                    obj.SetPropertyValue("f_stair2amount", ui_stair2amount.Text, false);
                    obj.SetPropertyValue("f_stair2price", ui_stair2price.Text, false);
                    obj.SetPropertyValue("f_stair3amount", ui_stair3amount.Text, false);
                    obj.SetPropertyValue("f_stair3price", ui_stair3price.Text, false);
                    obj.SetPropertyValue("f_stair4price", ui_stair4price.Text, false);
                    obj.SetPropertyValue("f_stairmonths", ui_stairmonths.SelectedValue, false);
                    obj.SetPropertyValue("f_gasproperties", ui_f_gasproperties.SelectedValue, false);
                    obj.SetPropertyValue("FirstStair", FirstStair.Text, false);
                    obj.SetPropertyValue("SecondStair", SecondStair.SelectedValue, false);
                    obj.SetPropertyValue("ThirdStair", ThirdStair.SelectedValue, false);
                    obj.SetPropertyValue("FourthStair", FourthStair.SelectedValue, false);
                    obj.SetPropertyValue("f_OrgStr", f_OrgStr.Text, false);
                    obj.Name = "t_stairprice";
                    obj.Completed += obj_Completed;
                    obj.Save();
                }
                else
                {
                    MessageBox.Show("请输入完整信息！");
                    return;
                }
            }
        }

        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            stairList.IsOld = true;
            ui_searchBusy.IsBusy = false;
            Clear();
        }

        private void obj_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            stairList.IsOld = true;
            ui_searchBusy.IsBusy = false;
            Clear();
        }

        private void ui_stairDataGaid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            count = 1;

            GeneralObject go = ui_stairDataGaid.SelectedItem as GeneralObject;
            ui_id.Text = go.GetPropertyValue("id").ToString();
            ui_stairtype.Text = go.GetPropertyValue("f_stairtype").ToString();
            ui_stair1amount.Text = go.GetPropertyValue("f_stair1amount").ToString();
            ui_stair1price.Text = go.GetPropertyValue("f_stair1price").ToString();
            ui_stair2amount.Text = go.GetPropertyValue("f_stair2amount").ToString();
            ui_stair2price.Text = go.GetPropertyValue("f_stair2price").ToString();
            ui_stair3amount.Text = go.GetPropertyValue("f_stair3amount").ToString();
            ui_stair3price.Text = go.GetPropertyValue("f_stair3price").ToString();
            ui_stair4price.Text = go.GetPropertyValue("f_stair4price").ToString();
            ui_stairmonths.SelectedValue = go.GetPropertyValue("f_stairmonths").ToString();
            ui_f_gasproperties.SelectedValue = go.GetPropertyValue("f_gasproperties").ToString();
        }

        private void ui_CancelStairButton_Click(object sender, RoutedEventArgs e)
        {
            count = 0;
            Clear();
        }
        private void Clear()
        {
            ui_id.Text = "";
            ui_stairtype.Text = "";
            ui_stair1amount.Text = "";
            ui_stair1price.Text = "";
            ui_stair2amount.Text = "";
            ui_stair2price.Text = "";
            ui_stair3amount.Text = "";
            ui_stair3price.Text = "";
            ui_stair4price.Text = "";
            ui_stairmonths.SelectedValue = "";
            ui_f_gasproperties.SelectedValue = "";
        }
        void ThirdStairStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (ThirdStairStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 3)
                {
                    ThirdStair.SelectedValue = str[2];
                }

            }
        }
        void FourthStairlist1_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (FourthStairlist1.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 4)
                {
                    FourthStair.SelectedValue = str[3];
                }

            }
        }
        void kbfee_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if (kbfee.Count > 0)
            {
                string cc = loginUser.GetPropertyValue("orgpathstr").ToString();

                char[] c = { '.' };
                string[] str = cc.Split(c);
                if (str.Length >= 2)
                {
                    SecondStair.SelectedValue = str[1];
                }

            }
        }
    }
}
