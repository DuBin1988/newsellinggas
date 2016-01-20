using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
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
using Card;

namespace WpfApplication3
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        #region 银川天佳动态库导入
        //读卡，标准接口
        [DllImport("YCTJ_ZQ.dll", EntryPoint = "ReadICCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ReadICCard(int COMID, int COMHZ, ref string ICId, ref int ICType, ref double ICCSpare, ref int GASCOUNT,
            ref int CusType,ref double ICUsed,ref double ICMSpare,ref int ICNum);
        //写新卡，标准接口
        [DllImport("YCTJ_ZQ.dll", EntryPoint = "WriteICCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int WriteICCard(int COMID,int COMHZ,string ICId,int OPCODE,int GASCOUNT,double ICCSpare,
            int ICType,string ICMark);
        //测卡，标准接口
        [DllImport("YCTJ_ZQ.dll", EntryPoint = "CheckOwnCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int CheckOwnCard(int COMID,int COMHZ);
        //格式化卡，标准接口
        [DllImport("YCTJ_ZQ.dll", EntryPoint = "ClearAllCard", CallingConvention = CallingConvention.StdCall)]
        public static extern int ClearAllCard(int COMID,int COMHZ);
        #endregion

        private void ClearCard(object sender, RoutedEventArgs e)
        {
            long rs = ClearAllCard(0,9600);
            MessageBox.Show("clear card end rs=" + rs);
        }

        private void InitCard(object sender, RoutedEventArgs e)
        {
            int rs = WriteICCard(0, 9600, "1234567890", 7, 1, 100, 0, "");  //卡号为10位
            MessageBox.Show("init card end rs=" + rs);
        }

        private void ReadCard(object sender, RoutedEventArgs e)
        {
            string ICId = "";
            int ICType = 0;
            double ICCSpare = 0;
            int GASCOUNT = 0;
            int CusType = 0;
            double ICUsed = 0;
            double ICMSpare = 0;
            int ICNum = 0;
            long rs = ReadICCard(0, 9600,ref ICId,ref ICType,ref ICCSpare,ref GASCOUNT,ref CusType,ref ICUsed,ref ICMSpare,ref ICNum);
            MessageBox.Show("read card end rs=" + rs + ",cardid=" + ICId + ",ql=" + ICCSpare + ",cs=" + GASCOUNT);
        }

        private void SellGas(object sender, RoutedEventArgs e)
        {
            int rs = WriteICCard(0, 9600, "1234567890", 2, 2, 50, 0, "");  //卡号为10位
            MessageBox.Show("init card end rs=" + rs);
        }

        private void CheckCard(object sender, RoutedEventArgs e)
        {
            int rs = CheckOwnCard(0, 9600);
            MessageBox.Show("check card end rs="+rs);
            

        }

    }
}
