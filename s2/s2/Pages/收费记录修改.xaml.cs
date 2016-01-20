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
using System.Windows.Browser;
using Com.Aote.ObjectTools;
using Com.Aote.Behaviors;

namespace Com.Aote.Pages
{
    public partial class 收费记录修改 : UserControl
    {
        int id = 0;
        public 收费记录修改()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            //拿出datagrid所选数据
            GeneralObject go =SellUserUnits.SelectedItem as GeneralObject;
            //拿出页面数据上下文
            GeneralObject updatehandplan = SellUserUnit.DataContext as GeneralObject;
            //新建对象，往t_updatehandplan插入数据
            GeneralObject obj = new GeneralObject();
            try
            {
                obj.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                obj.EntityType = "t_updatesellinggas";

                obj.SetPropertyValue("f_userid", go.GetPropertyValue("f_userid")+"", false);
                obj.SetPropertyValue("f_newuserid", ui_userid.Text, false);
                obj.SetPropertyValue("f_username", go.GetPropertyValue("f_username")+"", false);
                obj.SetPropertyValue("f_newusername", ui_username.Text, false);
                obj.SetPropertyValue("f_address", go.GetPropertyValue("f_address")+"", false);
                obj.SetPropertyValue("f_newaddress", ui_address.Text, false);
                obj.SetPropertyValue("f_districtname", go.GetPropertyValue("f_districtname")+"", false);
                obj.SetPropertyValue("f_newdistrictname", ui_f_districtname.Text, false);

                obj.SetPropertyValue("f_sellid",go.GetPropertyValue("id").ToString(), false);
                obj.SetPropertyValue("f_usertype", ui_usertype.Text, false);
               if (go.GetPropertyValue("f_zhinajin") == null)
                {
                    go.SetPropertyValue("f_zhinajin", 0.0m, false);
                }
                 obj.SetPropertyValue("f_zhinajin", decimal.Parse(go.GetPropertyValue("f_zhinajin").ToString()), false);
                 if (ui_f_zhinajin.Text.ToString().Equals("")) {
                     ui_f_zhinajin.Text = "0";
                 }
                
                 obj.SetPropertyValue("f_newzhinajin", decimal.Parse(ui_f_zhinajin.Text), false);
                 if (go.GetPropertyValue("f_amountmaintenance") == null) 
                 {
                     go.SetPropertyValue("f_amountmaintenance", 0.0m, false);
                 }
                obj.SetPropertyValue("f_weihufei", decimal.Parse(go.GetPropertyValue("f_amountmaintenance").ToString()), false);
                if (ui_f_amountmaintenance.Text.ToString().Equals(""))
                {
                    ui_f_amountmaintenance.Text = "0";
                }
                
                obj.SetPropertyValue("f_newweihufei", decimal.Parse(ui_f_amountmaintenance.Text), false);
                obj.SetPropertyValue("f_shifouyouxiao", go.GetPropertyValue("f_payfeevalid").ToString(), false);
                obj.SetPropertyValue("f_newshifouyouxiao", ui_f_payfeevalid.Text, false);
                obj.SetPropertyValue("f_pregas", decimal.Parse(go.GetPropertyValue("f_pregas").ToString()), false);
                obj.SetPropertyValue("f_newpregas", decimal.Parse(ui_f_pregas.Text), false);
                obj.SetPropertyValue("f_preamount", decimal.Parse(go.GetPropertyValue("f_preamount").ToString()), false);
                obj.SetPropertyValue("f_newpreamount", decimal.Parse(ui_f_preamount.Text), false);
                obj.SetPropertyValue("f_shoukuan", decimal.Parse(go.GetPropertyValue("f_grossproceeds").ToString()), false);
                obj.SetPropertyValue("f_newshoukuan", decimal.Parse(ui_f_grossproceeds.Text), false);
                if (go.GetPropertyValue("f_benqizhye") == null)
                {
                    go.SetPropertyValue("f_benqizhye", 0.0m, false);
                }
                obj.SetPropertyValue("f_zhye", decimal.Parse(go.GetPropertyValue("f_benqizhye").ToString()), false);
                if (ui_f_benqizhye.Text.ToString().Equals(""))
                {
                    ui_f_benqizhye.Text = "0";
                }
                
                obj.SetPropertyValue("f_newzhye", decimal.Parse(ui_f_benqizhye.Text), false);

                if (go.GetPropertyValue("f_zhye") == null)
                {
                    go.SetPropertyValue("f_zhye", 0.0m, false);
                }
                obj.SetPropertyValue("f_shangqizhye", decimal.Parse(go.GetPropertyValue("f_zhye").ToString()), false);
                if (ui_f_zhye.Text.ToString().Equals(""))
                {
                    ui_f_zhye.Text = "0";
                }

                obj.SetPropertyValue("f_newshangqizhye", decimal.Parse(ui_f_zhye.Text), false);

                if (go.GetPropertyValue("lastinputgasnum") == null)
                {
                    go.SetPropertyValue("lastinputgasnum", 0.0m, false);
                }
                obj.SetPropertyValue("f_lastinputgasnums", decimal.Parse(go.GetPropertyValue("lastinputgasnum").ToString()), false);
                if (ui_f_zhye.Text.ToString().Equals(""))
                {
                    ui_f_zhye.Text = "0";
                }

                obj.SetPropertyValue("f_newlastinputgasnums", decimal.Parse(ui_f_lastinputgasnums.Text), false);

                if (go.GetPropertyValue("lastrecord") == null)
                {
                    go.SetPropertyValue("lastrecord", 0.0m, false);
                }
                obj.SetPropertyValue("f_lastrecord", decimal.Parse(go.GetPropertyValue("lastrecord").ToString()), false);
                if (ui_f_lastrecord.Text.ToString().Equals(""))
                {
                    ui_f_lastrecord.Text = "0";
                }

                obj.SetPropertyValue("f_newlastrecord", decimal.Parse(ui_f_lastrecord.Text), false);


                obj.SetPropertyValue("f_sellinggasoperator", ui_handplanoperator.Text, false);
                obj.SetPropertyValue("f_sellinggasdate", ui_handplandate.SelectedDate, false);
                obj.SetPropertyValue("f_updatenote", ui_updatenote.Text, false);
                obj.Name = "t_updatesellinggas";
                //obj.Completed += obj_Completed;
                obj.Save();
            }
            catch (Exception a)
            {
                MessageBox.Show(a.Message);
            }
           // BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
           // save.Invoke();

