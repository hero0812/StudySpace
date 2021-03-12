## 【A】Android 虚拟机原理 --开篇

### 输出倒逼输入

- Dalvik和Art虚拟机区别？
- dex对class的优化、odex对dex的优化、
- .oat与.art文件
- .java类双亲加载机制。pathClassLoader，dexClassLoader的区别? 一个dex的加载到一个java类的加载过程。



### Class文件格式

.class可以看作是Java虚拟机的可执行文件。.java文件可以通过javac命令编译成.class文件：



##### Class文件格式总览

![image-20210118140127809](./images/image-20210118140127809.png)



总体来看，class文件是有两种数据结构组合而成的：**无符号整数**、**表**。

> u4：表示长度为4字节，内容为无符号整数

- 根据规范Class文件前8个字节依次是magic（魔数）、minor_version、major_version。取值为0xCAFEBABY、class文件小版本信息、class文件大版本信息。

- constant_pool_count，表示常量池数组中元素个数。

  constant_pool （常量池）是一个存储cp_info的数组，注意索引从1开始。

  ```
  cp_info{
  	u1 tag; //1个字节， 表明常量项的类型
  	u1 info[]; //常量项具体内容
  }
  ```

  

- access_flags：该类的访问权限，比如：public、private之类的信息

- this_class和super_class：其值为指向常量池数组元素的索引。通过索引和常量池对应元素内容，我们可以知道本类和父类的类名（只是类名，不包含包名，类名最终用字符串描述）。

- interfaces_count和interfaces：表示类实现了多少个接口以及接口类的类名。同样是常量池索引。

- filed_count和fileds：成员变量数量和它们的信息。

- methods_count和methods：成员函数的数量和它们的信息。

- attributes_count和attributes：属性数量和属性描述集合。

  

##### 常量池

常量池本身是一个存储cp_info元素的表（数组），而常量项 cp_info 本身也是一个表：

```
cp_info{
	u1 tag; //1个字节， 表明常量项的类型
	u1 info[]; //常量项具体内容
}
```

tag 值是表的标识，JVM 解析 class 文件时，通过这个值来判断当前数据结构是哪一种表。



![img](./images/Cgq2xl6DCV6AcrLKAAIl1RRQwuM068.png)

以上 14 种表都有自己的结构，以 CONSTANT_Class_info 和 CONSTANT_Utf8_info 这两张表举例说明：

>  CONSTANT_Class_info 

```
table CONSTANT_Class_info {
    u1  tag = 7;
    u2  name_index;
}
```

name_index：是一个索引值，指向常量池中索引为name_index的常量表。



> CONSTANT_Utif8_info 

```
table CONSTANT_utf8_info{
	u1 tag;
	u2 length;
	u1[] bytes;
}
```

* Tag : 值为1，表示是CONSTANT_Utif8_info 类型的表。
* length ： length表示后面的 u1[] 的长度，比如lenght = 5，则表示接下来的数据是5个连续的u1类型数据。
* bytes ： u1类型数组，长度为length的值。

> **java中 String 字符串的长度有限制吗？**

在Java代码中声明的String字符串最终在class文件中的存储格式就是CONSTANT_utf8_info。因此一个字符串最大长度也就是u2 所能代表的最大值 65536 ,但是需要使用2个字节保存null值，因此一个字符串最大长度为 65536 - 2 = 65534.



实例

> Test.java

```
import java.io.Serializable;

public class Test implements Serializable, Cloneable {
    private int num = 1;
    String str = "abc";

    public int add(int i) {
        num = num + i;
        return num;
    }
}

对应的16进制字节码：
cafe babe 0000 0034 001d 0a00 0600 1509
0005 0016 0800 1709 0005 0018 0700 1907
001a 0700 1b07 001c 0100 036e 756d 0100
0149 0100 0373 7472 0100 124c 6a61 7661
2f6c 616e 672f 5374 7269 6e67 3b01 0006
3c69 6e69 743e 0100 0328 2956 0100 0443
6f64 6501 000f 4c69 6e65 4e75 6d62 6572
5461 626c 6501 0003 6164 6401 0004 2849
2949 0100 0a53 6f75 7263 6546 696c 6501
0009 5465 7374 2e6a 6176 610c 000d 000e
0c00 0900 0a01 0003 6162 630c 000b 000c
0100 1463 6f6d 2f7a 6878 2f6a 6176 616c
6962 2f54 6573 7401 0010 6a61 7661 2f6c
616e 672f 4f62 6a65 6374 0100 146a 6176
612f 696f 2f53 6572 6961 6c69 7a61 626c
6501 0013 6a61 7661 2f6c 616e 672f 436c
6f6e 6561 626c 6500 2100 0500 0600 0200
0700 0800 0200 0200 0900 0a00 0000 0000
0b00 0c00 0000 0200 0100 0d00 0e00 0100
0f00 0000 3000 0200 0100 0000 102a b700
012a 04b5 0002 2a12 03b5 0004 b100 0000
0100 1000 0000 0e00 0300 0000 0500 0400
0600 0900 0700 0100 1100 1200 0100 0f00
0000 2b00 0300 0200 0000 0f2a 2ab4 0002
1b60 b500 022a b400 02ac 0000 0001 0010
0000 000a 0002 0000 000a 000a 000b 0001
0013 0000 0002 0014 
```

