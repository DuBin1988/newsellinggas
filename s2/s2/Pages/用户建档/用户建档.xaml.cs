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

namespace s2
{
    public partial class 用户建档 : UserControl
    {
        public 用户建档()
        {
            InitializeComponent();
        }
        

        private void f_address_MouseEnter(object sender, RoutedEventArgs e)
        {
            f_address.Text = f_districtname.Text + "#" + f_cusDom.Text + "-" + f_cusDy.Text + "-" + f_cusFloor.Text + "-" + f_apartment.Text;
        }
    }
}
