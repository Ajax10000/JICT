package core;

import dialogs.ImageView;

import dtos.OneInt;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;

import math.TMatrix;

import structs.Point3d;


public interface ISceneList {
    SceneElement setCurrentModel(String desiredModel);

    void setCurrentModelTransform(float rx, float ry, float rz,
    float sx, float sy, float sz, 
    float tx, float ty, float tz);

    void getCurrentModelTransform(Point3d pRot,
    Point3d pScale, 
    Point3d pTran);

    void showModels(JComboBox<String> theCombo);

    int listLength();

    int getSceneInfo(StringBuffer psbName, 
    Integer pIType, Integer pICMode, Integer pIOutRows, Integer piOutCols);

    int setSceneOutImageSize(int piOutRows, int piOutCols);

    int getViewTransform(Point3d pView, Point3d pRot);

    int setViewTransform(float pfViewX, float pfViewY, float pfViewZ,
    float pfRotateX, float pfRotateY, float pfRotateZ);

    int writeList(StringBuffer psbErrorText, String psFileName);

    int preview(BufferedImage buffImg, TMatrix modelMatrix, TMatrix viewMatrix);

    int previewStill(BufferedImage buffImg, TMatrix modelMatrix, TMatrix viewMatrix);

    int render(ImageView displayWindow, TMatrix viewMatrix,
    boolean depthSortingEnabled, boolean zBufferEnabled, boolean antiAliasEnabled, 
    boolean hazeFogEnabled);

    void getFileName(StringBuffer psbOutputFileName, String psPrefix, 
    int piCounter, int piTheColor);

    int addScene(String theSceneName, int theType, 
    int outImCols, int outImRows, int theColorMode, 
    Point3d rt, Point3d tr, String thePath);

    int addSceneElement(String mdName, String fName, boolean blendI,
    int theType, boolean warpI, float aScale, 
    Point3d rt, Point3d sc, Point3d tr, 
    String motionPath, String theAlphaPath,
    boolean theSortLayer, Color anAdjustment, 
    String adjustmentType, String colorAdjustedPath,
    boolean definedRefPt, Point3d refPoint);

    void display();

    void clear();

    int setModelReferencePoint(String psModelName, 
    float pfCentroidX, float pfCentroidY, float pfCentroidZ);

    int setCompoundRefPoints();

    int depthSort(SceneElement[] models, float[] distances,
    OneInt numModels, boolean depthSortingEnabled);
} // interface ISceneList