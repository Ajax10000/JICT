package dialogs;

import core.MemImage;

import fileUtils.BMPFileFilter;
import fileUtils.FileUtils;

import globals.Globals;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import math.TMatrix;

// This dialog is displayed  when the user selects the 
// "Create a Morph Sequence..." menu item from the Tools menu.
// See method onToolsMorphSequence of the MainFrame class.
// To see what it should look like, see Figure D.11 on p 283 of the book.
public class MorphDlg extends JDialog {
    int m_morphType;
    int m_numFrames;
    String m_morph1Path;
    String m_morph2Path;
    String m_firstOutputPath;

    // EDITTEXT        IDC_NumBlurFrames,43,116,36,13,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_NumBlurFrames, m_preNumFrames);
	JTextField	m_preNumFrames;

    // EDITTEXT        IDC_EDITDIRECTORY,43,80,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITDIRECTORY, m_firstOutPath);
	JTextField	m_firstOutPath;

    // EDITTEXT        IDC_EDITFIRSTIMAGE2,43,50,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITFIRSTIMAGE2, m_secondImage);
	JTextField	m_secondImage;

    // EDITTEXT        IDC_EDITFIRSTIMAGE,42,17,194,14,ES_AUTOHSCROLL
    // DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
	JTextField	m_firstImage;

    // CONTROL         "2D",IDC_2D,"Button",BS_AUTORADIOBUTTON,140,117,25,10
    JRadioButton rbn2D;

    // CONTROL         "3D",IDC_3D,"Button",BS_AUTORADIOBUTTON,140,128,25,10
    JRadioButton rbn3D;

    // GROUPBOX        "Type",IDC_Type,132,104,41,37
    JPanel pnlGroupBox;

    // DEFPUSHBUTTON   "OK",IDOK,206,110,29,14
    JButton btnOK;

    // PUSHBUTTON      "Cancel",IDCANCEL,206,126,29,14
    JButton btnCancel;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,17,34,14
    JButton btnLocateFirstImgToMorph;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR2,6,80,34,14
    JButton btnLocateSecImgToMorph;

    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR3,6,50,34,14
    JButton btnLocateCompletePath;

    // LTEXT           "First Image to Morph",-1,43,6,81,11
    JLabel lblFirstImg;

    // LTEXT           "Second Image to Morph",-1,44,39,81,11
    JLabel lblSecImg;

    // LTEXT           "Complete pathname of first image in output sequence.",
    //                 IDC_STATIC2,45,68,181,11
    JLabel lblComplPath;

    // LTEXT           "#Frames in Sequence",-1,43,106,73,10
    JLabel lblNumFrames;


    // These values were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    // These values were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 0;

    // These values were defined in MORPHDIALOG.H
    public static final int I_TWOD   = 2;
    public static final int I_THREED = 3;

/* 
class CMorphDialog : public CDialog
{
// Construction
public:
	CMorphDialog(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CMorphDialog)
	enum { IDD = IDD_Morph };
	CEdit	m_preNumFrames;
	CEdit	m_firstOutPath;
	CEdit	m_secondImage;
	CEdit	m_firstImage;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMorphDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
     int m_morphType;
     int m_numFrames;
     char m_morph1Path[80];
     char m_morph2Path[80];
     char m_firstOutputPath[80];
	// Generated message map functions
	//{{AFX_MSG(CMorphDialog)
	afx_msg void OnLocatedestdir();
	afx_msg void OnLocatedestdir2();
	afx_msg void OnLocatedestdir3();
	afx_msg void On2d();
	afx_msg void On3d();
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
*/

    // This constructor came from MORPHDIALOG.CPP
    public MorphDlg(JFrame pParent, boolean pModal) {
        super(pParent, pModal);

        setTitle("Create Morph Sequence");
    } // MorphDlg ctor


    /*
    // This method came from MORPHDIALOG.CPP
    void DoDataExchange(CDataExchange pDX) {
        CDialog.DoDataExchange(pDX);
        
        //{{AFX_DATA_MAP(CMorphDialog)
        DDX_Control(pDX, IDC_NumBlurFrames, m_preNumFrames);
        DDX_Control(pDX, IDC_EDITDIRECTORY, m_firstOutPath);
        DDX_Control(pDX, IDC_EDITFIRSTIMAGE2, m_secondImage);
        DDX_Control(pDX, IDC_EDITFIRSTIMAGE, m_firstImage);
        //}}AFX_DATA_MAP
    }
    */

