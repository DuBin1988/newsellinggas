using System;
using System.Net;
using System.IO.IsolatedStorage;
using System.IO;
using System.Text;

namespace Com.Aote.Logs
{
    public class FileAppender : IAppender
    {

        public void ShowMessage(string msg)
        {
            //检查是否有当天日期名称文件，没有创建，已创建，追加
            //System.IO.FileStream fs = null;
            StreamWriter sw = null;
            try
            {
                //检查是否有当天日期名称文件，没有创建，已创建，追加
                string fileName = DateTime.Now.ToString("yyyy-MM-dd");
                string path = "C:/WCSlogs/" + fileName + "WCS.txt";
                byte[] data = new UTF8Encoding().GetBytes(msg);
                sw = new StreamWriter(path, true, Encoding.GetEncoding("GB2312"));
                sw.WriteLine(msg);
            }
            catch (Exception e)
            {

            }
            finally
            {
                if (sw != null)
                    sw.Close();
            }
        }
    }
}
