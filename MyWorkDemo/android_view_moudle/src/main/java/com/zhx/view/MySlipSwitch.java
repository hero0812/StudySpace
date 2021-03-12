package com.zhx.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class MySlipSwitch extends View {
    private static final int MAX_ANIMATION_DURATION = 400;

    // 开关开启时的背景，关闭时的背景，滑动按钮
    private Bitmap mSwitch_on_Bkg, mSwitch_off_Bkg, mSlip_on_Btn, mSlip_off_Btn;
    private Rect mOn_Rect, mOff_Rect;

    // 是否正在滑动
    private boolean mIsSlipping = false;
    // 当前开关状态，true为开启，false为关闭
    private boolean mIsSwitchOn = false;

    // 手指按下时的水平坐标X，当前的水平坐标X，按钮左边坐标
    private float mActionDownX, mCurrentX, mRealLeft;

    // 开关监听器
    private OnSwitchListener mOnSwitchListener;

    private boolean isTapEvent = true;

    private Matrix matrix = new Matrix();
    private boolean isDark = false;
    private Scroller mScroller;

    public MySlipSwitch(Context context) {
        super(context);
        setImageResource();
    }

    public MySlipSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource();
    }

    public void setTheme(boolean isDark) {
        this.isDark = isDark;
        setImageResource();
        invalidate();
    }

    public void setImageResource() {
        mScroller = new Scroller(getContext());
        mSwitch_on_Bkg = BitmapFactory.decodeResource(getResources(), android.R.drawable.btn_star_big_on);
        mSwitch_off_Bkg = BitmapFactory.decodeResource(getResources(), isDark ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        mSlip_on_Btn = BitmapFactory.decodeResource(getResources(), isDark ?android.R.drawable.bottom_bar : android.R.drawable.bottom_bar);
        mSlip_off_Btn = BitmapFactory.decodeResource(getResources(), isDark ? android.R.drawable.star_big_off : android.R.drawable.star_big_on);

        // 右半边Rect，即滑动按钮在右半边时表示开关开启
        mOn_Rect = new Rect(mSwitch_off_Bkg.getWidth() - mSlip_on_Btn.getWidth(), 0, mSwitch_off_Bkg.getWidth(),
                mSlip_on_Btn.getHeight());
        // 左半边Rect，即滑动按钮在左半边时表示开关关闭
        mOff_Rect = new Rect(0, 0, mSlip_off_Btn.getWidth(), mSlip_off_Btn.getHeight());
    }

    public void setSwitchOn(boolean isOn) {
        setSwitchOn(isOn, false);
    }

    public void setSwitchOn(boolean isOn, boolean hasAnimation) {
        if (mIsSwitchOn != isOn) {
            mIsSwitchOn = isOn;
            if (hasAnimation) {
                startAnimation();
            }
        }
        invalidate();
    }

    public boolean isSwitchOn() {
        return mIsSwitchOn;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mRealLeft = mScroller.getCurrX();
            mCurrentX = mRealLeft + mSlip_on_Btn.getWidth() / 2;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 滑动按钮的左边坐标
        float left_SlipBtn;

        // 手指滑动到左半边的时候表示开关为关闭状态，滑动到右半边的时候表示开关为开启状态
        if (mCurrentX < (mSwitch_on_Bkg.getWidth() / 2)) {
            canvas.drawBitmap(mSwitch_off_Bkg, matrix, null);
        } else {
            canvas.drawBitmap(mSwitch_on_Bkg, matrix, null);
        }

        if (mScroller.computeScrollOffset()) {
            left_SlipBtn = mRealLeft;
            // 判断当前是否正在滑动
        } else if (mIsSlipping) {
            if (mCurrentX > mSwitch_on_Bkg.getWidth()) {
                left_SlipBtn = mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth();
            } else {
                left_SlipBtn = mCurrentX - mSlip_on_Btn.getWidth() / 2;
            }
        } else {
            // 根据当前的开关状态设置滑动按钮的位置
            if (mIsSwitchOn) {
                left_SlipBtn = mOn_Rect.left;
                canvas.drawBitmap(mSwitch_on_Bkg, matrix, null);
            } else {
                left_SlipBtn = mOff_Rect.left;
                canvas.drawBitmap(mSwitch_off_Bkg, matrix, null);
            }
        }

        // 对滑动按钮的位置进行异常判断
        if (left_SlipBtn < 0) {
            left_SlipBtn = 0;
        } else if (left_SlipBtn > mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth()) {
            left_SlipBtn = mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth();
        }

        if (mIsSwitchOn) {
            canvas.drawBitmap(mSlip_on_Btn, left_SlipBtn, 0, null);
        } else {
            canvas.drawBitmap(mSlip_off_Btn, left_SlipBtn, 0, null);
        }

        mRealLeft = left_SlipBtn;
        mCurrentX = mRealLeft + mSlip_on_Btn.getWidth() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSwitch_on_Bkg.getWidth(), mSwitch_on_Bkg.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                if (getParent() != null)
                    getParent().requestDisallowInterceptTouchEvent(true);
                if (event.getX() > mSwitch_on_Bkg.getWidth() || event.getY() > mSwitch_on_Bkg.getHeight()) {
                    return false;
                }
                isTapEvent = true;
                mIsSlipping = true;
                mActionDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mActionDownX) > 10) {
                    isTapEvent = false;
                }
                if (!isTapEvent) {
                    mCurrentX = event.getX();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsSlipping = false;
                boolean previousSwitchState = mIsSwitchOn;
                if (isTapEvent) {
                    mIsSwitchOn = !mIsSwitchOn;
                } else {
                    mIsSwitchOn = event.getX() >= (mSwitch_on_Bkg.getWidth() / 2);
                }
                if (mOnSwitchListener != null && previousSwitchState != mIsSwitchOn) {
                    mOnSwitchListener.onSwitched(mIsSwitchOn);
                }

                startAnimation();
                break;
        }
        invalidate();
        return true;
    }

    private void startAnimation() {
        if (mRealLeft >= 0 && mRealLeft <= mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth()) {
            int dx = (int) ((mIsSwitchOn ? mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth() : 0) - mRealLeft);
            mScroller.startScroll((int) mRealLeft, 0, dx, 0, MAX_ANIMATION_DURATION * Math.abs(dx) / (mSwitch_on_Bkg.getWidth() - mSlip_on_Btn.getWidth()));
        }
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mOnSwitchListener = listener;
    }

    public interface OnSwitchListener {
        void onSwitched(boolean isSwitchOn);
    }
}