This file contains information on the files that make up the original ICT application.

# Files

## BLEND.CPP

Has the following memImage methods:

1. int memImage::createAlphaImage(memImage \*outImage)
2. int memImage::unPack(memImage \*outputImage)
3. int memImage::adjustImageBorder(char \*outPath)

Has the following non-class functions:

1. int blend(memImage *inImage, memImage *maskImage, memImage \*outImage, float alphaScale)
2. int blendz(memImage *inImage, memImage *matteImage, memImage *zImage, memImage *zBuffer, memImage \*outImage, float alphaScale)
3. int createCutout(memImage *originalImage, memImage *maskImage, char *cutoutName, shape3d *aShape)
4. int in_boundary(memImage \*anImage, int x, int y)
5. int probe(memImage *anImage, int x, int y, int dir, int *new_x, int \*new_y)
6. int neighbor(memImage *anImage, int x, int y, int last_dir, int *new_x, int \*new_y)
7. int shapeFromImage(memImage *anImage, shape3d *aShape)

## DEPTHSRT.CPP

Has the following sceneList methods:

1. int sceneList::depthSort(sceneElement *models[], float distances[], int *numModels, int depthSortingEnabled)

Has the following shape3d methods:

1. void shape3d::getTCentroid(float *centroidX, float *centroidY, float \*centroidZ)

Has the following non-class functions:

1. void insertionSort2(float theItems[], DWORD itemData[], int numItems)
2. float getDistance2d(float x1, float y1, float x2, float y2)
3. float getDistance3d(float x1, float y1, float z1, float x2, float y2, float z2)

## GPIPE.CPP

Has the following gPipe methods:

1. gPipe::gPipe()
2. gPipe::~gPipe()
3. void gPipe::reset()
4. int gPipe::initialize()
5. int gPipe::addFace(point3d *p1, point3d *p2, point3d *c1, point3d *c2)
6. void gPipe::setPenScale(float scaleX, float scaleY, float scaleZ)
7. void gPipe::setPenXRotation(float angleRad)
8. void gPipe::setPenYRotation(float angleRad)
9. void gPipe::setPenZRotation(float angleRad)
10. void gPipe::setPenTranslation(float tranX, float tranY, float tranZ)
11. void gPipe::setPenMatrix()
12. void gPipe::setViewMatrix()
13. void gPipe::setViewPenMatrix()
14. void gPipe::resetPenMatrix()
15. int gPipe::saveZBuffer(char \*outputPath)
16. int gPipe::saveOutputImage(char \*outputPath)
17. void gPipe::setZBuffer(int indicator)
18. void gPipe::setLighting(int indicator)
19. void gPipe::addCube(float width, float height, float depth)
20. void gPipe::addSphere(float radius)
21. void gPipe::addCylTop(float height, float radius)
22. void gPipe::addCylBottom(float height, float radius)
23. void gPipe::addCylSides(float height, float radius)
24. void gPipe::addConeBottom(float height, float bottomRadius)
25. void gPipe::addConeSides(float height, float bottomRadius)
26. void gPipe::updateBoundingBox(point3d \*point)
27. int gPipe::viewPointInsideBoundingBox()
28. void gPipe::setLightSource(point3d \*aPoint)

## ICT20.CPP

Has the following CIctApp methods:

1. CIctApp::CIctApp()
2. BOOL CIctApp::InitInstance()
3. void CIctApp::OnAppAbout()
4. void CIctApp::OnFileOpenimage()
5. void CIctApp::OnFileOpenictlog()
6. void CIctApp::OnFileOpen()

Has the following CAboutDlg methods:

1. CAboutDlg::CAboutDlg()
2. void CAboutDlg::DoDataExchange(CDataExchange\* pDX)

## ICT20DOC.CPP

Has the following CIctDoc methods:

1. CIctDoc::CIctDoc()
2. CIctDoc::~CIctDoc()
3. BOOL CIctDoc::OnNewDocument()
4. void CIctDoc::Serialize(CArchive& ar)

## ICT20MDI.CPP

Has the following CICTMDIChildWnd methods:

1. CICTMDIChildWnd::CICTMDIChildWnd()
2. CICTMDIChildWnd::~CICTMDIChildWnd()
3. BOOL CICTMDIChildWnd::PreCreateWindow(CREATESTRUCT &cs)

## ICT20VIEW.CPP

Has the following CIctView methods:

