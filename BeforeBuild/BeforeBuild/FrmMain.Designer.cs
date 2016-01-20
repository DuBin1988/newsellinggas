namespace BeforeBuild
{
    partial class FrmMain
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.panel1 = new System.Windows.Forms.Panel();
            this.btnChooseSrcFolder = new System.Windows.Forms.Button();
            this.label3 = new System.Windows.Forms.Label();
            this.btnGo = new System.Windows.Forms.Button();
            this.btnChooseDstFolder = new System.Windows.Forms.Button();
            this.dstDir = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.srcDir = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.lstLog = new System.Windows.Forms.ListBox();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.btnChooseSrcFolder);
            this.panel1.Controls.Add(this.label3);
            this.panel1.Controls.Add(this.btnGo);
            this.panel1.Controls.Add(this.btnChooseDstFolder);
            this.panel1.Controls.Add(this.dstDir);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.srcDir);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Margin = new System.Windows.Forms.Padding(10);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(506, 185);
            this.panel1.TabIndex = 0;
            // 
            // btnChooseSrcFolder
            // 
            this.btnChooseSrcFolder.Location = new System.Drawing.Point(97, 3);
            this.btnChooseSrcFolder.Name = "btnChooseSrcFolder";
            this.btnChooseSrcFolder.Size = new System.Drawing.Size(75, 23);
            this.btnChooseSrcFolder.TabIndex = 7;
            this.btnChooseSrcFolder.Text = "选择路径";
            this.btnChooseSrcFolder.UseVisualStyleBackColor = true;
            this.btnChooseSrcFolder.Click += new System.EventHandler(this.btnChooseSrcFolder_Click);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(13, 159);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(67, 13);
            this.label3.TabIndex = 6;
            this.label3.Text = "处理日志：";
            // 
            // btnGo
            // 
            this.btnGo.Location = new System.Drawing.Point(16, 121);
            this.btnGo.Name = "btnGo";
            this.btnGo.Size = new System.Drawing.Size(75, 23);
            this.btnGo.TabIndex = 5;
            this.btnGo.Text = "处理";
            this.btnGo.UseVisualStyleBackColor = true;
            this.btnGo.Click += new System.EventHandler(this.btnGo_Click);
            // 
            // btnChooseDstFolder
            // 
            this.btnChooseDstFolder.Location = new System.Drawing.Point(97, 66);
            this.btnChooseDstFolder.Name = "btnChooseDstFolder";
            this.btnChooseDstFolder.Size = new System.Drawing.Size(75, 23);
            this.btnChooseDstFolder.TabIndex = 4;
            this.btnChooseDstFolder.Text = "选择路径";
            this.btnChooseDstFolder.UseVisualStyleBackColor = true;
            this.btnChooseDstFolder.Click += new System.EventHandler(this.btnChooseDstFolder_Click);
            // 
            // dstDir
            // 
            this.dstDir.Enabled = false;
            this.dstDir.Location = new System.Drawing.Point(15, 95);
            this.dstDir.Name = "dstDir";
            this.dstDir.Size = new System.Drawing.Size(479, 20);
            this.dstDir.TabIndex = 3;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 71);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(79, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "目标项目路径";
            // 
            // srcDir
            // 
            this.srcDir.Enabled = false;
            this.srcDir.Location = new System.Drawing.Point(15, 33);
            this.srcDir.Name = "srcDir";
            this.srcDir.Size = new System.Drawing.Size(479, 20);
            this.srcDir.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(67, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "源项目路径";
            // 
            // lstLog
            // 
            this.lstLog.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lstLog.FormattingEnabled = true;
            this.lstLog.Location = new System.Drawing.Point(0, 185);
            this.lstLog.Name = "lstLog";
            this.lstLog.Size = new System.Drawing.Size(506, 209);
            this.lstLog.TabIndex = 1;
            // 
            // FrmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(506, 394);
            this.Controls.Add(this.lstLog);
            this.Controls.Add(this.panel1);
            this.Name = "FrmMain";
            this.Text = "BeforeBuild";
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.ListBox lstLog;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button btnGo;
        private System.Windows.Forms.Button btnChooseDstFolder;
        private System.Windows.Forms.TextBox dstDir;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox srcDir;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btnChooseSrcFolder;
    }
}

