## Android Studio 构建过程与Gradle

> 知识浩如烟海，学技术如苦海行舟，要有自己的目的性和学习主线，方能在最短的时间内获得提升，到达彼岸；
>
> 读书恰恰相反，海纳百川，不求实用，厚积薄发，量变自然产生质变。																																						





### SelfCheck

-  APK的结构？APK打包流程	?  

- 安卓资源id如何查找，资源id的前两位代表着什么？

- 运行时删除项目的多余的资源文件 是否影响项目运行？ 提成R文件内指向的数字是否影响 ？（可以参考下网上关于arsc的资料来回答这个问题）

- **APK 为什么要签名？是否了解过具体的签名机制？**

- 安卓的混淆原理是什么？Android打包哪些类型文件不能混淆？

- MultiDex原理 -- 用MultiDex解决何事？其根本原因在于？SDK 21 不分 dex，直接全部加载会不会有什么问题？主Dex放哪些东西？主Dex和其他Dex调用、关联？

  

  



### Android Studio构建过程

#### APK结构

一个 APK 打包完之后，通常有下面几个目录，用来存放不同的文件。
**assets**
	原生资源文件，不会被压缩或者处理
**classes.dex**
	java 代码通过 javac 转化成 class 文件，再通过 dx 文件转化成 dex 文件。如果有多个 dex 文件，其命名会是这样的：
