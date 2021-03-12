//package com.zhx.view.event;
//
//import android.view.MotionEvent;
//
//import java.util.ArrayList;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * 事件分发View 处理事件逻辑
// */
//public class View {
//
//    static final int ENABLE = 0x00000000;
//
//    static final int DISABLE = 0x00000020;
//
//    static final int ENABLE_MASK = 0x00000020;
//
//    static final int CLICKABLE = 0x00004000;
//
//    static final int LONG_CLICKABLE = 0x00200000;
//
//    static final int CONTEXT_CLICKABLE = 0x00800000;
//
//    private static final int PFLAG_PRESSED = 0x00004000;
//
//    ListenerInfo mListenerInfo;
//
//    int mViewFlags;
//
//    PerformClick mPerformClick;
//
//    boolean mHasPerformedLongPress;
//
//    private CheckForLongPress mPendingCheckForLongPress;
//
//    /* @hide */
//    public int mPrivateFlags;
//
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        boolean result = false;
//        ListenerInfo li = mListenerInfo;
//        if (li != null && li.mOnTouchListener != null
//                && (mViewFlags & ENABLE_MASK) == ENABLE
//                && li.mOnTouchListener.onTouch(this, event)) {
//            result = true; // View是ENABLE状态 ，True 表示listener消费了事件
//        }
//        //OnTouchListener如果不消费事件，交给 onTouchEvent(event)方法消费
//        if (!result && onTouchEvent(event)) {
//            result = true;  //onTouchEvent() 返回true，则告知父ViewGroup它要消费事件
//        }
//
//        return result;
//    }
//
//    /**
//     * 自定义View可以实现这个方法处理屏幕事件
//     *
//     * @return True if the event was handled, false otherwise.
//     */
//    public boolean onTouchEvent(MotionEvent event) {
//        final float x = event.getX();
//        final float y = event.getY();
//        final int viewFlags = mViewFlags;
//        final int action = event.getAction();
//
//        final boolean clickable = ((viewFlags & CLICKABLE) == CLICKABLE
//                || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)
//                || (viewFlags & CONTEXT_CLICKABLE) == CONTEXT_CLICKABLE;
//        if (clickable) {
//            switch (action) {
//                case MotionEvent.ACTION_UP:
//                    //click事件在 MotionEvent.ACTION_UP 处理逻辑中检测
//                    if (!mHasPerformedLongPress) {
//                        if (mPerformClick == null) {
//                            mPerformClick = new PerformClick();
//                        }
//                        if (!post(mPerformClick)) {
//                            performClickInternal();
//                        }
//                    }
//                    break;
//                case MotionEvent.ACTION_DOWN:
//                    mHasPerformedLongPress = false;
//                    setPressed(true);
//                    checkForLongClick(0, x, y);
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    if (clickable) {
//                        setPressed(false);
//                    }
//                    mHasPerformedLongPress = false;
//
//                    break;
//                case MotionEvent.ACTION_MOVE:
////                    if (!pointInView(x, y, mTouchSlop)) {
////                        // Outside button
////                        // Remove any future long press/tap checks
////                        removeTapCallback();
////                        removeLongPressCallback();
////                        if ((mPrivateFlags & PFLAG_PRESSED) != 0) {
////                            setPressed(false);
////                        }
////                        mPrivateFlags3 &= ~PFLAG3_FINGER_DOWN;
////                    }
//                    break;
//            }
//            return true;
//        }
//
//        return false;
//    }
//
//
//    private void checkForLongClick(int i, float x, float y) {
//        if ((mViewFlags & LONG_CLICKABLE) == LONG_CLICKABLE) {
//            mHasPerformedLongPress = false;
//            if (mPendingCheckForLongPress == null) {
//                mPendingCheckForLongPress = new CheckForLongPress();
//            }
//            mPendingCheckForLongPress.rememberPressedState();//记录按压状态
//            postDelayed(mPendingCheckForLongPress,
//                    500);
//        }
//    }
//
//    private final class CheckForLongPress implements Runnable {
//        boolean mOriginalPressedState;
//
//        @Override
//        public void run() {
//            if (mOriginalPressedState == isPressed()) {
//                if (performLongClick()) {
//                    mHasPerformedLongPress = true;
//                }
//            }
//        }
//
//        public void rememberPressedState() {
//            mOriginalPressedState = isPressed();
//        }
//    }
//
//
//    private void setPressed(boolean pressed) {
//        final boolean needsRefresh = pressed != ((mPrivateFlags & PFLAG_PRESSED) == PFLAG_PRESSED);
//        if (pressed) {
//            mPrivateFlags |= PFLAG_PRESSED;
//        } else {
//            mPrivateFlags &= ~PFLAG_PRESSED;
//        }
////        if (needsRefresh) {
////            refreshDrawableState();
////        }
//    }
//
//    private boolean isPressed() {
//        return (mPrivateFlags & PFLAG_PRESSED) == PFLAG_PRESSED;
//    }
//
//    /**
//     * 如果 onLongClick 返回false，则可以继续处理ContextMenu
//     *
//     * @return
//     */
//    public boolean performLongClick() {
//        boolean handled = false;
//        final ListenerInfo li = mListenerInfo;
//        if (li != null && li.mOnLongClickListener != null) {
//            handled = li.mOnLongClickListener.onLongClick(this);
//        }
//        if (!handled) {
//            final boolean isAnchored = !Float.isNaN(x) && !Float.isNaN(y);
//            handled = isAnchored ? showContextMenu(x, y) : showContextMenu();
//        }
//        return handled;
//    }
//
//    /**
//     * 将Runnable加入到用户线程MessageQueue并执行
//     *
//     * @param action
//     * @return
//     */
//    public boolean post(Runnable action) {
//        // AttachInfo -- View所附属信息集合，里边包括了处理事件的Handler、Window等
//        final AttachInfo attachInfo = mAttachInfo;
//        if (attachInfo != null) {
//            return attachInfo.mHandler.post(action);
//        }
//
//        // Postpone the runnable until we know on which thread it needs to run.
//        // Assume that the runnable will be successfully placed after attach.
//        getRunQueue().post(action);
//        return true;
//    }
//
//    public boolean postDelayed(Runnable action, long delayMillis) {
//        final AttachInfo attachInfo = mAttachInfo;
//        if (attachInfo != null) {
//            return attachInfo.mHandler.postDelayed(action, delayMillis);
//        }
//
//        // Postpone the runnable until we know on which thread it needs to run.
//        // Assume that the runnable will be successfully placed after attach.
//        getRunQueue().postDelayed(action, delayMillis);
//        return true;
//    }
//
//    private final class PerformClick implements Runnable {
//
//        @Override
//        public void run() {
//            performClickInternal();
//        }
//    }
//
//    private void performClickInternal() {
//        performClick();
//    }
//
//    /**
//     * 回调这个View的OnClickListener
//     *
//     * @return true 只要设置了OnClickListener就返回true
//     */
//    public boolean performClick() {
//        final boolean result;
//        final ListenerInfo li = mListenerInfo;
//        if (li != null && li.mOnClickListener != null) {
//            li.mOnClickListener.onClick(this);
//            result = true;  //onClick()方法没有返回值，回调后直接认为事件被消费了
//        } else {
//            result = false;
//        }
//        return result;
//    }
//
//    ListenerInfo getListenerInfo() {
//        if (mListenerInfo != null) {
//            return mListenerInfo;
//        }
//        return new ListenerInfo();
//    }
//
//    /**
//     * 设置OnClickListener回调，如果View是不可点击的，把它变成clickable的
//     *
//     * @param l
//     */
//    public void setOnClickListener(OnClickListener l) {
//        if (!isClickable()) {
//            setClickable(true);
//        }
//        getListenerInfo().mOnClickListener = l;
//    }
//
//    public boolean isClickable() {
//        return (mViewFlags & CLICKABLE) == CLICKABLE;
//    }
//
//    public void setClickable(boolean clickable) {
////        setFlags(clickable ? CLICKABLE : 0, CLICKABLE);
//    }
//
//    public void setOnTouchListener(OnTouchListener l) {
//        getListenerInfo().mOnTouchListener = l;
//    }
//
//    public interface OnTouchListener {
//        /**
//         * Called when a touch event is dispatched to a view. This allows listeners to
//         * get a chance to respond before the target view.
//         *
//         * @param v     The view the touch event has been dispatched to.
//         * @param event The MotionEvent object containing full information about
//         *              the event.
//         * @return True 表示listener消费了事件
//         */
//        boolean onTouch(View v, MotionEvent event);
//    }
//
//    public interface OnClickListener {
//        void onClick(View v);
//    }
//
//    public interface OnLongClickListener {
//        /**
//         * Called when a view has been clicked and held.
//         *
//         * @param v The view that was clicked and held.
//         * @return true if the callback consumed the long click, false otherwise.
//         */
//        boolean onLongClick(View v);
//    }
//
//    static class ListenerInfo {
//        /**
//         * Listener used to dispatch focus change events.
//         * This field should be made private, so it is hidden from the SDK.
//         * {@hide}
//         */
//        protected android.view.View.OnFocusChangeListener mOnFocusChangeListener;
//
//        /**
//         * Listeners for layout change events.
//         */
//        private ArrayList<android.view.View.OnLayoutChangeListener> mOnLayoutChangeListeners;
//
//        protected android.view.View.OnScrollChangeListener mOnScrollChangeListener;
//
//        /**
//         * Listeners for attach events.
//         */
//        private CopyOnWriteArrayList<android.view.View.OnAttachStateChangeListener> mOnAttachStateChangeListeners;
//
//        /**
//         * Listener used to dispatch click events.
//         * This field should be made private, so it is hidden from the SDK.
//         * {@hide}
//         */
//        public OnClickListener mOnClickListener;
//
//        /**
//         * Listener used to dispatch long click events.
//         * This field should be made private, so it is hidden from the SDK.
//         * {@hide}
//         */
//        protected OnLongClickListener mOnLongClickListener;
//
//        /**
//         * Listener used to dispatch context click events. This field should be made private, so it
//         * is hidden from the SDK.
//         * {@hide}
//         */
//        protected android.view.View.OnContextClickListener mOnContextClickListener;
//
//        /**
//         * Listener used to build the context menu.
//         * This field should be made private, so it is hidden from the SDK.
//         * {@hide}
//         */
//        protected android.view.View.OnCreateContextMenuListener mOnCreateContextMenuListener;
//
//        private android.view.View.OnKeyListener mOnKeyListener;
//
//        private OnTouchListener mOnTouchListener;
//
//        private android.view.View.OnHoverListener mOnHoverListener;
//
//        private android.view.View.OnGenericMotionListener mOnGenericMotionListener;
//
//        private android.view.View.OnDragListener mOnDragListener;
//
//        private android.view.View.OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
//
//        android.view.View.OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
//
//        android.view.View.OnCapturedPointerListener mOnCapturedPointerListener;
//
//        private ArrayList<android.view.View.OnUnhandledKeyEventListener> mUnhandledKeyListeners;
//    }
//}