1. CIctView::CIctView()
2. CIctView::~CIctView()
3. void CIctView::OnDraw(CDC\* pDC)

## IMAGEDOC.CPP

Has the following imageDoc methods:

1. imageDoc::imageDoc()
2. CSize imageDoc::GetDocSize()
3. memImage \*imageDoc::GetImagePointer()
4. CString imageDoc::GetPathName()
5. void imageDoc::SetImagePointer(memImage \*theImage)
6. imageDoc::~imageDoc()
7. BOOL imageDoc::OnNewDocument()
8. BOOL imageDoc::OnOpenDocument(LPCTSTR lpszPathName)
9. BOOL imageDoc::OnSaveDocument(LPCTSTR lpszPathName)
10. imageDoc \*imageDoc::GetDoc()

## IMAGEVIEW.CPP

Has the following imageView methods:

1. imageView::imageView()
2. imageView::~imageView()
3. void imageView::OnInitialUpdate()
4. void imageView::setCaption(char \*aCaption)
5. void imageView::getScrollPos(int *xPixels, int *yPixels)
6. void imageView::OnUpdate()
7. void imageView::OnDraw(CDC\* qdc)
8. BOOL imageView::associateMemImage(memImage \*theImage)
9. void imageView::getBitmap()
10. BOOL imageView::loadBMP(char\* name)
11. imageView \*imageView::GetView()
12. void imageView::OnLButtonDown(UINT nFlags, CPoint point)
13. void imageView::OnLButtonUp(UINT nFlags, CPoint point)
14. void imageView::OnLButtonDblClk(UINT nFlags, CPoint point)
15. void imageView::OnRButtonDown(UINT nFlags, CPoint point)
16. void imageView::OnRButtonUp(UINT nFlags, CPoint point)
17. void imageView::OnMouseMove(UINT nFlags, CPoint point)

Has the following non-class functions:

1. int getSampleRange(memImage *theImage, int x, int y, int *redLow, int *redHigh, int *greenLow, int *greenHigh, int *blueLow, int \*blueHigh)

## IWARP.CPP

Has the following memImage methods:

1. int memImage::alphaSmooth3()
2. int memImage::alphaSmooth5()
3. int memImage::alphaSmooth7()

Has the following non-class functions:

1. int iwarpz(memImage *inImage, memImage *outImage, memImage *zImage, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, float vx, float vy, float vz, tMatrix *viewMatrix, float refPointX, float refPointY, float refPointZ)
2. void getLineEquation(int x1, int y1, int x2, int y2, float *m, float *b, BOOL *horzFlag, BOOL *vertFlag)
3. void getFLineEquation(float x1, float y1, float x2, float y2, float *m, float *b, BOOL *horzFlag, BOOL *vertFlag)
4. int getIntervals (shape3d *theShape, int y, int *numCoords, int numAllocatedXCoords, int *screenXCoords, float *tXCoords, float *tYCoords, float *tZCoords)
5. void insertionSort(int theItems[], float itemData1[], float itemData2[], float itemData3[], int numItems)
6. void insertionSort(int theItems[], int itemData1[], float itemData2[], float itemData3[], float itemData4[], int numItems)
7. int removeDuplicates(int theList[], float theItemData1[], float theItemData2[], float theItemData3[], int \*listLength)
8. int removeDuplicates(int theList[], int theItemData1[], float theItemData2[], float theItemData3[], float theItemData4[], int \*listLength)
9. int removeSimilar(int theList[], float theItemData1[], float theItemData2[], float theItemData3[], int \*listLength, int difference)
10. int iRender(memImage *outImage, memImage *maskImage, memImage *inImage, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, tMatrix *viewMatrix, int warpIndicator, int blendIndicator, float alphaScale, float refPointX, float refPointY, float refPointZ)
11. int iRenderz(memImage *outImage, memImage *matteImage, memImage *inImage, memImage *zImage, memImage *zBuffer, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, float vx, float vy, float vz, tMatrix *viewMatrix, int warpIndicator, int blendIndicator, float alphaScale, float refPointX, float refPointY, float refPointZ)
12. int antiAlias(memImage *inImage, memImage *outImage)
13. int fWarp1(memImage *inImage, memImage *outImage, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, tMatrix \*viewMatrix, float refPointX, float refPointY, float refPointZ)
14. int fwarpz(memImage *inImage, memImage *outImage, memImage *zImage, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, float vx, float vy, float vz, tMatrix *viewMatrix, float refPointX, float refPointY, float refPointZ)
15. int fwarpz2(memImage *inputImage, memImage *outputImage, memImage *zBuffer, float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz, float vx, float vy, float vz, tMatrix *viewMatrix, float refPointX, float refPointY, float refpointZ)
16. int intervalDistance(int a, int b, int c)

