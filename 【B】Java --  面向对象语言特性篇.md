##### ****【B】Java -- 面向对象语言特性篇

> 天行健，君子以自强不息；地势坤，君子以厚德载物。 -- 《易经》



### 输出倒逼输入

- 动态代理传入的参数都有哪些？非接口的类能实现动态代理吗？

- **反射与动态代理**, 动态代理的方法怎么初始化的 , cglib动态代理

- 序列parcelable,serializable差别？Java的serializable有个Id，你知道是有啥用的吗？

- **Object中的方法及如何使用？**Object的hashcode()用来干嘛，怎么自己实现hashcode？

- 接口和抽象类区别 

- Java在程序运行时多态是如何实现的？

- **深拷贝浅拷贝说说？**

- **Java 值传递问题，** 下面代码 str 值最终为多少？换成 Integer 值又为多少，是否会被改变？

- String StringBuffer StringBuilder在进行字符串操作时的效率；这里主要考察String在内存中是如何创建的； String为什么final、最大长度是多少？

- **两个值相等的 Integer 对象，== 比较，判断是否相等？**

- Java两个整型相加怎么知道有没有溢出

- Unicode和ASCII的区别？Unicode有哪些种类？Java是哪种？

  



### 泛型

#### 基本概念与使用

   	 Java 容器中所持有的数据类型被称为负载类型（payload type），Java5 开始 使用菱形句法比如<T>为容器类显式指定负载类型。其中T被称为类型参数，它可以代表任何一个具体的、实际的负载类型（即实际类型参数）。

`Plate<T>中的”T”称为类型参数` 

`Plate<Banana>中的”Banana”称为实际类型参数`

声明中带有一个或多个类型参数的类或者接口，那么这个结构就被称为泛型类型。

`	“Plate<T>” 整个成为泛型类型` 

`Plate<Banana>”整个称为参数化的类型 Plate 就叫原生态（原始）类型（raw type）`



**泛型存在的意义在于：**

- ​	简洁。引入泛型之前，从容器里取数据需要强转。
- ​	健壮性。泛型提供给了编译器一种类型安全检查机制，将类型安全检查提到编译期，而不用等到运行时。
- ​	架构设计的角度来说。代码灵活、复用，泛型起到访问权限约束的作用



**使用**

泛型类、泛型接口、泛型方法

虽然没有强制要求，但是建议日常代码设计中，使用如下类型参数命名：

E：代表元素（在Java集合框架中有广泛的应用）

K：代表键

N：代表数字

T：代表类型

V：代表值

S,U,V 等：第二，第三，第四个类型



#### 泛型擦除 

##### 什么是泛型擦除？为什么会有泛型擦除？

​	Java 泛型是在 Java1.5 以后出现的，为保持对以前版本的兼容，使用了擦除的方法实现泛型。***擦除是指在 一定程度无视类型参数 T，直接从 T 所在的类开始向上 T 的父类去擦除，如调用泛型方法， 传入类型参数 T 进入方法内部，若没在声明时做类似 public T methodName(T extends Father t){}，Java就进行了向上类型的擦除，直接把参数t当做Object类来处理，而不是传进去的T。 即在有泛型的任何类和方法内部，它都无法知道自己的泛型参数，擦除和转型都是在边界上 发生，即传进去的参数在进入类或方法时被擦除掉，但传出来的时候又被转成了我们设置的 T。*** 在泛型类或方法内，任何涉及到具体类型(即擦除后的类型的子类)操作都不能进行，如 new T()，或者 T.play()(play 为某子类的方法而不是擦除后的类的方法)。

​	Java虚拟机只认识class，不认识泛型类型，所以Java在编译时擦除了泛型信息，只保留原始类型，这样就不会产生新的类型到字节码。

![image-20200828151754290](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828151754290.png)

两个泛型类getClasss的结果是相同的，都是原始类型（raw rype）Box.class：![image-20200828151837418](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828151837418.png)

