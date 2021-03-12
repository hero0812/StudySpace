## 【P】项目问题整理

> 项目中遇到过什么问题，如何解决 ？
>



### 173直播

1. ##### 【**线上问题排查】BadTokenException**

   ```
   android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@d4cacc6 is not valid; is your activity running?
       at android.view.ViewRootImpl.setView(ViewRootImpl.java:837)
       at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:356)
       at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:94)
       at android.app.Dialog.show(Dialog.java:329)
       at com.wanmei.show.fans.util.Utils.a(Utils.java:184)
       at com.wanmei.show.fans.util.Utils.a(Utils.java:158)
       at com.wanmei.show.fans.ui.base.BaseRoomActivity.a(BaseRoomActivity.java:60)
       at com.wanmei.show.fans.ui.playland.VideoLandActivity.a(VideoLandActivity.java:151)
       at com.wanmei.show.fans.ui.playland.VideoLandActivity$4.a(VideoLandActivity.java:272)
       at com.wanmei.show.fans.ui.play.OnlineBeat.d(OnlineBeat.java:109)
       at com.wanmei.show.fans.ui.play.OnlineBeat.b(OnlineBeat.java:21)
       at com.wanmei.show.fans.ui.play.OnlineBeat$3.a(OnlineBeat.java:99)
       at com.wanmei.show.fans.http.SocketUtils.a(SocketUtils.java:494)
       at com.wanmei.show.fans.http.SocketUtils.a(SocketUtils.java:561)
       at com.wanmei.show.fans.http.SocketUtils.a(SocketUtils.java:1073)
       at com.wanmei.show.fans.ui.play.OnlineBeat.c(OnlineBeat.java:81)
       at com.wanmei.show.fans.ui.play.OnlineBeat.a(OnlineBeat.java:21)
       at com.wanmei.show.fans.ui.play.OnlineBeat$2.run(OnlineBeat.java:72)
       at android.os.Handler.handleCallback(Handler.java:873)
       at android.os.Handler.dispatchMessage(Handler.java:99)
       at android.os.Looper.loop(Looper.java:201)
       at android.app.ActivityThread.main(ActivityThread.java:6864)
       at java.lang.reflect.Method.invoke(Native Method)
       at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:547)
       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:873)
   ```

   源码追踪

   ```
   /**
    * Exception that is thrown when trying to add view whose
    * {@link LayoutParams} {@link LayoutParams#token}
    * is invalid.
    */
   public static class BadTokenException extends RuntimeException {
       public BadTokenException() {
       }
   
       public BadTokenException(String name) {
           super(name);
       }
   }
   ```



2. **【线上问题排查】SVGA动画内存溢出**

   ```
   java.lang.OutOfMemoryError: Failed to allocate a 120012 byte allocation with 43176 free bytes and 42KB until OOM
       at dalvik.system.VMRuntime.newNonMovableArray(Native Method)
       at android.graphics.BitmapFactory.nativeDecodeByteArray(Native Method)
       at android.graphics.BitmapFactory.decodeByteArray(BitmapFactory.java:533)
       at com.opensource.svgaplayer.SVGAVideoEntity.a(SVGAVideoEntity.kt:88)
       at com.opensource.svgaplayer.SVGAVideoEntity.<init>(SVGAVideoEntity.kt:57)
       at com.opensource.svgaplayer.SVGAParser.a(SVGAParser.kt:182)
       at com.opensource.svgaplayer.SVGAParser.a(SVGAParser.kt:25)
       at com.opensource.svgaplayer.SVGAParser$parse$3.invoke(SVGAParser.kt:95)
       at com.opensource.svgaplayer.SVGAParser$parse$3.invoke(SVGAParser.kt:25)
       at com.wanmei.show.fans.ui.play.gift.common.DynamicEffectUtil$2$1.a(DynamicEffectUtil.java:153)
       at okhttp3.RealCall$AsyncCall.d(RealCall.java:141)
       at okhttp3.internal.NamedRunnable.run(NamedRunnable.java:32)
       at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1133)
       at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:607)
       at java.lang.Thread.run(Thread.java:761)
   ```

3. **【线上问题排查】Toast 添加到windowManager 异常**

```
java.lang.IllegalStateException: View android.widget.LinearLayout{9cb9554 V.E...... ......ID 0,0-690,147} has already been added to the window manager.
	at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:419)
	at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:118)
	at android.widget.Toast$TN.handleShow(Toast.java:536)
	at android.widget.Toast$TN$1.handleMessage(Toast.java:435)
	at android.os.Handler.dispatchMessage(Handler.java:110)
	at android.os.Looper.loop(Looper.java:219)
	at android.app.ActivityThread.main(ActivityThread.java:8349)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:513)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1055)
```

