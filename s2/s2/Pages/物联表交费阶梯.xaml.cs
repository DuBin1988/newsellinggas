using Com.Aote.ObjectTools;
using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Linq;
using Com.Aote.Utils;
using System.Text.RegularExpressions;
using Com.Aote.Behaviors;

namespace Com.Aote.Pages
{
    public partial class 物联表交费阶梯 : UserControl
    {
        String userid = "";
        public 物联表交费阶梯()
        {
            // Required to initialize variables
            InitializeComponent();
        }

        #region SaveClick 提交按钮操作过程
        // 提交数据到后台服务器
        private void SaveClick(object sender, RoutedEventArgs e)
        {
            //显示正在工作
            busy.IsBusy = true;
            userid = f_userid.Text;
            //取出登陆用户id，后台根据id查找登陆用户放入分公司等信息
            GeneralObject loginUser = (GeneralObject)FrameworkElementExtension.FindResource(this, "LoginUser");
            if (loginUser == null)
            {
                MessageBox.Show("无法获取当前登陆用户信息,请重新登陆后操作!");
                return;
            }
            string loginid = (string)loginUser.GetPropertyValue("id");
            //获取基础地址
            WebClientInfo wci = (WebClientInfo)Application.Current.Resources["server"];
            // 提交
            string str = wci.BaseAddress + "/sell/" + f_userid.Text + "/" + shoukuan.Text + "/"
                + f_zhinajin.Text + "/" + f_payment.SelectedValue + "/" + loginid;
            Uri uri = new Uri(str);
            WebClient client = new WebClient();
            client.DownloadStringCompleted += client_DownloadStringCompleted;
            client.DownloadStringAsync(uri);
        }

        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            //busy.IsBusy = false;
            WebClient client = sender as WebClient;
            client.DownloadStringCompleted -= client_DownloadStringCompleted;

            // 没有出错
            if (e.Error == null)
            {
                try
                {
                    String retid = e.Result as String;
                    if (!"noid".Equals(retid) && userid != null)
                    {
                        //产生要发送后台的JSON串
                        WebClientInfo wci1 = Application.Current.Resources["server"] as WebClientInfo;
                        string uri1 = wci1.BaseAddress + "/iesgas/gascz/comand";
                        WebClient client1 = new WebClient();
                        client1.UploadStringCompleted += client1_UploadStringCompleted;
                        client1.UploadStringAsync(new Uri(uri1), ("[{customer_code:\"" + userid + "\",id:\"" + retid + "\"}]"));
                    }

                }
                catch (Exception ex) { }
                // 调用打印
                MessageBoxResult mbr = MessageBox.Show("是否打印", "提示", MessageBoxButton.OKCancel);
                if (mbr == MessageBoxResult.OK)
                {
                    print1.Print();
                    print1.Completed += print_Completed;

                }
                else
                {
                    GeneralObject kbfee = (GeneralObject)(from r in loader.Res where r.Name.Equals("kbfee") select r).First();
                    BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction111") select p).First() as BatchExcuteAction;
                    save.Invoke();
                    kbfee.New();
                    busy.IsBusy = false;
                }

            }
            else
            {
                // 提示出错
                MessageBox.Show("连接服务器失败，请重试！如果继续失败，请联系系统管理员。");
                // 清除界面数据
                GeneralObject kbfee = (GeneralObject)(from r in loader.Res where r.Name.Equals("kbfee") select r).First();
                kbfee.New();
                busy.IsBusy = false;
            }
        }

        void client1_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            busy.IsBusy = false;
        }
        #endregion

        private void print_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            GeneralObject kbfee = (GeneralObject)(from r in loader.Res where r.Name.Equals("kbfee") select r).First();
            BatchExcuteAction save = (from p in loader.Res where p.Name.Equals("SaveAction111") select p).First() as BatchExcuteAction;
            save.Invoke();
            kbfee.New();
            busy.IsBusy = false;
        }

    }
}