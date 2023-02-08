package dialogs;

import frames.MainFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
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
import javax.swing.SwingConstants;

// This dialog is displayed when the user selects the 
// "Warp Image..." menu item from the Tools menu.
// See method onToolsWarpImage of the MainFrame class.
// To see what it should look like, see Figure D.7 on p 280 of the book.
//
// Per p 279 of the book Visual Special Effects Toolkit in C++:
// The Warp Image... menu item is a demonstration of one of the planar texture-
// mapping algorithms discussed in this text. When this option is selected, the
// dialog box shown in Figure D.7 appears. Enter the desired rotation angles 
// and/or scale factors and press the OK button. A sample image is texture-mapped
// onto the planar surface described by the rotation angles you supplied.
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

    private static final int iDlgWidth  = 380;
    private static final int iDlgHeight = 300;

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
        setSize(iDlgWidth, iDlgHeight);
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

        Box vertBox = Box.createVerticalBox();
        Box topBox = addTopSection();
        Box botBox = addBotSection();

        vertBox.add(topBox);
        vertBox.add(botBox);
        panel.add(vertBox);
        add(panel);

        setVisible(true);
    } // WarpParamDlg ctor


    // Called from:
    //     constructor
    private Box addTopSection() {
        final int iPnlWidth = 180;
        final int iPnlHeight = 150;

        Dimension lblSize = new Dimension(80, 25);
        Dimension lblFldSpacerSize = new Dimension(10, 25);
        Dimension fldSize = new Dimension(30, 30);
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();
        Box row04Box = Box.createHorizontalBox();
        Box row05Box = Box.createHorizontalBox();

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

        // Create component for row02Box
        // This is a spacer between the X-Axis text field and Y-Axis text field
        Component rowSpacer01 = Box.createRigidArea(new Dimension(iDlgWidth - 5, 8));

        // Populate row02Box
        row02Box.add(rowSpacer01);

        // Create the components for row03Box
        JLabel lblRotY = new JLabel("Y-Axis: ");
        lblRotY.setSize(lblSize);
        lblRotY.setPreferredSize(lblSize);

        Component lblFldSpacer02 = Box.createRigidArea(lblFldSpacerSize);

        fldRotAboutYAxis = new JTextField();
        fldRotAboutYAxis.setSize(fldSize);
        fldRotAboutYAxis.setPreferredSize(fldSize);

        // Populate row03Box
        row03Box.add(lblRotY);
        row03Box.add(lblFldSpacer02);
        row03Box.add(fldRotAboutYAxis);

        // Create the component for row04Box
        // This is a spacer between the Y-Axis text field and the Z-Axis text field
        Component rowSpacer02 = Box.createRigidArea(new Dimension(iPnlWidth - 5, 8));

        // Populate row04Box
        row04Box.add(rowSpacer02);

        // Create the components for row05Box
        JLabel lblRotZ = new JLabel("Z-Axis: ");
        lblRotZ.setSize(lblSize);
        lblRotZ.setPreferredSize(lblSize);

        Component lblFldSpacer03 = Box.createRigidArea(lblFldSpacerSize);

        fldRotAboutZAxis = new JTextField();
        fldRotAboutZAxis.setSize(fldSize);
        fldRotAboutZAxis.setPreferredSize(fldSize);

        // Populate row05Box
        row05Box.add(lblRotZ);
        row05Box.add(lblFldSpacer03);
        row05Box.add(fldRotAboutZAxis);

        Dimension pnlSize = new Dimension(iPnlWidth, iPnlHeight);
        JPanel pnlRotAngleGroup = new JPanel();
        pnlRotAngleGroup.setSize(pnlSize);
        pnlRotAngleGroup.setMaximumSize(pnlSize);
        pnlRotAngleGroup.setPreferredSize(pnlSize);
        BoxLayout boxLayout = new BoxLayout(pnlRotAngleGroup, BoxLayout.Y_AXIS);
        pnlRotAngleGroup.setLayout(boxLayout);
        pnlRotAngleGroup.setBorder(BorderFactory.createTitledBorder(loweredetched, "Rotation Angles in Degrees"));

        pnlRotAngleGroup.add(row01Box);
        pnlRotAngleGroup.add(row02Box);
        pnlRotAngleGroup.add(row03Box);
        pnlRotAngleGroup.add(row04Box);
        pnlRotAngleGroup.add(row05Box);
        pnlRotAngleGroup.setAlignmentX(SwingConstants.LEFT);

        Box vertBox = Box.createVerticalBox();
        // Component spacer = Box.createRigidArea(new Dimension(70, 150));
        Component spacer = Box.createHorizontalGlue();
        vertBox.add(spacer);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(pnlRotAngleGroup);
        horizontalBox.add(vertBox);

        return horizontalBox;
    } // addTopSection


    // Called from:
    //     constructor
    private Box addBotSection() {
        final int iPnlWidth = 180;
        final int iPnlHeight = 100;

        Dimension lblSize = new Dimension(80, 25);
        Dimension lblFldSpacerSize = new Dimension(10, 25);
        Dimension fldSize = new Dimension(30, 30);
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        Box row01Box = Box.createHorizontalBox();
        Box row02Box = Box.createHorizontalBox();
        Box row03Box = Box.createHorizontalBox();

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

        // Create component for row02Box
        // This is a separator between the X-Axis text field and the Y-Axis text field
        Component rowSpacer = Box.createRigidArea(new Dimension(iPnlWidth - 5, 8));

        // Populate row02Box
        row02Box.add(rowSpacer);

        // Create components for row03Box
        JLabel lblSclY = new JLabel("Y-Axis: ");
        lblSclY.setSize(lblSize);
        lblSclY.setPreferredSize(lblSize);

        Component lblFldSpacer02 = Box.createRigidArea(lblFldSpacerSize);

        fldSclAlongYAxis = new JTextField();
        fldSclAlongYAxis.setSize(fldSize);
        fldSclAlongYAxis.setPreferredSize(fldSize);

        // Populate row03Box
        row03Box.add(lblSclY);
        row03Box.add(lblFldSpacer02);
        row03Box.add(fldSclAlongYAxis);

        Dimension pnlSize = new Dimension(iPnlWidth, iPnlHeight);
        JPanel pnlSclFactorGroup = new JPanel();
        pnlSclFactorGroup.setSize(pnlSize);
        pnlSclFactorGroup.setMaximumSize(pnlSize);
        pnlSclFactorGroup.setPreferredSize(pnlSize);

        BoxLayout boxLayout = new BoxLayout(pnlSclFactorGroup, BoxLayout.Y_AXIS);
        pnlSclFactorGroup.setLayout(boxLayout);
        pnlSclFactorGroup.setBorder(BorderFactory.createTitledBorder(loweredetched, "Scale Factors"));

        pnlSclFactorGroup.add(row01Box);
        pnlSclFactorGroup.add(row02Box);
        pnlSclFactorGroup.add(row03Box);

        // Component rectSpacer = Box.createRigidArea(new Dimension(15, 90));
        Component rectSpacer = Box.createHorizontalGlue();
        Box row00Box = Box.createHorizontalBox();
        row00Box.add(pnlSclFactorGroup);
        row00Box.add(rectSpacer);

        Box vertBtnBox = Box.createVerticalBox();
        Component smallRectSpacer = Box.createRigidArea(new Dimension(30, 25));
        Dimension btnDim = new Dimension(80, 25);
        btnOK = new JButton("OK");
        btnOK.setSize(btnDim);
        btnOK.setMinimumSize(btnDim);
        btnOK.setMaximumSize(btnDim);
        btnOK.setPreferredSize(btnDim);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        Component smallVertSpacer = Box.createRigidArea(new Dimension(80, 5));
        btnCancel = new JButton("Cancel");
        btnCancel.setSize(btnDim);
        btnCancel.setMinimumSize(btnDim);
        btnCancel.setMaximumSize(btnDim);
        btnCancel.setPreferredSize(btnDim);

        vertBtnBox.add(smallRectSpacer);
        vertBtnBox.add(btnOK);
        vertBtnBox.add(smallVertSpacer);
        vertBtnBox.add(btnCancel);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(row00Box);
        horizontalBox.add(vertBtnBox);

        return horizontalBox;
    } // addBotSection


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
        Float parsedFloatValue = 0.0f;
        if (getFloatValue(m_rx, parsedFloatValue) == null) {
            mMainFrame.mWarpRotateX = parsedFloatValue.floatValue();
        } else {
            JOptionPane.showMessageDialog(this, "Rotation value entered in x-axis is not a valid number.");
            fldRotAboutXAxis.requestFocusInWindow();
            return;
        }

        if (getFloatValue(m_ry, parsedFloatValue) == null) {
            mMainFrame.mWarpRotateY = parsedFloatValue.floatValue();
        } else {
            JOptionPane.showMessageDialog(this, "Rotation value entered in y-axis is not a valid number.");
            fldRotAboutYAxis.requestFocusInWindow();
            return;
        }

        if (getFloatValue(m_rz, parsedFloatValue) == null) {
            mMainFrame.mWarpRotateZ = parsedFloatValue.floatValue();
        } else {
            JOptionPane.showMessageDialog(this, "Rotation value entered in z-axis is not a valid number.");
            fldRotAboutZAxis.requestFocusInWindow();
            return;
        }

        if (getFloatValue(m_sx, parsedFloatValue) == null) {
            mMainFrame.mWarpScaleX = parsedFloatValue.floatValue();
        } else {
            JOptionPane.showMessageDialog(this, "Scale value entered in x-axis is not a valid number.");
            fldSclAlongXAxis.requestFocusInWindow();
            return;
        }

        if (getFloatValue(m_sy, parsedFloatValue) == null) {
            mMainFrame.mWarpScaleY = parsedFloatValue.floatValue();
        } else {
            JOptionPane.showMessageDialog(this, "Scale value entered in y-axis is not a valid number.");
            fldSclAlongYAxis.requestFocusInWindow();
            return;
        }

        mMainFrame.onWarpParamDlgClosed();
        dispose();
    } // onOk


    private NumberFormatException getFloatValue(String psFloatAsString, Float pFValue) {
        try {
            pFValue = Float.valueOf(psFloatAsString);
        } catch (NumberFormatException nfe) {
            return nfe;
        }
        return null;
    }


    // Called when the user clicks on the Cancel button
    public void onCancel() {
        dispose();
    } // onCancel
} // class WarpParamDlg