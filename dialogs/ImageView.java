package dialogs;

import core.MemImage;
import core.RenderObject;
import core.Shape3d;

import docs.ImageDoc;

import fileUtils.BMPFileFilter;

import frames.MainFrame;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Color;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class ImageView extends JDialog {
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

    // Called from:
    //     MainFrame.onPreviewStillScene
    //     MainFrame.onPreviewSequenceScene
    public ImageView(MainFrame pMainFrame) {
        this.mMainFrame = pMainFrame;

        mbFirstPress = false;
        mbEraseMe = false;
        mMImage = null;
    } // ImageView ctor


    public void finalize() {
        // DeleteObject(hBitmap);
    } // finalize
    

    // This method originally came from IMAGEVW.H
    public ImageDoc getDocument() {
        ASSERT (m_pDocument.IsKindOf(RUNTIME_CLASS(imageDoc)));
        return (ImageDoc)m_pDocument;
    } // getDocument


    public void onInitialUpdate() {
        String sImage;

        ScrollView.OnInitialUpdate();
        ASSERT(getDocument() != null);
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

    
    // Called from:
    //     MainFrame.onToolsWarpImage
    public void setCaption(String psCaption) {
         GetParent().SetWindowText(psCaption);
    } // setCaption
    

    // Called from:
    //     onLButtonUp
    public void getScrollPos(Integer pIXPixels, Integer pIYPixels) {
        CPoint locationPt;

        locationPt = GetDeviceScrollPosition();

        // Set the output parameters
        pIXPixels = locationPt.x;
        pIYPixels = locationPt.y;
    } // getScrollPos
    

    // Called from:
    //     associateMemImage
    public void onUpdate() {
        repaint();
    } // onUpdate
    

    // Called from:
    //     associateMemImage
    public void onDraw(CDC qdc) {
        int iStatus;
    
        if(mMainFrame.mbPreviewingScene) {
            iStatus = mMainFrame.mSceneList.previewStill(m_hWnd, 
                mMainFrame.mModelMatrix, mMainFrame.mViewMatrix);
            if(iStatus != 0) { 
                exit;
            }
            return;
        }

        if(theFrame.previewingSequence) {
            iStatus = mMainFrame.mSceneList.preview(m_hWnd, 
                mMainFrame.mModelMatrix, mMainFrame.mViewMatrix);
            if(iStatus != 0) {
                exit;
            }
            return;
        }
    
        HBITMAP holdBitmap;
        if(!hBitmap) {
            Globals.statusPrint("ImageView.onDraw  hBitmap not defined");
            return;
        }
      
        ASSERT(getDocument() != null);
        if (!getDocument().getImagePointer()) {
            return;
        }

        CSize docSize = getDocument().getDocSize();
        
        CRect clientRect;
        GetClientRect(clientRect);
        
        HDC memDC = CreateCompatibleDC();

        // Select the bitmap into the memory DC
        holdBitmap = SelectObject(memDC, hBitmap);
        SetStretchBltMode(memDC, COLORONCOLOR);
        RECT imageRect;
        imageRect.left = 0; imageRect.top = 0;
        imageRect.right = docSize.cx; imageRect.bottom = docSize.cy;
    
        CPoint scrollPos = GetDeviceScrollPosition();
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
    
        PatBlt(docSize.cx, 0,
            clientRect.right - docSize.cx,
            clientRect.bottom, PATCOPY);
        PatBlt(0, docSize.cy,
            clientRect.right, 
            clientRect.bottom - docSize.cy, PATCOPY);
    
        SelectObject(memDC, holdBitmap);
    } // onDraw
    

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
    

    public void onLButtonDown(UINT nFlags, CPoint point) {
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
            Byte bytRed = 0, bytGreen = 0, bytBlue = 0;
            byte bytValue;
            float fValue32;
            int iXOffset, iYOffset;
            getScrollPos(iXOffset, iYOffset);

            switch(mMImage.getBitsPerPixel()) {
            case 32:
                fValue32 = mMImage.getMPixel32(point.x + iXOffset + 1, mMImage.getHeight() - point.y - iYOffset);
                sMsgText = "x: " + point.x + iXOffset + 1 + 
                         "  y: " + mMImage.getHeight() - point.y - iYOffset + 
                     "  Value: " + fValue32;
                break;
        
            case 24:
                mMImage.getMPixelRGB(point.x + iXOffset + 1, mMImage.getHeight() - point.y - iYOffset, 
                    bytRed, bytGreen, bytBlue);
                sMsgText = "x: " + point.x + iXOffset + 1 + 
                         "  y: " + mMImage.getHeight() - point.y - iYOffset + 
                     "  Color: red: " + bytRed + "  green: " + bytGreen + "  blue: "  + bytBlue;
                break;
        
            case 8:
                bytValue = mMImage.getMPixel(point.x + iXOffset + 1, 
                    mMImage.getHeight() - point.y - iYOffset);
                sMsgText = "x: " + point.x + iXOffset + 1 + 
                         "  y: " + mMImage.getHeight() - point.y - iYOffset + 
                      " Value: " + bytValue;
                break;
        
            default:
                sMsgText = "The image must have 8, 24 or 32 bits per pixel";
            } // switch
            Globals.statusPrint(sMsgText);

            // If the user wishes to remove sampled colors...do it now
            if(mMainFrame.mbRemoveSampleColorsEnabled) {
                Integer iRedLow = 0, iRedHigh = 0;
                Integer iGreenLow = 0, iGreenHigh = 0;
                Integer iBlueLow = 0, iBlueHigh = 0;

                // The following sets parameters iRedLow, iRedHigh, iGreenLow, iGreenHigh, iBlueLow and iBlueHigh
                int iStatus = getSampleRange(mMImage, 
                    point.x + iXOffset, mMImage.getHeight() - point.y - iYOffset, 
                    iRedLow,    iRedHigh, 
                    iGreenLow,  iGreenHigh,
                    iBlueLow,   iBlueHigh);
            
                if (iStatus != -1) {
                    mMImage.clearRGBRange(
                        (byte)iRedLow.intValue(),   (byte)iRedHigh.intValue(),
                        (byte)iGreenLow.intValue(), (byte)iGreenHigh.intValue(), 
                        (byte)iBlueLow.intValue(),  (byte)iBlueHigh.intValue());
                }
            
                getBitmap();
                repaint();
            } 
        }

        CScrollView.OnLButtonDown(nFlags, point);
    } // onLButtonDown
    

    public void onLButtonUp(UINT nFlags, CPoint point) {
        if(mbCutoutEnabled) {
            String sMsgText;
            if(mbFirstPress == false) {
                return;
            }

            int iXOffset, iYOffset;
            getScrollPos(iXOffset, iYOffset);
            sMsgText = "Adding point " + mShape.getNumVertices() + 
                ": (" + point.x + iXOffset + ", " + point.y - iYOffset + ")";
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

        CScrollView.OnLButtonUp(nFlags, point);
    } // onLButtonUp
    

    public void onLButtonDblClk(UINT nFlags, CPoint point) {
        String sMsgText;
        String sCutoutName, sImageFileName;
        CSize docSize = getDocument().getDocSize();

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
                int iStatus = RenderObject.prepareCutout(mShape, m_hWnd, 
                    sImageFileName, sCutoutName, (int)docSize.cx, (int)docSize.cy);
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

        CScrollView.OnLButtonDblClk(nFlags, point);
    } // onLButtonDblClk
    
    
    public void onRButtonDown(UINT nFlags, CPoint point) {
        if(mbCutoutEnabled) {
            String sMsgText;
            Integer iXOffset = 0, iYOffset = 0;

            getScrollPos(iXOffset, iYOffset);
            sMsgText = "Deleting: (" + point.x + iXOffset + ", " + point.y - iYOffset + ")";
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
        }
        
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

        CScrollView.OnRButtonDown(nFlags, point);
    } // onRButtonDown


    public void onRButtonUp(UINT nFlags, CPoint point) {
        // TODO: Add your message handler code here and/or call default
        CScrollView.OnRButtonUp(nFlags, point);
    } // onRButtonUp


    public void onMouseMove(UINT nFlags, CPoint point) {
        ScrollView.OnMouseMove(nFlags, point);
    } // onMouseMove


    int getSampleRange(MemImage pMImage, int x, int y, 
    Integer redLow, Integer redHigh, 
    Integer greenLow, Integer greenHigh,
    Integer blueLow, Integer blueHigh) {
        byte red, green, blue;
        int status;

        pMImage.getMPixelRGB(x, y, red, green, blue);
        if((red == 0) && (green == 0) && (blue == 0)) {
            return -1;  //a background pixel was clicked on
        }
        redLow = redHigh = (int)red;
        greenLow = greenHigh = (int)green;
        blueLow = blueHigh = (int)blue;
      
        status = pMImage.getMPixelRGB(x + 1, y + 1, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = (int)red;
            if(red > redHigh) redHigh = (int)red;

            if(green < greenLow)  greenLow  = (int)green;
            if(green > greenHigh) greenHigh = (int)green;

            if(blue < blueLow)  blueLow  = (int)blue;
            if(blue > blueHigh) blueHigh = (int)blue;
        }
    
        status = pMImage.getMPixelRGB(x , y + 1, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = (int)red;
            if(red > redHigh) redHigh = (int)red;

            if(green < greenLow)  greenLow  = (int)green;
            if(green > greenHigh) greenHigh = (int)green;

            if(blue < blueLow)  blueLow  = (int)blue;
            if(blue > blueHigh) blueHigh = (int)blue;
        }
    
        status = pMImage.getMPixelRGB(x + 1, y, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = (int)red;
            if(red > redHigh) redHigh = (int)red;

            if(green < greenLow)  greenLow  = (int)green;
            if(green > greenHigh) greenHigh = (int)green;

            if(blue < blueLow)  blueLow  = (int)blue;
            if(blue > blueHigh) blueHigh = (int)blue;
        }

        return 0;
    } // getSampleRange
} // class ImageView