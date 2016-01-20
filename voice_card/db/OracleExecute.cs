using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Data.Common;
using System.Data.OleDb;
using log4net;
using System.Data.OracleClient;
using System.Data;

namespace voice_card.db
{
    class OracleExecute : DBExecute
    {

        private static ILog log = LogManager.GetLogger(typeof(OracleExecute));
        public override DbConnection CreateConn()
        {
            string server = this.Cinfo.Attrs["server"];
            string user = this.Cinfo.Attrs["user"];
            string password = this.Cinfo.Attrs["password"];
            //string server = "192.168.1.134";
            //string user = "xygas";
            //string password = "xygas";
            string port = this.Cinfo.Attrs["port"];
            string database = this.Cinfo.Attrs["dbname"];
            //string chartset = this.Cinfo.Attrs["charset"];
            string connStr = "provider=MSDAORA;Data Source=(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = " + server + ")(PORT = " + port + ")))(CONNECT_DATA =(SERVICE_NAME =" + database + ")));User ID=" + user + ";Password=" + password + ";";
            OleDbConnection conn = new OleDbConnection(connStr);
            return conn;
        }

        public override ArrayList Query(string sql)
        {
            ArrayList result = new ArrayList();
            //创建和数据库的连接
            OleDbConnection conn = (OleDbConnection)this.CreateConn();
            //新建一个对数据库操作的实例
            OleDbCommand oraCmd = new OleDbCommand(sql, conn);
            //打开数据库连接
            conn.Open();
            //DataReader提供一种从数据库读取行的只进流的方式。
            OleDbDataReader oraRD = oraCmd.ExecuteReader();
            while (oraRD.Read())
            {
                result.Add(oraRD);
            }
            oraRD.Close();
            //关闭数据库连接
            conn.Close();
            return result;
        }

        public override void Execute(string sql)
        {
            try
            {
                OleDbConnection conn = (OleDbConnection)this.CreateConn();
                conn.Open();
                //System.Text.Encoding iso = System.Text.Encoding.GetEncoding(this.Cinfo.Attrs["charset"]);
                //byte[] temp = Encoding.Convert(Encoding.Default, Encoding.Default, Encoding.Default.GetBytes(sql));
                //sql = iso.GetString(temp);
                OleDbCommand cmd = new OleDbCommand(sql, conn);
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (Exception e)
            {
                //log.Debug("数据库错误" + e.ToString());
                Console.WriteLine("数据库错误" +sql);
            }
        }
    }
}
