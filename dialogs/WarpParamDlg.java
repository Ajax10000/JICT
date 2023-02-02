package dialogs;

import frames.MainFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// This dialog is displayed when the user selects the 
// "Warp Image..." menu item from the Tools menu.
// See method onToolsWarpImage of the MainFrame class.
// To see what it should look like, see Figure D.7 on p 280 of the book.
public class WarpParamDlg extends JDialog {
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
        // mMainFrame = (MainFrame)pParent;
        setTitle("Enter Warp Parameters");
        setSize(400, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        m_rx = "0.0";
        m_sx = "1.0";
        m_ry = "0.0";
        m_sy = "1.0";
        m_rz = "0.0";

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box vertBtnBox = Box.createVerticalBox();

        addRotationFields();
        addScaleFields();
        Component rectSpacer = Box.createRigidArea(new Dimension(30, 90));
        row01Box.add(pnlRotAngleGroup);
        row01Box.add(rectSpacer);

        Component smallRectSpacer = Box.createRigidArea(new Dimension(30, 25));
        Dimension btnDim = new Dimension(80, 25);
        btnOK = new JButton("  OK  ");
        btnOK.setSize(btnDim);
        btnOK.setPreferredSize(btnDim);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        Component smallVertSpacer = Box.createRigidArea(new Dimension(80, 5));
        btnCancel = new JButton("Cancel");
        btnCancel.setSize(btnDim);
        btnCancel.setPreferredSize(btnDim);

        vertBtnBox.add(smallRectSpacer);
        vertBtnBox.add(btnOK);
        vertBtnBox.add(smallVertSpacer);
        vertBtnBox.add(btnCancel);

        row02Box.add(pnlSclFactorGroup);
        row02Box.add(vertBtnBox);

        panel.add(row01Box);
        panel.add(row02Box);
        add(panel);

        setVisible(true);
    } // WarpParamDlg ctor


    // Called from:
    //     constructor
    private void addRotationFields() {
        Dimension lblSize = new Dimension(80, 25);
        Dimension lblFldSpacerSize = new Dimension(10, 25);
        Dimension fldSize = new Dimension(50, 30);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();

        // Create the components for row 01
        JLabel lblRotX = new JLabel("X-Axis: ");
        lblRotX.setSize(lblSize);
        lblRotX.setPreferredSize(lblSize);

        Component lblFldSpacer01 = Box.createRigidArea(lblFldSpacerSize);

        fldRotAboutXAxis = new JTextField();
        fldRotAboutXAxis.setSize(fldSize);
        fldRotAboutXAxis.setPreferredSize(fldSize);

        // Populate row01Box
        row01Box.add(lblRotX);
        row01Box.add(lblFldSpacer01);
        row01Box.add(fldRotAboutXAxis);

        // Create the components for row 02
        JLabel lblRotY = new JLabel("Y-Axis: ");
        lblRotY.setSize(lblSize);
        lblRotY.setPreferredSize(lblSize);

        Component lblFldSpacer02 = Box.createRigidArea(lblFldSpacerSize);

        fldRotAboutYAxis = new JTextField();
        fldRotAboutYAxis.setSize(fldSize);
        fldRotAboutYAxis.setPreferredSize(fldSize);

        // Populate row02Box
        row02Box.add(lblRotY);
        row02Box.add(lblFldSpacer02);
        row02Box.add(fldRotAboutYAxis);

        // Create the components for row 03
        JLabel lblRotZ = new JLabel("Z-Axis: ");
        lblRotZ.setSize(lblSize);
        lblRotZ.setPreferredSize(lblSize);

        Component lblFldSpacer03 = Box.createRigidArea(lblFldSpacerSize);

        fldRotAboutZAxis = new JTextField();
        fldRotAboutZAxis.setSize(fldSize);
        fldRotAboutZAxis.setPreferredSize(fldSize);

        // Populate row03Box
        row03Box.add(lblRotZ);
        row03Box.add(lblFldSpacer03);
        row03Box.add(fldRotAboutZAxis);

        pnlRotAngleGroup = new JPanel();
        BoxLayout boxLayout = new BoxLayout(pnlRotAngleGroup, BoxLayout.Y_AXIS);
        pnlRotAngleGroup.setLayout(boxLayout);
        pnlRotAngleGroup.setBorder(BorderFactory.createTitledBorder("Rotation Angles in Degrees"));

        pnlRotAngleGroup.add(row01Box);
        pnlRotAngleGroup.add(row02Box);
        pnlRotAngleGroup.add(row03Box);
    } // addRotationFields