## MAINFRAME.CPP

Has the following CMainFrame methods:

1. CMainFrame::CMainFrame()
2. CMainFrame::~CMainFrame()
3. int CMainFrame::OnCreate(LPCREATESTRUCT lpCreateStruct)
4. void CMainFrame::OnPreviewScene()
5. void CMainFrame::OnPreviewSequence()
6. void CMainFrame::OnRenderDepthsorting()
7. void CMainFrame::OnRenderScene()
8. void CMainFrame::OnRenderSequence()
9. void CMainFrame::OnToolsCreatealphaimage()
10. void CMainFrame::OnToolsCreateascenelist()
11. void CMainFrame::OnToolsCreatecutout()
12. void CMainFrame::OnToolsMorphSequence()
13. void CMainFrame::OnToolsWarpimage()
14. void CMainFrame::getViewMatrix(tMatrix \*viewMatrix)
15. void CMainFrame::closeAllChildren()
16. void CMainFrame::OnUpdateToolsCreatecutout(CCmdUI\* pCmdUI)
17. void CMainFrame::OnUpdatePreviewScene(CCmdUI\* pCmdUI)
18. void CMainFrame::OnUpdatePreviewSequence(CCmdUI\* pCmdUI)
19. void CMainFrame::OnUpdateRenderScene(CCmdUI\* pCmdUI)
20. void CMainFrame::OnUpdateRenderSequence(CCmdUI\* pCmdUI)
21. void CMainFrame::OnUpdateRenderDepthsorting(CCmdUI\* pCmdUI)
22. BOOL CMainFrame::OnEraseBkgnd(CDC\* pDC)
23. void CMainFrame::OnToolsTest()
24. void CMainFrame::OnRenderZbuffer()
25. void CMainFrame::OnUpdateRenderZbuffer(CCmdUI\* pCmdUI)
26. void CMainFrame::OnUpdateToolsSampleimage(CCmdUI\* pCmdUI)
27. void CMainFrame::OnToolsSampleimage()
28. void CMainFrame::OnToolsRemoveSampleColors()
29. void CMainFrame::OnUpdateToolsRemoveSampleColors(CCmdUI\* pCmdUI)
30. void CMainFrame::OnUpdateRenderHazefog(CCmdUI\* pCmdUI)
31. void CMainFrame::OnUpdateRenderAntialias(CCmdUI\* pCmdUI)
32. void CMainFrame::OnToolsCreatemesh()
33. void CMainFrame::OnToolsCreatetextureimage()
34. void CMainFrame::OnToolsMotionblur()
35. void CMainFrame::OnRenderHazefog()
36. void CMainFrame::OnRenderAntialias()
37. void CMainFrame::OnToolsRenderVrmlFile()

## MAKETEXTUREDLG.CPP

Has the following MakeTextureDlg methods:

1. MakeTextureDlg::MakeTextureDlg(CWnd* pParent /*=NULL\*/)
2. void MakeTextureDlg::DoDataExchange(CDataExchange\* pDX)
3. void MakeTextureDlg::OnOK()
4. void MakeTextureDlg::OnLocatedestdir()

## MEMIMG32.CPP

Has the following memImage methods:

