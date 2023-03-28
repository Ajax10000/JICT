package dialogs;

import frames.MainFrame;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import math.MathUtils;

import structs.Point3d;

// This dialog is displayed when the user selects the 
// "Still" or "Sequence" menu item from the Preview menu.
// See methods onPreviewStillScene and onPreviewSequenceScene of the MainFrame class.
// To see what it should look like, see Figure D.13 on p 285 of the book.
public class ScenePreviewDlg extends JDialog {
    // Used in methods:
    //     onSelChangeCmbModels
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    //     onOK
    //     setTextBoxesWithModelTransform
    //     setTextBoxesWithViewTransform
    //     chooseModel
    //     onInitDialog
    protected MainFrame mMainFrame;

    // Set to true in method onCmdPlus
    // Read in method onOK
    protected boolean mbIsDirty;
    protected float mfIncrScaleFactor;

    private JCheckBox cbxRotationX;
    private JCheckBox cbxRotationY;
    private JCheckBox cbxRotationZ;
    private JCheckBox cbxScaleX;
    private JCheckBox cbxScaleY;
    private JCheckBox cbxScaleZ;
    private JCheckBox cbxTranslationX;
    private JCheckBox cbxTranslationY;
    private JCheckBox cbxTranslationZ;
    private JCheckBox cbxMoveViewPt;

    private JComboBox<String> cboModel;

    // DDX_Text(pDX, IDC_txtXIncr, m_txtXIncr);
    private JTextField txtIncrX;

    // DDX_Text(pDX, IDC_txtYIncr, m_txtYIncr);
    private JTextField txtIncrY;
    private JTextField txtIncrZ;
    private JTextField txtRotationX;
    private JTextField txtRotationY;
    private JTextField txtRotationZ;
    private JTextField txtScaleX;
    private JTextField txtScaleY;
    private JTextField txtScaleZ;
    private JTextField txtTranslationX;
    private JTextField txtTranslationY;
    private JTextField txtTranslationZ;

    // m_ckMoveViewPoint is true if the user sets the cbxMoveViewPt checkbox
    // DDX_Check(pDX, IDC_chkMoveViewPoint, m_chkMoveViewPoint);
    boolean	m_chkMoveViewPoint;

    // m_chkRx is true if the user sets the cbxRotationX checkbox
    // DDX_Check(pDX, IDC_chkRx, m_chkRx);
	boolean	m_chkRx;

    // m_chkRy is true if the user sets the cbxRotationY checkbox
    // DDX_Check(pDX, IDC_chkRy, m_chkRy);
	boolean	m_chkRy;

    // m_chkRz is true if the user sets the cbxRotationZ checkbox
    // DDX_Check(pDX, IDC_chkRz, m_chkRz);
	boolean	m_chkRz;

    // m_chkSx is true if the user sets the cbxScaleX checkbox
    // DDX_Check(pDX, IDC_chkSx, m_chkSx);
	boolean	m_chkSx;

    // m_chkSy is true if the user sets the cbxScaleY checkbox
    // DDX_Check(pDX, IDC_chkSy, m_chkSy);
	boolean	m_chkSy;

    // m_chkSz is true if the user sets the cbxScaleZ checkbox
    // DDX_Check(pDX, IDC_chkSz, m_chkSz);
	boolean	m_chkSz;

    // m_chkTx is true if the user sets the cbxTranslationX checkbox
    // DDX_Check(pDX, IDC_chkTx, m_chkTx);
	boolean	m_chkTx;

    // m_chkTy is true if the user sets the cbxTranslationY checkbox
    // DDX_Check(pDX, IDC_chkTy, m_chkTy);
	boolean	m_chkTy;

    // m_chkTz is true if the user sets the cbxTranslationZ checkbox
    // DDX_Check(pDX, IDC_chkTz, m_chkTz);
	boolean	m_chkTz;

    // m_txtCurRx holds the value in JTextfield txtRotationX
    // DDX_Text(pDX, IDC_txtCurRx, m_txtCurRx);
    String	m_txtCurRx;

    // m_txtCurRy holds the value in JTextfield txtRotationY
    // DDX_Text(pDX, IDC_txtCurRy, m_txtCurRy);
	String	m_txtCurRy;

    // m_txtCurRz holds the value in JTextfield txtRotationZ
    // DDX_Text(pDX, IDC_txtCurRz, m_txtCurRz);
	String	m_txtCurRz;

    // m_txtCurSx holds the value in JTextfield txtScaleX
    // DDX_Text(pDX, IDC_txtCurSx, m_txtCurSx);
	String	m_txtCurSx;

    // m_txtCurSy holds the value in JTextfield txtScaleY
    // DDX_Text(pDX, IDC_txtCurSy, m_txtCurSy);
	String	m_txtCurSy;

    // m_txtCurSz holds the value in JTextfield txtScaleZ
    // DDX_Text(pDX, IDC_txtCurSz, m_txtCurSz);
	String	m_txtCurSz;

    // m_txtCurTx holds the value in JTextfield txtTranslationX
    // DDX_Text(pDX, IDC_txtCurTx, m_txtCurTx);
	String	m_txtCurTx;

    // m_txtCurTy holds the value in JTextfield txtTranslationY
    // DDX_Text(pDX, IDC_txtCurTy, m_txtCurTy);
	String	m_txtCurTy;

