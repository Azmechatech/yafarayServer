/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.truegeometry.mediaprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author PDI
 */
public class Point3D {
    private double x = 1;
    private double y = 1;
    private double z = 1;

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public int getDECIMAL_CONTROLLER() {
        return DECIMAL_CONTROLLER;
    }

    public void setDECIMAL_CONTROLLER(int DECIMAL_CONTROLLER) {
        this.DECIMAL_CONTROLLER = DECIMAL_CONTROLLER;
    }

    public boolean isIsUdefined() {
        return isUdefined;
    }

    public void setIsUdefined(boolean isUdefined) {
        this.isUdefined = isUdefined;
    }
    private double a = 1;
    private double b = 1;
    private double g = 1;
    private long id = 0;
    private boolean record_history = true;
    int DECIMAL_CONTROLLER=100;
    

    public Point3D(double x,double y,double z) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.isUdefined=false;
    }
    
    public Point3D(double xyz[]) {
        this.x=xyz.length>0?xyz[0]:0;
        this.y=xyz.length>1?xyz[1]:0;
        this.z=xyz.length>2?xyz[2]:0;
        this.isUdefined=false;
    }
    
    public Point3D(double xyz[], long id) {
        this.x=xyz.length>0?xyz[0]:0;
        this.y=xyz.length>1?xyz[1]:0;
        this.z=xyz.length>2?xyz[2]:0;
        this.id=id;
        this.isUdefined=false;
    }

    public double[] getXYZ(){
        return new double[]{x,y,z};
    }
    
    public double[] getXYZ(int decimalControl){
        // double roundOff = (double) Math.round(a * 100) / 100;
        return new double[]{(double) Math.round(x * decimalControl) / decimalControl,(double) Math.round(y * decimalControl) / decimalControl,(double) Math.round(z * decimalControl) / decimalControl};
    }
    public double[] getPointAt(double distanceWithSign){
        return null;
    
    }
    
    
    public Point3D subtract(Point3D toSubtract){
        return new Point3D(x-toSubtract.getX(), y-toSubtract.getY(), z-toSubtract.getZ());
    }
    
    
        
    private Set<Point3D> history_set=new LinkedHashSet();

    /**
     * Get the value of history_set
     *
     * @return the value of history_set
     */
    public Set<Point3D> getHistory_set() {
        return history_set;
    }

    /**
     * Get the value of record_history
     *
     * @return the value of record_history
     */
    public boolean isRecord_history() {
        return record_history;
    }

    /**
     * Set the value of record_history
     *
     * @param record_history new value of record_history
     */
    public void setRecord_history(boolean record_history) {
        this.record_history = record_history;
    }

    
    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the value of z
     *
     * @return the value of z
     */
    public double getZ() {
        return z;
    }

    /**
     * Set the value of z
     *
     * @param z new value of z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Get the value of y
     *
     * @return the value of y
     */
    public double getY() {
        return y;
    }

    /**
     * Set the value of y
     *
     * @param y new value of y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the value of x
     *
     * @return the value of x
     */
    public double getX() {
        return x;
    }

    /**
     * Set the value of x
     *
     * @param x new value of x
     */
    public void setX(double x) {
        this.x = x;
    }
    
    boolean isUdefined=true;
    public boolean isUndefined(){
        return isUdefined;
    }
    
    
    public Point3D translateTo(Point3D point3D){
        if(record_history) history_set.add(new Point3D(this.getXYZ(),this.id));
        x=x+point3D.getX();
        y=y+point3D.getY();
        z=z+point3D.getZ();
        
       // System.out.println("#translateTo "+x);
        
        return this;
    }
    
     public Point3D scaleTo(double scale){
        if(record_history) history_set.add(new Point3D(this.getXYZ(),this.id));
        x=x*scale;
        y=y*scale;
        z=z*scale;
        
       // System.out.println("#translateTo "+x);
        
        return this;
    }
    
    
    public Point3D moveTo(Point3D point3D){
        if(record_history) history_set.add(new Point3D(this.getXYZ(),this.id));
        x=point3D.getX();
        y=point3D.getY();
        z=point3D.getZ();
 
        return this;
    }
    
    
    /**
     * Let xc, yc be the coordinates of the center of the rectangle.
     *
     * Translate your points such that the center is the new origin:
     *
     * xt = x1 - xc; yt = y1 - yc;
     *
     * Rotate around the origin by the angle a:
     *
     * c = cos(a); // compute trig. functions only once
     * s = sin(a);
     * xr = xt * c - yt * s;
     * yr = xt * s + yt * c;
     *
     * Translate points back:
     *
     * x2 = xr + xc; 
     * y2 = yr + yc;
     *
     * If you do this for all 4 corners of the rectangle and then draw lines
     * between the transformed corners, you get the rotated rectangle.
     *
     * @param angle3D
     * @param point3D
     */
    public void rotateAboutZAxisBy(Angle3D angle3D,Point3D point3D){
        if(record_history) history_set.add(new Point3D(this.getXYZ(),this.id));
        //++++++New Implementation +++++
        Matrix3D baseMatrix=new Matrix3D();
        Matrix3D translationMatrix=new Matrix3D();
        Matrix3D rotationMatrix=new Matrix3D();
        Matrix3D inverseTranslationMatrix=new Matrix3D();
        
        //Set values
        baseMatrix.set(0, 0, getX());
        baseMatrix.set(0, 1, getY());
        baseMatrix.set(0, 2, getZ());
        baseMatrix.set(0, 3, 1);
        baseMatrix.set(1, 3, 1);
        baseMatrix.set(2, 3, 1);
        baseMatrix.set(3, 3, 1);
        
        translationMatrix.set(0, 0, 1);translationMatrix.set(1, 1, 1);translationMatrix.set(2, 2, 1);translationMatrix.set(3, 3, 1);
        translationMatrix.set(3, 0, -point3D.getX());translationMatrix.set(3, 1, -point3D.getY());translationMatrix.set(3, 2, -point3D.getZ());
        inverseTranslationMatrix.set(0, 0, 1);inverseTranslationMatrix.set(1, 1, 1);inverseTranslationMatrix.set(2, 2, 1);inverseTranslationMatrix.set(3, 3, 1);
        inverseTranslationMatrix.set(3, 0, point3D.getX());inverseTranslationMatrix.set(3, 1, point3D.getY());inverseTranslationMatrix.set(3, 2, point3D.getZ());
        
        rotationMatrix.set(0, 0, Math.cos(angle3D.getAboutZ()));
        rotationMatrix.set(0, 1, Math.sin(angle3D.getAboutZ()));
        rotationMatrix.set(1, 0, -Math.sin(angle3D.getAboutZ()));
        rotationMatrix.set(1, 1, Math.cos(angle3D.getAboutZ()));
        rotationMatrix.set(2, 2, 1);
        rotationMatrix.set(3, 3, 1);
        
        //Formula [X*]=[X][T][R][T^-1]
        translationMatrix.postMultiply(rotationMatrix);
        translationMatrix.postMultiply(inverseTranslationMatrix);
        baseMatrix.postMultiply(translationMatrix);
        
        //Set new values
        setX(baseMatrix.get(0, 0));
        setY(baseMatrix.get(0, 1));
        
        //----Old implementation----
//        double xt = getX() - point3D.getX(); 
//        double yt = getY() - point3D.getY();
//        double c=Math.cos(angle3D.getAboutZ());
//        double s=Math.sin(angle3D.getAboutZ());
//        
//        double xr = xt * c - yt * s;
//        double yr = xt * s + yt * c;
//        
//        setX(xr + point3D.getX());
//        setY(yr + point3D.getY());
        
        
    }
    
    public Point3D getXminPoint() {
        Point3D result = null;
        for (Point3D point : history_set) {
            if (result == null) {//First time case handling
                result = point;
                continue;
            }
            result = result.getX() < point.getX() ? result : point;
        }
        return result;
    }
    
    public Point3D getXmaxPoint() {
        Point3D result = null;
        for (Point3D point : history_set) {
            if (result == null) {//First time case handling
                result = point;
                continue;
            }
            result = result.getX() > point.getX() ? result : point;
        }
        return result;
    }
    
    public static Point3D getMaxPoint(ArrayList<Point3D> allPoints) {
        Point3D result = new Point3D(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        for (Point3D point : allPoints) {
            if (result == null) {//First time case handling
                result = point;
                continue;
            }
            result.setX( result.getX() > point.getX() ? result.getX() : point.getX());
            result.setY( result.getY() > point.getY() ? result.getY() : point.getY());
            result.setZ( result.getZ() > point.getZ() ? result.getZ() : point.getZ());
        }
        return result;
    }
    
    public static double[][] getArray(Set<Point3D> PointSet){
            double[][] result=new double[PointSet.size()][3];
            int i=0;
            for(Point3D point : PointSet){
             result[i]=point.getXYZ();
             i++;
            }
        return result;
    }

     public static ArrayList<Point3D> getArrayList(double[][] PointSet){
            ArrayList<Point3D> result = new ArrayList<>();
        for (double[] point : PointSet) {
            Point3D p3d = new Point3D(point);
            result.add(p3d);

        }
        
        return result;
    }
     
    public static ArrayList<Point3D> getScaledArrayList(ArrayList<Point3D> PointSet, double scale) {
        ArrayList<Point3D> result = new ArrayList<>();
        for (Point3D point : PointSet) {
            Point3D p3d = point.scaleTo(scale);
            result.add(p3d);

        }

        return result;
    }
    
    public static ArrayList<Point3D> getTranslatedArrayList(ArrayList<Point3D> PointSet, Point3D translateTo) {
        ArrayList<Point3D> result = new ArrayList<>();
        for (Point3D point : PointSet) {
            Point3D p3d = point.translateTo(translateTo);
            result.add(p3d);

        }

        return result;
    }
        /**
         * Only works for z-axis rotation. Need to work for others.
         * @param PointSet
         * @param angle3D
         * @param centerOfPoint
         * @return 
         */
    public static ArrayList<Point3D> getRotatedArrayList(ArrayList<Point3D> PointSet,Angle3D angle3D, Point3D centerOfPoint) {
        ArrayList<Point3D> result = new ArrayList<>();

        for (Point3D point : PointSet) {
            point.rotateAboutZAxisBy(angle3D, centerOfPoint);
            Point3D p3d = point;
            result.add(p3d);

        }

        return result;
    }
      /**
       * 
       * @param PointSet
       * @param angle3D
       * @param centerOfPoint
       * @return 
       */
    public static ArrayList<Point3D> getTransRotArrayList(ArrayList<Point3D> PointSet,Angle3D angle3D, Point3D centerOfPoint) {
        ArrayList<Point3D> result = new ArrayList<>();
        /*
            1. Translate to zero
            2. Rotate,
            3. Translate back
            */
        
        for (Point3D point : PointSet) {
            point.rotateAboutZAxisBy(angle3D, centerOfPoint);
            Point3D p3d = point;
            result.add(p3d);

        }

        return result;
    }
    
    /**
     * Returns map of x,y,z with values.
     * @return 
     */
    public HashMap<String,Double> toMap(){
         HashMap<String,Double> result=new HashMap<>();
         result.put("x", getX());
         result.put("y", getY());
         result.put("z", getZ());
         return result;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("x =").append(getX()).append(", y =").append(getY()).append(", z =").append(getZ()).toString();
    }
    
    public String toString(String seprator) {
        return new StringBuilder().append(getX()).append(seprator).append(getY()).append(seprator).append(getZ()).toString();
    }
    
}
