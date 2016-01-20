using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Com.Aote.ObjectTools;
using s2.Pages;



namespace Com.Aote.Pages
{
	public partial class 小区安检查询 : UserControl
	{
		public 小区安检查询()
		{
			// 为初始化变量所必需
			InitializeComponent();
		}

        private void picture_MouseEnter(object sender, MouseEventArgs e)
        {
            Image image = sender as Image;
            if (image.Source == null)
                return;
            bigPic.Source = image.Source;
        }

        /// <summary>
        /// 安检单列表选择改变
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void areafiles_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            GeneralObject item = areafiles.SelectedItem as GeneralObject;
            if (item == null)
                return;
            GeneralObject go = new GeneralObject();
            go.WebClientInfo = Application.Current.Resources["dbclient"] as WebClientInfo;
            go.EntityType = "T_INSPECTION";
            if (!item.GetPropertyValue("CONDITION").Equals("未检"))
            {
                go.Path = "one/select distinct t from T_INSPECTION t left join fetch t.LINES left join fetch t.CHECKPLAN_ID where t.id ='" + item.GetPropertyValue("id").ToString() + "'";
                go.DataLoaded += go_DataLoaded;
                //if (!item.GetPropertyValue("CONDITION").Equals("正常"))
                //    userfile.IsEnabled = false;
                //else
                //    userfile.IsEnabled = true;
                go.Load();
            }
            else
            {
                userfile.DataContext = null;
                ClearAll();
                userfile.IsEnabled = false;
            }
        }

        void go_DataLoaded(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            (sender as GeneralObject).DataLoaded -= go_DataLoaded;
            transformData(sender as GeneralObject);
            userfile.DataContext = sender;
        }

        private void transformData(GeneralObject go)
        {
            ClearAll();

            if (go == null)
                return;

            PostUITask(go);
        }

        private void ClearAll()
        {
            //清除以前的选择
            ClearChecks();
            bigPic.Source = null;
            CurrentArchiveAddress.Text = "";
            WARM_OTHER.Text = "";
            JB_METER_NAME_OTHER.Text = "";
            IC_METER_NAME_OTHER.Text = "";
            WARM.SelectedItem = null;
            IC_METER_NAME.SelectedItem = null;
            JB_METER_NAME.SelectedItem = null;
        }

        private void ClearChecks()
        {
            Panel[] panels = { MeterDefectsPane,  PlumbingDefectsPane, PlumbingProofPane, PlumbingPressurePane, PlumbingMeterValvePane, PlumbingCookerValvePane, PlumbingAutomaticValvePane, PlumbingPipePane, PlumbingLeakagePane, 
                                 CookerPipePane, BoilerPipePane, BoilerDefectsPane, WHEDefectsPane, precautionCheckPane };
            foreach (Panel p in panels)
            {
                foreach (UIElement element in p.Children)
                {
                    if (element is CheckBox && !(element as CheckBox).Content.ToString().Equals("正常") && !(element as CheckBox).Content.ToString().Equals("无"))
                    {
                        if (element == cbRIGIDITYLeakage || element == cbRIGIDITYNormal || element == cbPressureNormal || element == cbPressureAbnormal ||
                            element == cbLEAKAGE_COOKER || element == cbLEAKAGE_BOILER || element == cbLEAKAGE_HEATER || element == cbLEAKAGE_NOTIFIED)
                            continue;
                        (element as CheckBox).IsChecked = false;
                    }
                    if (element is RadioButton)
                        (element as RadioButton).IsChecked = false;
                }
            }
        }


