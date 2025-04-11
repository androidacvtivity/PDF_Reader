package com.bancusoft.pdfreader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class HighlightOverlayView extends View {
    private final Paint paint;
    private RectF highlightRect;
    private float scale = 1f;

    public HighlightOverlayView(Context context) {
        this(context, null);
    }

    public HighlightOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0x55FF0000); // roșu semi-transparent
        paint.setStyle(Paint.Style.FILL);
    }

    public void setHighlight(RectF rect, float scaleFactor) {
        if (rect != null) {
            this.scale = scaleFactor;
            this.highlightRect = new RectF(
                    rect.left * scale,
                    rect.top * scale,
                    rect.right * scale,
                    rect.bottom * scale
            );
            invalidate();
        }
    }

    public void clearHighlight() {
        highlightRect = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (highlightRect != null) {
            canvas.drawRect(highlightRect, paint);
        }
    }
}
