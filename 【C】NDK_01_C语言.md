## 【C】NDK_01_C 语言

### 基础篇

#### 数据类型

| 类型     | 说明                             |
| -------- | -------------------------------- |
| 基本类型 | 整型、浮点型                     |
| 构造类型 | 指针、数组、结构体、共同体、枚举 |

| 类型           | 32位 | 64位 | 取值范围          |
| -------------- | ---- | ---- | ----------------- |
| char           | 1    | 1    | -128～127或0～255 |
| Short          | 2    | 2    |                   |
| int            | 4    | 4    |                   |
| Long           | 4    | 8    |                   |
| unsigned char  |      |      |                   |
| unsigned short |      |      |                   |
| unsigned int   |      |      | 0～65535          |
| unsigned long  |      |      |                   |

1字节 = 8位二进制

带符号基本数据类型，首位表示正负，0正1负，所以正极大加1，进位，变成负数极小值



| 格式控制符   | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| %c           | 读取一个单一的字符                                           |
| %hd、%d、%ld | 读取一个十进制数，并分别赋值给short、int、long类型           |
| %ho、%o、%lo | 读取一个八进制数，并分别赋值给short、int、long类型           |
| %hx、%x、%lx | 读取一个十六进制数，并分别赋值给short、int、long类型         |
| %hu、%u、%lu | 读取一个无符号整数，并分别赋值给unsigned short、unsigned int、unsigned long类型 |
| %f、%lf      | 读取一个十进制小数，并分别赋值给float、double类型            |
| %s           | 读取一个字符串                                               |



```c
char c  = 128;// 2^7
printf("%d\n",c);//以signed int类型打印 -128
printf("%hhd\n",c);//以signed char类型打印整数 -128
printf("%hd\n",c);//短整型 输出-128
printf("%hu\n",c);//无符号短整型 输出65408
```



#### typedef

可以使用它来为类型取一个新的名字，下面为单字节数字定义了一个术语 **BYTE**：

```c
typedef unsigned char BYTE;
```

在这个类型定义之后，标识符 BYTE 可作为类型 **unsigned char** 的缩写，例如：

```c
BYTE  b1, b2; // 值范围[0,255]
```







#### 变量与常量

##### 声明变量

1. 声明同时定义变量。声明变量时即建立存储空间 
2. 声明而不定义。extern关键字。



##### 定义常量

1. #define 宏预处理器

宏就是文本替换

```c
#define A '1
#define test(i) i >10?1:0
```

优点：

​	文本替换，每个使用到的地方被替换成宏定义。不会造成函数调用的开销。

缺点：生成目标文件太大，不会执行代码检查。



2. const关键字



#### 存储类

- auto

- register

  定义寄存器变量，只用于需要快速访问的变量，比如计数器。

- static

  程序的生命周期内，声明它的文件内的全局变量，不需要在它每次进出作用域时进行创建和销毁。

- extern

  该存储类用于提供一个全局变量的引用，全局变量对于所有的程序文件都是可见的。当您使用一个extern时，对于无法初始化的变量，会把变量名指向一个之前定义过的存储位置。





#### 枚举

1. 先定义枚举类型再定义枚举变量

   ```c
      enum DAY {
           MON = 1, TUE, WEB, THU, FRI, SAT, SUN
       };
   
       enum DAY day;
   ```

2. 定义枚举类型的同时定义枚举变量

   ```c
   enum DAY {
       MON = 1, TUE, WEB, THU, FRI, SAT, SUN
   } day;
   ```

   

3. 省略枚举名称，直接定义枚举变量

   ```c
   enum {
       MON = 1, TUE, WEB, THU, FRI, SAT, SUN
   } day;
   ```





#### 指针

##### 指针的算术运算

int* ptr++ 指针将向后移动4字节，指向下一个整数int的位置。

```c
const int MAX = 3;

int main() {

    int var[] = {10,100,200};
    int i , *ptr;
    //指针指向数组首元素地址
    ptr = var;
    for (i = 0; i < MAX; i++) {
        printf("存储地址：var[%d] = %p \n",i,ptr);
        printf("存储值：var[%d] = %d \n",i,*ptr);
        ptr ++;
    }
    return 0;
}

运行结果：
存储地址：var[0] = 0x7ffee29b1a0c 
存储值：var[0] = 10 
存储地址：var[1] = 0x7ffee29b1a10 
存储值：var[1] = 100 
存储地址：var[2] = 0x7ffee29b1a14 
存储值：var[2] = 200 
```



