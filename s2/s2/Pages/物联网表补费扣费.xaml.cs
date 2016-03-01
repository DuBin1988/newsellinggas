using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.Behaviors;
using Com.Aote.ObjectTools;
using System.Linq;
using System.Net;

namespace s2
{
    public partial class 物联网表补费扣费 : UserControl
	{
        String userid = "";
        String f_deliverydate_tb = "";
        String f_grossproceeds = "";
        String f_sgoperator = "";
        String f_payfeetype = "";
        public 物联网表补费扣费()
		{
			// Required to initialize variables
			InitializeComponent();
		}

        #region SaveClick 提交按钮操作过程
        // 提交数据到后台服务器
        private void SaveClick(object sender, RoutedEventArgs e)
        {
            //取出登陆用户id，后台根据id查找登陆用户放入分公司等信息
            GeneralObject kbfee = cbgasmanege.DataContext as GeneralObject;
            userid = kbfee.GetPropertyValue("f_userid")+"";
            f_deliverydate_tb = kbfee.GetPropertyValue("f_deliverydate_tb") + "";
            f_grossproceeds = kbfee.GetPropertyValue("f_grossproceeds") + "";
            f_sgoperator = kbfee.GetPropertyValue("f_sgoperator") + "";
            f_payfeetype = kbfee.GetPropertyValue("f_payfeetype") + "";
            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction") select p).First() as BatchExcuteAction;
            save.Invoke();
            save.Completed += save_Completed;
        }

        void save_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            BatchExcuteAction save = sender as BatchExcuteAction;
            save.Completed -= save_Completed;

            //产生要发送后台的JSON串
            WebClientInfo wci1 = Application.Current.Resources["server"] as WebClientInfo;
            string uri1 = wci1.BaseAddress + "/iesgas/gasbk/comand";
            WebClient client1 = new WebClient();
            client1.UploadStringCompleted += client1_UploadStringCompleted;
            client1.UploadStringAsync(new Uri(uri1), ("[{customer_code:\"" + userid +
                                                 "\",f_deliverydate_tb:\"" + f_deliverydate_tb +
                                                 "\",f_grossproceeds:\"" + f_grossproceeds +
                                                 "\",f_sgoperator:\"" + f_sgoperator +
                                                 "\",f_payfeetype:\"" + f_payfeetype +
                                                 "\"}]"));
        }
        
        void client1_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            
        }
        #endregion

	}
}