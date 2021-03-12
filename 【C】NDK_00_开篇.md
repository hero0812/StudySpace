## **NDK_00_开篇**

> C -> JNI -> C++  -> Linux基础 -> 编译so -> NDK工具链及简单实战
>
> -> NDK高级应用 ：音视频ffmpeg、opencv、openGL



### NDK学习路线

-》编译环境 -> C -> JNI 

  -> C++

  -> Linux基础 

 ->AS继承.so动态库和.a静态库

 -> NDK工具链及简单实战

-> NDK高级应用 ：音视频ffmpeg、opencv、openGL



### 编译环境

####  IDE下载安装

1. CLion + MinGW64 ( Android开发者对 jetbean出品的编译器用着顺手)

https://www.cnblogs.com/attentle/p/12652512.html

  

2. CLion中文乱码问题解决：

 打开File - Settings -> File Encodings编码改成UTF-8

 编译器右下角改成GBK



#### 常用的C/C++编译器

clang

​	更轻更新。轻量级编译器，基于LLVM。Android Studio使用

gcc

​	c编译器，拓展为可处理c++，但后缀为.c的仍当作是c程序处理

g++

​	g++自动连接c++标准库，将.c和.cpp都当作是c++程序处理

GDB

​	调试工具



#### C/C++编译过程

##### 预处理

​	主要处理include和define等。把#include包含进来的.h文件插入到#include所在的位置。把#define定义的宏用实际的字符串代替。

##### 编译

​	编译器检查代码的规范性、语法错误等，检查无误后，编译成汇编语言。

##### 汇编

​	把.s文件翻译成二进制机器指令文件.o，这个阶段接收.c，.i, .s文件都没有问题。

##### 链接

​	链接其余的函数库，比如我们自己编写的c/c++文件中用到了三方的函数库，在链接阶段需要把三方的库链接进来。







### JNI 简介

​	JNI 即Java Native Interface 是Java代码与native交互的接口。通过  JNI 与底层Framework（例如：驱动、编解码、图形绘制模块）进行交互，拓展Java虚拟机的能力。   



#### JNI编程步骤：

- 在Java中声明Native方法
- 编译上述Java源文件javac得到.class文件
- 通过javah命令导出JNI头文件(.h文件)
- 使用Java需要交互的本地代码，实现在Java中声明的Native方法
- 编译.so库文件





### NDK简介

​	native development kit ，Android提供的快速开发c/c++动态库的工具包，提供了一些基础功能，如：把.so和.apk打包的工具、交叉编译器，用于生成特定阿CPU平台动态库、一些基础代码库。

​	相比于Java代码，native代码运行效率高、代码安全性好，但是不受Java虚拟机约束。

​	JNI是Java调用C/C++的途径，NDK是Android提供的实现JNI的手段。



#### 静态库与动态库

​	在Linux环境下，静态库(.a)，动态库（.so）。

​	静态库：对函数库的链接放在编译时期完成的，被链接合成一个可执行文件，不需要再依赖其他文件。

​	动态库：把对一些库函数的链接载入推迟到程序运行时，可实现进程间的资源共享，甚至可以做到链接、载入完全由程序员在程序代码中控制。



### 交叉编译

​	在当前编译平台下，编译出不同目标平台运行的程序。

#### 交叉编译链

​	ndk17及以下 ，gcc作为交叉编译器

​	ndk17以上，使用clang



###  参考资源

- NDK 开发汇总

  https://blog.csdn.net/baopengjian/article/details/104615972