##### 指针数组

指针类型的数组，每个元素都是指向相应类型变量的指针。

```c
#include<stdio.h>

const int MAX = 3;

int main() {

    int var[] = {10, 100, 200};

    int j, *p[MAX];
    //将数组中int变量地址赋给指针数组中的指针变量
    for (j = 0; j < MAX; j++) {
        p[j] = &var[j];
    }

    for (j = 0; j < MAX; j++) {
        printf("存储地址：var[%d] = %p \n", j, p[j]);
        printf("存储值：var[%d] = %d \n", j, *p[j]);
    }
    return 0;
}
```



##### 指向指针的指针

通常指针保存的是一个变量的内存地址，这个变量如果也是一个指针，那么指向指针的指针行成一种多级间接寻址的形式。





##### 传递指针给函数

```c
#include<stdio.h>
#include<time.h>

void getSeconds(unsigned long *par);

int main() {

    unsigned long sec;

    getSeconds(&sec);

    printf("Now time : %ld", sec);
    return 0;
}

void getSeconds(unsigned long *par) {
    /* 获取当前时间*/
    *par = time(NULL);
    return;
}
```



##### 从函数返回指针

```c
#include<stdio.h>
#include<time.h>
#include <stdlib.h>

/*生成并返回随机数的函数*/
int *getRandom() {
    static int r[10];
    /*设置种子*/
    srand((unsigned) time(NULL));
    for (int j = 0; j < 10; ++j) {
        r[j] = rand();
        printf("%d\n", r[j]);
    }
    return r;
}

int main() {
    /*定义一个指向整数的指针*/
    int *pInt;
    pInt = getRandom();
    for (int i = 0; i < 10; ++i) {
        printf("*p+[%d] : %d", i, *(pInt + i));
    }
    return 0;
}
```



##### 函数指针与回调函数

```c
void println(char *buffer){
    printf("%s\n",buffer);
}

//接收一个函数作为参数
void say(void(*p)(char*),char *buffer){
    p(buffer);
}

int main() {
    void(*p)(char*) = println;
    p("hello");
    say(p,"world");
    return 0;
}
```



利用typedef创建别名，类似Java中的回调函数

```c
typedef void(*callback)(int);

void test(callback callback){
    callback("success");
}
```





#### C内存管理 -- 指针动态分配内存

- malloc

  作用:   堆上分配连续的内存空间，并返回这块内存的指针。在申请完空间后，不会被初始化 -- 它们的值是未知的，需手动判空及初始化这块空间或通过realloc函数初始化内存空间。

  参数：需要分配的字节数

  实例：

  ```c
  int * pmalloc = NULL;
  pmalloc = (int*)malloc(sizeof(int)*5);
  if (NULL == pmalloc){
      printf("alloc memory failed");
      return 0;
  }
  ```

- calloc

  会把内存空间初始化为0

  ```c
  int * pcalloc = NULL;
  pcalloc = calloc(5,sizeof(int));
  if (NULL == pcalloc){
      printf("alloc memory failed");
      return 0;
  }
  ```

- realloc

  作用：修改已分配的内存块的大小，接在原内存块后边扩容或缩小，或另外开辟内存空间。

  需要使用返回的新指针，旧指针不能用了。

- free()函数，在不需要内存时，都应该调用函数 **free()** 来释放内存。




#### c字符串 -- 字符数组

​	c风格的字符串 == c字符串 == 字符数组 ， 以'\0' 结尾

声明方式：

```c
char str[10] = {'h','e','l','l','o','\0'};
char str2[10] = "hello";
char * str3 = "hello";
```

后两种方式相同点是可以省略 ‘\0’ 

前两种栈上分配内存，第三种方式 "hello"  是在常量区分配内存，可读不可写。



##### c字符串常用函数：

