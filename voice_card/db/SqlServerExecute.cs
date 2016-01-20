using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.Data.SqlClient;
using log4net;
using System.Collections;  //使用的命名空间

namespace voice_card.db
{
    class SqlServerExecute : DBExecute
    {
        private static ILog log = LogManager.GetLogger(typeof(MysqlExecute));

        public override System.Data.Common.DbConnection CreateConn()
        {
            string server = this.Cinfo.Attrs["server"];
             string user = this.Cinfo.Attrs["user"];
            string password = this.Cinfo.Attrs["password"];
            string port = this.Cinfo.Attrs["port"];
            string database = this.Cinfo.Attrs["dbname"];
            string chartset = this.Cinfo.Attrs["charset"];
           
            string connStr = "server=" + server + ";database =" + database + ";uid = " + user + ";pwd = " + password;
            SqlConnection connection = new SqlConnection(connStr);
            return connection;
        }

        //未实现
        public override ArrayList Query(string sql)
        {
            return new ArrayList();
        }

        public override void Execute(string sql)
        {
            try
            {
                SqlConnection conn = (SqlConnection)this.CreateConn();
                conn.Open();
                System.Text.Encoding iso = System.Text.Encoding.GetEncoding(this.Cinfo.Attrs["charset"]);
                byte[] temp = Encoding.Convert(Encoding.Default, Encoding.Default, Encoding.Default.GetBytes(sql));
                sql = iso.GetString(temp);
                SqlCommand cmd = new SqlCommand(sql, conn);
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                log.Debug("数据库错误" + e.ToString());
                Console.WriteLine("数据库错误" + e.ToString());
            }
        }
    }
}
