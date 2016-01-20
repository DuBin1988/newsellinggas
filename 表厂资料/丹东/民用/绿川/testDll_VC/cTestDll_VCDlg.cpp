// cTestDll_VCDlg.cpp : implementation file
//

#include "stdafx.h"
#include "testDll_VC.h"
#include "cTestDll_VCDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CcTestDll_VCDlg dialog


CTestDll_VCDlg::CTestDll_VCDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CTestDll_VCDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CcTestDll_VCDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CcTestDll_VCDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CcTestDll_VCDlg)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CcTestDll_VCDlg, CDialog)
	//{{AFX_MSG_MAP(CcTestDll_VCDlg)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CcTestDll_VCDlg message handlers