| 序号 | 函数 & 目的                                                  |
| :--- | :----------------------------------------------------------- |
| 1    | **strcpy(s1, s2);** 复制字符串 s2 到字符串 s1。              |
| 2    | **strcat(s1, s2);** 连接字符串 s2 到字符串 s1 的末尾。       |
| 3    | **strlen(s1);** 返回字符串 s1 的长度。                       |
| 4    | **strcmp(s1, s2);** 如果 s1 和 s2 是相同的，则返回 0；如果 s1<s2 则返回小于 0；如果 s1>s2 则返回大于 0。 |
| 5    | **strchr(s1, ch);** 返回一个指针，指向字符串 s1 中字符 ch 的第一次出现的位置。 |
| 6    | **strstr(s1, s2);** 返回一个指针，指向字符串 s1 中字符串 s2 的第一次出现的位置。 |



- scanf函数接收字符串输入的问题：

1、 空格、回车 中断输入问题

2、输入溢出问题



- get()函数 没有 空格、回车 中断输入问题但是还有溢出的问题，溢出数组部分可能乱码或造成崩溃。

- 推荐 fgets() 函数 ，可以严格约束要输入的字符长度。

```c
int main() {
    //字符串数组接收输入字符串
    char str[10];
    printf(" input something : \n");
    fgets(str,10,stdin);
    gets(str);
    printf(" input str length : %d",(int)sizeof(str));
    printf(" input : %s",str);
    return 0;
}
```



- c字符串赋值函数

  ```c
  strcpy(dest,source);
  ```

- 读取字符串长度

  ```c
  #include <stdio.h>
  #include <string.h>
  
  int main() {
      char str[10];
      char str2[10];
      printf(" input something : \n");
      char * s = gets(str);
      //给字符数组赋值
      strcpy(str2,s);
      printf(" str2 : %s",str2);
      //计算字符串（字符数组）大小/长度
      printf(" input str array length : %d \n",(int)sizeof(str));
      printf(" input str length : %d",(int)strlen(str));
      return 0;
  }
  ```

  输出结果：

  ```c
   input something :
   hello
   str2 : hello input str array length : 10
   input str length : 5
  ```

  strlen()函数计算真实字符串长度，sizeof()计算数组占用内存大小



- 字符串拼接函数：

```c
strcat(dest,source);
```





#### 结构体

##### 定义结构

```c
struct tag { 
    member-list
    member-list 
    member-list  
    ...
} variable-list ;
```

tag是结构体标签

**member-list** 是标准的变量定义，比如 int i; 或者 float f，或者其他有效的变量定义。

**variable-list** 结构变量，定义在结构的末尾，最后一个分号之前，您可以指定一个或多个结构变量。

**注：tag、member-list、variable-list** 这 3 部分至少要出现 2 个。如：

```c
//1。声明无标签（匿名）结构体，并声明一个变量
struct {
    int a;
    char b;
    double c;
} s1,s4;

//2。只声明一个标签为SAMPLE的结构体
struct SAMPLE {
    int a;
    char b;
    double c;
};
//声明一个SAMPLE标签的结构体变量
struct SAMPLE s2;

//3。使用typedef 定义一个结构体类型SAMPLE2，这样可以直接使用SAMPLE2 声明结构体变量，不必再指定struct关键字
typedef struct {
    int a;
    char b;
    double c;
} SAMPLE2;
SAMPLE2 s3;
```



他们的成员列表是一样的,但对编译器来说它们是不同的结构体。

结构体的成员可以包含其他结构体 -- 使用指针指向自己。

```c
struct B;    //对结构体B进行不完整声明
 
//结构体A中包含指向结构体B的指针
struct A
{
    struct B *partner;
    //other members;
};
 
//结构体B中包含指向结构体A的指针，在A声明完后，B也随之进行声明
struct B
{
    struct A *partner;
    //other members;
};
```



##### 结构体变量初始化

对结构体变量可以在定义时指定初始值

```c
struct Books
{
   char  title[50];
   char  author[50];
   char  subject[100];
   int   book_id;
} book = {"C 语言", "RUNOOB", "编程语言", 123456};
```



##### 访问结构成员

使用**成员访问运算符（.）**



##### 结构作为函数参数

可以把结构作为函数参数，传参方式与其他类型的变量或指针类似。