1. memImage::memImage (char \*fileName, int imHeight, int imWidth, int imAccessMode, char rw, int colorSpec)
2. memImage::~memImage ()
3. memImage::memImage(int height, int width, int aBitsPerPixel)
4. memImage::memImage(memImage \*m)
5. void memImage::allocate(int height, int widthInPixels)
6. void memImage::clear()
7. int memImage::clearRGB(BYTE red, BYTE green, BYTE blue)
8. int memImage::clearRGBRange(BYTE redLow, BYTE redHigh, BYTE greenLow, BYTE greenHigh, BYTE blueLow, BYTE blueHigh)
9. void memImage::init32(float aValue)
10. int memImage::scaleTo8(memImage \*scaledImage)
11. void memImage::display(HDC dc, int outWidth, int outHeight)
12. int memImage::drawMask(HDC dc, POINT far \*thePoints, int numVertices)
13. int memImage::copy(memImage \*outImage, int xoffset, int yoffset)
14. int memImage::fastCopy(memImage \*outImage, int xOffset, int yOffset)
15. int memImage::copy8To24(memImage \*outImage)
16. BYTE memImage::getMPixel(int x, int y, char aColor)
17. int memImage::getMPixelRGB(int x, int y, BYTE *red, BYTE *green, BYTE \*blue)
18. int memImage::setMPixelRGB(int x, int y, BYTE red, BYTE green, BYTE blue)
19. int memImage::setMPixel(int x, int y, BYTE value)
20. int memImage::setMPixelA(float x, float y, BYTE value)
21. BYTE memImage::getMPixelA(float x, float y)
22. BYTE memImage::getMPixel(int x, int y)
23. int memImage::setMPixel32(int x, int y, float aValue)
24. float memImage::getMPixel32(int x, int y)
25. int memImage::getHeight()
26. int memImage::getWidth()
27. int memImage::getAccessMode()
28. int memImage::getColorSpec()
29. int memImage::getBitsPerPixel()
30. BYTE \*memImage::getBytes()
31. int memImage::isValid()
32. int memImage::writeBMP(char \*fileName)
33. int memImage::readBMP(char \*fileName, int colorSpec)
34. int memImage::readNextRow()
35. void memImage::close()
36. int memImage::writeNextRow()
37. unsigned int memImage::getImageSizeInBytes()
38. int memImage::saveAs8(char \*outImagePathName)
39. int memImage::histogram()
40. int memImage::adjustColor(BYTE desiredRed, BYTE desiredGreen, BYTE desiredBlue, BYTE *midRed, BYTE *midGreen, BYTE *midBlue, memImage *outImage, char \*adjustmentType, int inputImageColor)
41. int memImage::printValue(int x, int y)
42. void memImage::setFileName(char \*fileName)
43. int memImage::clearRectangle(int startX, int startY, int endX, int endY)
44. int memImage::getBoundingBox(int *xBeg, int *xEnd, int *yBeg, int *yEnd)

Has the following non-class functions:

1. int readBMPHeader(char *fileName, int *height, int *width, int *bitsPerPixel)
2. int makeRGBimage(char *redImage, char *greenImage, char *blueImage, char *outFileName)
3. int mapColorSpecToBitsPerPixel(int colorSpec)
4. int mapBitsPerPixelToColorSpec(int bitsPerPixel)

## MODEL.CPP

Has the following scene methods:

1. int scene::isValid()
2. scene::~scene()
3. void scene::writeFile(ofstream \*fileout)
4. scene::scene(char *sName, int seqType, int numOutCols, int numOutRows, int aColorMode, point3d *rt, point3d *tr, char *sensorpth)

Has the following sceneElement methods:

1. int sceneElement::isValid()
2. sceneElement::sceneElement(char _mName, char _ fName, int blendI, int theType, int warpI, float aScale, point3d *rt, point3d *sc, point3d *tr, char *theMotionPath, char *theAlphaPath, int compoundMMember, RGBTRIPLE anAdjustment, char *adjType, char *colorAdjPath, int definedRefPt, point3d *refPoint)
3. void sceneElement::fshowlist()
4. sceneElement::~sceneElement()
5. void sceneElement::writeFile(ofstream \*fileout)

Has the following SceneList methods:

1. int sceneList::addScene(char *theSceneName,int theType, int outImCols, int outImRows, int theColorMode, point3d *rt, point3d *tr, char *thePath)
2. int sceneList::addSceneElement(char _mdName, char _ fName, int blendI, int theType, int warpI, float aScale,point3d *rt, point3d *sc, point3d *tr, char *motionPath, char *theAlphaPath, int theSortLayer, RGBTRIPLE anAdjustment, char *adjustmentType, char *colorAdjustedPath, int definedRefPt, point3d *refPoint)
3. void sceneList::display()
4. void sceneList::clear()
5. int sceneList::setModelReferencePoint(char \*modelName, float centroidX, float centroidY, float centroidZ)
6. int sceneList::setCompoundRefPoints()
7. int sceneList::copyRefPoints()

## MORPHDIALOG.CPP

Has the following CMorphDialog methods:

