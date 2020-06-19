/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.truegeometry.mediaprocessing;

/**
 *
 * @author PDI
 */
public class Angle3D {
    private double aboutX = 1;
    private double aboutY = 1;
    private double aboutZ = 1;

    public Angle3D(double x,double y,double z) {
        this.aboutX=x;
        this.aboutY=y;
        this.aboutZ=z;
    }
    
    public Angle3D(double[] aboutXYZ) {
        this.aboutX=aboutXYZ.length>0?aboutXYZ[0]:0;
        this.aboutY=aboutXYZ.length>1?aboutXYZ[1]:0;
        this.aboutZ=aboutXYZ.length>2?aboutXYZ[2]:0;
    }

    public double[] getAboutXYZ(){
        return new double[]{aboutX,aboutY,aboutZ};
    }
    public double[] getPointAt(double distanceWithSign){
        return null;
    
    }


    /**
     * Get the value of aboutZ
     *
     * @return the value of aboutZ
     */
    public double getAboutZ() {
        return aboutZ;
    }

    /**
     * Set the value of aboutZ
     *
     * @param z new value of aboutZ
     */
    public void setAboutZ(double z) {
        this.aboutZ = z;
    }

    /**
     * Get the value of aboutY
     *
     * @return the value of aboutY
     */
    public double getAboutY() {
        return aboutY;
    }

    /**
     * Set the value of aboutY
     *
     * @param y new value of aboutY
     */
    public void setAboutY(double y) {
        this.aboutY = y;
    }

    /**
     * Get the value of aboutX
     *
     * @return the value of aboutX
     */
    public double getAboutX() {
        return aboutX;
    }

    /**
     * Set the value of aboutX
     *
     * @param x new value of aboutX
     */
    public void setAboutX(double x) {
        this.aboutX = x;
    }
    
    
    public void addAnglesTo(Angle3D angle3D){
        aboutX=aboutX+angle3D.getAboutX();
        aboutY=aboutY+angle3D.getAboutY();
        aboutZ=aboutZ+angle3D.getAboutZ();
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("x =").append(getAboutX()).append(", y =").append(getAboutY()).append(", z =").append(getAboutZ()).toString();
    }
}
