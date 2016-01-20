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
	public partial class 话务员绩效统计 : UserControl
	{
       

        public 话务员绩效统计()
		{
			// Required to initialize variables
			InitializeComponent();
            //daninfos.ItemsSource = hejilists;
		}
    private void so_heji(object sender, RoutedEventArgs e)
        {
            SearchObject so = daninfosearch.DataContext as SearchObject;
            PagedList hejilists = (PagedList)daninfos.ItemsSource;
            so.Search();
            String sql = "" +
                //小区，安检情况
                        " count(t1.id) hj, t1.f_sender f_sender,count(t2.online) online,count(t3.online_zyj) online_zyj,count(h1.ywc) ywc,count(h2.dwc) dwc,count(h3.wwc) wwc,count(f1.fwmy) fwmy,count(f2.fwbmy) fwbmy,count(z1.zlmy) zlmy,count(z2.zlbmy) zlbmy" +
                        " FROM t_repairsys t1"+
                        " LEFT JOIN "+
                        " (select id,count(f_dealtype) online from t_repairsys where f_dealtype ='电话处理' group by id) t2"+
                        " ON t1.id=t2.id"+
                        " LEFT JOIN"+ 
                        " (select id,count(f_dealtype) online_zyj from t_repairsys where f_dealtype ='转交维修' group by id) t3"+
                        " ON t1.id=t3.id"+
                        " LEFT JOIN"+
                        " (select id,count(f_havacomplete) ywc from t_repairsys where f_havacomplete ='已完成' group by id) h1"+
                        " ON t1.id=h1.id"+
                        " LEFT JOIN"+
                        " (select id,count(f_havacomplete) dwc from t_repairsys where f_havacomplete ='待完成' group by id) h2"+
                        " ON t1.id=h2.id"+
                        " LEFT JOIN "+
                        " (select id,count(f_havacomplete) wwc from t_repairsys where f_havacomplete ='未完成' group by id) h3"+
                        " ON t1.id=h3.id"+
                        " LEFT JOIN "+
                        " (select id,count(f_fwtdmiqk) fwmy from t_repairsys where f_fwtdmiqk ='满意' group by id) f1"+
                        " ON t1.id=f1.id"+
                        " LEFT JOIN "+
                        " (select id,count(f_fwtdmiqk) fwbmy from t_repairsys where f_fwtdmiqk ='不满意' group by id) f2"+
                        " ON t1.id=f2.id"+
                        " LEFT JOIN "+
                        " (select id,count(f_zlmiqk) zlmy from t_repairsys where f_zlmiqk ='满意' group by id) z1"+
                        " ON t1.id=z1.id"+
                        " LEFT JOIN "+
                        " (select id,count(f_zlmiqk) zlbmy from t_repairsys where f_zlmiqk ='不满意' group by id) z2"+
                        " ON t1.id=z2.id ";

            hejilists.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            hejilists.LoadOnPathChanged = false;
            hejilists.Path = "sql";
            hejilists.SumNames = "hj,";
            hejilists.PageSize = 1000;
            hejilists.HQL = "select " + sql + " where " + so.Condition + " group by t1.f_sender";
            hejilists.PageIndex = 0;
            hejilists.DataLoaded +=hejilists_DataLoaded;
            //hejilists.LoadDetail();
        }

    private void hejilists_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
    {
        //throw new NotImplementedException();
        //hejilists.DataLoaded -= hejilists_DataLoaded;
        PagedList hejilists = (PagedList)sender;
        if (null != hejilists && hejilists.Count >= 1)
        {
            daninfos.ItemsSource = hejilists;
        }

    }




  }

}