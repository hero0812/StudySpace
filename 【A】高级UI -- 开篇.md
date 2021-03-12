## 【A】高级UI -- 开篇

> 集中注意力是造就**心流**的关键 
>
> 米哈里问读者：什么是自得其乐？他自己的回答是：“就是‘拥有自足目标的自我’"
>
> 瑜伽的目标与本书所描述的心流活动多么接近 -- “控制内心所发生的一切”



### 输出倒逼输入

- View绘制机制，onMeasure onLayout ,onDraw方法的调用机制谈一下
- 硬件加速的底层实现。
- 双缓冲机制。
- 矢量图的原理。
- 【设计】设计检测过度绘制的工具，要具体到view。
- 

- 对ACTION_CANCEL事件的理解，什么时候触发 

- **事件分发机制/****点击事件流程，**touch事件的传递机制和函数调用栈

- 给出一个activity的布局：activity里包含viewgroup1，viewgroup1里包含viewgroup2，viewgroup2里包含button，问touch事件的传递和处理机制；

- **多点触控如何传递**

- 

- Android中你认为哪种动画最为强大？简述其实现原理

- 动画的实现

- 

- **RecyclerView优点，原理，**说下RecyclerView回收池原理

- 为什么RecyclerView加载首屏会慢一些，recycleview的性能指标

  

  





### 知识体系导图

- **自定义View/ViewGroup基本流程**
  - FlowLayout实战
  - 自定义Banner实战
- View绘制原理
- View事件分发机制
  - PhotoView事件分发
- 滑动冲突处理
  - 京东淘宝首页二级联动
  - NestedScrollView嵌套滑动机制分析
  - 协调者布局 CoordinatorLayout、CollapsingToolbarLayout
- Android 动画实战与原理
  
  - 灵动的锦鲤
- 常用UI组件 -- RecycleView原理
- 常用UI组件 -- FrameLayout、LinearLayout



### 文章导航

高级UI系列一：自定义View基本流程

高级UI系列二：布局解析与加载过程 --  Activiy、Window、View的关系

高级UI系列三：View的绘制过程（View的渲染）

高级UI系列四：View事件分发机制、多点触控

高级UI系列五：View事件分发实战--自定义PhotoView

高级UI系列六：解决滑动冲突、NestedScrollView嵌套滑动机制分析

高级UI系列七：RecycleView使用与原理

高级UI系列八：动画应用与原理 -- 自定义View实战活动的锦鲤



### 参考资源

- 享学课堂 -- 高级UI

  

- 源码分析Android UI何时刷新Choreographer

​		https://www.jianshu.com/p/d7be5308d06e

- VSYNC信号的理解

  https://www.jianshu.com/p/99617dacc7dc