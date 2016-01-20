// demonewDlg.cpp : implementation file
//

#include "stdafx.h"
#include "demonew.h"
#include "demonewDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
		// No message handlers
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CDemonewDlg dialog

CDemonewDlg::CDemonewDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CDemonewDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CDemonewDlg)
	m_gasvalue = 0;
	m_rgasvalue = 0;
	m_wgasvalue = 0;
	m_rcardno = _T("");
	m_cardno = _T("");
	m_com = -1;
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CDemonewDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CDemonewDlg)
	DDX_Text(pDX, IDC_EDIT2, m_gasvalue);
	DDV_MinMaxInt(pDX, m_gasvalue, 0, 999999);
	DDX_Text(pDX, IDC_EDIT4, m_rgasvalue);
	DDX_Text(pDX, IDC_EDIT5, m_wgasvalue);
	DDV_MinMaxInt(pDX, m_wgasvalue, 0, 999999);
	DDX_Text(pDX, IDC_EDIT3, m_rcardno);
	DDV_MaxChars(pDX, m_rcardno, 8);
	DDX_Text(pDX, IDC_EDIT1, m_cardno);
	DDV_MaxChars(pDX, m_cardno, 8);
	DDX_CBIndex(pDX, IDC_COMBO1, m_com);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CDemonewDlg, CDialog)
	//{{AFX_MSG_MAP(CDemonewDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_BUTTON1, OnButton1)
	ON_BN_CLICKED(IDC_BUTTON2, OnButton2)
	ON_BN_CLICKED(IDC_BUTTON3, OnButton3)
	ON_BN_CLICKED(IDC_BUTTON4, OnButton4)
	ON_BN_CLICKED(IDC_BUTTON5, OnButton5)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CDemonewDlg message handlers