可见编译后泛型信息被擦除了。



从泛型类的声明来看泛型擦除原理：

![image-20200828154032983](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828154032983.png)



**对于泛型类/接口/方法，编译时，擦除类型变量，替换为限定类型，没有限定类型则替换为Object类型;**

对于泛型接口的实现类而言，声明时便已经确定了具体的类型参数，并实现了具体的接口方法，由于泛型擦除机制的存在，编译后泛型接口的实现类与泛型接口本身方法签名不同了，为解决这个问题，保证多态性（调用接口的方法时，可以正确调用到实现类的方法），java编译器自动生成了桥方法、并提供类型检查。

![image-20200828163313111](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828163313111.png)



##### 泛型擦除带来的问题？

1. 泛型类型变量不能使用基本数据类型

   A: 比如没有ArrayList<int>,只有ArrayList<Integer>.当类型擦除后，

      	ArrayList的原始类中的类型变量(T)替换成Object,但Object类型不能
      	
      	 存放int值

   

2. 不能直接创建（new）一个泛型（类型参数）的实例

   ![image-20200828180922556](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828180922556.png)

3. 泛型类不能使用instanceof 运算符

   ![image-20200828180651107](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828180651107.png)

      因为擦除后，ArrayList<String>只剩下原始类型，泛型信息String

      不存在了，所有没法使用 instanceof

   

4. 协变不遵循 -- 不能创建所谓的泛型数组

   什么是数组的协变？

   ​		A 类继承自 B，则 A[] 也是 B[] 的子类 ，即 instanceof 关系成立。

   且看Object[] 与 String[] 印证数组的协变特性：

   ![image-20200828213829506](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828213829506.png)

   

   但是数组协变是有风险的，把objects[1]  赋值为Box对象，编译虽然可以通过objects[]确实可以存任何对象，但是运行时报错 java.lang.ArrayStoreException 。**为了避免这种情况，编译器对于方法参数是有类型检查的。**

   >  “可以把List< String>传递给一个接受List< Object>参数的方法吗？”

   ```
“不可以”。
   ```

   
   
   
   
   同样的道理，由于泛型擦除的存在，泛型中类型参数退化成Object，形如List<Fruit>[]、List<Apple>[]这样的结构，是编译不通过的。
   
   
   
   但是使用通配符Box<?>[] 创建数组是可以的，它的元素可以是任意已Box类（及其子类）为原始类型的泛型类。



5. 泛型，继承和子类型

   类型参数间继承关系 与 泛型类之间的关系无关

   

   虽然类型参数 Fruit 与 Apple有继承关系，类型参数只表示容器中负载因子的类型而已，不能决定容器（泛型类）之间的继承关系。

   如下图，他们两个泛型类公共基类只有Object，也就意味着他们没有关系。Plate<Fruit>与Plate<Apple>为什么无关呢？

   ![image-20200828202302050](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828202302050.png)

   原始类型间继承关系具有传递性 :

   

![image-20200828190133969](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828190133969.png)







6. 通配符 与 泛型层级关系

   既然类型参数间的继承关系没有意义，那么如何实现泛型类之间的转型、传递呢？答案是通配符 “？”

   通配符代表“满足条件的任何一个实际类型参数”，T 表示声明时不确定、具体实现（调用）时，才能确定的，形式参数

7. ![image-20200828195619063](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828195619063.png)

   

   ![image-20200828195455419](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828195455419.png)



#### 通配符与PECS原则 -- 泛型在框架源码设计中的应用

我们回到这个问题 “可以把List< String>传递给一个接受List< Object>参数的方法吗？”，答案是不可以，如何实现相同语义的需求呢，使用通配符，把List< Object> 替换成List<?>也就是 List<? extends Object> ：

这样编译器就允许我们传入任何类型的List了，为了避免使用List<Object>时可能出现的java.lang.ArrayStoreException问题，编译器规定PECS原则 ：

