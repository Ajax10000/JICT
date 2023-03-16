package core;

import dialogs.ImageView;

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

    void getCurrentModelTransform(Float rx, Float ry, Float rz,
    Float sx, Float sy, Float sz, 
    Float tx, Float ty, Float tz);

    void showModels(JComboBox<String> theCombo);

    int listLength();

    int getSceneInfo(StringBuffer psbName, 
    Integer pIType, Integer pICMode, Integer pIOutRows, Integer piOutCols);

    int setSceneOutImageSize(int piOutRows, int piOutCols);

    int getViewTransform(Float pFViewX, Float pFViewY, Float pFViewZ, 
    Float pFRotateX, Float pFRotateY, Float pFRotateZ);

    int setViewTransform(float pfViewX, float pfViewY, float pfViewZ,
    float pfRotateX, float pfRotateY, float pfRotateZ);

    int writeList(String psErrorText, String psFileName);

    int preview(BufferedImage buffImg, TMatrix modelMatrix, TMatrix viewMatrix);

    int previewStill(BufferedImage buffImg, TMatrix modelMatrix, TMatrix viewMatrix);

    int render(ImageView displayWindow, TMatrix viewMatrix,
    boolean depthSortingEnabled, boolean zBufferEnabled, boolean antiAliasEnabled, 
    boolean hazeFogEnabled);

    void getFileName(String psOutputFileName, String psPrefix, 
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
    Integer numModels, boolean depthSortingEnabled);
} // interface ISceneList