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
    public partial class 收费报表系统 : UserControl
    {
        PagedList list = new PagedList();

        public 收费报表系统()
        {
            InitializeComponent();

            ui_list.ItemsSource = list;
            string sql = "select " +
                "SUM(convert(float,ISNULL(f_totalfee,0))) as fee_yj, " +
                "SUM(convert(float,ISNULL(f_actualfee,0))) as fee_sj, " +
                "SUM((ISNULL(f_totalfee,0)-ISNULL(f_actualfee,0))) as fee_qf, " +
                "f_gasproperties as lx " +
                "from t_apply " +
                "where f_gasproperties in ('非民用','民用') GROUP BY f_gasproperties";
            list.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            list.LoadOnPathChanged = false;
            list.Path = "sql";
            list.PageSize = 100;
            list.HQL = sql;
            list.PageIndex = 0;
        }

        private void ui_searchButton_Click(object sender, RoutedEventArgs e)
        {
            
           
        }
    }
}
