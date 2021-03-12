

## 【A】Handler专题

### SelfCheck

- **Looper和handler的关系，handler原理，looper里的无限循环return之后，什么时候再进行检测待发送的消息。无消息阻塞为何不会卡顿，Looper如何被唤醒?** 
- IdleHandler(闲时机制）；
- **主线程中的Looper.loop()一直无限循环为什么不会造成ANR？** **Looper一直循环查消息为何没卡主线程？**
- ThreadLocal底层原理和Handler的关系

### 		



### Handler的总体架构设计

Handler机制工作流程可以形象比作“传送带”：

![](.\images\handler_00.png)

handler ： 发送和分发消息

looper   :     用于轮询消息队列，一个线程只有一个looper

Message ： 消息实体

MessageQueue：消息队列，用于存储消息和管理消息



### 

​		生产者-消费者模型：生产者和消费者在同一时间段内共同用同一个存储空间，生产者往存储空间中添加数据。消费者从存储空间中取走数据。

![handler_01](.\images\handler_01.png)

​		

这样设计好处：

	1. 利用MessageQueue先进先出的特性，保证生产消费顺序
	2. 解耦，生产者消费者之间不会互相持有。





### handler源码分析

#### 整体流程

​	handler内部持有looper和messagequeue的引用，我们sendMessageDelayed、sendMessageAtTime、再调到enqueueMessage方法，此时将msg.target = this; this即代表发送这个message的Handler 对象本身。最终由MessageQueue对象的queue.enqueueMessage(msg, uptimeMillis) 加入消息队列。再由Looper对象，不断轮询，从MessageQueue中取出该处理的Message，直接回调到上边设置的msg.target的Handler对象的dispatchMessage()方法。

​	    另外我们常用Handler 的 post(Runnanle)、sendMessage（Message）两种方法，他们最终都由MessageQueue对象queue.enqueueMessage(msg, uptimeMillis) 加入消息队列。只不过post(Runnable)方法会将Runnable对象赋值给Message的callback成员属性，Looper对象从MessageQueue中取出Message，回调到上边设置的msg.target.dispatchMessage()方法, 也就是Handler的dispatchMessage方法，在该方法中做了针对处理，callback 不为空，则执行Runnable的run方法，否则执行handleMessage方法。

​	以上只是整体的流程，有很多源码中的实现细节和关键点，需要详细梳理一下。



#### Message 数据结构

​	Message为单链表实现的优先级队列，Handler本质是内存共享，也就是一个个Message对象的线程间共享。

伪代码：

```java
	public final class Message {
		public int what;
		public int arg1; 
		public int arg2;
		public Object obj;
		
		long when;		//按执行时间先后顺序进行插入排序，取出时总是取头节点Message，即执行时间临近的哪个，所以是优先级队列
 	
		Message next;	//是为单链表特征
		....
	}
```

**Message的创建**

​	由于应用场景中，常常会有短时间内，发送大量Message的情境。使用Message时应该通过它提供的obtain方法来获取Message实例。该方法内部通过 享元模式 实现Message对象内存复用，防止内存碎片 - 内存抖动 - OOM。

```java
/**
 * 对象锁
 */
private static final Object sPoolSync = new Object();
/**
 * 全局静态的Message对象缓存池（链表结构）
 */
private static Message sPool;
/**
 * 从全局的缓存池中获取一个新的Message对象
 */
public static Message obtain() {
    synchronized (sPoolSync) {
        if (sPool != null) {
            Message m = sPool;	//头节点出队,return 备用
            sPool = m.next;		//头节点指针后移
            m.next = null;		//属性重置
            m.flags = 0; // clear in-use flag 属性重置
            sPoolSize--; 
            return m;
        }
    }
    return new Message();
}
```



#### 入口 - - Handler 发送与处理消息

​	Handler工作由sendMessage开始 -  ........ - 直到 handleMessage()方法结束，与 其成员属性 Looper和MessageQueue协同工作。

  1. Handler是如何持有Looper、MessageQueue对象的引用的 -- Handler构造方法

     ```java
     /**
     *	参数 async ：默认false 即Handlers是同步的，全局遵循全局Messages顺序。
     				当设置成true时，handlers发消息前调用Message对象的setAsynchronous()方法。异步消息则表示全局messags的同步顺序。异步消息不遵守同步屏障，同步屏障相关内容下面会讲到。
     *
     */
     public Handler(Callback callback, boolean async) {
         // ...省略无关代码
         mLooper = Looper.myLooper();
         if (mLooper == null) {
             throw new RuntimeException(
                 "Can't create handler inside thread that has not called Looper.prepare()");
         }
         mQueue = mLooper.mQueue;
         mCallback = callback;
         mAsynchronous = async;
     }
     ```

  2. 消息的发送过程 

     ![handler](.\images\handler.png)

     Handler的enqueueMessage()方法:

     ```java
        private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
            	// 我们通过api调用sendXXX或者postXXX方法，最终在此都会将Handler本身设置给message.target, 取出消息时候会回调
             msg.target = this;	
             if (mAsynchronous) {
                 msg.setAsynchronous(true);
             }
             return queue.enqueueMessage(msg, uptimeMillis);
         }
     ```

     ​	

  3. 消息的处理过程

     ​     通常，我们从子线程构建要发送的消息Message对象，通过主线程的handler.sendMessage发送消息，其内部消息入队前将Handler本身设置给message.target属性, MessageQueue.next()将消息从队列取出的时候会回调message.target.dispatchMessage() 也就是主线程的Handler.dispatchMessage(Message)方法，从而实现在主线程处理子线程发来的Message，进行更新UI等操作，是为“子线程-主线程切换” 。  

     Handler.java 分发、处理消息通过dispatchMessage()方法：

     ```java
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
     ```

​	

​	Handler 调用消息入队之后发生了什么？消息在队列之中又是怎样的管理机制？



> **handler泄漏的引用链。**最终是谁持有的activity，handler内存泄露 

```
Handler 匿名内部类 持有外部类 Activity的引用

Handler发送msg的时候, enqueueMessage() 有 msg.target = this ;这个this正是Handler对象自己；

msg可以长时间存在于内存（延迟消息），那么 因为 msg对象持有handler对象，handler对象持有Activity对象，GC可达性算法认为 Activity 不满足gc条件，不能回收。造成内存泄漏。
```





#### MessageQueue的职责

​		MessageQueue是单链表实现的优先级队列，职责是管理和维护Message的秩序。

​		

 1. ##### 消息入队

    ```java
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
    ```







 1. ##### 消息出队

    ```java
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
    ```
    



子线程中维护的Looper，消息队列无消息的时候应该如何处理？





##### MessageQueue睡眠和唤醒机制

有两种情境需要进入等待：
1） msg 不到执行时间 ， 到预设时间会自动唤醒

2） msg 队列为空 ， ptr = -1 ，在MessageQueue#enqueueMessage() 中通过nativeWake(mptr) 主动唤醒

```java
//ptr :在MessageQueue构造方法中通过nativeInit()初始化；-1则表示无限等待，直到有事件发生为止；如果值为0，表示线程已退出销毁
private native void nativePollOnce(long ptr,int timeoutMillis); 
```

>
> ​    nativePollOnce/ nativeWake代码：

```c++

void NativeMessageQueue::pollOnce(JNIEnv* env, jobject pollObj, int timeoutMillis) {
    mPollEnv = env;
    mPollObj = pollObj;
    mLooper->pollOnce(timeoutMillis);
    mPollObj = NULL;
    mPollEnv = NULL;

    if (mExceptionObj) {
        env->Throw(mExceptionObj);
        env->DeleteLocalRef(mExceptionObj);
        mExceptionObj = NULL;
    }
}

```


​    
​    mLooper->pollOnce  ==>   Looper::pollInner  ==> epoll_wait(mEpollFd, eventItems, EPOLL_MAX_EVENTS, timeoutMillis)
​    

    走到Linux epoll 调用进入等待，epoll是Linux系统中 IO 复用模式的调用，直到nativeWake 写入一个 IO 操作到描述符唤醒等待。

>
> ​    Looper.cpp 中唤醒函数：

```c++
    void Looper::wake() {
        uint64_t inc = 1;
        ssize_t nWrite = TEMP_FAILURE_RETRY(write(mWakeEventFd, &inc, sizeof(uint64_t)));
        if (nWrite != sizeof(uint64_t)) {
            if (errno != EAGAIN) {
                LOG_ALWAYS_FATAL("Could not write wake signal to fd %d: %s",
                        mWakeEventFd, strerror(errno));
            }
        }
    }
```





​    消息出队列的方法是由Looper对象在loop()方法中调用的。而Lopper的创建与运行，从系统角度上来看，才是Handler机制的起点。



#### Looper的创建与运行

​		点击应用图标，launcher ->  zygote -> jvm ->  ActivityThread.main() ，在此处主线程的Looper 即 "Main Looper"被创建并轮询起来。

> ActivityThread.main()方法 ：

```java
public static void main(String[] args) {
    // ... 省略无关代码
 	Looper.prepareMainLooper(); //创建Main Looper对象、Looper构造方法中创建消息队列
    
    ActivityThread thread = new ActivityThread();
    thread.attach(false);	//application 启动

    if (sMainThreadHandler == null) {
        sMainThreadHandler = thread.getHandler();
    }

    if (false) {
        Looper.myLooper().setMessageLogging(new
                LogPrinter(Log.DEBUG, "ActivityThread"));
    }
    Looper.loop(); //Looper开始轮询主线程的消息队列
}
```



Looper对象在Looper.prepare方法中创建。

> Looper.prepareMainLooper() ：
>

```java

/**
*	ThreadLocal对象是全局唯一的，用来保存线程间隔离的线程上下文数据
*/
 static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

 private static Looper sMainLooper;  // Looper.class 类锁，保证sMainLooper线程安全


/**
* 通过ThreadLocal 和 synchronized 保证looper 是线程唯一的
*
*/
public static void prepareMainLooper() {
      prepare(false); //主线程消息队列不可以销毁
      synchronized (Looper.class) {
         if (sMainLooper != null) {
             throw new IllegalStateException("The main Looper has already been prepared.");
         }
         sMainLooper = myLooper();
      }
 }

/**
*	quitAllowed 标识消息队列是否可以销毁，默认是true可以销毁
*
*/
private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) { //这里保证一个线程只创建一个Looper，否则抛异常
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}


public static @Nullable Looper myLooper() {
     return sThreadLocal.get();
}


```
在Looper的构造方法中，创建MessageQueue消息队列，也就是说MessageQueue也是线程唯一的。

```java
private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```



> 怎样获取当前线程是否是主线程

```
Looper.getMainLooper().getThread() != Thread.currentThread()
或
Looper.myLooper() == Looper.getMainLooper()
```



##### **ThreadLocal的作用**

每个Thread有一个 

```
ThreadLocal.ThreadLocalMap threadLocals = null;
```

ThreadLocal封装了set、get方法，内部通过Thread.currentThread获取到对应的Thread及其ThreadLocal.ThreadLocalMap。这个ThreadLocal.ThreadLocalMap 以key-value形式，保存线程用到的各种变量的。其中key是ThreadLocal<?> 对象自己，通过指定范型表示ThreadLocal 是哪一种线程本地变量比如ThreadLocal<Looper>，如此可以实现ThreadLocalMap 为线程创建多个线程本地变量。value则保存着各个线程各自使用的对象。



Looper类中 ThreadLocal<Looper>对象是静态的，也就实现了虽然各个线程的looper对象是不同的，但是key都是同一个sThreadLocal对象。

```
static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
```



```java
public void set(T looper) { 
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);//先走createMap(t，looper)创建
    if (map != null)
        map.set(this, looper);//ThreadLocalMap 不为空则以当前ThreadLocal对象为key，looper对象为value保存
    else
        createMap(t,  looper);//创建了ThreadLocalMap后，赋值给了当前线程对象的threadLocals成员属性，以后通过getMap获取
}

void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
}

ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
}


public T get() {
   Thread t = Thread.currentThread();
   ThreadLocalMap map = getMap(t);
   if (map != null) {
      ThreadLocalMap.Entry e = map.getEntry(this);  //set的时候是以ThreadLocal对象为key，looper对象为value保存的
       	if (e != null) {
             @SuppressWarnings("unchecked")
             T result = (T)e.value;
             return result;
        }
      }
   return setInitialValue();
}
```



值得提一下的是，ThreadLocalMap 内部用以保存数据的Entry[] ，其Entry 类是专门用来保存ThreadLocal对象的虚引用类型的子类，

```java
static class Entry extends WeakReference<ThreadLocal<?>> {
    /** The value associated with this ThreadLocal. */
    Object value;

    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
}
```



> ThreadLocal底层原理和Handler的关系
>

ThreadLocal用来保证一个线程只有一个Looper

Handler构造方法里需要通过myLooper()获取到当前线程对应的looper，如果把所有的looper单纯放在一张大的Map里，即使加同步锁，仍会有性能问题。ThreadLocal通过隔离的方式，使得每个线程只能访问属于自己的那一份上下文数据，避免线程间冲突造成安全问题。

```
public static Looper myLooper() {
    return sThreadLocal.get();
}
```



**loop() 方法** -- Handler机制地永动机

```java
public static void loop() {
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    final MessageQueue queue = me.mQueue;

    // Make sure the identity of this thread is that of the local process,
    // and keep track of what that identity token actually is.
    Binder.clearCallingIdentity();
    final long ident = Binder.clearCallingIdentity();

    for (;;) {
        Message msg = queue.next(); // might block
        if (msg == null) {
            // 当Looper.quit()调用后 ， message被置为null，for循环退出
            return;
        }

        // This must be in a local variable, in case a UI event sets the logger
        final Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " +
                    msg.callback + ": " + msg.what);
        }

        final long traceTag = me.mTraceTag;
        if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
            Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
        }
        try {
            msg.target.dispatchMessage(msg);
        } finally {
            if (traceTag != 0) {
                Trace.traceEnd(traceTag);
            }
        }

        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }

        // Make sure that during the course of dispatching the
        // identity of the thread wasn't corrupted.
        final long newIdent = Binder.clearCallingIdentity();
        if (ident != newIdent) {
            Log.wtf(TAG, "Thread identity changed from 0x"
                    + Long.toHexString(ident) + " to 0x"
                    + Long.toHexString(newIdent) + " while dispatching to "
                    + msg.target.getClass().getName() + " "
                    + msg.callback + " what=" + msg.what);
        }

        msg.recycleUnchecked();
    }
}
```
​	



****







#### 同步屏障机制

​		同步屏障，顾名思义是“屏蔽（推迟）同步消息，保证异步消息优先执行的机制”。

​        Handler中消息有两种 -- 同步消息和异步消息。可由Handler构造方法 async 参数指定。默认是同步消息，即发送后严格按顺序同步执行。同步消息要服从同步屏障机制，即当消息是从MessageQueue.postSyncBarrier()发送的时候，后续所有同步消息立即推迟直到满足某个条件或者屏障解除。

​       而异步消息则它不受同步屏障的约束（照常执行），它们通常代表着必须被独立处理的中断、输入事件或者其他信号，甚至即使其他工作已被暂停的情况下。要注意的是，异步消息有可能是不按顺序分发的，如果消息执行顺序很重要的话，这一点要慎重考虑。

​		*同步屏障机制整体流程是*：只要有一个屏障消息为第一个消息 ，那么相当于再这个时间添加了屏障（这里不会主动唤醒线程） ，那么后面只要后面入队的消息为异步消息 都优先执行，没有则一直阻塞，如果这个时候一个普通消息sendMessageDelayed(getPostMessage, 0)入队 会触发唤醒线程，有异步消息 则取出此异步消息返回 然后继续阻塞线程 ， 直到移除屏障消息(这里才会触发唤醒线程)。没有异步消息 则取出这个普通消息 返回



​		下面分析同步屏障在MessageQueue源码中实现:

```java

public int postSyncBarrier() {
    return postSyncBarrier(SystemClock.uptimeMillis());
}

private int postSyncBarrier(long when) {
    // 不需要唤醒锁，因为同步屏障的目的就是要推迟 
    synchronized (this) {
        final int token = mNextBarrierToken++; //一个新的同步屏障token，标记当前屏障的数量与flag
        final Message msg = Message.obtain();	//添加一个Message ，注意这里没有设置target
        msg.markInUse();
        msg.when = when;
        msg.arg1 = token;
			
        Message prev = null;
        Message p = mMessages;
        if (when != 0) {
            while (p != null && p.when <= when) {
                prev = p;
                p = p.next;
            }
        }
        if (prev != null) { 
            msg.next = p;
            prev.next = msg;
        } else {
            msg.next = p;
            mMessages = msg;
        }
 
        //给这个msg按时间顺序添加到消息队列
        //这里没有用nativeWake(mPtr) 来唤醒线程（当主线程阻塞状态 才会触发nativeWake）。
        return token;
    }
}
```







##### 同步屏障应用场景

​		比如 View#invalidate 调用后，后续的messages将被同步屏障截断，直到下一帧准备好绘制。同步屏障保证了绘制请求完全处理完成之后再恢复。

> ViewRootImpl :

  		reqeustLayout和invalidate方法都有调用到scheduleTraversals()方法

```java
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        checkThread();
        mLayoutRequested = true;
        scheduleTraversals();
    }
}

 void invalidate() {
        mDirty.set(0, 0, mWidth, mHeight);
        if (!mWillDrawSoon) {
            scheduleTraversals();
        }
 }
```
```java
  

/**
*	安排遍历视图
*/
   void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier(); //添加同步屏障
            //申请VSYNC信号，Choreographer类职责是控制动画、输入、绘制的时机。接收垂直同步的时间脉冲，准备下一帧的内容。
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }
```



VSYNC信号申请到之后，mTraversalRunnable的run将被执行，其中只有一个方法调用-- doTraversal()：

```java
void doTraversal() {
    if (mTraversalScheduled) {
        mTraversalScheduled = false;
      //移除消息屏障  
     mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
        if (mProfile) {
            Debug.startMethodTracing("ViewAncestor");
        }
        performTraversals(); //至此就到了熟悉的performLayout、performMessure、performDraw方法
        if (mProfile) {
            Debug.stopMethodTracing();
            mProfile = false;
        }
    }
}
```









### HandlerThread & IntentService

HandlerThread 内部帮我们创建了Looper ， 且保证线程安全。

> HandlerThread#run()

```java
   @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
```

Thread.start()调用后，不会立即调用run() ，所以IntentService要使用Looper的话，需要wait()等待：

```java
public Looper getLooper() {
    if (!isAlive()) {
        return null;
    }
    
    // If the thread has been started, wait until the looper has been created.
    synchronized (this) {
        while (isAlive() && mLooper == null) {
            try {
                wait();//释放锁
            } catch (InterruptedException e) {
            }
        }
    }
    return mLooper;
}
```


IntentService是 后台运行的，自带HandlerThread 执行异步任务的 service；

> 其onCreate方法中，创建并启动了HandlerThread：

```java
 HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
 thread.start();
 //thread start()后不会立即run所以getLooper()方法内部使用了wait()，直到HandlerThread#run()执行，Looper被初始化并运行
 mServiceLooper = thread.getLooper();
 mServiceHandler = new ServiceHandler(mServiceLooper);
```





> 在onStart()中 ，mServiceHandler发送消息

```java
   @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

```



> ServiceHandler#handleMessage():

调用者只需在重写 onHandleIntent((Intent)msg.obj) 中处理异步任务即可，IntentService#stopSelf会自动调用，任务结束后销毁。

```java
public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
}
```





### Interview & Exercise

- **Handler的post(Runnable)与sendMessage有什么区别？****

  两者都会调到sendMessageDelayed、sendMessageAtTime、enqueueMessage方法，最终都由MessageQueue对象queue.enqueueMessage(msg, uptimeMillis) 加入消息队列。只不过post(Runnable)方法会将Runnable对象赋值给Message的callback成员属性，等到Message被Looper从MessageQueue中取出时，回调并执行：

```java
public final boolean post(Runnable r){
   return  sendMessageDelayed(getPostMessage(r), 0);
}

private static Message getPostMessage(Runnable r) {
     Message m = Message.obtain(); //post跟sendMessage一样都是获得并发送一个Message对象
     m.callback = r;  //只不过Runnable对象 r ，被赋值给Message的callback成员变量
     return m;
}
```

```java
/**
*  等到Looper.loop()中取出Message后，调用到msg.target.dispatchMessage() 即 发送者Handler的dispatchMesasge方法
*/
public void dispatchMessage(Message msg) {
    if (msg.callback != null) {  //分发Message时，callback若不为空，则直接调用其run方法
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

 private static void handleCallback(Message message) {
        message.callback.run();  // Runnable的run被执行
 }
```



- **Handler 发送延迟消息或者延迟任务 的sendMessageDelayed和postDelayed是如何实现的？**

​      都会走到，Hanlder#sendMessageDelayed(Message msg, long delayMillis)  ==》 sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis) 当前时间加上要延迟的时间，计算出真实Message执行时间。进而传给MessageQueue#enqueueMessage(Message msg , long when) ; 就是这里的when

> MessageQueue#enqueueMessage(Message msg , long when)  关键代码 ：

```java
 			if (p == null || when == 0 || when < p.when) {
          		//要立刻执行的消息
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                //延时消息的逻辑，就是从消息队列头部向后遍历，依次比对when大小，然后插入节点中
                Message prev;
                for (;;) {
                    prev = p;	//双指针遍历链表
                    p = p.next;
                    if (p == null || when < p.when) { //插入的消息执行时间（when）在前，则跳出for循环
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; //执行节点插入
                prev.next = msg;
            }
```







- **为什么主线程可以new Handler? 如果想在子线程中new Handler要怎么做？**

  >  不能。"Can't create handler inside thread that has not called Looper.prepare()"
  >
  > Looper.prepare();
  >
  > Looper.loop();

  

- **子线程中维护的Looper，消息队列无消息的时候处理方案是什么？有什么用?**

  > 调用 Looper.quit(); 方法

  ​	Looper.quit()方法 内部调用了 MessageQueue的 quit(boolean); 方法: 

```java
     void quit(boolean safe) {
        if (!mQuitAllowed) {
            throw new IllegalStateException("Main thread not allowed to quit.");
        }

        synchronized (this) {
            if (mQuitting) {
                return;
            }
            mQuitting = true;

            if (safe) {
                removeAllFutureMessagesLocked();
            } else {
                removeAllMessagesLocked();
            }

            // We can assume mPtr != 0 because mQuitting was previously false.
            nativeWake(mPtr);
        }
    }

  // 直接重置掉当前线程的Message队列中所有消息
  private void removeAllMessagesLocked() {
        Message p = mMessages;
        while (p != null) {
            Message n = p.next;
            p.recycleUnchecked();
            p = n;
        }
        mMessages = null;
  }
```




- **既然可以存在多个Handler往MessageQueue中添加数据（各Handler可能处于不同线程），那它内部是如何保证线程安全？ 取消息呢？**

  > 通过内置锁 synchronized 

   一个线程只有一个MessageQueue对象，MessageQueue 的enqueueMessage（）和next() 都由synchronized (this) 锁进行和线程同步，保证线程安全。

  




- **looper里的无限循环return之后，什么时候再进行检测待发送的消息。无消息阻塞为何不会卡顿，Looper如何被唤醒**

  Looper#loope() 中 for循环轮询 MessageQueue#next()  ， MessageQueue#next()方法中有for循环轮询Message链表。

  

   MessageQueue的for循环，在消息队列为空或者未到下一条消息执行时间的时候，调用 nativePollOnce(ptr, nextPollTimeoutMillis); ==》  epoll_wait()。epoll+pipe机制，使得主线程在阻塞的时候，让出CPU资源，同时等待新消息。在 MessageQueue#enqueueMessage()中，nativeWake()唤醒。



​       而Looper中的for循环，是Java层面上的，保证Android 主线程能够一直运行在JVM中，不退出的一种机制。



- **主线程中的Looper.loop()一直无限循环为什么不会造成ANR？ Looper一直循环查消息为何没卡主线程？**

  

  

  

  

- **整理一下 MessageQueue队列处理机制，在fragment生命周期管理中的应用**

  

  

  

  

  













### 参考资源

- 享学课堂 -- Handler

-  源码分析_Android UI何时刷新_Choreographer

  https://www.jianshu.com/p/d7be5308d06e

- 同步屏障的理解

  https://blog.csdn.net/lggisking/article/details/108091203

- Android工程师进阶34讲 -- 15分钟掌握Handler

  https://kaiwu.lagou.com/course/courseInfo.htm?sid=&courseId=67&lagoufrom=noapp#/detail/pc?id=1877
  
  































