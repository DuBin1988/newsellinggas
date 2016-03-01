using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using System.Windows.Controls.Primitives;
using Com.Aote.ObjectTools;
using System.Net;
using System.Collections.Generic;
using System.Json;

namespace Com.Aote.Pages
{
    public partial class 物联表远传记录 : UserControl
	{
        PagedList list = new PagedList();

        public 物联表远传记录()
		{
			// Required to initialize variables
			InitializeComponent();

		}

        #region 查询按钮处理过程
        // 查询按钮处理过程
        private void dansearchbutton_Click(object sender, RoutedEventArgs e)
        {
            /* 
             * //beginDate=&customerCode=&endDate=&meterCode=6152500083&operateType=&order=desc&page=1&rows=15&sort=create_date&telecommAddress=&terminalCode=
             * http://125.64.74.13:8000
             * /cmr/auto/remote-operate!remoteOperateRecordList.action?beginDate=&customerCode=100100&endDate=&meterCode=&operateType=&order=desc&page=1&rows=15&sort=create_date&telecommAddress=&terminalCode= 
             */
            if (f_beginDate.Text==null)
            {
            }else{
            }
            String uil = "beginDate=" + f_beginDate.Text + "&customerCode=" + f_userid.Text + "&endDate=" + f_endDate.Text + "&meterCode=" + f_meternumber.Text + "&operateType=" + f_operateType.Text + "&order=desc&page=1&rows=30&sort=create_date&telecommAddress=" + f_telecommAddress.Text + "&terminalCode=" + "";
            // 调用服务
            WebClientInfo wci = Application.Current.Resources["ycurl"] as WebClientInfo;
            string uri = wci.BaseAddress + uil + ""; //?uuid=" + System.Guid.NewGuid().ToString();
            WebClient client = new WebClient();
            client.DownloadStringCompleted += dansearch_DownloadStringCompleted;
            client.DownloadStringAsync(new Uri(uri));

            

        }

        void dansearch_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            //有错误
            if (e.Error != null)
            {
                MessageBox.Show(e.Error.Message);
            }
            else
            {
                //把数据转换成JSON
                JsonObject obj = JsonValue.Parse(e.Result) as JsonObject;
                JsonArray items = obj["rows"] as JsonArray;
                daninfos.ItemsSource = list;
                if (list.Size != 0)
                    list.Clear();
                foreach (JsonObject json in items)
                {
                    GeneralObject go = new GeneralObject();
                    try
                    {
                        go.EntityType = "t_";
                        go.SetPropertyValue("id", json["id"], false);//用户编号
                        go.SetPropertyValue("remote_key", json["remote_key"], false);//用户编号
                        go.SetPropertyValue("terminal_code", json["terminal_code"], false);//用户编号
                        go.SetPropertyValue("meter_code", json["meter_code"], false);//用户编号
                        go.SetPropertyValue("operate_type", json["operate_type"], false);//用户编号
                        go.SetPropertyValue("send_data", json["send_data"], false);//用户编号
                        go.SetPropertyValue("receive_data", json["receive_data"], false);//用户编号
                        go.SetPropertyValue("state", json["state"], false);//用户编号
                        go.SetPropertyValue("result", json["result"], false);//用户编号
                        go.SetPropertyValue("operator1",json["operator"], false);//用户编号
                        go.SetPropertyValue("des", json["des"], false);//用户编号
                        go.SetPropertyValue("create_date", json["create_date"], false);//用户编号
                        go.SetPropertyValue("company_code", json["company_code"], false);//用户编号
                        go.SetPropertyValue("telecomm_address", json["telecomm_address"], false);//用户编号
                        go.SetPropertyValue("customer_code",json["customer_code"], false);//用户编号
                        list.Add(go);
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show(ex.Message);
                        return;
                    }
                }
            }
        }

        #endregion

    }

}