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

namespace Com.Aote.Pages
{
	public partial class 历史售气收费维护 : UserControl
	{
		public 历史售气收费维护()
		{
			// Required to initialize variables
			InitializeComponent();
            
		}

        private void save2_Click(object sender, RoutedEventArgs e)
        {
            changebusy.IsBusy = true;
            GeneralObject go = userfiles.SelectedItem as GeneralObject;
            GeneralObject kbfe = new GeneralObject();
            kbfe.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            kbfe.EntityType = "t_upkeep";
            if (go.GetPropertyValue("f_userid")!= null)
            {
                kbfe.SetPropertyValue("f_userid", go.GetPropertyValue("f_userid").ToString(), false);
            }
            if (go.GetPropertyValue("f_username") != null)
            {
                kbfe.SetPropertyValue("f_username", go.GetPropertyValue("f_username").ToString(), false);
            }
            if (f_username.Text != null && f_username.Text != "")
            {
                kbfe.SetPropertyValue("f_newusername", f_username.Text, false);
            }
            if (go.GetPropertyValue("f_cardid") != null)
            {
                kbfe.SetPropertyValue("f_cardid", go.GetPropertyValue("f_cardid").ToString(), false);
            }
            if (go.GetPropertyValue("f_districtname") != null)
            {
                kbfe.SetPropertyValue("f_districtname", go.GetPropertyValue("f_districtname").ToString(), false);
            }
            if (f_districtname.Text != null && f_districtname.Text != "")
            {
                kbfe.SetPropertyValue("f_newdistrictname", f_districtname.Text, false);
            }
            if (go.GetPropertyValue("f_gaswatchbrand") != null)
            {
                kbfe.SetPropertyValue("f_gaswatchbrand", go.GetPropertyValue("f_gaswatchbrand").ToString(), false);
            }
            if (f_gasproperties.Text != null && f_gasproperties.Text != "")
            {
                kbfe.SetPropertyValue("f_gasproperties", f_gasproperties.Text, false);
            }
            if (go.GetPropertyValue("f_beginfee") != null)
            {
                kbfe.SetPropertyValue("f_beginfee", go.GetPropertyValue("f_beginfee").ToString(), false);
            }
            if (f_beginfee.Text != null && f_beginfee.Text != "")
            {
                kbfe.SetPropertyValue("f_newbeginfee", f_beginfee.Text, false);
            }
            if (go.GetPropertyValue("f_upbuynum") != null)
            {
                kbfe.SetPropertyValue("f_upbuynum", go.GetPropertyValue("f_upbuynum").ToString(), false);
            }
            if (go.GetPropertyValue("f_pregas") != null )
            {
                kbfe.SetPropertyValue("f_pregas", go.GetPropertyValue("f_pregas").ToString(), false);
            }
            if (f_pregas.Text != null && f_pregas.Text != "")
            {
                kbfe.SetPropertyValue("f_newpregas", f_pregas.Text, false);
            }
            if (go.GetPropertyValue("f_payment") != null)
            {
                kbfe.SetPropertyValue("f_payment", go.GetPropertyValue("f_payment").ToString(), false);
            }
            if (f_payment.SelectedValue != null)
            {
                kbfe.SetPropertyValue("f_newpayment", f_payment.SelectedValue, false);
            }
            if (go.GetPropertyValue("f_sgnetwork") != null )
            {
                kbfe.SetPropertyValue("f_sgnetwork", go.GetPropertyValue("f_sgnetwork").ToString(), false);
            }
            if (f_reason.Text != null && f_reason.Text != "")
            {
                kbfe.SetPropertyValue("f_reason", f_reason.Text, false);
            }
            if (go.GetPropertyValue("f_usertype") != null )
            {
                kbfe.SetPropertyValue("f_usertype", go.GetPropertyValue("f_usertype").ToString(), false);
            }
            if (go.GetPropertyValue("f_address") != null)
            {
                kbfe.SetPropertyValue("f_address", go.GetPropertyValue("f_address").ToString(), false);
            }
            if (f_address.Text != null && f_address.Text != "")
            {
                kbfe.SetPropertyValue("f_newaddress", f_address.Text, false);
            }
            if (go.GetPropertyValue("f_metertype") != null )
            {
                kbfe.SetPropertyValue("f_metertype", go.GetPropertyValue("f_metertype").ToString(), false);
            }
            if (go.GetPropertyValue("f_gaspricetype") != null)
            {
                kbfe.SetPropertyValue("f_gaspricetype", go.GetPropertyValue("f_gaspricetype").ToString(), false);
            }
            if (go.GetPropertyValue("f_endfee") != null)
            {
                kbfe.SetPropertyValue("f_endfee", go.GetPropertyValue("f_endfee").ToString(), false);
            }
            if (f_endfee.SelectedDate != null)
            {
                kbfe.SetPropertyValue("f_newendfee", f_endfee.SelectedDate, false);
            }
            if (go.GetPropertyValue("f_premetergasnums") != null)
            {
                kbfe.SetPropertyValue("f_premetergasnums", go.GetPropertyValue("f_premetergasnums").ToString(), false);
            }
            if ( go.GetPropertyValue("f_preamount") != null)
            {
                kbfe.SetPropertyValue("f_preamount", go.GetPropertyValue("f_preamount").ToString(), false);
            }
            if (f_preamount.Text != null && f_preamount.Text != "")
            {
                kbfe.SetPropertyValue("f_newpreamount", f_preamount.Text, false);
            }
            if (go.GetPropertyValue("f_grossproceeds") != null)
            {
                kbfe.SetPropertyValue("f_grossproceeds", go.GetPropertyValue("f_grossproceeds").ToString(), false);
            }
            if (f_grossproceeds.Text != null)
            {
                kbfe.SetPropertyValue("f_newgrossproceeds", f_grossproceeds.Text, false);
            }
            if (go.GetPropertyValue("f_sgoperator") != null)
            {
                kbfe.SetPropertyValue("f_sgoperator", go.GetPropertyValue("f_sgoperator").ToString(), false);
            }
            if (go.GetPropertyValue("f_oldtype") != null)
            {
                kbfe.SetPropertyValue("f_oldtype", go.GetPropertyValue("f_oldtype").ToString(), false);
            }
            if (go.GetPropertyValue("f_payfeevalid") != null)
            {
                kbfe.SetPropertyValue("f_payfeevalid", go.GetPropertyValue("f_payfeevalid").ToString(), false);
            }
            if (f_payfeevalid.SelectedValue != null)
            {
                kbfe.SetPropertyValue("f_newpayfeevalid", f_payfeevalid.SelectedValue, false);
            }
            if (go.GetPropertyValue("f_limitbuygas") != null)
            {
                kbfe.SetPropertyValue("f_limitbuygas", go.GetPropertyValue("f_limitbuygas").ToString(), false);
            }
            if (go.GetPropertyValue("f_gasprice") !=null)
            {
                kbfe.SetPropertyValue("f_gasprice", go.GetPropertyValue("f_gasprice").ToString(), false);
            }
            if(f_gasprice.Text != null && f_gasprice.Text != "")
            {
                kbfe.SetPropertyValue("f_newgasprice", f_gasprice.Text, false);
            }
            if(go.GetPropertyValue("f_repairnum")!=null)
            {
                kbfe.SetPropertyValue("f_repairnum", go.GetPropertyValue("f_repairnum").ToString(), false);
            }
            if (f_repairnum.Text != "" && f_repairnum.Text != null)
            {
                kbfe.SetPropertyValue("f_newrepairnum", f_repairnum.Text, false);
            }
            if (go.GetPropertyValue("f_amountmaintenance") != null)
            {
                kbfe.SetPropertyValue("f_amountmaintenance", go.GetPropertyValue("f_amountmaintenance").ToString(), false);
            }
            if (go.GetPropertyValue("f_totalcost") != null)
            {
                kbfe.SetPropertyValue("f_totalcost", go.GetPropertyValue("f_totalcost").ToString(), false);
            }
            if (f_totalcost.Text != "" && f_totalcost.Text != null)
            {
                kbfe.SetPropertyValue("f_newtotalcost", f_totalcost.Text, false);
            }
            if (go.GetPropertyValue("f_benqizhye") != null)
            {
                kbfe.SetPropertyValue("f_benqizhye", go.GetPropertyValue("f_benqizhye").ToString(), false);
            }
            if (f_benqizhye.Text != "" && f_benqizhye.Text != null)
            {
                kbfe.SetPropertyValue("f_newbenqizhye", f_benqizhye.Text, false);
            }
            if (go.GetPropertyValue("f_zhye")!= null)
            {
                kbfe.SetPropertyValue("f_zhye", go.GetPropertyValue("f_zhye").ToString(), false);
            }
            if (go.GetPropertyValue("f_deliverydate") != null)
            {
                kbfe.SetPropertyValue("f_deliverydate", Convert.ToDateTime( go.GetPropertyValue("f_deliverydate")), false);
            }
            if (f_deliverydate.SelectedDate != null)
            {
                kbfe.SetPropertyValue("f_newdeliverydate", Convert.ToDateTime( f_deliverydate.SelectedDate), false);
            }
            if (go.GetPropertyValue("f_deliverytime") != null)
            {
                kbfe.SetPropertyValue("f_deliverytime", Convert.ToDateTime(go.GetPropertyValue("f_deliverytime")), false);
            }
            if (f_deliverytime.GetSelectedValue() != null)
            {
                kbfe.SetPropertyValue("f_newdeliverytime", f_deliverytime.GetSelectedValue(), false);
            }
            if (f_changedate.SelectedDate != null)
            {
                kbfe.SetPropertyValue("f_changedate", f_changedate.SelectedDate, false);
            }
                kbfe.Name = "t_upkeep";
          kbfe.Completed += kbfe_Completed;

            kbfe.Save();

          
          
        }

       void kbfe_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction action = (from p in PageResourceLoad.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            action.Invoke();
            changebusy.IsBusy = false;
        }
      }


}