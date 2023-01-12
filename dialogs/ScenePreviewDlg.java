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

    private JTextField txtIncrX;
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

    boolean	m_chkMoveViewPoint;
	boolean	m_chkRx;
	boolean	m_chkRy;
	boolean	m_chkRz;
	boolean	m_chkSx;
	boolean	m_chkSy;
	boolean	m_chkSz;
	boolean	m_chkTx;
	boolean	m_chkTy;
	boolean	m_chkTz;

    String	m_txtCurRx;
	String	m_txtCurRy;
	String	m_txtCurRz;
	String	m_txtCurSx;
	String	m_txtCurSy;
	String	m_txtCurSz;
	String	m_txtCurTx;
	String	m_txtCurTy;
	String	m_txtCurTz;
	String	m_txtXIncr;
	String	m_txtYIncr;
	String	m_txtZIncr;
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
    void onSelChangeCmbModels() {
        String selectedModel;
        float  rx, ry, rz, sx, sy, sz, tx, ty, tz;
        JComboBox theCombo;
        
        theCombo = (JComboBox)GetDlgItem(IDC_cmbModels);
        int theChoice = theCombo.getSelectedIndex();

        if (theChoice != -1) {
            theCombo.GetLBText(theChoice, selectedModel);
            m_theFrame.mySceneList.setCurrentModel(selectedModel);
            m_theFrame.mySceneList.getCurrentModelTransform(
                rx, ry, rz, sx, sy, sz, tx, ty, tz);
                
            m_theFrame.warpTranslateX = tx;
            m_theFrame.warpTranslateY = ty;
            m_theFrame.warpTranslateZ = tz;
        
            m_theFrame.warpScaleX = sx;
            m_theFrame.warpScaleY = sy;
            m_theFrame.warpScaleZ = sz;
        
            m_theFrame.warpRotateX  = rx;
            m_theFrame.warpRotateY  = ry;
            m_theFrame.warpRotateZ  = rz;
            setTextBoxesWithModelTransform();
        }
    } // onSelChangeCmbModels
    

    // This method came from SCENEPREVIEWDLG.CPP
    void onChkMoveViewPoint() {
        m_theFrame.changeViewPoint = !m_theFrame.changeViewPoint;
        if(m_theFrame.changeViewPoint == true) {
            setTextBoxesWithViewTransform();
        } else {
            setTextBoxesWithModelTransform();
        }
    } // onChkMoveViewPoint
    

    // This method came from SCENEPREVIEWDLG.CPP
    void onCmdMinus() {
        incrementScaleFactor = -1.0f;
        onCmdPlus();	
    } // onCmdMinus
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onCmdMinus
    void onCmdPlus() {
        String theBuffer;
        boolean changingModel = (m_theFrame.changeViewPoint == false);
        JComboBox cmbModels = (JComboBox)GetDlgItem(IDC_cmbModels);

        if(changingModel) {
            if((m_theFrame.effectType == SEQUENCE) || (m_theFrame.effectType == MORPH)) {
                cmbModels.setSelectedIndex(0);    // Select the first model
                chooseModel();
            } else {
                // Make certain a model has been selected
                if(cmbModels.getSelectedIndex() == -1 ) {
                    Globals.beep(100, 100);
                    Globals.statusPrint("Please select a model");
                    return;
                }
            }
        }

        isDirty = true;
        int bufferLength = 16;

        String aBuffer;
        GetDlgItemText(IDC_txtXIncr, aBuffer, bufferLength);
        float deltaX = Float.parseFloat(aBuffer) * incrementScaleFactor;

        GetDlgItemText(IDC_txtYIncr, aBuffer, bufferLength);
        float deltaY = Float.parseFloat(aBuffer) * incrementScaleFactor;

        GetDlgItemText(IDC_txtZincr, aBuffer, bufferLength);
        float deltaZ = Float.parseFloat(aBuffer) * incrementScaleFactor;
        
        if(isChecked(IDC_chkTx)) {
            if(changingModel) {
                m_theFrame.warpTranslateX += deltaX;
            } else {
                m_theFrame.viewTranslateX += deltaX;
            }
        }

        if(isChecked(IDC_chkTy)) {
            if(changingModel) {
                m_theFrame.warpTranslateY += deltaY;
            } else {
                m_theFrame.viewTranslateY += deltaY;
            }
        }

        if(isChecked(IDC_chkTz)) {
            if(changingModel) {
                m_theFrame.warpTranslateZ += deltaZ;
            } else {
                m_theFrame.viewTranslateZ += deltaZ;
            }
        }

        if(isChecked(IDC_chkSx)) {
            if(changingModel) {
                m_theFrame.warpScaleX = deltaX;
            }
        }

        if(isChecked(IDC_chkSy)) {
            if(changingModel) {
                m_theFrame.warpScaleY = deltaY;
            }
        }

        if(isChecked(IDC_chkSz)) {
            if(changingModel) {
                m_theFrame.warpScaleZ = deltaZ;
            }
        }

        if(isChecked(IDC_chkRx)) {
            if(changingModel) {
                m_theFrame.warpRotateX += deltaX;
                m_theFrame.warpRotateX = Globals.fPolar(m_theFrame.warpRotateX);
            } else {
                m_theFrame.viewRotateX += deltaX;
                m_theFrame.viewRotateX = Globals.fPolar(m_theFrame.viewRotateX);
            }
        }

        if(isChecked(IDC_chkRy)) {
            if(changingModel) {
                m_theFrame.warpRotateY += deltaY;
                m_theFrame.warpRotateY = Globals.fPolar(m_theFrame.warpRotateY);
            } else {
                m_theFrame.viewRotateY += deltaY;
                m_theFrame.viewRotateY = Globals.fPolar(m_theFrame.viewRotateY);
            }
        }

        if(isChecked(IDC_chkRz)) {
            if(changingModel) {
                m_theFrame.warpRotateZ += deltaZ;
                m_theFrame.warpRotateZ = Globals.fPolar(m_theFrame.warpRotateZ);
            } else {
                m_theFrame.viewRotateZ += deltaZ;
                m_theFrame.viewRotateZ = Globals.fPolar(m_theFrame.viewRotateZ);
            }
        }

        if(changingModel) {
            // Save the current Transform parameters
            m_theFrame.mySceneList.setCurrentModelTransform(
                m_theFrame.warpRotateX,    m_theFrame.warpRotateY,    m_theFrame.warpRotateZ,
                m_theFrame.warpScaleX,     m_theFrame.warpScaleY,     m_theFrame.warpScaleZ,
                m_theFrame.warpTranslateX, m_theFrame.warpTranslateY, m_theFrame.warpTranslateZ);
            setTextBoxesWithModelTransform();
        } else {
            setTextBoxesWithViewTransform();
        }

        //  Build the view Matrix
        m_theFrame.viewMatrix.setIdentity();
        float xRadians = m_theFrame.viewRotateX * F_DTR;
        float yRadians = m_theFrame.viewRotateY * F_DTR;
        float zRadians = m_theFrame.viewRotateZ * F_DTR;
        m_theFrame.viewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        m_theFrame.viewMatrix.translate(-m_theFrame.viewTranslateX,
            -m_theFrame.viewTranslateY, -m_theFrame.viewTranslateZ);

        //  Redraw the scene list
        m_theFrame.previewWindowHandle.repaint();
        incrementScaleFactor = 1.0f;
    } // onCmdPlus
    

    // This method came from SCENEPREVIEWDLG.CPP
    void onCmdReset() {
        if(m_theFrame.changeViewPoint == false) {  // If manipulating a model...
            m_theFrame.warpTranslateX = 0.0f;
            m_theFrame.warpTranslateY = 0.0f;
            m_theFrame.warpTranslateZ = 0.0f;

            m_theFrame.warpScaleX = 1.0f;
            m_theFrame.warpScaleY = 1.0f;
            m_theFrame.warpScaleZ = 1.0f;

            m_theFrame.warpRotateX = 0.0f;
            m_theFrame.warpRotateY = 0.0f;
            m_theFrame.warpRotateZ = 0.0f;
            setTextBoxesWithModelTransform();
        } else {  // If manipulating the viewPoint...
            m_theFrame.viewTranslateX = 0.0f;
            m_theFrame.viewTranslateY = 0.0f;
            m_theFrame.viewTranslateZ = 0.0f;

            m_theFrame.viewRotateX = 0.0f;
            m_theFrame.viewRotateY = 0.0f;
            m_theFrame.viewRotateZ = 0.0f;
            setTextBoxesWithViewTransform();
        }	
    } // onCmdReset
    

    // This method came from SCENEPREVIEWDLG.CPP
    void onOK() {
        String Buffer;

        m_theFrame.mySceneList.setViewTransform(
            m_theFrame.viewTranslateX, m_theFrame.viewTranslateY, m_theFrame.viewTranslateZ, 
            m_theFrame.viewRotateX,    m_theFrame.viewRotateY,    m_theFrame.viewRotateZ);

        if(isDirty) {
            //int result = MessageBox("Do you want to save the scene file?", "A model has changed",
            //    MB_YESNO|MB_ICONQUESTION);
            int result = JOptionPane.showConfirmDialog(null, 
                "Do you want to save the scene file?", "A model has changed", 
                JOptionPane.YES_NO_OPTION);

            switch(result) {
            case JOptionPane.YES_OPTION:
                m_theFrame.mySceneList.writeList(Buffer, m_theFrame.sceneFileName);
            } // switch

            m_theFrame.repaint();
            CDialog.OnOK();
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
        aBuffer = sixDotTwo.format(m_theFrame.warpRotateX);
        SetDlgItemText(IDC_txtCurRx, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpRotateY);
        SetDlgItemText(IDC_txtCurRy, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpRotateZ);
        SetDlgItemText(IDC_txtCurRz, aBuffer);
        
        // warpScale
        aBuffer = sixDotTwo.format(m_theFrame.warpScaleX);
        SetDlgItemText(IDC_txtCurSx, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpScaleY);
        SetDlgItemText(IDC_txtCurSy, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpScaleZ);
        SetDlgItemText(IDC_txtCurSz, aBuffer);
        
        // warpTranslate
        aBuffer = sixDotTwo.format(m_theFrame.warpTranslateX);
        SetDlgItemText(IDC_txtCurTx, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpTranslateY);
        SetDlgItemText(IDC_txtCurTy, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.warpTranslateZ);
        SetDlgItemText(IDC_txtCurTz, aBuffer);
    } // setTextBoxesWithModelTransform
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onChkMoveViewPoint
    //     onCmdPlus
    //     onCmdReset
    void setTextBoxesWithViewTransform() {
        String aBuffer;

        aBuffer = sixDotTwo.format(m_theFrame.viewRotateX);
        SetDlgItemText(IDC_txtCurRx, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.viewRotateY);
        SetDlgItemText(IDC_txtCurRy, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.viewRotateZ);
        SetDlgItemText(IDC_txtCurRz, aBuffer);
        
        aBuffer = sixDotTwo.format(0.0f);
        SetDlgItemText(IDC_txtCurSx, aBuffer);
        SetDlgItemText(IDC_txtCurSy, aBuffer);
        SetDlgItemText(IDC_txtCurSz, aBuffer);
        
        aBuffer = sixDotTwo.format(m_theFrame.viewTranslateX);
        SetDlgItemText(IDC_txtCurTx, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.viewTranslateY);
        SetDlgItemText(IDC_txtCurTy, aBuffer);

        aBuffer = sixDotTwo.format(m_theFrame.viewTranslateZ);
        SetDlgItemText(IDC_txtCurTz, aBuffer);
    } // setTextBoxesWithViewTransform
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onCmdPlus
    void chooseModel() {
        String selectedModel;
        Float rx = 0f, ry = 0f, rz = 0f;
        Float sx = 0f, sy = 0f, sz = 0f;
        Float tx = 0f, ty = 0f, tz = 0f;
        JComboBox theCombo;
        theCombo = (JComboBox)GetDlgItem(IDC_cmbModels);
        theCombo.GetLBText(theCombo.getSelectedIndex(), selectedModel);
        
        m_theFrame.mySceneList.setCurrentModel(selectedModel);
        m_theFrame.mySceneList.getCurrentModelTransform(rx, ry, rz, sx, sy, sz, tx, ty, tz);
        m_theFrame.warpTranslateX = tx;
        m_theFrame.warpTranslateY = ty;
        m_theFrame.warpTranslateZ = tz;
        
        m_theFrame.warpScaleX = sx;
        m_theFrame.warpScaleY = sy;
        m_theFrame.warpScaleZ = sz;
        
        m_theFrame.warpRotateX  = rx;
        m_theFrame.warpRotateY  = ry;
        m_theFrame.warpRotateZ  = rz;
        setTextBoxesWithModelTransform();
        m_theFrame.changeViewPoint = false;
    } // chooseModel
    

    // This method came from SCENEPREVIEWDLG.CPP
    boolean onInitDialog() {
        CDialog.OnInitDialog();
        
        setTextBoxesWithModelTransform();
        JComboBox cmbModels = (JComboBox)GetDlgItem(IDC_cmbModels);
        // model names in cboBox
        m_theFrame.mySceneList.showModels(cmbModels);  
        m_theFrame.changeViewPoint = false;
    
        CRect windowRect;
        GetUpdateRect(windowRect);
        int dialogLeft   = 50;
        int dialogWidth  = windowRect.right  - windowRect.left + 1;
        int dialogHeight = windowRect.bottom - windowRect.top  + 1;
        int dialogTop    = 320;
        
        // return true unless you set the focus to a control
        return true;
    } // onInitDialog
    

    // This method came from SCENEPREVIEWDLG.CPP
    // Called from:
    //     onCmdPlus
    boolean isChecked(int chkBoxID) {
       JButton aChkBox = (JButton)GetDlgItem(chkBoxID);
       return aChkBox.GetCheck();
    } // isChecked
    
    
    // This method came from SCENEPREVIEWDLG.CPP
    void onMove(int x, int y) {
        CDialog.OnMove(x, y);	
    } // onMove
} // class ScenePreviewDlg