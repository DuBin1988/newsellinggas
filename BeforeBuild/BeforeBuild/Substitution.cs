using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;

namespace BeforeBuild
{
    public class Substitution
    {
        private FrmMain form;
        private bool clearDst;
        private string srcDir;
        private string dstDir;
        log4net.ILog log = log4net.LogManager.GetLogger(typeof(BeforeBuild.Substitution)); 

        public Substitution(FrmMain frmMain, bool clearDst, string srcDir, string dstDir)
        {
            // TODO: Complete member initialization
            this.form = frmMain;
            this.clearDst = clearDst;
            this.srcDir = srcDir;
            this.dstDir = dstDir;
        }

        public void Run()
        {
            //清空目标文件夹
            if(clearDst)
            {
                clearFolder(dstDir);
                Log("清空目标文件夹。");
            }
            Log("-------------------扫描替换文件-----------------------");
            IDictionary<string, string> partials = new Dictionary<string, string>();
            if (!CompilePartials(srcDir + FrmMain.CONFIG_FILE, partials))
            {
                Log("处理结束：解析替换文件出错。");
                return;
            }
            Log("----------------扫描替换文件结束-----------------------");
            //handle the xaml files recursively, copy other type of file as it is
            Log("----------------替换文件开始-----------------------");
            WalkThrough(0, srcDir, dstDir, partials);
            Log("----------------替换文件结束-----------------------");
        }

        private bool WalkThrough(int indent, string sd, string dd, IDictionary<string, string> partials)
        {
            DirectoryInfo dir = new DirectoryInfo(sd);

            foreach (FileInfo fi in dir.GetFiles())
            {
                Log(indent, "处理文件" + fi.Name);
                if(fi.Name.ToUpper().EndsWith(".XAML"))
                {
                    //查找注释项，并替换
                    Substitute(indent, fi.FullName, dd + fi.Name, partials);
                }
                else
                {
                    File.Copy(fi.FullName, dd + fi.Name);
                }
            }

            foreach (DirectoryInfo di in dir.GetDirectories())
            {
                String subDir = di.FullName.Substring(srcDir.Length);
                if (!subDir.EndsWith("\\"))
                    subDir += "\\";
                Log(indent, "创建目录" + subDir.Substring(0, subDir.Length-1));
                Directory.CreateDirectory(dstDir + subDir);
                return WalkThrough(indent+1, srcDir + subDir, dstDir + subDir, partials);
            }
            return true;
        }

        private void Log(int indent, string p)
        {
            Log("".PadLeft(indent * 7, ' ') + p);
        }

        private void Substitute(int indent, string sName, string dName, IDictionary<string, string> partials)
        {
            Boolean needsCancelOff = false;
            String key = "";
            String value = "";
            String lines = "";
            int lineNumber = 1;
            Boolean hasError = false;
            String comment = "";
            StreamReader sr = File.OpenText(sName);
            try
            {
                string line = sr.ReadLine();
                while (line != null)
                {
                    String aline = line.Trim();
                    if (aline.StartsWith("<!--"))
                    {
                        //错误
                        if (!aline.EndsWith("-->"))
                        {
                            Log(indent+1, String.Format("行号：{0} 错误：注释未结束。", new object[] { lineNumber }));
                            hasError = true;
                            break;
                        }
                        else
                        {
                            if (!needsCancelOff)
                            {
                                needsCancelOff = true;
                                key = aline.Substring(4, aline.Length - 7).Trim();
                                if (key.StartsWith("/"))
                                {
                                    Log(indent+1, String.Format("行号：{0} 错误：注释{1}没有匹配的开始注释。", new object[] { lineNumber, key }));
                                    hasError = true;
                                    break;
                                }
                                value = "";
                                comment = line;
                            }
                            else
                            {
                                String backslashKey = aline.Substring(4, aline.Length - 7).Trim();
                                if (!("/" + key).Equals(backslashKey))
                                {
                                    Log(indent+1, String.Format("行号：{0} 错误：注释{1}没有匹配的结束注释，注释{2}没有匹配的开始注释。", new object[] { lineNumber, key, backslashKey }));
                                    hasError = true;
                                    break;
                                }
                                needsCancelOff = false;
                                Log("----------------------------------------------------");
                                Log(indent+1, String.Format("发现注释项：" + key));
                                if (partials.Keys.Contains(key))
                                {
                                    lines = lines + Environment.NewLine + comment + Environment.NewLine + partials[key] + Environment.NewLine + line;
                                    Log(indent + 1, String.Format("替换注释项：" + key));
                                    Log("----------------------------------------------------");
                                }
                                else
                                    lines = lines + Environment.NewLine + comment + Environment.NewLine + value + Environment.NewLine + line;
                            }
                        }
                    }
                    else
                    {
                        if (needsCancelOff)
                            value = value + Environment.NewLine + line;
                        else
                            lines = lines + Environment.NewLine + line;
                    }
                    lineNumber++;
                    line = sr.ReadLine();
                }
                //still needs cancel off, no matching closing comment
                if (needsCancelOff)
                {
                    Log(indent+1, String.Format("行号：{0} 错误：注释{1}没有匹配的结束注释。", new object[] { lineNumber, key }));
                    hasError = true;
                }
                if(!hasError)
                {
                    WriteFile(dName, lines);
                }
            }
            finally
            {
                sr.Close();
            }
        }

