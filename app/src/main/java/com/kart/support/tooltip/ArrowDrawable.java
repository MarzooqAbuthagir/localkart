package com.kart.support.tooltip;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.ColorInt;

public class ArrowDrawable extends ColorDrawable {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    public static final int AUTO = 4;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int mBackgroundColor;
    private final int mDirection;
    private Path mPath;

    ArrowDrawable(@ColorInt int foregroundColor, int direction) {
        this.mBackgroundColor = Color.TRANSPARENT;
        this.mPaint.setColor(foregroundColor);
        this.mDirection = direction;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updatePath(bounds);
    }

    private synchronized void updatePath(Rect bounds) {
        mPath = new Path();

        if (mDirection == LEFT) {
            mPath.moveTo(bounds.width(), bounds.height());
            mPath.lineTo(0, bounds.height() / 2f);
            mPath.lineTo(bounds.width(), 0);
            mPath.lineTo(bounds.width(), bounds.height());

        } else if (mDirection == TOP) {
            mPath.moveTo(0, bounds.height());
            mPath.lineTo(bounds.width() / 2f, 0);
            mPath.lineTo(bounds.width(), bounds.height());
            mPath.lineTo(0, bounds.height());

        } else if (mDirection == RIGHT) {
            mPath.moveTo(0, 0);
            mPath.lineTo(bounds.width(), bounds.height() / 2f);
            mPath.lineTo(0, bounds.height());
            mPath.lineTo(0, 0);

        } else if (mDirection == BOTTOM) {
            mPath.moveTo(0, 0);
            mPath.lineTo(bounds.width() / 2f, bounds.height());
            mPath.lineTo(bounds.width(), 0);
            mPath.lineTo(0, 0);

        }

        mPath.close();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);
        if (mPath == null)
            updatePath(getBounds());
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        if (mPaint.getColorFilter() != null) {
            return PixelFormat.TRANSLUCENT;
        }

        int i = mPaint.getColor() >>> 24;
        if (i == 255) {
            return PixelFormat.OPAQUE;
        } else if (i == 0) {
            return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }
}