            string sql = "update t_sellinggas set f_zhinajin= " + decimal.Parse(ui_f_zhinajin.Text) +
                ",f_amountmaintenance=" + decimal.Parse(ui_f_amountmaintenance.Text) +
                ",f_payfeevalid='" + ui_f_payfeevalid.Text +
                "',f_pregas=" + decimal.Parse(ui_f_pregas.Text) +
                ",f_preamount=" +  decimal.Parse(ui_f_preamount.Text) +
                 ",f_grossproceeds=" +  decimal.Parse(ui_f_grossproceeds.Text) +
                  ",f_benqizhye=" +  decimal.Parse(ui_f_benqizhye.Text) +
                 ",f_zhye=" +  decimal.Parse(ui_f_zhye.Text)+
                 ",lastinputgasnum=" + decimal.Parse(ui_f_lastinputgasnums.Text) +
                 ",lastrecord=" + decimal.Parse(ui_f_lastrecord.Text) +
                 ",f_userid='" + ui_userid.Text +
                 "',f_payment='" +ui_payment.SelectedValue +
                 "',f_username='" + ui_username.Text +
                 "',f_address='" + ui_address.Text +
                 "',f_districtname='" + ui_f_districtname.Text +
                 "'  where id = " + go.GetPropertyValue("id");
            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "abc";
            action.Invoke();
            PagedObjectList save1 = (from p in loader.Res where p.Name.Equals("personlist") select p).First() as PagedObjectList;
            save1.IsOld = true;
            updatehandplan.New();
        }

    }
}