// goldcarddlltestDlg.h : header file
//

#if !defined(AFX_GOLDCARDDLLTESTDLG_H__924B6829_A8B6_4882_977D_9AD8AFD84441__INCLUDED_)
#define AFX_GOLDCARDDLLTESTDLG_H__924B6829_A8B6_4882_977D_9AD8AFD84441__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CGoldcarddlltestDlg dialog

class CGoldcarddlltestDlg : public CDialog
{
// Construction
public:
	CGoldcarddlltestDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CGoldcarddlltestDlg)
	enum { IDD = IDD_GOLDCARDDLLTEST_DIALOG };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGoldcarddlltestDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CGoldcarddlltestDlg)
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

#endif // !defined(AFX_GOLDCARDDLLTESTDLG_H__924B6829_A8B6_4882_977D_9AD8AFD84441__INCLUDED_)