    // Called from:
    //     constructor
    private void addScaleFields() {
        Dimension lblSize = new Dimension(80, 25);
        Dimension lblFldSpacerSize = new Dimension(10, 25);
        Dimension fldSize = new Dimension(50, 30);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();

        // Create components for row01Box
        JLabel lblSclX = new JLabel("X-Axis: ");
        lblSclX.setSize(lblSize);
        lblSclX.setPreferredSize(lblSize);

        Component lblFldSpacer01 = Box.createRigidArea(lblFldSpacerSize);

        fldSclAlongXAxis = new JTextField();
        fldSclAlongXAxis.setSize(fldSize);
        fldSclAlongXAxis.setPreferredSize(fldSize);

        // Populate row01Box
        row01Box.add(lblSclX);
        row01Box.add(lblFldSpacer01);
        row01Box.add(fldSclAlongXAxis);

        // Create components for row02Box
        JLabel lblSclY = new JLabel("Y-Axis: ");
        lblSclY.setSize(lblSize);
        lblSclY.setPreferredSize(lblSize);

        Component lblFldSpacer02 = Box.createRigidArea(lblFldSpacerSize);

        fldSclAlongYAxis = new JTextField();
        fldSclAlongYAxis.setSize(fldSize);
        fldSclAlongYAxis.setPreferredSize(fldSize);

        // Populate row02Box
        row02Box.add(lblSclY);
        row02Box.add(lblFldSpacer02);
        row02Box.add(fldSclAlongYAxis);

        pnlSclFactorGroup = new JPanel();
        BoxLayout boxLayout = new BoxLayout(pnlSclFactorGroup, BoxLayout.Y_AXIS);
        pnlSclFactorGroup.setLayout(boxLayout);
        pnlSclFactorGroup.setBorder(BorderFactory.createTitledBorder("Scale Factors"));

        pnlSclFactorGroup.add(row01Box);
        pnlSclFactorGroup.add(row02Box);
    } // addScaleFields


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
    // Called when the user clicks on the OK button
    public void onOK() {
        // Save the values the user entered in the WarpParamDlg dialog
        m_rx = fldRotAboutXAxis.getText();
        m_ry = fldRotAboutYAxis.getText();
        m_rz = fldRotAboutZAxis.getText();
        m_sx = fldSclAlongXAxis.getText();
        m_sy = fldSclAlongYAxis.getText();

        // Get the floating point values the user entered.
        // If they are valid floating point values, store them in the appropriate 
        // MainFrame class field.
        Float rx = getFloatValue(m_rx);
        if (rx == null) {
            JOptionPane.showMessageDialog(this, "Rotation value entered in x-axis is not a valid number.");
            fldRotAboutXAxis.requestFocus();
            return;
        } else {
            mMainFrame.mWarpRotateX = rx;
        }

        Float ry = getFloatValue(m_ry);
        if (ry == null) {
            JOptionPane.showMessageDialog(this, "Rotation value entered in y-axis is not a valid number.");
            fldRotAboutYAxis.requestFocus();
            return;
        } else {
            mMainFrame.mWarpRotateY = ry;
        }

        Float rz = getFloatValue(m_rz);
        if (rz == null) {
            JOptionPane.showMessageDialog(this, "Rotation value entered in z-axis is not a valid number.");
            fldRotAboutZAxis.requestFocus();
            return;
        } else {
            mMainFrame.mWarpRotateZ = rz;
        }

        Float sx = getFloatValue(m_sx);
        if (sx == null) {
            JOptionPane.showMessageDialog(this, "Scale value entered in x-axis is not a valid number.");
            fldSclAlongXAxis.requestFocus();
            return;
        } else {
            mMainFrame.mWarpScaleX = sx;
        }

        Float sy = getFloatValue(m_sy);
        if (sy == null) {
            JOptionPane.showMessageDialog(this, "Scale value entered in y-axis is not a valid number.");
            fldSclAlongYAxis.requestFocus();
            return;
        } else {
            mMainFrame.mWarpScaleY = sy;
        }

        mMainFrame.onWarpParamDlgClosed();
        dispose();
    } // onOk


    private Float getFloatValue(String psFloatAsString) {
        Float fValue = null;

        try {
            fValue = Float.valueOf(psFloatAsString);
        } catch (NumberFormatException nfe) {
            fValue = null;
        }

        return fValue;
    }

    // Called when the user clicks on the Cancel button
    public void onCancel() {
        dispose();
    } // onCancel
} // class WarpParamDlg