> 引申问题 ：Toast 为什么不需要弹窗权限



https://juejin.cn/post/6844903674066518024







### 验证码SDK

1. WebView绘制与  js中innerWidth、innerHeight获取问题







### 游戏聚合SDK

**【问题描述】可正常打包但是安装时报签名错误，对此Apk反编译后再打包签名对齐后可正常是什么原因？**
目前对此现象进行分析，发现新引入的一个jar包包含一个 META_INF/MANIFEST.MF文件，该文件只包含一个回车符。

将此文件从该jar中删除，即可恢复正常，如果把该文件中的回车符删除使之成为一个空文件，也可正常安装打包后得到的apk。

推测可能签名对此文件有BUG。

##### **【标题】AynscTask内部共享线程池导致问题**

【问题描述】

​		xx手游登录时间较长（大约在10秒以上），之前线上版本登录正常,确实属于异常现象。

【排查过程】

​		登录时涉及到多个接口调用。渠道的配置接口和登录接口的访问均同时发生在几乎同一时间，

【原理解释】

​	根据AsyncTask源码可知，执行任务需要等待上一任务完成才能执行下一个任务，符合本问题的特点，由于当前版SDK更新了miitSDK，基本定位在miitSDK在部分华为手机的获取oaid等值时会导致任务执行较慢

![image-20210311092628670](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311092628670.png)

AsyncTask具有模块间共享同一线程池，防止性能开销过大，易用的优点，并且属于Android原生级别的维护迭代，不要求立即响应的任务可以继续建议使用。
如果有影响响应时间的任务，还是需要单独管理模块的线程池以达到模块隔离的需求，其他任务不会影响到该任务的正常执行。



【总结】

​		SDK内部为AsyncTask指定独立的线程池 ，达到模块隔离的需求。

```
AsyncTask#executeOnExecutor
```



##### 【标题】Android8.0 SDK版本 透明的Activity允许设置方向导致崩溃

【问题描述】

应用宝渠道包，targetVersion为27，在Android8.0设备上点击qq登录，会产生崩溃。崩溃日志如下：

![image-20210311094451532](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311094451532.png)



【原理解释】

​		当targetVersion大于26时，全屏且透明的Activity不允许设置方向，否则会抛出异常产生崩溃。此为Google在Android 26 版本时增加了一个限制。

![image-20210311094921189](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311094921189.png)

【排查过程】

​	根据日志提示，查看Activity配置。

![image-20210311094538123](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311094538123.png)

应用宝渠道SDK的AssistActivity代码中，调用了requestWindowFeature()方法：：

![image-20210311094707172](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311094707172.png)





【总结】

​		游戏降低了targetVersion,以满足8.0设备对应用宝渠道的支持。





##### 【标题】SDK版本升级，对外API不规范/兼容导致崩溃

【问题描述】

【原因】

**SDK 1.5.1版修改了对外接口中的参数类型（将HashMap改为Map<String, String>）、同时之后的版本没有对1.4.1版本进行兼容所致**

![image-20210311100411443](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311100411443.png)

【总结】

	1. SDK 对外api返回null ，调用方未进行判空导致崩溃
	2. SDK升级，对外api方法签名发生改变，调用崩溃. 对外接口原则上任何情况下都不可改变，若接口设计确实存在不合理处、宜新增接口并将原有接口废弃，切忌直接修改接口！



##### 数据库升级失败问题

单词APP项目遇到一个bug，数据库提示“database disk image is malformed (code 11 SQLITE_CORRUPT)”，表示数据库已损坏，重建的空db文件，以前的数据全没了；

这是因为单词APP项目词典db在升级时是直接替换原有的数据库文件，不是升级操作。sqlite3.7之后会有-shm和-wal文件，其中shm文件包含一个到wal文件的索引。按理说替换时这些文件都应该一起删掉。之前没删，但是之前词典小也没问题。这次词典比较大。他重建索引失败了。导致抛出来一个database disk image is malformed (code 11 SQLITE_CORRUPT)。出现数据库损坏后系统会删除重建；

出现database disk image is malformed (code 11 SQLITE_CORRUPT)异常时系统会删除重建，这个是系统行为。我们的操作是触发了这个异常的一个原因；

正常数据库升级应该不会，出现这个问题。主要还是在删除数据库文件时，其对应的-shm,-wal,-journal都应该进行删除。





