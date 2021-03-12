## 【C】NDK_02_JNI

> 1. c/c++ 基础与进阶学习
> 2.  JNI入门
> 3. Android虚拟机  JNI 原理



### SelfCheck

- 简述写一个JNI HelloWorld的基本流程,从写出Java native方法到打印到手机屏幕上为止,说说这整个过程里面每一步产生了什么文件
- **一个a包里的B类的c方法，cpp代码中的方法一定要a_B_c()这样吗？除了名称直接对应native方法,还有哪些绑定入口到原生方法的手段 ？动态映射native方法和直接对应原生方法对比,分别说出优点和缺点**
- 可以在不同的线程里面使用同一个JNI env对象吗 ？
- **so文件加载的过程 。如果有一个A.so依赖B.so,在未加载B.so的情况下直接加载A.so会发生什么? 你该如何避免?** 
- **在native的线程中调用attach方法是为了什么,之后什么情况下要detach ？**
- 有哪些Android支持的ABI,怎么样确定手机CPU最适合的ABI ？有写过针对特定ABI优化的native代码吗？
- 怎么样设计一个JNI的异常捕捉机制? 给你一个系统API的调用栈(只有系统方法,没有APP里的),你该如何排查问题?
- C++里A实例赋予给B，java里A实例赋予给B，这中间发生了什么事情
- c的计算struct数据大小



### JNI入门基础

Java层有两种field：静态成员和实例成员，两者在JNi层获取和赋值方法是不同的。Java层的field和method无论是public 、还是private，JNI都可以访问到，Java语言的封装性不见了。



在JNI层维护一个Java对象，如何避免被VM 垃圾回收？

使用NewGlobalRef  告诉VM不要回收此对象，当本地代码最终结束该对象的引用时，DeleteGlobalRef释放之。

LocalRef：每个被创建的Java对象首先会被加入一个LocalRef table，这个table大小是有限的。



#### SO文件加载过程

System.loadLibrary

Runtime#loadLibrary

ClassLoader#findLibrary()












### 学习资源

- 《JNI 规范》

- Android官方文档 -- NDK 

  https://developer.android.com/training/articles/perf-jni

- 享学课堂-NDK系列课

