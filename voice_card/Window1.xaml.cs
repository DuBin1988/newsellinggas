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
using voice_card.service;
using System.Collections;
using voice_card.entity;
using System.Windows.Threading;
using System.Collections.ObjectModel;
using voice_card.helper;
using System.Data.SqlClient;

namespace voice_card
{
    public partial class Window1 : Window
    {
        WorkService workService;
        ObservableCollection<LineInfo> obColl = new ObservableCollection<LineInfo>();

        public ObservableCollection<LineInfo> ObColl
        { get { return obColl; } }

        public Window1()
        {
            InitializeComponent();
        }

        private void button2_Click(object sender, RoutedEventArgs e)
        {
            if (workService != null)
            {
                workService.Exit();
                workService.CloseServer();
            }
            this.Close();
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            this.button1.IsEnabled = false;
            this.button1.Content = "已运行";
            workService = new WorkService();
            workService.LoadDriver(obColl);
            DispatcherTimer dispatcherTimer = new DispatcherTimer();
            dispatcherTimer.Tick += new EventHandler(timer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0,0,0,0,100);
            dispatcherTimer.Start();
            //web启动
            workService.StartServer();
        }

        void timer_Tick(object sender, EventArgs e)
        {
            workService.Run();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            string server = "125.76.225.223,5223";
            string database = "yulingas";
            string user = "sa";
            string password = "af@QFG123";
            string connStr = "server=" + server + ";database =" + database + ";uid = " + user + ";pwd = " + password;
            SqlConnection conn = new SqlConnection(connStr);
         
            conn.Open();
          //  System.Text.Encoding iso = System.Text.Encoding.GetEncoding(this.Cinfo.Attrs["charset"]);
           // byte[] temp = Encoding.Convert(Encoding.Default, Encoding.Default, Encoding.Default.GetBytes(sql));
          //  sql = iso.GetString(temp);
            string sql = "insert into bsdual values(22);";
            SqlCommand cmd = new SqlCommand(sql, conn);
            cmd.ExecuteNonQuery();
            conn.Close();
        }
    }
}
