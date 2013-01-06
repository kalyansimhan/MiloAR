package com.apv.accelerate.miloAR.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.apv.accelerate.miloAR.ui.objects.PaintableIcon;
import com.apv.accelerate.miloAR.ui.objects.PaintablePosition;

public class IconMarker extends Marker {
    private static final float[] symbolArray = new float[3];
    private Bitmap bitmap = null;

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, Bitmap bitmap) {
        super(name, latitude, longitude, altitude, color);
        this.bitmap = bitmap;
    }

    @Override
    public void drawIcon(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();
    	
        if (gpsSymbol==null) gpsSymbol = new PaintableIcon(bitmap,96,96);
    	
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) 
            symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        else 
            symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        symbolContainer.paint(canvas);
    }
}
