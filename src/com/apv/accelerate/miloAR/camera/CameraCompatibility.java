package com.apv.accelerate.miloAR.camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.hardware.Camera;


/**
 * AR Framework
 */
public class CameraCompatibility {
	private static Method getSupportedPreviewSizes = null;

	static {
		initCompatibility();
	};

	/** 
	 * This will fail on older phones (Android version < 2.0) 
	 */
	private static void initCompatibility() {
		try {
			getSupportedPreviewSizes = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", new Class[] { } );
			/* success, this is a newer device */
		} catch (NoSuchMethodException nsme) {
			/* failure, must be older device */
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters params) {
		List<Camera.Size> retList = null;

		try {
			Object retObj = getSupportedPreviewSizes.invoke(params);
			if (retObj != null) {
				retList = (List<Camera.Size>)retObj;
			}
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new RuntimeException(ite);
			}
		} catch (IllegalAccessException ie) {
			ie.printStackTrace();
		}
		return retList;
	}
}
