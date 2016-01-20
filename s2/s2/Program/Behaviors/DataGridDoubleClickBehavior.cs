using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Windows.Interactivity;

using Com.Aote.ObjectTools;


using Com.Aote.Logs;
using System.Windows.Navigation;
using Com.Aote.Controls;

namespace Com.Aote.Behavior
{
    public class DataGridDoubleClickBehavior : Behavior<DataGrid>
    {

        private static Log Log = Log.GetInstance("Com.Aote.Behavior.DataGridDoubleClickBehavior");
        //双击打开页面
        //打开页面名
        private string page;
        public string Page
        {
            set 
            { 
                page = value;
            }
            get { return this.page; }
        }

        public string ParentElementName { get; set; }

        //放入资源
        private string selectedTo;
        public string SelectedTo
        {
            set { this.selectedTo = value; }
            get { return this.selectedTo; }
        }

        public string ToElementName { get; set; }

        private readonly MouseClickManager _gridClickManager;


        public DataGridDoubleClickBehavior()
        {
            _gridClickManager = new MouseClickManager(300);
            _gridClickManager.DoubleClick -= _gridClickManager_DoubleClick;
            _gridClickManager.DoubleClick += new MouseButtonEventHandler(_gridClickManager_DoubleClick);
        }

        protected override void OnAttached()
        {
            base.OnAttached();
            AssociatedObject.LoadingRow += OnLoadingRow;
            AssociatedObject.UnloadingRow += OnUnloadingRow;
        }

        void OnUnloadingRow(object sender, DataGridRowEventArgs e)
        {

            //row is no longer visible so remove double click event otherwise

            //row events will miss fire

            e.Row.MouseLeftButtonUp -= _gridClickManager.HandleClick;

        }

        public FrameworkElement ParentElement;

        void OnLoadingRow(object sender, DataGridRowEventArgs e)
        {
            //row is visible in grid, wire up double click event
            e.Row.MouseLeftButtonUp += _gridClickManager.HandleClick;
            if (ParentElementName != null)
            {
            //    FrameworkElement ui = Tools.getUserControl(sender as FrameworkElement);
              //  ParentElement = (FrameworkElement)ui.FindName(ParentElementName);
            }
        }

        protected override void OnDetaching()
        {

            base.OnDetaching();
            AssociatedObject.LoadingRow -= OnLoadingRow;
            AssociatedObject.UnloadingRow -= OnUnloadingRow;
        }

        public string PageCoverter { get; set; }

        void _gridClickManager_DoubleClick(object sender, MouseButtonEventArgs e)
        {
            DataGridRow dgrow =  sender as DataGridRow;
            PageResourceContentLoader load = new PageResourceContentLoader();
            load.BeginLoad(new Uri(page + ".xaml", UriKind.Relative), null, new AsyncCallback(r =>
            {
                LoadResult ui = load.EndLoad(r);
                CustomChildWindow showWin = (CustomChildWindow)ui.LoadedContent;
                showWin.ParamValue = dgrow.DataContext;
                showWin.Show();
               
                
            }), 1);
           


             
            
 
        }
    }
}
