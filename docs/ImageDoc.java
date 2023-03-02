package docs;

import core.MemImage;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Dimension;

// Class ImageView has method getDocument, which returns an object of type ImageDoc
public class ImageDoc { // : public CDocument {
	private boolean bIctDebug = false;
	protected Dimension mDocDim;
    protected MemImage mMImage;
    protected String msPathName;

/*
protected: // create from serialization only
	DECLARE_DYNCREATE(imageDoc)

// Attributes
public:
	imageDoc(); - implemented
	virtual ~imageDoc(); - implemented as method finalize
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName); - implemented
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName); - implemented
    CSize GetDocSize(); - implemented
    memImage *GetImagePointer(); - implemented
    CString GetPathName(); - implemented
    void SetImagePointer(memImage *theImage); - implemented
    static imageDoc * GetDoc(); - implemented
   

// Implementation
protected:
	CSize m_sizeDoc;
    memImage *m_theImage;
    CString m_pathName;

#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
protected:
	virtual BOOL    OnNewDocument(); - implemented

// Generated message map functions
protected:
	//{{AFX_MSG(imageDoc)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
}
*/

	// This constructor originally came from IMAGEDOC.CPP
	public ImageDoc() {
		mDocDim = new Dimension(1, 1);     // dummy value to make CScrollView happy
		if(bIctDebug) {
			Globals.statusPrint("ImageDoc Constructor");
		}
	} // ImageDoc ctor


	// This method originally came from IMAGEDOC.CPP
	// Not called from within this file.
	public Dimension getDocSize() {   
		int iImHeight = mMImage.getHeight();
		int iImWidth  = mMImage.getWidth();
		mDocDim = new Dimension(iImWidth, iImHeight);
		return mDocDim;     
	} // getDocSize

	
	// This method originally came from IMAGEDOC.CPP
	public MemImage getImagePointer() {
		return mMImage;
	} // getImagePointer


	// This method originally came from IMAGEDOC.CPP
	public String getPathName() {
		return msPathName;
	} // getPathName


	// This method originally came from IMAGEDOC.CPP
	public void setImagePointer(MemImage pMImage) {
		mMImage = pMImage;
		return ;
	} // setImagePointer


	// This destructor originally came from IMAGEDOC.CPP
	public void finalize() {
		if(bIctDebug) {
			Globals.statusPrint("ImageDoc Destructor");
		}
	} // finalize


	// This method originally came from IMAGEDOC.CPP
	protected boolean onNewDocument() {
		// The original C++ imageDoc class extended CDocument. 
		// Here it is calling the parent's OnNewDocument() method.
		if (!CDocument.OnNewDocument()) {
			return false;
		}
		mMImage = null;
		return true;
	} // onNewDocument


	// This method originally came from IMAGEDOC.CPP
	public boolean onOpenDocument(String psPathName) {
		mMImage = new MemImage(psPathName, 0, 0, 
			JICTConstants.I_RANDOM, 'R', 0);
		if(!mMImage.isValid()) {
			String sMsgText = "OnOpenDocument: Cannot open image: " + psPathName;
			Globals.statusPrint(sMsgText);
			return false;
		}

		String sMsgText = "Opened: " + psPathName + "  " + mMImage.getHeight() + " rows  " + mMImage.getWidth() + " cols.";
		Globals.statusPrint(sMsgText);
		msPathName = new String(psPathName);
		return true;
	} // onOpenDocument


	// This method originally came from IMAGEDOC.CPP
	public boolean onSaveDocument(String psPathName) {
		mMImage.writeBMP(psPathName);
		return true;
	} // onSaveDocument


	// This method originally came from IMAGEDOC.CPP
	public ImageDoc getDoc() {
		CMDIChildWnd childWnd = ((CMDIFrameWnd)(AfxGetApp().m_pMainWnd)).MDIGetActive();

		if (!childWnd) {
			return null;
		}
		
		CDocument doc = childWnd.GetActiveDocument();

		if (!doc) {
			return null;
		}

		// Fail if doc is the wrong kind
		if(!doc.IsKindOf(RUNTIME_CLASS(ImageDoc))) {
			return null;
		}

		return (ImageDoc)doc;
	} // getDoc
} // class ImageDoc