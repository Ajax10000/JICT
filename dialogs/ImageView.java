package dialogs;

import core.MemImage;
import core.RenderObject;
import core.Shape3d;

import docs.ImageDoc;

import dtos.ColorAsBytes;
import dtos.LowHiInts;

import fileUtils.BMPFileFilter;

import frames.MainFrame;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ImageView extends JDialog implements MouseListener {
/*
class imageView : public CScrollView{
protected:
  DECLARE_DYNCREATE(imageView)
  
  HBITMAP hBitmap;
  memImage *m_anImage;
  shape3d *m_aShape;  // used during cutout image creation
  BOOL m_cutoutEnabled;
  BOOL m_eraseMe;  // if true then the onDraw erases the window prior to redrawing
  BOOL firstPress;
  BOOL loadBMP(char *);
  void getBitmap();
  CString m_theFileName;
public:
  imageView();
  imageDoc* GetDocument() {
    ASSERT (m_pDocument->IsKindOf(RUNTIME_CLASS(imageDoc)));
	return (imageDoc *) m_pDocument;
  }

  virtual ~imageView();
  virtual void OnDraw(CDC *pDC);
  virtual void OnUpdate();
  virtual void OnInitialUpdate();
  void setCaption(char *aCaption);
  void getScrollPos(int *xPixels, int *yPixels);
  void CmFileRead(char *);
  BOOL imageView::associateMemImage(memImage *outImage);
  static imageView * GetView();
  HWND getImageWindowHandle() {return m_hWnd;};
protected:
  //  Message Handlers
  //{{AFX_MSG(imageView)
	afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
    afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnRButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	//}}AFX_MSG
  DECLARE_MESSAGE_MAP()
};
*/

    private MainFrame mMainFrame;
    private BufferedImage mBuffImage;
    public HBITMAP hBitmap;

    // Modified in:
    //     associateMemImage
    public MemImage mMImage;
    public Shape3d mShape;  // used during cutout image creation

    // Modified in:
    //     onLButtonDown
    //     onInitialUpdate
    // Read in:
    //     onLButtonUp
    //     onRButtonDown
    public boolean mbCutoutEnabled;

    // if true then the onDraw erases the window prior to redrawing
    // Modified in:
    //     associateMemImage - set temporarily to true then back to false
    public boolean mbEraseMe;

    // Read in:
    //     onLButtonUp
    public boolean mbFirstPress;

    // Modified in:
    //     loadBMP
    //     onLButtonDblClk
    public String msFileName;

    private JPanel mJPanel;
/*
    protected:
    ...
    BOOL loadBMP(char *);
    void getBitmap();
    public:
    imageView();
    virtual ~imageView();
    virtual void OnDraw(CDC *pDC);
    virtual void OnUpdate();
    virtual void OnInitialUpdate();
    void setCaption(char *aCaption);
    void getScrollPos(int *xPixels, int *yPixels);
    void CmFileRead(char *);
    BOOL imageView::associateMemImage(memImage *outImage);
    static imageView * GetView();
    HWND getImageWindowHandle(){return m_hWnd;};
    protected:
      //  Message Handlers
      //{{AFX_MSG(imageView)
        afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
        afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
        afx_msg void OnRButtonUp(UINT nFlags, CPoint point);
        afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
        afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
        afx_msg void OnMouseMove(UINT nFlags, CPoint point);
*/

    // This constructor originally came from IMAGEVEW.CPP
    //
    // Called from:
    //     MainFrame.onPreviewStillScene
    //     MainFrame.onPreviewSequenceScene
    public ImageView(MainFrame pMainFrame) {
        this.mMainFrame = pMainFrame;

        mbFirstPress = false;
        mbEraseMe = false;
        mMImage = null;
    } // ImageView ctor


    // This destructor originally came from IMAGEVEW.CPP
    public void finalize() {
        // DeleteObject(hBitmap);
    } // finalize
    

    // This method originally came from IMAGEVW.H
    public ImageDoc getDocument() {
        // m_pDocument is a field inherited from CView
        // The original C++ class extended CScrollView, which itself extends CView
        // Apparently, it is cast into an object of type ImageDoc.
        //ASSERT (m_pDocument.IsKindOf(RUNTIME_CLASS(imageDoc)));
        //return (ImageDoc)m_pDocument;
        return null;
    } // getDocument


    // This method originally came from IMAGEVEW.CPP
    public void onInitialUpdate() {
        String sImage;

        ScrollView.OnInitialUpdate();
        // ASSERT(getDocument() != null);
        mMImage = getDocument().getImagePointer();
        if (mMImage != null) {
            SetScrollSizes(MM_TEXT, getDocument().getDocSize());
            getBitmap();
            msFileName = getDocument().getPathName();
            sImage = new String(msFileName);
            setCaption(sImage);
        } else {
            SetScrollSizes(MM_TEXT, CSize(1,1));
        }
    
        mbCutoutEnabled = mMainFrame.mbCutoutEnabled;
    } // onInitialUpdate


    // This method originally came from IMAGEVEW.CPP
    // 
    // Called from:
    //     MainFrame.onToolsWarpImage
    public void setCaption(String psCaption) {
         GetParent().SetWindowText(psCaption);
    } // setCaption
    

    // This method originally came from IMAGEVEW.CPP
    //
    // Called from:
    //     onLButtonUp
    public void getScrollPos(Integer pIXPixels, Integer pIYPixels) {
        Point locationPt;

        locationPt = GetDeviceScrollPosition();

        // Set the output parameters
        pIXPixels = locationPt.x;
        pIYPixels = locationPt.y;
    } // getScrollPos
    

    // This method originally came from IMAGEVEW.CPP
    //
    // Called from:
    //     associateMemImage
    public void onUpdate() {
        repaint();
    } // onUpdate
    

    // This method originally came from IMAGEVEW.CPP
    //
    // Called from:
    //     associateMemImage
    public void onDraw(CDC qdc) {
        int iStatus;
    
        if(mMainFrame.mbPreviewingScene) {
            iStatus = mMainFrame.mSceneList.previewStill(mBuffImage, 
                mMainFrame.mModelMatrix, mMainFrame.mViewMatrix);
            if(iStatus != 0) { 
                dispose();
            }
            return;
        }

        if(mMainFrame.mbPreviewingSequence) {
            iStatus = mMainFrame.mSceneList.preview(mBuffImage, 
                mMainFrame.mModelMatrix, mMainFrame.mViewMatrix);
            if(iStatus != 0) {
                dispose();
            }
            return;
        }
    
        HBITMAP holdBitmap;
        if(!hBitmap) {
            Globals.statusPrint("ImageView.onDraw  hBitmap not defined");
            return;
        }
      
        // ASSERT(getDocument() != null);
        if (!getDocument().getImagePointer()) {
            return;
        }

        Dimension docSize = getDocument().getDocSize();
        
        CRect clientRect;
        GetClientRect(clientRect);
        
        HDC memDC = CreateCompatibleDC();

        // Select the bitmap into the memory DC
        holdBitmap = SelectObject(memDC, hBitmap);
        SetStretchBltMode(memDC, COLORONCOLOR);
        RECT imageRect;
        imageRect.left = 0; imageRect.top = 0;
        imageRect.right = docSize.getWidth(); imageRect.bottom = docSize.getHeight();
    
        Point scrollPos = GetDeviceScrollPosition();
        int iXPos = scrollPos.x;
        int iYPos = scrollPos.y;
        clientRect.left   += iXPos;
        clientRect.right  += iXPos;
        clientRect.top    += iYPos;
        clientRect.bottom += iYPos;
        
        BitBlt(imageRect.left, imageRect.top,
            imageRect.right - imageRect.left,
            imageRect.bottom - imageRect.top,
            memDC, imageRect.left + iXPos,
            imageRect.top + iYPos, SRCCOPY);
    
        PatBlt(docSize.getWidth(), 0,
            clientRect.right - docSize.getWidth(),
            clientRect.bottom, PATCOPY);
        PatBlt(0, docSize.getHeight(),
            clientRect.right, 
            clientRect.bottom - docSize.getHeight(), PATCOPY);
    
        SelectObject(memDC, holdBitmap);
    } // onDraw
    

    // This method originally came from IMAGEVEW.CPP
    // 
    // Called from:
    //     MainFrame.onToolsWarpImage
    public boolean associateMemImage(MemImage pMImage) {
        mMImage = pMImage;                      //  set the view's image
        getDocument().setImagePointer(pMImage);  //  set the document's image
        getBitmap();

        CDC qdc;
        mbEraseMe = true;
        onUpdate();
        onDraw(qdc);
        mbEraseMe = false;

        return true;
    } // associateMemImage
    

    // This method originally came from IMAGEVEW.CPP
    // 
    // Called from:
    //     associateMemImage
    public void getBitmap() {
        PBITMAPINFO pbmi;
        RGBQUAD pal = new RGBQUAD[256];

        pbmi = LocalLock();
        
        for(int i = 0; i < 256; i++) {
            pal[i].rgbRed   = i; // previously cast as (unsigned char)
            pal[i].rgbGreen = i; // previously cast as (unsigned char)
            pal[i].rgbBlue  = i; // previously cast as (unsigned char)
            pal[i].rgbReserved = 0;
        }
        
        pbmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
        pbmi.bmiHeader.biWidth = mMImage.getWidth();
        pbmi.bmiHeader.biHeight = mMImage.getHeight();
        pbmi.bmiHeader.biPlanes = 1;
        pbmi.bmiHeader.biBitCount = mMImage.getBitsPerPixel();
        pbmi.bmiHeader.biCompression = BI_RGB;
        
        memcpy(pbmi.bmiColors, pal, sizeof(RGBQUAD) * 256);

        //create a bitmap data structure containing the memImage bits
        hBitmap = CreateDIBitmap(pbmi);
    } // getBitmap
    

    // This method originally came from IMAGEVEW.CPP
    // 
    // Not called from within this file
    public boolean loadBMP(String psName) {
        String sMsgText;

        mMImage = new MemImage(psName, 0, 0, 
            JICTConstants.I_RANDOM, 'R', 0);
        if(!mMImage.isValid()) {
            sMsgText = "Cannot open bitmap file: " + psName;
            Globals.statusPrint(sMsgText);
            return false;
        }

        msFileName = psName;
        getBitmap();
        return true;
    } // loadBMP
    
    
    // This method originally came from IMAGEVEW.CPP
    // 
    // Called from:
    //     MainFrame.onPreviewStillScene
    //     MainFrame.onPreviewSequenceScene
    //     MainFrame.onToolsWarpImage
    public static ImageView getView() {
        CMDIChildWnd pChild = ((AfxGetApp().m_pMainWnd)).MDIGetActive();
        if(!pChild) {
            return null;
        }
        
        CView pView = pChild.GetActiveView();
        if (!pView) {
            return null;
        }
        
        // Fail if view is the wrong kind
        if (!pView.IsKindOf(RUNTIME_CLASS(ImageView))) {
            return null;
        }
        
        return pView;
    } // getView
    

    // This method originally came from IMAGEVEW.CPP
    public void onLButtonDown(int piFlags, Point pPoint) {
        // Peek into the CMainFrame window object to see if 
        // the cutout enabled option has been checked.
        String sMsgText;

        mbCutoutEnabled = mMainFrame.mbCutoutEnabled;
        if(mbCutoutEnabled) {
            if (mbFirstPress == false) {
                mbFirstPress = true;
                mShape = new Shape3d(JICTConstants.I_MAXVERTICES); 
                Globals.statusPrint("LButtonDown event: Created shape object");
            }
        }
        
        if (!mbCutoutEnabled && mMainFrame.mbImageSamplingEnabled) {
            // Sample the color pixel and display the RGB values
            ColorAsBytes cab;
            byte bytValue;
            float fValue32;
            int iXOffset, iYOffset;
            getScrollPos(iXOffset, iYOffset);

            int iX = pPoint.x + iXOffset + 1;
            int iY = mMImage.getHeight() - pPoint.y - iYOffset;
            switch(mMImage.getBitsPerPixel()) {
            case 32:
                fValue32 = mMImage.getMPixel32(iX, iY);
                sMsgText = "x: " + iX + "  y: " + iY + 
                     "  Value: " + fValue32;
                break;
        
            case 24:
                cab = new ColorAsBytes();
                // The following method modifies parameter cab.
                mMImage.getMPixelRGB(iX, iY, cab);
                sMsgText = "x: " + iX + "  y: " + iY + 
                     "  Color: red: " + cab.bytRed + 
                     "  green: " + cab.bytGreen + 
                     "  blue: "  + cab.bytBlue;
                break;
        
            case 8:
                bytValue = mMImage.getMPixel(iX, iY);
                sMsgText = "x: " + iX + "  y: " + iY + 
                      " Value: " + bytValue;
                break;
        
            default:
                sMsgText = "The image must have 8, 24 or 32 bits per pixel";
            } // switch
            Globals.statusPrint(sMsgText);

            // If the user wishes to remove sampled colors...do it now
            if(mMainFrame.mbRemoveSampleColorsEnabled) {
                LowHiInts redLH = new LowHiInts();
                LowHiInts greenLH = new LowHiInts();
                LowHiInts blueLH = new LowHiInts();

                // The following sets parameters iRedLow, iRedHigh, iGreenLow, iGreenHigh, iBlueLow and iBlueHigh
                int iStatus = getSampleRange(mMImage, 
                    pPoint.x + iXOffset, (mMImage.getHeight() - pPoint.y - iYOffset), 
                    redLH, greenLH, blueLH);
            
                if (iStatus != -1) {
                    mMImage.clearRGBRange(
                        (byte)redLH.iLow,   (byte)redLH.iHi,
                        (byte)greenLH.iLow, (byte)greenLH.iHi, 
                        (byte)blueLH.iLow,  (byte)blueLH.iHi);
                }
            
                getBitmap();
                repaint();
            } 
        }

        // CScrollView.OnLButtonDown(nFlags, point);
    } // onLButtonDown
    

    // This method originally came from IMAGEVEW.CPP
    public void onLButtonUp(int nFlags, Point point) {
        if(mbCutoutEnabled) {
            String sMsgText;
            if(mbFirstPress == false) {
                return;
            }

            int iXOffset, iYOffset;
            getScrollPos(iXOffset, iYOffset);
            sMsgText = "Adding point " + mShape.getNumVertices() + 
                ": (" + point.x + iXOffset + ", " + (point.y - iYOffset) + ")";
            Globals.statusPrint(sMsgText);
            int iStatus;
            iStatus = mShape.addWorldVertex(
                (float)(point.x + iXOffset), 
                (float)(point.y - iYOffset), 
                0.0f); // z = 0 at the screen
            
            HPEN hpen;
            hpen = CreatePen(PS_SOLID, 1, Color.WHITE);
            SelectObject(hpen);

            float fX, fY, fZ;
            iStatus = mShape.getPreviousWorldVertex(fX, fY, fZ);
            if (iStatus == 0) {
                MoveToEx((int)(fX - iXOffset + 0.5), (int)(fY + iYOffset + 0.5), 0L);
                LineTo(point.x, point.y);
            } else {
                MoveToEx(point.x, point.y, 0L); // draw a single point
                LineTo(point.x+1, point.y+1);
            }
        } // if(mbCutoutEnabled)

        // CScrollView.OnLButtonUp(nFlags, point);
    } // onLButtonUp
    

    // This method originally came from IMAGEVEW.CPP
    public void onLButtonDblClk(int nFlags, Point point) {
        String sMsgText;
        String sCutoutName, sImageFileName;
        Dimension docSize = getDocument().getDocSize();

        if(mbCutoutEnabled) {
            Globals.statusPrint("Left button double click: Save files and Exit");
            // Remove the vertex added by the first click in the double-click
            mShape.deleteLastWorldVertex();

            NameDlg dlg = new NameDlg(null, true);
            dlg.setVisible(true);

            // May have to move this if statement to NameDlg.java
            if (dlg.DoModal() == IDOK) {
                msFileName = getDocument().getPathName();
                sImageFileName = msFileName; 
                sCutoutName = new String(dlg.m_Name);
                int iStatus = RenderObject.prepareCutout(mShape, mBuffImage, 
                    sImageFileName, sCutoutName, (int)docSize.getWidth(), (int)docSize.getHeight());
                if(iStatus != 0) {
                    sMsgText = "Unable to Create Cutout. " + iStatus;
                    Globals.statusPrint(sMsgText);
                    mbCutoutEnabled = false;
                    mbFirstPress = false;
                    return;
                }

                mbCutoutEnabled = false;
                Globals.statusPrint("Cutout Created Successfully");
                mbFirstPress = false;
            }
        }

        // CScrollView.OnLButtonDblClk(nFlags, point);
    } // onLButtonDblClk
    
    
    // This method originally came from IMAGEVEW.CPP
    public void onRButtonDown(int nFlags, Point point) {
        if(mbCutoutEnabled) {
            String sMsgText;
            Integer iXOffset = 0, iYOffset = 0;

            getScrollPos(iXOffset, iYOffset);
            sMsgText = "Deleting: (" + point.x + iXOffset + ", " + (point.y - iYOffset) + ")";
            Globals.statusPrint(sMsgText);
            HPEN hpen;
        
            hpen = CreatePen(PS_SOLID, 1, Color.BLACK);  
            SelectObject(hpen);
            int iStatus;

            Float fX = 0.0f, fY = 0.0f, fZ = 0.0f;
            Float fPx = 0.0f, fPy = 0.0f, fPz = 0.0f;
            mShape.getLastWorldVertex(fX, fY, fZ);
            mShape.getPreviousWorldVertex(fPx, fPy, fPz);

            MoveToEx((int)(fPx + 0.5f), (int)(fPy + 0.5f), 0L);
            LineTo((int)(fX + 0.5f), (int)(fY + 0.5f));
            iStatus = mShape.deleteLastWorldVertex();
            // TODO: We are not looking at the return value for an error status
        } // if(mbCutoutEnabled)
        
        if(mMainFrame.mbRemoveSampleColorsEnabled) {
            //  Ask if the user wishes to save the thresholded image which they have been creating
            // int result = MessageBox("Do you wish to save the modified image?", "Save Image As", MB_YESNO|MB_ICONQUESTION);
            int iResult = JOptionPane.showConfirmDialog(null, 
                "Do you wish to save the modified image?", "Save Image As", 
                JOptionPane.YES_NO_OPTION);

            if(iResult == JOptionPane.YES_OPTION) {
                String sFileName;

                JFileChooser dlg = new JFileChooser();
                dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
                dlg.setFileFilter(new BMPFileFilter());
                int showDlgResult = dlg.showDialog(null, "Select image name");

                if (showDlgResult == JFileChooser.APPROVE_OPTION) {
                    File file = dlg.getSelectedFile();
                    sFileName = file.getName();
                    mMImage.writeBMP(sFileName);
                }
            } // if(iResult == JOptionPane.YES_OPTION)
        } // if(mMainFrame.mbRemoveSampleColorsEnabled)

        // CScrollView.OnRButtonDown(nFlags, point);
    } // onRButtonDown


    // This method originally came from IMAGEVEW.CPP
    public void onRButtonUp(int nFlags, Point point) {
        // TODO: Add your message handler code here and/or call default
        // CScrollView.OnRButtonUp(nFlags, point);
    } // onRButtonUp


    // This method originally came from IMAGEVEW.CPP
    public void onMouseMove(int nFlags, Point point) {
        // ScrollView.OnMouseMove(nFlags, point);
    } // onMouseMove


    // This method originally came from IMAGEVEW.CPP
    // 
    // Called from:
    //    onLButtonDown
    private int getSampleRange(MemImage pMImage, int piX, int piY, 
    LowHiInts pRedLH, 
    LowHiInts pGreenLH,
    LowHiInts pBlueLH) {
        ColorAsBytes cab;
        int iStatus;

        cab = new ColorAsBytes();
        // The following method modifies parameter cab
        pMImage.getMPixelRGB(piX, piY, cab);
        if(
        (cab.bytRed == 0) && 
        (cab.bytGreen == 0) && 
        (cab.bytBlue == 0)) {
            return -1;  //a background pixel was clicked on
        }

        pRedLH.iLow = pRedLH.iHi = (int)cab.bytRed;
        pGreenLH.iLow = pGreenLH.iHi = (int)cab.bytGreen;
        pBlueLH.iLow = pBlueLH.iHi = (int)cab.bytBlue;
      
        // Next we will inspect the colors at the following 3 points:
        // (piX + 1, piY + 1), (piX, piY + 1), (piX + 1, piY)
        // and update the output parameters.

        cab = new ColorAsBytes();
        // The following method modifies parameter cab
        iStatus = pMImage.getMPixelRGB(piX + 1, piY + 1, cab);
        if(
        (cab.bytRed != 0) && 
        (cab.bytGreen != 0) && 
        (cab.bytBlue != 0)) {
            // Update the output parameters
            if(cab.bytRed < pRedLH.iLow) pRedLH.iLow = (int)cab.bytRed;
            if(cab.bytRed > pRedLH.iHi)  pRedLH.iHi  = (int)cab.bytRed;

            if(cab.bytGreen < pGreenLH.iLow) pGreenLH.iLow = (int)cab.bytGreen;
            if(cab.bytGreen > pGreenLH.iHi)  pGreenLH.iHi  = (int)cab.bytGreen;

            if(cab.bytBlue < pBlueLH.iLow) pBlueLH.iLow = (int)cab.bytBlue;
            if(cab.bytBlue > pBlueLH.iHi)  pBlueLH.iHi  = (int)cab.bytBlue;
        }
    
        cab = new ColorAsBytes();
        // The following method modifies parameter cab
        iStatus = pMImage.getMPixelRGB(piX , piY + 1, cab);
        if(
        (cab.bytRed != 0) && 
        (cab.bytGreen != 0) && 
        (cab.bytBlue != 0)) {
            // Update the output parameters
            if(cab.bytRed < pRedLH.iLow) pRedLH.iLow = (int)cab.bytRed;
            if(cab.bytRed > pRedLH.iHi)  pRedLH.iHi  = (int)cab.bytRed;

            if(cab.bytGreen < pGreenLH.iLow) pGreenLH.iLow = (int)cab.bytGreen;
            if(cab.bytGreen > pGreenLH.iHi)  pGreenLH.iHi  = (int)cab.bytGreen;

            if(cab.bytBlue < pBlueLH.iLow) pBlueLH.iLow = (int)cab.bytBlue;
            if(cab.bytBlue > pBlueLH.iHi)  pBlueLH.iHi  = (int)cab.bytBlue;
        }
    
        cab = new ColorAsBytes();
        // The following method modifies parameter cab
        iStatus = pMImage.getMPixelRGB(piX + 1, piY, cab);
        if(
        (cab.bytRed != 0) && 
        (cab.bytGreen != 0) && 
        (cab.bytBlue != 0)) {
            // Update the output parameters
            if(cab.bytRed < pRedLH.iLow) pRedLH.iLow = (int)cab.bytRed;
            if(cab.bytRed > pRedLH.iHi)  pRedLH.iHi  = (int)cab.bytRed;

            if(cab.bytGreen < pGreenLH.iLow) pGreenLH.iLow = (int)cab.bytGreen;
            if(cab.bytGreen > pGreenLH.iHi)  pGreenLH.iHi  = (int)cab.bytGreen;

            if(cab.bytBlue < pBlueLH.iLow) pBlueLH.iLow = (int)cab.bytBlue;
            if(cab.bytBlue > pBlueLH.iHi)  pBlueLH.iHi  = (int)cab.bytBlue;
        }

        return 0;
    } // getSampleRange

    
    // Setter for our mBuffImage field
    public void setBufferedImage(BufferedImage pBuffImage) {
        this.mBuffImage = pBuffImage;
    } // setBufferedImage


    // Method mouseClicked is necessary for implementing the MouseListener interface
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2 && !me.isConsumed()) {
            me.consume();
            int iFlags = me.getModifiersEx();
            Point pt = me.getLocationOnScreen();
            onLButtonDblClk(iFlags, pt);
       };
    } // mouseClicked


    // Method mouseEntered is necessary for implementing the MouseListener interface
    // Invoked when the mouse enters a component.
    public void mouseEntered(MouseEvent me) {
        return;
    } // mouseEntered
    

    // Method mouseExited is necessary for implementing the MouseListener interface
    // Invoked when the mouse exits a component.
    public void mouseExited(MouseEvent me) {
        return;
    } // mouseExited
    

    // Method mousePressed is necessary for implementing the MouseListener interface
    // Invoked when a mouse button has been pressed on a component.
    public void mousePressed(MouseEvent me) {
        // onLButtonDown
        // onRButtonDown
        Point pt = me.getLocationOnScreen();
        int iFlags = me.getModifiersEx();
        if (me.getButton() == MouseEvent.BUTTON1) {
            onLButtonDown(iFlags, pt);
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            onRButtonDown(iFlags, pt);
        }
    } // mousePressed
    

    // Method mouseReleased is necessary for implementing the MouseListener interface
    // Invoked when a mouse button has been released on a component.
    public void mouseReleased(MouseEvent me) {
        // onLButtonUp
        // onRButtonUp
        return;
    } // mouseReleased
    
} // class ImageView