    // m_txtCurTz holds the value in JTextfield txtTranslationZ
    // DDX_Text(pDX, IDC_txtCurTz, m_txtCurTz);
	String	m_txtCurTz;

    // m_txtXIncr holds the value in JTextfield txtIncrX
    // DDX_Text(pDX, IDC_txtXIncr, m_txtXIncr);
	String	m_txtXIncr;

    // m_txtYIncr holds the value in JTextfiled txtIncrY
    // DDX_Text(pDX, IDC_txtYIncr, m_txtYIncr);
	String	m_txtYIncr;

    // m_txtZIncr holds the value in JTextfield txtIncrZ
    // DDX_Text(pDX, IDC_txtZincr, m_txtZIncr);
	String	m_txtZIncr;

    // DDX_CBString(pDX, IDC_cmbModels, m_theModel);
	String	m_theModel;

    private int miDlgHeight = 260;
    private int miDlgWidth = 700;
    private Dimension mTinySpacerSize = new Dimension(5, 25);
    private Dimension mSmallSpacerSize = new Dimension(10, 25);

    private DecimalFormat sixDotTwo = new DecimalFormat("####.##");

/*
class CScenePreviewDlg : public CDialog
{
// Construction
public:
	CScenePreviewDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CScenePreviewDlg)
	enum { IDD = IDD_ScenePreview };
	BOOL	m_chkMoveViewPoint;
	BOOL	m_chkRx;
	BOOL	m_chkRy;
	BOOL	m_chkRz;
	BOOL	m_chkSx;
	BOOL	m_chkSy;
	BOOL	m_chkSz;
	BOOL	m_chkTx;
	BOOL	m_chkTy;
	BOOL	m_chkTz;
	CString	m_txtCurRx;
	CString	m_txtCurRy;
	CString	m_txtCurRz;
	CString	m_txtCurSx;
	CString	m_txtCurSy;
	CString	m_txtCurSz;
	CString	m_txtCurTx;
	CString	m_txtCurTy;
	CString	m_txtCurTz;
	CString	m_txtXIncr;
	CString	m_txtYIncr;
	CString	m_txtZIncr;
	CString	m_theModel;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CScenePreviewDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
    CMainFrame *m_theFrame;
    short isDirty;
	renderObject *anObject; // not used
    float incrementScaleFactor;
    void setTextBoxesWithViewTransform();
    void setTextBoxesWithModelTransform();
    void setViewPoint();
    void chooseModel();
    BOOL isChecked(int chkBoxID);
// Generated message map functions
	//{{AFX_MSG(CScenePreviewDlg)
	afx_msg void OnSelchangecmbModels();
	afx_msg void OnchkMoveViewPoint();
	afx_msg void OncmdMinus();
	afx_msg void OncmdPlus();
	afx_msg void OncmdReset();
	virtual void OnOK();
	virtual BOOL OnInitDialog();
	afx_msg void OnMove(int x, int y);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
 */

    // Called from:
    //     MainFrame.onPreviewSequenceScene
    //     MainFrame.onPreviewStillScene
    public ScenePreviewDlg(JFrame pParent, boolean pbModal) {
        super(pParent, pbModal);
        setTitle("Scene Preview");
        setSize(miDlgWidth, miDlgHeight);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Try to make the dialog appear as it does on the bottom part of 
        // Figure D.13 on page 285 of the book
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
    }


