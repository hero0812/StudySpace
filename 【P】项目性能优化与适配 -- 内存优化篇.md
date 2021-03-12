## 【P】项目性能优化与适配 -- 内存优化篇



### 面试要点

- 内存泄露的分类。**什么么情况会导致内存泄漏如何修复？**
- **下载一张很大的图，如何保证不 oom？**

\- [Android性能优化（五）之细说Bitmap](https://www.jianshu.com/p/e49ec7d053b3)

- 一张图片100*100的图片在内存中的大小？
- 以handler匿名内部类做例子讲。泄露链是怎样的。
- leakcanary原理。
- 为什么会出现抖动，为什么要做抖动的检查，有啥用，
- 线上有泄露，怎么收集，排查，设计方案。



### 内存问题与分析工具

#### 系统对App内存限制

利用命令行

```
adb shell cat /system/build.prop
```

![image-20210116000952862](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116000952862.png)

可以看到：

1. App启动的初始分配内存 : dalvik.vm.heapstartsize = 16m
2. App最大内存限制 ：dalvik.vm.heapgrowlimit = 128m
3. 开启largeHeap = “true” 之后 最大内存限制 dalvik.vm.heapsize = 192



> 为什么要关注内存占用？





### 内存溢出（OOM）





##### OOM原因分类：

1. Java堆内存溢出
2. 没有足够连续内存空间
3. FD数量超出限制
4. 线程数超出限制
5. 虚拟内存不足







### 内存泄漏

当前应用周期内不再使用的对象被GC Roots引用，导致不能回收，致使可用内存变小。最终导致OOM。



##### Android内存泄漏常见场景以及解决方案

**1****、资源性对象未关闭**

对于资源性对象不再使用时，应该立即调用它的close()函数，将其关闭，然后再置为null。例如Bitmap 等资源未关闭会造成内存泄漏，此时我们应该在Activity销毁时及时关闭。

**2****、注册对象未注销**

 例如BraodcastReceiver、EventBus未注销造成的内存泄漏，我们应该在Activity销毁时及时注销。

**3****、类的静态变量持有大数据对象**

 尽量避免使用静态变量存储数据，特别是大数据对象，建议使用数据库存储。

**4****、单例造成的内存泄漏**

 优先使用Application的Context，如需使用Activity的Context，可以在传入Context时使用弱引用进行封

装，然后，在使用到的地方从弱引用中获取Context，如果获取不到，则直接return即可。

 **5****、非静态内部类的静态实例**

该实例的生命周期和应用一样长，这就导致该静态实例一直持有该Activity的引用，Activity的内存资源 不能正常回收。此时，我们可以将该内部类设为静态内部类或将该内部类抽取出来封装成一个单例，如 果需要使用Context，尽量使用Application Context，如果需要使用Activity Context，就记得用完后置 空让GC可以回收，否则还是会内存泄漏。

**6****、****Handler****临时性内存泄漏**

Message发出之后存储在MessageQueue中，在Message中存在一个target，它是Handler的一个引 用，Message在Queue中存在的时间过长，就会导致Handler无法被回收。如果Handler是非静态的， 则会导致Activity或者Service不会被回收。并且消息队列是在一个Looper线程中不断地轮询处理消息， 当这个Activity退出时，消息队列中还有未处理的消息或者正在处理的消息，并且消息队列中的Message 持有Handler实例的引用，Handler又持有Activity的引用，所以导致该Activity的内存资源无法及时回 收，引发内存泄漏。

解决方案如下所示:

1、使用一个静态Handler内部类，然后对Handler持有的对象(一般是Activity)使用弱引用，这 样在回收时，也可以回收Handler持有的对象。

2、在Activity的Destroy或者Stop时，应该移除消息队列中的消息，避免Looper线程的消息队列中 有待处理的消息需要处理。

需要注意的是，AsyncTask内部也是Handler机制，同样存在内存泄漏风险，但其一般是临时性的。对于 类似AsyncTask或是线程造成的内存泄漏，我们也可以将AsyncTask和Runnable类独立出来或者使用静 态内部类。

**7****、容器中的对象没清理造成的内存泄漏**

在退出程序之前，将集合里的东西clear，然后置为null，再退出程序

**8****、****WebView**

WebView都存在内存泄漏的问题，在应用中只要使用一次WebView，内存就不会被释放掉。我们可以为 WebView开启一个独立的进程，使用AIDL与应用的主进程进行通信，WebView所在的进程可以根据业 务的需要选择合适的时机进行销毁，达到正常释放内存的目的。







#### 内存抖动及案例

> 何为内存抖动？

短时间内大量创建、回收对象，造成内存波动图呈锯齿的现象

> 为什么要关注内存抖动呢？

GC 过程中会有stop the world，短暂停止用户线程，GC太频繁势必造成卡顿。





#### 内存分析工具

##### 基础知识 -hprof文件

使用 android.os.Debug.dumpHprofData 方法

直接使用 Debug 类提供的 dumpHprofData 方法即可。



Debug.dumpHprofData(heapDumpFile.getAbsolutePath());

复制代码Android Studio 自带 Android Profiler 的 Memory 模块的 dump 操作使用的是方法一。这两种方法生成的 .hprof 文件都是 Dalvik 格式，需要使用 AndroidSDK 提供的 hprof-conv 工具转换成J2SE HPROF格式才能在MAT等标准 hprof 工具中查看。

```
hprof-conv dump.hprof converted-dump.hprof  
```



##### 1.AS 自带的Memory Profile

##### 2.MAT （Memory Analyzer Tools）



Incoming Refrences

Outgoing References

![image-20210116002256821](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116002256821.png)

浅堆（Shadow Heap）与 深堆（Retain Heap）

Shadow Heap 表示对象本身占用内存大小，而Retain Heap 还要包括对其他对象的引用

![image-20210116002422921](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116002422921.png)







##### 3.LeakCanary使用与原理

![image-20210116004524896](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116004524896.png)

![image-20210116005718359](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20210116005718359.png)

1. 初始化

   利用ContentProvider onCreate 先于Application onCreate 机制，在AndroidManifest.xml中 声明了名为 LeakSentryInstaller的ContentProvider，从而实现自动初始化。

   > ASM handleBindApplication(AppBindData data) 方法内部：

   ![image-20210116002806994](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116002806994.png)

   

   ![image-20210116003322673](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116003322673.png)

   ![image-20210116003423034](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116003423034.png)

   ![image-20210116003443487](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116003443487.png)

   ![image-20210116003524141](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116003524141.png)

   注册ActivityLifecycleCallback实现监测Activity/Fragment销毁

   

2. ActivityDestroyWatcher中注册了ActivityLifecycleCallback，监听onActivityDestroyed()

   FragmentDestroyWatcher

   把要观察的对象放入watch，这里利用到到弱引用WeakReference的特性 -- 在被GC回收时，会被放入引用队列ReferenceQueue。

   ![image-20210116005212575](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116005212575.png)

   ![image-20210116005246169](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116005246169.png)

   根据Queue中是否包含观察对象的引用来判断是否释放

   ![image-20210116005414785](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116005414785.png)

3. 如何判定泄漏

   

   ![image-20210116001927896](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116001927896.png)

   

   

   

   ![image-20210116004644920](./images/image-20210116004644920.png)

   

   

   观察列表 watchReferences[]  存放obj uuid 

   5s后观察

   怀疑列表

   haha 进行可达性分析



#### 腾讯内部使用线上内存监控库 --  JVMTI

性能优化训练营















### Bitmap 使用优化

#### Bitmap内存模型

![image-20210116011832263](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116011832263.png)



默认ARGB_8888 ：一个像素占4字节

RGB_565 ： 一个像素2字节



##### **Bitmap占用的内存 **

图片放在某个特定drawable中，比如drawable-hdpi，如果设备的屏幕密度高于当前drawable目

录所代表的密度，则图片会被放大，否则会被缩小

 放大或缩小比例 scale = 设备屏幕密度 / drawable目录所代表的屏幕密度

> drawable目录所代表的屏幕密度

![image-20210116012213341](/Users/zhanghongxi/ABP-study/StudySpace/images/image-20210116012213341.png)



**Bitmap内存占用** ≈ 像素数据总大小 = 图片宽 × 图片高× (设备分辨率/资源目录分辨率)^2 × 1个像素的字节大小



#### Bitmap的复用

1. **使用LruCache和DiskLruCache做内存和磁盘缓存；**

2. **使用Bitmap复用，同时针对版本进行兼容。**

   ```java
   BitmapFactory.Options options = new BitmapFactory.Options();
   // 图片复用，这个属性必须设置；
   options.inMutable = true;
   // 手动设置缩放比例，使其取整数，方便计算、观察数据；
   options.inDensity = 320;
   options.inTargetDensity = 320;
   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.resbitmap, options);
   // 对象内存地址；
   Log.i(TAG, "bitmap = " + bitmap);
   Log.i(TAG, "bitmap：ByteCount = " + bitmap.getByteCount() + ":::bitmap：AllocationByteCount = " + bitmap.getAllocationByteCount());
   
   // 使用inBitmap属性，这个属性必须设置；
   options.inBitmap = bitmap;
   options.inDensity = 320;
   // 设置缩放宽高为原始宽高一半；
   options.inTargetDensity = 160;
   options.inMutable = true;
   Bitmap bitmapReuse = BitmapFactory.decodeResource(getResources(), R.drawable.resbitmap_reuse, options);
   // 复用对象的内存地址；
   Log.i(TAG, "bitmapReuse = " + bitmapReuse);
   Log.i(TAG, "bitmap：ByteCount = " + bitmap.getByteCount() + ":::bitmap：AllocationByteCount = " + bitmap.getAllocationByteCount());
   Log.i(TAG, "bitmapReuse：ByteCount = " + bitmapReuse.getByteCount() + ":::bitmapReuse：AllocationByteCount = " + bitmapReuse.getAllocationByteCount());
   
   输出：
   I/lz: bitmap = android.graphics.Bitmap@35ac9dd4
   I/lz: width:1024:::height:594
   I/lz: bitmap：ByteCount = 2433024:::bitmap：AllocationByteCount = 2433024
   I/lz: bitmapReuse = android.graphics.Bitmap@35ac9dd4 // 两个对象的内存地址一致
   I/lz: width:512:::height:297
   I/lz: bitmap：ByteCount = 608256:::bitmap：AllocationByteCount = 2433024
   I/lz: bitmapReuse：ByteCount = 608256:::bitmapReuse：AllocationByteCount = 2433024 // ByteCount比AllocationByteCount小
   ```

   可以看出：

   1. **从内存地址的打印可以看出，两个对象其实是一个对象，Bitmap复用成功；**

   2. **bitmapReuse占用的内存（608256）正好是bitmap占用内存（2433024）的四分之一；**

   3. **getByteCount()获取到的是当前图片应当所占内存大小，getAllocationByteCount()获取到的是被复用Bitmap真实占用内存大小。虽然bitmapReuse的内存只有608256，但是因为是复用的bitmap的内存，因而其真实占用的内存大小是被复用的bitmap的内存大小（2433024）。这也是getAllocationByteCount()可能比getByteCount()大的原因。**

      

#### Bitmap 内存占用

**getByteCount()与getAllocationByteCount()的区别**

- **一般情况下两者是相等的；**
- **通过复用Bitmap来解码图片，如果被复用的Bitmap的内存比待分配内存的Bitmap大,那么getByteCount()表示新解码图片占用内存的大小（并非实际内存大小,实际大小是复用的那个Bitmap的大小），getAllocationByteCount()表示被复用Bitmap真实占用的内存大小（即mBuffer的长度）。**



####  **Bitmap如何压缩？**

##### ** Bitmap.compress()**

质量压缩： 它是在保持像素的前提下改变图片的位深及透明度等，来达到压缩图片的目的，**不会减少图片的像素。进过它压缩的图片文件大小会变小，但是解码成bitmap后占得内存是不变的。**

##### **BitmapFactory.Options.inSampleSize**

**内存压缩：**

- **解码图片时，设置BitmapFactory.Options类的inJustDecodeBounds属性为true，可以在Bitmap不被加载到内存的前提下，获取Bitmap的原始宽高。而设置BitmapFactory.Options的inSampleSize属性可以真实的压缩Bitmap占用的内存，加载更小内存的Bitmap。**
- **设置inSampleSize之后，Bitmap的宽、高都会缩小inSampleSize倍。例如：一张宽高为2048x1536的图片，设置inSampleSize为4之后，实际加载到内存中的图片宽高是512x384。占有的内存就是0.75M而不是12M，足足节省了15倍。**

**备注：inSampleSize值的大小不是随便设、或者越大越好，需要根据实际情况来设置。** **以下是设置inSampleSize值的一个示例：**

```java
public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
        int reqWidth, int reqHeight) {
    // 设置inJustDecodeBounds属性为true，只获取Bitmap原始宽高，不分配内存；
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);
    // 计算inSampleSize值；
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    // 真实加载Bitmap；
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
}

public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;
        // 宽和高比需要的宽高大的前提下最大的inSampleSize
        while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }
    }
    return inSampleSize;
}
```























