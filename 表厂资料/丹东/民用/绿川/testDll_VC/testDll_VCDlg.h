// testDll_VCDlg.h : header file
//

#if !defined(AFX_TESTDLL_VCDLG_H__F1CC067F_0631_4E04_AB47_B466FFF75FE3__INCLUDED_)
#define AFX_TESTDLL_VCDLG_H__F1CC067F_0631_4E04_AB47_B466FFF75FE3__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CTestDll_VCDlg dialog

class CTestDll_VCDlg : public CDialog
{
// Construction
public:
	CTestDll_VCDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CTestDll_VCDlg)
	enum { IDD = IDD_TESTDLL_VC_DIALOG };
	CString	m_strUserNo;
	CString	m_stryhh;
	CString	m_strDate;
	double	m_fGasvalue;
	double	m_fAccValue;
	double	m_fResi;
	int		m_nAlarm;
	int     m_nUpper;
	int     m_nOver;
	int     m_nTestGasValue;
	CString	m_sMeterType;
	

	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CTestDll_VCDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CTestDll_VCDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnButton1();
	afx_msg void OnButton2();
	afx_msg void OnButton3();
	afx_msg void OnButton4();
	afx_msg void OnButton5();
	afx_msg void OnButton6();
	afx_msg void OnButton7();
	afx_msg void OnButton8();
	afx_msg void OnButton9();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TESTDLL_VCDLG_H__F1CC067F_0631_4E04_AB47_B466FFF75FE3__INCLUDED_)
