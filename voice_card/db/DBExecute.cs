using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Data.Common;
using System.Xml;
 

namespace voice_card.db
{
    abstract class DBExecute
    {

        private ConnInfo cinfo;
        public ConnInfo Cinfo
        {
            set { cinfo = value; }
            get { return cinfo; }
        }

        
        public  DBExecute()
        {
        }

        public void SetConnInfo(XmlElement config)
        {
            this.Cinfo = new ConnInfo(config);
        }
        

        /**
         * 创建连接 子类需实现
         * */
        public abstract DbConnection CreateConn();
         
        /**
         * 查询
         **/
        public abstract ArrayList Query(String sql);

        /**
         * 执行
         * */
        public abstract void Execute(String sql);
 
    }
}
