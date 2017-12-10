package com.lecoder.team9.lecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

/**
 * Created by GAYEON on 2017-12-09.
 */

public class PaintBoard extends View {
    Stack undos = new Stack();
    public static int maxUndos = 10;
    public boolean changed = false;
    Canvas mCanvas;
    Bitmap mBitmap;
    final Paint mPaint;
    float lastX;
    float lastY;

    private final Path mPath = new Path();
    private float mCurveEndX;
    private float mCurveEndY;

    private int mInvalidateExtraBorder = 10;
    static final float TOUCH_TOLERANCE = 8;

    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private int mCertainColor = 0xFF000000;
    private float mStrokeWidth = 2.0f;

    public PaintBoard(Context context) {
        super(context);

        // create a new paint object
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);
        mPaint.setColor(mCertainColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(DITHER_FLAG);

        lastX = -1;
        lastY = -1;
    }

    public void clearUndo() {
        while(true) {
            Bitmap prev = (Bitmap) undos.pop();
            if (prev == null) return;

            prev.recycle();
        }
    }

    public void saveUndo() {
        if (mBitmap == null) return;

        while(undos.size() >= maxUndos) {
            Bitmap i = (Bitmap) undos.get(undos.size() - 1);
            i.recycle();
            undos.remove(i);
        }

        Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        undos.push(img);
    }

    public void undo() {
        Bitmap prev = null;
        try {
            prev = (Bitmap) undos.pop();
        } catch (Exception e) {
            Log.e("GoodPaintBoard", "exception : " + e.getMessage());
        }

        if (prev != null) {
            drawBackground(mCanvas);
            mCanvas.drawBitmap(prev, 0, 0, mPaint);
            invalidate();

            prev.recycle();
        }
    }

    public void drawBackground(Canvas canvas) {
        if (canvas != null)
            canvas.drawColor(Color.WHITE);
    }

    public void updatePaintProperty(int color, int size) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(size);
    }

    public void newImage(int width, int height) {
        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        drawBackground(mCanvas);

        changed = false;
        invalidate();
    }

    public void setImageSize(int width, int height, Bitmap newImage) {
        if (mBitmap != null) {
            if (width < mBitmap.getWidth()) width = mBitmap.getWidth();
            if (height < mBitmap.getHeight()) height = mBitmap.getHeight();
        }

        if (width < 1 || height < 1) return;

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        drawBackground(canvas);

        if (newImage != null) {
            canvas.setBitmap(newImage);
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mCanvas.restore();
        }

        mBitmap = img;
        mCanvas = canvas;

        clearUndo();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            newImage(w, h);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP :
                changed = true;

                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                }
                mPath.rewind();

                return true;

            case MotionEvent.ACTION_DOWN:
                saveUndo();

                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
        }
        return false;
    }

    private Rect touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        lastX = x;
        lastY = y;

        Rect mInvalidRect = new Rect();
        mPath.moveTo(x, y);

        final int border = mInvalidateExtraBorder;
        mInvalidRect.set((int)x - border, (int) y -border, (int)x+border, (int) y+border);

        mCurveEndX = x;
        mCurveEndY = y;

        mCanvas.drawPath(mPath, mPaint);

        return mInvalidRect;
    }

    private Rect touchMove(MotionEvent event) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect touchUp(MotionEvent event, boolean cancle) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x-lastX);
        final float dy = Math.abs(y-lastY);

        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;
            mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY-border, (int) mCurveEndX + border, (int) mCurveEndY + border);

            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;

            mPath.quadTo(lastX, lastY, cX, cY);

            mInvalidRect.union((int) lastX-border, (int) lastY -border, (int) lastX+border, (int)lastY+border);
            mInvalidRect.union((int) cX-border, (int) cY -border, (int) cX+border, (int)cY+border);

            lastX = x;
            lastY = y;

            mCanvas.drawPath(mPath, mPaint);
        }
        return mInvalidRect;
    }
}
