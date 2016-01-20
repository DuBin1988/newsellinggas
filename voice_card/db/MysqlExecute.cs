using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using MySql.Data.MySqlClient;
using System.Xml;
using log4net;


namespace voice_card.db
{
    class MysqlExecute : DBExecute
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
            string connStr = "server="+server+";user="+user+";database="+database+";port="+port+";password="+password+";";
            MySqlConnection connection  = new MySqlConnection(connStr);
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
                MySqlConnection conn = (MySqlConnection)this.CreateConn();
                conn.Open();
                System.Text.Encoding iso = System.Text.Encoding.GetEncoding(this.Cinfo.Attrs["charset"]);
                byte[] temp = Encoding.Convert(Encoding.Default, Encoding.Default, Encoding.Default.GetBytes(sql));
                sql = iso.GetString(temp);
                MySqlCommand cmd = new MySqlCommand(sql, conn);
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
