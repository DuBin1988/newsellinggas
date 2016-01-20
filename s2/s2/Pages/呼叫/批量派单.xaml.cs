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
    public partial class 批量派单 : UserControl
	{
        SearchObject search = new SearchObject();
        PagedObjectList pl = new PagedObjectList();

		public 批量派单()
		{
			// Required to initialize variables
			InitializeComponent();
            daninfosearch.DataContext = search;
		}


        private void Button_Click(object sender, RoutedEventArgs e)
        {
            ui_searchBusy.IsBusy = true;
            search.Search();

            string sql = "update t_repairsys set f_downloadstatus=NULL,f_accepter='" + f_accepter.SelectedValue + "' where " + search.Condition + "";
            HQLAction action = new HQLAction();
            action.HQL = sql;
            action.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            action.Name = "t_repairsys";
            action.Completed += action_Completed;
            action.Invoke();

        }

        private void Button_Click1(object sender, RoutedEventArgs e)
        {
            pl = daninfos.ItemsSource as PagedObjectList;
            pl.Save();

        }


        private void action_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            //
        }

	}
}