using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;
using System.Net;
using log4net;
 

namespace voice_card.helper
{
    class WebSaveHelper :RecordSave
    {
        private static ILog log = LogManager.GetLogger(typeof(LineRecordHelper));
        
        public void Save(entity.LineInfo trunk, entity.LineInfo inline)
        {
            //如果id为空，说明是心来电，插入，否则更新
            string id = trunk.Id;
            if (id == null || id.Equals(""))
            {
                trunk.Id = Guid.NewGuid().ToString();
                Insert(trunk);
            }
            else
            {
                Update(trunk, inline);
            }            
        }


        //插入来电记录
        private static void Insert(LineInfo line)
        {
            string id = line.Id;
            string comingtime = line.Comingtime.ToLocalTime().ToString("yyyyMMdd HH:mm:ss");
            string trunk = line.Number.ToString();
            string sql = "insert into t_comingrecord(id,comingtime,trunk,islink) values('" + id + "','" + comingtime + "','" + trunk + "','no')";
            // log.Debug("插入数据:" + sql);
            SendMessage(sql);
            //DBHelper.executeNonQuery(sql);
        }

        private static void Update(LineInfo trunk, LineInfo inline)
        {
            string sql = "update t_comingrecord set ";
            //主叫
            if (inline != null && inline.CallerPhone != null && !inline.CallerPhone.Equals(""))
            {
                Console.Write("号码" + inline.CallerPhone);
                sql += " callnumber='" + inline.CallerPhone + "',";
            }
            string rectime = trunk.Rectime.ToLocalTime().ToString("yyyyMMdd HH:mm:ss");
            sql += "rectime='" + rectime + "',";
            string handuptime = trunk.Handuptime.ToLocalTime().ToString("yyyyMMdd HH:mm:ss");
            sql += "handuptime='" + handuptime + "',";
            string islink = trunk.Islink;
            sql += "islink='" + islink + "',";
            if (inline != null)
            {
                string inlinenum = inline.Number.ToString();
                sql += " inline = '" + inlinenum + "',";
                string gonghao = inline.Gonghao;
                sql += " gonghao='" + gonghao + "',";
            }
            if (sql.EndsWith(","))
            {
                sql = sql.Substring(0, sql.Length - 1);
            }
            sql += " where id='" + trunk.Id + "'";
            // log.Debug("更新数据" + sql);
            SendMessage(sql);
        }

        /**
         * 给web服务发送信息
         **/
        private static void SendMessage(string sql)
        {
            string address = XmlService.getProperty("WebServer", "uri");
            Uri uri = new Uri(address);
            WebClient client = new WebClient();
            client.UploadStringCompleted += new UploadStringCompletedEventHandler(client_UploadStringCompleted);
            client.UploadStringAsync(uri, sql);
        }

        static void client_UploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            if (e.Error != null)
            {
                log.Debug("保存数据失败!" + e.Error.ToString());
            }
        }

       
      
       
    
    }
}