        private void PostUITask(GeneralObject go)
        {
            if (go.GetPropertyValue("CONDITION").ToString().Equals("正常"))
            {
                //供暖方式
                ObjectCollection oc = this.Resources["WARM"] as ObjectCollection;
                bool found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("WARM").ToString()))
                    {
                        found = true;
                        WARM.SelectedItem = pair;
                    }
                }
                if (!found)
                {
                    WARM_OTHER.Text = go.GetPropertyValue("WARM").ToString();
                    WARM.SelectedIndex = oc.Count - 1;
                }

                //基表厂家型号
                oc = this.Resources["JB_METER_NAME"] as ObjectCollection;
                found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("JB_METER_NAME").ToString()))
                    {
                        JB_METER_NAME.SelectedItem = pair;
                        found = true;
                    }
                }
                if (!found)
                {
                    JB_METER_NAME_OTHER.Text = go.GetPropertyValue("JB_METER_NAME").ToString();
                    JB_METER_NAME.SelectedIndex = oc.Count - 1;
                }

                //IC卡表厂家型号
                oc = this.Resources["IC_METER_NAME"] as ObjectCollection;
                found = false;
                foreach (Pair pair in oc)
                {
                    if (pair.CName.Equals(go.GetPropertyValue("IC_METER_NAME").ToString()))
                    {
                        found = true;
                        IC_METER_NAME.SelectedItem = pair;
                    }
                }
                if (!found)
                {
                    IC_METER_NAME_OTHER.Text = go.GetPropertyValue("IC_METER_NAME").ToString();
                    go.SetPropertyValue("IC_METER_NAME", (oc.ElementAt(oc.Count - 1) as Pair).Code, true, true);
                    IC_METER_NAME.SelectedIndex = oc.Count - 1;
                }

                ObjectList lines = go.GetPropertyValue("LINES") as ObjectList;
                //不存在隐患
                if (lines == null)
                    return;

                foreach (GeneralObject line in lines)
                {
                    String EQUIPMENT = line.GetPropertyValue("EQUIPMENT") as string;
                    String CONTENT = line.GetPropertyValue("CONTENT") as string;
                    if (EQUIPMENT.Equals("安全隐患"))
                        CheckCheckBox(CONTENT, precautionCheckPane);
                    else if (EQUIPMENT.Equals("燃气表"))
                        CheckCheckBox(CONTENT, MeterDefectsPane);
                    else if (EQUIPMENT.Equals("立管"))
                        CheckPlumbingBox(CONTENT, PlumbingDefectsPane);
                    else if (EQUIPMENT.Equals("阀门表前阀"))
                        CheckCheckBox(CONTENT, PlumbingMeterValvePane);
                    else if (EQUIPMENT.Equals("阀门灶前阀"))
                        CheckCheckBox(CONTENT, PlumbingCookerValvePane);
                    else if (EQUIPMENT.Equals("阀门自闭阀"))
                        CheckCheckBox(CONTENT, PlumbingAutomaticValvePane);
                    else if (EQUIPMENT.Equals("户内管"))
                        CheckCheckBox(CONTENT, PlumbingPipePane);
                    else if (EQUIPMENT.Equals("灶具软管"))
                        CheckCheckBox(CONTENT, CookerPipePane);
                    else if (EQUIPMENT.Equals("热水器软管"))
                        CheckCheckBox(CONTENT, BoilerPipePane);
                    else if (EQUIPMENT.Equals("热水器安全隐患"))
                        CheckCheckBox(CONTENT, BoilerDefectsPane);
                    else if (EQUIPMENT.Equals("壁挂锅炉安全隐患"))
                        CheckCheckBox(CONTENT, WHEDefectsPane);
                }

                //提取用户档案地址
                String card_id = go.GetPropertyValue("CARD_ID") as string;
                if (IsNullOrEmpty(card_id))
                    return;
                WebClient wc = new WebClient();
                wc.DownloadStringCompleted += wc_GetUserProfileCompleted;
                wc.DownloadStringAsync(new Uri(go.WebClientInfo.BaseAddress + "/one/from T_IC_USERFILE where CARD_ID='" + card_id + "'"));
            }

        }

        private void wc_GetUserProfileCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                JsonObject item = JsonValue.Parse(e.Result) as JsonObject;
                String ROAD = null;
                String UNIT_NAME = null;
                String CUS_DOM = null;
                String CUS_DY = null;
                String CUS_FLOOR = null;
                String CUS_ROOM = null;
                if (item.ContainsKey("ROAD"))
                    ROAD = item["ROAD"];
                if (item.ContainsKey("UNIT_NAME"))
                    UNIT_NAME = item["UNIT_NAME"];
                if (item.ContainsKey("CUS_DOM"))
                    CUS_DOM = item["CUS_DOM"];
                if (item.ContainsKey("CUS_DY"))
                    CUS_DY = item["CUS_DY"];
                if (item.ContainsKey("CUS_FLOOR"))
                    CUS_FLOOR = item["CUS_FLOOR"];
                if (item.ContainsKey("CUS_ROOM"))
                    CUS_ROOM = item["CUS_ROOM"];
                CurrentArchiveAddress.Text = String.Format("{0}\t{1}\t{2}-{3}-{4}-{5}",
                    new String[] { ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM });
            }
        }

        private void CheckPlumbingBox(String CONTENT, Panel panel)
        {
            foreach (UIElement element in panel.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.Content.ToString().Equals(CONTENT))
                        aBox.IsChecked = true;
                }
                if (element is RadioButton)
                {
                    RadioButton aBox = element as RadioButton;
                    if (aBox.Content.ToString().Equals(CONTENT))
                    {
                        aBox.IsChecked = true;
                        cbEroded.IsChecked = true;
                    }
                }
            }
        }

        private void CheckCheckBox(String CONTENT, Panel panel)
        {
            foreach (UIElement element in panel.Children)
            {
                if (element is CheckBox)
                {
                    CheckBox aBox = element as CheckBox;
                    if (aBox.Content.ToString().Equals(CONTENT))
                        aBox.IsChecked = true;
                }
            }
        }

        private Boolean IsNullOrEmpty(String value)
        {
            return value == null || value.Trim().Length == 0;
        }

	}
}