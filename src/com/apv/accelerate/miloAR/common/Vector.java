/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file was an original part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.apv.accelerate.miloAR.common;



/**
 * AR Framework
 */
public class Vector {
    private final float[] matrixArray = new float[9];
    
	private volatile float x = 0f;
    private volatile float y = 0f;
	private volatile float z = 0f;

	public Vector() {
		this(0, 0, 0);
	}

	public Vector(Vector v) {
		this(v.x, v.y, v.z);
	}

	public synchronized float getX() {
        return x;
    }
    public synchronized void setX(float x) {
        this.x = x;
    }

    public synchronized float getY() {
        return y;
    }
    public synchronized void setY(float y) {
        this.y = y;
    }

    public synchronized float getZ() {
        return z;
    }
    public synchronized void setZ(float z) {
        this.z = z;
    }

	public Vector(float x, float y, float z) {
	    set(x, y, z);
	}

    public synchronized void get(float[] array) {
        if (array==null || array.length!=3) 
            throw new IllegalArgumentException("get() array must be non-NULL and size of 3");
        
        array[0] = this.x;
        array[1] = this.y;
        array[2] = this.z;
    }

    public void set(Vector v) {
        if (v==null) return;
        
        set(v.x, v.y, v.z);
    }

    public void set(float[] array) {
        if (array==null || array.length!=3) 
            throw new IllegalArgumentException("get() array must be non-NULL and size of 3");
        
        set(array[0], array[1], array[2]);
    }
	public synchronized void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		if (obj==null) return false;

		Vector v = (Vector) obj;
		return (v.x == this.x && v.y == this.y && v.z == this.z);
	}

	public synchronized boolean equals(float x, float y, float z) {
		return (this.x == x && this.y == y && this.z == z);
	}

	public synchronized void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void add(Vector v) {
		if (v==null) return;
		
		add(v.x, v.y, v.z);
	}

	public void sub(float x, float y, float z) {
		add(-x, -y, -z);
	}

	public void sub(Vector v) {
		if (v==null) return;
		
		add(-v.x, -v.y, -v.z);
	}

	public synchronized void mult(float s) {
	    this.x *= s;
	    this.y *= s;
	    this.z *= s;
	}

	public synchronized void divide(float s) {
	    this.x /= s;
	    this.y /= s;
	    this.z /= s;
	}

	public synchronized float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public synchronized float length2D() {
		return (float) Math.sqrt(this.x * this.x + this.z * this.z);
	}

	public void norm() {
		divide(length());
	}

	public synchronized float dot(Vector v) {
		if (v==null) return 0f;

		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	public synchronized void cross(Vector u, Vector v) {
		if (v==null || u==null) return;
		
		float x = u.y * v.z - u.z * v.y;
		float y = u.z * v.x - u.x * v.z;
		float z = u.x * v.y - u.y * v.x;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public synchronized void prod(Matrix m) {
		if (m==null) return;

		m.get(matrixArray);
        float xTemp = matrixArray[0] * this.x + matrixArray[1] * this.y + matrixArray[2] * this.z;
        float yTemp = matrixArray[3] * this.x + matrixArray[4] * this.y + matrixArray[5] * this.z;
        float zTemp = matrixArray[6] * this.x + matrixArray[7] * this.y + matrixArray[8] * this.z;

		this.x = xTemp;
		this.y = yTemp;
		this.z = zTemp;
	}

	@Override
	public synchronized String toString() {
		return "<" + this.x + ", " + this.y + ", " + this.z + ">";
	}
}