​	***如果你只需要从集合中获得类型T , 使用<? extends T>通配符***

​	***如果你只需要将类型T放到集合中, 使用<? super T>通配符***

​	若 List是 生产者 -- 定义成 List<? extends X>，只允许取

​	若 List是消费者 -- 定义成 List<? super X> ，只允许存。



​	如果你既要获取又要放置元素，则不使用任何通配符。例如List<Apple>

​	PECS即 Producer extends  Consumer super， 为了便于记忆。

如图可见，向List<? extends Object> 中添加元素编译不通过。

![image-20200828213121516](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828213121516.png)









​    框架设计 -- JDK 、RxJava

![image-20200828194533164](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200828194533164.png)



> 在什么情况下可以在运行时获取泛型参数？
>

```

```



#### 面试问题

- 泛型的作用及使用场景

- 什么是泛型擦除，为什么要这样做，泛型擦除会带来什么问题？

- List<? extends E> 在编译后会变成什么样

- List<? extends T>和List <? super T>之间有什么区别

- 你可以把List< String>传递给一个接受List< Object>参数的方法吗，为什么

- C++模板和java泛型之间有何不同？

  ```
  C++ template是reified generic，Java generic用的是type erasure。
  C++是在call site做instantiate type，Java是在call site插入cast。
  C++ template在call site可以做inline，Java generic因为并没有在call site生成代码所以不行。
  C++在runtime没有额外的开销，Java在runtime有cast的开销。
  C++的每个reified generic type都有一份独立的代码，Java只有一份type erased之后的代码
  C++的type check是在编译时做的，Java的type check在编译期和运行期都要做一些工作。
  ```

  

#### 资源参考

https://zhuanlan.zhihu.com/p/35387281





### 注解

​	JDK 5.0引入的一种注释机制。提供有关于程序但不属于程序本身的数据。注解对它们注解的代码的操作没有直接影响。就像我们读书的时候写的注释，注释本身不属于文章内容，读者可以利用注释提供的信息更好地理解文章。Java的注释也是如此，在它被处理的时候，提供有用的信息。

![image-20200914165559345](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200914165559345.png)





#### 元注解与自定义注解

​	作常用的用于注解的注解，称为元注解。常用的元注解：表示声明的注解允许作用于哪些节点使用@Target；保留级别由@Retention 。注解的三个保留级别与应用场景：

- RetentionPolicy.SOURCE 

标记的注解仅保留在源级别中，并被编译器忽略。



- RetentionPolicy.CLASS 

标记的注解在编译时由编译器保留，但 Java 虚拟机(JVM)会忽略。



- RetentionPolicy.RUNTIME 

标记的注解由 JVM 保留，因此运行时环境可以使用它。



![image-20200907191856001](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200907191856001.png)



自定义注解的关键是使用“元注解”来注释自定义注解的类型的定义

![image-20200914180644684](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200914180644684.png)









#### 注解实践 

##### 1.通过自定义注解与反射实现页面跳转的参数注入：

new Intent().putExtra("name","Lance").putExtra("boy",true); //页面跳转携带参数

@Autowired
public String name;
@Autowired("boy")
public boolean isBoy

getIntent().getStringExtra("name");//使用属性名
getIntent().getBooleanExtra("boy");//读取注解元素



```java
/**
 * intent参数注入
 */
public static void injectAutowired(Activity activity) {
    Class<? extends Activity> cls = activity.getClass();

    //读取Intent
    Intent intent = activity.getIntent();
    Bundle extras = intent.getExtras();
    if (extras == null) {
        return;
    }
    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields) {
        if (field.isAnnotationPresent(Autowired.class)) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            //是否有自定义字段名
            String key = TextUtils.isEmpty(autowired.value()) ? field.getName() : autowired.value();
            if (extras.containsKey(key)) {
                Object obj = extras.get(key);
                //对于Parcelable[]需要特殊处理
                Class<?> componentType = field.getType().getComponentType();
                if (field.getType().isArray() && Parcelable.class.isAssignableFrom(componentType)) {
                    //手动创建Object数组并拷贝，进行数据类型转换
                    Object[] objs = (Object[]) obj;
                    Object[] objects = Arrays.copyOf(objs, objs.length, (Class<? extends Object[]>) field.getType());
                    obj = objects;
                }

                field.setAccessible(true);
                try {
                    field.set(activity,obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```