```c
struct SAMPLE {
    int a;
    int b;
    int c;
};
struct SAMPLE s2,s2_2;

void printStruct(struct SAMPLE sample);

int main() {
    s2.a = 1;
    s2.b = 2;
    s2.c = 3;

    s2_2.a = 4;
    s2_2.b = 5;
    s2_2.c = 6;
    printStruct(s2);
    printStruct(s2_2);
    return 0;
}

void printStruct(struct SAMPLE sample){
    printf("%d -> %d -> %d  ",sample.a,sample.b,sample.c);
}
```









##### 指向结构的指针

与定义指向其他类型变量的指针相似

```c
struct Books *struct_pointer;
```



```c
//2。只声明一个标签为SAMPLE的结构体
struct SAMPLE {
    int a;
    char b[20];
    int c;
};
//声明一个SAMPLE类型的结构体变量，
struct SAMPLE s2;

void printStrcutFiled(struct SAMPLE *sample2);

int main() {
    strcpy(s2.b,"hello c struct");
    printStrcutFiled(&s2);
    return 0;
}
void printStrcutFiled(struct SAMPLE *sample2){
    printf("%s  ",sample2->b);
}
```



#### 位域

有些信息在存储时，并不需要占用一个完整的字节，而只需占几个或一个二进制位。例如在存放一个开关量时，只有 0 和 1 两种状态，用 1 位二进位即可。所谓"位域"是把一个字节中的二进位划分为几个不同的区域，并说明每个区域的位数。每个域有一个域名，



``` 
struct bs{
    int a : 2; //带符号整型，只能表示0或1
    int b : 4; //带符号整型，只能表示最大2的3次方减1
} bs1;

int main() {
    bs1.a = 1;
    bs1.b = 7;
    printf("%d -> %d   ", bs1.a, bs1.b);
    return 0;
}
```





#### 共同体

定义共用体，必须使用 **union** 语句，方式与定义结构类似。

```c
union [union tag]
{
   member definition;
   member definition;
   ...
   member definition;
} [one or more union variables];
```



```c
union Data
{
   int i;
   float f;
   char  str[20];
} data;
```

现在，**Data** 类型的变量可以存储一个整数、一个浮点数，或者一个字符串。这意味着一个变量（相同的内存位置）可以存储多个多种类型的数据。





#### 文件读写

##### 打开文件

使用 **fopen( )** 函数来创建一个新的文件或者打开一个已有的文件，这个调用会初始化类型 **FILE** 的一个对象，类型 **FILE** 包含了所有用来控制流的必要的信息。

```c
FILE *fopen( const char * filename, const char * mode );
```



filename 是字符串，文件名

对参数mode的说明：

| 模式 | 描述                                                         |
| :--- | :----------------------------------------------------------- |
| r    | 打开一个已有的文本文件，允许读取文件。                       |
| w    | 打开一个文本文件，允许写入文件。如果文件不存在，则会创建一个新文件。在这里，您的程序会从文件的开头写入内容。如果文件存在，则该会被截断为零长度，重新写入。 |
| a    | 打开一个文本文件，以追加模式写入文件。如果文件不存在，则会创建一个新文件。在这里，您的程序会在已有的文件内容中追加内容。 |
| r+   | 打开一个文本文件，允许读写文件。                             |
| w+   | 打开一个文本文件，允许读写文件。如果文件已存在，则文件会被截断为零长度，如果文件不存在，则会创建一个新文件。 |
| a+   | 打开一个文本文件，允许读写文件。如果文件不存在，则会创建一个新文件。读取会从文件的开头开始，写入则只能是追加模式。 |

 如果处理的是二进制文件，则需使用下面的访问模式来取代上面的访问模式：

```c
"rb", "wb", "ab", "rb+", "r+b", "wb+", "w+b", "ab+", "a+b"
```



##### 关闭文件

使用 fclose( ) 函数。这个函数实际上，会清空缓冲区中的数据，关闭文件，并释放用于该文件的所有内存。

```c
 int fclose( FILE *fp ); //关闭成功返回0，失败返回EOF
```



##### 写入文件

把字符或字符串写入到fp所指向的输出流中

```c
int fputc( int c, FILE *fp ); //成功返回写入的字符，失败返回EOF
```

