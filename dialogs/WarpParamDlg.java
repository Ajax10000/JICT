package dialogs;

import frames.MainFrame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// This dialog is displayed when the user selects the 
// "Warp Image..." menu item from the Tools menu.
// See method onToolsWarpImage of the MainFrame class.
// To see what it should look like, see Figure D.7 on p 280 of the book.
public class WarpParamDlg extends JDialog implements ActionListener {
    private MainFrame mMainFrame;

    // EDITTEXT        IDC_EDITx,59,20,35,14,ES_AUTOHSCROLL
    // DDX_Text(pDX, IDC_EDITx, m_rx);
	public String	m_rx;

    // EDITTEXT        IDC_EDITy,59,37,35,14,ES_AUTOHSCROLL
    // DDX_Text(pDX, IDC_EDITy, m_ry);
	public String	m_ry;

    // EDITTEXT        IDC_EDITz,59,53,35,14,ES_AUTOHSCROLL
    // DDX_Text(pDX, IDC_EDITz, m_rz);
	public String	m_rz;

    // EDITTEXT        IDC_EDITxScale,60,95,35,14,ES_AUTOHSCROLL
    // DDX_Text(pDX, IDC_EDITxScale, m_sx);
	public String	m_sx;

    // EDITTEXT        IDC_EDITyScale,60,111,35,14,ES_AUTOHSCROLL
    // DDX_Text(pDX, IDC_EDITyScale, m_sy);
	public String	m_sy;

    private JTextField fldRotAboutXAxis;
    private JTextField fldRotAboutYAxis;
    private JTextField fldRotAboutZAxis;

    private JTextField fldSclAlongXAxis;
    private JTextField fldSclAlongYAxis;

    private JButton btnOK;
    private JButton btnCancel;

    private JPanel pnlRotAngleGroup;
    private JPanel pnlSclFactorGroup;

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
        mMainFrame = (MainFrame)pParent;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        m_rx = "0.0";
        m_sx = "1.0";
        m_ry = "0.0";
        m_sy = "1.0";
        m_rz = "0.0";

        addRotationFields();
        addScaleFields();
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");

        add(pnlRotAngleGroup);
        add(pnlSclFactorGroup);
        add(btnOK);
        add(btnCancel);
    } // WarpParamDlg ctor


    // Called from:
    //     constructor
    private void addRotationFields() {
        Dimension fldSize = new Dimension(50, 30);
        JLabel lblRotX = new JLabel("X-Axis: ");
        JLabel lblRotY = new JLabel("Y-Axis: ");
        JLabel lblRotZ = new JLabel("Z-Axis: ");

        fldRotAboutXAxis = new JTextField();
        fldRotAboutXAxis.setSize(fldSize);
        fldRotAboutXAxis.setPreferredSize(fldSize);

        fldRotAboutYAxis = new JTextField();
        fldRotAboutYAxis.setSize(fldSize);
        fldRotAboutYAxis.setPreferredSize(fldSize);

        fldRotAboutZAxis = new JTextField();
        fldRotAboutZAxis.setSize(fldSize);
        fldRotAboutZAxis.setPreferredSize(fldSize);

        pnlRotAngleGroup = new JPanel();
        pnlRotAngleGroup.setBorder(BorderFactory.createTitledBorder("Rotation Angles in Degrees"));

        pnlRotAngleGroup.add(lblRotX);
        pnlRotAngleGroup.add(fldRotAboutXAxis);

        pnlRotAngleGroup.add(lblRotY);
        pnlRotAngleGroup.add(fldRotAboutYAxis);

        pnlRotAngleGroup.add(lblRotZ);
        pnlRotAngleGroup.add(fldRotAboutZAxis);
    }


    // Called from:
    //     constructor
    private void addScaleFields() {
        JLabel lblSclX = new JLabel("X-Axis: ");
        JLabel lblSclY = new JLabel("Y-Axis: ");

        fldSclAlongXAxis = new JTextField();
        fldSclAlongYAxis = new JTextField();

        pnlSclFactorGroup = new JPanel();
        pnlSclFactorGroup.setBorder(BorderFactory.createTitledBorder("Scale Factors"));

        pnlSclFactorGroup.add(lblSclX);
        pnlSclFactorGroup.add(fldSclAlongXAxis);

        pnlSclFactorGroup.add(lblSclY);
        pnlSclFactorGroup.add(fldSclAlongYAxis);
    }

    public void actionPerformed(ActionEvent ae) {

    }

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
        // Save the values the user entered in the WarpParamDlg dialog
        mMainFrame.mWarpRotateX = Float.valueOf(m_rx);
        mMainFrame.mWarpRotateY = Float.valueOf(m_ry);
        mMainFrame.mWarpRotateZ = Float.valueOf(m_rz);
        mMainFrame.mWarpScaleX  = Float.parseFloat(m_sx);
        mMainFrame.mWarpScaleY  = Float.parseFloat(m_sy);

        mMainFrame.onWarpParamDlgClosed();
    } // onOk
} // class WarpParamDlg