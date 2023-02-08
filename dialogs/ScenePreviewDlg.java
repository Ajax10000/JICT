package dialogs;

import core.RenderObject;

import frames.MainFrame;

import globals.Globals;

import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import math.MathUtils;

// This dialog is displayed when the user selects the 
// "Still" or "Sequence" menu item from the Preview menu.
// See methods onPreviewStillScene and onPreviewSequenceScene of the MainFrame class.
// To see what it should look like, see Figure D.13 on p 285 of the book.
public class ScenePreviewDlg extends JDialog {
    protected MainFrame m_theFrame;
    protected boolean isDirty;
	protected RenderObject anObject;
    protected float incrementScaleFactor;

    private JButton btnOk;
    private JButton btnCancel;
    private JButton btnReset;
    private JButton btnMinus;
    private JButton btnPlus;

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

    private DecimalFormat sixDotTwo = new DecimalFormat("####.##");

    // Effect Types
    // These were defined in ICT20.H
    public static final int STILL    = 1;
    public static final int SEQUENCE = 2;
    public static final int MORPH    = 3;

    // This value came from ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;

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
	renderObject *anObject;
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
    public ScenePreviewDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Try to make the dialog appear as it does on the bottom part of 
        // Figure D.13 on page 285 of the book
        JPanel topPnl;
        JPanel botPnl;
        setupTopPanel();
        setupBottomPanel();
    }

    private void setupTopPanel() {
        JLabel lblRotation = new JLabel("Rotation");
        JLabel lblScale = new JLabel("Scale");
        JLabel lblTranslation = new JLabel("Translation");
        JLabel lblRotX = new JLabel("X");
        JLabel lblRotY = new JLabel("Y");
        JLabel lblRotZ = new JLabel("Z");
        JLabel lblSclX = new JLabel("X");
        JLabel lblSclY = new JLabel("Y");
        JLabel lblSclZ = new JLabel("Z");
        JLabel lblTrnX = new JLabel("X");
        JLabel lblTrnY = new JLabel("Y");
        JLabel lblTrnZ = new JLabel("Z");

        JLabel lblIncr = new JLabel("Increment");
        JLabel lblSelModel = new JLabel("Select a Model");

        btnPlus = new JButton("+");
        btnMinus = new JButton("-");
        btnReset = new JButton("Reset");
    }

    private void setupBottomPanel() {
        JLabel lblRotation = new JLabel("Rotation");
        JLabel lblScale = new JLabel("Scale");
        JLabel lblTranslation = new JLabel("Translation");
        JLabel lblRotX = new JLabel("X");
        JLabel lblRotY = new JLabel("Y");
        JLabel lblRotZ = new JLabel("Z");
        JLabel lblSclX = new JLabel("X");
        JLabel lblSclY = new JLabel("Y");
        JLabel lblSclZ = new JLabel("Z");
        JLabel lblTrnX = new JLabel("X");
        JLabel lblTrnY = new JLabel("Y");
        JLabel lblTrnZ = new JLabel("Z");

        txtRotationX    = new JTextField("0.00", 6);
        txtRotationY    = new JTextField("0.00", 6);
        txtRotationZ    = new JTextField("0.00", 6);
        txtScaleX       = new JTextField("0.00", 6);
        txtScaleY       = new JTextField("0.00", 6);
        txtScaleZ       = new JTextField("0.00", 6);
        txtTranslationX = new JTextField("0.00", 6);
        txtTranslationY = new JTextField("0.00", 6);
        txtTranslationZ = new JTextField("0.00", 6);
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

    // This method came from SCENEPREVIEWDLG.CPP
    // ON_CBN_SELCHANGE(IDC_cmbModels, OnSelchangecmbModels)
    void onSelChangeCmbModels() {
        String selectedModel;
        Float rx = 0f, ry = 0f, rz = 0f;
        Float sx = 0f, sy = 0f, sz = 0f;
        Float tx = 0f, ty = 0f, tz = 0f;

        int theChoice = cboModel.getSelectedIndex();

        if (theChoice != -1) {
            selectedModel = (String)cboModel.getSelectedItem();
            m_theFrame.mySceneList.setCurrentModel(selectedModel);

            // The following method sets all of the parameters
            m_theFrame.mySceneList.getCurrentModelTransform(
                rx, ry, rz, 
                sx, sy, sz, 
                tx, ty, tz);
                
            m_theFrame.mWarpTranslateX = tx;
            m_theFrame.mWarpTranslateY = ty;
            m_theFrame.mWarpTranslateZ = tz;

            m_theFrame.mWarpScaleX = sx;
            m_theFrame.mWarpScaleY = sy;
            m_theFrame.mWarpScaleZ = sz;

            m_theFrame.mWarpRotateX = rx;
            m_theFrame.mWarpRotateY = ry;
            m_theFrame.mWarpRotateZ = rz;

            setTextBoxesWithModelTransform();
        }
    } // onSelChangeCmbModels
    

    // This method came from SCENEPREVIEWDLG.CPP
    // ON_BN_CLICKED(IDC_chkMoveViewPoint, OnchkMoveViewPoint)
    void onChkMoveViewPoint() {
        m_theFrame.changeViewPoint = !m_theFrame.changeViewPoint;
        if(m_theFrame.changeViewPoint == true) {
            setTextBoxesWithViewTransform();
        } else {
            setTextBoxesWithModelTransform();
        }
    } // onChkMoveViewPoint
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called when the user clicks on the - button
    // ON_BN_CLICKED(IDC_cmdMinus, OncmdMinus)
    void onCmdMinus() {
        incrementScaleFactor = -1.0f;
        onCmdPlus();	
    } // onCmdMinus
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called when the user clicks on the + button
    // ON_BN_CLICKED(IDC_cmdPlus, OncmdPlus)
    // Called from:
    //     onCmdMinus
    void onCmdPlus() {
        String theBuffer;
        boolean changingModel = (m_theFrame.changeViewPoint == false);

        if(changingModel) {
            if((m_theFrame.effectType == SEQUENCE) || (m_theFrame.effectType == MORPH)) {
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

        isDirty = true;
        int bufferLength = 16;

        String aBuffer;
        aBuffer = txtIncrX.getText();
        float deltaX = Float.parseFloat(aBuffer) * incrementScaleFactor;

        aBuffer = txtIncrY.getText();
        float deltaY = Float.parseFloat(aBuffer) * incrementScaleFactor;

        aBuffer = txtIncrZ.getText();
        float deltaZ = Float.parseFloat(aBuffer) * incrementScaleFactor;
        
        if(cbxTranslationX.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpTranslateX += deltaX;
            } else {
                m_theFrame.mViewTranslateX += deltaX;
            }
        }

        if(cbxTranslationY.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpTranslateY += deltaY;
            } else {
                m_theFrame.mViewTranslateY += deltaY;
            }
        }

        if(cbxTranslationZ.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpTranslateZ += deltaZ;
            } else {
                m_theFrame.mViewTranslateZ += deltaZ;
            }
        }

        if(cbxScaleX.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpScaleX = deltaX;
            }
        }

        if(cbxScaleY.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpScaleY = deltaY;
            }
        }

        if(cbxScaleZ.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpScaleZ = deltaZ;
            }
        }

        if(cbxRotationX.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpRotateX += deltaX;
                m_theFrame.mWarpRotateX = MathUtils.fPolar(m_theFrame.mWarpRotateX);
            } else {
                m_theFrame.mViewRotateX += deltaX;
                m_theFrame.mViewRotateX = MathUtils.fPolar(m_theFrame.mViewRotateX);
            }
        }

        if(cbxRotationY.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpRotateY += deltaY;
                m_theFrame.mWarpRotateY = MathUtils.fPolar(m_theFrame.mWarpRotateY);
            } else {
                m_theFrame.mViewRotateY += deltaY;
                m_theFrame.mViewRotateY = MathUtils.fPolar(m_theFrame.mViewRotateY);
            }
        }

        if(cbxRotationZ.isSelected()) {
            if(changingModel) {
                m_theFrame.mWarpRotateZ += deltaZ;
                m_theFrame.mWarpRotateZ = MathUtils.fPolar(m_theFrame.mWarpRotateZ);
            } else {
                m_theFrame.mViewRotateZ += deltaZ;
                m_theFrame.mViewRotateZ = MathUtils.fPolar(m_theFrame.mViewRotateZ);
            }
        }

        if(changingModel) {
            // Save the current Transform parameters
            m_theFrame.mySceneList.setCurrentModelTransform(
                m_theFrame.mWarpRotateX,    m_theFrame.mWarpRotateY,    m_theFrame.mWarpRotateZ,
                m_theFrame.mWarpScaleX,     m_theFrame.mWarpScaleY,     m_theFrame.mWarpScaleZ,
                m_theFrame.mWarpTranslateX, m_theFrame.mWarpTranslateY, m_theFrame.mWarpTranslateZ);
            setTextBoxesWithModelTransform();
        } else {
            setTextBoxesWithViewTransform();
        }

        //  Build the view matrix
        m_theFrame.mViewMatrix.setIdentity();
        float xRadians = m_theFrame.mViewRotateX * F_DTR;
        float yRadians = m_theFrame.mViewRotateY * F_DTR;
        float zRadians = m_theFrame.mViewRotateZ * F_DTR;
        m_theFrame.mViewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        m_theFrame.mViewMatrix.translate(-m_theFrame.mViewTranslateX,
            -m_theFrame.mViewTranslateY, -m_theFrame.mViewTranslateZ);

        //  Redraw the scene list
        m_theFrame.previewWindowHandle.repaint();
        incrementScaleFactor = 1.0f;
    } // onCmdPlus
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called when the user clicks on the Reset button
    // ON_BN_CLICKED(IDC_cmdReset, OncmdReset)
    void onCmdReset() {
        if(m_theFrame.changeViewPoint == false) {  // If manipulating a model...
            m_theFrame.mWarpTranslateX = 0.0f;
            m_theFrame.mWarpTranslateY = 0.0f;
            m_theFrame.mWarpTranslateZ = 0.0f;

            m_theFrame.mWarpScaleX = 1.0f;
            m_theFrame.mWarpScaleY = 1.0f;
            m_theFrame.mWarpScaleZ = 1.0f;

            m_theFrame.mWarpRotateX = 0.0f;
            m_theFrame.mWarpRotateY = 0.0f;
            m_theFrame.mWarpRotateZ = 0.0f;
            setTextBoxesWithModelTransform();
        } else {  // If manipulating the viewPoint...
            m_theFrame.mViewTranslateX = 0.0f;
            m_theFrame.mViewTranslateY = 0.0f;
            m_theFrame.mViewTranslateZ = 0.0f;

            m_theFrame.mViewRotateX = 0.0f;
            m_theFrame.mViewRotateY = 0.0f;
            m_theFrame.mViewRotateZ = 0.0f;
            setTextBoxesWithViewTransform();
        }	
    } // onCmdReset
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called when the user clicks on the OK button in the dialog.
    void onOK() {
        String sBuffer = "";

        m_theFrame.mySceneList.setViewTransform(
            m_theFrame.mViewTranslateX, m_theFrame.mViewTranslateY, m_theFrame.mViewTranslateZ, 
            m_theFrame.mViewRotateX,    m_theFrame.mViewRotateY,    m_theFrame.mViewRotateZ);

        if(isDirty) {
            //int result = MessageBox("Do you want to save the scene file?", "A model has changed",
            //    MB_YESNO|MB_ICONQUESTION);
            int result = JOptionPane.showConfirmDialog(null, 
                "Do you want to save the scene file?", "A model has changed", 
                JOptionPane.YES_NO_OPTION);

            switch(result) {
            case JOptionPane.YES_OPTION:
                m_theFrame.mySceneList.writeList(sBuffer, m_theFrame.sceneFileName);
            } // switch

            m_theFrame.repaint();
        }
    } // onOK
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     chooseModel
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    //     onInitDialog
    //     onSelChangeCmbModels
    void setTextBoxesWithModelTransform() {
        String aBuffer;

        // warpRotate
        aBuffer = sixDotTwo.format(m_theFrame.mWarpRotateX);
        txtRotationX.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpRotateY);
        txtRotationY.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpRotateZ);
        txtRotationZ.setText(aBuffer);
        
        // warpScale
        aBuffer = sixDotTwo.format(m_theFrame.mWarpScaleX);
        txtScaleX.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpScaleY);
        txtScaleY.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpScaleZ);
        txtScaleZ.setText(aBuffer);
        
        // warpTranslate
        aBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateX);
        txtTranslationX.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateY);
        txtTranslationY.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateZ);
        txtTranslationZ.setText(aBuffer);
    } // setTextBoxesWithModelTransform
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    void setTextBoxesWithViewTransform() {
        String aBuffer;

        aBuffer = sixDotTwo.format(m_theFrame.mViewRotateX);
        txtRotationX.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mViewRotateY);
        txtRotationY.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mViewRotateZ);
        txtRotationZ.setText(aBuffer);
        
        aBuffer = sixDotTwo.format(0.0f);
        txtScaleX.setText(aBuffer);
        txtScaleY.setText(aBuffer);
        txtScaleZ.setText(aBuffer);
        
        aBuffer = sixDotTwo.format(m_theFrame.mViewTranslateX);
        txtTranslationX.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mViewTranslateY);
        txtTranslationY.setText(aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.mViewTranslateZ);
        txtTranslationZ.setText(aBuffer);
    } // setTextBoxesWithViewTransform
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onCmdPlus
    void chooseModel() {
        String selectedModel;
        Float rx = 0f, ry = 0f, rz = 0f;
        Float sx = 0f, sy = 0f, sz = 0f;
        Float tx = 0f, ty = 0f, tz = 0f;

        selectedModel = (String)cboModel.getSelectedItem();
        m_theFrame.mySceneList.setCurrentModel(selectedModel);
        m_theFrame.mySceneList.getCurrentModelTransform(rx, ry, rz, sx, sy, sz, tx, ty, tz);

        m_theFrame.mWarpTranslateX = tx;
        m_theFrame.mWarpTranslateY = ty;
        m_theFrame.mWarpTranslateZ = tz;
        
        m_theFrame.mWarpScaleX = sx;
        m_theFrame.mWarpScaleY = sy;
        m_theFrame.mWarpScaleZ = sz;
        
        m_theFrame.mWarpRotateX = rx;
        m_theFrame.mWarpRotateY = ry;
        m_theFrame.mWarpRotateZ = rz;

        setTextBoxesWithModelTransform();
        m_theFrame.changeViewPoint = false;
    } // chooseModel
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called before the dialog box is displayed.
    void onInitDialog() {
        setTextBoxesWithModelTransform();

        // Place model names in cboModel
        m_theFrame.mySceneList.showModels(cboModel);  
        m_theFrame.changeViewPoint = false;
    } // onInitDialog
    

    // This method came from SCENEPREVIEWDLG.CPP
    // ON_WM_MOVE()
    void onMove(int x, int y) {
        
    } // onMove
} // class ScenePreviewDlg