```
反编译查看class字节码
javap -v Test.class
```

![image-20210118151641355](./images/image-20210118151641355.png)





##### 访问标志（access_flags）

常量池之后就是访问标志，

![image-20210118151119348](./images/image-20210118151119348.png)



##### 类索引、父类索引与接口索引计数器

在访问标志后的 2 个字节就是**类索引**，类索引后的 2 个字节就是**父类索引**，父类索引后的 2 个字节则是**接口索引计数器**。



##### 字段表

字段表的主要功能是用来描述类或者接口中声明的变量。这里的字段包含了类级别变量以及实例变量，但是不包括方法内部声明的局部变量。

**0002** 表示类中声明了 2 个变量（在 class 文件中叫字段），字段计数器之后会紧跟着 2 个字段表的数据结构。

![image-20210118153943590](./images/image-20210118153943590.png)



字段访问标志

![image-20210118154024033](./images/image-20210118154024033.png)



**值得注意的是：**

1. 字段表中不会列出从父类或者父接口中继承而来的字段。
2. 内部类中为了保持对外部类的访问性，会自动添加指向外部类实例的字段。



##### 方法表

方法表前也通过计数器记录有几个方法。

方法表结构：

```
CONSTANT_Methodref_info{

    u2  access_flags;        方法的访问标志

    u2  name_index;          指向方法名的索引

    u2  descriptor_index;    指向方法类型的索引

    u2  attributes_count;    方法属性计数器

    attribute_info attributes;

}

```



方法访问标志对应表：

![img](./images/Ciqah16DCWCAAdkIAAFVaPL8OfA302.png)



add方法，16进制字节码表示为：

![img](./images/Cgq2xl6DCWCAK8l0AAAU6SkPGdo841.png)

可以看出 add 方法的以下字段的具体值：

1. **access_flags** = **0001** 也就是访问权限为 public。
2. **name_index** = 0X**0011** 指向常量池中的第 17 个常量，也就是“add”。
3. **type_index** = 0X**0012**  指向常量池中的第 18 个常量，也即是 (I)。这个方法接收 int 类型参数，并返回 int 类型参数。



##### 属性表

属性可以分很多种， 可以用 attribute_info 数据结构伪代码表示

```
attribute_info {
		u2 attribute_name_index;		//属性名称，指向常量池中utf-8常量项的索引
		u4 attribute_length;				// 该属性具体内容的长度
		u1 info[attribute_lenght]; //属性具体内容
}
```

与常量池不一样的是，属性是由其名称来区别的。

> 属性名称和作用

![image-20210118155516228](./images/image-20210118155516228.png)



###### Code属性

一个函数的源码转换后得到的Java字节码就存储在Code属性中。

> Code_attribute数据结构

![image-20210118155950247](./images/image-20210118155950247.png)



- mac stack：该函数执行过程中需要最深多少栈空间（栈项）
- max_locals 表示该函数包含最多几个局部变量
- 





### DVM和ART如何对JVM进行优化的？

#### 1. 基于寄存器 vs 基于堆栈

移动设备大都使用ARM 的 CPU，其主要特点是通用寄存器比较多，而“因地制宜” ， 使用寄存器替代内存中的操作数栈，存取速度要快得多。基于寄存器 则使得Android 的字节码（smali）可以使用更简洁的是二地址指令和三地址指令。这一点优势便体现在dex文件对class文件的优化当中。



#### 2. Dex文件

传统JVM里加载、运行的是.class文件，而Android是把class文件进行了合并与优化，生成了classes.dex文件。

1）dex文件去除了class文件中的冗余信息（比如重复字符常量），并且结构更加紧凑，因此在dex解析阶段，可以减少I/O操作，提高类的查找速度。

2）Android 的字节码（smali）更多的是二地址指令和三地址指令，对比来看同一个Java方法编在class和dex文件中字节码格式：

> Java方法

```
public int add(int i , int j){
	return i + j;
}
```

> .class文件中字节码

![image (5).png](./images/Ciqc1F6qehmAcmWNAAB3egTLyn0886.png)