```c
int fputs( const char *s, FILE *fp );//把null结尾的字符串写入输出流，成功返回非负值，失败返回EOF
```

也可以使用如下函数，把字符串写入文件

```c
 int fprintf(FILE *fp,const char *format, ...) 
```

> **注意：**请确保您有可用的 **tmp** 目录，如果不存在该目录，则需要在您的计算机上先创建该目录。
>
> **/tmp** 一般是 Linux 系统上的临时目录，如果你在 Windows 系统上运行，则需要修改为本地环境中已存在的目录，例如: **C:\tmp**、**D:\tmp**等。





##### 读取文件

读取单个字符

```c
int fgetc( FILE * fp );
```



用fscanf函数来从文件中读取字符串，但是在遇到第一个空格和换行符时，它会停止读取。

```c
int fscanf(FILE \*fp, const char \*format, ...)
```



从fp所指向的输入流中读取n -1个字符，并把读取的字符串复制到缓冲区buf，并在最后追加一个null字符来终止字符串。

```c
char *fgets( char *buf, int n, FILE *fp );
```



```c
FILE *fp = NULL;
fp = fopen("/tmp/test.txt","w+");
fprintf(fp,"hello c world\n");
fputs("hello c file  \n",fp);
fclose(fp);

fp = fopen("/tmp/test.txt","r");
char buff[255];
fscanf(fp,"%s",buff);
printf("1: %s\n", buff );

fgets(buff,255,fp);
printf("2: %s\n", buff );

fgets(buff,255,fp);
printf("3: %s\n", buff );
```

打印结果：

```
1: hello
2:  c world
3: hello c file  
```

首先，**fscanf()** 方法只读取了 hello，因为它在后边遇到了一个空格。其次，调用 **fgets()** 读取剩余的部分，直到行尾。最后，调用 **fgets()** 完整地读取第二行。



##### 二进制I/O函数

这两个函数都是用于存储块的读写 - 通常是数组或结构体。

```c
size_t fread(void *ptr, size_t size_of_elements, 
             size_t number_of_elements, FILE *a_file);
              
size_t fwrite(const void *ptr, size_t size_of_elements, 
             size_t number_of_elements, FILE *a_file);
```





#### 





### 提高篇





#### void指针





#### 二维数组







#### 指针数组



##### 字符指针数组





#### 动态分配内存



##### 常见内存错误



#### 读写函数

字符读写函数



字符串读写函数



二进制读写函数



#### 文件操作

文件系统的概念

文件打开

文件定位



#### 预处理和宏定义

C 预处理器只不过是一个文本替换工具而已，它们会指示编译器在实际编译之前完成所需的预处理。我们将把 C 预处理器（C Preprocessor）简写为 CPP。

所有的预处理器命令都是以井号（#）开头。它必须是第一个非空字符，为了增强可读性，预处理器指令应从第一列开始。下面列出了所有重要的预处理器指令：



##### 预处理

| 指令     | 描述                                                        |
| :------- | :---------------------------------------------------------- |
| #define  | 定义宏                                                      |
| #include | 包含一个源代码文件                                          |
| #undef   | 取消已定义的宏                                              |
| #ifdef   | 如果宏已经定义，则返回真                                    |
| #ifndef  | 如果宏没有定义，则返回真                                    |
| #if      | 如果给定条件为真，则编译下面代码                            |
| #else    | #if 的替代方案                                              |
| #elif    | 如果前面的 #if 给定条件不为真，当前条件为真，则编译下面代码 |
| #endif   | 结束一个 #if……#else 条件编译块                              |
| #error   | 当遇到标准错误时，输出错误消息                              |
| #pragma  | 使用标准化方法，向编译器发布特殊的命令到编译器中            |

```
#ifndef MESSAGE
   #define MESSAGE "You wish!"
#endif
```

这个指令告诉 CPP 只有当 MESSAGE 未定义时，才定义 MESSAGE。



##### 预定义宏

