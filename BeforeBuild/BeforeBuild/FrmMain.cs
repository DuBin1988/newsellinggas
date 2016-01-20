using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace BeforeBuild
{
    public delegate void HintUpdater(String hint);

    public partial class FrmMain : Form
    {
        log4net.ILog log = log4net.LogManager.GetLogger(typeof(BeforeBuild.FrmMain)); 
        public static string CONFIG_FILE = "partial.config";

        public FrmMain()
        {
            InitializeComponent();
            srcDir.Text = System.Windows.Forms.Application.StartupPath + "\\";
            dstDir.Text = srcDir.Text + "backup\\";
        }

        private void btnChooseSrcFolder_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog dialog = new FolderBrowserDialog();
            if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                srcDir.Text = dialog.SelectedPath;
                if(!srcDir.Text.EndsWith("\\"))
                    srcDir.Text = dialog.SelectedPath + "\\";
            }
        }

        private void btnChooseDstFolder_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog dialog = new FolderBrowserDialog();
            if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                dstDir.Text = dialog.SelectedPath;
                if (!dstDir.Text.EndsWith("\\"))
                    dstDir.Text = dialog.SelectedPath + "\\";
            }
        }

        public void SetHint(string hint)
        {
            if (this.InvokeRequired)
            {
                HintUpdater updater = new HintUpdater(SetHint);
                this.Invoke(updater, new object[] { hint });
                return;
            }
            this.lstLog.Items.Add(hint);
        }

        private void btnGo_Click(object sender, EventArgs e)
        {
             if(dstDir.Text.ToUpper().StartsWith(srcDir.Text.ToUpper()))
             {
                 SetHint("目标目录不能在源目录下。请重新选择。");
                 return;
             }

             if (!File.Exists(srcDir.Text + CONFIG_FILE))
             {
                 SetHint("替换文件" + srcDir.Text + CONFIG_FILE + "不存在。");
                 return;
             }
             bool clearDst = false;
             if (Directory.Exists(dstDir.Text))
             {
                 if (MessageBox.Show("这将会把目标路径的所有文件删除。确定要进行吗？", "警告", MessageBoxButtons.YesNo) != DialogResult.Yes)
                     return;
                 clearDst = true;
             }
             else
                 Directory.CreateDirectory(dstDir.Text);

             lstLog.Items.Clear();

             Substitution sub = new Substitution(this, clearDst, srcDir.Text, dstDir.Text);
             new Thread(new ThreadStart(sub.Run)).Start();
     }
    }

}
