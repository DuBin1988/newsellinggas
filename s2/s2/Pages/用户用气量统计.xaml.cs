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

namespace Com.Aote.Pages
{
	public partial class 用户用气量统计 : UserControl
	{
       // PagedList hejilists = new PagedList();
        public 用户用气量统计()
		{
			InitializeComponent();
          //  userfiles.ItemsSource = hejilists;
		}

        //private void searchbutton_Click(object sender, RoutedEventArgs e)
        //{
        //    SearchObject so = infosearch.DataContext as SearchObject;
        //    so.Search();

        //    String sql = "select f_username,f_districtname,f_apartment,f_cardid,f_givecarddate,f_linkname,f_phone,f_usertype,f_gasprice,f_userid,count(num) num,sum(pregas) pregas,sum(totalcost) totalcost from" +
        //        " (" +
        //        " select t2.f_username,t2.f_districtname,t2.f_apartment,t2.f_cardid,t2.f_givecarddate,t2.f_linkname,t2.f_phone,t2.f_usertype,t2.f_gasprice,t2.f_userid,t1.num,t1.pregas,t1.totalcost,t1.f_deliverydate from [jinzhong].[dbo].[t_userfiles] t2" +
        //        " LEFT JOIN " +
        //        " (SELECT id num,f_userid,ISNULL(f_pregas,0) pregas,ISNULL(f_totalcost,0) totalcost,f_deliverydate FROM [jinzhong].[dbo].[t_sellinggas]) t1" +
        //        " ON t1.f_userid=t2.f_userid" +
        //        " ) t" +
        //        " where 1=1 and " + so.Condition +
        //        " group by f_username,f_districtname,f_apartment,f_cardid,f_givecarddate,f_linkname,f_phone,f_usertype,f_gasprice,f_userid";

        //    hejilists.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
        //    hejilists.LoadOnPathChanged = false;
        //    hejilists.Path = "sql";
        //    hejilists.SumNames = ",";
        //    hejilists.PageSize = 100;
        //    hejilists.HQL = sql;
        //    hejilists.PageIndex = 0;
        //   //hejilists.Load();

        //}
	}
}