1. CMorphDialog::CMorphDialog(CWnd* pParent /*=NULL\*/)
2. void CMorphDialog::DoDataExchange(CDataExchange\* pDX)
3. void CMorphDialog::OnLocatedestdir()
4. void CMorphDialog::OnLocatedestdir2()
5. void CMorphDialog::OnLocatedestdir3()
6. void CMorphDialog::On2d()
7. void CMorphDialog::On3d()
8. void CMorphDialog::OnOK()

## MOTION.CPP

Has the following motionPath methods:

1. void motionPath::allocate(int num)
2. void motionPath::getFirstLastFrame(int *firstFrame, int *lastFrame)
3. int motionPath::getNode(int frameNumber, motionNode \*mn)
4. motionPath::motionPath(void)
5. motionPath::~motionPath(void)
6. int motionPath :: readMotion(char \*pathName)

Has the following motionNode methods:

1. void motionNode::copy(motionNode mn)
2. void motionNode::clear(void)
3. int motionNode::read(char \*buffer)

Has the following non-class functions:

1. char *getNextMotionLine(char *theText, int *lineNumber, ifstream *filein)
2. int motionBlur(char *firstImagePath, char *outputDir, int numFrames, int blurDepth)

## MOTIONBLURDIALOG.CPP

Has the following MotionBlurDialog methods:

1. MotionBlurDialog::MotionBlurDialog(CWnd* pParent /*=NULL\*/)
2. void MotionBlurDialog::DoDataExchange(CDataExchange\* pDX)
3. void MotionBlurDialog::OnLocatedestdir()
4. void MotionBlurDialog::OnLocatedestdir2()
5. void MotionBlurDialog::OnOK()

## NAMEDLG.CPP

Has the following CNameDialog methods:

1. CNameDialog::CNameDialog(CWnd* pParent /*=NULL\*/)
2. void CNameDialog::DoDataExchange(CDataExchange\* pDX)
3. void CNameDialog::OnOK()

## QUADMESHDLG.CPP

Has the following CQuadMeshDlg methods:

1. CQuadMeshDlg::CQuadMeshDlg(CWnd* pParent /*=NULL\*/)
2. void CQuadMeshDlg::DoDataExchange(CDataExchange\* pDX)
3. void CQuadMeshDlg::OnOK()
4. void CQuadMeshDlg::OnLocatetexture()
5. void CQuadMeshDlg::OnLocatedestdir()

## QMESHMODEL.CPP

Has the following non-class functions:

1. int createQMeshModel(char *inputImagePath, char *destinationDir, int modelType)
2. int getMeshCentroid(memImage *xImage, memImage *yImage, memImage *zImage, float *centroidX, float *centroidY, float *centroidZ)
3. int translateMesh(memImage *xImage, memImage *yImage, memImage \*zImage, float offsetX, float offsetY, float offsetZ)

## READVRML.CPP

Has the following non-class functions:

1. int readVRML(char \*pathName)
2. int renderVRML(char *inWorldPath, char *outImagePath)

## RENDER.CPP

Has the following renderObject methods:

1. renderObject::renderObject(point3d *aUL,point3d *aUR,point3d *aLR,point3d *aLL)
2. renderObject::renderObject (char *fileName, int aModelType, int userPOR, point3d *POR )
3. renderObject::~renderObject ()
4. void renderObject::drawSequence(HDC theDC, char \*modelName, int screenHeight, int screenWidth, int frameCounter)
5. void renderObject::drawStill(HWND theWindow, char \*modelName, int screenHeight,int screenWidth)
6. int renderObject::isValid()
7. void renderObject::previewMesh(HDC theDC, char \*modelName, float xOff, float yOff, int screenHeight, int screenWidth)
8. int renderObject::renderMesh(memImage *outputImage, memImage *inputImage, int blendIndicator)
9. int renderObject::renderMeshz(memImage *outputImage, memImage *maskImage, memImage *inputImage, memImage *zBuffer, float vx, float vy, float vz)
10. void renderObject::transformAndProject (tMatrix \*aMatrix, int outHeight, int outWidth, int externalCentroid, float centroidX, float centroidY, float centroidZ)
11. int renderObject::renderShape(memImage \*outputImage, int blendIndicator)
12. int renderObject::renderShapez(memImage *outputImage, memImage *alphaImage, memImage \*zBuffer, float vx, float vy, float vz)

Has the following non-class functions:

