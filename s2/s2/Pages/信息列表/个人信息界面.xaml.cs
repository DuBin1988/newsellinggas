﻿using System;
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
using Com.Aote.Utils;
using Com.Aote.ObjectTools;

namespace s2.Pages
{
    public partial class 个人信息界面 : CustomChildWindow
    {
        public 个人信息界面()
        {
            InitializeComponent();
        }

        private void OKButton_Click(object sender, RoutedEventArgs e)
        { 
            this.DialogResult = true;
        }

    }
}

