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
	public partial class 民用建档发卡 : UserControl
	{
		public 民用建档发卡()
		{
            
			// Required to initialize variables
			InitializeComponent();
		} 
           private void f_address_MouseEnter(object sender, MouseEventArgs e)
           {
               f_address.Text = f_area.Text + f_districtname.Text + f_cusDom.Text + f_cusDy.Text + f_cusFloor.Text + f_apartment.Text;
           }
	}
}