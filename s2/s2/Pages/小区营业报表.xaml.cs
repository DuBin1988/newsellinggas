using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.Pages
{
	public partial class 小区营业报表 : UserControl
	{
		public 小区营业报表()
		{
			// Required to initialize variables
			InitializeComponent();
		}
        private void Button_Click(object sender, RoutedEventArgs e)
        {
            detail.Visibility = Visibility.Visible;
        }
	}
}