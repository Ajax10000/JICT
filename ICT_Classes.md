# Class Notes

ICT is composed of the following classes:

1. bundle
2. CAboutDlg
3. CIctApp
4. CIctDoc
5. CICTMDIChildWnd
6. CIctView
7. CMainFrame
8. CMorphDialog
9. CNameDialog
10. CQuadMeshDlg
11. CScenePreviewDlg
12. CWarpParamDlg
13. faceSet
14. gPipe
15. imageDoc
16. imageView
17. imageWin
18. MakeTextureDlg
19. memImage
20. MotionBlurDialog
21. motionNode
22. motionPath
23. point2d
24. point3d
25. preference
26. renderObject
27. scene
28. sceneElement
29. sceneList
30. shape3d
31. tMatrix
32. vertexSet

Below each class is discussed in more detail.

## bundle

Defined in:
SCENELST.H (as struct)

Java version is structs/Bundle.java

## CAboutDlg

Extends CDialog

Defined in:
ICT20.CPP

Java version is dialogs/AboutDlg.java

## CIctApp

Extends CWinApp

Defined in:
ICT20.H
ICT20.CPP

Java version is apps/IctApp.java

## CIctDoc

Extends CDocument

Defined in:
ICTDOC.H
ICTDOC.CPP

Java version is docs/IctDoc.java

## CICTMDIChildWnd

Extends CMDIChildWnd

Defined in:
ICTMDI.H

There is no corresponding Java version.

## CIctView

Extends CEditView

Defined in:
ICTVIEW.H

There is no corresponding Java version.

## CMainFrame

Extends CMDIFrameWnd

Defined in:
MAINFRM.H
MAINFRM.CPP

Java version is frames/MainFrame.java

## CMorphDialog

Extends CDialog

Defined in:
MORPHDIALOG.H
MORPHDIALOG.CPP

Java version is dialogs/MorphDlg.java

## CNameDialog

Extends CDialog

Defined in:
NAMEDLG.H
NAMEDLG.CPP

Java version is dialogs/NameDlg.java

## CQuadMeshDlg

Extends CDialog

Defined in:
QUADMESHDLG.H
QUADMESHDLG.CPP

Java version is dialogs/QuadMeshDlg.java

## CScenePreviewDlg

Extends CDialog

Defined in:
SCNPREVW.H
SCENEPREVIEWDLG.CPP

Java version is dialogs/ScenePreviewDlg.java

## CWarpParamDlg

Defined in:
WARPPARAM.H
WARPPARAMDLG.CPP

Java version is dialogs/WarpParamDlg.java

## faceSet

Defined in:
SHAPE3D.H (as struct)

Java version is structs/FaceSet.java

## gPipe

Defined in:
GPIPE.H
GPIPE.CPP

Java version is globals/GPipe.java

## imageDoc

Defined in:
IMAGEDOC.H
IMAGEDOC.CPP

Java version is docs/ImageDoc.java

## imageView

Extends CScrollView

Defined in:
IMAGEVW.H
IMAGEVEW.CPP

Java version is dialogs/ImageView.java

## imageWin

Extends CMDIChildWnd

Defined in:
IMAGEWN.H

Java version is windows/ImageWindow.java

## MakeTextureDlg

Extends CDialog

Defined in:
MAKETEXTUREDLG.H
MAKETEXTUREDLG.CPP

Java version is dialogs/MakeTextureDlg.java

## memImage

Defined in:
BLEND.CPP
IWARP.CPP
MEMIMAGE.H
MEMIMG32.CPP
SHADERS.CPP

Java version is core/MemImage.java

## MotionBlurDialog

Extends CDialog

Defined in:
MOTIONBLURDIALOG.H
MOTIONBLURDIALOG.CPP

Java version is MotionBlurDlg.java

## motionNode

Defined in:
MOTION.H
MOTION.CPP

Java version is motion/MotionNode.java

## motionPath

Defined in:
MOTION.H
MOTION.CPP

Java version is motion/MotionPath.java

## point2d

Defined in:
ICT20.H (as a struct)
VECTOR.CPP

Java version is structs/Point2d.java

## point3d

Defined in:
ICT20.H (as a struct)
VECTOR.CPP

Java version is structs/Point3d.java

## preference

Defined in:
PREFRENCE.H
UTILS.CPP

No corresponding Java version, as Java provides the java.utils.prefs.Preferences class.

## renderObject

Defined in:
RENDER.H
RENDER.CPP

Java version is core/RenderObject.java

## scene

Defined in:
MODEL.CPP  
SCENELST.H

Java version is core/Scene.java

## sceneElement

Defined in:  
MODEL.CPP  
SCENELST.H

Java version is core/SceneElement.java

## sceneList

Defined in:  
DEPTHSRT.CPP  
MODEL.CPP  
SCENELST.H  
SCENELST.CPP

Java version is core/SceneList.java

## shape3d

Defined in:  
DEPTHSRT.CPP  
SHAPE3D.H  
SHAPE3D.CPP  
TWEEN.CPP

Java version is core/Shape3d.java

## tMatrix

Defined in:  
TMATRIX.H  
TMATRIX.CPP

Java version is math/TMatrix.java

## vertexSet

Defined in:  
SHAPE3D.H (as struct)

Java version is structs/VertexSet.java

# Other Java Classes

There are a few classes that were added that had no counterparts in the original C++ code. These include:

1. core/ScnFileParser.java – Contains the converted readList and checkFor3 methods from SCENELST.CPP. I placed readList in a separate class as it is such a large method. I placed checkFor3 in the same class as it is only called from readList.
2. fileUtils/BMPFileFilter.java – Used to filter what files are displayed when a user is asked to choose a .bmp file.
3. fileUtils/SCNFileFilter.java – Used to filter what files are displayed when a user is asked to choose a .scn file.
4. fileUtils/WRLFileFilter.java – Used to filter what files are displayed when a user is asked to choose a .wrl file.
5. fileUtils/FileUtils.java – Contains file-related methods.
6. globals/Globals.java – Contains most of the functions that were defined in .CPP files but were not part of a C++ class.
7. globals/JICTConstants.java – Contains many constants. The corresponding C++ constants came from several different files, and were often defined as macros (via the #define keyword).
8. globals/Sound.java – Added to provide a beeping sound.
9. globals/Texture.java – Holds most of the converted code from TEXTURE.CPP.
10. globals/VRML.java – Holds the converted code from READVRML.CPP.
11. math/MathUtils.java – Holds math-related functions.
12. math/Vect.java – Holds most of the converted code from VECTOR.CPP.
