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

namespace s2
{
	public partial class 话务员界面 : UserControl
	{
        //ObjectList list = new ObjectList();
        //PagedList list = new PagedList();
		public 话务员界面()
		{
           
			// Required to initialize variables
			InitializeComponent();
          //  jiedanren.ItemsSource = list;
           // jiedanren.ItemsSource = list;
            //jiedanren.ItemsSource = list;
		}

        private void opertabs_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {

        }

        private void f_phone(object sender, TextChangedEventArgs e)
        {
            ldlist.Path = "from t_repairsys where f_phone='" + ((TextBox)sender).Text + "'";
        }

        private void f_address(object sender, TextChangedEventArgs e)
        {
           // ldlist.Path = "from t_repairsys where f_address like '%" + ((TextBox)sender).Text + "%'";
        }

      /*  private void jiedanren_MouseEnter(object sender, MouseEventArgs e)
        {
            string sql = "select man,phone from t_repairsman where f_repairsmanstate = '空闲'";
            list.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            list.LoadOnPathChanged = false;
            list.Path = "sql";
            list.HQL = sql;
            list.PageSize = 100;
            list.LoadDetail();
            list.PageIndex = 0;
            jiedanren.ItemsSource = list;
            jiedanren.ItemsSource = list;
            
        }*/
	}
}