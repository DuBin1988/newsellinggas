namespace QwIcDazhuDemo
{
    partial class DemoFrm
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.button1 = new System.Windows.Forms.Button();
            this.btnIsNewCard = new System.Windows.Forms.Button();
            this.label13 = new System.Windows.Forms.Label();
            this.cboBaud = new System.Windows.Forms.ComboBox();
            this.label1 = new System.Windows.Forms.Label();
            this.cboPort = new System.Windows.Forms.ComboBox();
            this.btnExit = new System.Windows.Forms.Button();
            this.btnClearCard = new System.Windows.Forms.Button();
            this.txtRepairState = new System.Windows.Forms.TextBox();
            this.label14 = new System.Windows.Forms.Label();
            this.txtBuyGasTotal = new System.Windows.Forms.TextBox();
            this.label11 = new System.Windows.Forms.Label();
            this.txtBuyGasRemain = new System.Windows.Forms.TextBox();
            this.label12 = new System.Windows.Forms.Label();
            this.txtBuyGasTime = new System.Windows.Forms.TextBox();
            this.label9 = new System.Windows.Forms.Label();
            this.txtBuyGasCount = new System.Windows.Forms.TextBox();
            this.label10 = new System.Windows.Forms.Label();
            this.txtBuyGasVal = new System.Windows.Forms.TextBox();
            this.label7 = new System.Windows.Forms.Label();
            this.txtReadState = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.txtCardID = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.txtUserID = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.txtMeterID = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.txtCardType = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.txtCompanyCode = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.btnMakeCard = new System.Windows.Forms.Button();
            this.lbMsg = new System.Windows.Forms.ListBox();
            this.btnRepairCard = new System.Windows.Forms.Button();
            this.btnWriteOrders = new System.Windows.Forms.Button();
            this.btnReadCard = new System.Windows.Forms.Button();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 0);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(632, 446);
            this.tabControl1.TabIndex = 7;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.button1);
            this.tabPage1.Controls.Add(this.btnIsNewCard);
            this.tabPage1.Controls.Add(this.label13);
            this.tabPage1.Controls.Add(this.cboBaud);
            this.tabPage1.Controls.Add(this.label1);
            this.tabPage1.Controls.Add(this.cboPort);
            this.tabPage1.Controls.Add(this.btnExit);
            this.tabPage1.Controls.Add(this.btnClearCard);
            this.tabPage1.Controls.Add(this.txtRepairState);
            this.tabPage1.Controls.Add(this.label14);
            this.tabPage1.Controls.Add(this.txtBuyGasTotal);
            this.tabPage1.Controls.Add(this.label11);
            this.tabPage1.Controls.Add(this.txtBuyGasRemain);
            this.tabPage1.Controls.Add(this.label12);
            this.tabPage1.Controls.Add(this.txtBuyGasTime);
            this.tabPage1.Controls.Add(this.label9);
            this.tabPage1.Controls.Add(this.txtBuyGasCount);
            this.tabPage1.Controls.Add(this.label10);
            this.tabPage1.Controls.Add(this.txtBuyGasVal);
            this.tabPage1.Controls.Add(this.label7);
            this.tabPage1.Controls.Add(this.txtReadState);
            this.tabPage1.Controls.Add(this.label8);
            this.tabPage1.Controls.Add(this.txtCardID);
            this.tabPage1.Controls.Add(this.label5);
            this.tabPage1.Controls.Add(this.txtUserID);
            this.tabPage1.Controls.Add(this.label6);
            this.tabPage1.Controls.Add(this.txtMeterID);
            this.tabPage1.Controls.Add(this.label4);
            this.tabPage1.Controls.Add(this.txtCardType);
            this.tabPage1.Controls.Add(this.label3);
            this.tabPage1.Controls.Add(this.txtCompanyCode);
            this.tabPage1.Controls.Add(this.label2);
            this.tabPage1.Controls.Add(this.btnMakeCard);
            this.tabPage1.Controls.Add(this.lbMsg);
            this.tabPage1.Controls.Add(this.btnRepairCard);
            this.tabPage1.Controls.Add(this.btnWriteOrders);
            this.tabPage1.Controls.Add(this.btnReadCard);
            this.tabPage1.Location = new System.Drawing.Point(4, 21);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(624, 421);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "调用DLL";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(512, 185);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(87, 25);
            this.button1.TabIndex = 42;
            this.button1.Text = "退气";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            this.button1.MouseEnter += new System.EventHandler(this.button1_MouseEnter);
            // 
            // btnIsNewCard
            // 
            this.btnIsNewCard.Location = new System.Drawing.Point(512, 247);
            this.btnIsNewCard.Name = "btnIsNewCard";
            this.btnIsNewCard.Size = new System.Drawing.Size(87, 25);
            this.btnIsNewCard.TabIndex = 40;
            this.btnIsNewCard.Text = "判卡";
            this.btnIsNewCard.UseVisualStyleBackColor = true;
            this.btnIsNewCard.Click += new System.EventHandler(this.btnIsNewCard_Click);
            this.btnIsNewCard.MouseEnter += new System.EventHandler(this.btnIsNewCard_MouseEnter);
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Location = new System.Drawing.Point(429, 13);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(41, 12);
            this.label13.TabIndex = 39;
            this.label13.Text = "波特率";
            // 
            // cboBaud
            // 
            this.cboBaud.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboBaud.FormattingEnabled = true;
            this.cboBaud.Items.AddRange(new object[] {
            "9600",
            "19200",
            "28800",
            "38400",
            "57600",
            "115200"});
            this.cboBaud.Location = new System.Drawing.Point(476, 10);
            this.cboBaud.Name = "cboBaud";
            this.cboBaud.Size = new System.Drawing.Size(121, 20);
            this.cboBaud.TabIndex = 38;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(196, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 12);
            this.label1.TabIndex = 37;
            this.label1.Text = "端口";
            // 
            // cboPort
            // 
            this.cboPort.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboPort.FormattingEnabled = true;
            this.cboPort.Location = new System.Drawing.Point(231, 10);
            this.cboPort.Name = "cboPort";
            this.cboPort.Size = new System.Drawing.Size(121, 20);
            this.cboPort.TabIndex = 36;
            // 
            // btnExit
            // 
            this.btnExit.Location = new System.Drawing.Point(512, 278);
            this.btnExit.Name = "btnExit";
            this.btnExit.Size = new System.Drawing.Size(87, 25);
            this.btnExit.TabIndex = 35;
            this.btnExit.Text = "退出";
            this.btnExit.UseVisualStyleBackColor = true;
            this.btnExit.Click += new System.EventHandler(this.btnExit_Click);
            // 
            // btnClearCard
            // 
            this.btnClearCard.Location = new System.Drawing.Point(512, 213);
            this.btnClearCard.Name = "btnClearCard";
            this.btnClearCard.Size = new System.Drawing.Size(87, 25);
            this.btnClearCard.TabIndex = 34;
            this.btnClearCard.Text = "清卡";
            this.btnClearCard.UseVisualStyleBackColor = true;
            this.btnClearCard.Click += new System.EventHandler(this.btnClearCard_Click);
            this.btnClearCard.MouseEnter += new System.EventHandler(this.btnClearCard_MouseEnter);
            // 
            // txtRepairState
            // 
            this.txtRepairState.Location = new System.Drawing.Point(65, 170);
            this.txtRepairState.Name = "txtRepairState";
            this.txtRepairState.Size = new System.Drawing.Size(154, 21);
            this.txtRepairState.TabIndex = 31;
            this.txtRepairState.Visible = false;
            // 
            // label14
            // 
            this.label14.AutoSize = true;
            this.label14.Location = new System.Drawing.Point(6, 173);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(53, 12);
            this.label14.TabIndex = 30;
            this.label14.Text = "补卡标识";
            this.label14.Visible = false;
            // 
            // txtBuyGasTotal
            // 
            this.txtBuyGasTotal.Location = new System.Drawing.Point(310, 260);
            this.txtBuyGasTotal.Name = "txtBuyGasTotal";
            this.txtBuyGasTotal.Size = new System.Drawing.Size(154, 21);
            this.txtBuyGasTotal.TabIndex = 29;
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(271, 263);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(35, 12);
            this.label11.TabIndex = 28;
            this.label11.Text = "ljgql";
            // 
            // txtBuyGasRemain
            // 
            this.txtBuyGasRemain.Location = new System.Drawing.Point(65, 260);
            this.txtBuyGasRemain.Name = "txtBuyGasRemain";
            this.txtBuyGasRemain.Size = new System.Drawing.Size(154, 21);
            this.txtBuyGasRemain.TabIndex = 27;
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Location = new System.Drawing.Point(32, 263);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(29, 12);
            this.label12.TabIndex = 26;
            this.label12.Text = "syql";
            // 
            // txtBuyGasTime
            // 
            this.txtBuyGasTime.Location = new System.Drawing.Point(310, 212);
            this.txtBuyGasTime.Name = "txtBuyGasTime";
            this.txtBuyGasTime.Size = new System.Drawing.Size(154, 21);
            this.txtBuyGasTime.TabIndex = 25;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(275, 215);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(29, 12);
            this.label9.TabIndex = 24;
            this.label9.Text = "sqrq";
            // 
            // txtBuyGasCount
            // 
            this.txtBuyGasCount.Location = new System.Drawing.Point(65, 212);
            this.txtBuyGasCount.Name = "txtBuyGasCount";
            this.txtBuyGasCount.Size = new System.Drawing.Size(154, 21);
            this.txtBuyGasCount.TabIndex = 23;
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(43, 215);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(17, 12);
            this.label10.TabIndex = 22;
            this.label10.Text = "cs";
            // 
            // txtBuyGasVal
            // 
            this.txtBuyGasVal.Location = new System.Drawing.Point(310, 170);
            this.txtBuyGasVal.Name = "txtBuyGasVal";
            this.txtBuyGasVal.Size = new System.Drawing.Size(154, 21);
            this.txtBuyGasVal.TabIndex = 21;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(288, 173);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(17, 12);
            this.label7.TabIndex = 20;
            this.label7.Text = "ql";
            // 
            // txtReadState
            // 
            this.txtReadState.Location = new System.Drawing.Point(310, 129);
            this.txtReadState.Name = "txtReadState";
            this.txtReadState.Size = new System.Drawing.Size(154, 21);
            this.txtReadState.TabIndex = 19;
            this.txtReadState.Visible = false;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(251, 132);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(53, 12);
            this.label8.TabIndex = 18;
            this.label8.Text = "读卡标识";
            this.label8.Visible = false;
            // 
            // txtCardID
            // 
            this.txtCardID.Location = new System.Drawing.Point(65, 129);
            this.txtCardID.Name = "txtCardID";
            this.txtCardID.Size = new System.Drawing.Size(154, 21);
            this.txtCardID.TabIndex = 17;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(38, 132);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(23, 12);
            this.label5.TabIndex = 16;
            this.label5.Text = "yhh";
            // 
            // txtUserID
            // 
            this.txtUserID.Location = new System.Drawing.Point(65, 89);
            this.txtUserID.Name = "txtUserID";
            this.txtUserID.Size = new System.Drawing.Size(154, 21);
            this.txtUserID.TabIndex = 15;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(43, 92);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(17, 12);
            this.label6.TabIndex = 14;
            this.label6.Text = "kh";
            // 
            // txtMeterID
            // 
            this.txtMeterID.Location = new System.Drawing.Point(310, 89);
            this.txtMeterID.Name = "txtMeterID";
            this.txtMeterID.Size = new System.Drawing.Size(154, 21);
            this.txtMeterID.TabIndex = 13;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(288, 92);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(17, 12);
            this.label4.TabIndex = 12;
            this.label4.Text = "tm";
            // 
            // txtCardType
            // 
            this.txtCardType.Location = new System.Drawing.Point(65, 49);
            this.txtCardType.Name = "txtCardType";
            this.txtCardType.Size = new System.Drawing.Size(154, 21);
            this.txtCardType.TabIndex = 11;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(38, 52);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(23, 12);
            this.label3.TabIndex = 10;
            this.label3.Text = "klx";
            // 
            // txtCompanyCode
            // 
            this.txtCompanyCode.Location = new System.Drawing.Point(310, 49);
            this.txtCompanyCode.Name = "txtCompanyCode";
            this.txtCompanyCode.Size = new System.Drawing.Size(154, 21);
            this.txtCompanyCode.TabIndex = 9;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(261, 52);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(41, 12);
            this.label2.TabIndex = 8;
            this.label2.Text = "供应商";
            // 
            // btnMakeCard
            // 
            this.btnMakeCard.Location = new System.Drawing.Point(512, 85);
            this.btnMakeCard.Name = "btnMakeCard";
            this.btnMakeCard.Size = new System.Drawing.Size(87, 25);
            this.btnMakeCard.TabIndex = 3;
            this.btnMakeCard.Text = "写新卡";
            this.btnMakeCard.UseVisualStyleBackColor = true;
            this.btnMakeCard.Click += new System.EventHandler(this.btnMakeCard_Click);
            this.btnMakeCard.MouseEnter += new System.EventHandler(this.btnMakeCard_MouseEnter);
            // 
            // lbMsg
            // 
            this.lbMsg.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.lbMsg.FormattingEnabled = true;
            this.lbMsg.ItemHeight = 12;
            this.lbMsg.Location = new System.Drawing.Point(3, 318);
            this.lbMsg.Name = "lbMsg";
            this.lbMsg.Size = new System.Drawing.Size(618, 100);
            this.lbMsg.TabIndex = 2;
            // 
            // btnRepairCard
            // 
            this.btnRepairCard.Location = new System.Drawing.Point(512, 157);
            this.btnRepairCard.Name = "btnRepairCard";
            this.btnRepairCard.Size = new System.Drawing.Size(87, 25);
            this.btnRepairCard.TabIndex = 5;
            this.btnRepairCard.Text = "补卡";
            this.btnRepairCard.UseVisualStyleBackColor = true;
            this.btnRepairCard.Click += new System.EventHandler(this.btnRepairCard_Click);
            this.btnRepairCard.MouseEnter += new System.EventHandler(this.btnRepairCard_MouseEnter);
            // 
            // btnWriteOrders
            // 
            this.btnWriteOrders.Location = new System.Drawing.Point(512, 125);
            this.btnWriteOrders.Name = "btnWriteOrders";
            this.btnWriteOrders.Size = new System.Drawing.Size(87, 25);
            this.btnWriteOrders.TabIndex = 4;
            this.btnWriteOrders.Text = "写用户卡";
            this.btnWriteOrders.UseVisualStyleBackColor = true;
            this.btnWriteOrders.Click += new System.EventHandler(this.btnWriteOrders_Click);
            this.btnWriteOrders.MouseEnter += new System.EventHandler(this.btnWriteOrders_MouseEnter);
            // 
            // btnReadCard
            // 
            this.btnReadCard.Location = new System.Drawing.Point(512, 45);
            this.btnReadCard.Name = "btnReadCard";
            this.btnReadCard.Size = new System.Drawing.Size(87, 25);
            this.btnReadCard.TabIndex = 0;
            this.btnReadCard.Text = "读卡";
            this.btnReadCard.UseVisualStyleBackColor = true;
            this.btnReadCard.Click += new System.EventHandler(this.btnReadCard_Click);
            this.btnReadCard.MouseEnter += new System.EventHandler(this.btnReadCard_MouseEnter);
            // 
            // DemoFrm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(632, 446);
            this.Controls.Add(this.tabControl1);
            this.MaximumSize = new System.Drawing.Size(640, 480);
            this.MinimumSize = new System.Drawing.Size(640, 480);
            this.Name = "DemoFrm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "IC卡Demo";
            this.Load += new System.EventHandler(this.DemoFrm_Load);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TextBox txtRepairState;
        private System.Windows.Forms.Label label14;
        private System.Windows.Forms.TextBox txtBuyGasTotal;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.TextBox txtBuyGasRemain;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.TextBox txtBuyGasTime;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.TextBox txtBuyGasCount;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.TextBox txtBuyGasVal;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox txtReadState;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.TextBox txtCardID;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox txtUserID;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.TextBox txtMeterID;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.TextBox txtCardType;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox txtCompanyCode;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button btnMakeCard;
        private System.Windows.Forms.ListBox lbMsg;
        private System.Windows.Forms.Button btnRepairCard;
        private System.Windows.Forms.Button btnWriteOrders;
        private System.Windows.Forms.Button btnReadCard;
        private System.Windows.Forms.Button btnExit;
        private System.Windows.Forms.Button btnClearCard;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.ComboBox cboBaud;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ComboBox cboPort;
        private System.Windows.Forms.Button btnIsNewCard;
        private System.Windows.Forms.Button button1;
    }
}

