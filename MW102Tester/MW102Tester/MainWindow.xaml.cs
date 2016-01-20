using Card;
using service;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace mw102Tester
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void Test_Click(object sender, RoutedEventArgs e)
        {
            BtnTest.IsEnabled = false;
            HintList.Items.Clear();
            try
            {
                if (TestCard() == 0)
                    MessageBox.Show("卡片正常。");
                else
                    MessageBox.Show("卡片错误！");
            }
            finally
            {
                BtnTest.IsEnabled = true;
            }
        }

        private int TestCard()
        {
            MessageBox.Show("请先把卡片拔出，再插入。");
            //获取本地端口号，波特率
            short Port = short.Parse(Config.GetConfig("Port"));
            int Baud = int.Parse(Config.GetConfig("Baud"));
            int handle = MingHua.ic_init(Port, Baud);
            if (handle < 0)
            {
                HintList.Items.Add("错误：打开串口错误！");
                return -1;
            }
            else
            {
                HintList.Items.Add("打开串口正常！");
            }

            try
            {
                if(MingHua.chk_102(handle) != 0)
                {
                    HintList.Items.Add("错误：不是102卡！");
                    return -1;
                }
                //读代码保护区（从0E开始）
                byte[] buf = new byte[4];
                if(MingHua.srd_102(handle, 0, 0x0E, 4, buf) != 0)
                {
                    HintList.Items.Add("错误：读卡错误。");
                    return -1;
                }
                else
                {
                    HintList.Items.Add("读卡正常。");
                }
                //擦卡
                if(MingHua.ser_102(handle, 0, 0x0E, 4) == 0)
                {
                    if(MingHua.swr_102(handle, 0, 0x0E, 4, buf) == 0)
                    {
                        HintList.Items.Add("写卡正常。");
                        return 0;
                    }
                    else
                    {
                        HintList.Items.Add("错误：写卡错误。");
                        return -1;
                    }
                }
                else
                {
                    HintList.Items.Add("错误：写卡错误。");
                    return -1;
                }
            }
            finally
            {
                MingHua.ic_exit(handle);
            }
        }
    }
}