        private static void WriteFile(string dName, String lines)
        {
            StreamWriter sw = null;
            try
            {
                sw = File.CreateText(dName);
                sw.Write(lines);
            }
            finally
            {
                if(sw != null)
                    sw.Close();
            }
        }

        private void Log(String msg)
        {
            form.SetHint(msg);
            log.Info(msg);
        }

        //error stop compiler
        public Boolean CompilePartials(string cfn, IDictionary<string, string> fragments)
        {
            Boolean needsCancelOff = false;
            String key = "";
            String value = "";
            StreamReader sr = File.OpenText(cfn);
            try
            {
                string line = sr.ReadLine();
                int lineNumber = 1;
                while (line != null)
                {
                    String aline = line.Trim();
                    if (aline.StartsWith("<!--"))
                    {
                        //错误
                        if (!aline.EndsWith("-->"))
                        {
                            Log(String.Format("行号：{0} 错误：注释未结束。", new object[] { lineNumber }));
                            return false;
                        }
                        else
                        {
                            if (!needsCancelOff)
                            {
                                needsCancelOff = true;
                                key = aline.Substring(4, aline.Length - 7).Trim();
                                if (key.StartsWith("/"))
                                {
                                    Log(String.Format("行号：{0} 错误：注释{1}没有匹配的开始注释。", new object[] { lineNumber, key }));
                                    return false;
                                }
                                value = "";
                            }
                            else
                            {
                                String backslashKey = aline.Substring(4, aline.Length - 7).Trim();
                                if (!("/" + key).Equals(backslashKey))
                                {
                                    Log(String.Format("行号：{0} 错误：注释{1}没有匹配的结束注释，注释{2}没有匹配的开始注释。", new object[] { lineNumber, key, backslashKey }));
                                    return false;
                                }
                                needsCancelOff = false;
                                Log(String.Format("发现注释项：" + key));
                                fragments.Add(key, value);
                            }
                        }
                    }
                    else
                    {
                        if (needsCancelOff)
                        {
                            if (value.Length > 0)
                                value = value + Environment.NewLine + line;
                            else
                                value = line;
                        }
                    }
                    lineNumber++;
                    line = sr.ReadLine();
                }
                //still needs cancel off, no matching closing comment
                if (needsCancelOff)
                {
                    Log(String.Format("行号：{0} 错误：注释{1}没有匹配的结束注释。", new object[] { lineNumber, key }));
                    return false;
                }
                return true;            
            }
            finally
            {
                sr.Close();
            }
        }


        private void clearFolder(string FolderName)
        {
            DirectoryInfo dir = new DirectoryInfo(FolderName);

            foreach (FileInfo fi in dir.GetFiles())
            {
                fi.Delete();
            }

            foreach (DirectoryInfo di in dir.GetDirectories())
            {
                clearFolder(di.FullName);
                di.Delete();
            }
        }

        
    }
}
