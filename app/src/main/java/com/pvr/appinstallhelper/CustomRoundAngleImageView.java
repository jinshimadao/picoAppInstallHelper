package com.pvr.appinstallhelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by goodman.ye on 2019/8/9.
 */
public class CustomRoundAngleImageView extends ImageView {
    float width, height;
    int radio = 20;

    public CustomRoundAngleImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public CustomRoundAngleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public CustomRoundAngleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        Log.i("circleImageview", "width = " + width + ",height = " + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= radio && height > radio) {
            Path path = new Path();
            //四个圆角
            path.moveTo(radio, 0);
            path.lineTo(width - radio, 0);
            path.quadTo(width, 0, width, radio);
            path.lineTo(width, height - radio);
            path.quadTo(width, height, width - radio, height);
            path.lineTo(radio, height);
            path.quadTo(0, height, 0, height - radio);
            path.lineTo(0, radio);
            path.quadTo(0, 0, radio, 0);

            canvas.clipPath(path);
            Log.i("circleImageview", "Do canvas.clipPath()");
        }
        super.onDraw(canvas);
    }

}