1. void setPalette()
2. void insertionSort(int theItems[], int numItems)
3. void removeDuplicates(int theList[], int \*listLength)
4. int prepareCutout(shape3d *aShape, HWND HWindow, char *imageFileName, char \*cutoutName, int imageWidth,int imageHeight)
5. int maskFromShape(shape3d *inShape, memImage *maskImage)
6. void assembleName(char *inputName, char theSuffix, char *outputName)
7. void transformAndProjectPoint2(tMatrix *aMatrix, float x, float y, float z, int *sx, int \*sy, float refX, float refY, float refZ, int outHeight, int outWidth)
8. void drawBox(HDC theDC, HPEN hPointPen, HPEN hBlackPen, int x, int y)
9. int renderMesh(char *outputImagePath, memImage *textureImage, memImage *xImage, memImage *yImage, memImage *zImage, tMatrix *aMatrix)

## SCENELST.CPP

Has the following sceneList methods:

1. sceneList::sceneList()
2. sceneList::~sceneList()
3. sceneElement *sceneList::setCurrentModel(char *desiredModel)
4. void sceneList::setCurrentModelTransform(float rx, float ry, float rz, float sx, float sy, float sz, float tx, float ty, float tz)
5. void sceneList::getCurrentModelTransform(float *rx, float *ry, float *rz, float *sx, float *sy, float *sz, float *tx, float *ty, float \*tz)
6. int sceneList::readList(char *errorText, char *pathName)
7. void sceneList::showModels(CComboBox \*theCombo)
8. int sceneList::listLength()
9. int sceneList::getSceneInfo(char *name, int *type, int *cMode, int *outRows, int \*outCols)
10. int sceneList::setSceneOutImageSize(int outRows, int outCols)
11. int sceneList::getViewTransform(float *viewX, float *viewY, float *viewZ, float *rotateX, float *rotateY, float *rotateZ)
12. int sceneList::setViewTransform(float viewX, float viewY, float viewZ, float rotateX, float rotateY, float rotateZ)
13. int sceneList::getViewPoint(float *viewX, float *viewY, float *viewZ, float *rotateX, float *rotateY, float *rotateZ)
14. int sceneList::writeList(char *errorText, char *fileName)
15. int sceneList::preview(HWND theWindow, tMatrix *modelMatrix, tMatrix *viewMatrix)
16. int sceneList::previewStill(HWND theWindow, tMatrix *modelMatrix, tMatrix *viewMatrix)
17. int sceneList::render(imageView *displayWindow, tMatrix *viewMatrix, int depthSortingEnabled, int zBufferEnabled, int antiAliasEnabled, int hazeFogEnabled)
18. void sceneList::adjustTransforms(int effectType, sceneElement *theModel, motionNode *aMotion, bundle \*xfrm)
19. void sceneList::getViewMatrix(tMatrix *viewMatrix, int frameCounter, scene *theScene)

Has the following non-class functions:

1. void getSequenceFileName(char \*theInputPath, int frameCounter)
2. void getFileName(char *outputFileName, char *prefix, int counter, int theColor)
3. void appendFileName(char *outputFileName, char *prefix, char \*suffix)
4. void constructPathName(char *outPath, char *inPath, char lastLetter)
5. int checkFor3(char \*aString)

## SCENEPREVIEWDLG.CPP

Has the following CScenePreviewDlg methods:

1. CScenePreviewDlg::CScenePreviewDlg(CWnd* pParent /*=NULL\*/)
2. void CScenePreviewDlg::DoDataExchange(CDataExchange\* pDX)
3. void CScenePreviewDlg::OnSelchangecmbModels()
4. void CScenePreviewDlg::OnchkMoveViewPoint()
5. void CScenePreviewDlg::OncmdMinus()
6. void CScenePreviewDlg::OncmdPlus()
7. void CScenePreviewDlg::OncmdReset()
8. void CScenePreviewDlg::OnOK()
9. void CScenePreviewDlg::setTextBoxesWithModelTransform()
10. void CScenePreviewDlg::setTextBoxesWithViewTransform()
11. void CScenePreviewDlg::chooseModel()
12. BOOL CScenePreviewDlg::OnInitDialog()
13. BOOL CScenePreviewDlg::isChecked(int chkBoxID)
14. void CScenePreviewDlg::OnMove(int x, int y)

## SHADERS.CPP

Had the following class methods:

1. int memImage::fillPolyz(int I1x, int I1y, float I1p, float I1d, int I2x, int I2y, float I2p, float I2d, int I3x, int I3y, float I3p, float I3d, int I4x, int I4y, float I4p, float I4d, memImage \*zBuffer)

