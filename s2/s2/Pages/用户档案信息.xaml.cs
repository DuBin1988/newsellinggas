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
	public partial class 用户档案信息 : UserControl
	{
        SearchObject userSearch = new SearchObject();

        PagedList userList = new PagedList();

		public 用户档案信息()
		{
			// Required to initialize variables
			InitializeComponent();

            daninfosearch.DataContext = userSearch;

            daninfos.ItemsSource = userList;
		}
        int pageIndex = 0;
        private void dansearchbutton_Click(object sender, RoutedEventArgs e)
        {
            ui_busys.IsBusy = true;
            userSearch.Search();
            userList.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            userList.LoadOnPathChanged = false;
            string sql = "select f_userid,old,f_meternumber,f_username,f_usertype,f_cardid,f_gaswatchbrand,f_metertype," +
                "lastinputgasnum,f_zhye,f_address,f_districtname,f_apartment,f_phone,f_gasproperties,f_gaspricetype,f_gasprice," +
                "f_wallhangboiler,f_cumulativepurchase,f_metergasnums,CONVERT(varchar(12), f_beginfee, 111 ) f_beginfee," +
                "CONVERT(varchar(12), f_givecarddate, 111 ) f_givecarddate,f_whethergivecard,f_userstate,f_finallybought," +
                "CONVERT(varchar(12), f_finabuygasdate, 111 ) f_finabuygasdate,f_anzhuanguser,CONVERT(varchar(12), " +
                "f_watchinstalldate, 111 ) f_watchinstalldate,f_payment,f_bankname,f_usermc,f_idofcard,f_filiale,f_yytdepa," +
                "f_yytoper,CONVERT(varchar(12), f_yytdate, 111 ) f_yytdate,f_credentials,f_idnumber,f_dibaohu,f_gasmetermanufacturers," +
                "f_aroundmeter,f_kitchennum,f_kitchenbrand,f_kitchenmodel,f_kitstyle,f_kitchenbrand2,f_kitchenmodel2,f_waterheaternum,f_waterheaterbrand,f_waterheatermodel,f_wallhangboilernum,f_wallhangboilerbrand,f_wallhangboilermodel,f_gasmeteraccomodations,f_aliasname,f_kitchen,f_waterheater,f_stairtype,f_stair1amount,f_stair1price,f_stair2amount,f_stair2price,f_stair3amount,f_stair3price,f_stairmonths from t_userfiles " +
                "where " + userSearch.Condition + " order by id";
            userList.LoadOnPathChanged = false;
            userList.Path = "sql";
            userList.SumHQL = "select f_userid,old,f_meternumber,f_username,f_usertype,f_cardid,f_gaswatchbrand,f_metertype,"+
                "lastinputgasnum,f_zhye,f_address,f_districtname,f_apartment,f_phone,f_gasproperties,f_gaspricetype,f_gasprice,"+
                "f_wallhangboiler,f_cumulativepurchase,f_metergasnums,CONVERT(varchar(12), f_beginfee, 111 ) f_beginfee,"+
                "CONVERT(varchar(12), f_givecarddate, 111 ) f_givecarddate,f_whethergivecard,f_userstate,f_finallybought,"+
                "CONVERT(varchar(12), f_finabuygasdate, 111 ) f_finabuygasdate,f_anzhuanguser,CONVERT(varchar(12), "+
                "f_watchinstalldate, 111 ) f_watchinstalldate,f_payment,f_bankname,f_usermc,f_idofcard,f_filiale,f_yytdepa,"+
                "f_yytoper,CONVERT(varchar(12), f_yytdate, 111 ) f_yytdate,f_credentials,f_idnumber,f_dibaohu,f_gasmetermanufacturers,"+
                "f_aroundmeter,f_gasmeteraccomodations,f_aliasname,f_stairtype,f_stair1amount,f_stair1price,f_stair2amount,f_stair2price,f_stair3amount,f_stair3price,f_stairmonths from t_userfiles " +
                "where "+userSearch.Condition+"";
            userList.HQL = sql;
            userList.PageSize = pager2.PageSize;
            userList.SumNames = ",";
            userList.DataLoaded += setList_DataLoaded;
            userList.PageIndex = pageIndex;
            userList.Load();
        }

        private void setList_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            if(userList.Size!=0){
                double zhye = 0;
                foreach(GeneralObject go in userList){
                    if (go.GetPropertyValue("f_zhye") == null) {
                        go.SetPropertyValue("f_zhye",0.0,false);
                       }
                    zhye += Double.Parse(go.GetPropertyValue("f_zhye").ToString());
                }
                ui_sumzhye.Text = "用户余额："+zhye.ToString();
            }
            ui_busys.IsBusy = false;
        }
        private void ui_pager_PageIndexChanged(object sender, EventArgs e)
        {
            userList.PageIndex = pager2.PageIndex;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            detail.Visibility = Visibility.Visible;
        }
	}
}