using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;
using log4net;

namespace voice_card.helper
{
    class DBSaveHelper :RecordSave
    {

        private static ILog log = LogManager.GetLogger(typeof(DBSaveHelper));
        public void Save(LineInfo trunk, LineInfo inline)
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
            //Console.WriteLine("插入数据:" + sql);
            DBHelper.executeNonQuery(sql);
        }

        private static void Update(LineInfo trunk, LineInfo inline)
        {
            string sql = "update t_comingrecord set ";
            //主叫
            if (inline != null && inline.CallerPhone != null && !inline.CallerPhone.Equals(""))
            {
                Console.Write("号码" + inline.CallerPhone);
                //截取11位手机号码
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
                if(inline.RecordFile != null)
                {
                   
                    string recordfile = inline.RecordFile.ToString();
                    log.Debug("录音文件号:"+recordfile+"");
                     sql += " recordfile='" + recordfile + "',";
                }
            }
            
            
            if (sql.EndsWith(","))
            {
                sql = sql.Substring(0, sql.Length - 1);
            }
            sql += " where id='" + trunk.Id + "'";
            //Console.WriteLine("更新数据" + sql);
            log.Debug("更新语句:"+sql);
            DBHelper.executeNonQuery(sql);

        }
    }
}
