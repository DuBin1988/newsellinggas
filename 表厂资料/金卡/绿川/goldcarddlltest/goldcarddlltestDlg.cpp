// goldcarddlltestDlg.cpp : implementation file
//

#include "stdafx.h"
#include "goldcarddlltest.h"
#include "goldcarddlltestDlg.h"
#include "goldcard.h"

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
// CGoldcarddlltestDlg dialog

CGoldcarddlltestDlg::CGoldcarddlltestDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CGoldcarddlltestDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CGoldcarddlltestDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CGoldcarddlltestDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CGoldcarddlltestDlg)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CGoldcarddlltestDlg, CDialog)
	//{{AFX_MSG_MAP(CGoldcarddlltestDlg)
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
// CGoldcarddlltestDlg message handlers

BOOL CGoldcarddlltestDlg::OnInitDialog()
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

void CGoldcarddlltestDlg::OnSysCommand(UINT nID, LPARAM lParam)
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

void CGoldcarddlltestDlg::OnPaint() 
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
HCURSOR CGoldcarddlltestDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CGoldcarddlltestDlg::OnButton1() 
{
	// TODO: Add your control notification handler code here
	int st;
	unsigned char kh[]={'0','0','0','0','0','0','0','1'},dqdm[]={'0','5','7','7'},kmm[10],yhh[]={'0','0','0','0','0','0','0','0','0','1'},tm[10];
	CString aa;
	unsigned char sqrq[10],sxrq[10],sxbj[10];

	st=WriteNewCard(0,9600,kmm,1,0,kh,dqdm,yhh,tm,100,1,0,0,0,0,0,0,sqrq,0,0,sxrq,sxbj);
	if(0!=st)
	{
		aa.Format("写卡错误，错误代码%d",st);
		AfxMessageBox(aa);
		return;
	}
	AfxMessageBox("写卡成功");
}

void CGoldcarddlltestDlg::OnButton2() 
{
	// TODO: Add your control notification handler code here
	unsigned char kh[11],dqdm[5],kmm[10],yhh[10],tm[10];
	int st,ljyql,syql,bjql,czsx,tzed,ql,ljgql;
	__int16 klx,kzt,cs,bkcs;

	unsigned char sqrq[10]; 
	__int32 oldprice;
	__int32 newprice;
	unsigned char sxrq[10];
	unsigned char sxbj[10];
	CString aa;
	st=ReadGasCard(0,9600,kmm,&klx,&kzt,kh,dqdm,yhh,tm,&ql,&cs,&ljgql,&bkcs,&ljyql,&syql,&bjql,&czsx,&tzed,sqrq,&oldprice,&newprice,sxrq,sxbj);
	if(0!=st)
	{
		aa.Format("读卡错误，错误代码%d",st);
		AfxMessageBox(aa);
		return;
	}
	aa.Format("卡类型：%d，卡状态：%d，卡号：%s，地区代码：%s，气量：%d，购气次数：%d，累计用气量：%d",klx,kzt,kh,dqdm,ql,cs,ljyql);
	AfxMessageBox(aa);
}

void CGoldcarddlltestDlg::OnButton3() 
{
	// TODO: Add your control notification handler code here
	int st;
	unsigned char kh[]={'0','0','0','0','0','0','0','1'},dqdm[]={'0','5','7','7'},kmm[10];
	unsigned char sqrq[10],sxrq[10],sxbj[10];
	CString aa;
	st=WriteGasCard(0,9600,kmm,1,kh,dqdm,1234,2,0,0,0,0,sqrq,0,0,sxrq,sxbj);
	if(0!=st)
	{
		aa.Format("写卡错误，错误代码%d",st);
		AfxMessageBox(aa);
		return;
	}
	AfxMessageBox("写卡成功");
	
}

void CGoldcarddlltestDlg::OnButton4() 
{
	// TODO: Add your control notification handler code here
	int st;
	CString aa;
	st=CheckGasCard(0,9600);
	if(st<0)
	{
		aa.Format("错误，错误代码%d",st);
		AfxMessageBox(aa);
		return;
	}

	if(0==st)
		AfxMessageBox("用户卡");
	else
		AfxMessageBox("工具卡");
	
}

void CGoldcarddlltestDlg::OnButton5() 
{
	// TODO: Add your control notification handler code here
	int st;
	unsigned char kh[]={'0','0','0','0','0','0','0','1'},dqdm[]={'0','5','7','7'},kmm[10];
	CString aa;

	st=FormatGasCard(0,9600,kmm,1,kh,dqdm);
	if(0!=st)
	{
		aa.Format("清卡错误，错误代码%d",st);
		AfxMessageBox(aa);
		return;
	}
	AfxMessageBox("清卡成功");
	
}