> .dex文件中字节码

![image (6).png](./images/Ciqc1F6qeiKAYxCiAAA-QtFxAzw826.png)



可以看出， Dalvik 字节码只需要 2 行指令。基于寄存器的指令明显会比基于栈的指令少，虽然增加了指令长度但却缩减了指令的数量，执行也更为快速。







#### 3. 内存管理与回收

区别主要体现在堆内存的管理。

JVM中年轻代、老年代，年轻代又分为Eden、Survivor，Survivor又分为s0、s1

DVM中堆内存被划分为2部分：Active Heap和Zygote Heap

![图片2.png](./images/CgqCHl6qe9WAY-x4AAHlcF3z4X8795.png)



图中的 Card Table 以及两个 Heap Bitmap 主要是用来记录垃圾收集过程中对象的引用情况，以便实现 Concurrent GC。



> ##### 为什么要分Zygote和Active两部分？

Zygote 进程是在系统启动时产生的，它会完成虚拟机的初始化，库的加载，预置类库的加载和初始化等操作，而在系统需要一个新的虚拟机实例时，Zygote 通过复制自身，最快速的提供一个进程；另外，对于一些只读的系统库，所有虚拟机实例都和 Zygote 共享一块内存区域，大大节省了内存开销。拷贝是一件费时费力的事情。因此，为了尽量地避免拷贝，Dalvik 虚拟机将自己的堆划分为两部分。

当启动一个应用时，Android 操作系统需要为应用程序创建新的进程，而这一步操作是通过一种写时拷贝技术（COW）直接复制 Zygote 进程而来。当 Zygote 进程在 fork 第一个应用程序进程之前，会将已经使用的那部分堆内存划分为一部分，把还没有使用的堆内存划分为另外一部分。前者就称为 Zygote 堆，后者就称为 Active 堆。以后无论是 Zygote 进程，还是应用程序进程，当它们需要分配对象的时候，都在 Active 堆上进行。这样就可以使得 Zygote 堆尽可能少地被执行写操作，因而就可以减少执行写时拷贝的操作时间。



##### Dalvik虚拟机堆与堆内存的分配

在Dalvik虚拟机中，堆实际上是一块匿名共享内存。Dalvik虚拟机并不是直接管理这块匿名共享内存，而是将它封装成一个mspace，交给c库来管理。Android 系统使用的 C 库 bionic 使用了 Doug Lea 写的 dlmalloc 内存分配器，也就是说，我们调用函数 malloc 的时候，使用的是 dlmalloc 内存分配器来分配内存。这是一个成熟的内存分配器，可以很好地解决内存碎片问题。







### Dex文件格式

#### Dex文件格式概貌

![image-20210118162142559](./images/image-20210118162142559.png)





* Dex文件头，类型为header_item

* string_ids :元素类型string_id_item,它存储和字符串相关的信息

* type_ids ：元素类型type_id_item，存储类型相关信息（TypeDescriptor描述）

* field_ids：元素类型field_id_item，存储成员函数信息，包括变量名、类型等。

* method_ids：元素类型method_id_item，存储成员函数信息包括函数名、参数和返回值类型等。

* class_defs：元素类型class_def_item，存储类的信息。

* data：Des文件重要的数据内容都存在data区域里。一些数据结构会通过xx_off这样的成员变量指向文件的某个位置，从该位置开始存储了对应数据结构内容，而xx_off的位置一般落在data区域里。

  



#### header_item

> header_item 数据结构伪代码

![image-20210118163107181](./images/image-20210118163107181.png)



- checksum，文件内容的校验和。该字段用于检查文件是否损坏。
- signature，该字段内容用于检查文件是否被篡改。
- file_size , 整个文件的长度，单位为字节，包括所有内容。
- header_size ，默认0x70 个字节
- endian_tag，表示文件内容按照什么字节序来处理。



#### string_id_item等

![image-20210118163622866](./images/image-20210118163622866.png)



#### class_def

![image-20210118163826441](./images/image-20210118163826441.png)



* class_idx , 指向type_ids ，代表本类的类型。

* access_flags，访问标志

* superclass_idx，指向type_ids ，代表基类的类型

* interfaces_off ， 如果本类实现了接口，则指向文件对应的位置，所存储的type_list。

* source_file_idx，指向string_ids，该类对应的源文件名。

* Annotations_off，存储和注解有关的信息。

* class_data_off，指向文件对应位置，那里存储了由class_data_item类型描述的细节信息。

* static_values_off，存储用来初始化类的静态变量的值或零值。

  

Andoid SDK提供dexdump工具用来解析dex文件：

```
dexdump -d dex 文件
```





