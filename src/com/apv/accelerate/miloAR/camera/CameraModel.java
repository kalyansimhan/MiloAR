package com.apv.accelerate.miloAR.camera;

import com.apv.accelerate.miloAR.common.Matrix;
import com.apv.accelerate.miloAR.common.Vector;


/**
 * AR Framework
 */
public class CameraModel {
    private static final float[] tmp1 = new float[3];
    private static final float[] tmp2 = new float[3];
    
	private static Matrix transform = new Matrix();
	private static Vector lco = new Vector();

	private int width = 0; 
	private int height = 0;
	private float viewAngle = 0F;
	private float distance = 0F;
	
	public static final float DEFAULT_VIEW_ANGLE = (float) Math.toRadians(45);
	
	public CameraModel(int width, int height) {
		this(width, height, true);
	}

	public CameraModel(int width, int height, boolean init) {
		set(width, height, init);
	}
	
	public void set(int width, int height, boolean init) {
		this.width = width;
		this.height = height;

		if (init) {
			transform.set(0, 0, 0, 0, 0, 0, 0, 0, 0);
			transform.toIdentity();
			lco.set(0, 0, 0);
		}
	}
	
	public int getWidth() {
	    return width;
	}
    
    public int getHeight() {
        return height;
    }
    
	public Matrix getTransform() {
		return transform;
	}
	
	public void setTransform(Matrix transform) {
		CameraModel.transform = transform;
	}

	public Vector getLco() {
		return lco;
	}
	
	public void setLco(Vector lco) {
		CameraModel.lco = lco;
	}
	
	public void setViewAngle(float viewAngle) {
		this.viewAngle = viewAngle;
		this.distance = (this.width / 2) / (float) Math.tan(viewAngle / 2);
	}
	
	public void setViewAngle(int width, int height, float viewAngle) {
		this.viewAngle = viewAngle;
		this.distance = (width / 2) / (float) Math.tan(viewAngle / 2);
	}

	public void projectPoint(Vector orgPoint, Vector prjPoint, float addX, float addY) {
	    orgPoint.get(tmp1);
	    tmp2[0]=(distance * tmp1[0] / -tmp1[2]);
	    tmp2[1]=(distance * tmp1[1] / -tmp1[2]);
	    tmp2[2]=(tmp1[2]);
	    tmp2[0]=(tmp2[0] + addX + width / 2);
	    tmp2[1]=(-tmp2[1] + addY + height / 2);
	    prjPoint.set(tmp2);
	}

	@Override
	public String toString() {
		return "CAM(" + width + "," + height + "," + viewAngle + "," + distance + ")";
	}
}
