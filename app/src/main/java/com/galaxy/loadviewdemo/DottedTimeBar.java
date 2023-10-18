package com.galaxy.loadviewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

import java.util.List;

public class DottedTimeBar extends DefaultTimeBar {
    /**
     * Int values which corresponds to dots
     */
    private List<TypePosition> mDotsPositions = null;
    /**
     * Drawable for dot
     */
    private Bitmap mDotBitmap = null;

    public DottedTimeBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Initializes Seek bar extended attributes from xml
     *
     * @param attributeSet {@link AttributeSet}
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void init(final AttributeSet attributeSet) {
        final TypedArray attrsArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.DottedTimeBar, 0, 0);

        final int dotsArrayResource = attrsArray.getResourceId(R.styleable.DottedTimeBar_dots_positions, 0);

        /*if (0 != dotsArrayResource) {
            mDotsPositions = getResources().getIntArray(dotsArrayResource);
        }*/

        final int dotDrawableId = attrsArray.getResourceId(R.styleable.DottedTimeBar_dots_drawable, 0);

        if (0 != dotDrawableId) {
            //mDotBitmap = BitmapFactory.decodeResource(getResources(), dotDrawableId);
            mDotBitmap = Utils.drawableToBitmap(getResources().getDrawable(dotDrawableId));
        }
    }

    /**
     * @param mDotsPositions to be displayed on this SeekBar
     */
    public void setDots(List<TypePosition> mDotsPositions) {
        this.mDotsPositions = mDotsPositions;
        invalidate();
    }

    /**
     * @param dotsResource resourcCone id to be used for dots drawing
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setDotsDrawable(final int dotsResource) {
        mDotBitmap = Utils.drawableToBitmap(getResources().getDrawable(dotsResource));
        invalidate();
    }


    @Override
    public synchronized void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final float width = getMeasuredWidth();
        final float step = width / 100/*getMax()*/;
        if (null != mDotsPositions && 0 != mDotsPositions.size()) {
            // draw dots if we have ones
            for (TypePosition item : mDotsPositions) {
                canvas.drawBitmap(item.getDotBitmap(), (item.getPosition() * step) == 0 ? 10 : (item.getPosition() * step), getContext().getResources().getDimension(R.dimen._5sdp), null);
            }
        }
    }
}
