## 【P】项目经验 --  Webview & Hybrid 实践方案

> 验证码SDK、HybridSDK、OTP项目学习整理
>



### 面试问题

- webview如何做资源缓存
- webview和js交互的几种方式，拦截的方法  **,  jsbridge是如何实现的  有没有其他方式 客户端如何调用前端  有几个方法 如何优化？**

### 目录

1. 掌握WebView的封装，了解所有的WebSettings配置，掌握WebViewClient、* WebChromeClient
2. 掌握WebView和Native双向通信机制，会自己封装双向通信中间件
3. 对WebView的封装可参考：GitHub: AgentWeb
4. 对通信中间件原理理解：GitHub：webprogress



### 前言

WebView控件功能强大，除了具有一般View的属性和设置外，还可以对url请求、页面加载、渲染、页面交互进行强大的处理。



### 1.[理论]WebView 基本使用、注意事项与优化

https://mp.weixin.qq.com/s/5Hs9Bg93IbY2uRUMeIqAJQ



#### 1-1 WebView类常用方法

```java
//激活WebView为活跃状态，能正常执行网页的响应
webView.onResume() ；
//当页面被失去焦点被切换到后台不可见状态，需要执行onPause
//通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
webView.onPause()；
//当应用程序(存在webview)被切换到后台时，这个方法不仅仅针对当前的webview而是全局的全应用程序的webview
//它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
webView.pauseTimers()
//恢复pauseTimers状态
webView.resumeTimers()；
//销毁Webview
//在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
//但是注意：webview调用destory时,webview仍绑定在Activity上
//这是由于自定义webview构建时传入了该Activity的context对象
//因此需要先从父容器中移除webview,然后再销毁webview:
rootLayout.removeView(webView); 
webView.destroy();



//是否可以后退
Webview.canGoBack() 
//后退网页
Webview.goBack()
//是否可以前进                     
Webview.canGoForward()
//前进网页
Webview.goForward()
//以当前的index为起始点前进或者后退到历史记录中指定的steps
//如果steps为负数则为后退，正数则为前进
Webview.goBackOrForward(intsteps) 
  
  
//清除网页访问留下的缓存
//由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
Webview.clearCache(true);
//清除当前webview访问的历史记录
//只会webview访问历史记录里的所有记录除了当前访问记录
Webview.clearHistory()；
//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
Webview.clearFormData()；
```



#### 1-2 WebSettings类与常用方法

```java
//声明WebSettings子类
WebSettings webSettings = webView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
webSettings.setJavaScriptEnabled(true);  

//支持插件
webSettings.setPluginsEnabled(true); 

//设置自适应屏幕，两者合用
webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小 
webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 
webSettings.setAllowFileAccess(true); //设置可以访问文件 
webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 
webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

//设置是否需要手动点击才能播放视频
setMediaPlaybackRequiresUserGesture(true)
```

```java

//缓存策略
    WebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

	//不使用缓存: 
	WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
```

> 常见实践方案 - 组合使用

```java
if (NetStatusUtil.isConnected(getApplicationContext())) {
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
} else {
    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
}

webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
webSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录

```



1-3 WebViewClient类与常用方法

> **shouldOverrideUrlLoading()**

```java
//步骤1. 定义Webview组件
Webview webview = (WebView) findViewById(R.id.webView1);

//步骤2. 选择加载方式
  //方式1. 加载一个网页：
  webView.loadUrl("http://www.google.com/");

  //方式2：加载apk包中的html页面
  webView.loadUrl("file:///android_asset/test.html");

  //方式3：加载手机本地的html页面
   webView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");

//步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
          view.loadUrl(url);
      return true;
      }
  });

```



> **onLoadResource()**

```java
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean onLoadResource(WebView view, String url) {
         //设定加载资源的操作
      }
  });

```



> **onReceivedError（）**

```java
//App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面，即webview如何加载一个本地的页面

//步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
//步骤2：将该html文件放置到代码根目录的assets文件夹下

//步骤3：复写WebViewClient的onRecievedError方法
//该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
				switch(errorCode)
                {
                case HttpStatus.SC_NOT_FOUND:
                    view.loadUrl("file:///android_assets/error_handle.html");
                    break;
                }
            }
        });

```



> **onReceivedSslError()**

```java

webView.setWebViewClient(new WebViewClient() {    
        @Override    
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {    
            handler.proceed();    //表示等待证书响应
        // handler.cancel();      //表示挂起连接，为默认方式
        // handler.handleMessage(null);    //可做其他处理
        }    
    });    

```



#### 1-4 WebChromeClient类与常用方法

> **onProgressChanged（）**