    // Called from the constructor
    private Box addTopSection() {
        Dimension lblSelModelSize = new Dimension(140, 25);
        Dimension lblMoveViewPointSize = new Dimension(150, 25);
        Dimension lblIncrementSize = new Dimension(90, 25);
        Dimension lblRotationSize = new Dimension(80, 25);
        Dimension lblScaleSize = new Dimension(50, 25);
        Dimension lblTranslationSize = new Dimension(110, 25);
        Dimension lblXSize = new Dimension(10, 25);
        Dimension lblYSize = new Dimension(10, 25);
        Dimension lblZSize = new Dimension(10, 25);
        Dimension txtFldSize = new Dimension(10, 30);
        Dimension btnPlusSize = new Dimension(10, 25);
        Dimension btnMinusSize = new Dimension(10, 25);
        Dimension btnResetSize = new Dimension(50, 25);
        Dimension btnSize = new Dimension(80, 30);
        Dimension buttonSpacerSize = new Dimension(80, 5);

        Component[] tinySpacers = createArrayOfTinySpacers(2);
        Component[] smallSpacers = createArrayOfSmallSpacers(6);

        Box horizBox   = Box.createHorizontalBox();
        Component tinySpacer01 = Box.createRigidArea(mTinySpacerSize);
        Component tinySpacer02 = Box.createRigidArea(mTinySpacerSize);
        Component btnSpacer = Box.createRigidArea(buttonSpacerSize);

        // will hold model/model view section
        Box vertBox01  = Box.createVerticalBox(); 
        Box horizBox01 = Box.createHorizontalBox();

        // will hold increment section
        Box vertBox02  = Box.createVerticalBox(); 
        Box horizBox02a = Box.createHorizontalBox();
        Box horizBox02b = Box.createHorizontalBox();
        Box horizBox02c = Box.createHorizontalBox();

        // will hold labels for the rotation/scale/translation section
        Box vertBox03  = Box.createVerticalBox(); 
        Box horizBox03a = Box.createHorizontalBox();
        Box horizBox03b = Box.createHorizontalBox();
        Box horizBox03c = Box.createHorizontalBox();

        // will hold rotation/scale/translation section
        Box vertBox04  = Box.createVerticalBox(); 
        Box horizBox04a = Box.createHorizontalBox();
        Box horizBox04b = Box.createHorizontalBox();
        Box horizBox04c = Box.createHorizontalBox();

        // Will hold OK and Cancel buttons
        Box vertBox05 = Box.createVerticalBox();

        // Create the model/model view section
        JLabel lblSelModel = new JLabel("Select a Model");
        lblSelModel.setSize(lblSelModelSize);
        lblSelModel.setPreferredSize(lblSelModelSize);

        cbxMoveViewPt = new JCheckBox();

        JLabel lblMoveViewPoint = new JLabel("Move View Point");
        lblMoveViewPoint.setSize(lblMoveViewPointSize);
        lblMoveViewPoint.setPreferredSize(lblMoveViewPointSize);

        horizBox01.add(cbxMoveViewPt);
        horizBox01.add(lblMoveViewPoint);

        vertBox01.add(lblSelModel);
        // vertBox01.add(); // TODO: Add a JList here
        vertBox01.add(horizBox01);

        // Create the increment section
        JLabel lblIncr = new JLabel("Increment");
        lblIncr.setSize(lblIncrementSize);
        lblIncr.setPreferredSize(lblIncrementSize);

        horizBox02a.add(lblIncr);

        JLabel lblIncrX = new JLabel("X");
        lblIncrX.setSize(lblXSize);
        lblIncrX.setPreferredSize(lblXSize);

        txtIncrX = new JTextField("0.00", 6);
        txtIncrX.setSize(txtFldSize);
        txtIncrX.setPreferredSize(txtFldSize);

        JLabel lblIncrY = new JLabel("Y");
        lblIncrY.setSize(lblYSize);
        lblIncrY.setPreferredSize(lblYSize);

        txtIncrY = new JTextField("0.00", 6);
        txtIncrY.setSize(txtFldSize);
        txtIncrY.setPreferredSize(txtFldSize);

        JLabel lblIncrZ = new JLabel("Z");
        lblIncrZ.setSize(lblZSize);
        lblIncrZ.setPreferredSize(lblZSize);

        txtIncrZ = new JTextField("0.00", 6);
        txtIncrZ.setSize(txtFldSize);
        txtIncrZ.setPreferredSize(txtFldSize);

        horizBox02b.add(lblIncrX);
        horizBox02b.add(txtIncrX);
        horizBox02b.add(tinySpacers[0]);
        horizBox02b.add(lblIncrY);
        horizBox02b.add(txtIncrY);
        horizBox02b.add(tinySpacers[1]);
        horizBox02b.add(lblIncrZ);
        horizBox02b.add(txtIncrZ);

        JButton btnPlus = new JButton("+");
        btnPlus.setSize(btnPlusSize);
        btnPlus.setPreferredSize(btnPlusSize);
        btnPlus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCmdPlus();
            }
        });

        JButton btnReset = new JButton("Reset");
        btnReset.setSize(btnResetSize);
        btnReset.setPreferredSize(btnResetSize);
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCmdReset();
            }
        });

        JButton btnMinus = new JButton("-");
        btnMinus.setSize(btnMinusSize);
        btnMinus.setPreferredSize(btnMinusSize);
        btnMinus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCmdMinus();
            }
        });

        horizBox02c.add(btnPlus);
        horizBox02c.add(tinySpacer01);
        horizBox02c.add(btnReset);
        horizBox02c.add(tinySpacer02);
        horizBox02c.add(btnMinus);

        vertBox02.add(horizBox02a);
        vertBox02.add(horizBox02b);
        vertBox02.add(horizBox02c);

        // Create the labels for the rotation/scale/translation section
        JLabel lblRotation = new JLabel("Rotation");
        lblRotation.setSize(lblRotationSize);
        lblRotation.setPreferredSize(lblRotationSize);

        JLabel lblScale = new JLabel("Scale");
        lblScale.setSize(lblScaleSize);
        lblScale.setPreferredSize(lblScaleSize);

        JLabel lblTranslation = new JLabel("Translation");
        lblTranslation.setSize(lblTranslationSize);
        lblTranslation.setPreferredSize(lblTranslationSize);

        horizBox03a.add(lblRotation);
        horizBox03b.add(lblScale);
        horizBox03c.add(lblTranslation);

        vertBox03.add(horizBox03a);
        vertBox03.add(horizBox03b);
        vertBox03.add(horizBox03c);

        // Create the rotation/scale/translation section
        JLabel lblRotX = new JLabel("X");
        lblRotX.setSize(lblXSize);
        lblRotX.setPreferredSize(lblXSize);

        JLabel lblRotY = new JLabel("Y");
        lblRotY.setSize(lblYSize);
        lblRotY.setPreferredSize(lblYSize);

        JLabel lblRotZ = new JLabel("Z");
        lblRotZ.setSize(lblZSize);
        lblRotZ.setPreferredSize(lblZSize);

        cbxRotationX = new JCheckBox();
        cbxRotationY = new JCheckBox();
        cbxRotationZ = new JCheckBox();

        horizBox04a.add(lblRotX);
        horizBox04a.add(cbxRotationX);
        horizBox04a.add(smallSpacers[0]);
        horizBox04a.add(lblRotY);
        horizBox04a.add(cbxRotationY);
        horizBox04a.add(smallSpacers[1]);
        horizBox04a.add(lblRotZ);
        horizBox04a.add(cbxRotationZ);

        JLabel lblSclX = new JLabel("X");
        lblSclX.setSize(lblXSize);
        lblSclX.setPreferredSize(lblXSize);

        JLabel lblSclY = new JLabel("Y");
        lblSclY.setSize(lblYSize);
        lblSclY.setPreferredSize(lblYSize);

        JLabel lblSclZ = new JLabel("Z");
        lblSclZ.setSize(lblZSize);
        lblSclZ.setPreferredSize(lblZSize);

        cbxScaleX = new JCheckBox();
        cbxScaleY = new JCheckBox();
        cbxScaleZ = new JCheckBox();

        horizBox04b.add(lblSclX);
        horizBox04b.add(cbxScaleX);
        horizBox04b.add(smallSpacers[2]);
        horizBox04b.add(lblSclY);
        horizBox04b.add(cbxScaleY);
        horizBox04b.add(smallSpacers[3]);
        horizBox04b.add(lblSclZ);
        horizBox04b.add(cbxScaleZ);

        JLabel lblTrnX = new JLabel("X");
        lblTrnX.setSize(lblXSize);
        lblTrnX.setPreferredSize(lblXSize);

        JLabel lblTrnY = new JLabel("Y");
        lblTrnY.setSize(lblYSize);
        lblTrnY.setPreferredSize(lblYSize);

        JLabel lblTrnZ = new JLabel("Z");
        lblTrnZ.setSize(lblZSize);
        lblTrnZ.setPreferredSize(lblZSize);

        cbxTranslationX = new JCheckBox();
        cbxTranslationY = new JCheckBox();
        cbxTranslationZ = new JCheckBox();

        horizBox04c.add(lblTrnX);
        horizBox04c.add(cbxTranslationX);
        horizBox04c.add(smallSpacers[4]);
        horizBox04c.add(lblTrnY);
        horizBox04c.add(cbxTranslationY);
        horizBox04c.add(smallSpacers[5]);
        horizBox04c.add(lblTrnZ);
        horizBox04c.add(cbxTranslationZ);

        vertBox04.add(horizBox04a);
        vertBox04.add(horizBox04b);
        vertBox04.add(horizBox04c);

        // Create the OK/Cancel button section
        JButton btnOk = new JButton("OK");
        btnOk.setSize(btnSize);
        btnOk.setPreferredSize(btnSize);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onOK();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setSize(btnSize);
        btnCancel.setPreferredSize(btnSize);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onCancel();
            }
        });

        vertBox05.add(btnOk);
        vertBox05.add(btnSpacer);
        vertBox05.add(btnCancel);

        horizBox.add(vertBox01);
        horizBox.add(vertBox02);
        horizBox.add(vertBox03);
        horizBox.add(vertBox04);
        horizBox.add(vertBox05);

        return horizBox;
    }


    // Called from the constructor
    private Box addBotSection() {
        final int iPnlWidth = miDlgWidth - 20;
        final int iPnlHeight = miDlgHeight / 2;

        Dimension lblRotationSize = new Dimension(80, 25);
        Dimension lblScaleSize = new Dimension(50, 25);
        Dimension lblTranslationSize = new Dimension(110, 25);

        Dimension lblXSize = new Dimension(10, 25);
        Dimension lblYSize = new Dimension(10, 25);
        Dimension lblZSize = new Dimension(10, 25);

        Dimension txtFldSize = new Dimension(70, 30);

        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Component[] tinySpacers = createArrayOfTinySpacers(6);

        Box horizBox   = Box.createHorizontalBox();

        Box vertBox01  = Box.createVerticalBox(); // will hold rotation section
        Box horizBox01 = Box.createHorizontalBox();

        Box vertBox02  = Box.createVerticalBox(); // will hold scale section
        Box horizBox02 = Box.createHorizontalBox();

        Box vertBox03  = Box.createVerticalBox(); // will hold translation section
        Box horizBox03 = Box.createHorizontalBox();

        // Rotation section
        JLabel lblRotation = new JLabel("Rotation");
        lblRotation.setSize(lblRotationSize);
        lblRotation.setPreferredSize(lblRotationSize);

        JLabel lblRotX = new JLabel("X");
        lblRotX.setSize(lblXSize);
        lblRotX.setPreferredSize(lblXSize);

        JLabel lblRotY = new JLabel("Y");
        lblRotY.setSize(lblYSize);
        lblRotY.setPreferredSize(lblYSize);

        JLabel lblRotZ = new JLabel("Z");
        lblRotZ.setSize(lblZSize);
        lblRotZ.setPreferredSize(lblZSize);

        txtRotationX = new JTextField("0.00", 6);
        txtRotationX.setSize(txtFldSize);
        txtRotationX.setPreferredSize(txtFldSize);
        txtRotationX.setMaximumSize(txtFldSize);

        txtRotationY = new JTextField("0.00", 6);
        txtRotationY.setSize(txtFldSize);
        txtRotationY.setPreferredSize(txtFldSize);
        txtRotationY.setMaximumSize(txtFldSize);

        txtRotationZ = new JTextField("0.00", 6);
        txtRotationZ.setSize(txtFldSize);
        txtRotationZ.setPreferredSize(txtFldSize);
        txtRotationZ.setMaximumSize(txtFldSize);

        horizBox01.add(lblRotX);
        horizBox01.add(txtRotationX);
        horizBox01.add(tinySpacers[0]);
        horizBox01.add(lblRotY);
        horizBox01.add(txtRotationY);
        horizBox01.add(tinySpacers[1]);
        horizBox01.add(lblRotZ);
        horizBox01.add(txtRotationZ);

        vertBox01.add(lblRotation);
        vertBox01.add(horizBox01);

        // Scale section
        JLabel lblScale = new JLabel("Scale");
        lblScale.setSize(lblScaleSize);
        lblScale.setPreferredSize(lblScaleSize);

        JLabel lblSclX = new JLabel("X");
        lblSclX.setSize(lblXSize);
        lblSclX.setPreferredSize(lblXSize);

        JLabel lblSclY = new JLabel("Y");
        lblSclY.setSize(lblYSize);
        lblSclY.setPreferredSize(lblYSize);

        JLabel lblSclZ = new JLabel("Z");
        lblSclZ.setSize(lblZSize);
        lblSclZ.setPreferredSize(lblZSize);

        txtScaleX = new JTextField("0.00", 6);
        txtScaleX.setSize(txtFldSize);
        txtScaleX.setPreferredSize(txtFldSize);
        txtScaleX.setMaximumSize(txtFldSize);

        txtScaleY = new JTextField("0.00", 6);
        txtScaleY.setSize(txtFldSize);
        txtScaleY.setPreferredSize(txtFldSize);
        txtScaleY.setMaximumSize(txtFldSize);

        txtScaleZ = new JTextField("0.00", 6);
        txtScaleZ.setSize(txtFldSize);
        txtScaleZ.setPreferredSize(txtFldSize);
        txtScaleZ.setMaximumSize(txtFldSize);
        
        horizBox02.add(lblSclX);
        horizBox02.add(txtScaleX);
        horizBox02.add(tinySpacers[2]);
        horizBox02.add(lblSclY);
        horizBox02.add(txtScaleY);
        horizBox02.add(tinySpacers[3]);
        horizBox02.add(lblSclZ);
        horizBox02.add(txtScaleZ);

        vertBox02.add(lblScale);
        vertBox02.add(horizBox02);

        // Translation Section
        JLabel lblTranslation = new JLabel("Translation");
        lblTranslation.setSize(lblTranslationSize);
        lblTranslation.setPreferredSize(lblTranslationSize);

        JLabel lblTrnX = new JLabel("X");
        lblTrnX.setSize(lblXSize);
        lblTrnX.setPreferredSize(lblXSize);

        JLabel lblTrnY = new JLabel("Y");
        lblTrnY.setSize(lblYSize);
        lblTrnY.setPreferredSize(lblYSize);

        JLabel lblTrnZ = new JLabel("Z");
        lblTrnZ.setSize(lblZSize);
        lblTrnZ.setPreferredSize(lblZSize);

        txtTranslationX = new JTextField("0.00", 6);
        txtTranslationX.setSize(txtFldSize);
        txtTranslationX.setPreferredSize(txtFldSize);
        txtTranslationX.setMaximumSize(txtFldSize);

        txtTranslationY = new JTextField("0.00", 6);
        txtTranslationY.setSize(txtFldSize);
        txtTranslationY.setPreferredSize(txtFldSize);
        txtTranslationY.setMaximumSize(txtFldSize);

        txtTranslationZ = new JTextField("0.00", 6);
        txtTranslationZ.setSize(txtFldSize);
        txtTranslationZ.setPreferredSize(txtFldSize);
        txtTranslationZ.setMaximumSize(txtFldSize);

        horizBox03.add(lblTrnX);
        horizBox03.add(txtTranslationX);
        horizBox03.add(tinySpacers[4]);
        horizBox03.add(lblTrnY);
        horizBox03.add(txtTranslationY);
        horizBox03.add(tinySpacers[5]);
        horizBox03.add(lblTrnZ);
        horizBox03.add(txtTranslationZ);

        vertBox03.add(lblTranslation);
        vertBox03.add(horizBox03);

        Dimension pnlSize = new Dimension(iPnlWidth, iPnlHeight);
        JPanel pnlCurrLocnAndOrtn = new JPanel();
        pnlCurrLocnAndOrtn.setSize(pnlSize);
        pnlCurrLocnAndOrtn.setMaximumSize(pnlSize);
        pnlCurrLocnAndOrtn.setPreferredSize(pnlSize);

        BoxLayout boxLayout = new BoxLayout(pnlCurrLocnAndOrtn, BoxLayout.X_AXIS);
        pnlCurrLocnAndOrtn.setLayout(boxLayout);
        pnlCurrLocnAndOrtn.setBorder(BorderFactory.createTitledBorder(loweredetched, "Current Location and Orientation"));

        pnlCurrLocnAndOrtn.add(vertBox01);
        pnlCurrLocnAndOrtn.add(vertBox02);
        pnlCurrLocnAndOrtn.add(vertBox03);

        horizBox.add(pnlCurrLocnAndOrtn);

        return horizBox;
    }

    private Component[] createArrayOfTinySpacers(int piNumSpacers) {
        Component[] tinySpacers = new Component[piNumSpacers];

        for (int i = 0; i < piNumSpacers; i++) {
            tinySpacers[i] = Box.createRigidArea(mTinySpacerSize);
        }

        return tinySpacers;
    }


    private Component[] createArrayOfSmallSpacers(int piNumSpacers) {
        Component[] smallSpacers = new Component[piNumSpacers];

        for (int i = 0; i < piNumSpacers; i++) {
            smallSpacers[i] = Box.createRigidArea(mSmallSpacerSize);
        }

        return smallSpacers;
    }


    /*
    // This method came from SCENEPREVIEWDLG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);

        //{{AFX_DATA_MAP(CScenePreviewDlg)
        DDX_Check(pDX, IDC_chkMoveViewPoint, m_chkMoveViewPoint);
        DDX_Check(pDX, IDC_chkRx, m_chkRx);
        DDX_Check(pDX, IDC_chkRy, m_chkRy);
        DDX_Check(pDX, IDC_chkRz, m_chkRz);

        DDX_Check(pDX, IDC_chkSx, m_chkSx);
        DDX_Check(pDX, IDC_chkSy, m_chkSy);
        DDX_Check(pDX, IDC_chkSz, m_chkSz);

        DDX_Check(pDX, IDC_chkTx, m_chkTx);
        DDX_Check(pDX, IDC_chkTy, m_chkTy);
        DDX_Check(pDX, IDC_chkTz, m_chkTz);

        DDX_Text(pDX, IDC_txtCurRx, m_txtCurRx);
        DDX_Text(pDX, IDC_txtCurRy, m_txtCurRy);
        DDX_Text(pDX, IDC_txtCurRz, m_txtCurRz);

        DDX_Text(pDX, IDC_txtCurSx, m_txtCurSx);
        DDX_Text(pDX, IDC_txtCurSy, m_txtCurSy);
        DDX_Text(pDX, IDC_txtCurSz, m_txtCurSz);

        DDX_Text(pDX, IDC_txtCurTx, m_txtCurTx);
        DDX_Text(pDX, IDC_txtCurTy, m_txtCurTy);
        DDX_Text(pDX, IDC_txtCurTz, m_txtCurTz);

        DDX_Text(pDX, IDC_txtXIncr, m_txtXIncr);
        DDX_Text(pDX, IDC_txtYIncr, m_txtYIncr);
        DDX_Text(pDX, IDC_txtZincr, m_txtZIncr);
        
        DDX_CBString(pDX, IDC_cmbModels, m_theModel);
        //}}AFX_DATA_MAP
    }
    */

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // ON_CBN_SELCHANGE(IDC_cmbModels, OnSelchangecmbModels)
    void onSelChangeCmbModels() {
        String sSelectedModel;
        Point3d rot = new Point3d();
        Point3d scale = new Point3d();
        Point3d tran = new Point3d();

        int iChoice = cboModel.getSelectedIndex();

        if (iChoice != -1) {
            sSelectedModel = (String)cboModel.getSelectedItem();
            mMainFrame.mSceneList.setCurrentModel(sSelectedModel);

            // The following method sets all of the parameters
            mMainFrame.mSceneList.getCurrentModelTransform(
                rot, 
                scale, 
                tran);
                
            mMainFrame.mWarpTranslateX = tran.fX;
            mMainFrame.mWarpTranslateY = tran.fY;
            mMainFrame.mWarpTranslateZ = tran.fZ;

            mMainFrame.mWarpScaleX = scale.fX;
            mMainFrame.mWarpScaleY = scale.fY;
            mMainFrame.mWarpScaleZ = scale.fZ;

            mMainFrame.mWarpRotateX = rot.fX;
            mMainFrame.mWarpRotateY = rot.fY;
            mMainFrame.mWarpRotateZ = rot.fZ;

            setTextBoxesWithModelTransform();
        }
    } // onSelChangeCmbModels
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // ON_BN_CLICKED(IDC_chkMoveViewPoint, OnchkMoveViewPoint)
    void onChkMoveViewPoint() {
        mMainFrame.mbChangeViewPoint = !mMainFrame.mbChangeViewPoint;
        if(mMainFrame.mbChangeViewPoint == true) {
            setTextBoxesWithViewTransform();
        } else {
            setTextBoxesWithModelTransform();
        }
    } // onChkMoveViewPoint
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the - button
    // ON_BN_CLICKED(IDC_cmdMinus, OncmdMinus)
    void onCmdMinus() {
        mfIncrScaleFactor = -1.0f;
        onCmdPlus();	
    } // onCmdMinus
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the + button
    // ON_BN_CLICKED(IDC_cmdPlus, OncmdPlus)
    // Called from:
    //     onCmdMinus
    void onCmdPlus() {
        // String theBuffer; // not used
        boolean bChangingModel = (mMainFrame.mbChangeViewPoint == false);

        if(bChangingModel) {
            if(
            (mMainFrame.miEffectType == JICTConstants.I_SEQUENCE) || 
            (mMainFrame.miEffectType == JICTConstants.I_MORPH)) {
                cboModel.setSelectedIndex(0);    // Select the first model
                chooseModel();
            } else {
                // Make certain a model has been selected
                if(cboModel.getSelectedIndex() == -1 ) {
                    Globals.beep(100, 100);
                    Globals.statusPrint("Please select a model");
                    return;
                }
            }
        }

        mbIsDirty = true;
        // int bufferLength = 16; // not used

        String sBuffer;
        sBuffer = txtIncrX.getText();
        float fDeltaX = Float.parseFloat(sBuffer) * mfIncrScaleFactor;

        sBuffer = txtIncrY.getText();
        float fDeltaY = Float.parseFloat(sBuffer) * mfIncrScaleFactor;

        sBuffer = txtIncrZ.getText();
        float fDeltaZ = Float.parseFloat(sBuffer) * mfIncrScaleFactor;
        
        if(cbxTranslationX.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpTranslateX += fDeltaX;
            } else {
                mMainFrame.mViewTranslateX += fDeltaX;
            }
        }

        if(cbxTranslationY.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpTranslateY += fDeltaY;
            } else {
                mMainFrame.mViewTranslateY += fDeltaY;
            }
        }

        if(cbxTranslationZ.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpTranslateZ += fDeltaZ;
            } else {
                mMainFrame.mViewTranslateZ += fDeltaZ;
            }
        }

        if(cbxScaleX.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpScaleX = fDeltaX;
            }
        }

        if(cbxScaleY.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpScaleY = fDeltaY;
            }
        }

        if(cbxScaleZ.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpScaleZ = fDeltaZ;
            }
        }

        if(cbxRotationX.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpRotateX += fDeltaX;
                mMainFrame.mWarpRotateX = MathUtils.fPolar(mMainFrame.mWarpRotateX);
            } else {
                mMainFrame.mViewRotateX += fDeltaX;
                mMainFrame.mViewRotateX = MathUtils.fPolar(mMainFrame.mViewRotateX);
            }
        }

        if(cbxRotationY.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpRotateY += fDeltaY;
                mMainFrame.mWarpRotateY = MathUtils.fPolar(mMainFrame.mWarpRotateY);
            } else {
                mMainFrame.mViewRotateY += fDeltaY;
                mMainFrame.mViewRotateY = MathUtils.fPolar(mMainFrame.mViewRotateY);
            }
        }

        if(cbxRotationZ.isSelected()) {
            if(bChangingModel) {
                mMainFrame.mWarpRotateZ += fDeltaZ;
                mMainFrame.mWarpRotateZ = MathUtils.fPolar(mMainFrame.mWarpRotateZ);
            } else {
                mMainFrame.mViewRotateZ += fDeltaZ;
                mMainFrame.mViewRotateZ = MathUtils.fPolar(mMainFrame.mViewRotateZ);
            }
        }

        if(bChangingModel) {
            // Save the current Transform parameters
            mMainFrame.mSceneList.setCurrentModelTransform(
                mMainFrame.mWarpRotateX,    mMainFrame.mWarpRotateY,    mMainFrame.mWarpRotateZ,
                mMainFrame.mWarpScaleX,     mMainFrame.mWarpScaleY,     mMainFrame.mWarpScaleZ,
                mMainFrame.mWarpTranslateX, mMainFrame.mWarpTranslateY, mMainFrame.mWarpTranslateZ);
            setTextBoxesWithModelTransform();
        } else {
            setTextBoxesWithViewTransform();
        }

        //  Build the view matrix
        mMainFrame.mViewMatrix.setIdentity();
        float fXRadians = mMainFrame.mViewRotateX * JICTConstants.F_DTR;
        float fYRadians = mMainFrame.mViewRotateY * JICTConstants.F_DTR;
        float fZRadians = mMainFrame.mViewRotateZ * JICTConstants.F_DTR;
        mMainFrame.mViewMatrix.rotate(-fXRadians, -fYRadians, -fZRadians);
        mMainFrame.mViewMatrix.translate(-mMainFrame.mViewTranslateX,
            -mMainFrame.mViewTranslateY, -mMainFrame.mViewTranslateZ);

        //  Redraw the scene list
        mMainFrame.mPreviewWindowHandle.repaint();
        mfIncrScaleFactor = 1.0f;
    } // onCmdPlus
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the Reset button
    // ON_BN_CLICKED(IDC_cmdReset, OncmdReset)
    void onCmdReset() {
        if(mMainFrame.mbChangeViewPoint == false) {  // If manipulating a model...
            mMainFrame.mWarpTranslateX = 0.0f;
            mMainFrame.mWarpTranslateY = 0.0f;
            mMainFrame.mWarpTranslateZ = 0.0f;

            mMainFrame.mWarpScaleX = 1.0f;
            mMainFrame.mWarpScaleY = 1.0f;
            mMainFrame.mWarpScaleZ = 1.0f;

            mMainFrame.mWarpRotateX = 0.0f;
            mMainFrame.mWarpRotateY = 0.0f;
            mMainFrame.mWarpRotateZ = 0.0f;
            setTextBoxesWithModelTransform();
        } else {  // If manipulating the viewPoint...
            mMainFrame.mViewTranslateX = 0.0f;
            mMainFrame.mViewTranslateY = 0.0f;
            mMainFrame.mViewTranslateZ = 0.0f;

            mMainFrame.mViewRotateX = 0.0f;
            mMainFrame.mViewRotateY = 0.0f;
            mMainFrame.mViewRotateZ = 0.0f;
            setTextBoxesWithViewTransform();
        }	
    } // onCmdReset
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the OK button in the dialog.
    void onOK() {
        StringBuffer sbBuffer = new StringBuffer();

        mMainFrame.mSceneList.setViewTransform(
            mMainFrame.mViewTranslateX, mMainFrame.mViewTranslateY, mMainFrame.mViewTranslateZ, 
            mMainFrame.mViewRotateX,    mMainFrame.mViewRotateY,    mMainFrame.mViewRotateZ);

        if(mbIsDirty) {
            //int result = MessageBox("Do you want to save the scene file?", "A model has changed",
            //    MB_YESNO|MB_ICONQUESTION);
            int iResult = JOptionPane.showConfirmDialog(null, 
                "Do you want to save the scene file?", "A model has changed", 
                JOptionPane.YES_NO_OPTION);

            switch(iResult) {
            case JOptionPane.YES_OPTION:
                mMainFrame.mSceneList.writeList(sbBuffer, mMainFrame.msSceneFileName);
                // TODO: Returned error code is ignored
            } // switch

            mMainFrame.repaint();
        } // if (mbIsDirty)
    } // onOK


    // Called when the Cancel button is clicked
    public void onCancel() {
        this.dispose();
    }


    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called from:
    //     chooseModel
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    //     onInitDialog
    //     onSelChangeCmbModels
    void setTextBoxesWithModelTransform() {
        String sBuffer;

        // warpRotate
        sBuffer = sixDotTwo.format(mMainFrame.mWarpRotateX);
        txtRotationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpRotateY);
        txtRotationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpRotateZ);
        txtRotationZ.setText(sBuffer);
        
        // warpScale
        sBuffer = sixDotTwo.format(mMainFrame.mWarpScaleX);
        txtScaleX.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpScaleY);
        txtScaleY.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpScaleZ);
        txtScaleZ.setText(sBuffer);
        
        // warpTranslate
        sBuffer = sixDotTwo.format(mMainFrame.mWarpTranslateX);
        txtTranslationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpTranslateY);
        txtTranslationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mWarpTranslateZ);
        txtTranslationZ.setText(sBuffer);
    } // setTextBoxesWithModelTransform
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called from:
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    void setTextBoxesWithViewTransform() {
        String sBuffer;

        sBuffer = sixDotTwo.format(mMainFrame.mViewRotateX);
        txtRotationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mViewRotateY);
        txtRotationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mViewRotateZ);
        txtRotationZ.setText(sBuffer);
        
        sBuffer = sixDotTwo.format(0.0f);
        txtScaleX.setText(sBuffer);
        txtScaleY.setText(sBuffer);
        txtScaleZ.setText(sBuffer);
        
        sBuffer = sixDotTwo.format(mMainFrame.mViewTranslateX);
        txtTranslationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mViewTranslateY);
        txtTranslationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(mMainFrame.mViewTranslateZ);
        txtTranslationZ.setText(sBuffer);
    } // setTextBoxesWithViewTransform
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called from:
    //     onCmdPlus
    void chooseModel() {
        String sSelectedModel;
        Point3d rot = new Point3d();
        Point3d scale = new Point3d();
        Point3d tran = new Point3d();

        sSelectedModel = (String)cboModel.getSelectedItem();
        mMainFrame.mSceneList.setCurrentModel(sSelectedModel);

        // The following method modifies parameters rot, scale and tran
        mMainFrame.mSceneList.getCurrentModelTransform(rot, scale, tran);

        mMainFrame.mWarpTranslateX = tran.fX;
        mMainFrame.mWarpTranslateY = tran.fY;
        mMainFrame.mWarpTranslateZ = tran.fZ;
        
        mMainFrame.mWarpScaleX = scale.fX;
        mMainFrame.mWarpScaleY = scale.fY;
        mMainFrame.mWarpScaleZ = scale.fZ;
        
        mMainFrame.mWarpRotateX = rot.fX;
        mMainFrame.mWarpRotateY = rot.fY;
        mMainFrame.mWarpRotateZ = rot.fZ;

        setTextBoxesWithModelTransform();
        mMainFrame.mbChangeViewPoint = false;
    } // chooseModel
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called before the dialog box is displayed.
    void onInitDialog() {
        setTextBoxesWithModelTransform();

        // Place model names in cboModel
        mMainFrame.mSceneList.showModels(cboModel);  
        mMainFrame.mbChangeViewPoint = false;
    } // onInitDialog
    

    // This method came from SCENEPREVIEWDLG.CPP
    //
    // ON_WM_MOVE()
    void onMove(int x, int y) {
        
    } // onMove
} // class ScenePreviewDlg