package com.zhx.baselibrary.handler;

public final class Message {

    private static final int MAX_POOL_SIZE = 50;

    //flag -- in use
    static final int FLAG_IN_USE = 1 << 0;

    //flag -- 异步消息
    static final int FLAG_ASYNCHRONOUS = 1 << 1;

    public int what;

    public Object obj;

    int arg1;

    long when;

    //in-use、asynchronous 等flag
    int flags;

    Runnable callback;

    Handler target;

    Message next;

    static final Object sPoolSync = new Object();

    static Message sPool;

    private static int sPoolSize = 0;

    public Message() {
    }

    /**
     * 享元模式， 从全局的缓存池中获取一个新的Message对象，缓存池为空才新创建Message对象
     *
     * @return
     */
    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                //取出链表头部节点，清除in-use flag
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0;
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Handler h){
        Message m = obtain();
        m.target = h;
        return m;
    }

    public void setAsynchronous(boolean async) {
        if (async) {
            flags |= FLAG_ASYNCHRONOUS;
        } else {
            flags &= ~FLAG_ASYNCHRONOUS;
        }
    }

    boolean isAsynchronous(){
        return ((FLAG_ASYNCHRONOUS & flags) != 0);
    }

    boolean isInUse() {
        return (FLAG_IN_USE & flags) == FLAG_IN_USE;
    }

    void markInUse() {
        flags |= FLAG_IN_USE;
    }

    public void recycle(){
       recycleUnchecked();
    }

    //重置内存
    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        obj = null;
        when = 0;
        target = null;
        callback = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
}
