using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.Controls;

namespace s2
{
	public partial class 折子打印提示界面 : CustomChildWindow
	{
		public 折子打印提示界面()
		{
			// 为初始化变量所必需
			InitializeComponent();
		}
		
		 private void OKButton_Click(object sender, RoutedEventArgs e)
        {
            this.ReturnValue = true;
            this.DialogResult = true;
        }

        private void CancelButton_Click(object sender, RoutedEventArgs e)
        {
            this.ReturnValue = false;
            this.DialogResult = false;
        }
	}
}