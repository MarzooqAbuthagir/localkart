package com.localkartmarketing.localkart.support.tooltip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import com.localkartmarketing.localkart.R;


public class OverlayView extends View {

    public static final int HIGHLIGHT_SHAPE_OVAL = 0;
    public static final int HIGHLIGHT_SHAPE_RECTANGULAR = 1;
    private static final int M_DEFAULT_OVERLAY_ALPHA_RES = R.integer.simpletooltip_overlay_alpha;
    private int highlightShape;
    private float mOffset;
    private View mAnchorView;
    private Bitmap bitmap;
    private boolean invalidated = true;

    public OverlayView(Context context) {
        super(context);
    }

    OverlayView(Context context, View anchorView, int highlightShape, float offset) {
        super(context);
        this.mAnchorView = anchorView;
        this.mOffset = offset;
        this.highlightShape = highlightShape;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (invalidated || bitmap == null || bitmap.isRecycled())
            createWindowFrame();
        // The bitmap is checked again because of Android memory cleanup behavior. (See #42)
        if (bitmap != null && !bitmap.isRecycled())
            canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private void createWindowFrame() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (width <= 0 || height <= 0)
            return;

        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas osCanvas = new Canvas(bitmap);

        RectF outerRectangle = new RectF(0, 0, width, height);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setAlpha(getResources().getInteger(M_DEFAULT_OVERLAY_ALPHA_RES));
        osCanvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        RectF anchorRecr = SimpleTooltipUtils.calculeRectInWindow(mAnchorView);
        RectF overlayRecr = SimpleTooltipUtils.calculeRectInWindow(this);

        float left = anchorRecr.left - overlayRecr.left;
        float top = anchorRecr.top - overlayRecr.top;

        RectF rect = new RectF(left - mOffset, top - mOffset, left + mAnchorView.getMeasuredWidth() + mOffset, top + mAnchorView.getMeasuredHeight() + mOffset);

        if (highlightShape == HIGHLIGHT_SHAPE_RECTANGULAR) {
            osCanvas.drawRect(rect, paint);
        } else {
            osCanvas.drawOval(rect, paint);
        }

        invalidated = false;
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidated = true;
    }

}
