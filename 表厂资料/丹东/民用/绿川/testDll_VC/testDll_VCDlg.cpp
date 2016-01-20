// testDll_VCDlg.cpp : implementation file
//

#include "stdafx.h"
#include "testDll_VC.h"
#include "testDll_VCDlg.h"

//#pragma comment(lib,"tdate.lib")

#include "DR_Soft.h"
#pragma  comment(lib,"DR_Soft.lib") 
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
// CTestDll_VCDlg dialog

CTestDll_VCDlg::CTestDll_VCDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CTestDll_VCDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CTestDll_VCDlg)
	
	
	m_strUserNo = _T("00000001");
		m_stryhh = _T("1111111111");
	m_fAccValue = 0.0;
	m_fGasvalue = 20;
	m_fResi = 0;
	m_nAlarm = 3;
	m_nOver = 0;
	m_nUpper = 600;
	m_nTestGasValue = 0;
	m_sMeterType = _T("");

	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CTestDll_VCDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CTestDll_VCDlg)
	DDX_Text(pDX, IDC_EDIT1, m_strUserNo);
	//DDV_MaxChars(pDX, m_strUserNo, 10);
DDX_Text(pDX, IDC_EDIT12, m_stryhh);
	DDX_Text(pDX, IDC_EDIT3, m_fGasvalue);
	//DDV_MinMaxDouble(pDX, m_fGasvalue, 0., 9999.);
	DDX_Text(pDX, IDC_EDIT4, m_fAccValue);
//	DDV_MinMaxDouble(pDX, m_fuseValue, 0., 9999.);
	DDX_Text(pDX, IDC_EDIT5, m_fResi);
//	DDV_MinMaxDouble(pDX, m_fResi, 0., 9999.);
	
	DDX_Text(pDX, IDC_EDIT7, m_nAlarm);
//	DDV_MinMaxInt(pDX, m_nAlarm, 0, 6000);

	DDX_Text(pDX, IDC_EDIT8, m_nOver);
//	DDV_MinMaxInt(pDX, m_nOver, 0, 6000);

	DDX_Text(pDX, IDC_EDIT9, m_nUpper);
//	DDV_MinMaxInt(pDX, m_nUpper, 0, 6000);

	DDX_Text(pDX, IDC_EDIT10, m_sMeterType);
	DDX_Text(pDX, IDC_EDIT11, m_nTestGasValue);
//	DDV_MinMaxInt(pDX, m_nTestGasValue, 0, 6000);

	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CTestDll_VCDlg, CDialog)
	//{{AFX_MSG_MAP(CTestDll_VCDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_BUTTON1, OnButton1)
	ON_BN_CLICKED(IDC_BUTTON2, OnButton2)
	ON_BN_CLICKED(IDC_BUTTON3, OnButton3)
	ON_BN_CLICKED(IDC_BUTTON4, OnButton4)
	ON_BN_CLICKED(IDC_BUTTON5, OnButton5)
	ON_BN_CLICKED(IDC_BUTTON6, OnButton6)
	ON_BN_CLICKED(IDC_BUTTON7, OnButton7)
	ON_BN_CLICKED(IDC_BUTTON8, OnButton8)
	ON_BN_CLICKED(IDC_BUTTON9, OnButton9)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CTestDll_VCDlg message handlers

BOOL CTestDll_VCDlg::OnInitDialog()
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

void CTestDll_VCDlg::OnSysCommand(UINT nID, LPARAM lParam)
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