Had the following non-class methods

1. int fillRect(int x1, int y1, float i1, int x2, int y2, float i2, int x3, int y3, float i3, int x4, int y4, float i4, memImage \*theImage)
2. int fillTrianglez(int x1, int y1, float i1, float d1, int x2, int y2, float i2, float d2, int x3, int y3, float i3, float d3, memImage *outImage, memImage *zImage)
3. BYTE getLight(point3d *p1, point3d *p2, point3d *c1, point3d *c2)

## SHAPE3D.CPP

Has the following shape3d methods

1. shape3d::shape3d(char \*fileName, int modelType)
2. shape3d :: shape3d(char \*pathName)
3. shape3d :: ~shape3d()
4. shape3d::shape3d(int numVerts)
5. shape3d::shape3d(shape3d \*transformedShape)
6. shape3d :: shape3d(point3d *UL, point3d *UR, point3d *LR, point3d *LL)
7. int shape3d :: readShape(char \*pathName)
8. int shape3d :: getShapeFileInfo(char *pathName, int *fileType, int *numVertices, int *numFaces)
9. int shape3d :: shapeFromBMP(char \*imageFileName)
10. int shape3d::writeShape(char \*pathName)
11. void shape3d::printShape(char \*comment)
12. void shape3d::screenBoundingBox()
13. void shape3d::initCurrentVertex()
14. void shape3d::initCurrentFace()
15. int shape3d::getNumVertices()
16. int shape3d::getNumFaces()
17. void shape3d::setNumVertices(int nv)
18. void shape3d::worldBoundingBox()
19. void shape3d::transformBoundingBox()
20. void shape3d::invertY(int screenHeight)
21. int shape3d::addWorldVertex(float x, float y, float z)
22. int shape3d::addTransformedVertex(float x, float y, float z)
23. int shape3d::deleteLastWorldVertex()
24. int shape3d::getLastWorldVertex(float *x, float *y, float \*z)
25. int shape3d::getPreviousWorldVertex(float *x, float *y, float \*z)
26. float shape3d::averageX()
27. float shape3d::averageY()
28. void shape3d::getWCentroid(float *centroidX, float *centroidY, float \*centroidZ)
29. void shape3d::translateW(float offsetX, float offsetY, float offsetZ)
30. void shape3d::floor()
31. void shape3d::translateT(float offsetX, float offsetY, float offsetZ)
32. void shape3d::translateS(int offsetX, int offsetY)
33. int shape3d::isValid()
34. int shape3d::addVertices(shape3d \*child)
35. void shape3d::getReferencePoint(float *centroidX, float *centroidY, float \*centroidZ)
36. void shape3d::setReferencePoint(float centroidX, float centroidY, float centroidZ)
37. int shape3d::getScreenVertex(int index, int *sx, int *sy)
38. int shape3d::getTransformedVertex(int index, float *tx, float *ty, float \*tz)
39. float shape3d::getWorldDistance(int vertexNumber)
40. int shape3d::getWorldVertex(float distanceFraction, int *vertex, float *x, float *y, float *z)
41. int shape3d::removeDuplicates()

Has the following non-class functions:

1. char *getNextLine(char *theText, int *lineNumber, ifstream *filein, int minLineLength)
2. void getShapePath(char *modelPath, char *shapeDir, char \*shapePath)
3. int getBoundaryPoint (shape3d *theShape, float rayCentroidX, float rayCentroidY, float rayX2, float rayY2, float *outX, float \*outY, float lastX, float lastY)

## TEXTURE.CPP

Has the following non-class functions:

1. int createTexture(char *texturePath, char *outDirectory, int textureType, int imageType, int foreColor, int backColor, int numRows, int numColumns)
2. void plasma(memImage \*anImage, int x, int y, int x1, int y1)
3. void createPlasma(memImage \*anImage, int numRows, int numColumns)
4. int myRand(int maxVal)

## TMATRIX.CPP

Has the following tMatrix methods:

