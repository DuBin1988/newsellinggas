using Card;
using Com.Aote.Logs;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace CardHost
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private static ILog Log = LogManager.GetLogger(typeof(MainWindow));

        //托盘
        private NotifyIcon notifyIcon;

        public MainWindow()
        {
            InitializeComponent();
            //显示成托盘
            this.Visibility = Visibility.Hidden;
            this.notifyIcon = new NotifyIcon();
            this.notifyIcon.BalloonTipText = "卡服务程序";
            this.notifyIcon.Text = "卡服务程序正在运行......";
            this.notifyIcon.Icon = System.Drawing.Icon.ExtractAssociatedIcon(System.Windows.Forms.Application.ExecutablePath);
            this.notifyIcon.Visible = true;
            this.notifyIcon.ShowBalloonTip(1000);
            this.notifyIcon.MouseClick += new System.Windows.Forms.MouseEventHandler(notifyIcon_MouseClick);
            System.Windows.Forms.MenuItem item1 = new System.Windows.Forms.MenuItem("显示主界面");
            item1.Click += new EventHandler(item1_Click);
            System.Windows.Forms.MenuItem item2 = new System.Windows.Forms.MenuItem("退出");
            item2.Click += new EventHandler(item2_Click);
            System.Windows.Forms.MenuItem[] menuItems = new System.Windows.Forms.MenuItem[] { item1, item2 };
            this.notifyIcon.ContextMenu = new System.Windows.Forms.ContextMenu(menuItems);
            this.StateChanged += new EventHandler(MainWindow_StateChanged);
            this.Closing += new System.ComponentModel.CancelEventHandler(MainWindow_Closing);
            this.MouseDoubleClick += MainWindow_MouseDoubleClick;
            DownloadSupportingDLLS();
            StartService();
        }

        /// <summary>
        /// 测试例子
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void MainWindow_MouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            WebClient wc = new WebClient();
            wc.Encoding = System.Text.Encoding.UTF8;
            wc.DownloadStringCompleted += wc_DownloadStringCompleted;
            wc.DownloadStringAsync(new Uri("http://127.0.0.1:8000/ReadCard"));
        }

        private void wc_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            try
            {
                ShowMessage(e.Result.ToString());
            }
            catch(Exception ex)
            {
                ShowMessage(ex.ToString());
            }
        }

        /// <summary>
        /// 远程下载需要的动态库
        /// </summary>
        private void DownloadSupportingDLLS()
        {
            try
            {
                string ip = Config.GetConfig("RemoteIP");
                //从后台获取所有文件信息
                WebClient client = new WebClient();
                string str = client.DownloadString(ip + "/rs/card");
                string[] infos = str.Split('|');
                //对于每一个文件，检查时间是否一致，不一致则到后台获取文件
                //foreach (string item in infos)
                //{
                //    string[] items = item.Split(',');
                //    string fpath = items[0];
                //    log.Debug("find fpath is " + fpath);
                //    long ftime = long.Parse(items[1]);
                //    DateTime from1970 = new DateTime(1970, 1, 1, 8, 0, 0, DateTimeKind.Local);
                //    DateTime time = from1970.AddMilliseconds(ftime);
                //    //如果与本地文件时间不同，去后台获取文件
                //    int index = fpath.LastIndexOf('\\');
                //    string fname = fpath.Substring(index + 1);
                //    log.Debug("find fname is " + fname);
                //    DateTime local = File.GetLastWriteTime(fname);
                //    Log.Debug("time = " + time.ToString());
                //    Log.Debug("local = " + local.ToString());
                //    /*  if (local != time)
                //      {
                //          Log.Debug("load file " + fname);
                //          client.DownloadFile(ip + "/rs/card/" + fpath.Replace("\\", "^"), fname);
                //          File.SetLastWriteTime(fname, time);
                //      }*/
                //}
            }
            catch(Exception e)
            {
                ShowMessage("下载动态链接库出错。");
                Log.Debug("下载动态链接库出错。");
            }
        }

        private void StartService()
        {
            Uri httpUrl = new Uri("http://127.0.0.1:8000/");
            ServiceHost host = new ServiceHost(typeof(CardHost.CardService), httpUrl);
            host.AddServiceEndpoint(typeof(Card.ICardService), new WebHttpBinding(), "").Behaviors.Add(new WebHttpBehavior());
            host.Open();
        }

        void notifyIcon_MouseClick(object sender, System.Windows.Forms.MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                if (this.Visibility == Visibility.Visible)
                {
                    this.Visibility = Visibility.Hidden;
                }
                else
                {
                    this.Visibility = Visibility.Visible;
                    this.Activate();
                }
            }
        }

        void item1_Click(object sender, EventArgs e)
        {
            this.Visibility = Visibility.Visible;
            this.Activate();
        }
        void item2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        void MainWindow_StateChanged(object sender, EventArgs e)
        {
            if (this.WindowState == WindowState.Minimized)
            {
                this.Visibility = Visibility.Hidden;
            }
        }
        void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (System.Windows.MessageBox.Show("确定要退出卡服务程序吗？",
                                               "卡服务程序",
                                               MessageBoxButton.YesNo,
                                               MessageBoxImage.Question,
                                               MessageBoxResult.No) == MessageBoxResult.Yes)
            {
                this.notifyIcon.Visible = false;
            }
            else
            {
                e.Cancel = true;
            }
        }

        #region ShowMessage 在消息区显示信息
        public void ShowMessage(string msg)
        {
            message.Text += msg + "\n";
        }
        #endregion

        private void clearBtn_Click(object sender, RoutedEventArgs e)
        {
            this.message.Text = "";
        }

    }

}
