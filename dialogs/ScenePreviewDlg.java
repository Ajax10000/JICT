package dialogs;

import core.RenderObject;

import frames.MainFrame;

import globals.Globals;
import globals.JICTConstants;

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
    protected float mfIncrScaleFactor;

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
    public ScenePreviewDlg(JFrame pParent, boolean pbModal) {
        super(pParent, pbModal);

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

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // ON_CBN_SELCHANGE(IDC_cmbModels, OnSelchangecmbModels)
    void onSelChangeCmbModels() {
        String sSelectedModel;
        Float fRx = 0f, fRy = 0f, fRz = 0f;
        Float fSx = 0f, fSy = 0f, fSz = 0f;
        Float fTx = 0f, fTy = 0f, fTz = 0f;

        int iChoice = cboModel.getSelectedIndex();

        if (iChoice != -1) {
            sSelectedModel = (String)cboModel.getSelectedItem();
            m_theFrame.mSceneList.setCurrentModel(sSelectedModel);

            // The following method sets all of the parameters
            m_theFrame.mSceneList.getCurrentModelTransform(
                fRx, fRy, fRz, 
                fSx, fSy, fSz, 
                fTx, fTy, fTz);
                
            m_theFrame.mWarpTranslateX = fTx;
            m_theFrame.mWarpTranslateY = fTy;
            m_theFrame.mWarpTranslateZ = fTz;

            m_theFrame.mWarpScaleX = fSx;
            m_theFrame.mWarpScaleY = fSy;
            m_theFrame.mWarpScaleZ = fSz;

            m_theFrame.mWarpRotateX = fRx;
            m_theFrame.mWarpRotateY = fRy;
            m_theFrame.mWarpRotateZ = fRz;

            setTextBoxesWithModelTransform();
        }
    } // onSelChangeCmbModels
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // ON_BN_CLICKED(IDC_chkMoveViewPoint, OnchkMoveViewPoint)
    void onChkMoveViewPoint() {
        m_theFrame.mbChangeViewPoint = !m_theFrame.mbChangeViewPoint;
        if(m_theFrame.mbChangeViewPoint == true) {
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
        boolean bChangingModel = (m_theFrame.mbChangeViewPoint == false);

        if(bChangingModel) {
            if(
            (m_theFrame.mIEffectType == JICTConstants.I_SEQUENCE) || 
            (m_theFrame.mIEffectType == JICTConstants.I_MORPH)) {
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
                m_theFrame.mWarpTranslateX += fDeltaX;
            } else {
                m_theFrame.mViewTranslateX += fDeltaX;
            }
        }

        if(cbxTranslationY.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpTranslateY += fDeltaY;
            } else {
                m_theFrame.mViewTranslateY += fDeltaY;
            }
        }

        if(cbxTranslationZ.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpTranslateZ += fDeltaZ;
            } else {
                m_theFrame.mViewTranslateZ += fDeltaZ;
            }
        }

        if(cbxScaleX.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpScaleX = fDeltaX;
            }
        }

        if(cbxScaleY.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpScaleY = fDeltaY;
            }
        }

        if(cbxScaleZ.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpScaleZ = fDeltaZ;
            }
        }

        if(cbxRotationX.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpRotateX += fDeltaX;
                m_theFrame.mWarpRotateX = MathUtils.fPolar(m_theFrame.mWarpRotateX);
            } else {
                m_theFrame.mViewRotateX += fDeltaX;
                m_theFrame.mViewRotateX = MathUtils.fPolar(m_theFrame.mViewRotateX);
            }
        }

        if(cbxRotationY.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpRotateY += fDeltaY;
                m_theFrame.mWarpRotateY = MathUtils.fPolar(m_theFrame.mWarpRotateY);
            } else {
                m_theFrame.mViewRotateY += fDeltaY;
                m_theFrame.mViewRotateY = MathUtils.fPolar(m_theFrame.mViewRotateY);
            }
        }

        if(cbxRotationZ.isSelected()) {
            if(bChangingModel) {
                m_theFrame.mWarpRotateZ += fDeltaZ;
                m_theFrame.mWarpRotateZ = MathUtils.fPolar(m_theFrame.mWarpRotateZ);
            } else {
                m_theFrame.mViewRotateZ += fDeltaZ;
                m_theFrame.mViewRotateZ = MathUtils.fPolar(m_theFrame.mViewRotateZ);
            }
        }

        if(bChangingModel) {
            // Save the current Transform parameters
            m_theFrame.mSceneList.setCurrentModelTransform(
                m_theFrame.mWarpRotateX,    m_theFrame.mWarpRotateY,    m_theFrame.mWarpRotateZ,
                m_theFrame.mWarpScaleX,     m_theFrame.mWarpScaleY,     m_theFrame.mWarpScaleZ,
                m_theFrame.mWarpTranslateX, m_theFrame.mWarpTranslateY, m_theFrame.mWarpTranslateZ);
            setTextBoxesWithModelTransform();
        } else {
            setTextBoxesWithViewTransform();
        }

        //  Build the view matrix
        m_theFrame.mViewMatrix.setIdentity();
        float fXRadians = m_theFrame.mViewRotateX * JICTConstants.F_DTR;
        float fYRadians = m_theFrame.mViewRotateY * JICTConstants.F_DTR;
        float fZRadians = m_theFrame.mViewRotateZ * JICTConstants.F_DTR;
        m_theFrame.mViewMatrix.rotate(-fXRadians, -fYRadians, -fZRadians);
        m_theFrame.mViewMatrix.translate(-m_theFrame.mViewTranslateX,
            -m_theFrame.mViewTranslateY, -m_theFrame.mViewTranslateZ);

        //  Redraw the scene list
        m_theFrame.mPreviewWindowHandle.repaint();
        mfIncrScaleFactor = 1.0f;
    } // onCmdPlus
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the Reset button
    // ON_BN_CLICKED(IDC_cmdReset, OncmdReset)
    void onCmdReset() {
        if(m_theFrame.mbChangeViewPoint == false) {  // If manipulating a model...
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
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called when the user clicks on the OK button in the dialog.
    void onOK() {
        String sBuffer = "";

        m_theFrame.mSceneList.setViewTransform(
            m_theFrame.mViewTranslateX, m_theFrame.mViewTranslateY, m_theFrame.mViewTranslateZ, 
            m_theFrame.mViewRotateX,    m_theFrame.mViewRotateY,    m_theFrame.mViewRotateZ);

        if(isDirty) {
            //int result = MessageBox("Do you want to save the scene file?", "A model has changed",
            //    MB_YESNO|MB_ICONQUESTION);
            int iResult = JOptionPane.showConfirmDialog(null, 
                "Do you want to save the scene file?", "A model has changed", 
                JOptionPane.YES_NO_OPTION);

            switch(iResult) {
            case JOptionPane.YES_OPTION:
                m_theFrame.mSceneList.writeList(sBuffer, m_theFrame.msSceneFileName);
            } // switch

            m_theFrame.repaint();
        }
    } // onOK
    

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
        sBuffer = sixDotTwo.format(m_theFrame.mWarpRotateX);
        txtRotationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpRotateY);
        txtRotationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpRotateZ);
        txtRotationZ.setText(sBuffer);
        
        // warpScale
        sBuffer = sixDotTwo.format(m_theFrame.mWarpScaleX);
        txtScaleX.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpScaleY);
        txtScaleY.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpScaleZ);
        txtScaleZ.setText(sBuffer);
        
        // warpTranslate
        sBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateX);
        txtTranslationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateY);
        txtTranslationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mWarpTranslateZ);
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

        sBuffer = sixDotTwo.format(m_theFrame.mViewRotateX);
        txtRotationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mViewRotateY);
        txtRotationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mViewRotateZ);
        txtRotationZ.setText(sBuffer);
        
        sBuffer = sixDotTwo.format(0.0f);
        txtScaleX.setText(sBuffer);
        txtScaleY.setText(sBuffer);
        txtScaleZ.setText(sBuffer);
        
        sBuffer = sixDotTwo.format(m_theFrame.mViewTranslateX);
        txtTranslationX.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mViewTranslateY);
        txtTranslationY.setText(sBuffer);

        sBuffer = sixDotTwo.format(m_theFrame.mViewTranslateZ);
        txtTranslationZ.setText(sBuffer);
    } // setTextBoxesWithViewTransform
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called from:
    //     onCmdPlus
    void chooseModel() {
        String sSelectedModel;
        Float fRx = 0f, fRy = 0f, fRz = 0f;
        Float fSx = 0f, fSy = 0f, fSz = 0f;
        Float fTx = 0f, fTy = 0f, fTz = 0f;

        sSelectedModel = (String)cboModel.getSelectedItem();
        m_theFrame.mSceneList.setCurrentModel(sSelectedModel);
        m_theFrame.mSceneList.getCurrentModelTransform(fRx, fRy, fRz, fSx, fSy, fSz, fTx, fTy, fTz);

        m_theFrame.mWarpTranslateX = fTx;
        m_theFrame.mWarpTranslateY = fTy;
        m_theFrame.mWarpTranslateZ = fTz;
        
        m_theFrame.mWarpScaleX = fSx;
        m_theFrame.mWarpScaleY = fSy;
        m_theFrame.mWarpScaleZ = fSz;
        
        m_theFrame.mWarpRotateX = fRx;
        m_theFrame.mWarpRotateY = fRy;
        m_theFrame.mWarpRotateZ = fRz;

        setTextBoxesWithModelTransform();
        m_theFrame.mbChangeViewPoint = false;
    } // chooseModel
    

    // This method originally came from SCENEPREVIEWDLG.CPP
    //
    // Called before the dialog box is displayed.
    void onInitDialog() {
        setTextBoxesWithModelTransform();

        // Place model names in cboModel
        m_theFrame.mSceneList.showModels(cboModel);  
        m_theFrame.mbChangeViewPoint = false;
    } // onInitDialog
    

    // This method came from SCENEPREVIEWDLG.CPP
    //
    // ON_WM_MOVE()
    void onMove(int x, int y) {
        
    } // onMove
} // class ScenePreviewDlg