BOOL CDemonewDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CDemonewDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CDemonewDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CDemonewDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CDemonewDlg::OnButton1() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE HmyDLL;
	HmyDLL=LoadLibrary("WRwCard.dll");
	int n,port;
   ////第四步：定义函数地址变量
    UpdateData(TRUE);
	port=m_com; 
   if(HmyDLL!=NULL)
   {
     INT (_stdcall *CheckGasCard)(__int16 com, __int32 baud);
	 CheckGasCard=(INT (__stdcall *)(__int16,__int32))GetProcAddress(HmyDLL,"CheckGasCard");
	 n=CheckGasCard(port,9600);
	 if (n==0)
	 {
	    MessageBox("判卡成功!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 } 
	 else
	 {
        MessageBox("判卡失败!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 }
   }
   else
   {
	   UpdateData(FALSE);
     //FreeLibrary(HmyDLL);
   }
}

void CDemonewDlg::OnButton2() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE HmyDLL;
	HmyDLL=LoadLibrary("WRwCard.dll");
	int n,ql,port;
   ////第四步：定义函数地址变量
	UpdateData(TRUE);
	ql=m_gasvalue;
	port=m_com;
   if(HmyDLL!=NULL)
   {
     INT (_stdcall *WriteNewCard)(__int16 com, __int32 baud, unsigned char *kmm, __int16 klx, __int16 kzt, unsigned char *kh, unsigned char *dqdm, unsigned char *yhh, unsigned char *tm, __int32 ql, __int16 cs, __int32 ljgql, __int16 bkcs, __int32 ljyql, __int32 bjql, __int32 czsx, __int32 tzed, unsigned char *sqrq ,__int32  oldprice, __int32  newprice ,unsigned char * sxrq,unsigned char * sxbj);
	 WriteNewCard=(INT (__stdcall *)(__int16, __int32, unsigned char *, __int16, __int16, unsigned char *, unsigned char *, unsigned char *, unsigned char *, __int32, __int16, __int32, __int16, __int32, __int32, __int32, __int32, unsigned char * ,__int32, __int32,unsigned char *,unsigned char *))GetProcAddress(HmyDLL,"WriteNewCard");
	 //n=WriteNewCard(1,9600,(unsigned char *)"",0,0,(unsigned char *)m_cardno,(unsigned char *)"",(unsigned char *)"",(unsigned char *)"",m_gasvalue,0,0,0,0,0,0,0,(unsigned char *)"",0,0,(unsigned char *)"",(unsigned char *)"");
	 n=WriteNewCard(port,9600,(unsigned char *)"",0,0,(unsigned char*)m_cardno.GetBuffer(m_cardno.GetLength()),(unsigned char *)"",(unsigned char *)"",(unsigned char *)"",ql,0,0,0,0,0,0,0,(unsigned char *)"",0,0,(unsigned char *)"",(unsigned char *)"");

	 if (n==0)
	 {
	    MessageBox("制卡成功!");
		m_cardno="";
		m_gasvalue=0;
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 } 
	 else
	 {
        MessageBox("制卡失败!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 }
   }
   else
   {
	   UpdateData(FALSE);
     //FreeLibrary(HmyDLL);
   }
}

void CDemonewDlg::OnButton3() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE HmyDLL;
	HmyDLL=LoadLibrary("WRwCard.dll");
	int n,port;
   ////第四步：定义函数地址变量
     UpdateData(TRUE);
	port=m_com;
   if(HmyDLL!=NULL)
   {
     INT (_stdcall *FormatGasCard )(__int16 com, __int32 baud, unsigned char *kmm, __int16 klx, unsigned char *kh, unsigned char *dqdm);
	 FormatGasCard =(INT (__stdcall *)(__int16, __int32, unsigned char *, __int16, unsigned char *, unsigned char *))GetProcAddress(HmyDLL,"FormatGasCard");
	 n=FormatGasCard (port,9600,(unsigned char *)"",0,(unsigned char *)"",(unsigned char *)"");
	 if (n==0)
	 {
	    MessageBox("清卡成功!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 } 
	 else
	 {
        MessageBox("清卡失败!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 }
   }
   else
   {
	   UpdateData(FALSE);
     //FreeLibrary(HmyDLL);
   }
}

void CDemonewDlg::OnButton4() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE HmyDLL;
	HmyDLL=LoadLibrary("WRwCard.dll");
	int n,port;
   ////第四步：定义函数地址变量
	CString str;
	unsigned char kh[9];
	unsigned char kmm[2],dqdm[2],yhh[2],tm[2],sqrq[2],sxrq[2],sxbj[2];
	memset(kh, 0, sizeof(kh));
	memset(kmm, 0, sizeof(kmm));
	memset(dqdm, 0, sizeof(dqdm));
	memset(yhh, 0, sizeof(yhh));
	memset(tm, 0, sizeof(tm));
	memset(sqrq, 0, sizeof(sqrq));
	memset(sxrq, 0, sizeof(sxrq));
	memset(sxbj, 0, sizeof(sxbj));
	UpdateData(TRUE);
  __int16 klx,kzt,cs,bkcs;
  __int32 ql,ljgql,ljyql,syql,bjql,czsx,tzed,oldprice,newprice;
    port=m_com; 
   if(HmyDLL!=NULL)
   {
     INT (_stdcall *ReadGasCard )(__int16 com, __int32 baud, unsigned char *kmm, __int16 *klx, __int16 *kzt, unsigned char *kh, unsigned char *dqdm, unsigned char *yhh, unsigned char *tm, __int32 *ql, __int16 *cs, __int32 *ljgql,__int16 *bkcs, __int32 *ljyql, __int32 *syql, __int32 *bjql, __int32 *czsx, __int32 *tzed, unsigned char *sqrq, __int32 *oldprice, __int32 *newprice ,unsigned char * sxrq,unsigned char * sxbj);
	 ReadGasCard =(INT (__stdcall *)(__int16, __int32, unsigned char *, __int16 *, __int16 *, unsigned char *, unsigned char *, unsigned char *, unsigned char *, __int32 *, __int16 *, __int32 *,__int16 *, __int32 *, __int32 *, __int32 *, __int32 *, __int32 *, unsigned char *, __int32 *, __int32 *,unsigned char *,unsigned char *))GetProcAddress(HmyDLL,"ReadGasCard");
	 n=ReadGasCard(port,9600, (unsigned char *)kmm,&klx,&kzt,(unsigned char *)kh,(unsigned char *)dqdm,(unsigned char *)yhh,(unsigned char *)tm,&ql,&cs,&ljgql,&bkcs,&ljyql,&syql,&bjql,&czsx,&tzed, (unsigned char *)sqrq,&oldprice,&newprice ,(unsigned char *)sxrq,(unsigned char *)sxbj);
	 if (n==0)
	 {
		MessageBox("读卡成功!");
		//m_rcardno=CString::Format(_T("%d,%f,%ld,%lf,%c,%s"),int,float,long,double,char,char*)//各类型到CString
		//str.Format(_T("%s"), kh);//各类型到CString
        //m_rcardno=atoi((char *)kh);//各类型到CString
        m_rcardno=kh;
	    m_rgasvalue=ql;
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 } 
	 else
	 {
        MessageBox("读卡失败");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 }
   }
   else
   {
	   UpdateData(FALSE);
     //FreeLibrary(HmyDLL);
   }
}

void CDemonewDlg::OnButton5() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE HmyDLL;
	HmyDLL=LoadLibrary("WRwCard.dll");
	int n,ql,port;
	CString str;
   ////第四步：定义函数地址变量
	UpdateData(TRUE);
	ql=m_wgasvalue;
	str=m_rcardno;
	port=m_com;
   if(HmyDLL!=NULL)
   {
     INT (_stdcall *WriteGasCard)(__int16 com, __int32 baud, unsigned char *kmm, __int16 klx, unsigned char *kh, unsigned char *dqdm, __int32 ql, __int16 cs, __int32 ljgql, __int32 bjql, __int32 czsx, __int32 tzed,unsigned char *sqrq, __int32  oldprice, __int32  newprice ,unsigned char * sxrq,unsigned char * sxbj);
	 WriteGasCard=(INT (__stdcall *)(__int16, __int32, unsigned char *, __int16, unsigned char *, unsigned char *, __int32, __int16, __int32, __int32, __int32, __int32, unsigned char *, __int32, __int32,unsigned char *,unsigned char *))GetProcAddress(HmyDLL,"WriteGasCard");
	 //n=WriteNewCard(1,9600,(unsigned char *)"",0,0,(unsigned char *)m_cardno,(unsigned char *)"",(unsigned char *)"",(unsigned char *)"",m_gasvalue,0,0,0,0,0,0,0,(unsigned char *)"",0,0,(unsigned char *)"",(unsigned char *)"");
	 n=WriteGasCard(port,9600,(unsigned char *)"",0, (unsigned char *)str.GetBuffer(str.GetLength()), (unsigned char *)"",ql,0,0,0,0,0,(unsigned char *)"",0,0,(unsigned char *)"",(unsigned char *)"");
	 if (n==0)
	 {
	    MessageBox("写卡成功!");
		m_rcardno="";
		m_rgasvalue=0;
		m_wgasvalue=0;
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 } 
	 else
	 {
        MessageBox("写卡失败!");
		UpdateData(FALSE);
		//FreeLibrary(HmyDLL);
	 }
   }
   else
   {
	   UpdateData(FALSE);
     //FreeLibrary(HmyDLL);
   }
}