void CTestDll_VCDlg::OnPaint() 
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
HCURSOR CTestDll_VCDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CTestDll_VCDlg::OnButton1() 
{
	char msg[200] = {0};
	unsigned char usercode[20]= {0} ;
	unsigned char bl[8] = {0};
	unsigned char  pwd[16]={0};  
		unsigned char  yhh[10]={0};  

		unsigned char  sqrq[10]={0};  
		unsigned char  sxrq[10]={0};  
		unsigned char  sxbj[10]={0};  
	int result;

	__int32 gql ,ljyql,syl, bjql, czsx, tzed,ljgql,oldprice,newprice;  
    __int16 klx,kzt,cs,bkcs;  
	UpdateData(TRUE);
//	memcpy(pwd,"17D1EB6FD1F4DB4E",16);
    result = ReadGasCard(1, 9600,pwd, &klx, &kzt,usercode,bl,yhh,bl, &gql, &cs, &ljgql,&bkcs, &ljyql,&syl, &bjql,&czsx, &tzed,sqrq,&oldprice,&newprice,sxrq,sxbj);

	if (result == 0)
	{
	
	  	m_strUserNo = usercode; 
		m_stryhh = yhh; 		 
		m_fGasvalue =gql;	
		m_fAccValue = ljyql;
		m_fResi = syl;
	}
	else
	{
		sprintf(msg, "读卡失败，错误代号：%d", result);
		MessageBox(msg);
	}    
	UpdateData(FALSE);
}

void CTestDll_VCDlg::OnButton2() 
{

    int result;
	char msg[200] = {0};
    unsigned char usercode[8];
    unsigned char pwd[16]={0}; 
    unsigned char GasType[8]={0}; 
    unsigned char bl[8];
	unsigned char dqdm[4];
	unsigned char yhh[10];
		unsigned char  sqrq[8]={0};  
		unsigned char  sxrq[8]={0};  
		unsigned char  sxbj[8]={0};  
	UpdateData(TRUE);
	//memcpy(pwd,"17D1EB6FD1F4DB4E",16);
	memcpy(GasType,"J2.5C",8);
	
	memcpy(usercode,m_strUserNo,8);
	memcpy(yhh,m_stryhh,10);
	memcpy(dqdm,"9A00",4);
    result =WriteNewCard(1, 9600, pwd, 0, 0, usercode, dqdm, yhh, bl, m_fGasvalue, 0, 0,0, 0, 0, 0, 0,sqrq,0,0,sxrq,sxbj);

	if (result == 0)
	{
		MessageBox("发卡成功");
	}
	else
	{
		sprintf(msg, "发卡失败，错误代号：%d", result);
		MessageBox(msg);
	}

}

void CTestDll_VCDlg::OnButton3() 
{

	int result;
	char msg[200] = {0};
	unsigned char pwd[16] = {0};
	unsigned char d[8] = {0};
    unsigned char usercode[8];    
    unsigned char bl[8]= {0};
	unsigned char yhh[10];
	unsigned char dqdm[4];
		unsigned char  sqrq[8]={0};  
		unsigned char  sxrq[8]={0};  
		unsigned char  sxbj[8]={0};  
	UpdateData(TRUE);
//	memcpy(pwd,"17D1EB6FD1F4DB4E",16);
	memcpy(usercode,m_strUserNo,8);
	memcpy(yhh,m_stryhh,10);
	memcpy(dqdm,"9A00",4);
  result =  WriteGasCard(1, 9600, pwd, 0, usercode, dqdm,yhh,m_fGasvalue, 0, 0, 0, 0,0,sqrq,0,0,sxrq,sxbj);

    
	if (result == 0)
	{
		MessageBox("充值成功");
	}
	else
	{
		sprintf(msg, "充值失败，错误代号：%d", result);
		MessageBox(msg);
	}
	
}

void CTestDll_VCDlg::OnButton4() 
{
      int result;
	char msg[200] = {0};
    unsigned char usercode[8];
    unsigned char pwd[16]={0}; 
    unsigned char GasType[8]={0}; 
    unsigned char bl[8];
	UpdateData(TRUE);
	//memcpy(pwd,"17D1EB6FD1F4DB4E",16);
	memcpy(GasType,"J2.5C",8);
	memcpy(usercode,m_strUserNo,8);	
  
   result =WriteGjkCard(1, 9600, pwd, 1,GasType,  m_nAlarm, m_nUpper, m_nOver,0);

	if (result == 0)
	{
		MessageBox("发卡成功");
	}
	else
	{
		sprintf(msg, "发卡失败，错误代号：%d", result);
		MessageBox(msg);
	}
		
}

