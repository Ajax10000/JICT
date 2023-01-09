package dialogs;

import core.MemImage;

import globals.Globals;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

import math.TMatrix;

public class MorphDlg extends JDialog {

	JTextField	m_preNumFrames;
	JTextField	m_firstOutPath;
	JTextField	m_secondImage;
	JTextField	m_firstImage;
    
    // These were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    // These were defined in MEMIMAGE.H
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

        //{{AFX_DATA_INIT(CMorphDialog)
        //}}AFX_DATA_INIT
    }


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


    /////////////////////////////////////////////////////////////////////////////
    // CMorphDialog message handlers

    // This method came from MORPHDIALOG.CPP
    void OnLocatedestdir() {
        // TODO: Replace with JFileChooser
        CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");	//find a morph image
        if (dlg.DoModal() == IDOK) {
            m_firstImage.SetWindowText(dlg.GetPathName());
        }
    }


    // This method came from MORPHDIALOG.CPP
    void OnLocatedestdir2() {
        // TODO: Replace with JFileChooser
        CFileDialog dlg = new CFileDialog(true, "bmp", "*.bmp");	//find a morph image
        if (dlg.DoModal() == IDOK) {
            m_firstOutPath.SetWindowText(dlg.GetPathName());
        }
        
    }


    // This method came from MORPHDIALOG.CPP
    void OnLocatedestdir3() {
        // TODO: Replace with JFileChooser
        CFileDialog dlg = new CFileDialog(true, "*", "*.*");	//find a morph image
        if (dlg.DoModal() == IDOK) {
            m_secondImage.SetWindowText(dlg.GetPathName());	
        }
    }


    // This method came from MORPHDIALOG.CPP
    void On2d() {
        m_morphType = I_TWOD;
    }


    // This method came from MORPHDIALOG.CPP
    void On3d() {
        m_morphType = I_THREED;
    }


    // This method came from MORPHDIALOG.CPP
    void OnOK() {
        String msgText;
        MemImage inImageA, inImageB;
        float aFraction, increment;
        String imagePath, shapePath;
        int aStatus, i, outFrameNum;
        String outPath, aNumFrames, firstImage, secondImage;
        String directory, fileName, prefix, suffix;
        String cNumFrames, cPathName;

        aNumFrames = m_preNumFrames.getText();
        m_numFrames = Integer.parseInt(aNumFrames);

        firstImage = m_firstImage.getText();
        secondImage = m_secondImage.getText();
        outPath = m_firstOutPath.getText();

        Globals.getPathPieces(outPath, directory, fileName,
            prefix, outFrameNum, suffix);

        switch(m_morphType){
        case I_TWOD:
            inImageA = new MemImage(firstImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            if(!inImageA.isValid()) {
                Globals.statusPrint("CMorphDialog: Unable to open firstImage.");
                return;
            }
            inImageA.adjustImageBorder(firstImage);

            inImageB = new MemImage(secondImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            if(!inImageB.isValid()){
                Globals.statusPrint("CMorphDialog: Unable to open secondImage.");
                return;
            }

            inImageB.adjustImageBorder(secondImage);

            inImageB = new MemImage(firstImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            inImageA = new MemImage(secondImage, 0, 0, RANDOM, 'R', RGBCOLOR);
            aFraction = 0.0f;
            increment = 1.0f/(float)(m_numFrames - 1.0f);

            for(i = 1; i <= m_numFrames; i++){
                imagePath = directory + prefix + String.format("%04o", i) + suffix + ".bmp";
                Globals.statusPrint(imagePath);

                shapePath = directory + prefix + String.format("%04o", i) + suffix + ".shp";
                aStatus = tweenImage(aFraction, inImageA, inImageB, imagePath, shapePath);
                aFraction += increment;
            }
            break;

        case I_THREED:
            // Given two quadmeshes a and b, create a sequence of images where the
            // result mesh transitions from a to b in numFrames frames.
            float aFraction, fractionIncrement;
            int aStatus, aHeight, aWidth, bpp;

            String aTexPath, aXPath, aYPath, aZPath;
            String bTexPath, bXPath, bYPath, bZPath;
            MemImage aTexture, aX, aY, aZ;
            MemImage bTexture, bX, bY, bZ; 
            MemImage oTexture, oX, oY, oZ;
            
            aTexPath = new String(firstImage);
            Globals.constructPathName(aXPath, aTexPath, 'x');
            Globals.constructPathName(aYPath, aTexPath, 'y');
            Globals.constructPathName(aZPath, aTexPath, 'z');
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
                break;
            } // switch

            aX = new MemImage(aXPath, 0, 0, RANDOM, 'R', A32BIT);
            aY = new MemImage(aYPath, 0, 0, RANDOM, 'R', A32BIT);
            aZ = new MemImage(aZPath, 0, 0, RANDOM, 'R', A32BIT);

            bTexPath = new String(secondImage);
            Globals.constructPathName(bXPath, bTexPath, 'x');
            Globals.constructPathName(bYPath, bTexPath, 'y');
            Globals.constructPathName(bZPath, bTexPath, 'z');
        
            switch (bpp){
            case 8:
                bTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', GREENCOLOR);
                break;

            case 24:
                bTexture = new MemImage(aTexPath, 0, 0, RANDOM, 'R', RGBCOLOR);
                break;

            default:
                Globals.statusPrint("MorphDlg.onOK(): Image b must have 8 or 24 bit pixels");
                return;
                break;
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
            
            TMatrix aMatrix;
            float centroidX, centroidY, centroidZ;

            for (int i = 1; i <= m_numFrames; i++) {
                msgText = "tweenMesh. Frame " + i + "  aFraction: " + aFraction;
                Globals.statusPrint(msgText);
                aStatus = Globals.tweenMesh(aFraction, 
                    aTexture, aX, aY, aZ,
                    bTexture, bX, bY, bZ, 
                    oTexture, oX, oY, oZ);

                //  Move the mesh to the center of the view screen
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
        }

        CDialog.OnOK();
    }
} // class ImageView