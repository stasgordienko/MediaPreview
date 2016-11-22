package com.stang.mediapreview;

import android.content.Context;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by Stanislav on 22.11.2016.
 */

public class SquareRoundedImageView extends RoundedImageView {

    public SquareRoundedImageView(Context context) {
        super(context);
    }

    public SquareRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
//        setMeasuredDimension(width, height);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }
}