| 宏       | 描述                                                |
| :------- | :-------------------------------------------------- |
| __DATE__ | 当前日期，一个以 "MMM DD YYYY" 格式表示的字符常量。 |
| __TIME__ | 当前时间，一个以 "HH:MM:SS" 格式表示的字符常量。    |
| __FILE__ | 这会包含当前文件名，一个字符串常量。                |
| __LINE__ | 这会包含当前行号，一个十进制常量。                  |
| __STDC__ | 当编译器以 ANSI 标准编译时，则定义为 1。            |



```c
printf("File :%s\n", __FILE__ );
printf("Date :%s\n", __DATE__ );
printf("Time :%s\n", __TIME__ );
printf("Line :%d\n", __LINE__ );
printf("ANSI :%d\n", __STDC__ );
```

输出：

File : ~/ABP-study/codespace/c/hello_c/struct_test.c
Date :Nov 27 2020
Time :01:04:29
Line :63
ANSI :1



##### 预处理运算符

- ##### defined() 运算符

  用来确定一个标识符是否已经使用#define定义过

  ```
  #if !defined (MESSAGE)
     #define MESSAGE "You wish!"
  #endif
  ```

- 宏延续运算符（\）

  宏太长，换行接续

- ##### 字符串常量化运算符（#）

```
	#define message_for(a,b) printf(#a " and " #b ": We love you !\n")
	
	int main() {
    message_for(LiLei,HanMeimei);
  }
 
```

输出：

LiLei and HanMeimei: We love you !





##### 参数化的宏

简化函数调用，更直观地显示函数要表达的意义

```
#define square(x) ((x) * (x))
#define MAX(x,y) ((x) > (y) ? (x) : (y))
```

```c
int main(void)
{
   printf("Max between 20 and 10 is %d\n", MAX(10, 20));  
   return 0;
}
```

##### 



#### 头文件

如果一个头文件被引用两次，编译器会处理两次头文件的内容，这将产生错误。为了防止这种情况，标准的做法是把文件的整个内容放在条件编译语句中，如下：

```
#ifndef HEADER_FILE
#define HEADER_FILE

the entire header file file

#endif
```

这种结构就是通常所说的包装器 **#ifndef**。当再次引用头文件时，条件为假，因为 HEADER_FILE 已定义。此时，预处理器会跳过文件的整个内容，编译器会忽略它。



在有多个 **.h** 文件和多个 **.c** 文件的时候，往往我们会用一个 **global.h** 的头文件来包括所有的 **.h** 文件，然后在除 **global.h** 文件外的头文件中 包含 **global.h** 就可以实现所有头文件的包含，同时不会乱。方便在各个文件里面调用其他文件的函数或者变量。

```c
#ifndef _GLOBAL_H
#define _GLOBAL_H
#include <fstream>
#include <iostream>
#include <math.h>
#include <Config.h>
```



### 扩展篇（项目实战）

#### c语言的编译和运行过程



#### 



#### 多模块编译

##### 案例





#### 静态库和共享库





#### make和Makefile及案例



##### 案例







#### 项目实战 -- 猜拳游戏

##### 简介

玩家出拳1，2，3代表石头剪子布，电脑随机生成数，显示胜负，退出时显示排行榜；

```shell
Welcome to Guess Game !
======================

1.石头 2.剪刀 .3布 0.退出

Your turn : 
1
电脑出拳中 :
 2
 1
 0
电脑出拳:2
恭喜，你赢了
按回车继续

Your turn : 
0

 排行榜 
==========

姓名  总局数  胜利数     胜率 
===================

zhangsan 1 		1  		100.00%
```