##### 2.注解实现一个提示功能：如果int的值大于了3需要提示。

```

```



### 反射 

​	一般情况下，我们使用某个类时必定知道它是什么类，是用来做什么的，并且能够获得此类的引用。于是我们直接对这个类进行实例化，之后使用这个类对象进行操作。



​	反射则是一开始并不知道我要初始化的类对象是什么，自然也无法使用 new 关键字来创建对象了。这时候，我们使用 JDK 提供的反射 API 进行反射调用。***反射就是在运行状态中,对于任意一个类,都能够知道这个类的所有属性和方法;对于任意一个对象,都能够调用它的任意方法和属性;并且能改变它的属性。***	具体来说Java反射机制主要提供以下功能：

1. 运行时构造任意一个类的对象
2. 在运行时获取或者修改任意一个类所具有的成员变量和方法
3. 在运行时调用任意一个对象的方法（属性）





#### Class对象

我们知道对象是类的实例，比如zhangSan 是Person类的一个实例，Person类中定义了Person类所拥有的属性和方法。而Class类则是封装了对于类文件的属性获取或操作手段。基于Class类，可以实现反射机制的主要功能。



##### 创建实例

通过反射来生成对象主要有两种方式：

- 使用Class对象的**newInstance() 方法**来创建实例

  ```
  Class<?> c = String.class;
  Object str = c.newInstance();
  ```

  

- 先通过Class对象的**getConstructor()方法**获取构造器对象，再调用**Constructor对象的newInstance()方法**创建实例，这种方法可以利用指定的构造器构造类的实例。

  ```
  Class<?> c = String.class;
  //指定构造器
  Constructor constructor = c.getConstructor(String.class);
  Object obj = construtor.newInstance("xxxxx");
  ```

  

> 获取构造器信息

```
Constructor getConstructor(Class[] params) // 获得使用特殊的参数类型的public构造函数(包括父类) Constructor[] getConstructors() //获得类的所有公共构造函数
Constructor getDeclaredConstructor(Class[] params) // 获得使用特定参数类型的构造函数(包括私有) Constructor[] getDeclaredConstructors() // 获得类的所有构造函数(与接入级别无关)
```



##### 获取类的成员属性(字段)信息

```
Field getField(String name) -- 获得命名的公共字段
Field[] getFields() -- 获得类的所有公共字段
Field getDeclaredField(String name) -- 获得类声明的命名的字段 
Field[] getDeclaredFields() -- 获得类声明的所有字段
```



*通过反射获取成员变量具体对象*

filed.get(Object owner)  

例，获取一个BaseClassLoader对象的dexPathList成员变量：

```java
						Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = clazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);	
						// 宿主的 类加载器
            ClassLoader pathClassLoader = context.getClassLoader();
            // DexPathList对象
            Object hostPathList = pathListField.get(pathClassLoader);
```



*通过反射赋值给具体成员变量*

filed.set(owner,value);

给宿主ClassLoader的pathList变量的dexElements属性设置新值，例：

```java
 Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            
 dexElementsField.set(hostPathList, newDexElements);
```

语义为：访问 dalvik.system.DexPathList的 dexElements 属性

为DexPathList对象 hostPathList 的dexElements属性赋值为newDexElements。



##### 调用方法

获取方法信息：

```java
Method getMethod(String name, Class[] params) -- 使用特定的参数类型，获得命名的公共方法
Method[] getMethods() -- 获得类的所有公共方法
Method getDeclaredMethod(String name, Class[] params) -- 使用特写的参数类型，获得类声明的命名的方法 
Method[] getDeclaredMethods() -- 获得类声明的所有方法
```