    /////////////////////////////////////////////////////////////////////////////
    // CMorphDialog message handlers

    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the top LOCATE button
    // Note the (x, y) coordinates for this button are (5, 17)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR,5,17,34,14
    private void onLocateDestDir() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select morph image");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_firstImage.setText(sFileName);
        }
    } // onLocateDestDir


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the bottom locate button 
    // Note the (x, y) coordinates for this button are are (6, 80)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR2,6,80,34,14
    private void onLocateDestDir2() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dlg.setFileFilter(new BMPFileFilter());
        int showDlgResult = dlg.showDialog(null, "Select morph image");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_firstOutPath.setText(sFileName);
        }
    } // onLocateDestDir2


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the middle Locate button
    // Note the (x, y) coordinates for this button are (6, 50)
    // PUSHBUTTON      "Locate",IDC_LOCATEDESTDIR3,6,50,34,14
    private void onLocateDestDir3() {
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int showDlgResult = dlg.showDialog(this, "Select scn file");

        if (showDlgResult == JFileChooser.APPROVE_OPTION) {
            File file = dlg.getSelectedFile();
            String sFileName = file.getName();
            m_secondImage.setText(sFileName);	
        }
    } // onLocateDestDir3


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the 2D radio button
    // CONTROL         "2D",IDC_2D,"Button",BS_AUTORADIOBUTTON,140,117,25,10
    private void on2d() {
        m_morphType = I_TWOD;
    } // on2d


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the 3D radio button
    // CONTROL         "3D",IDC_3D,"Button",BS_AUTORADIOBUTTON,140,128,25,10
    private void on3d() {
        m_morphType = I_THREED;
    } // on3d


    // This method came from MORPHDIALOG.CPP
    // Called when the user clicks on the OK button
    // DEFPUSHBUTTON   "OK",IDOK,206,110,29,14
    private void onOK() {
        String msgText;
        MemImage inImageA, inImageB;
        float aFraction, increment;
        String imagePath, shapePath;
        int aStatus, i, outFrameNum = 0;
        String outPath, aNumFrames, firstImage, secondImage;
        String directory = "", fileName = "", prefix = "", suffix = "";
        // String cNumFrames, cPathName; // These variables are not being used.

        aNumFrames  = m_preNumFrames.getText();
        m_numFrames = Integer.parseInt(aNumFrames);

        firstImage  = m_firstImage.getText();
        secondImage = m_secondImage.getText();
        outPath     = m_firstOutPath.getText();

        FileUtils.getPathPieces(outPath, directory, fileName,
            prefix, outFrameNum, suffix);

        switch(m_morphType) {
        case I_TWOD:
            inImageA = new MemImage(firstImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            if(!inImageA.isValid()) {
                Globals.statusPrint("CMorphDialog: Unable to open firstImage.");
                return;
            }
            inImageA.adjustImageBorder(firstImage);

            inImageB = new MemImage(secondImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            if(!inImageB.isValid()) {
                Globals.statusPrint("CMorphDialog: Unable to open secondImage.");
                return;
            }

            inImageB.adjustImageBorder(secondImage);

            inImageB = new MemImage(firstImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            inImageA = new MemImage(secondImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            aFraction = 0.0f;
            increment = 1.0f/(float)(m_numFrames - 1.0f);

            for(i = 1; i <= m_numFrames; i++) {
                imagePath = directory + prefix + String.format("%04o", i) + suffix + ".bmp";
                Globals.statusPrint(imagePath);

                shapePath = directory + prefix + String.format("%04o", i) + suffix + ".shp";
                aStatus = Globals.tweenImage(aFraction, inImageA, inImageB, imagePath, shapePath);
                aFraction += increment;
            }
            break;

        case I_THREED:
            // Given two quadmeshes a and b, create a sequence of images where the
            // result mesh transitions from a to b in numFrames frames.
            float fractionIncrement;
            Integer aHeight = 0, aWidth = 0, bpp = 0;

            String aTexPath, aXPath = "", aYPath = "", aZPath = "";
            String bTexPath, bXPath = "", bYPath = "", bZPath = "";
            MemImage aTexture, aX, aY, aZ;
            MemImage bTexture, bX, bY, bZ; 
            MemImage oTexture, oX, oY, oZ;
            
            aTexPath = new String(firstImage);
            FileUtils.constructPathName(aXPath, aTexPath, 'x');
            FileUtils.constructPathName(aYPath, aTexPath, 'y');
            FileUtils.constructPathName(aZPath, aTexPath, 'z');
            aStatus = Globals.readBMPHeader(aTexPath, aHeight, aWidth, bpp);
            if(aStatus != 0) {
                Globals.statusPrint("MorphDlg.onOK(): Unable to open image header.");
                return;
            }

            bpp = 8;
            switch (bpp) {
            case 8:
                aTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', GREENCOLOR);
                break;

            case 24:
                aTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', RGBCOLOR);
                break;

            default:
                Globals.statusPrint ("MorphDlg.onOK(): Image a must have 8 or 24 bit pixels");
                return;
            } // switch

            aX = new MemImage(aXPath, 0, 0, RANDOM, 'R', A32BIT);
            aY = new MemImage(aYPath, 0, 0, RANDOM, 'R', A32BIT);
            aZ = new MemImage(aZPath, 0, 0, RANDOM, 'R', A32BIT);

            bTexPath = new String(secondImage);
            FileUtils.constructPathName(bXPath, bTexPath, 'x');
            FileUtils.constructPathName(bYPath, bTexPath, 'y');
            FileUtils.constructPathName(bZPath, bTexPath, 'z');
        
            switch (bpp) {
            case 8:
                bTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', GREENCOLOR);
                break;

            case 24:
                bTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', RGBCOLOR);
                break;

            default:
                Globals.statusPrint("MorphDlg.onOK(): Image b must have 8 or 24 bit pixels");
                return;
            } // switch

            bX = new MemImage(bXPath, 0, 0, RANDOM, 'R', A32BIT);
            bY = new MemImage(bYPath, 0, 0, RANDOM, 'R', A32BIT);
            bZ = new MemImage(bZPath, 0, 0, RANDOM, 'R', A32BIT);

            int imHeight = aTexture.getHeight();
            int imWidth  = aTexture.getWidth();

            oTexture = new MemImage(imHeight, imWidth, bpp);
            oX = new MemImage(imHeight, imWidth, 32);
            oY = new MemImage(imHeight, imWidth, 32);
            oZ = new MemImage(imHeight, imWidth, 32);
            aFraction = 1.0f;
            fractionIncrement = 1.0f/((float)m_numFrames - 1.0f);
            
            TMatrix aMatrix = new TMatrix();
            Float centroidX = 0f, centroidY = 0f, centroidZ = 0f;

            for (i = 1; i <= m_numFrames; i++) {
                msgText = "tweenMesh. Frame " + i + "  aFraction: " + aFraction;
                Globals.statusPrint(msgText);
                aStatus = Globals.tweenMesh(aFraction, 
                    aTexture, aX, aY, aZ,
                    bTexture, bX, bY, bZ, 
                    oTexture, oX, oY, oZ);

                // Move the mesh to the center of the view screen
                aStatus = Globals.getMeshCentroid(oX, oY, oZ,
                    centroidX, centroidY, centroidZ);

                aStatus = Globals.translateMesh(oX, oY, oZ,
                    -centroidX, -centroidY, -centroidZ);

                // sprintf(imagePath, "%s%s%#04d%c.bmp", directory, prefix, i, suffix);
                imagePath = directory + prefix + String.format("%04o", i) + suffix + ".shp";
                Globals.statusPrint(imagePath);
                aStatus = Globals.renderMesh(imagePath, oTexture, oX, oY, oZ, aMatrix);

                aFraction -= fractionIncrement;
                oTexture.clear();
                oX.clear();
                oY.clear();
                oZ.clear();
            }

            Globals.statusPrint("3D morph sequence complete.");
            break;
        } // switch

        this.setVisible(false);
    } // onOK

    private void onCancel() {
        this.setVisible(false);
    } // onCancel
} // class MorphDlg