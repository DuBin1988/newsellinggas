// demonewDlg.h : header file
//

#if !defined(AFX_DEMONEWDLG_H__B79DBC62_20FA_4E51_9DBC_8BA7C29958A8__INCLUDED_)
#define AFX_DEMONEWDLG_H__B79DBC62_20FA_4E51_9DBC_8BA7C29958A8__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CDemonewDlg dialog

class CDemonewDlg : public CDialog
{
// Construction
public:
	CDemonewDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CDemonewDlg)
	enum { IDD = IDD_DEMONEW_DIALOG };
	int		m_gasvalue;
	int		m_rgasvalue;
	int		m_wgasvalue;
	CString	m_rcardno;
	CString	m_cardno;
	int		m_com;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDemonewDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CDemonewDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnButton1();
	afx_msg void OnButton2();
	afx_msg void OnButton3();
	afx_msg void OnButton4();
	afx_msg void OnButton5();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_DEMONEWDLG_H__B79DBC62_20FA_4E51_9DBC_8BA7C29958A8__INCLUDED_)
