package com.zhx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: TangCan
 * Date: 13-4-10
 * Time: 下午1:33
 */
public class BatteryView extends View {
    Paint paint;
    int inner_color;
    int outer_color;
    int outer_width;
    int progress;

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        readAttrs(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    private void readAttrs(Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);

//        inner_color = a.getColor(R.styleable.BatteryView_battery_inner_color, 0xff333333);
//        outer_color = a.getColor(R.styleable.BatteryView_battery_out_color, 0xff333333);
//        outer_width = LayoutUtil.GetPixelByDIP(getContext(), (int) a.getDimension(R.styleable.BatteryView_battery_out_width, 1));
//        progress = a.getInt(R.styleable.BatteryView_battery_progress, 10);
//        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(outer_color);
        Rect rect = canvas.getClipBounds();
        Rect left = new Rect(rect.left, rect.height() * 7 / 18, rect.left + 3, rect.height() * 11 / 18);
        canvas.drawRect(left, paint);

        Rect wrapper = new Rect(left.right, 0, rect.right, rect.bottom);
        canvas.drawRect(wrapper, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(inner_color);
        int total = wrapper.width() - 4;
        int right = wrapper.right - 2;
        Rect inner = new Rect(right - total * progress / 100, 2, right, wrapper.bottom - 2);
        canvas.drawRect(inner, paint);
    }

    public void setColor(int color){
        outer_color = color;
        inner_color = color;
        invalidate();
    }

    public void setColor(int out, int inner) {
        outer_color = out;
        inner_color = inner;
        invalidate();
    }

    public void setProgress(int progress) {
        progress = Math.max(0, Math.min(progress, 100));
        this.progress = progress;
    }
}
