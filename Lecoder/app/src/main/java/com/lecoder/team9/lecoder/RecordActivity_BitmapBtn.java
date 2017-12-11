package com.lecoder.team9.lecoder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by GAYEON on 2017-11-24.
 */

public class RecordActivity_BitmapBtn extends AppCompatButton {
    int iconNormal= R.drawable.normalbutton;
    int iconClicked = R.drawable.clickedbutton;

    int iconStatus = STATUS_NORMAL;
    public static int STATUS_NORMAL = 0;
    public static int STATUS_CLICKED = 1;

    public RecordActivity_BitmapBtn(Context context) {
        super(context);
        init();
    }

    public RecordActivity_BitmapBtn(Context context, AttributeSet atts) {
        super(context, atts);
        init();
    }

    public void init() {
        setBackgroundResource(iconNormal);

        int defaultTextColor = Color.WHITE;
        Typeface defaultTypeface = Typeface.DEFAULT_BOLD;

        setTextColor(defaultTextColor);
        setTypeface(defaultTypeface);
    }

    public void setIcon(int iconNormal, int iconClicked) {
        this.iconNormal = iconNormal;
        this.iconClicked = iconClicked;
    }

    public boolean  onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundResource(R.drawable.clickedbutton);
                iconStatus = STATUS_CLICKED;
                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setBackgroundResource(R.drawable.normalbutton);
                iconStatus = STATUS_NORMAL;
                break;
        }

        invalidate();
        return true;
    }
}
