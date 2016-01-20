using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Forms;
using service;

namespace LocalService
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //托盘
        private NotifyIcon notifyIcon;

        //服务
        private WorkService service = new WorkService();

        public MainWindow()
        {
            InitializeComponent();
            //显示成托盘
            /*this.Visibility = Visibility.Hidden;
            this.notifyIcon = new NotifyIcon();
            this.notifyIcon.BalloonTipText = "托盘应用程序正在运行！";
            this.notifyIcon.Text = "托盘应用程序！";
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
            this.Closing += new System.ComponentModel.CancelEventHandler(MainWindow_Closing);*/
            //启动服务
            service.StartServer();
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
            if (System.Windows.MessageBox.Show("确定要退出托盘应用程序吗？",
                                               "托盘应用程序",
                                               MessageBoxButton.YesNo,
                                               MessageBoxImage.Question,
                                               MessageBoxResult.No) == MessageBoxResult.Yes)
            {
                this.notifyIcon.Visible = false;
                //停止服务
                service.CloseServer();
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
    }
}