### Android 虚拟机类加载机制



#### Android 虚拟机中的ClassLoader

![image-20210118192251519](./images/image-20210118192251519.png)



dex是Android虚拟机特有的文件而不是Java虚拟机可执行文件，所以在BaseDexClassLoader 中实现了从dex文件里查找class的逻辑。PathClassLoader 和DexClassLoader都继承自BaseDexClassLoader，两者都只有构造方法，没有具体类加载的逻辑。在Android O 之前两者唯一区别，是构造方法第二个参数 File optimizedDirectory, PathClassLoader 传null ， 而DexClassLoader可以指定具体文件路径。所以在Android O 之前 PathClassLoader 被认为只能用来加载已安装的apk的dex，而DexClassLoader支持加载apk、dex和jar，也可以从SD卡加载。

但是从Android O开始，从源码可见，两者在构造方法中File optimizedDirectory都传null ， 所以功能上不再有任何区别。

BootClassLoader 用来加载Android SDK中的类。





#### 双亲委托机制



ClassLoader类构造函数中，可以传入另一个ClassLoader作为parent，在调用加载器的loadClass()方法加载类时，首先将加载任务交给上述parent 加载器，依次递归，如果parent加载器可以完成类加载任务，就成功返回，否则才自己去加载。

![image-20210119012837036](./images/image-20210119012837036.png)



上面说过，PathClassLoader 和DexClassLoader都继承自BaseDexClassLoader，两者都只有构造方法，没有具体类加载的逻辑。所以当指定的parent 加载器加载不到，那么就要调用它们继承关系上讲的父类BaseDexClassLoader的 findClass方法来从dex中查找：

![image-20210119013857119](./images/image-20210119013857119.png)



DexPathList中持有一个dexElements数组，1个Element对象对应一个DexFile，所以BaseDexClassLoader中查找类的逻辑，遍历每个DexFile，通过DexFile的 native方法 loadClassBinaryName尝试查找class

![image-20210119014416311](./images/image-20210119014416311.png)



双亲委派机制时序图

![image-20210206121508168](./images/image-20210206121508168.png)



> 双亲委托机制的意义

1. 避免重复加载，当父加载器已经加载了该类的时候，就没有必要子ClassLoader再加载一次。
2. 安全性考虑，防止核心API库被随意篡改。









### ART虚拟机原理

#### ART 与 Dalvik区别与改进

##### 对于dex编译和运行区别

- Dalvik虚拟机的 JIT

Android 2.2之后，Dalvik虚拟机执行的是dex字节码，解释执行，支持 JIT即时编译。

Dalvik虚拟机在加载一个dex文件前，系统通过dexopt会对dex做优化，生成可执行文件odex，保存到/data/dalvik-cache目录，最后把apk中的dex文件删除。

- ART虚拟机的AOT

在安装时，dex2oat工具来编译应用，dex中的字节将被编译成本地机器码。

- Android N之后，混合运作

1. 最初安装应用不进行任何AOT编译，运行过程中，解释执行，对经常执行的方法进行JIT，经过JIT编译的方法将会记录到Profile配置文件中。
2. 当设备空闲和充电时，编译守护进程会运行，根据Profile文件对常用代码进行AOT编译。待下次运行时直接使用。

![image-20210118171253299](./images/image-20210118171253299.png)



##### 运行时堆划分区别

DVM的运行时堆主要由两个Space以及多个辅助数据结构组成，两个Space分别是**Zygote Space（Zygote Heap）和Allocation Space（Active Heap）**。Zygote Space用来管理Zygote进程在启动过程中预加载和创建的各种对象，Zygote Space中不会触发GC，所有进程都共享该区域，比如系统资源。Allocation Space是在Zygote进程fork第一个子进程之前创建的，它是一种进程私有空间。

1. 与DVM的GC不同的是，ART的GC类型有多种，主要分为Mark-Sweep GC和Compacting GC。ART的运行时堆的空间根据不同的GC类型也有着不同的划分，如果采用的是Mark-Sweep GC，运行时堆主要是由四个Space和多个辅助数据结构组成，四个Space分别是**Zygote Space、Allocation Space、Image Space和Large Object Space**。Zygote Space、Allocation Space和DVM中的作用是一样的。Image Space用来存放一些预加载类，Large Object Space用来分配一些大对象（默认大小为12k）。其中Zygote Space和Image Space是进程间共享的。 

2. 除了这四个Space，ART的Java堆中还包括两个Mod Union Table，一个Card Table，两个Heap Bitmap，两个Object Map，以及三个Object Stack



#### ELF文件格式





### 参考资源

- 官方文档 -- ART和Dalvik

https://source.android.com/devices/tech/dalvik/