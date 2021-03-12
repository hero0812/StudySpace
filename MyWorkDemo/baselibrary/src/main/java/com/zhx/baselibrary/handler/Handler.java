package com.zhx.baselibrary.handler;

import android.os.SystemClock;
import android.util.Log;

/**
 * 成员属性，构造函数，核心方法
 */
public class Handler {

    interface Callback {
        boolean handleMessage(Message msg);
    }

    public void handleMessage(Message msg) {

    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

    private static void handleCallback(Message msg) {
        msg.callback.run();
    }

    final Looper mLooper;
    final MessageQueue mQueue;
    final Callback mCallback;
    final boolean mAsynchronous;//可见一个Handler只能发出一种类型的msg

    public Handler() {
        this(null, false);
    }

    public Handler(Looper looper) {
        this(looper, null, false);
    }

    public Handler(Callback callback, boolean isAsync) {
        //检查Thread Looper
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            //Thread中必须调用Lopper.prepare()之后才会创建Looper对象
            throw new RuntimeException("Can't create handler inside thread " + Thread.currentThread()
                    + " that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = isAsync;
    }

    public Handler(Looper looper, Callback callback, boolean async) {
        mLooper = looper;
        mQueue = looper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    public Message obtainMessage() {
        return Message.obtain(this);
    }

    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postDelay(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postDelay(Runnable r, Object token, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r, token), delayMillis);
    }

    //把post Runnable方式转化为send message方式
    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.callback = r;
        m.obj = token;
        return m;
    }

    public final boolean sendMessage(Message m) {
        return sendMessageDelayed(m, 0);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            Log.w("Looper", this + " sendMessageAtTime() called with no mQueue");
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    /**
     * 设定msg target为当前handler对象
     * 设置同步/异步标识
     */
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeCallbacksAndMessages(Object token) {
        mQueue.removeCallbacksAndMessages(this, token);
    }
}