##### 2G以上APK反编译问题

~~1、目前打包工具使用的apktool，无法反编译超过2G的APK。~~

~~不过可以使用取巧的方式绕过，方法是，用解压软件删除apk中超大的文件，放到渠道包的相应目录中去。（仅针对assets下文件）~~

~~2、使用打包工具的develop模式打包时，当执行zipalign对齐apk时，zipalign工具报错，报无法打开zip文件的错误，不支持超2G的文件。~~

~~目前as、unity都是可以支持超2G文件的出包，他们在执行对齐步骤的时候不是调用的zipalign.exe，而是采用构建gradle工程的方式、~~

~~然后采用gradle编译出包。原zipalign的步骤由内部的task取代。打包工具目前还无法调用gradle的方式。~~



反编译

Apktool 2.4.1+

apktool d --only-main-classes xxx.apk

参考：https://ibotpeaches.github.io/Apktool/

 

编译打包

Apktool 2.4.1+

apktool b xxx -o new_apk.apk

参考：https://ibotpeaches.github.io/Apktool/



签名

需要使用apksigner，工具具体位置如图

![image-20210311153212108](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153212108.png)

 

Android 支持以下三种应用签名方案：

v1 方案：基于 JAR 签名。

v2 方案：APK 签名方案 v2（在 Android 7.0 中引入）。

v3 方案：APK 签名方案 v3（在 Android 9 中引入）。

参考大厂，微信使用v1，美团使用v1+v2

一般情况下只支持v1即可。

V1支持所有情况，V2必须在7.0及以上手机才支持。仅有v2签名的应用不能在7.0以下手机安装。

![image-20210311153139869](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153139869.png)

apksigner sign --ks apksignerchannel.jks --ks-pass pass:123456 --out out.apk input.apk

如果不加

--v1-signing-enabled <true | false>

--v2-signing-enabled <true | false>

--v3-signing-enabled <true | false>

则默认根据androidmanifest中的--min-sdk-version 和 --max-sdk-version 的值来决定何时应用此签名方案。

 

只进行v1签名

apksigner sign --ks apksignerchannel.jks --v2-signing-enabled false --v3-signing-enabled false --ks-pass pass:123456 --out out.apk input.apk

 

同一个签名文件签名的apk不管v1 v2 v3或者起组合都可以相互覆盖安装，前提是系统支持当前的签名（比如仅有v2签名须在7.0及以上手机安装）。

签名文件是jks或者keystore均可，游戏未要求的情况下只进行v1签名即可

参考：https://developer.android.com/studio/command-line/apksigner

https://source.android.com/security/apksigning

 

zipalign

使用的是 apksigner签名，则只能在为 APK 文件签名之前执行 zipalign。

官方的zipalign不支持超过2g的应用对齐。原因如下：

![image-20210311153103147](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153103147.png)

参考：https://developer.android.com/studio/command-line/zipalign

 

 

手机厂商应用市场支持2g应用情况

华为：不支持

小米：2g以上，无限制

Oppo：游戏最多3g

Vivo：2g以上，无限制

 

结论：

在非游戏强制要求对2g以上游戏zipalign的情况下（即不做zipalign），以上方案可以解决，并安装游戏apk正常使用。





##### 游戏母包分dex方案

若游戏母包主dex方法数过多，打出渠道包后，可能会超出65535方法数限制。此时需要游戏母包分dex。

母包分dex需要保证部分jar包必须在classes.dex中。

 

AndroidSDK提供的dx工具，可以指定一个文件来保持某些类保持在主dex中，在maindexlist.txt(文件名不固定)中，在文件中列出所有的class文件。但是对于当前的需求来说，需要保持在主dex中的类需要以jar包来区分，如果按照这种方式，需要列出的class文件太多，需要把每个jar包中所有class的路径统计出来再加入到文件中，使用不方便且容易出错。鉴于OneSDK的需求，推荐以下方是进行分dex。

分dex的步骤：

  \1. 将需要加入classes2.dex中的jar包挑拣到一个文件夹中，合并jar包；

  \2. jar转成dex；

  \3. unity工程中删除已经分出去的jar包，打出apk；

  \4. 使用aapt add [apkPath] [dexPath] 将2dex打入生成的apk中；

  \5. 签名、对齐。

 

**下面为详细步骤作为参考示例，游戏也可以根据自己的方式去实现上面各个步骤。**



###### 一、将需要加入classes2.dex中的jar包挑拣到一个文件夹中，合并jar包

