﻿using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;

namespace Com.Aote.Pages
{
    public partial class 批量上传档案信息 : UserControl
	{

        public 批量上传档案信息()
		{
			// Required to initialize variables
			InitializeComponent();
            Clipboard.Text = "";
		}

    }
}