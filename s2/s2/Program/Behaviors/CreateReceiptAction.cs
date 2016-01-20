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
using Com.Aote.ObjectTools;

namespace Com.Aote.Behaviors
{
    //生成发票
    public class CreateReceiptAction : BaseAsyncAction
    {
        public GeneralObject Source { get; set; }

        public string EntityType { get; set; }

        public string BeginNo { get; set; }
        public string EndNo { get; set; }

        public ObjectList DataList;

        public override void Invoke()
        {
            No = -1;
            State = State.Start;
            IsBusy = true;
            DataList = new ObjectList();
            double no;
            while ((no = GetNo()) != 0)
            {
                GeneralObject go = CreateObj(Source, no);
                DataList.Add(go);
            }
            Source.Completed += (o, e) =>
            {
                Source.NewPropertyValue("id");
            };
            Source.SetPropertyValue("invoicelist", DataList, true);
            Source.Save();
            IsBusy = false;
            State = State.End;
            MessageBox.Show("分配完成！");
        }

        //创建发票对象
        public GeneralObject CreateObj(GeneralObject source,double no)
        {
            GeneralObject go = new GeneralObject();
            go.EntityType = EntityType;
            string value = no.ToString();
            if (value.Length != BeginNo.ToString().Length)
            {
                for (int i = 0; i <= BeginNo.ToString().Length-value.Length; i++)
                {
                    value = "0" + value;
                }
            }
            //发票号
            go.SetPropertyValue("f_invoicenum", value, true);
            //所属公司
            go.SetPropertyValue("f_filiale", source.GetPropertyValue("f_filiale"), true);
            //发票状态
            go.SetPropertyValue("f_fapiaostatue", "未用", true);
            //使用人
            go.SetPropertyValue("f_sgoperator", source.GetPropertyValue("f_sgoperator"), true);
            //分配人
            go.SetPropertyValue("f_operator", source.GetPropertyValue("f_operator"), true);
            //分配日期
            go.SetPropertyValue("f_date", source.GetPropertyValue("f_date"), true);
            return go;
        }


        private double No;
        //获得当前编号
        public double GetNo()
        {
            if (No == -1)
            {
                No = double.Parse(BeginNo);
            }
            if (double.Parse(EndNo) >= No)
            {
                return No++;
            }
            return 0;
        }
    }
}