```java
webview.setWebChromeClient(new WebChromeClient(){

      @Override
      public void onProgressChanged(WebView view, int newProgress) {
          if (newProgress < 100) {
              String progress = newProgress + "%";
              progress.setText(progress);
            } else {
        }
    });

```

> **onReceivedTitle（）**

```java
webview.setWebChromeClient(new WebChromeClient(){

    @Override
    public void onReceivedTitle(WebView view, String title) {
       titleview.setText(title)；
    }

```



#### WebView缓存机制概览

![image-20210101115544421](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210101115544421.png)





#### 注意事项：如何避免WebView内存泄露？

1. 不在xml中定义 Webview ，而是在需要的时候在Activity中创建，并且Context使用 getApplicationgContext()**

```
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
1234
```

2.  在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。**

```
@Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
```



3. WebView Activity独立进程，销毁时System.exit()主动杀死进程



#### 优化思路：WebView速度优化

一个加载网页的过程中，native、网络、后端处理、CPU都会参与，各自都有必要的工作和依赖关系；让他们相互并行处理而不是相互阻塞才可以让网页加载更快：

1. 1. WebView初始化慢，可以在初始化同时先请求数据，让后端和网络不要闲着。
   2. 后端处理慢，可以让服务器分trunk输出，在后端计算的同时前端也加载网络静态资源。
   3. 脚本执行慢，就让脚本在最后运行，不阻塞页面解析。
   4. 同时，合理的预加载、预缓存可以让加载速度的瓶颈更小。
   5. WebView初始化慢，就随时初始化好一个WebView待用。
   6. DNS和链接慢，想办法复用客户端使用的域名和链接。
   7. 脚本执行慢，可以把框架代码拆分出来，在请求页面之前就执行好。



常见实践方案：

1. Application中提前初始化WebView对象
2. 构建WebView复用池



#### WebView中的漏洞

WebView中，主要漏洞有三类：

- ​	**•任意代码执行漏洞**

  出现该漏洞的原因有三个：

  - WebView 中 addJavascriptInterface（） 接口

    Google 在Android 4.2 版本中规定对被调用的函数以 @JavascriptInterface进行注解从而避免漏洞攻击；android 4.2之前我们不用去关心了。

  - WebView 内置导出的 searchBoxJavaBridge_对象

  - WebView 内置导出的 accessibility 和 accessibilityTraversalObject 对象

- ​	**•密码明文存储漏洞**

  **WebView**默认开启密码保存功能，开启后，在用户输入密码时，会弹出提示框：询问用户是否保存密码；

  如果选择”是”，密码会被明文保到 /data/data/com.package.name/databases/webview.db 中，这样就有被盗取密码的危险。**WebSettings.setSavePassword(false)** 

- ​	**•域控制不严格漏洞**

  即 A 应用可以通过 B 应用导出的 Activity 让 B 应用加载一个恶意的 file 协议的 url，从而可以获取 B 应用的内部私有文件，从而带来数据泄露威胁

  ```java
  // 需要使用 file 协议 
  setAllowFileAccess(true); 
  setAllowFileAccessFromFileURLs(false); 
  setAllowUniversalAccessFromFileURLs(false); // 禁止 file 协议加载 JavaScript 
  if (url.startsWith("file://") { 
  setJavaScriptEnabled(false); 
  } else { 
  setJavaScriptEnabled(true); 
  } 
  ```

  



### 2.【理论】hybrid 实现方案

https://blog.csdn.net/carson_ho/article/details/64904691

Android调用JS代码的方法有2种：

1. 通过`WebView`的`loadUrl（）`

   优点：方便简洁。

   缺点：效率低；获取返回值麻烦

2. 通过`WebView`的`evaluateJavascript（）`

   优点：效率高

   缺点：向下兼容问题（4.4以下）

JS调用Android代码的方法有3种：

1. 通过`WebView`的`addJavascriptInterface（）`进行对象映射

   优点：方便简洁

   缺点：Android 4.2以下漏洞；通过@JavaScriptInterface注解修复

2. 通过 `WebViewClient` 的`shouldOverrideUrlLoading ()`方法回调拦截 url

   使用复杂；需要进行协议约束；

   适于不需要返回值情况下的互调场景。

3. 通过 `WebChromeClient` 的`onJsAlert()`、`onJsConfirm()`、`onJsPrompt（）`方法回调拦截JS对话框`alert()`、`confirm()`、`prompt（）` 消息





### 3.【实践】WebView组件化架构设计







### 4.【原理】WebView -- 初始化、加载、缓存管理、销毁

chromium内核源码地址

https://chromium.googlesource.com/chromium/src.git/+/28cc253ce347f9a58a0e7c6b7b249c239c4b2669/android_webview/glue/java/src/com/android/webview/chromium







### 5. 项目问题排查

