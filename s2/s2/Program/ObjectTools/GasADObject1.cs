using System;
using System.Collections.Generic;
using System.Net;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Com.Aote.ObjectTools
{
    //用气地址生成对象

    /**
     * 
     * 一单元                             
601	602	603
501	502	503
401	402	403
301	302	303
201	202	203
101	102	103
     * 二单元   
601	602	603
501	502	503
401	402	403
301	302	303
201	202	203
101	102	103
     * 
     */



    public class GasADObject1 : GeneralObject
    {

        public GasADObject1()
        {
            this.InitFinished += GasADObject_InitFinished;
        }

        private void GasADObject_InitFinished(object sender, RoutedEventArgs e)
        {
            this.PropertyChanged += GasADObject_PropertyChanged;
            State = State.Loaded;
        }


        private void GasADObject_PropertyChanged(object sender, System.ComponentModel.PropertyChangedEventArgs e)
        {
            //if (e.PropertyName.Equals("f_startbuild") || e.PropertyName.Equals("f_endbuild") ||
            //    e.PropertyName.Equals("f_startunit") || e.PropertyName.Equals("f_endunit") ||
            //    e.PropertyName.Equals("f_startlayer") || e.PropertyName.Equals("f_endlayer") ||
            //    e.PropertyName.Equals("f_startroom") || e.PropertyName.Equals("f_endroom"))
            //    Change();
        }

       // private string address = "#f_region##f_districtname##f_startbuild##f_startunit##f_startlayer#";

        //生成用气地址列表
        public void Invoke()
        {
            State = State.StartLoad;
            this.isBusy = true;
            //楼号
            string startbuild = this.GetPropertyValue("f_startbuild") + "";
            string endbuild = startbuild;
            //结束楼号不为空
            if (this.GetPropertyValue("f_endbuild") + "" != "")
            {
                endbuild = this.GetPropertyValue("f_endbuild") + "";
            }
            //单元
            string startunit = this.GetPropertyValue("f_startunit") + "";
            string endunit = startunit;
            if (this.GetPropertyValue("f_endunit") + "" != "")
            {
                endunit = this.GetPropertyValue("f_endunit") + "";
            }
            //楼层
            string startlayer = this.GetPropertyValue("f_startlayer") + "";
            string endlayer = startlayer;
            if (this.GetPropertyValue("f_endlayer") + "" != "")
            {
                endlayer = this.GetPropertyValue("f_endlayer") + "";
            }
            //房间号
            string startroom = this.GetPropertyValue("f_startroom") + "";
            string endroom = startroom;
            if (this.GetPropertyValue("f_endroom") + "" != "")
            {
                endroom = this.GetPropertyValue("f_endroom") + "";
            }
            //楼号列表
            List<string> builds = GetList(startbuild, endbuild);
            List<string> units = GetList(startunit, endunit);
            List<string> layers = GetList(startlayer, endlayer);
            List<string> rooms = GetList(startroom, endroom);
            ObjectList plans = new ObjectList();
            plans.WebClientInfo = this.WebClientInfo;
            plans.Name = Guid.NewGuid().ToString();
            try
            {
                foreach (string build in builds)
                {
                    foreach (string unit in units)
                    {
                        foreach (string layer in layers)
                        {
                            //每层多少室
                            foreach (string room in rooms)
                            {
                                GeneralObject go = new GeneralObject();
                                go.EntityType = this.EntityType;
                                go.WebClientInfo = this.WebClientInfo;
                                go.SetPropertyValue("f_road", this.GetPropertyValue("f_road") + "", true);
                                go.SetPropertyValue("f_districtname", this.GetPropertyValue("f_districtname") + "", true);
                                //设置楼号，有模式，按模式设置
                                string buildpattern = this.GetPropertyValue("f_buildpattern") + "";
                                string str = MatchPattern(build, buildpattern);
                                go.SetPropertyValue("f_cusDom", str, true);
                                //设置单元
                                string unitpattern = this.GetPropertyValue("f_unitpattern") + "";
                                str = MatchPattern(unit, unitpattern);
                                go.SetPropertyValue("f_cusDy", str, true);
                                //设置层
                                string layerpattern = this.GetPropertyValue("f_layerpattern") + "";
                                str = MatchPattern(layer, layerpattern);
                                go.SetPropertyValue("f_cusFloor", str, true);
                                //设置每层室
                                string roompattern = this.GetPropertyValue("f_roompattern") + "";
                                str = MatchPattern(room, roompattern);
                                go.SetPropertyValue("f_apartment", str, true);
                                //Change();
                                //string ad = this.GetPropertyValue("f_gasaddress") + "";
                                go.Name = "test";
                                plans.Add(go);
                            }
                        }
                    }
                }
            }catch(NullReferenceException e)
            {
                MessageBox.Show("输入的内容不符合要求");
            }
            finally
            {
                plans.Completed += plans_Completed;
                plans.Save();

            }
            
        }

        private void plans_Completed(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
        {
            (sender as ObjectList).Completed -= plans_Completed;
            State = State.Loaded;
            this.OnPropertyChanged("State");
            this.isBusy = false;
        }


        //根据输入的起止号码，产生字符串列表。如果输入内容为数字，按数字枚举；如果输入内容为字母，按字母枚举；也可规定枚举格式。
        private List<string> GetList(string start, string end)
        {
            List<string> result = new List<string>();
            //如果是数字，产生数字列表
            int iStart, iEnd;
            if (int.TryParse(start, out iStart) && int.TryParse(end, out iEnd))
            {
                for (int i = iStart; i <= iEnd; i++)
                {
                    result.Add(i + "");
                }
                return result;
            }

            //如果是单个大写字母，产生大写字母列表
            if (start.Length == 1 && end.Length == 1 && start[0] >= 'A' && start[0] <= 'Z' && end[0] >= 'A' && end[0] <= 'Z')
            {
                for (char i = start[0]; i <= end[0]; i++)
                {
                    result.Add(i + "");
                }
                return result;
            }
            //试试写纯汉字的楼栋
            if (start.Equals(end) && Regex.IsMatch(start, @"[\u4e00-\u9fbb]+$") && Regex.IsMatch(end, @"[\u4e00-\u9fbb]+$"))
            {
                result.Add(start);
                return result;
            }
            return null;
        }

        //产生模式匹配结果，如果模式不为空，替换模式中的X，否则返回原串 
        private string MatchPattern(string str, string pattern)
        {
            if (pattern == null || pattern == "")
            {
                return str;
            }
            str = pattern.Replace("X", str);
            return str;
        }

        //生成地址例子
        public void Change()
        {


            try
            {
                //楼号
                string startbuild = this.GetPropertyValue("f_startbuild") + "";
                string endbuild = startbuild;
                //结束楼号不为空
                if (this.GetPropertyValue("f_endbuild") + "" != "")
                {
                    endbuild = this.GetPropertyValue("f_endbuild") + "";
                }
                //单元
                string startunit = this.GetPropertyValue("f_startunit") + "";
                string endunit = startunit;
                if (this.GetPropertyValue("f_endunit") + "" != "")
                {
                    endunit = this.GetPropertyValue("f_endunit") + "";
                }
                //楼层
                string startlayer = this.GetPropertyValue("f_startlayer") + "";
                string endlayer = startlayer;
                if (this.GetPropertyValue("f_endlayer") + "" != "")
                {
                    endlayer = this.GetPropertyValue("f_endlayer") + "";
                }
                //房间号
                string startroom = this.GetPropertyValue("f_startroom") + "";
                string endroom = startroom;
                if (this.GetPropertyValue("f_endroom") + "" != "")
                {
                    endroom = this.GetPropertyValue("f_endroom") + "";
                }

                List<string> builds = GetList(startbuild, endbuild);
                List<string> units = GetList(startunit, endunit);
                List<string> layers = GetList(startlayer, endlayer);
                List<string> rooms = GetList(startroom, endroom);
                this.SetPropertyValue("f_n", builds.Count * units.Count * layers.Count * rooms.Count + "", true);
            }
            catch
            {
                this.SetPropertyValue("f_n", "", true);
            }
            State = State.Loaded;
        }
    }
}
