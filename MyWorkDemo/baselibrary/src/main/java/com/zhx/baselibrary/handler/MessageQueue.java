package com.zhx.baselibrary.handler;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

public class MessageQueue {

    private final boolean mQuitAllowed;
    private long mPtr;

    Message mMessages;

    private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<>();

    private IdleHandler[] mPendingIdleHandlers;

    private boolean mQuitting;

    //下一个barrier token
    //屏障指的是target为null的message ， arg1 携带token
    private int mNextBarrierToken;

    //Indicates whether next() is blocked waiting in pollOnce() with a non-zero timeout;
    //next()函数是否正被阻塞，等待pollOnce()一定时间后超时唤醒
    private boolean mBlocked;

    MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
    }

    public int postSyncBarrier() {
        return postSyncBarrier(SystemClock.uptimeMillis());
    }

    /**
     * 发送内存屏障消息
     *
     * @param when
     * @return
     */
    private int postSyncBarrier(long when) {
        synchronized (this) {
            final int token = mNextBarrierToken++;
            final Message msg = Message.obtain();
            msg.markInUse();
            msg.when = when;
            msg.arg1 = token;

            Message prev = null;
            Message p = mMessages;
            //比较消息队列中消息计划执行时间when ， 将屏障消息插入合适位置
            //如果屏障 when = 0 ，直接插到消息队列头部
            if (when != 0) {
                while (p != null && p.when <= msg.when) {
                    prev = p;
                    p = p.next;
                }
            }
            if (prev != null){//插入中间某个节点
                msg.next = p;
                prev.next = msg;
            }else{ //头插
                msg.next = p;
                mMessages = msg;
            }
            return token;
        }
    }

    boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()){
            throw new IllegalStateException(msg + " This message is already in use.");
        }
        synchronized (this){
            if (mQuitting){
                msg.recycle();
                return false;
            }
            msg.markInUse();
            msg.when = when;
            //mMessages代表当前消息队列头节点（优先级最高）的消息，mMessages为null表示消息队列是空闲的
            Message p = mMessages;
            boolean needWake;
            if (p == null || when == 0 || when < p.when){
                //消息队列为空或msg是需要立即执行的消息，直接插到或者创建新的头节点，如果queue阻塞则唤醒之
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;//如果正被pollOnce()阻塞，则需要唤醒
            }else {
                // 一般情况。将这个消息插入到消息队列中间
                // 通常不用唤醒队列，除非队列头有同步屏障，并且msg是一个异步消息。
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;){
                    prev = p;//找到该插入的位置前置节点
                    p = p.next;
                    if (p == null || when < p.when){
                        break;
                    }
                    //msg之前已经有优先级更高的别的异步消息，老实排着就行了，不用唤醒
                    if (needWake && p.isAsynchronous()){
                        needWake = false;
                    }
                }
                msg.next = p;
                prev.next = msg;
            }

            if (needWake){
                nativeWake(mPtr);
            }
        }
        return true;
    }

    public Message next() {
        int pendingIdleHandlerCount = -1;
        int nextPollTimeoutMillis = 0;

        for(;;){
            //如果消息队列为空或者当透节点msg没到执行时间
            // 计算唤醒时间，并调用nativePollOnce方法（底层调用系统epoll_wait）进行阻塞等待。
            nativePollOnce(mPtr,nextPollTimeoutMillis);

            synchronized (this){
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg  = mMessages;

                //如果队列头部是同步屏障消息，则从队列里找异步消息来处理
                if (msg != null && msg.target == null){
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    }while(msg != null && !msg.isAsynchronous());
                }

                //通常地，不管是msg是同步消息还是异步消息都会走这里
                if (msg != null){
                    if (now < msg.when){
                        // 没到msg执行时间，将使queue进入睡眠，计算超时唤醒时间nextPollTimeoutMillis
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now , Integer.MAX_VALUE);
                    }else{
                        //拿到了要执行的message
                        mBlocked = false;
                        if (prevMsg != null){//
                            prevMsg.next = msg.next; //msg是队列中间的异步消息
                        }else{
                            mMessages = msg.next;//msg是队列头的消息
                        }
                        msg.next = null;
                        msg.markInUse();;
                        return msg;
                    }
                }else{
                    //MessageQueue为空，没有更多消息，无限等待
                    nextPollTimeoutMillis = -1;
                }

                //pendingIdleHandlerCount < 0表示第一次idle，mMessages == null || now < mMessages.when表示确实没有消息要立刻处理
                if (pendingIdleHandlerCount < 0 && (mMessages == null || now < mMessages.when)){
                    pendingIdleHandlerCount = mIdleHandlers.size();
                }

                //确实没有IdleHandler ， 则进入阻塞
                if (pendingIdleHandlerCount <= 0){
                    mBlocked = true;
                    continue;
                }

                if (mPendingIdleHandlers == null){
                    mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount,4)];
                }
                mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);

                //run idle handlers
                for (int i = 0; i < pendingIdleHandlerCount; i++) {
                    final IdleHandler idler = mPendingIdleHandlers[i];
                    mPendingIdleHandlers[i] = null; // release the reference to the handler

                    boolean keep = false;
                    try {
                        keep = idler.queueIdle();
                    } catch (Throwable t) {
                    }

                    if (!keep) {
                        synchronized (this) {
                            mIdleHandlers.remove(idler);
                        }
                    }
                }
                // Reset the idle handler count to 0 so we do not run them again.
                pendingIdleHandlerCount = 0;
                // While calling an idle handler, a new message could have been delivered
                // so go back and look again for a pending message without waiting.
                nextPollTimeoutMillis = 0;
            }
        }
    }

    void removeCallbacksAndMessages(Handler handler, Object o) {

    }

    /**
     * 判定当前lopper是否空闲（没有消息要处理）
     *
     * @return
     */
    public boolean isIdle() {
        synchronized (this) {
            final long now = SystemClock.uptimeMillis();
            return mMessages == null || now < mMessages.when;
        }
    }


    public interface IdleHandler {
        /**
         * return true to keep your idle handler active , false to have it removed;
         */
        boolean queueIdle();
    }

    private native static long nativeInit();

    private native void nativePollOnce(long ptr, int timeoutMillis);/*non-static for callbacks*/

    private native static void nativeWake(long ptr);
}