```c
/**
 * 猜拳游戏
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/types.h>
#include <unistd.h>

#define SIZE 20
#define NAME "zhangsan"
#define PASS "123456"
#define YOU_WIN "恭喜，你赢了\n"
#define YOU_LOSE "遗憾，你输了\n"
#define PEACE "呦，平了\n"
#define ENTER_CONTINUE "按回车继续\n"

#define TRUE 1
//为简化调用，定义全局可替换的值、字符串或代码段
#define  CONTINUE(X) ({printf("%s",X);getchar();})
#define  MYFLASH \
        ({int ch = 0 ; \
            while((ch = getchar()) != '\n' \
            && ch != EOF);})

// 玩家结构体
typedef struct player {
    char name[SIZE];    //玩家姓名
    char pass[SIZE];    //登录密码
    int win;            //本次猜拳是否获胜
    int tol;            //记录该玩家总局数
    int victory;        // 总胜局
} player_t;

//玩家结构体指针变量，全局变量，存储于静态存储区
player_t *user;

/**
 * 创建玩家函数
 * @param 无
 * @return 玩家指针
 */
player_t *create_player(void) {
    user = (player_t *) calloc(1, sizeof(player_t)); //calloc向堆申请内存，并初始化（为0）
    if (NULL == user) return NULL; //申请完内存都要判空
    strcpy(user->name, NAME);
    strcpy(user->pass, PASS);
    return user;
}

/**
 * 菜单函数
 * @param 无
 * @return 无
 */
void menu(void) {
    system("clear");    //清屏
    printf("Welcome to Guess Game !\n");
    printf("======================\n\n");
    printf("1.石头 2.剪刀 .3布 0.退出\n\n");
}

/**
 * 电脑倒计时出拳
 */
void load(void) {
    int j = 2;
    printf("电脑出拳中 :");
    for (j = 2; j >= 0; j--) {
//        system("clear");
        printf("\n");
        printf(" %d", j);//行缓存，满一行或回车才输出
        fflush(stdout); //force flush
        sleep(1);
    }
    printf("\n");
}

/**
 * 随机数生成函数
 * @param num 石头，剪子，布 3种情况
 * @return 1～3之间的随机数
 */
int my_rand(int num) {
    srand(time(NULL) + num); //以当前时间作随机数种子
    return rand() % 3 + 1;    //取余结果范围[0,2]，加1得到石头剪刀布数值[1,3]

}

/**
 * 排行榜
 */
void rank_display() {
    printf("\n\n 排行榜 \n");
    printf("==========\n\n");
    printf("%10s %10s %10s %10s \n", \
    "姓名", "总局数", "胜利数", "胜率");// %10s 为统一输出10位长度的串，用于对齐
    printf("===================\n\n");
    double v = 0.0;
    //计算胜率
    if (user->tol > 0)
        v = (double) user->victory / user->tol * 100;
    printf("%10s %d %d  %5.2lf%%", \
    user->name, user->tol, user->victory, v);//5.2lf%%表示长度为5，精度为2， %% 为了显示百分号防转义
}

/**
 * 控制函数
 * @param 无
 * @return 无
 */
void control(void) {
    //玩家出拳
    int chose = 0;
    //电脑出拳
    int computer = 0;
    menu();
    while (TRUE) {
        do {
            printf("Your turn : \n");
            scanf("%d", &chose);
            MYFLASH;
//            int ch = 0 ;
//            while((ch = getchar()) != '\n' && ch != EOF);  通过getchar()把上边scanf()最后输入完成的回车换行符吃掉，防止对后续程序中的getChar逻辑造成影响
        } while (chose < 0 || chose > 3);  //处理非法输入
        if (chose == 0) return;
        load();
        computer = my_rand(getpid());
        printf("电脑出拳:%d\n", computer);
        //通过计算差值，判断玩家是否获胜
        int win = chose - computer;
        user->tol++;
        switch (win) {
            case -1:
            case 2:
                user->victory++;
                printf(YOU_WIN);
                CONTINUE(ENTER_CONTINUE);
                break;
            case 0:
                printf(PEACE);
                CONTINUE(ENTER_CONTINUE);
                break;
            default:
                printf(YOU_LOSE);
                CONTINUE(ENTER_CONTINUE);
//                printf("%s",ENTER_CONTINUE);getchar(); //等待回车符
                break;
        }
    }
}


int main() {
    user = create_player();
    if (NULL == user) exit(1);
    control();
    rank_display();
    return 0;
}
```



























#### 项目实战 -- 酒店管理系统









### 参考资源

- DevYK音视频学习（一）-- c语言

  https://juejin.im/post/6844904022827073543#heading-35
  
  
  
- 腾讯课堂-职坐标-c语言提高篇

- 腾讯课堂-职坐标-c语言扩展篇

  

- 传智播客c++深入浅出系列课 -- c语言提高

  链接：https://pan.baidu.com/s/1pOqODgwxIdbp9KORFwMtBA 
  提取码：uk9b 
  
- 

  





















