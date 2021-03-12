> **路漫漫其修远兮，吾将上下而求索** 



## C++ 开篇 -- 从C到C++

### 面试问题

1. C++面向对象特性
2. 讲一下内联函数 ，内联函数和#define区别。inline 内联什么时候失效？为什么循环、分支不能设为内联？如果设为内联会怎样？
3. 静态链接和动态链接的区别？哪种更快？哪种生成的可执行文件更大？
4. main函数之前和之后都会做什么工作
5. 内存空间分为哪几个部分？全局变量存在哪个部分？
6. 指针和引用的区别
7. 





###  IDE下载安装

1. CLion + MinGW64 ( Android开发者对 jetbean出品的编译器用着顺手)

https://www.cnblogs.com/attentle/p/12652512.html

  

> ​    CLion中文乱码问题解决：
>

```
 打开File - Settings -> File Encodings编码改成UTF-8

 编译器右下角改成GBK
```



> Mac升级Catalina(10.15)后 clion不能运行，提示「xcrun: error: invalid active developer path ...」

```
1.是因为升级后少了系统需要的运行库，重新安装Xcode developer tools即可解决该问题。在终端中运行命令行xcode-select --install 后会自动安装Xcode developer tools，安装成功后即可解决该问题。

2.Tools -> CMmake -> reset cache and reload project
```



### C++ 知识大纲

#### 传统C++ ：C++98 知识体系

- 基础语言特性

  - 函数
    - 函数基础
    - 函数高级
  - 内存四区 -- 栈区、堆区、全局区、代码区
  - 指针
  - 结构体
  - C++中的引用
  - 类和对象
  - C++文件操作

- 模版

  - 

- STL

  - 

    



#### 现代C++：C++11/14新特性

- 语言可用性的强化
  - nullptr 与 constexpr
  - 类型推导
    - auto
    - decltype
    - 尾返回类型、auto 与 decltype 配合
  - 区间迭代
    - 基于范围的 for 循环
  - 初始化列表
    - std::initializer_list
    - 统一初始化语法
  - 模板增强
    - 外部模板
    - 尖括号 >
    - 类型别名模板
    - 变长参数模板
  - 面向对象增强
    - 委托构造
    - 继承构造
    - 显式虚函数重载
      - override
      - final
    - 显式禁用默认函数
  - 强类型枚举
- 语言运行期的强化
  - lambda 表达式
    - lambda 表达式基础
      - 值捕获
      - 引用捕获
      - 隐式捕获
      - 表达式捕获
    - 泛型 lambda
  - 函数对象包装器
    - std::function
    - std::bind/std::placeholder
  - 右值引用
    - 左值、右值的纯右值、将亡值、右值
    - 右值引用和左值引用
    - 移动语义
    - 完美转发
- 对标准库的扩充: 新增容器
  - std::array
  - std::forward_list
  - std::unordered_set
  - std::unordered_map
  - std::tuple
    - 基本操作
    - 运行期索引
    - 合并与迭代
- 对标准库的扩充: 智能指针和引用计数
  - 引用计数
  - std::shared_ptr
  - std::make_shared
  - std::unique_ptr
  - std::weak_ptr
- 对标准库的扩充: 正则表达式库
  - 正则表达式简介
    - 普通字符
    - 特殊字符
    - 限定符
  - std::regex 及其相关
    - std::regex
    - std::regex_match
    - std::match_results
- 对标准库的扩充: 语言级线程支持
  - std::thread
  - std::mutex
  - std::unique_lock
  - std::future
  - std::packaged_task
  - std::condition_variable
- 其他杂项
  - 新类型
    - long long int
  - noexcept 的修饰和操作
  - 字面量
    - 原始字符串字面量
    - 自定义字面量









### 第一个c++程序

 输入半径，求圆的周长面积:

> 思考类与对象的关系：c++编译器是如何区分是c1,c2,c3对象调用了类的方法？



```c++
#include <iostream>
/**
 * 第一个c++程序：求圆的周长和面积
 * @return
 */
using namespace std;

class MyCircle {
public:
    double getMR() {
        return m_r;
    }

    void setMR(double mR) {
        m_r = mR;
    }

    double getMS() {
        m_s = 3.14 * m_r * m_r;
        return m_s;
    }

private:
    double m_r;
    double m_s;
};

int main() {
    cout << "请输入半径：" << std::endl;
    MyCircle c1{};
    double r;
    cin >> r;
    c1.setMR(r);
    cout << "圆的面积是：" << c1.getMS() << std::endl;
    return 0;
}
```





