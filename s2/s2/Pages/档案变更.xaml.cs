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
using Com.Aote.Behaviors;
using System.Net;
using System.Linq;

namespace Com.Aote.Pages
{


        #region saveButton_Click 保存按钮按下时

    public partial class 档案变更 : UserControl
    {
        public 档案变更()
        {
            InitializeComponent();
        }

        private void save_Click(object sender, RoutedEventArgs e)
        {

            ui_userfileschange.IsBusy = true;


            String sql = "update t_handplan set f_username='" + ui_usernamechange.Text + "'" +
                ",lastinputdate='" + ui_lastinputdatechange.Text + "'" +
                ",lastinputgasnum='" + ui_lastinputgasnumchange.Text + "'" +
                ",f_inputtor='" + ui_inputtorchange.SelectedValue + "'" +
                ",f_phone='" + ui_phonechange.Text + "'" +
                ",f_address='" + ui_addresschange.Text + "'" +
                ",f_usertype='" + ui_usertypechange.Text + "'" +
                ",f_weizhi='" + ui_weizhichange.Text + "'" +
                ",f_gasproperties='" + CoboxGasPro.SelectedValue + "'" +
                ",f_gaspricetype='" + CoboxGas.SelectedValue + "'" +
                ",f_gasprice='" + ui_gaspricechange.Text + "'" +
                ",f_metertype='" + ui_metertypechange.SelectedValue + "'" +
                ",f_gasmetermanufacturers='"+ui_gasmetermanufacturerschange.Text+"'"+
                ",f_gasmeterstyle='"+ui_gasmeterstylechange.Text+"'"+
                ",f_meternumber='"+ui_meternumberchange.Text+"'"+
                 ",f_stairtype='" + CoboxStair.SelectedValue.ToString() + "'" +
                ",f_aliasname='"+ui_aliasnamechange.Text+"' where f_state='未抄表' and f_userid='"+ui_userid.Text+"'";

            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "abc";
            action.Invoke();

            GeneralObject obj = userfileschange.DataContext as GeneralObject;
            userid = obj.GetPropertyValue("f_userid")+"";
            if ((obj.GetPropertyValue("f_meternumber") + "").Equals(obj.GetPropertyValue("f_meternumberchange") + "") && (obj.GetPropertyValue("terminal_name") + "").Equals(obj.GetPropertyValue("terminal_namechange") + "") && (obj.GetPropertyValue("meter_phone") + "").Equals(obj.GetPropertyValue("meter_phonechange") + ""))
                ui_refreshCachechange.Text = "0";
            else
            //{
                //if ("物联网表".Equals(ui_gasmeterstylechange.Text))
                    ui_refreshCachechange.Text = "1";
                //else
                    //ui_refreshCachechange.Text = "0";
            //}
            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save.Completed += save_Completed;
            save.Invoke();

        }
        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction save = sender as BatchExcuteAction;
            save.Completed -= save_Completed;
            // 界面的表号、终端名称、标记电话，清空
            terminal_namechange.Text = "";
            meter_phonechange.Text = "";
            ui_meternumberchange.Text = "";
            if (userid != null)
            {
                //将产生的json串送后台服务进行处理
                WebClientInfo wci = Application.Current.Resources["server"] as WebClientInfo;
                string uri = wci.BaseAddress + "/iesgas/user/comand";
                WebClient client = new WebClient();
                client.UploadStringCompleted += client_UploadStringCompleted;
                client.UploadStringAsync(new Uri(uri), "[{customer_code:\"" + userid + "\"}]");
            }
            else
            {
                ui_userfileschange.IsBusy = false;
            }
        }

        void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            ui_userfileschange.IsBusy = false;
            if (e.Error == null)
            {
                //弹出错误信息
                if (e.Result != "")
                {
                    // MessageBox.Show(e.Result);
                }

            }
        }
        #endregion

        private void TextBox_LostFocus_1(object sender, RoutedEventArgs e)
        {
            //{m:Exp Str=tabletel.IsOld\=True}
            if (null != ui_meternumberchange.Text && !"".Equals(ui_meternumberchange.Text))
            {
                GeneralObject save2 = (from p in loader.Res where p.Name.Equals("tabletel") select p).First() as GeneralObject;
                save2.isBusy = true;
                save2.Path = "one/select new Map(meter_phone as meter_phone,terminal_name as terminal_name) from t_table_tel  where f_meternumber='" + ui_meternumberchange.Text + "'";
                save2.Load();
            }
        }
    }
}
