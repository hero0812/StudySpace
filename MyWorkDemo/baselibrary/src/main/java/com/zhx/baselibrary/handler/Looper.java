package com.zhx.baselibrary.handler;

import android.os.Trace;

public class Looper {
    final MessageQueue mQueue;
    final Thread mThread;

    //调用prepare()之前，sThreadLocal.get()返回null
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

    private static Looper sMainLooper;

    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }

    public static void prepare() {
        prepare(true);//子线程创建的Lopper是可quit的
    }

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            //当前线程已经创建了Looper，不能多次创建
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(quitAllowed));
    }

    public static void prepareMainLooper() {
        prepare(false);//主线程Looper不可quit
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    public static Looper getMainLooper() {
        synchronized (Looper.class) {
            return sMainLooper;
        }
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        for (; ; ) {
            Message msg = queue.next();//might block
            if (msg == null) {
                // next()方法没有msg的话会阻塞；当Looper.quit()调用后 ，mMessages消息队列清空，loop for循环退出
                return;
            }

            msg.target.dispatchMessage(msg);

            msg.recycleUnchecked();
        }

    }
}
