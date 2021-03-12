package com.zhx.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FixedRatioImageView extends androidx.appcompat.widget.AppCompatImageView {
    public FixedRatioImageView(@NonNull Context context) {
        super(context);
    }

    public FixedRatioImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (MeasureSpec.AT_MOST == widthMode || MeasureSpec.AT_MOST == heightMode) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(widthSize, heightSize);
            //固定宽高比例4:3显示图片
            setMeasuredDimension(size, (int) (size * 0.75));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
