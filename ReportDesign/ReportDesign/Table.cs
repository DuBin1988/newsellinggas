using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Com.Aote.Report
{
    /// <summary>
    /// 按照步骤 1a 或 1b 操作，然后执行步骤 2 以在 XAML 文件中使用此自定义控件。
    ///
    /// 步骤 1a) 在当前项目中存在的 XAML 文件中使用该自定义控件。
    /// 将此 XmlNamespace 特性添加到要使用该特性的标记文件的根 
    /// 元素中:
    ///
    ///     xmlns:MyNamespace="clr-namespace:WpfApplication1"
    ///
    ///
    /// 步骤 1b) 在其他项目中存在的 XAML 文件中使用该自定义控件。
    /// 将此 XmlNamespace 特性添加到要使用该特性的标记文件的根 
    /// 元素中:
    ///
    ///     xmlns:MyNamespace="clr-namespace:WpfApplication1;assembly=WpfApplication1"
    ///
    /// 您还需要添加一个从 XAML 文件所在的项目到此项目的项目引用，
    /// 并重新生成以避免编译错误:
    ///
    ///     在解决方案资源管理器中右击目标项目，然后依次单击
    ///     “添加引用”->“项目”->[浏览查找并选择此项目]
    ///
    ///
    /// 步骤 2)
    /// 继续操作并在 XAML 文件中使用控件。
    ///
    ///     <MyNamespace:Table/>
    ///
    /// </summary>
    [TemplatePartAttribute(Name = "PART_RootPanel", Type = typeof(Canvas))]
    [TemplatePartAttribute(Name = "PART_LineLayout", Type = typeof(Canvas))]
    [TemplatePartAttribute(Name = "PART_CellLayout", Type = typeof(Canvas))]
    [TemplatePartAttribute(Name = "PART_EditTextBox", Type = typeof(TextBox))]

    public class Table : Control, INotifyPropertyChanged
    {
        //sql语句列表，支持多sql语句
        public ObservableCollection<Sql> sqls = new ObservableCollection<Sql>();

        //表格线所在层，从模板里取
        private Canvas lineLayout;

        //单元格所在层，从模板里取
        private Canvas cellLayout;

        //编辑时用的TextBox
        private TextBox editTextBox;
        public TextBox EditTextBox
        {
            get { return editTextBox; }
            set
            {
                this.editTextBox = value;
                OnPropertyChanged("EditTextBox");
            }
        }

        //左侧表达式
        private string leftvalue;
        public string LeftValue
        {
            get { return leftvalue; }
            set
            {
                this.leftvalue = value;
                OnPropertyChanged("LeftValue");
            }
        }

        //表头表达式
        private string headvalue;
        public string HeadValue
        {
            get { return headvalue; }
            set
            {
                this.headvalue = value;
                OnPropertyChanged("HeadValue");
            }
        }

        //单元格列表，每个单元格主要包括坐标及大小
        List<Cell> cells = new List<Cell>();

        //列信息
        List<Row> rows = new List<Row>();

        //行信息
        List<Column> columns = new List<Column>();

        //正在编辑的单元格
        Cell editCell = null;

        //选中的单元格
        List<Cell> selectedCells = new List<Cell>();
        
        //当前选中的对象
        IMove selected = null;

        static Table()
        {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(Table), new FrameworkPropertyMetadata(typeof(Table)));
        }

        public Table()
        {
            //默认5行5列
            for (int i = 0; i < 5; i++)
            {
                Row row = new Row() { Number = i, Height = 100 };
                rows.Add(row);
            }

            for (int i = 0; i < 5; i++)
            {
                Column column = new Column() { Number = i, Width = 100 };
                columns.Add(column);
            }

            for (int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    Cell cell = new Cell() { Row = i, Column = j };
                    cells.Add(cell);
                }
            }

            //选中第一个单元格进行编辑
            this.editCell = this.cells[0];
        }

        public override void OnApplyTemplate()
        {
            base.OnApplyTemplate();

            //获取画线的面板
            this.lineLayout = GetTemplateChild("PART_LineLayout") as Canvas;

            //获取放单元格的面板
            this.cellLayout = GetTemplateChild("PART_CellLayout") as Canvas;

            //获取编辑所有输入框
            this.EditTextBox = GetTemplateChild("PART_EditTextBox") as TextBox;

            Layout();
        }

        protected override void OnMouseLeftButtonUp(MouseButtonEventArgs e)
        {
            base.OnMouseLeftButtonUp(e);
            //有选中对象，由选中对象处理移动过程
            if (this.selected != null)
            {
                Point point = e.GetPosition(this);
                this.selected.MoveTo(point);

                //如果是单元格，选择单元格
                if (this.selected is Cell)
                {
                    this.SelectCell(point);
                }
                
                Layout();
            }
            this.selected = null;
        }

        protected override void OnMouseLeftButtonDown(MouseButtonEventArgs e)
        {
            base.OnMouseLeftButtonDown(e);

            //获得选中的对象
            Point point = e.GetPosition(this);
            this.selected = this.GetPosition(point) as IMove;

            if (this.selected is Cell)
            {
                //把输入内容放到原单元格中
                this.editCell.Content = this.editTextBox.Text;
                //选中新单元格
                Cell cell = this.selected as Cell;
                this.editCell = cell;

                //清空所有选中单元格
                this.selectedCells.Clear();
                this.Layout();
            }
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            //如果有选中对象，对选中对象进行拖放处理
            if (selected != null)
            {
            }
            else
            {
                //获取鼠标坐标，根据坐标所在范围，改变鼠标形状
                Point point = e.GetPosition(this);
                object obj = this.GetPosition(point);
                //在行附近
                if (obj is Row)
                {
                    this.Cursor = Cursors.SizeNS;
                }
                else if (obj is Column)
                {
                    this.Cursor = Cursors.SizeWE;
                }
                else if (obj is Cell)
                {
                    this.Cursor = Cursors.Arrow;
                }
            }
        }

        //合并单元格
        public void ComposeCell()
        {
            int minRow = 10000;
            int maxRow = 0;
            int minColumn = 10000;
            int maxColumn = 0;

            //最小坐标单元格
            Cell minCell = null;

            //获取选中单元格的坐标范围
            foreach (Cell cell in this.selectedCells)
            {
                if (cell.Row < minRow)
                {
                    minRow = cell.Row;
                }
                if (cell.Row + cell.RowSpan > maxRow)
                {
                    maxRow = cell.Row + cell.RowSpan;
                }
                if (cell.Column < minColumn)
                {
                    minColumn = cell.Column;
                }
                if (cell.Column + cell.ColumnSpan > maxColumn)
                {
                    maxColumn = cell.Column + cell.ColumnSpan;
                }

                //第一个单元格
                if (minCell == null)
                {
                    minCell = cell;
                }
                //当前单元格是最小单元格，把原来单元格的内容，合并到当前单元格中，删除原来单元格
                else if (cell.Row < minCell.Row || (cell.Row == minCell.Row && cell.Column < minCell.Column))
                {
                    cell.Content += minCell.Content;
                    this.cells.Remove(minCell);
                    minCell = cell;
                }
                //保留原最小单元格，合并当前单元格内容
                else
                {
                    minCell.Content += cell.Content;
                    this.cells.Remove(cell);
                }
            }

            //设置单元格宽、高
            minCell.RowSpan = maxRow - minCell.Row;
            minCell.ColumnSpan = maxColumn - minCell.Column;

            //清除选中单元格
            this.selectedCells.Clear();
            this.Layout();
        }

        //取消合并
        public void DecomposeCell()
        {
            //针对当前单元格，按行列重新产生单元格
            for (int i = 0; i < this.editCell.RowSpan; i++)
            {
                for (int j = 0; j < this.editCell.ColumnSpan; j++)
                {
                    //不是原来cell
                    if(!(i == 0 && j == 0))
                    {
                        Cell cell = new Cell() { Row = this.editCell.Row + i, Column = this.editCell.Column + j };
                        this.cells.Add(cell);
                    }
                }
            }

            this.editCell.ColumnSpan = 1;
            this.editCell.RowSpan = 1;
            this.Layout();
        }

        //前插一行
        public void InsertRow()
        {
            //当前行之后所有行，行号+1
            for (int i = this.editCell.Row; i < this.rows.Count; i++)
            {
                this.rows[i].Number++;
            }
            //所有当前行之后的单元格，row+1
            foreach (Cell cell in this.cells)
            {
                if (cell.Row >= this.editCell.Row && cell != this.editCell)
                {
                    cell.Row++;
                }
            }

            //生成新单元格
            for (int i = 0; i < this.columns.Count; i++)
            {
                Cell cell = new Cell() { Row = this.editCell.Row, Column = i };
                this.cells.Add(cell);
            }

            //插入新行
            Row row = new Row() { Number = this.editCell.Row, Height = 100 };
            rows.Add(row);

            this.editCell.Row++;
            rows.Sort();
            Layout();
        }

        //前插一列
        public void InsertColumn()
        {
            //当前列之后所有列，列号+1
            for (int i = this.editCell.Column; i < this.columns.Count; i++)
            {
                this.columns[i].Number++;
            }

            //所有当前列之后的单元格，column+1
            foreach (Cell cell in this.cells)
            {
                if (cell.Column >= this.editCell.Column && cell != this.editCell)
                {
                    cell.Column++;
                }
            }

            //生成新单元格
            for (int i = 0; i < this.rows.Count; i++)
            {
                Cell cell = new Cell() { Row = i, Column = this.editCell.Column };
                this.cells.Add(cell);
            }

            //插入新列
            Column column = new Column() { Number = this.editCell.Column, Width = 100 };
            this.columns.Add(column);

            this.editCell.Column++;
            this.columns.Sort();
            Layout();
        }
          
        //删除当前行
        public void DeleteRow()
        {
            if (this.rows.Count == 1)
            {
                MessageBox.Show("只有一行，不能删除！");
                return;
            }
            //当前行之后所有行，行号+1
            for (int i = this.editCell.Row+1; i < this.rows.Count; i++)
            {
                this.rows[i].Number--;
            }
            //所有当前行之后的单元格，row+1
            foreach (Cell cell in this.cells)
            {
                if (cell.Row > this.editCell.Row && cell != this.editCell)
                {
                    cell.Row--;
                }
            }
            //删除当前行
            rows.Remove(rows[this.editCell.Row]);

            rows.Sort();
            Layout();
        }

        //新增一条sql语句
        public void AddSql()
        {
            Sql sql = new Sql() { Name="", Content="" };
            this.sqls.Add(sql);
        }

        //从文件中加载数据，文件格式与Save函数相同
        public void Load(string fileName)
        {
            //清除原来的数据
            this.cells.Clear();
            this.columns.Clear();
            this.rows.Clear();
            this.selectedCells.Clear();
            this.sqls.Clear();
            if (fileName == "" || fileName == null) return;
            StreamReader sw = new StreamReader(fileName, Encoding.Default, false);

            string jsonText = sw.ReadToEnd();

            // JsonObject json;
            var json = (JObject)JsonConvert.DeserializeObject(jsonText);

            //读出所有sql语句
            JArray array = (JArray)json["sqls"];
            foreach (JObject obj in array)
            {
                Sql sql = new Sql()
                {
                    Name = (string)obj["name"],
                    Content = (string)obj["sql"]
                };
                this.sqls.Add(sql);
            }

            //左表头内容表达式
            LeftValue = (string)json["leftsql"];
            //左表头内容表达式
            HeadValue = (string)json["headsql"];

            //读出所有列
            int i = 0;
            array = (JArray)json["columns"];
            foreach (JObject obj in array)
            {
                Column column = new Column()
                {
                    Width = (int)obj["width"],
                    Number = i++
                };
                this.columns.Add(column);
            }

            //读出所有行
            i = 0;
            array = (JArray)json["rows"];
            foreach (JObject obj in array)
            {
                Row row = new Row()
                {
                    Height = (int)obj["height"],
                    Type = (string)obj["type"],
                    Number = i++
                };
                this.rows.Add(row);
            }

            this.headCells.Clear();
            this.leftCells.Clear();
            this.mainCells.Clear();

            //读出所有单元格
            array = (JArray)json["cells"];
            foreach (JObject obj in array)
            {
                Cell cell = new Cell()
                {
                    Row = (int)obj["row"],
                    RowSpan = (int)obj["rowspan"],
                    ColumnSpan = (int)obj["columnspan"],
                    Column = (int)obj["column"],
                    Content = (string)obj["content"],
                    location = (string)obj["location"],
                    Height = (int)obj["height"],
                };
                this.cells.Add(cell);

                //根据单元格类型，把单元格添加到对应队列中
                string type = (string)obj["type"];
                if (type == "head")
                {
                    headCells.Add(cell);
                }
                else if (type == "left")
                {
                    leftCells.Add(cell);
                }
                else if (type == "main")
                {
                    mainCells.Add(cell);
                }
                else if (type == "bottom")
                {
                    bottomCells.Add(cell);
                }
                if (type == "headchange")
                {
                    headchangeCells.Add(cell);
                }
            }

            this.Layout();
        }

        //把表格数据保存到文件中，格式为JSON格式：{columns:[{width:10},{width:20}], rows:[{height:10}], cells:[{row:1,column:1}]}
        public void Save(string fileName)
        {
            string str = "{";

            //写入所有sql语句
            str += "sqls:[";

            string sqls = "";
            foreach (Sql sql in this.sqls)
            {
                if (sqls != "")
                {
                    sqls += ",";
                }
                sqls += "{name:'" + sql.Name + "', sql:'" + sql.Content.Replace("'", "\\'") + "'}";
            }
            str += sqls + "]";
            //表头内容表达式
            str += ",headsql:'" + HeadValue + "'";
            //左表头内容表达式
            str += ",leftsql:'" + LeftValue+"'";

            str += ",columns:[";

            string columns = "";
            //写入所有列
            foreach (Column column in this.columns)
            {
                if (columns != "")
                {
                    columns += ",";
                }
                columns += "{width:" + column.Width + "}";
            }
            str += columns + "]";

            //写入列与行的分割符号
            str += ", rows:[";

            string rows = "";
            //写入所有行
            foreach (Row row in this.rows)
            {
                if (rows != "")
                {
                    rows += ",";
                }
                rows += "{height:" + row.Height + ",type:'" + row.Type + "'}";
            }
            str += rows + "]";

            //写入分割符号
            str += ", cells:[";

            string cells = "";
            //写入所有单元格
            foreach (Cell cell in this.cells)
            {
                if (cells != "")
                {
                    cells += ",";
                }
                string type = "";
                //判断单元格类型
                if (this.headCells.Contains(cell))
                {
                    type = "head";
                }
                else if (this.leftCells.Contains(cell))
                {
                    type = "left";
                }
                else if (this.mainCells.Contains(cell))
                {
                    type = "main";
                }
                else if (this.bottomCells.Contains(cell))
                {
                    type = "bottom";
                }
                if (this.headchangeCells.Contains(cell))
                {
                    type = "headchange";
                }
                cells += "{row:" + cell.Row + ",type:'" + type + "',rowspan:" + cell.RowSpan + ",columnspan:" + cell.ColumnSpan + ", column:" + cell.Column + ", content:'" + cell.Content + "', location:'" + cell.location + "'" + ",height:" + cell.Height + "}";
            }
            str += cells + "]";

            str += "}";
            StreamWriter sw = new StreamWriter(fileName, false, Encoding.Default);
            sw.Write(str);
            sw.Close();
        }

        //选择单元格
        private void SelectCell(Point point)
        {
            this.selectedCells.Clear();

            //获得最后选择的单元格
            Cell endCell = this.GetPosition(point) as Cell;

            //对所有单元格，如果在选择范围内，则选中
            foreach (Cell cell in this.cells)
            {
                if (endCell!=null && cell.Column >= Math.Min(this.editCell.Column, endCell.Column) &&
                    cell.Column <= Math.Max(this.editCell.Column, endCell.Column) &&
                    cell.Row >= Math.Min(this.editCell.Row, endCell.Row) &&
                    cell.Row <= Math.Max(this.editCell.Row, endCell.Row))
                {
                    this.selectedCells.Add(cell);
                }
            }
        }

        //获取鼠标所在位置，单元格或者某行、某列上
        private object GetPosition(Point point)
        {
            int x = (int)point.X;
            Column inColumn = null;
            Column preColumn = null;
            //由x坐标，算出在哪列范围内
            foreach (Column column in this.columns)
            {
                inColumn = column;
                x -= column.Width;
                if (x < 0)
                {
                    break;
                }
                preColumn = inColumn;
            }

            int y = (int)point.Y;
            Row inRow = null;
            Row preRow = null;
            //由y坐标，算出在哪行范围内
            foreach (Row row in this.rows)
            {
                inRow = row;
                y -= row.Height;
                if (y < 0)
                {
                    break;
                }
                preRow = inRow;
            }

            //在行线附近，返回行
            if (inRow!=null && Math.Abs(inRow.StartY - point.Y) < 5)
            {
                return preRow;
            }

            if (inRow != null && Math.Abs(inRow.StartY + inRow.Height - point.Y) < 5)
            {
                return inRow;
            }

            //在列线附近，返回列
            if (inColumn != null && Math.Abs(inColumn.StartX - point.X) < 5)
            {
                return preColumn;
            }

            if (inColumn != null && Math.Abs(inColumn.StartX + inColumn.Width - point.X) < 5)
            {
                return inColumn;
            }

            //返回行列所在单元格
            return GetCell(inRow.Number, inColumn.Number);
        }

        //找到某行，某列单元格
        private Cell GetCell(int row, int column)
        {
            foreach (Cell cell in this.cells)
            {
                if (row >= cell.Row && row < cell.Row + cell.RowSpan && column >= cell.Column && column < cell.Column + cell.ColumnSpan)
                {
                    return cell;
                }
            }

            return null;
        }

        //计算单元格宽带
        private int GetWidth(Cell cell)
        {
            int result = 0;

            for (int i = 0; i < cell.ColumnSpan; i++)
            {
                result += this.columns[i + cell.Column].Width;
            }

            return result;
        }

        //计算单元格高度
        private int GetHeight(Cell cell)
        {
            int result = 0;

            for (int i = 0; i < cell.RowSpan; i++)
            {
                result += this.rows[i + cell.Row].Height;
            }

            return result;
        }

        //重新绘制
        private void Layout()
        { 
            LayoutColumns();
            LayoutRows();
            LayoutCellWidthAndHeight();
            LayoutLines();
            LayoutCells();
            LayoutEditBox();
        }

        //重画编辑框
        private void LayoutEditBox()
        {
            Column column = columns[editCell.Column];
            Row row = rows[editCell.Row];

            Canvas.SetLeft(this.editTextBox, (double)column.StartX);
            Canvas.SetTop(this.editTextBox, (double)row.StartY);
            this.editTextBox.Width = editCell.Width;
            this.editTextBox.Height = editCell.Height;

            //把单元格内容给编辑框
            this.editTextBox.Text = this.editCell.Content;

            //如果有选中单元格，编辑层不可见
            if (this.selectedCells.Count > 1)
            {
                this.editTextBox.Visibility = Visibility.Collapsed;
            }
            else
            {
                this.editTextBox.Visibility = Visibility.Visible;
            }
        }
    
        //重画表格线
        private void LayoutLines()
        {
            lineLayout.Children.Clear();

            //在面板上根据单元格内容画线
            foreach (Cell cell in this.cells)
            {
                Column column = columns[cell.Column];
                Row row = rows[cell.Row];

                Line topLine = new Line()
                {
                    X1 = column.StartX,
                    X2 = column.StartX + cell.Width,
                    Y1 = row.StartY,
                    Y2 = row.StartY,
                    Stroke = new SolidColorBrush(Colors.Black),
                    StrokeThickness = 1
                };
                Line downLine = new Line()
                {
                    X1 = column.StartX,
                    X2 = column.StartX + cell.Width,
                    Y1 = row.StartY + cell.Height,
                    Y2 = row.StartY + cell.Height,
                    Stroke = new SolidColorBrush(Colors.Black),
                    StrokeThickness = 1
                };
                Line leftLine = new Line()
                {
                    X1 = column.StartX,
                    X2 = column.StartX,
                    Y1 = row.StartY,
                    Y2 = row.StartY + cell.Height,
                    Stroke = new SolidColorBrush(Colors.Black),
                    StrokeThickness = 1
                };
                Line rightLine = new Line()
                {
                    X1 = column.StartX + cell.Width,
                    X2 = column.StartX + cell.Width,
                    Y1 = row.StartY,
                    Y2 = row.StartY + cell.Height,
                    Stroke = new SolidColorBrush(Colors.Black),
                    StrokeThickness = 1
                };
                lineLayout.Children.Add(topLine);
                lineLayout.Children.Add(downLine);
                lineLayout.Children.Add(leftLine);
                lineLayout.Children.Add(rightLine);
            }
        }

        //重新绘制单元格
        private void LayoutCells( )
        {
           this.cellLayout.Children.Clear();

            //在面板上根据单元格内容写TextBlock
            foreach (Cell cell in this.cells)
            {
                Column column = columns[cell.Column];
                Row row = rows[cell.Row];
                TextBlock text = new TextBlock();
                text.Text = cell.Content;
                Canvas.SetLeft(text, (double)column.StartX);
                Canvas.SetTop(text, (double)row.StartY);
                text.Width = cell.Width;
                text.Height = cell.Height;
                text.TextWrapping = TextWrapping.Wrap;
                if (cell.location == "center")
                {
                    text.TextAlignment = TextAlignment.Center;
                }
                else if (cell.location == "right")
                {
                    text.TextAlignment = TextAlignment.Right;
                }
                else if (cell.location == "left")
                {
                    text.TextAlignment = TextAlignment.Left;
                }
                //绘制标记的表头颜色
                if (this.headCells.Contains(cell))
                {
                    text.Background = new SolidColorBrush(Colors.Red);
                }
                //绘制标记的表头变化部分的颜色
                if (this.headchangeCells.Contains(cell))
                {
                    text.Background = new SolidColorBrush(Colors.DarkRed);
                }
                //绘制标记的左侧颜色
                if (this.leftCells.Contains(cell))
                {
                    text.Background = new SolidColorBrush(Colors.Yellow);
                }
                //绘制标记的主体颜色
                if (this.mainCells.Contains(cell))
                {
                    text.Background = new SolidColorBrush(Colors.Green);
                }
                //绘制标记的表低颜色
                if (this.bottomCells.Contains(cell))
                {
                    text.Background = new SolidColorBrush(Colors.Gray);
                }
                this.cellLayout.Children.Add(text);
                //单元格选中，背景色改变
                if (this.selectedCells.Contains(cell))
                {
                   text.Background = new SolidColorBrush(Colors.Blue);
                }
            }
           
        }

        //设置单元格居中
        public void setcenter()
        {

            foreach (Cell cell in this.selectedCells)
            {
                cell.location = "center";                
   
            }
           
            LayoutCells();

        }
        //设置单元格居右
        public void setright()
        {
            foreach (Cell cell in this.selectedCells)
            {
                cell.location = "right";

            }

            LayoutCells();
        }

        private List<Cell> headCells = new List<Cell>();
        //标记为表头
        public void markhead()
        {
            headCells.Clear();
            foreach (Cell cell in this.selectedCells)
            {
                headCells.Add(cell);
                this.rows[cell.Row].Type = "head";
            }
        }

        List<Cell> leftCells = new List<Cell>();
        //标记为左侧
        public void markleft()
        {
            leftCells.Clear();
            foreach (Cell cell in this.selectedCells)
            {
                leftCells.Add(cell);
                this.rows[cell.Row].Type = "left";
            }
        }
        List<Cell> mainCells = new List<Cell>();
        //标记为主体
        public void markmain()
        {
            mainCells.Clear();
            foreach (Cell cell in this.selectedCells)
            {
                mainCells.Add(cell);
                this.rows[cell.Row].Type = "main";
            }
        }

        List<Cell> bottomCells = new List<Cell>();
        //标记为主体
        public void markbottom()
        {
            bottomCells.Clear();
            foreach (Cell cell in this.selectedCells)
            {
                bottomCells.Add(cell);
                this.rows[cell.Row].Type = "bottom";
            }
        }

        private List<Cell> headchangeCells = new List<Cell>();

        //标记表头变化部分
        public void markheadchange()
        {
            if (headCells.Count <= 0)
            {
                MessageBox.Show("请先标记表头！");
                return;
            }
            foreach (Cell cell in this.selectedCells)
            {
                if (!this.headCells.Contains(cell))
                {
                    MessageBox.Show("必须先设置成表头，才能标记表头变化部分！");
                    return;
                }
                else
                {
                    headchangeCells.Add(cell);
                }
            }
        }

        //取消选中的标记
        public void markdrop()
        {
            foreach (Cell cell in this.selectedCells)
            {
                if (headCells.Contains(cell))
                {
                    headCells.Remove(cell);
                    this.rows[cell.Row].Type = "";
                }
                if(mainCells.Contains(cell))
                {
                    mainCells.Remove(cell);
                    this.rows[cell.Row].Type = "";
                }
                if(bottomCells.Contains(cell))
                {
                    bottomCells.Remove(cell);
                    this.rows[cell.Row].Type = "";
                }
                if (leftCells.Contains(cell))
                {
                    leftCells.Remove(cell);
                    this.rows[cell.Row].Type = "";
                }
            }
        }
        //居左
        public void setleft()
        {
            foreach (Cell cell in this.selectedCells)
            {
                cell.location = "left";
            }

            LayoutCells();
        }

        public void setfont()
        {
        }

        public void setfontsize()
        {
        }


        //重新计算单元格宽高
        private void LayoutCellWidthAndHeight()
        {
            //在面板上根据单元格内容写TextBlock
            foreach (Cell cell in this.cells)
            {
                cell.Width = this.GetWidth(cell);
                cell.Height = this.GetHeight(cell);
            }
        }

        //重新计算每列起始位置
        private void LayoutColumns()
        {
            int startX = 0;

            //循环加前面列的宽度
            foreach (Column column in this.columns)
            {
                column.StartX = startX;
                startX += column.Width;
            }
        }

        //重新计算每行起始位置
        private void LayoutRows()
        {
            int startY = 0;

            //循环加前面列的宽度
            foreach (Row row in this.rows)
            {
                row.StartY = startY;
                startY += row.Height;
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;
        public void OnPropertyChanged(string name)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(name));
            }
        }
    }

    //移动接口
    interface IMove
    {
        //完成最后的移动过程
        void MoveTo(Point point);
    }

    //单元格，包括坐标及大小
    class Cell : IMove
    {
        public int Row = 0;
        public int Column = 0;
        public int RowSpan = 1;
        public int ColumnSpan = 1;
         
        public int Width;
        public int Height;

        //单元格内容对齐方式
        public  string  location;

        //单元格内容，先考虑文本，不考虑控件
        public string Content = "";

        //选中单元格
        public void MoveTo(Point point)
        {
        }


    }

    //列信息，包括列宽及开始坐标
    class Column : IMove, IComparable
    {
        public int Number;
        public int Width;
        public int StartX;

        //移动
        public void MoveTo(Point point)
        {
            //由新的点重新计算宽带
            int width = (int)point.X - StartX;
            if (width > 10)
            {
                this.Width = width;
            }
        }

        public int CompareTo(object obj)
        {
            Column column = obj as Column;
            return this.Number - column.Number;
        }
    }

    //行信息，包括行高及开始坐标
    class Row : IMove, IComparable
    {
        public int Number;
        public int Height;
        public int StartY;
        public string Type;

        //移动
        public void MoveTo(Point point)
        {
            //由新的点重新计算高度
            int height = (int)point.Y - StartY;
            if (height > 10)
            {
                this.Height = height;
            }
        }

        public int CompareTo(object obj)
        {
            Row row = obj as Row;
            return this.Number - row.Number;
        }



    }

    //sql语句，包括sql名称及sql内容
    public class Sql
    {
        public string Name { get; set; }
        public string Content { get; set; }
    }
}
