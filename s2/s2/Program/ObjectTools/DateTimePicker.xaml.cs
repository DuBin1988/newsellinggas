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

namespace Com.Aote.ObjectTools
{
	public partial class DateTimePicker : UserControl
	{
		#region SelectedDateTimeStr dependency property
        public String SelectedDateTimeStr
        {
            get
            {
                return (String)GetValue(SelectedDateTimeStrProperty);
            }
            set
            {
                SetValue(SelectedDateTimeStrProperty, value);
            }
        }

        public static readonly DependencyProperty SelectedDateTimeStrProperty =
            DependencyProperty.Register("SelectedDateTimeStr",
            typeof(String),
            typeof(DateTimePicker),
            new PropertyMetadata(null, new PropertyChangedCallback(SelectedDateTimeStrChanged)));

        private static void SelectedDateTimeStrChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e)
        {
            DateTimePicker me = sender as DateTimePicker;

            if (me != null && e.NewValue != null)
            {
                me.DatePicker.SelectedDate = DateTime.ParseExact(e.NewValue as string, "yyyy-MM-dd HH:mm:ss", null);
                me.TimePicker.Value = DateTime.ParseExact(e.NewValue as string, "yyyy-MM-dd HH:mm:ss", null);
            }
        }
        #endregion

        #region SelectedDateTimeStr dependency property
        public DateTime? SelectedDateTime
		{
			get
			{
				return (DateTime?)GetValue(SelectedDateTimeProperty);
			}
			set
			{
				SetValue(SelectedDateTimeProperty, value);
			}
		}
		
		public static readonly DependencyProperty SelectedDateTimeProperty =
			DependencyProperty.Register("SelectedDateTime",
			typeof(DateTime?),
			typeof(DateTimePicker),
			new PropertyMetadata(null, new PropertyChangedCallback(SelectedDateTimeChanged)));

		private static void SelectedDateTimeChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e)
		{
			DateTimePicker me = sender as DateTimePicker;

			if (me != null)
			{
				me.DatePicker.SelectedDate = (DateTime?)e.NewValue;
				me.TimePicker.Value = (DateTime?)e.NewValue;
			}
		}

		#endregion

		public DateTimePicker()
		{
			InitializeComponent();

			DatePicker.SelectedDateChanged += new EventHandler<SelectionChangedEventArgs>(DatePicker_SelectedDateChanged);
			TimePicker.ValueChanged += new RoutedPropertyChangedEventHandler<DateTime?>(TimePicker_ValueChanged);
		}

		#region Event handlers

		private void TimePicker_ValueChanged(object sender, RoutedPropertyChangedEventArgs<DateTime?> e)
		{
			if (DatePicker.SelectedDate != TimePicker.Value)
			{
				DatePicker.SelectedDate = TimePicker.Value;
			}

			if (SelectedDateTime != TimePicker.Value)
			{
				SelectedDateTime = TimePicker.Value;
                try
                {
                    SelectedDateTimeStr = TimePicker.Value.Value.ToString("yyyy-MM-dd HH:mm:ss");
                }
                catch (Exception ex)
                {
                    SelectedDateTimeStr = "";
                }
            }
		}

		private void DatePicker_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
		{
			// correct the new date picker date by the time picker's time
			if (DatePicker.SelectedDate.HasValue && TimePicker.Value.HasValue)
			{
				// get both values
				DateTime datePickerDate = DatePicker.SelectedDate.Value;
				DateTime timePickerDate = TimePicker.Value.Value;
					
				// compare relevant parts manually
				if (datePickerDate.Hour != timePickerDate.Hour
					|| datePickerDate.Minute != timePickerDate.Minute 
					|| datePickerDate.Second != timePickerDate.Second)
				{
					// correct the date picker value
					DatePicker.SelectedDate = new DateTime(datePickerDate.Year, 
						datePickerDate.Month, 
						datePickerDate.Day, 
						timePickerDate.Hour, 
						timePickerDate.Minute, 
						timePickerDate.Second);

					// return, because this event handler will be executed a second time
					return;
				}
			}

			// now transfer the date picker's value to the time picker
			// and dependency property
			if (TimePicker.Value != DatePicker.SelectedDate)
			{
				TimePicker.Value = DatePicker.SelectedDate;
			}

			if (SelectedDateTime != DatePicker.SelectedDate)
			{
				SelectedDateTime = DatePicker.SelectedDate;
                try
                {
                    SelectedDateTimeStr = DatePicker.SelectedDate.Value.ToString("yyyy-MM-dd HH:mm:ss");
                }
                catch (Exception ex)
                {
                    SelectedDateTimeStr = "";
                }
            }
		}

		#endregion
	}
}