void CTestDll_VCDlg::OnButton5() 
{
	int result;
	char msg[200] = {0};

    unsigned char c[8];
    unsigned char  pwd[16]={0}; 
    unsigned char GasType[8]={0}; 
    unsigned char bl[8]={0};
	UpdateData(TRUE);
	//memcpy(pwd,"17D1EB6FD1F4DB4E",16);
	memcpy(GasType,"J2.5C",8);	
    result =FormatGasCard (1, 9600, pwd,0,bl,bl);

	if (result == 0)
	{
		MessageBox("回收成功");
	}
	else
	{
		sprintf(msg, "回收失败，错误代号：%d", result);
		MessageBox(msg);
	}	
		
}

void CTestDll_VCDlg::OnButton6() 
{
    char msg[200] = {0};
	int result;
	__int32 Bjz, Kcsx, Tzl,Csql;
    unsigned char gastype[10]= {0};
  
	UpdateData(TRUE);
	
    result=ReadInitCard(1,9600,gastype, &Bjz,&Kcsx,&Tzl, &Csql);

	if (result == 0)
	{	
		m_nAlarm = Bjz;
    	m_nOver =Tzl ;
    	m_nUpper =Kcsx ;
        m_sMeterType= gastype;		
	}
	else
	{
		sprintf(msg, "读卡失败，错误代号：%d", result);
		MessageBox(msg);
	}	
	
	UpdateData(FALSE);	
}

void CTestDll_VCDlg::OnButton7() 
{
char msg[200] = {0};
    int result;
	int istrue;	  
	UpdateData(TRUE);	
    result=CheckGasCard(1,9600);

		sprintf(msg, "卡类型：%d", result);
		MessageBox(msg);

	
	UpdateData(FALSE);	
	
}

void CTestDll_VCDlg::OnButton8() 
{
	 int result;
	char msg[200] = {0};
    unsigned char usercode[8];
    unsigned char pwd[16]={0}; 
    unsigned char GasType[8]={0}; 
    unsigned char bl[8];
		unsigned char  sqrq[8]={0};  
		unsigned char  sxrq[8]={0};  
		unsigned char  sxbj[8]={0};  
	UpdateData(TRUE);
//	memcpy(pwd,"17D1EB6FD1F4DB4E",16);
   
    result =WriteGjkCard(1, 9600, pwd, 4,GasType,  0, 0, 0,0);
	if (result == 0)
	{
		MessageBox("发卡成功");
	}
	else
	{
		sprintf(msg, "发卡失败，错误代号：%d", result);
		MessageBox(msg);
	}
		
	
}

void CTestDll_VCDlg::OnButton9() 
{
int result;
	char msg[200] = {0};
	unsigned char pwd[16] = {0};
	unsigned char d[8] = {0};
    unsigned char usercode[8];    
    unsigned char bl[8]= {0};
	unsigned char yhh[10];
	unsigned char dqdm[4];
		unsigned char  sqrq[8]={0};  
		unsigned char  sxrq[8]={0};  
		unsigned char  sxbj[8]={0};  
	UpdateData(TRUE);
	//memcpy(pwd,"17D1EB6FD1F4DB4E",16);
	memcpy(usercode,m_strUserNo,8);
	memcpy(yhh,m_stryhh,10);
	memcpy(dqdm,"9A00",4);
    result =  WriteGasCard(1, 9600, pwd, 0, usercode, dqdm,yhh,m_fGasvalue, 0, 0, 0, 0,0,sqrq,0,0,sxrq,sxbj);

    
	if (result == 0)
	{
		MessageBox("充值成功");
	}
	else
	{
		sprintf(msg, "充值失败，错误代号：%d", result);
		MessageBox(msg);
	}
}
