## 【A】Framework原理 -- SharedPreference



### 输出倒逼输入

- sharedPreference的commit ，apply区别。
- sharedPreference的其性能问题。
  
- sharedPreference线程安全性，还有进程安全性，sharedPreference锁的对象是谁？
   sharedPreference线程安全，锁的是contentImpl.class
- 跨进程文件写入如何保证安全

