package com.apv.accelerate.miloAR.views;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;

import com.apv.accelerate.miloAR.camera.CameraModel;
import com.apv.accelerate.miloAR.common.Utilities;
import com.apv.accelerate.miloAR.common.Vector;
import com.apv.accelerate.miloAR.data.ARData;
import com.apv.accelerate.miloAR.data.PhysicalLocation;
import com.apv.accelerate.miloAR.ui.objects.PaintableBoxedText;
import com.apv.accelerate.miloAR.ui.objects.PaintableGps;
import com.apv.accelerate.miloAR.ui.objects.PaintableObject;
import com.apv.accelerate.miloAR.ui.objects.PaintablePosition;

public class Marker implements Comparable<Marker> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
        
    private static final Vector originVector = new Vector(0, 0, 0);
    private static final Vector upVector = new Vector(0, 1, 0);

    private final Vector screenPositionVector = new Vector();
    private final Vector tmpVector1 = new Vector();
    private final Vector tmpVector2 = new Vector();
    private final Vector tmpVector3 = new Vector();
    private final float[] distanceArray = new float[1];
    private final float[] symbolArray = new float[3];
    private final float[] textArray = new float[3];
    private final float[] locationArray = new float[3];
    private final float[] screenPositionArray = new float[3];

    private float initialY = 0.0f;
    
    private volatile static CameraModel cam = null;

    private volatile PaintableBoxedText textBox = null;
    private volatile PaintablePosition textContainer = null;

    protected volatile PaintableObject gpsSymbol = null;
    protected volatile PaintablePosition symbolContainer = null;
    protected String name = null;
    protected volatile PhysicalLocation physicalLocation = new PhysicalLocation();
    protected volatile double distance = 0.0;
    protected volatile boolean isOnRadar = false;
    protected volatile boolean isInView = false;
    protected final Vector symbolXyzRelativeToCameraView = new Vector();
    protected final Vector textXyzRelativeToCameraView = new Vector();
    protected final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    protected int color = Color.WHITE;

	public Marker(String name, double latitude, double longitude, double altitude, int color) {
		set(name, latitude, longitude, altitude, color);
	}
	
	public synchronized void set(String name, double latitude, double longitude, double altitude, int color) {
		if (name==null) throw new NullPointerException();

		this.name = name;
		this.physicalLocation.set(latitude,longitude,altitude);
		this.color = color;
		this.isOnRadar = false;
		this.isInView = false;
		this.symbolXyzRelativeToCameraView.set(0, 0, 0);
		this.textXyzRelativeToCameraView.set(0, 0, 0);
		this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
		this.initialY = 0.0f;
	}
	
	public synchronized String getName(){
		return this.name;
	}

    public synchronized int getColor() {
    	return this.color;
    }

    public synchronized double getDistance() {
        return this.distance;
    }

    public synchronized float getInitialY() {
        return this.initialY;
    }

    public synchronized boolean isOnRadar() {
        return this.isOnRadar;
    }

    public synchronized boolean isInView() {
        return this.isInView;
    }

    public synchronized Vector getScreenPosition() {
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);
        float x = (symbolArray[0] + textArray[0])/2;
        float y = (symbolArray[1] + textArray[1])/2;
        float z = (symbolArray[2] + textArray[2])/2;
        screenPositionVector.set(x, y, z);
        return screenPositionVector;
    }

    public synchronized Vector getLocation() {
        return this.locationXyzRelativeToPhysicalLocation;
    }

    public synchronized float getHeight() {
        if (textContainer==null) return 0f;
        return symbolContainer.getHeight()+textContainer.getHeight();
    }
    
    public synchronized float getWidth() {
        if (textContainer==null) return 0f;
        float w1 = textContainer.getWidth();
        float w2 = symbolContainer.getWidth();
        return (w1>w2)?w1:w2;
    }
    
    public synchronized void update(Canvas canvas, float addX, float addY) {
    	if (canvas==null) throw new NullPointerException();
    	
    	if (cam==null) cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
    	cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        cam.setTransform(ARData.getRotationMatrix());
        populateMatrices(originVector, cam, addX, addY);
        updateRadar();
        updateView();
    }

	private synchronized void populateMatrices(Vector original, CameraModel cam, float addX, float addY) {
		if (original==null || cam==null) throw new NullPointerException();
		
		// Temp properties
		tmpVector1.set(original);
		tmpVector3.set(upVector);
		tmpVector1.add(locationXyzRelativeToPhysicalLocation);
		tmpVector3.add(locationXyzRelativeToPhysicalLocation);
		tmpVector1.sub(cam.getLco());
		tmpVector3.sub(cam.getLco());
		tmpVector1.prod(cam.getTransform());
		tmpVector3.prod(cam.getTransform());

		tmpVector2.set(0, 0, 0);
		cam.projectPoint(tmpVector1, tmpVector2, addX, addY);
		symbolXyzRelativeToCameraView.set(tmpVector2);
		cam.projectPoint(tmpVector3, tmpVector2, addX, addY);
		textXyzRelativeToCameraView.set(tmpVector2);
	}

	private synchronized void updateRadar() {
		isOnRadar = false;

		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
		locationXyzRelativeToPhysicalLocation.get(locationArray);
        float x = locationArray[0] / scale;
        float y = locationArray[2] / scale;
        symbolXyzRelativeToCameraView.get(symbolArray);
		if ((symbolArray[2] < -1f) && ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS))) {
			isOnRadar = true;
		}
	}

    private synchronized void updateView() {
        isInView = false;

        symbolXyzRelativeToCameraView.get(symbolArray);
        float x1 = symbolArray[0] + (getWidth()/2);
        float y1 = symbolArray[1] + (getHeight()/2);
        float x2 = symbolArray[0] - (getWidth()/2);
        float y2 = symbolArray[1] - (getHeight()/2);
        if (x1>=0 && 
            x2<=cam.getWidth() &&
            y1>=0 &&
            y2<=cam.getHeight()
        ) {
            isInView = true;
        }
    }
    public synchronized void calcRelativePosition(Location location) {
		if (location==null) throw new NullPointerException();
		
	    updateDistance(location);
	    
		if (physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		PhysicalLocation.convLocationToVector(location, physicalLocation, locationXyzRelativeToPhysicalLocation);
		this.initialY = locationXyzRelativeToPhysicalLocation.getY();
		updateRadar();
    }
    
    private synchronized void updateDistance(Location location) {
        if (location==null) throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), physicalLocation.getLongitude(), location.getLatitude(), location.getLongitude(), distanceArray);
        distance = distanceArray[0];
    }

    public synchronized boolean handleClick(float x, float y) {
    	if (!isOnRadar || !isInView) return false;
    	return isPointOnMarker(x,y);
    }

    public synchronized boolean isMarkerOnMarker(Marker marker) {
        marker.getScreenPosition().get(screenPositionArray);
        float x = screenPositionArray[0];
        float y = screenPositionArray[1];
        boolean middle = isPointOnMarker(x,y);
        if (middle) return true;

        float x1 = x - (marker.getWidth()/2);
        float y1 = y - (marker.getHeight()/2);
        boolean ul = isPointOnMarker(x1,y1);
        if (ul) return true;
        
        float x2 = x + (marker.getWidth()/2);
        float y2 = y - (marker.getHeight()/2);
        boolean ur = isPointOnMarker(x2,y2);
        if (ur) return true;
        
        float x3 = x - (marker.getWidth()/2);
        float y3 = y + (marker.getHeight()/2);
        boolean ll = isPointOnMarker(x3,y3);
        if (ll) return true;
        
        float x4 = x + (marker.getWidth()/2);
        float y4 = y + (marker.getHeight()/2);
        boolean lr = isPointOnMarker(x4,y4);
        if (lr) return true;
        
        return false;
    }

	public synchronized boolean isPointOnMarker(float x, float y) {
	    if (symbolContainer==null) return false;
	    
	    symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);        
        float x1 = symbolArray[0];
        float y1 = symbolArray[1];
        float x2 = textArray[0];
        float y2 = textArray[1];
        float adjX = (x1 + x2)/2;
        float adjY = (y1 + y2)/2;
        float adjW = (getWidth()/2);
        float adjH = (getHeight()/2);
        adjY += (adjH - 25); // A bit of a fudge factor to account for the distance between text and symbol
        
        if (x>=(adjX-adjW) && x<=(adjX+adjW) && y>=(adjY-adjH) && y<=(adjY+adjH)) 
            return true;
        return false;
	}

    public synchronized void draw(Canvas canvas) {
        if (canvas==null) throw new NullPointerException();

        update(canvas,0,0);
        
        if (!isOnRadar || !isInView) return;
        
        drawIcon(canvas);
        drawText(canvas);
    }
    
    protected synchronized void drawIcon(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        if (gpsSymbol==null) gpsSymbol = new PaintableGps(36, 36, true, getColor());
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        else symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        symbolContainer.paint(canvas);
    }

    private synchronized void drawText(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();
		
	    String textStr = null;
	    if (distance<1000.0) {
	        textStr = name + " ("+ DECIMAL_FORMAT.format(distance) + "m)";          
	    } else {
	        double d=distance/1000.0;
	        textStr = name + " (" + DECIMAL_FORMAT.format(d) + "km)";
	    }

	    textXyzRelativeToCameraView.get(textArray);
	    symbolXyzRelativeToCameraView.get(symbolArray);
	    float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
	    if (textBox==null) textBox = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    else textBox.set(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    float x = textArray[0] - textBox.getWidth() / 2;
	    float y = textArray[1] + maxHeight;
	    float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
	    float angle = currentAngle + 90;
	    if (textContainer==null) textContainer = new PaintablePosition(textBox, x, y, angle, 1);
	    else textContainer.set(textBox, x, y, angle, 1);
	    textContainer.paint(canvas);
	}

    @Override
    public synchronized int compareTo(Marker another) {
        if (another==null) throw new NullPointerException();
        
        return name.compareTo(another.getName());
    }
    
    @Override
    public synchronized boolean equals(Object marker) {
        if(marker==null || name==null) throw new NullPointerException();
        
        return name.equals(((Marker)marker).getName());
    }
}