classes.dex classes2.dex classes3.dex ...
在其中保存了类信息。
**lib/**
	保存了 native 库 .so 文件，其中会根据 cpu 型号划分不同的目录，比如 ARM，x86 等等。
**res/**
	保存了处理后的二进制资源文件。
**resources.arsc**
	保存了资源 id 名称以及资源对应的值／路径的映射。
**META-INF/**
	用来验证 APK 签名，其中有三个重要的文件 MANIFEST.MT，CERT.SF，CERT.RSA。
**MANIFEST.MF** 保存了所有文件对应的摘要，部分内容如下：

```
Manifest-Version: 1.0
Built-By: Generated-by-ADT
Created-By: Android Gradle 3.4.0

Name: AndroidManifest.xml
SHA-256-Digest: QxJh66y6ssDSNFgZSlf5jIWXfRdWnqL1c3BSwSDUYLQ=

Name: META-INF/android.arch.core_runtime.version
SHA-256-Digest: zFL2eISLgUNzdXtGA4O/YZYOSUPCA3Na3eCjULPlCYk=
```

**CERT.SF** 保存了MANIFEST.MF 中每条信息的摘要，部分内容如下：

```
Signature-Version: 1.0
Created-By: 1.0 (Android)
SHA-256-Digest-Manifest: j8YGFgHsujCHud09pT6Igh21XQKSnG+Gqy8VUE55u+g=
X-Android-APK-Signed: 2

Name: AndroidManifest.xml
SHA-256-Digest: qLofC3g32qJ5LmbjO/qeccx2Ie/PPpWSEPBIUPrlKlY=

Name: META-INF/android.arch.core_runtime.version
SHA-256-Digest: I65bgli5vdqHKel7MD74YlSuuyCR/5NDrXr2kf5FigA=
```

**CERT.RSA**     包含了对 CERT.SF 文件的签名以及签名用到的证书。

**AndroidManifest.xml**    这里是编译处理过的二进制的文件。







#### 全量编译关键步骤概述

1. 使用AAPT/AAPT2 编译资源文件生成resources.arsc以及R.java
2. 使用aidl处理aidl文件，生成java文件
3. 使用javac编译java文件，生成class文件
4. 使用dx/d8处理class文件，生成dex文件
5. 使用Android NDK处理native代码生成.so文件
6. 使用apkbuilder生成未签名的apk
7. 使用apksigner对apk进行签名，生成最终apk



#### 编译资源文件 -- AAPT/AAPT2

​	AAPT2 会解析资源、为资源编制索引，并将资源编译为针对 Android 平台进行过优化的二进制格式。

- [编译](https://developer.android.com/studio/command-line/aapt2?hl=zh-cn#compile)：将资源文件编译，生成一个扩展名为 `.flat` 的中间二进制文件。
- [链接](https://developer.android.com/studio/command-line/aapt2?hl=zh-cn#link)：将编译后的 xxx.flat 资源链接，生成一个完整的 resource.arsc，二进制资源和 R.java



##### **为什么要转化为二进制文件？**

- 二进制格式的XML文件占用空间更小。这是由于所有XML元素的标签、属性名称、属性值和内容所涉及到的字符串都会被统一收集到一个字符串资源池中去，并且会去重。有了这个字符串资源池，原来使用字符串的地方就会被替换成一个索引到字符串资源池的整数值，从而可以减少文件的大小。
- 二进制格式的XML文件解析速度更快。这是由于二进制格式的XML元素里面不再包含有字符串值，因此就避免了进行字符串解析，从而提高速度。 有了资源ID以及资源索引表之后，Android资源管理框架就可以迅速将根据设备当前配置信息来定位最匹配的资源了。



##### R文件生成与Resources.arsc解析资源id

1. 第一个字节是指资源所属包，7F代表普通应用程序
2. 第二个字节是指资源的类型，如02指drawable，03指layout
3. 第三四个字节是指资源的编号，往往从00开始递增



R文件中 id都是static final 修饰的静态常量，由于java编译器的优化，在编译时，所有**使用静态常量的地方，会被直接替换为常量值。**这样一来，R文件里的id在编译完java文件后，就没有被引用的地方了，此时如果开启proGuard混淆，就会删除整个R文件，从而会**减少field数和包大小。**



对于aar的编译，生成的R文件里的id，并不是public static final的常量，而是**public static的变量**，这是为什么呢？因为如果是常量，则会在编译打包时，调用的地方被替换为常量值，而这个值是AAR内部生成的临时id，是不对的，这样的话**主工程编译时将无法修改这个值**，就有问题了。aar中含有的是一个R.txt文本文件，这个文件以文本的形式记录了AAR中所有资源的类型、名字等，以便于**主工程打包时，可以依据这些资源信息统一生成最终的R.java文件。**



主工程编译时，最终**除了会在主工程包名下，生成一个包含主工程和AAR所有资源的R.java文件之外，还会在每个AAR相应的包名下，生成一个包含AAR资源的R.java文件**，当然，相同资源的id是一样的。这就是为什么我们可以在主工程中调用主包名的R文件，和AAR包名的R文件，都可以获取到一些资源id的原因。此时生成的所有R.java文件里的id值，都是**public static final的静态常量**，因为此刻的id值都已经确定了。然后在编译java文件时，常量值会被替换(包括资源文件中的引用也会被替换)，从而使R文件的field无引用，可以通过proGuard删除。



还有一个问题，就是**AAR里面的文件，使用到资源id的地方，并没有被替换为相应的常量值，但是R文件里面的资源id确实是常量。**

这是因为AAR的class文件，在主工程编译时，不会再次进行编译，也就是说**AAR的class文件原封不动的打包进apk**。而资源id为常量是在主工程编译时才行程的，但AAR生成class时，使用的是上面说到的变量，所以一直被保留了下来。



> 如何根据R.java中的资源id查找到对应的资源呢？
>

@link 【A】Amdroid系统架构-资源管理篇







#### apk签名过程











#### 混淆Proguard原理

andresguard 做arsc 缩小



#### MultiDex







### Grade自动构建框架

​	现在的Android集成开发工具Android Studio 采用Gradle作为 项目自动构建框架。开发者只需简单的操作，就能得到想要的编译产物，gradle框架内部全权处理了构建流程。



#### Gradle执行流程	

Gradle构建周期包含三个阶段：

 1. Initialization 初始化阶段

    首先根据settings.gradle文件构建出一个Seetings对象，然后根据Seetings中的配置，创建Project对象，去找各个project下的build.gradle文件，根据文件内容来对project对象进行配置。

 2. Configuration 配置阶段

    对project对象进行配置, 构建Configuration对象，包括解析 TaskDependency等。

    ```
    task a {
    
    }
    
    task testBoth {
    	// 依赖 a task 先执行
    	dependsOn("a")
    	println '我会在 Configuration 和 Execution 阶段都会执行'
        doFirst {
          println '我仅会在 testBoth 的 Execution 阶段执行'
        }
        doLast {
          println '我仅会在 testBoth 的 Execution 阶段执行'
        }
    }
    
    ```

    

 3. Execution 执行阶段

    ​     执行阶段，Configuration对象通过CompositeBuildClassPathInitializer#execute(Configuration)传入并获取其中的配置。IncludedBuildTaskGraph.addTask方法添加build task，构建生成Task DAG（有向无环图）。其addTask方法中调用到internalDefaultIncludedBuildController#queueForExecution(String taskPath) 将Task添加到LinkedHashMap中准备执行，继而doBuild方法执行各个task:



> gradle源码 ：composite-builds\org\gradle\composite\internalDefaultIncludedBuildController

```java
 private void doBuild(final Collection<String> tasksToExecute) {
        if (tasksToExecute.isEmpty()) {
            return;
        }
        LOGGER.info("Executing " + includedBuild.getName() + " tasks " + tasksToExecute);
        IncludedBuildExecutionListener listener = new IncludedBuildExecutionListener(tasksToExecute);
        try {
            includedBuild.execute(tasksToExecute, listener);
            tasksDone(tasksToExecute, null);
        } catch (RuntimeException failure) {
            tasksDone(tasksToExecute, failure);
        }
    }
```

​	这就有了我们在Android Studio中运行一下Build ：打印出来的图中第一句日志：

```
Executing tasks: [:captchademo:assembleDebug] in project /Users/xxxxxxx/captchademo
```



接下来就是每个Configuration阶段，对每个项目的build.gradle进行 “evaluate”操作，build.gradle 里的task不会真正执行，但是task必包里的代码会执行，可以通过设置build监听以及打印log验证：

```
task pack {
    println("zhx pack start")
    project.gradle.addProjectEvaluationListener(new ProjectEvaluationListener() {
        @Override
        void beforeEvaluate(Project project) {
            println("zhx beforeEvaluate")
        }

        @Override
        void afterEvaluate(Project project, ProjectState state) {
            println("zhx afterEvaluate")
        }
    })
    project.gradle.addBuildListener(new BuildListener() {
        @Override
        void buildStarted(Gradle gradle) {
            println("zhx buildStarted")
        }

        @Override
        void settingsEvaluated(Settings settings) {
            println("zhx settingsEvaluated")
        }

        @Override
        void projectsLoaded(Gradle gradle) {
            println("zhx projectsLoaded")
        }

        @Override
        void projectsEvaluated(Gradle gradle) {
            println("zhx projectsEvaluated")
        }

        @Override
        void buildFinished(BuildResult result) {
            println("zhx buildFinished")
        }
    })
    println("zhx pack end")
}
```

每个子module“evalute”完之后，会回调“afterEvaluate” ， 当最后一个Project “evalute”完之后，会回调给我们，整个项目“projectsEvaluated”。打包插件即是hook 到这个Configuration阶段的结束点，进行自定义的构建流程

```
> Configure project :captchademo
zhx pack start
zhx pack end
zhx afterEvaluate

> Configure project :CaptchaSDK
zhx beforeEvaluate
zhx afterEvaluate

> Configure project :system_webview_module
zhx beforeEvaluate
zhx afterEvaluate
zhx projectsEvaluated

/////////////////  Gradle框架默认的（Tasks）执行阶段///////////////////
> Task :captchademo:preBuild UP-TO-DATE
> Task :captchademo:preDebugBuild UP-TO-DATE
> Task :CaptchaSDK:preBuild UP-TO-DATE
> Task :CaptchaSDK:preDebugBuild UP-TO-DATE
> Task :CaptchaSDK:compileDebugAidl NO-SOURCE
> Task :system_webview_module:preBuild UP-TO-DATE
> Task :system_webview_module:preDebugBuild UP-TO-DATE
> Task :system_webview_module:compileDebugAidl NO-SOURCE
> Task :captchademo:compileDebugAidl NO-SOURCE
> Task :CaptchaSDK:packageDebugRenderscript NO-SOURCE
> Task :system_webview_module:packageDebugRenderscript NO-SOURCE
> Task :captchademo:compileDebugRenderscript NO-SOURCE
> Task :captchademo:generateDebugBuildConfig UP-TO-DATE
> Task :CaptchaSDK:compileDebugRenderscript NO-SOURCE
> Task :CaptchaSDK:generateDebugBuildConfig UP-TO-DATE
> Task :CaptchaSDK:generateDebugResValues UP-TO-DATE
> Task :CaptchaSDK:generateDebugResources UP-TO-DATE
> Task :CaptchaSDK:packageDebugResources UP-TO-DATE
> Task :CaptchaSDK:parseDebugLocalResources UP-TO-DATE
> Task :CaptchaSDK:processDebugManifest UP-TO-DATE
> Task :CaptchaSDK:generateDebugRFile UP-TO-DATE
> Task :CaptchaSDK:javaPreCompileDebug UP-TO-DATE
> Task :CaptchaSDK:compileDebugJavaWithJavac UP-TO-DATE
> Task :CaptchaSDK:bundleLibCompileDebug UP-TO-DATE
> Task :system_webview_module:compileDebugRenderscript NO-SOURCE
> Task :system_webview_module:generateDebugBuildConfig UP-TO-DATE
> Task :system_webview_module:generateDebugResValues UP-TO-DATE
> Task :system_webview_module:generateDebugResources UP-TO-DATE
> Task :system_webview_module:packageDebugResources UP-TO-DATE
> Task :system_webview_module:parseDebugLocalResources UP-TO-DATE
> Task :system_webview_module:processDebugManifest UP-TO-DATE
> Task :system_webview_module:generateDebugRFile UP-TO-DATE
> Task :system_webview_module:javaPreCompileDebug UP-TO-DATE
> Task :system_webview_module:compileDebugJavaWithJavac UP-TO-DATE
> Task :system_webview_module:bundleLibCompileDebug UP-TO-DATE
> Task :captchademo:javaPreCompileDebug UP-TO-DATE
> Task :captchademo:mainApkListPersistenceDebug UP-TO-DATE
> Task :captchademo:generateDebugResValues UP-TO-DATE
> Task :captchademo:generateDebugResources UP-TO-DATE
> Task :captchademo:mergeDebugResources UP-TO-DATE
> Task :captchademo:createDebugCompatibleScreenManifests UP-TO-DATE
> Task :captchademo:extractDeepLinksDebug UP-TO-DATE
> Task :CaptchaSDK:extractDeepLinksDebug UP-TO-DATE
> Task :system_webview_module:extractDeepLinksDebug UP-TO-DATE
> Task :captchademo:processDebugManifest UP-TO-DATE
> Task :CaptchaSDK:compileDebugLibraryResources UP-TO-DATE
> Task :system_webview_module:compileDebugLibraryResources UP-TO-DATE
> Task :captchademo:processDebugResources UP-TO-DATE
> Task :captchademo:compileDebugJavaWithJavac UP-TO-DATE
> Task :captchademo:compileDebugSources UP-TO-DATE
> Task :captchademo:mergeDebugShaders UP-TO-DATE
> Task :captchademo:compileDebugShaders UP-TO-DATE
> Task :captchademo:generateDebugAssets UP-TO-DATE
> Task :CaptchaSDK:mergeDebugShaders UP-TO-DATE
> Task :CaptchaSDK:compileDebugShaders UP-TO-DATE
> Task :CaptchaSDK:generateDebugAssets UP-TO-DATE
> Task :CaptchaSDK:packageDebugAssets UP-TO-DATE
> Task :system_webview_module:mergeDebugShaders UP-TO-DATE
> Task :system_webview_module:compileDebugShaders UP-TO-DATE
> Task :system_webview_module:generateDebugAssets UP-TO-DATE
> Task :system_webview_module:packageDebugAssets UP-TO-DATE
> Task :captchademo:mergeDebugAssets UP-TO-DATE
> Task :captchademo:processDebugJavaRes NO-SOURCE
> Task :CaptchaSDK:processDebugJavaRes NO-SOURCE
> Task :CaptchaSDK:bundleLibResDebug UP-TO-DATE
> Task :system_webview_module:processDebugJavaRes NO-SOURCE
> Task :system_webview_module:bundleLibResDebug UP-TO-DATE
> Task :captchademo:mergeDebugJavaResource UP-TO-DATE
> Task :captchademo:checkDebugDuplicateClasses UP-TO-DATE
> Task :captchademo:desugarDebugFileDependencies UP-TO-DATE
> Task :captchademo:mergeExtDexDebug UP-TO-DATE
> Task :CaptchaSDK:bundleLibRuntimeDebug UP-TO-DATE
> Task :system_webview_module:bundleLibRuntimeDebug UP-TO-DATE
> Task :captchademo:dexBuilderDebug UP-TO-DATE
> Task :captchademo:mergeLibDexDebug UP-TO-DATE
> Task :captchademo:mergeProjectDexDebug UP-TO-DATE
> Task :captchademo:mergeDebugJniLibFolders UP-TO-DATE
> Task :CaptchaSDK:mergeDebugJniLibFolders UP-TO-DATE
> Task :CaptchaSDK:mergeDebugNativeLibs UP-TO-DATE
> Task :CaptchaSDK:stripDebugDebugSymbols UP-TO-DATE
> Task :CaptchaSDK:copyDebugJniLibsProjectOnly UP-TO-DATE
> Task :system_webview_module:mergeDebugJniLibFolders UP-TO-DATE
> Task :system_webview_module:mergeDebugNativeLibs UP-TO-DATE
> Task :system_webview_module:stripDebugDebugSymbols UP-TO-DATE
> Task :system_webview_module:copyDebugJniLibsProjectOnly UP-TO-DATE
> Task :captchademo:mergeDebugNativeLibs UP-TO-DATE
> Task :captchademo:stripDebugDebugSymbols UP-TO-DATE
> Task :captchademo:validateSigningDebug UP-TO-DATE
> Task :captchademo:packageDebug UP-TO-DATE
> Task :captchademo:assembleDebug UP-TO-DATE
zhx buildFinished
```



接下来会根据DAG（有向无环图）的拓扑排序将Task顺序执行 ，它们对应着Android Studio构建的各个阶段。直到生成最终产物 -- apk。

> 拓扑排序(Topological Order)是指，将一个有向无环图(Directed Acyclic Graph简称DAG)进行排序进而得到一个有序的线性序列。







**gradle接下来的学习将分两个方向 --	gradle框架的原理和应用。**

1. 学习gradle框架设计和源码实现，了解gradle框架是如何将复杂的构建过程组织、串联起来的。进一步了解AAPT/AAPT2、R8、D8工作过程，进而探索能应用于项目实践中的优化技术，比如MainDex的优化、编译速度优化等。

   

2. gradle应用 -- 插件开发

   目前我所知道的两大类插件：

   1） 开发辅助型

   开发过程中，可以有效减少体力劳动，提升开发效率的工具型插件，比如自动生成 JavaBean 文件插件。

   

   2）hook gradle构建型

   很多插件要实现的功能都是想要在gradle 构建过程中动动手脚，AspectJ框架在编译时修改字节码、SDK打包插件则是自行组织项目构建流程，最终得到包括demo apk、SDK aar等多种打包产物。

   

3. gradle应用 -- 编译配置

   根据实际业务场景，个性化编译配置，比如多渠道打包配置、组件化项目架构配置、项目依赖管理等。

   

### Gradle原理

- 学习Gradle框架设计和源码实现

- 学习各个Tasks的职责以及分别是如何实现资源合并、dex分包、混淆签名等打包流程中关键任务的。原理指导实践，这些Tasks 又对我们实际项目工作优化实践有着两重指导意义：

1. 了解MergeResource Task **职责**，可Hook其执行时机，编写png图片自动转webp插件，实现APK瘦身

   https://mp.weixin.qq.com/s/X7ss6gLJ3kZbUVpgPn6jAw

2. 分析MainDexListTransform**源码**，指导对MainDex进行优化

   https://juejin.cn/post/6844903774415224839





### Gradle 应用 -- **插件开发实战**

- JavaBean文件生成插件

- ButterKnife助手-- 代码生成插件

- hook gradle构建过程 -- 编译时字节码插桩技术

- hook gradle构建过程 --SDK 打包插件

  

  

### Gradle 应用 --编译配置 

#### 多渠道打包



#### 依赖管理



####  组件化









### 









### 资源参考

 **AndroidStudio 编译apk打包过程**

​		https://github.com/5A59/android-training/blob/master/common-tec/android-%E6%89%93%E5%8C%85.md

​    	https://juejin.im/post/5a69c0ccf265da3e2a0dc9aa



 **aapt/appt2 工作原理 --  R 文件生成 与 resource.arsc 文件解析**

​		https://blog.csdn.net/beyond702/article/details/51744082

[		Android R文件生成_移动开发_依生依世-CSDN博客](https://blog.csdn.net/qq_15827013/article/details/103717702)

​		https://juejin.im/post/6844903933769433095



Android资源查找过程源码分析

​		https://sharrychoo.github.io/blog/android-source/resources-find-and-open



**签名机制** 

​	https://www.jianshu.com/p/286d2b372334



 **极客时间 - 张绍文 Android开发高手课 -- 关于编译你需要了解什么？**

[**https://time.geekbang.org/column/intro/142**](https://time.geekbang.org/column/intro/142)



- gradle for android

  https://juejin.cn/post/6844903604642562061#learn







* 字节抖音团队 -- ByteX插件平台

  https://github.com/bytedance/ByteX/blob/master/README_zh.md





