调用方法：

```java
public Object invoke(Object obj, Object... args)
```



> 反射调用静态方法

静态方法与具体对象无关， invoke(null)



##### 利用反射创建数组

数组在Java里是比较特殊的一种类型，它可以赋值给一个Object Reference 其中的Array类为

java.lang.reflect.Array类。我们通过Array.newInstance()创建数组对象，它的原型是:

```
public static Object newInstance(Class<?> componentType, int length);
```





##### 反射获取泛型信息 -- Type接口体系

​	反射的核心是从通过Class的对象，获取属性、构造方法、成员方法等信息，而Class类则是实现了Type接口：

![image-20200907192548753](./images/image-20200907192548753.png)

- TypeVariable  

 	泛型类型变量。可以泛型上下限等信息；

- ParameterizedType

 	具体的泛型类型，可以获得元数据中泛型签名类型(泛型真实类型)

- GenericArrayType

 	当需要描述的类型是泛型类的数组时，比如List[],Map[]，此接口会作为Type的实现。

- WildcardType

 	通配符泛型，获得上下限信息；



​	我们知道java编译时，将泛型信息擦除了，但是 java运行时仍可获取到泛型信息：

![image-20200907194257012](./images/image-20200907194257012.png)



> **泛型信息是如何保存到.class字节码文件、运行时被作为ParameterizedType的读取到的？**

![image-20200907214951973](./images/image-20200907214951973.png)

.java 编译成.class字节码文件后，生成signature签名，记录了泛型信息。

native层 生成Class对象、Filed对象，并将原始signature签名信息 赋值给**Filed signature**成员（Class元数据）。

反射得到Field ，调用**getGenericType()** 方法，最终由GenericDeclRepository 解析 signature字段 ，获取到到类型变量。









##### 反射获取注解信息

`设计：实现View注入，替代findviewbyid`

![image-20200907221411046](./images/image-20200907221411046.png)



##### 反射机制原理

https://zhuanlan.zhihu.com/p/152635495





#### 面试问题

> **反射为什么慢 ？ 为什么说反射机制会有性能问题？**

- 反射调用过程中会产生大量的**临时对象**，这些对象会占用内存，可能会导致频繁 gc，从而影响性能。比如Class.getMethods() ：

  ```java
    public Method[] getMethods() throws SecurityException {
          List<Method> methods = new ArrayList<Method>();
          getPublicMethodsInternal(methods);
          CollectionUtils.removeDuplicates(methods, Method.ORDER_BY_SIGNATURE);
          return methods.toArray(new Method[methods.size()]);
      }
  ```

  

- 反射调用方法时会**遍历方法数组**，并且会**检查可见性**等操作会耗时。

- 反射在**达到一定次数（15次）时，会动态编写字节码并加载到内存中**，这个字节码没有**经过编译器优化，也不能享受JIT优化**。

- 反射一般会涉及**自动装箱/拆箱和类型转换**，都会带来一定的资源开销。





### 动态代理

先说java类的完整生命周期

![image-20200907195141266](./images/image-20200907195141266.png)



##### 基本使用

相对于静态代理，动态代理在运行时再创建代理类和其实例，因此显然效率更低。要完成这个场景，需要在运行期动态创建一个Class。JDK提供了 Proxy 来完成这件事情。基本使用如下:

![image-20200907222427675](/Users/zhanghongxi/Library/Application Support/typora-user-images/image-20200907222427675.png)



##### 动态代理原理	

实际上，本质上与静态代理相同， Proxy.newProxyInstance 会创建一个代理了指定接口的Class，**与静态代理不同的是，这个Class不是由具体的.java源文件编译 而来，即没有真正的文件，只是在内存中按照Class格式生成了一个Class。**初始化获得method备用。

