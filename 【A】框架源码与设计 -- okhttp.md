## 【A】框架源码与设计 -- okhttp



### 输出倒逼输入

- okhttp讲讲事件处理机制 ，okhttp线程池怎么实现的
- 你们用的 okhttp ？那你有没有做过一些网络优化呢？比如弱网环境。
- okhttp头如果有特殊字符如何处理等等
- okhttp支持HTTP2？http2的功能有哪些？
- 网络请求涉及，如何设计缓存

  




### 基本使用

![image003.png](https://s0.lgstatic.com/i/image/M00/0D/2B/Ciqc1F7Dk--AZk_0AADHHDXvjvc960.png)

![image001.png](https://s0.lgstatic.com/i/image/M00/0D/2B/Ciqc1F7Dk-eAItVSAADuEPXjYPo944.png)



client.newCall().enqueue()方法开始：

### 网络请求流程

okHttpClient将任务调度委托给Dispatcher对象

```
client.dispatcher().enqueue(new AsyncCall(callback));
```

**Dispatcher**是okhttp内部的门面类   ，主要用来实现执行、取消异步请求操作。本质上是内部维护了一个线程池去执行异步操作，并且在 Dispatcher 内部根据一定的策略，保证最大并发个数、同一 host 主机允许执行请求的线程个数等。





将请求封装成AsyncCall ， AsyncCall实现了Runnanle接口。**Dispatcher** enqueue()即是使用内部线程池执行该任务。

```java
synchronized void enqueue(AsyncCall call){
  	if(runningAsyncCalls队列未满 && 未达到单host最大请求限制){
      runningAsyncCalls.add(call); //放入runningAsyncCalls队列
  		executorService().execute(call); 
    }else{
      readyAsyncCalls.add(call); //放入readyAsyncCalls就绪队列
    } 	
}
```

![image009.png](https://s0.lgstatic.com/i/image/M00/0D/2B/Ciqc1F7DlAeAY_UjAADrZmzFV74873.png)



Dispatcher内部线程池配置：

```java
public synchronized ExecutorService executorService() {
  if (executorService == null) {
    executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
  }
  return executorService;
}
```



AsyncCall实现了Runnanle接口，当任务执行时，Runnanle#run()方法被调度，通过 **getResponseWithInterceptorChain()** 方法，各个拦截器组成网络请求责任链：

```java
Response getResopnseWithInterceptorChain(){
	List<Interceptor> interceptors = new ArrayList<>();
  interceptors.addAll(client.interceptors()); //custom interceptors
  interceptors.add(retryAndFollowUpInterceptor) ; 
  interceptors.add(new BridgeInterceptor(client.cookieJar()));
  interceptors.add(new CacheInterceptor(client.internalCache()));
  interceptors.add(new ConnectInterceptor(client));
  interceptors.add(new CallServerInterceptor());
}
```

![image013.png](https://s0.lgstatic.com/i/image/M00/0D/2B/Ciqc1F7DlBqAHUznAAJGJV3xQJg366.png)

每一个拦截器的作用如下:

* retryAndFollowUpInterceptor：
* BridgeInterceptor : 对Request中Head设置默认值，比如Content-Type、Keep-Alive等
* CacheInterceptor ：对Http请求的缓存处理
* ConnectInterceptor：负责建立与服务器连接，也就是TCP连接
* CallServerInterceptor：向服务器发送请求，并从服务器拿到远端数据。

##### CacheInterceptor 缓存拦截器

​	通过Cache 类实现缓存功能。其内部是DiskLruCache。

![image023.png](https://s0.lgstatic.com/i/image/M00/0D/37/CgqCHl7DlEKAfkCcAADcFHFhEIE069.png)

1. 根据Request获取当前已有缓存的Response 并判断缓存是否有效，成功则返回缓存Resopnse，否则调用chain.proceed()继续执行下一个拦截器。

   ```java
   Response cacheCandidte = cache.get(chain.request()) ;
   CacheStrategy strategy = new CacheStrategy.Factory(chain.request(),cacheCandidte).get();
   Response cacheResponse = strategy.cacheResponse;
   Request networkRequest = strategy.networkRequest;
   
   if(networkRequest != null && cacheResponse != null){ //缓存有效
     	return cacheResponse.newBuilder().cacheResponse(cacheResponse).buid();
   }
   //缓存无效，执行下一个拦截器，继续网络请求流程
   Response networkResponse = null;
   networkResponse = chain.proceed(networkRequest);
   ```

   

2. 如果从服务端成功获取Reponse，再判断是否将此Response进行缓存操作。

```java
if(cache != null){
  	//如果设置了Cache缓存，则写入缓存后返回response
  	CacheRequest cacheRequest = cache.put(response);
  	return cacheWritingResponse(cacheRequest , response); 
}
return reponse;
```





##### CallServerInterceptor

![image025.png](https://s0.lgstatic.com/i/image/M00/0D/37/CgqCHl7DlEyAAYQ5AANbdjxrvDk061.png)















### Okhttp使用拓展

##### 1.继承ResponseBody

我们在构建 Response 时，需要调动 body() 方法传入一个 ResponseBody 对象。ResponseBody 内部封装了对请求结果的流读取操作。我们可以通过继承并扩展 ResponseBody 的方式获取网络请求的进度。



##### 2.拦截器自定义

1. 类似滴滴哆啦A梦，模拟断网、超时





### Okhttp的问题与网络优化

* OkHttp  HttpDns问题 踩坑记录

  https://www.jianshu.com/p/a0967bed8756









































