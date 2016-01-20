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
    public partial class 其他收费修改 : UserControl
    {
        int id = 0;
        public 其他收费修改()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            //拿出datagrid所选数据
            GeneralObject go = OtherUserUnits.SelectedItem as GeneralObject;
            //拿出页面数据上下文
            GeneralObject updatehandplan = OtherUserUnit.DataContext as GeneralObject;
            //新建对象，往t_updatehandplan插入数据
            GeneralObject obj = new GeneralObject();
            try
            {
                obj.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                obj.EntityType = "t_updateotherfee";
                obj.SetPropertyValue("f_userid", ui_userid.Text, false);
                obj.SetPropertyValue("f_username", ui_username.Text, false);
                obj.SetPropertyValue("f_address", ui_address.Text, false);
                obj.SetPropertyValue("f_sellid", go.GetPropertyValue("id").ToString(), false);


                obj.SetPropertyValue("f_feetype", go.GetPropertyValue("f_feetype").ToString(), false);
                obj.SetPropertyValue("f_newfeetype", ui_f_feetype.Text, false);
                obj.SetPropertyValue("f_fee", decimal.Parse(go.GetPropertyValue("f_fee").ToString()), false);
                obj.SetPropertyValue("f_newfee", decimal.Parse(ui_f_fee.Text), false);
                
                obj.SetPropertyValue("f_sellinggasoperator", ui_handplanoperator.Text, false);
                obj.SetPropertyValue("f_sellinggasdate", ui_handplandate.SelectedDate, false);
                obj.SetPropertyValue("f_updatenote", ui_updatenote.Text, false);
                obj.Name = "t_updateotherfee";
                //obj.Completed += obj_Completed;
                obj.Save();
            }
            catch (Exception a)
            {
                MessageBox.Show(a.Message);
            }
           
            //oughtfee shifoujiaofei f_operator f_inputtor f_zhinajindate
             string sql = "update t_otherfee set f_feetype= '" + ui_f_feetype.Text +
              "',f_fee="+decimal.Parse(ui_f_fee.Text)+
         
                 "  where id = " + go.GetPropertyValue("id");
           HQLAction action = new HQLAction();
           action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "abc";
           action.Invoke();
            //如果数据有误，页面提示
            //回调页面保存按钮功能
            //BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
           // save.Invoke();
            updatehandplan.New();
        }

    }
}