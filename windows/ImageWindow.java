package windows;

import core.MemImage;
import core.Shape3d;

public class ImageWindow { // : virtual public CMDIChildWnd{

    public String theFileName;

    protected MemImage anImage;
    protected HBITMAP hBitmap;
    protected CBrush BkgndBrush;
    protected int PixelWidth;
    protected int PixelHeight;
    protected Shape3d aShape;  // used during cutout image creation
    protected CMDIChildWnd theClient;
    protected short firstPress;

/*
public:
  Create(LPCSTR szTitle, LONG style, const RECT& rect,
             CMDIFrameWnd *aClient);
  ~imageWindow();
  void CmFileRead(char *);
  void SetCaption(char*);
  BOOL imageWindow::associateMemImage(memImage *outImage);

protected:
  memImage *anImage;
  HBITMAP hBitmap;
  CBrush *BkgndBrush;
  int PixelWidth;
  int PixelHeight;
  shape3d *aShape;  // used during cutout image creation
  CMDIChildWnd *theClient;
  short firstPress;
  BOOL loadBMP(char*);
  void getBitmap();

  //  Message Handlers
  afx_msg void onPaint();
  afx_msg void onCreate(LPCREATESTRUCT lpCreateStruct);
  afx_msg void onSize(UINT sizeType, CSize&);
  afx_msg void onLButtonDown(UINT, CPoint&);
  afx_msg void onRButtonDown(UINT, CPoint&);
  afx_msg void onLButtonUp(UINT, CPoint&);
  afx_msg void onLButtonDblClk(UINT, CPoint&);
  afx_msg void EvSetFocus(HWND);
  afx_msg void Setup(short imHeight, short imWidth);
  afx_msg void adjustScroller();
  DECLARE_MESSAGE_MAP()
*/
} // class ImageWindow