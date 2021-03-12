## 【A】Android虚拟机 -- JNI

> 3. 



### SelfCheck

- JNI是否了解 如何加载NDK库 ？
- 





### JNI入门基础



Java层有两种field：静态成员和实例成员，两者在JNi层获取和赋值方法是不同的。Java层的field和method无论是public 、还是private，JNI都可以访问到，Java语言的封装性不见了。



在JNI层维护一个Java对象，如何避免被VM 垃圾回收？

使用NewGlobalRef  告诉VM不要回收此对象，当本地代码最终结束该对象的引用时，DeleteGlobalRef释放之。

LocalRef：每个被创建的Java对象首先会被加入一个LocalRef table，这个table大小是有限的。



