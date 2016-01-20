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
using System.Linq;

namespace s2
{
    public partial class 冲正 : UserControl
    {
        public 冲正()
        {
            // 为初始化变量所必需
            InitializeComponent();
        }

        private void save_Click(object sender, RoutedEventArgs e)
        {
            if (ui_id.Text == null)
            {
                MessageBox.Show("无收费记录");
                return;
            }
            try
            {
                String sql = "update t_apply set f_actualfee=f_actualfee-(select f_fee from t_paydetail where id=" + ui_id.Text + ") where f_code='" + ui_code.Text + "'";
                HQLAction action = new HQLAction();
                action.HQL = sql;
                action.Type = "sql";
                action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action.Name = "abc";
                action.Invoke();
                String sql1 = "update t_paydetail set f_state='无效' where id=" + ui_id.Text;
                HQLAction action1 = new HQLAction();
                action1.HQL = sql1;
                action1.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
                action1.Name = "abc1";
                action1.Invoke();
                //GeneralObject go1=(from p in loader.Res where p.Name.Equals("reversalmx") select p).First() as GeneralObject;
                //go1.SetValue("id",null);
                SyncActionFactory save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as SyncActionFactory;
                save.Invoke();
                ((from p in loader.Res where p.Name.Equals("paydetails") select p).First() as GeneralObject).IsInit = true;
                //go1.IsInit = true;
                (reversalmxgrid.DataContext as GeneralObject).IsInit = true;
            }
            catch (Exception ex)
            {
                MessageBox.Show("请记录用户情况，联系管理员");
            }
        }

        private void TabControl_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {

        }
    }
}