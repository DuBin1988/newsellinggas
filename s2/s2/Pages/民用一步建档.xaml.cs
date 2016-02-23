using Com.Aote.ObjectTools;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Net;
using Com.Aote.Behaviors;
using System.Linq;
using System.Text.RegularExpressions;

namespace Com.Aote.Pages
{
    public partial class 民用一步建档 : UserControl
    {
        public 民用一步建档()
        {

            // Required to initialize variables
            InitializeComponent();
        }
        private void f_address_MouseEnter(object sender, MouseEventArgs e)
        {
            f_address.Text = f_road.Text + f_districtname.Text + f_cusDom.Text + "号楼" + f_cusDy.Text + "单元" + f_cusFloor.Text + "楼" + f_apartment.Text;
        }
    }
}