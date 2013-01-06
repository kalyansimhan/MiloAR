package com.apv.accelerate.miloAR.data;

import android.location.Location;

import com.apv.accelerate.miloAR.common.Vector;

public class PhysicalLocation {
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;

	public PhysicalLocation() { }

	public PhysicalLocation(PhysicalLocation pl) {
		if (pl==null) throw new NullPointerException();
		
		set(pl.latitude, pl.longitude, pl.altitude);
	}

	public void set(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public static void convLocationToVector(Location org, PhysicalLocation gp, Vector v) {
		if (org==null || gp==null || v==null) return;
		
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									gp.getLatitude(), org.getLongitude(), 
									z);
		float[] x = new float[1];
		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									org.getLatitude(), gp.getLongitude(), 
									x);
		double y = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;
		v.set(x[0], (float) y, z[0]);
	}

	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude + ")";
	}
}
