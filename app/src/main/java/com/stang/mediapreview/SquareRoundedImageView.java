package com.stang.mediapreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by Stanislav on 22.11.2016.
 */

public class SquareRoundedImageView extends RoundedImageView {
    public static final int MATCH_NONE = 0;
    public static final int MATCH_WIDTH = 1;
    public static final int MATCH_HEIGHT = 2;

    private int mMatch = MATCH_WIDTH;

    public SquareRoundedImageView(Context context) {
        super(context);
    }

    public SquareRoundedImageView(Context context, int match) {
        super(context);
        mMatch = match;
    }

    public SquareRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SquareRoundedImageView,
                0, 0
        );

        try {
            mMatch = a.getInt(R.styleable.SquareRoundedImageView_square_match, MATCH_NONE);
        } finally {
            a.recycle();
        }

    }


    public SquareRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
//        setMeasuredDimension(width, height);
        switch (mMatch) {
            case MATCH_WIDTH:
                setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
                break;
            case MATCH_HEIGHT:
                setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
                break;
            default:
                setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
                break;
        }

    }
}
