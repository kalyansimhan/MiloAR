package com.apv.accelerate.miloAR.activities;

import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.apv.accelerate.miloAR.data.ARData;
import com.apv.accelerate.miloAR.ui.objects.PaintableBoxedText;
import com.apv.accelerate.miloAR.ui.objects.PaintablePosition;
import com.apv.accelerate.miloAR.views.Marker;
import com.apv.accelerate.miloAR.views.Radar;



/**
 * AR Framework
 */
public class AugmentedView extends View {
    private static final AtomicBoolean drawing = new AtomicBoolean(false);
    
    private static final int fontSize = 14;
    private static final int startLabelX = 4;
    private static final int endLabelX = 92;
    private static final int labelY = 95;
    private static final String startKM = "0km";
    private static final String endKM = "80km";
    private static final int leftBound = 12;
    private static final int rightBound = 87;
    private static final int conflictHeight = 82;
    private static final Radar radar = new Radar();
    private static final float[] locationArray = new float[3];
    
    private static PaintablePosition startTxtContainter = null;
    private static PaintablePosition endTxtContainter = null;
    private static PaintablePosition currentTxtContainter = null;
    private static int lastZoom = 0;
    private static boolean useCollisionDetection = false;
    private static final int COLLISION_ADJUSTMENT = 1000;

    public AugmentedView(Context context, boolean useCollisionDetection) {
        super(context);
        AugmentedView.useCollisionDetection=useCollisionDetection;
    }

    private static PaintablePosition generateCurrentZoom(Canvas canvas) {
        lastZoom = ARData.getZoomProgress();
        PaintableBoxedText currentTxtBlock = new PaintableBoxedText(ARData.getZoomLevel(), fontSize, 30);
        int x = canvas.getWidth()/100*lastZoom;
        int y = canvas.getHeight()/100*labelY;
        if (lastZoom < leftBound || lastZoom > rightBound) {
            y = canvas.getHeight()/100*conflictHeight;
            if (lastZoom < leftBound)
                x = canvas.getWidth()/100*startLabelX;
            else
                x = canvas.getWidth()/100*endLabelX;
        }
        PaintablePosition container = new PaintablePosition(currentTxtBlock, x, y, 0, 1);
        return container;
    }

	@Override
    protected void onDraw(Canvas canvas) {
    	if (canvas==null) return;
    	
        if (drawing.compareAndSet(false, true)) { 
	        if (startTxtContainter==null) {
	            PaintableBoxedText startTextBlock = new PaintableBoxedText(startKM, fontSize, 30);
	            startTxtContainter = new PaintablePosition( startTextBlock, 
	                                                         (canvas.getWidth()/100*startLabelX), 
	                                                         (canvas.getHeight()/100*labelY), 
	                                                         0, 
	                                                         1);
	        }
	        startTxtContainter.paint(canvas);
	        
	        if (endTxtContainter==null) {
	            PaintableBoxedText endTextBlock = new PaintableBoxedText(endKM, fontSize, 30);
	            endTxtContainter = new PaintablePosition( endTextBlock, 
	                                                       (canvas.getWidth()/100*endLabelX), 
	                                                       (canvas.getHeight()/100*labelY), 
	                                                       0, 
	                                                       1);
	        }
	        endTxtContainter.paint(canvas);
        	
	        //Re-factor zoom text, if it has changed.
	        if (lastZoom != ARData.getZoomProgress()) currentTxtContainter = generateCurrentZoom(canvas);
	        currentTxtContainter.paint(canvas);

	        Collection<Marker> collection = ARData.getMarkers();
	        if (useCollisionDetection) collection = adjustForCollisions(canvas,collection);
	        //Draw AR markers
	        for (Marker marker : collection) {
	            marker.draw(canvas);
	        }

	        //Radar circle and radar markers
	        radar.draw(canvas);
	        drawing.set(false);
        }
    }
	
	private static Collection<Marker> adjustForCollisions(Canvas canvas, Collection<Marker> collection) {
        TreeSet<Marker> updated = new TreeSet<Marker>();
        //Update the AR markers for collisions
        for (Marker marker1 : collection) {
            if (updated.contains(marker1)) continue;

            int collisions = 1;
            for (Marker marker2 : collection) {
                if (marker1.equals(marker2) || updated.contains(marker2)) continue;

                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(locationArray);
                    float y = locationArray[1];
                    float h = collisions*COLLISION_ADJUSTMENT;
                    locationArray[1] = y+h;
                    marker2.getLocation().set(locationArray);
                    collisions++;
                    updated.add(marker2);
                }
            }
            updated.add(marker1);
        }
        return collection;
	}
}