不能放入分classes2.dex中的jar包：
  ①、SDK_Core*.jar
  ②、OneSDKApplication.jar
  ③、PermissionSupport*.jar（权限SDK）
此处使用ant进行合并，也可以使用其他方式。

1)、 将可以放入classes2.dex中的jar包，复制到一个文件夹中，新建index.xml文件：

![image-20210311153534015](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153534015.png)

2）、如图配置index.xml文件，其中basedir为此文件夹的路径，destfile为合并后的jar包

![image-20210311153551689](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153551689.png)

**注：分到2dex的jar包，不要再application中调用，否则4.4的手机会出现classNotFoundException(Android分dex的机制引起的)**

3）、切换到ant目录下（eclipse有内置的ant路径eclipse\plugins\org.apache.ant_XXX\bin），或者配置为环境变量，执行ant命令，生成合并后的jar包。

ant -buildfile index.xml

![image-20210311153616118](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153616118.png)

生成合并后的jar文件。

![image-20210311153634230](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153634230.png)

###### 二、 jar转成dex

 

   使用Android SDK中[SDK目录]/build-tools/[版本号]/下的dx.bat工具，执行命令：
   dx.bat --dex --output=classes2.dex [srcJarPath]

![image-20210311153658796](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311153658796.png)

###### 三、unity工程中删除已经分出去的jar包，然后打出apk；

   **注：打包前，一定要将Unity工程中已经复制出来jar包删除干净，否则主dex中会依然保留这些jar包！！！！**

###### 四、使用aapt命令，将classes.dex添加的apk中;

   aapt add [apkPath] [dexPath]

   注：①、**使用aapt 、dx工具的版本，要与unity使用的build-tools版本保持一致**。
       ②、添加完后，可通过rar工具打开，查看classes2.dex是否成功添加。若添加不成功，可尝试将添加前的apk、classes2.dex复制道aapt所在的目录下重新运行命令行

###### 五、签名 对齐

​    重签名：
​    jarsigner -verbose -keystore [证书] -signedjar [签名后的apk] [签名前的apk] [keyAlias]

​    对齐：
​    zipalign -v 4 xxx_signed.apk xxx_align.apk

​    注：若签名、对齐后母包不能安装并提示签名不正确（不影响出渠道包），建议先反编译后再重新打包、签名





##### Unity 启动launcher Activity启动模式限制问题

###### 一 、背景

部分渠道（小米、华为）建议launchMode 设置为 standard，但是unity官方建议launchMode 设置为 singleTask，如果设置为非singleTask，在与sdk交互过程中 发生了切换Activity 导致unity有概率崩溃，崩溃日志如下：

<img src="/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311154955377.png" alt="image-20210311154955377" style="zoom:50%;" />

Unity的官方文档建议：

![image-20210311155049379](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210311155049379.png)

具体可参考链接：https://docs.unity3d.com/Manual/android-manifest.html

###### 二、解决思路

在UnityPlayerActivity 前增加一个LaunchActivity，LaunchActivity的launchMode 设置为 standard，游戏UnityPlayerActivity 设置为singleTask

 

###### 三、具体解决方案

1.创建启动Activity，可参考LaunchActivity.java的实现方式，见下文附件

2.在AndroidManifest.xml添加如下声明（仅供参考）

 

```
<activity`` ``android:name=``"com.pwrd.onesdk.demo.LaunchActivity"`` ``android:configChanges=``"screenSize|orientation|keyboard|navigation|layoutDirection"`` ``android:launchMode=``"standard"`` ``android:theme=``"@style/WelcomeApp"``>`` ``<intent-filter>`` ``<action android:name=``"android.intent.action.MAIN"` `/>`` ``<category android:name=``"android.intent.category.LAUNCHER"` `/>`` ``</intent-filter>``</activity>
```

 

```
3.（可选）在res\values目录下的，*styles.xml文件里声明
<style name=``"WelcomeApp"``>``  ``<item name=``"android:windowIsTranslucent"``>``true``</item>``  ``<item name=``"android:windowBackground"``>``@color``/transparent</item>``  ``<!--<item name=``"windowActionBar"``>``false``</item>-->``  ``<!--<item name=``"windowNoTitle"``>``true``</item>-->``  ``<item name=``"android:windowFullscreen"``>``true``</item>``</style>
备注：这个style是设置了启动透明，但是透明的话就是不能设置屏幕方向。如果不这么设置的话，就有概率会启动黑屏的哈。
      游戏可以根据自己的需要选择是否需要该步骤
```





##### 打包客户端

1. 功能与操作流程

2. QT安装与配置

3. 代码结构



















