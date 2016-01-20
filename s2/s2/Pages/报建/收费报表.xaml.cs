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

namespace s2
{
    public partial class 收费报表 : UserControl
    {
        PagedList list = new PagedList();

        public 收费报表()
        {
            InitializeComponent();

            ui_list.ItemsSource = list;
        }

        private void ui_searchButton_Click(object sender, RoutedEventArgs e)
        {
            string worker1 = "%" + worker.Text + "%";
            string kssj1=kssj.Text;
            string jssj1=jssj.Text;
            string sql = "select SUM(convert(float,ISNULL(f_fee,0))) as fee_yj,f_opertor " + 
                "from t_paydetail "+
                "where (f_date BETWEEN '" + kssj1 + "' and '" + jssj1 + "') and f_opertor like '" + worker1 + "' GROUP BY f_opertor";
            list.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            list.LoadOnPathChanged = false;
            list.Path = "sql";
            list.PageSize = 100;
            list.HQL = sql;
            list.PageIndex = 0;
           // list.LoadDetail();
          

        }
    }
}