-  【机型适配】VIVO设备 WebView 视频无法播放

  WebSettings
  
  setMediaPlaybackRequiresUserGesture(true)
  
  默认是true，即html5中的视频，需要手动点击播放。
  
  但是VIVO部分设备上，手动点击也无法播放，改成setMediaPlaybackRequiresUserGesture(false)，自动播放解决问题。
  
  
  
-  【问题描述】 webview 设置为height = wrap_content 。第一次加载，html中打印innerWidth，innerHeight 都是0。

  attachToWindow 之前则可以，之后则不行。

​      问题 ： attachToWindow 时机与webview加载过程、window对象之间的关系？

   

1. WebView继承自ViewGroup

2. laodUrl [

   委托

   WebViewProvider.loadUrl(url)

   WebViewFactory -> WebViewFactoryProvider.createWebView() : WebViewProvider

   WebViewFactoryProvider 实现类 WebViewChromuimFactoryProvder  

   WebViewProvider 实现类 WebViewChromium

   委托AwContents // WebView Wrapper for 

   AwContents.loadUrl

3. 

WebView onPageStart调用时机

```
01-22 18:28:18.296 23255 23255 D LiveShowUtil: {"liveUrl":"https://mountain.show.173.com/event/live/zhuxian/index.html?roomid=96","perWH":1.76,"perHH":0.78125,"perOffsetY":0.0,"bCanDrag":false}
01-22 18:28:18.322   605   683 D ConnectivityService: listenForNetwork for Listen from uid/pid:10042/23255 for NetworkRequest [ id=39, legacyType=-1, [ Capabilities: INTERNET&NOT_RESTRICTED&TRUSTED] ]
01-22 18:28:18.342 23255 23255 W cr_AwContents: onDetachedFromWindow called when already detached. Ignoring
01-22 18:28:18.342 23255 23255 E LiveShowView: OnUpdateInfo->989, 562, 0, 0, 0, 0
01-22 18:28:18.342 23255 23255 D activitysdk_log: init
01-22 18:28:18.342 23255 23255 D activitysdk_log: debug = false
01-22 18:28:18.348 23255 23255 I cr_Ime  : ImeThread is not enabled.
01-22 18:28:18.349 23255 23255 E webview : loadurl=https://mountain.show.173.com/event/live/zhuxian/index.html?roomid=96
01-22 18:28:18.349 23255 23255 E FreeView: OnUpdateInfo down
01-22 18:28:18.407 23255 23433 E eglCodecCommon: glUtilsParamSize: unknow param 0x00008fbb
01-22 18:28:18.407 23255 23433 E eglCodecCommon: glUtilsParamSize: unknow param 0x00008fbb
01-22 18:28:18.425 23255 23255 W System.err: java.lang.NoSuchMethodException: uploadEventWithoutTaskId [class android.content.Context, class java.lang.String, interface java.util.Map]
01-22 18:28:18.429 23255 23323 E eglCodecCommon: glUtilsParamSize: unknow param 0x00008fbb
01-22 18:28:18.432 23255 23255 W System.err:    at java.lang.Class.getMethod(Class.java:624)
01-22 18:28:18.432 23255 23255 W System.err:    at java.lang.Class.getDeclaredMethod(Class.java:586)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.dfga.a.uploadEvent(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.dfga.b.uploadEvent(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.dfga.b.a(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.jsbridge.b.a(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.jsbridge.WebViewBridgeManager.onPageStarted(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.wanmei.activity.a$1.onPageStarted(Unknown Source)
01-22 18:28:18.432 23255 23255 W System.err:    at com.android.webview.chromium.WebViewContentsClientAdapter.onPageStarted(WebViewContentsClientAdapter.java:517)
01-22 18:28:18.432 23255 23255 W System.err:    at org.chromium.android_webview.AwContentsClientCallbackHelper$MyHandler.handleMessage(AwContentsClientCallbackHelper.java:144)
01-22 18:28:18.432 23255 23255 W System.err:    at android.os.Handler.dispatchMessage(Handler.java:102)
01-22 18:28:18.432 23255 23255 W System.err:    at android.os.Looper.loop(Looper.java:148)
01-22 18:28:18.432 23255 23255 W System.err:    at android.app.ActivityThread.main(ActivityThread.java:5606)
01-22 18:28:18.432 23255 23255 W System.err:    at java.lang.reflect.Method.invoke(Native Method)
01-22 18:28:18.432 23255 23255 W System.err:    at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:745)
01-22 18:28:18.432 23255 23255 W System.err:    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:635)
```



### 参考资源

- WebView封装

  https://github.com/yangchong211/YCWebView

- 支付宝移动端 Hybrid 解决方案探索与实践

  https://tech.antfin.com/community/articles/398