1. tMatrix::tMatrix()
2. tMatrix::tMatrix(tMatrix \*aMatrix)
3. void tMatrix::multiply(tMatrix *matrix1, tMatrix *matrix2)
4. void tMatrix::copy(tMatrix \*matrix)
5. void tMatrix::setIdentity()
6. tMatrix::~tMatrix()
7. void tMatrix::scale(float sx, float sy, float sz)
8. void tMatrix::translate(float tx, float ty, float tz)
9. void tMatrix::rotate(float rx,float ry,float rz)
10. void tMatrix::transpose()
11. void tMatrix::transformPoint(float xIn, float yIn, float zIn, float *xOut, float *yOut, float \*zOut)
12. void tMatrix::transformPoint1(point3d *in, point3d *out)
13. void tMatrix::display(char \*heading)
14. int tMatrix::invertg()
15. void tMatrix::transformAndProjectPoint(float x, float y, float z, int *sx, int *sy, float refX, float refY, float refZ, int outHeight, int outWidth, float *tx, float *ty, float \*tz)
16. void tMatrix::transformAndProjectPoint1(point3d *p, point2d *s, point3d *ref, int outHeight, int outWidth, point3d *t)
17. void tMatrix::transformAndProject (shape3d \*aShape, int outHeight, int outWidth, int useExternalCentroid, float centroidX, float centroidY, float centroidZ)

Has the following non-class functions:

1. void matmult(float result[4][4], float mat1[4][4], float mat2[4][4])
2. void matcopy(float dest[4][4], float source[4][4])

## TWEEN.CPP

Has the following shape3d methods:

1. int shape3d::insertVertexAfter(int index, float x, float y, float z)
2. shape3d \*shape3d::copyAndExpand(int numAddedVertices)
3. int shape3d::divideLongestArc()

Has the following non-class functions:

1. int createTweenableShapes(shape3d *inShape1, shape3d *inShape2, shape3d **outShapeA, shape3d **outShapeB)
2. int tweenShape(float fraction, shape3d \**outShape, shape3d *shape1, shape3d \*shape2)
3. int tweenImage(float aFraction, memImage *inImageA, memImage *inImageB, char *imagePath, char *shapePath)
4. int tweenMesh(float aFraction, memImage *aTexture, memImage *aX, memImage _aY, memImage _ aZ, memImage *bTexture, memImage *bX, memImage _bY, memImage _ bZ, memImage *oTexture, memImage *oX, memImage _oY, memImage _ oZ)
5. int getRowIntervals(memImage *anImage, int row, int *intervalList, int \*numIntervals)
6. int getTotalIntervalLength(int \*intervalList, int numIntervals)
7. int indexToCoord(int index, int \*intervalList, int numIntervals)

## UTILS.CPP

Has the following preference methods:

1. preference::preference()
2. char \*preference::getPath(int pathIndicator)
3. void preference::setPath(int pathIndicator, char \*thePath)
4. preference::~preference()

Has the following non-class functions:

1. void statusPrint(char \*aMessage)
2. int fileExists(char \*pathName)
3. float interpolate(float desired1, float desired2, float reference1, float reference2, float referenceCurrent)
4. float fPolar(float angle)
5. float polarAtan(float run, float rise)
6. float bound(float value, float minValue, float maxValue)
7. void makePath(char *currentPath, char *inPath, char *prefix, int frameCounter, char *inSuffix)
8. int getPathPieces(char *firstImagePath, char *directory, char *fileName, char *prefix, int *frameNum, char *inSuffix)

## VECTOR.CPP

Has the following point2d method:

1. void point2d::display(char \*message)

Has the following point3d method:

1. void point3d::display(char \*message)

Has the following non-class functions:

1. void vectorAdd(point3d *result, point3d *p1, point3d \*p2)
2. void vectorSubtract(point3d *result, point3d *p1, point3d \*p2)
3. float vectorMagnitude2(point3d *p1, point3d *p2)
4. float vectorMagnitude1(point3d \*p1)
5. void vectorNormalize(point3d \*p1)
6. void crossProduct(point3d *result, point3d *p1, point3d *p2, point3d *p3)
7. float dotProduct(point3d *p1, point3d *p2)
8. void getNormal1(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float *xN, float *yN, float \*zN)
9. void getNormal2(point3d *result, point3d *p0, point3d *p1, point3d *p2)
10. float lightModel(float kd, int Ip, int Ia, point3d *N, point3d *L, float d)

## WARPPARAMDLG.CPP

Has the following CWarpParamDlg methods:

1. CWarpParamDlg::CWarpParamDlg(CWnd* pParent /*=NULL\*/)
2. void CWarpParamDlg::DoDataExchange(CDataExchange\* pDX)
3. void CWarpParamDlg::OnOK()
