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
using Com.Aote.Logs;
using s2;

namespace Com.Aote.Pages
{
    public partial class 用气地址模式二 : UserControl
    {
        
        public 用气地址模式二()
        {
            InitializeComponent();
        }

        private void preview_Click(object sender, RoutedEventArgs e)
        {
            if ((apply.DataContext as GasADObject2).HasErrors)
                return;
            previewButton.IsEnabled = false;
            //先去清空临时表
            WebClientInfo wi = Application.Current.Resources["dbclient"] as WebClientInfo;
            WebClient wc = new WebClient();
            wc.UploadStringCompleted += deleteTmpCompleted;
            wc.UploadStringAsync(new Uri(wi.BaseAddress), "[{operator:'sql',data:'delete from t_gasaddresstemp'}]");
        }

        private void deleteTmpCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                //再保存数据到临时表
                Com.Aote.ObjectTools.GasADObject2 go2 = apply.DataContext as Com.Aote.ObjectTools.GasADObject2;
                go2.PropertyChanged += go2_PropertyChanged;
                go2.Invoke();
            }
            else
            {
                MessageBox.Show("后台提取数据出错！");
                previewButton.IsEnabled = true;
                save.IsEnabled = false;
                search.IsEnabled = false;
                cancel.IsEnabled = false;
            }
        }


        void go2_PropertyChanged(object sender, System.ComponentModel.PropertyChangedEventArgs e)
        {
            if(e.PropertyName.Equals("State"))
            {
                GasADObject2 obj = sender as GasADObject2;
                if(obj.State.Equals(State.Loaded))
                {
                    WebClientInfo wci = App.Current.Resources["dbclient"] as WebClientInfo;
                    applyslist.LoadOnPathChanged = false;
                    applyslist.Path = "sql";
                    applyslist.Names = "state,road,districtname,cusDy,cusDom,cusFloor,apartment,placeholder";
                    String sql = @"select * from (select case when a.f_districtname is null then '新增' when b.f_districtname is null then   '已建'  else  '重复 '  end  state, 
                                    isnull(a.f_road, b.f_road) road, 
                                    isnull(a.f_districtname, b.f_districtname) districtname,
                                    isnull( a.f_cusDy, b.f_cusDy) cusDy,
                                    isnull( a.f_cusDom, b.f_cusDom) cusDom,
                                    isnull(a.f_cusFloor, b.f_cusFloor) cusFloor,
                                    isnull(a.f_apartment, b.f_apartment) apartment
                                     from 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESS where f_districtname='{0}') a
                                    full join 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESSTEMP) b
                                    on a.f_road = b.f_road and a.f_districtname = b.f_districtname and a.f_cusDy=b.f_cusDy and a.f_cusDom=b.f_cusDom and a.f_cusFloor = b.f_cusFloor and a.f_apartment=b.f_apartment ) t ";
                        
                       string orderBy ="             order by len(road), road, len(districtname), districtname,len(cusDom), cusDom,len(cusDy), cusDy,len(cusFloor), cusFloor,len(apartment), apartment";
                    applyslist.SumHQL = String.Format(sql, new String[] { districtname.Text }).Replace("\r\n", " ");
                    applyslist.HQL = (applyslist.SumHQL + orderBy).Replace("\r\n", " ");
                    applyslist.Load();
                    save.IsEnabled = true;
                    search.IsEnabled = true;
                    cancel.IsEnabled = true;
                }
            }
        }


        private void search_Click(object sender, RoutedEventArgs e)
        {
            SearchObject criteria = (applysearch.DataContext as SearchObject);
            criteria.Search();
            WebClientInfo wci = App.Current.Resources["dbclient"] as WebClientInfo;
            String where = criteria.Condition;
            String dt = districtname.Text;
            String state = " 1=1 ";
            if ((cmbState.SelectedItem as ContentControl).Content.ToString().Length > 0)
                state = " state='" + (cmbState.SelectedItem as ContentControl).Content + "'";
            applyslist.LoadOnPathChanged = false;
            applyslist.Path = "sql";
            applyslist.Names = "state,road,districtname,cusDy,cusDom,cusFloor,apartment,placeholder";
            String sql = @"select * from (select state,road,districtname,cusDy,cusDom,cusFloor,apartment from (
                                     select case when a.f_districtname is null then '新增' when b.f_districtname is null then   '已建'  else  '重复 '  end  state,
                                    isnull( a.f_road, b.f_road) road, 
                                    isnull( a.f_districtname, b.f_districtname) districtname,
                                    isnull(a.f_cusDy, b.f_cusDy) cusDy,
                                    isnull(a.f_cusDom, b.f_cusDom) cusDom,
                                    isnull(a.f_cusFloor, b.f_cusFloor) cusFloor,
                                    isnull( a.f_apartment, b.f_apartment) apartment
                                     from 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESS where {0} and f_districtname='{1}') a
                                    full join 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESSTEMP where {0}) b
                                    on a.f_road = b.f_road and a.f_districtname = b.f_districtname and a.f_cusDy=b.f_cusDy and a.f_cusDom=b.f_cusDom and a.f_cusFloor = b.f_cusFloor and a.f_apartment=b.f_apartment) t
                                    ) tt  where {2} ";
            String orderBy = " order by len(road), road, len(districtname), districtname,len(cusDom), cusDom,len(cusDy), cusDy,len(cusFloor), cusFloor,len(apartment), apartment";
            applyslist.SumHQL = String.Format(sql, new String[] { where, dt, state }).Replace("\r\n", " ");
            applyslist.HQL = (applyslist.SumHQL + orderBy).Replace("\r\n", " ");
            applyslist.Load();
        }

        private void save_Click(object sender, RoutedEventArgs e)
        {
            String sql = @"insert into t_gasaddress(id,f_road,f_districtname,f_cusDy,f_cusDom,f_cusFloor,f_apartment) (
                            select NEWID(),road,districtname,cusDy,cusDom,cusFloor,apartment from
                            (
                           select case when a.f_districtname is null then '新增' when b.f_districtname is null then   '已建'  else  '重复 '  end state,
                                    isnull( a.f_road, b.f_road) road, 
                                    isnull( a.f_districtname, b.f_districtname) districtname,
                                    isnull( a.f_cusDy, b.f_cusDy) cusDy,
                                    isnull( a.f_cusDom, b.f_cusDom) cusDom,
                                    isnull( a.f_cusFloor, b.f_cusFloor) cusFloor,
                                    isnull( a.f_apartment, b.f_apartment) apartment
                                     from 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESS where f_districtname='{0}') a
                                    full join 
                                    (select f_road, f_districtname, f_cusDy, f_cusDom, f_cusFloor, f_apartment from T_GASADDRESSTEMP) b
                                    on a.f_road = b.f_road and a.f_districtname = b.f_districtname and a.f_cusDy=b.f_cusDy and a.f_cusDom=b.f_cusDom and a.f_cusFloor = b.f_cusFloor and a.f_apartment=b.f_apartment
                            ) t where state='新增')";
            sql = String.Format(sql, new String[] { districtname.Text });
            WebClientInfo wi = Application.Current.Resources["dbclient"] as WebClientInfo;
            WebClient wc = new WebClient();
            wc.UploadStringCompleted += saveAndClearCompleted;
            wc.UploadStringAsync(new Uri(wi.BaseAddress), "[{operator:\"sql\",data:\"" + sql.Replace("\r\n","") + "\"}]");
        }
        private void saveAndClearCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            if (e.Error != null)
            {
                MessageBox.Show("保存失败！");
                save.IsEnabled = false;
            }
            else
            {
                MessageBox.Show("保存成功！");
                save.IsEnabled = false;
            }
        }


        private void cancel_Click(object sender, RoutedEventArgs e)
        {
            previewButton.IsEnabled = true;
            save.IsEnabled = false;
            search.IsEnabled = false;
            cancel.IsEnabled = false;
            applyslist.Clear();
            (apply.DataContext as GasADObject2).IsInit = true;
        }
    }
}