### C/C++文件编译过程

1. 预处理

   把#include包含进来的.h文件 插入到#include所在位置，把源程序中使用到的#define定义的宏用实际的字符串替换。

   如果不用IDE，对应命令为：

   `gcc -E main.c -o main.i`

2. 编译

   编译阶段，编译器检查代码的规范性、语法错误等，检查无误后。编译器把代码翻译成汇编语言

   如果不用IDE，对应命令为：

   `gcc -S main.i -o main.s`

3. 汇编

   汇编阶段把.s文件翻译成二进制指令文件.o，这个阶段接收.c，.i ,.s的文件都没有问题

   如果不用IDE，对应命令为：

   `gcc -C main.s -o main.o`

4. 链接

   链接阶段，链接的是其余的函数库，比如我们自己编写的c/c++文件中用到了三方的函数库

   如果不用IDE，对应命令为：

   `gcc main.o -o main..exe`





### C++对c语言的扩展

- #### namespace作用域

  ```c++
  namespace mine{}
  ```

  控制标识符作用域，c语言只有一个全局作用域

- #### register 函数检测增强

  for循环中 int i ，c++会自行优化为寄存器变量

- #### struct类型类型加强

  c语言中认为struct定义了一组变量的集合，而不是一种类型

  c++中struct关键字和class关键字功能一样的，但是还是有区别,class成员默认private，struct默认public

- #### c++更强调类型（所有的变量和函数都必须有类型）

- #### const 不同表现

  c语言中const的表现：

  ​      const int *a; //const 修饰指针变量指向的内存空间 

  ​      int const *a; //const修饰指针变量本身

  ​	  const int const *a // 都不能修改

  但是c语言中const是个“冒牌货”，修饰的变量不是真的常量（可以通过指针修改）

  ```c
  const int  a = 10;
  int *p = NULL;
  p = &a;
  *p =  20;
  ```

  

  c++ 中const修饰的是一个真正的常量；原因是c++编译器扫描到const int a = 10; 时会把 a =10 以key-value形式放到符号表里。当const常量为全局，并且需要在其他文件中使用的时候、当使用&操作取const常量地址的是时候会分配新的内存空间，而不是在原变量a上直接操作。

  

  const内存分配机制：

  在编译器编译期间为p  = (int *)&a;分配内存

  

  const用途 -- 替代#define

  ​		相同点：

  ​				作用类似: const int a =5 ,#define a 5

  ​		不同点：
  
  #define由预处理器处理,是单纯的文本替换（没有作用域限制，除非使用#undef a卸载掉）const由编译器处理，提供类型和作用域检查。                                                                                                                                                                



-  #### **引用** 

  此处简要介绍引用相关的知识点

  1. 引用概念和用法
  
     变量的别名
  
     ```c++
     int a = 10;
     int &b = a;
     cout<<"b = "<<b;
     b = 100;
 cout<<"a = "<<b;
     ```

     > 引用的性质：普通引用必须初始化（依附于某个变量）
  
  2. **引用作函数参数、引用作返回值、引用作左值**
  
     ```c++
     int& getA2()
     {  
     	int a =10;  
     	return a;
 }
     ```

     如果函数返回栈上的引用（引用作返回值），可能会有问题。不可以作为其他引用的初始值（不能用其他引用变量来接收）

     函数返回值是引用（一块内存空间），用什么类型去接收，结果不同

  

​	   返回引用的函数当左值（特别适用于链式编程）

​			

  3. 复杂数据类型的引用

  ​        别名等同于指针方式修改内存

  

而下面函数对于形参pT的修改，不会影响到实参

```c++
void printfT(Teacher pT)
{  
    cout<<"age1 = "<<pT.age<<endl;   
    pT.age = 45;
}
```

​			4. 引用的本质

​				普通引用内部实现是指针常量，有自己的内存空间，也跟原变量操作同一块内存。

​			5.指针的引用

​			6. 常量引用

- #### 新增bool类型

  占用 1个字节，值为0或1

- #### 三目运算符 

  c语言中表达式的返回的是一个值，

  c++中返回的是变量本身（内存空间地址）。

  等同于如下c语言实现上述效果：

  ​	*(a < b ? &a : &b) = 30;

- #### 内联函数

- #### 函数

  ​	默认参数

  ​	函数占位参数

  ​	默认参数和占位参数混搭

  ​	函数重载

- #### new、delete

















### 学习资源

- B站 - 黑马程序员教程
  https://b23.tv/WbL7JB

  

































