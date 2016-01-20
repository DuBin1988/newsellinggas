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
    public partial class 发卡售气修改 : UserControl
    {
        int id = 0;
        public 发卡售气修改()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            //拿出datagrid所选数据
            GeneralObject go = FaKaUserUnits.SelectedItem as GeneralObject;
            //拿出页面数据上下文
            GeneralObject updatehandplan = FaKaUserUnit.DataContext as GeneralObject;
            //新建对象，往t_updatehandplan插入数据
            GeneralObject obj = new GeneralObject();
            try
            {
                obj.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                obj.EntityType = "t_updatemyfaka";
                obj.SetPropertyValue("f_userid", ui_userid.Text, false);
                obj.SetPropertyValue("f_username", ui_username.Text, false);
                obj.SetPropertyValue("f_address", ui_address.Text, false);
                obj.SetPropertyValue("f_usertype", ui_usertype.Text, false);
                obj.SetPropertyValue("f_qibiaochangjia", ui_gasmetermanufacturers.Text, false);
                obj.SetPropertyValue("f_qibiaopinpai", ui_gaswatchbrand.Text, false);
                obj.SetPropertyValue("f_gaspricetype", ui_f_gaspricetype.Text, false);
                obj.SetPropertyValue("f_newpregas", decimal.Parse(ui_f_pregas.Text), false);
                obj.SetPropertyValue("f_pregas", decimal.Parse(go.GetPropertyValue("f_pregas").ToString()), false);
                obj.SetPropertyValue("f_cardfees", decimal.Parse(go.GetPropertyValue("f_cardfees").ToString()), false);
                obj.SetPropertyValue("f_newcardfees", decimal.Parse(ui_f_cardfees.Text), false);
                obj.SetPropertyValue("f_preamount", decimal.Parse(go.GetPropertyValue("f_preamount").ToString()), false);
                obj.SetPropertyValue("f_newpreamount", decimal.Parse(ui_f_preamount.Text), false);
                obj.SetPropertyValue("f_totalcost", decimal.Parse(go.GetPropertyValue("f_totalcost").ToString()), false);
                obj.SetPropertyValue("f_newtotalcost", decimal.Parse(ui_f_totalcost.Text), false);
                obj.SetPropertyValue("f_whethergivecard", go.GetPropertyValue("f_whethergivecard").ToString(), false);
                obj.SetPropertyValue("f_newwhethergivecard", ui_f_whethergivecard.Text, false);
                obj.SetPropertyValue("f_cardid", ui_f_cardid.Text, false);
                obj.SetPropertyValue("f_updatenote", ui_updatenote.Text, false);           
                obj.SetPropertyValue("f_handplanoperator", ui_operator.Text, false);
                obj.SetPropertyValue("f_handplandate", ui_handplandate.SelectedDate, false);
                obj.Name = "t_updatemyfaka";
                //obj.Completed += obj_Completed;
                obj.Save();
            }
            catch (Exception a)
            {
                MessageBox.Show(a.Message);
            }
 
           
            //oughtfee shifoujiaofei f_operator f_inputtor f_zhinajindate
             string sql = "update t_myfaka set f_pregas= " + decimal.Parse(ui_f_pregas.Text) +
                 ",f_cardfees=" + decimal.Parse(ui_f_cardfees.Text) +
                 ",f_preamount=" + decimal.Parse(ui_f_preamount.Text) +
                 ",f_totalcost=" + decimal.Parse(ui_f_totalcost.Text) +
                 ",f_whethergivecard='" + ui_f_whethergivecard.Text +
                
                 "'  where id = " + go.GetPropertyValue("id");
            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "abc";
            action.Invoke();
            //如果数据有误，页面提示
            //回调页面保存按钮功能
            //BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
           // save.Invoke();
            PagedObjectList save1 = (from p in loader.Res where p.Name.Equals("personlist") select p).First() as PagedObjectList;
            save1.IsOld = true;
            updatehandplan.New();
        }

    }
}