![image-20200907222752016](./images/image-20200907222752016.png)

创建代理类时，外部会传入InvokeHandler和被代理接口，InvokeHandler充当的时回调的角色。这个代理类内部，通过反射拿到代理接口的方法，生成对应的Method对象，外部调用到某个方法时，通过 InvokeHandler 的回调方法 invoke() ，将Method对象传回给外部，从而实现了所有被代理接口的所有方法，都通过invoke方法统一起来。



![image-20200907222948287](./images/image-20200907222948287.png)



###### 动态代理内部，是如何在内存中生成代理Class的？



WeakCache

```
proxyClassCache.get(loader, interfaces)
```

调用到native 的generateProxy方法：

```
Class<?> generateProxy(String name, Class<?>[] interfaces,
                                                 ClassLoader loader, Method[] methods,
                                                 Class<?>[][] exceptions);
```



#### 实践 -- View事件注入

定义注解

```java
/**
 * 定义Click事件注解，用以接收要注入的view id
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventType(listenerType = View.OnClickListener.class, listenerSetter = "setOnClickListener")
public @interface OnClick {
    /**
     * 定义id数组，可以绑定多个view
     *
     * @return
     */
    int[] value() default {View.NO_ID};

}
```



EventType注解用以指定对应事件的绑定方式 -- 调用哪个setter方法、设置哪个Listener对象：

```java
/**
 * 事件注解的注解
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventType {

    Class listenerType();

    /**
     * 用以接收方法名，供动态代理使用
     * @return
     */
    String listenerSetter() default "";

}
```



定义注入方法和InvokeHandler类

``

```java
/**
 * 事件注入
 */
public static void injectEvent(Activity activity) {

    Class<? extends Activity> cls = activity.getClass();
    //获得所有声明的方法
    Method[] methods = cls.getDeclaredMethods();
    for (Method method : methods) {
        Logger.e("method :: " + method.getName());
        //获得所有方法上声明的注解
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            Logger.e("annotation :: " + annotation.toString());
            //获取注解类型
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            Logger.e("annotationClass :: " + annotationClass.getSimpleName());
            if (annotationClass.isAnnotationPresent(EventType.class)) {
                //获取到事件注解在定义时指定的对应事件的Listener设置方法
                EventType eventType = annotationClass.getAnnotation(EventType.class);
                //listener对应接口
                Class listenerType = eventType.listenerType();
                //设置listner的方法名
                String listenerSetter = eventType.listenerSetter();

                //不需要知道具体是OnClick还是OnLongClick，知道接口和方法名即可动态代理之
                try {
                    //反射获取到需要绑定的 view ids
                    Method valueMethod = annotationClass.getDeclaredMethod("value");
                    int[] viewIds = (int[]) valueMethod.invoke(annotation);

                    method.setAccessible(true);
                    ListenerInvokeHandler<Activity> handler = new ListenerInvokeHandler<>(activity, method);
                    //创建接口的动态代理类对象，无需关注具体类型，代理方法invoke之后，activity中声明的method方法将被调用到，以执行业务逻辑
                    Object listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);

                    for (int viewId : viewIds) {
                        View view = activity.findViewById(viewId);
                        //获取指定的方法
                        Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                        //执行view.setXXXXListener方法，由动态代理对象invoke方法，进行传递调用
                        setter.invoke(view, listenerProxy);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```



```java
static class ListenerInvokeHandler<T> implements InvocationHandler {

    private Method method;

    private T target;

    public ListenerInvokeHandler(T target, Method method) {
        this.target = target;
        this.method = method;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Logger.e("ListenerInvokeHandler invoke method = " + method.getName());
        Logger.e("ListenerInvokeHandler this.method = " + this.method.getName());
        return this.method.invoke(target, args);
    }

```



#### 

- > 动态代理传入的参数都有哪些？非接口的类能实现动态代理吗？

- > **反射与动态代理**, 动态代理的方法怎么初始化的 , cglib动态代理



