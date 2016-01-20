using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace s2
{
	public partial class 民用收费 : UserControl
	{
		public 民用收费()
		{
			// 为初始化变量所必需
			InitializeComponent();
		}

        private void applys_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            bcsk.Text = "0";
        }
	}
}