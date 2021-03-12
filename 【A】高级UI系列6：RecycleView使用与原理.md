## 【A】高级UI系列九：RecycleView使用与原理

### SelfCheck

- **RecyclerView优点，原理，**说下RecyclerView回收池原理？

- 为什么RecyclerView加载首屏会慢一些，recycleview的性能指标？
- 场景题：头条列表有多个不同的card，使用RecyclerView怎么解耦getViewType，获得不同的card



### 基本使用

RecycleView常规使用

```
RecycleView.setLayoutManager();

RecycleView.setAdapter();

RecycleView.setItemAnimator();

RecycleView.addItemDecoration();
```







### RecycleView原理



#### 绘制流程分析 

##### onMeasure







##### onLayout

dispatchLayout



###### prelayout

第一次布局时，并不会触发pre-layout。pre-layout只会在每次notify change时才会被触发，目的是通过saveOldPosition方法将屏幕中各位置上的ViewHolder的坐标记录下来，并在重新布局之后，**通过对比实现Item的动画效果**。



> 自定义LayoutManager注意要实现pre-layout

https://blog.csdn.net/weixin_39692245/article/details/111617184?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-5&spm=1001.2101.3001.4242





##### onDraw







#### 缓存复用原理 -- Recycler

RecycleView通过Recycler 4级缓存实现ViewHolder的缓存以及复用。













### RecycleView 性能优化

#### 卡顿问题

RecyclerView.Adapter里面的onCreateViewHolder()方法和onBindViewHolder()方法对时间都非常敏感。类似I/O读写，Bitmap解码一类的耗时操作，最好不要在它们里面进行。





#### 优化解决

* 优化的item布局

  1. 减少嵌套层级。合理运用<include>，<merge>，<ViewStub>等标签（***但是不推荐用约束布局***）。

  2. 减少过度绘制

  3. 减少xml文件解析时间。

     尝试用代码生成布局。

     

* 数据处理和视图加载分离

  1. 远端拉取数据--异步去完成耗时操作，比如通过Glide。
  2. 对数据进行缓存以提升二次加载速度，只对对于新增或者删除数据通过**DiffUtil ** 进行局部刷新。
  3. 数据拉下来之后加载到视图之前可能要经过一些加工处理，复杂计算或者格式转换之类。

  ```
  mTextView.setText(Html.fromHtml(data).toString())
  ```

  

* 基于交互场景优化

  addOnScrollListener添加对快速滑动的监听，当用户快速滑动时，停止加载数据操作。

  

更多参考：

https://blog.csdn.net/chuhe1989/article/details/108175646











### 进阶使用

#### 解耦getViewType方案





#### RecyclerView实现吸顶效果





#### 实现无限滚动列表