#### 资源参考

https://www.jianshu.com/p/f1a8356c615f

动态代理5种实现方式

https://cloud.tencent.com/developer/article/1461796



### 序列化







### 内部类

> static引用外部类变量



> 内部类为什么可以访问到外部类？持有外部类的引用，这个引用是在什么什么时候持有的





### 封装类问题

#### **Object类**

Object类中有哪些方法及如何使用？







#### Integer

##### 两个值相等的 Integer 对象，== 比较，判断是否相等？

```
Integer类中通过静态内部类 IntegerCache 缓存了一个Integer对象数组， 如果数值在 高位127 低位-128，即[-128,127] 区间内，通过Integer.valueOf(int)方法获得的Integer对象是从缓存池里取的。
```



#### String

##### java String类，为什么设计成不可变

```
1.设计角度上看。字符串是内存大户，所以要设计成保存到常量池中，重复利用。

2.安全性角度来看。url、密码等，在程序中以字符串形式存在于内存中，且保存在常量池里。如果可变，字符串值被篡改...

3.效率优化角度。String类中使用char[]数组保存数据，int hash缓存hashcode。String设计成不可变的，保证了hascode唯一性，hash值可以放心使用，不需要重新计算。这也是Map可以使用String作为Key的原因。
```







### 其他语言特性问题

#### 多态

##### invokeVirtual,invokeInteface,为什么你会觉得抽象类的效率高



##### 多态在内存中的实现 -- 虚函数表

![image-20210118104233612](./images/image-20210118104233612.png)

每个类，只要维持一个虚函数表就可以了。
每个对象，只要记录一个虚函数表的地址就可以了。





#### Java 值传递问题

> 下面代码 str 值最终为多少？换成 Integer 值又为多少，是否会被改变？

```java
public void test() {
    String str = "123";
    changeValue(str); 
    System.out.println("str值为: " + str);  // str未被改变，str = "123"
}

public changeValue(String str) {
    str = "abc";
}
```

**原理** ：[Java 程序设计语言总是采用按值调用](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2FSnailclimb%2FJavaGuide%2Fblob%2Fmaster%2Fdocs%2Fessential-content-for-interview%2FPreparingForInterview%2F%E5%BA%94%E5%B1%8A%E7%94%9F%E9%9D%A2%E8%AF%95%E6%9C%80%E7%88%B1%E9%97%AE%E7%9A%84%E5%87%A0%E9%81%93Java%E5%9F%BA%E7%A1%80%E9%97%AE%E9%A2%98.md%23%E4%B8%80-%E4%B8%BA%E4%BB%80%E4%B9%88-java-%E4%B8%AD%E5%8F%AA%E6%9C%89%E5%80%BC%E4%BC%A0%E9%80%92)，方法得到的是所有参数值的一个拷贝，即方法**不能修改**传递给它的任何参数变量的内容。基本类型参数传递的是参数副本，对象类型参数传递的是**对象地址；无法修改实参本身的值，但是可以根据实参的对象地址，去修改对象的属性**

```java
public void test() {
			  Student str = new Student();
			  str.setAge(18);
			  str.setName("小花");
			  changeValue(str); 
			  System.out.println("str值为: " + str.age + " 的 "+ str.name);  // str未被改变，str = "123"
		}

		public void changeValue(Student str) {
			    str.setAge(81);
			    str.setName("老花");
		}
```

输出：str值为: 81 的 老花



辨析Java值传递

https://cloud.tencent.com/developer/article/1645738?from=article.detail.1645927



#### 异常处理

Exception 和 Error都是Throwable类的子类

Error类对象一般由Java虚拟机生成并抛出，不可捕捉。表示出现的情况背离了可接受的一般法则。

Exception则是一种正常程序流程指令流中断。

不管有没有异常，finally中代码保证一定会执行

当try...catch中有return时，finally中的代码依然会继续执行



### 参考资源

