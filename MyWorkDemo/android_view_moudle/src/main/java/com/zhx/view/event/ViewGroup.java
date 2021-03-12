//package com.zhx.view.event;
//
//import android.content.Context;
//import android.view.InputDevice;
//import android.view.MotionEvent;
//
//import java.util.ArrayList;
//
//public class ViewGroup extends View {
//
//    protected static final int FLAG_DISALLOW_INTERCEPT = 0x80000;
//
//    static final int ARRAY_INITIAL_CAPACITY = 12;
//
//    TouchTarget mFirstTouchTarget = null;
//
//    private int mViewGroupFlags;
//
//    int mChildrenCount = 0;
//
//    private View[] mChildren;
//
//    public ViewGroup(Context context) {
//        mChildren = new View[ARRAY_INITIAL_CAPACITY];
//        mChildrenCount = 0;
//    }
//
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        final int actionMasked = action & MotionEvent.ACTION_MASK;
//
//        boolean handled = false;
//
//        //check for interception
//        final boolean intercepted;
//
//        //1.判断容器自身是否要拦截事件
//        if (ev.getAction() == MotionEvent.ACTION_DOWN
//                || mFirstTouchTarget != null) {
//            //接收到初始DOWN事件或者已有mFirstTouchTarget
//            final boolean disallowIntercept = (mViewGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
//            //子View没有不允许拦截
//            if (!disallowIntercept) {
//                intercepted = onInterceptTouchEvent(ev);//容器自身是否拦截
//            } else {
//                intercepted = false;
//            }
//        } else {
//            //不是DOWN事件的时候主动进行拦截
//            intercepted = true;
//        }
//
//        boolean canceled = false;
//
//        TouchTarget newTouchTarget = null;
//        //2.如果没cancel而且容器也不拦截，分发
//        boolean alreadyDispatchedToNewTouchTarget = false; //重置
//        if (!canceled && !intercepted) {
//            // DOWN事件才分发，到MOVE事件时不再进行分发
//            if (actionMasked == MotionEvent.ACTION_DOWN) {
//                final int actionIndex = ev.getActionIndex();//always 0 for down
//                int childrenCount = mChildrenCount;
//
//                if (newTouchTarget == null && childrenCount != 0) {
//                    //从前到后遍历children，找到接收事件的view
//                    final View[] children = mChildren;
//
//                    for (int i = childrenCount - 1; i >= 0; i--) {
//                        final View child = getAndVerifyPreorderedView(
//                                preorderedList, children, childIndex);
//
//                        //1.是否可见，是否有动画；2.点击区域是否在View范围内
//                        if (!canViewReceivePointerEvents(child)
//                                || !isTransformedTouchPointInView(x, y, child, null)) {
//                            continue;
//                        }
//
//                        if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
//                            // newTouchTarget == mFirstTouchTarget
//                            newTouchTarget = addTouchTarget(child, idBitsToAssign);
//                            alreadyDispatchedToNewTouchTarget = true;
//                            break; //跳出循环
//                        }
//                    }
//                }
//            }
//        }
//
//        //3.如果拦截，则分发给容器自己处理；否则分发给mFirstTouchTarget链表头部的子View
//        if (mFirstTouchTarget == null) {
//            //没有target，则把当前容器视为普通的View
//            handled = dispatchTransformedTouchEvent(ev, canceled, null, TouchTarget.ALL_POINTER_IDS);
//        } else {
//            TouchTarget predecessor = null;
//            TouchTarget target = mFirstTouchTarget;
//            //单点触控，while只一次
//            while (target != null) {
//                final TouchTarget next = target.next; //next == null;
//                if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
//                    handled = true;
//                } else {
//                    //分发前alreadyDispatchedToNewTouchTarget被重置为false，所以MOVE事件会走到这里
//
//                    //判断cancel
//                    final boolean cancelChild = resetCancelNextUpFlag(target.child)
//                            || intercepted;
//
//                    //MOVE事件，优先分发给初始DOWN事件保存的mFirstTouchTarget
//                    if (dispatchTransformedTouchEvent(ev,canceled,target.child,target.pointerIdBits)){
//                        handled = true;
//                    }
//                    //正常情况下 cancelChild = false
//                    if (cancelChild){
//                        if (predecessor == null){
//                            mFirstTouchTarget = next;
//                        }else{
//                            predecessor.next = next;
//                        }
//                        target = next;
//                        continue;
//                    }
//                }
//                target = next;//next null，所以 while循环结束
//            }
//        }
//        return handled;
//    }
//
//    /**
//     * 把 child 装进TouchTarget，并且插到mFirstTouchTarget链表头部
//     *
//     * @param child
//     * @return mFirstTouchTarget链表
//     */
//    private TouchTarget addTouchTarget(View child, int pointerIdBits) {
//        final TouchTarget target = TouchTarget.obtain(child, pointerIdBits);
//        target.next = mFirstTouchTarget;
//        mFirstTouchTarget = target;
//        return target;
//    }
//
//    private boolean canViewReceivePointerEvents(View child) {
//
//    }
//
//    ArrayList<View> mPreSortedChildren;
//
//    /**
//     * 根据z值排序；若没有z值，则按照布局中顺序
//     *
//     * @return
//     */
//    ArrayList<View> buildOrderedChildList() {
//        final int childrenCount = mChildrenCount;
//        if (mPreSortedChildren == null) {
//            mPreSortedChildren = new ArrayList<>(childrenCount);
//        } else {
//            mPreSortedChildren.clear();
//            mPreSortedChildren.ensureCapacity(childrenCount);
//        }
//        final boolean customOrder = false;
//        for (int i = 0; i < childrenCount; i++) {
//            // add next child (in child order) to end of list
//            final int childIndex = getAndVerifyPreorderedIndex(childrenCount, i, customOrder);
//            final View nextChild = mChildren[childIndex];
//            final float currentZ = nextChild.getZ();
//
//            // insert ahead of any Views with greater Z
//            int insertIndex = i;
//            while (insertIndex > 0 && mPreSortedChildren.get(insertIndex - 1).getZ() > currentZ) {
//                insertIndex--;
//            }
//            mPreSortedChildren.add(insertIndex, nextChild);
//        }
//        return mPreSortedChildren;
//    }
//
//    /**
//     * 事件处理
//     *
//     * @return true 事件已处理 ， false 事件未被处理
//     */
//    private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean canceled, com.zhx.baselibrary.view.event.View child, int allPointerIds) {
//        final boolean handled;
//        final int oldAction = event.getAction();
//        //对于CANCEL事件的处理
//        if(canceled || oldAction == MotionEvent.ACTION_CANCEL){
//            event.setAction(MotionEvent.ACTION_CANCEL);
//            if (child == null){
//                handled = super.dispatchTouchEvent(event);
//            }else{
//                handled = child.dispatchTouchEvent(event);
//            }
//            event.setAction(oldAction);
//            return handled;
//        }
//
//        if (child == null) {
//            handled = super.dispatchTouchEvent(event);
//        } else {
//            handled = child.dispatchTouchEvent(event);
//        }
//        return handled;
//    }
//
//    /**
//     * 容器自身是否拦截事件
//     * Google工程师已经处理了一些事件冲突的情况，所以这里没有直接返回拦截或者不拦截
//     *
//     * @return
//     */
//    protected boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (ev.isFromSource(InputDevice.SOURCE_MOUSE)
//                && ev.getAction() == MotionEvent.ACTION_DOWN
//                && ev.isButtonPressed(MotionEvent.BUTTON_PRIMARY)
//                && isOnScrollbarThumb(ev.getX(), ev.getY())) {
//            return true;
//        }
//        return false;
//    }
//
//    private static final class TouchTarget {
//        private static final int MAX_RECYCLED = 32;
//        private static final Object sRecycleLock = new Object[0];
//        private static TouchTarget sRecycleBin;
//        private static int sRecycledCount;
//
//        public static final int ALL_POINTER_IDS = -1; // all ones
//
//        // The touched child view.
//        public View child;
//
//        // The combined bit mask of pointer ids for all pointers captured by the target.
//        public int pointerIdBits;
//
//        // The next target in the target list.
//        public TouchTarget next;
//
//        private TouchTarget() {
//        }
//
//        public static TouchTarget obtain(View child, int pointerIdBits) {
//            if (child == null) {
//                throw new IllegalArgumentException("child must be non-null");
//            }
//
//            final TouchTarget target;
//            synchronized (sRecycleLock) {
//                if (sRecycleBin == null) {
//                    target = new TouchTarget();
//                } else {
//                    target = sRecycleBin;
//                    sRecycleBin = target.next;
//                    sRecycledCount--;
//                    target.next = null;
//                }
//            }
//            target.child = child;
//            target.pointerIdBits = pointerIdBits;
//            return target;
//        }
//
//        public void recycle() {
//            if (child == null) {
//                throw new IllegalStateException("already recycled once");
//            }
//
//            synchronized (sRecycleLock) {
//                if (sRecycledCount < MAX_RECYCLED) {
//                    next = sRecycleBin;
//                    sRecycleBin = this;
//                    sRecycledCount += 1;
//                } else {
//                    next = null;
//                }
//                child = null;
//            }
//        }
//    }
//}
