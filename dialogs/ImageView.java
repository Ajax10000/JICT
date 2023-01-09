package dialogs;

import core.MemImage;

import core.Shape3d;

import docs.ImageDoc;

import frames.MainFrame;

import globals.Globals;

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

    public HBITMAP hBitmap;
    public MemImage m_anImage;
    public Shape3d m_aShape;  // used during cutout image creation
    public boolean m_cutoutEnabled;

    // if true then the onDraw erases the window prior to redrawing
    public boolean m_eraseMe;

    public boolean firstPress;
    public String m_theFileName;

    // These were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 0;

    // This value ws defined in SHAPE3D.H
    public static final int I_MAXVERTICES = 1024;
    
    // Called from:
    //     MainFrame.onPreviewStillScene
    //     MainFrame.onPreviewSequenceScene
    public ImageView() {
        firstPress = false;
        m_eraseMe = false;
        m_anImage = null;
    } // ImageView ctor
    
    public void finalize() {
        // DeleteObject(hBitmap);
    } // finalize
    

    public ImageDoc getDocument() {
        ASSERT (m_pDocument.IsKindOf(RUNTIME_CLASS(imageDoc)));
        return (ImageDoc)m_pDocument;
    } // GetDocument


    public void onInitialUpdate() {
        String anImage;
        ScrollView.OnInitialUpdate();
        ASSERT(getDocument() != null);
        m_anImage = getDocument().getImagePointer();
        if (m_anImage != null) {
            SetScrollSizes(MM_TEXT, getDocument().getDocSize());
            getBitmap();
            m_theFileName = getDocument().getPathName();
            anImage = new String(m_theFileName);
            setCaption(anImage);
        } else {
            SetScrollSizes(MM_TEXT, CSize(1,1));
        }
    
        MainFrame theFrame = (MainFrame)AfxGetMainWnd();
        m_cutoutEnabled = theFrame.cutoutEnabled;
    } // OnInitialUpdate

    
    // Called from:
    //     MainFrame.onToolsWarpImage
    public void setCaption(String aCaption) {
         GetParent().SetWindowText(aCaption);
    } // setCaption
    

    public void getScrollPos(Integer xPixels, Integer yPixels) {
        CPoint aLocation;

        aLocation = GetDeviceScrollPosition();

        // Set the output parameters
        xPixels = aLocation.x;
        yPixels = aLocation.y;
    } // getScrollPos
    

    public void onUpdate() {
        repaint();
    } // onUpdate
    

    public void onDraw(CDC qdc) {
        int status;
        MainFrame theFrame = (MainFrame)AfxGetMainWnd();
    
        if(theFrame.previewingScene) {
            status = theFrame.mySceneList.previewStill(m_hWnd, theFrame.modelMatrix, theFrame.viewMatrix);
            if(status != 0) { 
                exit;
            }
            return;
        }

        if(theFrame.previewingSequence) {
            status = theFrame.mySceneList.preview(m_hWnd, theFrame.modelMatrix, theFrame.viewMatrix);
            if(status != 0) {
                exit;
            }
            return;
        }
    
        HBITMAP holdBitmap;
        if(!hBitmap) {
            Globals.statusPrint("imageView::OnDraw  hBitmap not defined");
            return;
        }
      
        ASSERT(getDocument() != null);
        if (!getDocument().getImagePointer()) {
            return;
        }

        CSize docSize = getDocument().getDocSize();
        
        HDC dc = ::GetDC(m_hWnd);
        CRect clientRect;
        GetClientRect(clientRect);
        
        HDC memDC = CreateCompatibleDC(dc);

        // Select the bitmap into the memory DC
        holdBitmap =(HBITMAP)SelectObject(memDC, hBitmap);
        SetStretchBltMode(memDC, COLORONCOLOR);
        RECT imageRect;
        imageRect.left = 0; imageRect.top = 0;
        imageRect.right = docSize.cx; imageRect.bottom = docSize.cy;
    
        CPoint scrollPos = GetDeviceScrollPosition();
        int XPos = scrollPos.x;
        int YPos = scrollPos.y;
        clientRect.left   += XPos;
        clientRect.right  += XPos;
        clientRect.top    += YPos;
        clientRect.bottom += YPos;
        
        BitBlt(dc, imageRect.left, imageRect.top,
          imageRect.right - imageRect.left,
          imageRect.bottom - imageRect.top,
          memDC, imageRect.left + XPos,
          imageRect.top + YPos, SRCCOPY);
    
        PatBlt(dc, docSize.cx, 0,
               clientRect.right - docSize.cx,
               clientRect.bottom, PATCOPY);
        PatBlt(dc, 0,docSize.cy,
        clientRect.right, clientRect.bottom - docSize.cy, PATCOPY);
    
        SelectObject(memDC, holdBitmap);
        DeleteDC(memDC);
        DeleteDC(dc);
    } // onDraw
    

    // Called from:
    //     MainFrame.onToolsWarpImage
    public boolean associateMemImage(MemImage theImage) {
        m_anImage = theImage;                      //  set the view's image
        getDocument().setImagePointer(theImage);  //  set the document's image
        getBitmap();

        CDC qdc;
        m_eraseMe = true;
        OnUpdate();
        OnDraw(qdc);
        m_eraseMe = false;

        return true;
    } // associateMemImage
    

    public void getBitmap() {
        HDC dc;
        HANDLE hloc;
        PBITMAPINFO pbmi;
        RGBQUAD pal = new RGBQUAD[256];

        dc = ::GetDC(m_hWnd);
        hloc = LocalAlloc(LMEM_ZEROINIT | LMEM_MOVEABLE, sizeof(BITMAPINFOHEADER) + (sizeof(RGBQUAD) * 256));
        pbmi = (PBITMAPINFO)LocalLock(hloc);
        
        for(int a = 0; a < 256; a++) {
            pal[a].rgbRed   = (unsigned char)a;
            pal[a].rgbGreen = (unsigned char)a;
            pal[a].rgbBlue  = (unsigned char)a;
            pal[a].rgbReserved = 0;
        }
        
        pbmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
        pbmi.bmiHeader.biWidth = m_anImage.getWidth();
        pbmi.bmiHeader.biHeight = m_anImage.getHeight();
        pbmi.bmiHeader.biPlanes = 1;
        pbmi.bmiHeader.biBitCount = m_anImage.getBitsPerPixel();
        pbmi.bmiHeader.biCompression = BI_RGB;
        
        memcpy(pbmi.bmiColors, pal, sizeof(RGBQUAD) * 256);
        //create a bitmap data structure containing the memImage bits
        hBitmap = CreateDIBitmap(dc, (BITMAPINFOHEADER) pbmi, CBM_INIT,
            m_anImage.getBytes(), pbmi, DIB_RGB_COLORS);
        LocalFree(hloc);
        ::ReleaseDC(m_hWnd, dc);
    } // getBitmap
    

    public boolean loadBMP(String name) {
        String msgText;
        m_anImage = new MemImage(name, 0, 0, RANDOM, 'R', 0);
        if(!m_anImage.isValid()) {
            msgText = "Cannot open bitmap file: " + name;
            Globals.statusPrint(msgText);
            return false;
        }
        m_theFileName = name;
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
        
        //  Fail if view is the wrong kind
        if (!pView.IsKindOf(RUNTIME_CLASS(ImageView))) {
            return null;
        }
        
        return pView;
    } // getView
    

    public void onLButtonDown(UINT nFlags, CPoint point) {
        // Peek into the CMainFrame window object to see if the cutout enabled option has been checked.
        String msgText;
        MainFrame theFrame = (MainFrame)AfxGetMainWnd();
        m_cutoutEnabled = theFrame.cutoutEnabled;
        if(m_cutoutEnabled) {
            if (firstPress == false) {
                firstPress = true;
                m_aShape = new Shape3d(I_MAXVERTICES); 
                Globals.statusPrint("LButtonDown event: Created shape object");
            }
        }
        
        if (!m_cutoutEnabled && theFrame.imageSamplingEnabled) {
            // Sample the color pixel and display the RGB values
            byte red, green, blue;
            byte theValue;
            float theValue32;
            int xOffset, yOffset;
            getScrollPos(xOffset, yOffset);

            switch(m_anImage.getBitsPerPixel()) {
            case 32:
                theValue32 = m_anImage.getMPixel32(point.x + xOffset + 1, m_anImage.getHeight() - point.y - yOffset);
                msgText = "x: " + point.x + xOffset + 1 + "  y: " + m_anImage.getHeight() - point.y - yOffset + 
                    "  Value: " + theValue32;
                break;
        
            case 24:
                m_anImage.getMPixelRGB(point.x + xOffset + 1, m_anImage.getHeight() - point.y - yOffset, 
                    red, green, blue);
                msgText = "x: " + point.x + xOffset + 1 + "  y: " + m_anImage.getHeight() - point.y - yOffset + 
                    "  Color: red: " + red + "  green: " + green + "  blue: "  + blue;
                break;
        
            case 8:
                theValue = m_anImage.getMPixel(point.x + xOffset + 1, 
                    m_anImage.getHeight() - point.y - yOffset);
                msgText = "x: " + point.x + xOffset + 1 + "  y: " + m_anImage.getHeight() - point.y - yOffset + 
                    " Value: " + theValue;
                break;
        
            default:
                msgText = "The image must have 8, 24 or 32 bits per pixel";
            } // switch
            Globals.statusPrint(msgText);

            // If the user wishes to remove sampled colors...do it now
            if(theFrame.removeSampleColorsEnabled) {
                int redLow, redHigh, greenLow,  greenHigh;
                int blueLow, blueHigh;

                int status = getSampleRange(m_anImage, point.x + xOffset, m_anImage.getHeight() - point.y - yOffset, 
                    redLow,  redHigh, 
                    greenLow,  greenHigh,
                    blueLow,  blueHigh);
            
                if (status != -1) {
                    m_anImage.clearRGBRange((byte)redLow,(byte)redHigh,
                    (byte)greenLow,(byte)greenHigh, 
                    (byte)blueLow, (byte)blueHigh);
                }
            
                DeleteObject(hBitmap);
                getBitmap();
                repaint();
            } 
        }

        CScrollView.OnLButtonDown(nFlags, point);
    } // onLButtonDown
    

    public void onLButtonUp(UINT nFlags, CPoint point) {
        if(m_cutoutEnabled) {
            String msgText;
            if(firstPress == false) {
                return;
            }

            int xOffset, yOffset;
            getScrollPos(xOffset, yOffset);
            msgText = "Adding point " + m_aShape.getNumVertices() + 
                ": (" + point.x + xOffset + ", " + point.y - yOffset + ")";
            Globals.statusPrint(msgText);
            int myStatus;
            myStatus = m_aShape.addWorldVertex((float)point.x + xOffset, (float)point.y - yOffset, 0.0f); // z = 0 at the screen
            
            HPEN hpen;
            HDC theDC = ::GetDC(m_hWnd);
            hpen = CreatePen(PS_SOLID, 1, Color.WHITE);
            SelectObject(theDC, hpen);

            float x, y, z;
            myStatus = m_aShape.getPreviousWorldVertex(x, y, z);
            if (myStatus == 0) {
                MoveToEx(theDC, (int)(x - xOffset + 0.5), (int)(y + yOffset + 0.5), 0L);
                LineTo(theDC, point.x, point.y);
            } else {
                MoveToEx(theDC, point.x, point.y, 0L); //draw a single point
                LineTo(theDC, point.x+1, point.y+1);
            }
            ::ReleaseDC(m_hWnd, theDC);
            DeleteObject(hpen);
        }

        CScrollView.OnLButtonUp(nFlags, point);
    } // onLButtonUp
    

    public void onLButtonDblClk(UINT nFlags, CPoint point) {
        String msgText;
        String cutoutName, imageFileName;
        CSize docSize = getDocument().getDocSize();

        if(m_cutoutEnabled) {
            Globals.statusPrint("Left button double click: Save files and Exit");
            // Remove the vertex added by the first click in the double-click
            m_aShape.deleteLastWorldVertex();

            NameDlg dlg = new NameDlg(null, true);
            dlg.setVisible(true);

            // May have to move this if statement to NameDlg.java
            if (dlg.DoModal() == IDOK) {
                m_theFileName = getDocument().getPathName();
                imageFileName = m_theFileName; 
                cutoutName = new String(dlg.m_Name);
                int myStatus = prepareCutout(m_aShape, m_hWnd, imageFileName, cutoutName, (int)docSize.cx, (int)docSize.cy);
                if(myStatus != 0) {
                    msgText = "Unable to Create Cutout. " + myStatus;
                    Globals.statusPrint(msgText);
                    m_cutoutEnabled = false;
                    firstPress = false;
                    return;
                }

                m_cutoutEnabled = false;
                Globals.statusPrint("Cutout Created Successfully");
                firstPress = false;
            }
        }

        CScrollView.OnLButtonDblClk(nFlags, point);
    } // onLButtonDblClk
    
    
    public void onRButtonDown(UINT nFlags, CPoint point) {
        if(m_cutoutEnabled) {
            String msgText;
            int xOffset, yOffset;

            getScrollPos(xOffset, yOffset);
            msgText = "Deleting: (" + point.x + xOffset + ", " + point.y - yOffset + ")";
            Globals.statusPrint(msgText);
            HPEN hpen;
            HDC theDC = ::GetDC(m_hWnd);
        
            hpen = CreatePen(PS_SOLID, 1, Color.BLACK);  
            SelectObject(theDC, hpen);
            int theStatus;

            float x, y, z, px, py, pz;
            m_aShape.getLastWorldVertex(x, y, z);
            m_aShape.getPreviousWorldVertex(px, py, pz);

            MoveToEx(theDC, (int)(px + 0.5f), (int)(py + 0.5f), 0L);
            LineTo(theDC, (int)(x + 0.5f), (int)(y + 0.5f));
            theStatus = m_aShape.deleteLastWorldVertex();
            if (theStatus == -1) {}

            ::ReleaseDC(m_hWnd, theDC);
            DeleteObject(hpen);
        }
        MainFrame theFrame = (MainFrame)AfxGetMainWnd();
        
        if(theFrame.removeSampleColorsEnabled) {
            //  Ask if the user wishes to save the thresholded image which they have been creating
            // int result = MessageBox("Do you wish to save the modified image?", "Save Image As", MB_YESNO|MB_ICONQUESTION);
            int result = JOptionPane.showConfirmDialog(null, 
                "Do you wish to save the modified image?", "Save Image As", 
                JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.YES_OPTION) {
                String aFileName;
                // TODO: Replace with JFileChooser, with a FileFilter
                // CFileDialog dlg = new CFileDialog(false, "bmp", "*.bmp");
                JFileChooser dlg = new JFileChooser();
                dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int showDlgResult = dlg.showDialog(null, "Select image name");

                if (showDlgResult == JFileChooser.APPROVE_OPTION) {
                    File file = dlg.getSelectedFile();
                    aFileName = file.getName();
                    m_anImage.writeBMP(aFileName);
                }
            }
        }

        CScrollView.OnRButtonDown(nFlags, point);
    } // onRButtonDown


    public void onRButtonUp(UINT nFlags, CPoint point) {
        // TODO: Add your message handler code here and/or call default
        CScrollView.OnRButtonUp(nFlags, point);
    } // onRButtonUp


    public void onMouseMove(UINT nFlags, CPoint point) {
        ScrollView.OnMouseMove(nFlags, point);
    } // onMouseMove


    int getSampleRange(MemImage theImage, int x, int y, 
    Integer redLow, Integer redHigh, 
    Integer greenLow, Integer greenHigh,
    Integer blueLow, Integer blueHigh) {
        byte red, green, blue;
        int status;

        theImage.getMPixelRGB(x, y, red, green, blue);
        if(red == 0 && green == 0 && blue == 0) {
            return -1;  //a background pixel was clicked on
        }
        redLow = redHigh = red;
        greenLow = greenHigh = green;
        blueLow = blueHigh = blue;
      
        status = theImage.getMPixelRGB(x + 1, y + 1, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = red;
            if(red > redHigh) redHigh = red;

            if(green < greenLow)  greenLow  = green;
            if(green > greenHigh) greenHigh = green;

            if(blue < blueLow)  blueLow  = blue;
            if(blue > blueHigh) blueHigh = blue;
        }
    
        status = theImage.getMPixelRGB(x , y + 1, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = red;
            if(red > redHigh) redHigh = red;

            if(green < greenLow)  greenLow  = green;
            if(green > greenHigh) greenHigh = green;

            if(blue < blueLow)  blueLow  = blue;
            if(blue > blueHigh) blueHigh = blue;
        }
    
        status = theImage.getMPixelRGB(x + 1, y, red, green, blue);
        if((red != 0) && (green != 0) && (blue != 0)) {
            if(red < redLow)  redLow  = red;
            if(red > redHigh) redHigh = red;

            if(green < greenLow)  greenLow  = green;
            if(green > greenHigh) greenHigh = green;

            if(blue < blueLow)  blueLow  = blue;
            if(blue > blueHigh) blueHigh = blue;
        }

        return 0;
    } // getSampleRange
} // class ImageView