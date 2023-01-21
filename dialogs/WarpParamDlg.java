package dialogs;

import javax.swing.JDialog;
import javax.swing.JFrame;

// This dialog is displayed when the user selects the 
// "Warp Image..." menu item from the Tools menu.
// See method onToolsWarpImage of the MainFrame class.
// To see what it should look like, see Figure D.7 on p 280 of the book.
public class WarpParamDlg extends JDialog {
    // DDX_Text(pDX, IDC_EDITx, m_rx);
	public String	m_rx;

    // DDX_Text(pDX, IDC_EDITxScale, m_sx);
	public String	m_sx;

    // DDX_Text(pDX, IDC_EDITy, m_ry);
	public String	m_ry;

    // DDX_Text(pDX, IDC_EDITyScale, m_sy);
	public String	m_sy;

    // DDX_Text(pDX, IDC_EDITz, m_rz);
	public String	m_rz;

/*
class CWarpParamDlg : public CDialog
{
// Construction
public:
	CWarpParamDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CWarpParamDlg)
	enum { IDD = IDD_WarpParams };
	CString	m_rx;
	CString	m_sx;
	CString	m_ry;
	CString	m_sy;
	CString	m_rz;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CWarpParamDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CWarpParamDlg)
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
 */

    // This constructor came from WARPPARMDLG.CPP
    // I added the pModal parameter
    // Called from:
    //     MainFrame.onToolsWarpImage
    public WarpParamDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);

        //{{AFX_DATA_INIT(CWarpParamDlg)
        m_rx = "0.0";
        m_sx = "1.0";
        m_ry = "0.0";
        m_sy = "1.0";
        m_rz = "0.0";
        //}}AFX_DATA_INIT
    } // WarpParamDlg ctor


    /*
    // This method came from WARPPARMDLG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);

        //{{AFX_DATA_MAP(CWarpParamDlg)
        DDX_Text(pDX, IDC_EDITx, m_rx);
        DDX_Text(pDX, IDC_EDITxScale, m_sx);
        DDX_Text(pDX, IDC_EDITy, m_ry);
        DDX_Text(pDX, IDC_EDITyScale, m_sy);
        DDX_Text(pDX, IDC_EDITz, m_rz);
        //}}AFX_DATA_MAP
    }
    */


    // This method came from WARPPARMDLG.CPP
    void onOK() {
        // TODO: Add extra validation here
    } // onOk
} // class WarpParamDlg