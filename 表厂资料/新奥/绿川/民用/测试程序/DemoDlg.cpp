// DemoDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Demo.h"
#include "DemoDlg.h"
#include "xinao.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

 
CString sTmp;


/////////////////////////////////////////////////////////////////////////////
// CDemoDlg dialog

CDemoDlg::CDemoDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CDemoDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CDemoDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CDemoDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CDemoDlg)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CDemoDlg, CDialog)
	//{{AFX_MSG_MAP(CDemoDlg)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_BUTTON1, OnAbout)
	ON_BN_CLICKED(IDC_BUTTON2, OnButton2)
	ON_BN_CLICKED(IDC_BUTTON3, OnButton3)
	ON_BN_CLICKED(IDC_BUTTON4, OnButton4)
	ON_BN_CLICKED(IDC_BUTTON6, OnButton6)
	ON_BN_CLICKED(IDC_BUTTON5, OnButton5)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CDemoDlg message handlers

BOOL CDemoDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here
	((CComboBox*)GetDlgItem(IDC_Port))->SetCurSel(0);
	((CComboBox*)GetDlgItem(IDC_Baud))->SetCurSel(2);
	((CComboBox*)GetDlgItem(IDC_klx))->SetCurSel(0);
	((CComboBox*)GetDlgItem(IDC_kzt))->SetCurSel(0);
 
	SetDlgItemText(IDC_EDIT1,"12345678");
	SetDlgItemText(IDC_EDIT2,"1");
 
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CDemoDlg::OnPaint() 
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
HCURSOR CDemoDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CDemoDlg::OnAbout() 
{
 
 
	
}

void CDemoDlg::OnButton2() 
{
	__int16 com;
	__int32 baud;
	__int16 klx;
	__int16 kzt;
	int		st;
	unsigned char kmm[10];
	unsigned char kh[8],dqdm[4],yhh[10],tm[10],sqrq[8],sxrq[8],sxbj[1];
	__int32 ql,ljql,ljyql,syql,bjql,czsx,tzed;
	__int16 oldprice,newprice,cs,bkcs;
 

	GetDlgItemText(IDC_Port,sTmp);				//得到串口号
	com = atol(sTmp) - 1 ;

 	GetDlgItemText(IDC_Baud, sTmp);				//得到波特率
	baud = atol(sTmp);

	st = ReadGasCard(com,baud,kmm,&klx,&kzt,kh,dqdm,yhh,tm,&ql,&cs,&ljql,&bkcs,&ljyql,&syql,&bjql,&czsx,&tzed,sqrq,oldprice,newprice,sxrq,sxbj);

	sTmp.Format("用户卡号:%s\n气量:%d\n返回值:%d",kh,ql,st);
	AfxMessageBox(sTmp);

}

void CDemoDlg::OnButton3() 
{
	__int16 com;
	__int32 baud;
	__int16 klx;
	__int16 kzt;
	int		st;
	unsigned char kmm[10];
	unsigned char kh[8],dqdm[4],yhh[10],tm[10],sqrq[8],sxrq[8],sxbj[1];

		
	//klx	=  ((CComboBox*)GetDlgItem(IDC_klx))->GetCurSel() + 1
	klx	= 1;		//1表示4442卡 2 表示AT88SC102卡	
	kzt	= 0;	// ((CComboBox*)GetDlgItem(IDC_kzt))->GetCurSel();			//0表示开户卡 1表示用户卡

	GetDlgItemText(IDC_EDIT1,sTmp);									//得到卡号
	memcpy(kh,sTmp,8);

	st = WriteNewCard(1,9600,kmm,klx,kzt,kh,dqdm,yhh,tm,0,0,0,0,0,0,0,0,sqrq,0,0,sxrq,sxbj);

	sTmp.Format("WriteNewCard返回值:%d",st);
	AfxMessageBox(sTmp);
}

void CDemoDlg::OnButton4() 
{
	__int16 com;
	__int32 baud;
	__int16 klx;
	__int16 kzt;
	int		st;
	unsigned char kmm[10];
	unsigned char kh[9],dqdm[4],yhh[10],tm[10],sqrq[8],sxrq[8],sxbj[1];
	__int32 ql,ljql,ljyql,syql,bjql,czsx,tzed,ljgql;
	__int16 oldprice,newprice,cs,bkcs;

	GetDlgItemText(IDC_Port,sTmp);			//得到串口号
	com = atol(sTmp) - 1 ;

 	GetDlgItemText(IDC_Baud, sTmp);			//得到波特率
	baud = atol(sTmp);

	st = ReadGasCard(com,baud,kmm,&klx,&kzt,kh,dqdm,yhh,tm,&ql,&cs,&ljql,&bkcs,&ljyql,&syql,&bjql,&czsx,&tzed,sqrq,oldprice,newprice,sxrq,sxbj);

	GetDlgItemText(IDC_EDIT2, sTmp);		//得到写卡气量
	ql	= atol(sTmp);

	st = WriteGasCard(	com,baud,kmm,klx,kh,dqdm,yhh,ql,cs,ljgql,bjql,czsx,tzed,sqrq,oldprice,newprice,sxrq,sxbj);

	sTmp.Format("WriteGasCard返回值:%d",st);
	AfxMessageBox(sTmp);
	
 
}

void CDemoDlg::OnButton6() 
{
	__int16 com;
	__int32 baud;
	int		st;

	GetDlgItemText(IDC_Port,sTmp);				//得到串口号
	com	=	atol(sTmp);
	
	GetDlgItemText(IDC_Baud, sTmp);				//得到波特率
	baud = atol(sTmp);

	st = CheckGasCard(1,baud);

	if (st == 0) 
	{
		AfxMessageBox("本厂商驱动支持的开户卡或用户卡");
	}
	else if (st == 1)
	{
		AfxMessageBox("本厂商的工具卡");
	}
	else
	{
		sTmp.Format("FormatGasCard返回值%d ",st);
		AfxMessageBox(sTmp);	
	}
 	
}

void CDemoDlg::OnButton5() 
{
	__int16 com;
	__int32 baud;
	__int16 klx;
	__int16 kzt;
	int		st;
	unsigned char kmm[10];
	unsigned char kh[9],dqdm[4],yhh[10],tm[10],sqrq[8],sxrq[8],sxbj[1];
	__int32 ql,ljql,ljyql,syql,bjql,czsx,tzed;
	__int16 oldprice,newprice,cs,bkcs;

	GetDlgItemText(IDC_Port,sTmp);
	com	=	atol(sTmp);
	
	GetDlgItemText(IDC_Baud, sTmp);
	baud = atol(sTmp);

	st = ReadGasCard(com,baud,kmm,&klx,&kzt,kh,dqdm,yhh,tm,&ql,&cs,&ljql,&bkcs,&ljyql,&syql,&bjql,&czsx,&tzed,sqrq,oldprice,newprice,sxrq,sxbj);


 	st = FormatGasCard(com,baud,kmm,0,kh,dqdm);
	
	 
	sTmp.Format("FormatGasCard返回值%d ",st);
	AfxMessageBox(sTmp);
	 
}
 