## 【P】项目设计实践 --组件化



### 



### 组件化基础

#### 组件之间的通信方案

模块之间不得存在横向依赖，不使用ARouter的通信有以下几种：

1. EventBus ，缺点维护成本高
2. 广播，缺点不好管理
3. 隐式意图。AndroidManifest.xml 配置太多
4. 类加载方式。缺点，容易写错包名类名，但是依然不失为一个可行方案。
5. 使用全局Map方式。缺点，要注册很多对象。



##### 类加载方式

获取目标类的Class对象，调用startActivity

```java
Class targetClass = Class.forName("com.zhx.Other.DemoActivity");
Intent intent = new Intent(context,targetClass);
startActivity(intent);
```



##### 全局Map方式

本质还是类加载，只是进行了封装。

> RecordPathManager

```
private static Map<String,List<PathBean>> maps = new HashMap();//组名A + [Acitity1,Activity2...]

 /**
     * 将路径信息加入全局Map
     *
     * @param groupName 组名，如："personal"
     * @param pathName  路劲名，如："Personal_MainActivity"
     * @param clazz     类对象，如：Personal_MainActivity.class
     */
    public static void addGroupInfo(String groupName, String pathName, Class<?> clazz) {
        List<PathBean> list = maps.get(groupName);

        if (null == list) {
            list = new ArrayList<>();
            list.add(new PathBean(pathName, clazz));
            // 存入仓库
            maps.put(groupName, list);
        } else {
            // 存入仓库
            maps.put(groupName, list);
        }
        // maps.put(groupName, list);
    }

    /**
     * 只需要告诉我，组名 ，路径名，  就能返回 "要跳转的Class"
     * @param groupName 组名 oder
     * @param pathName 路径名  OrderMainActivity1
     * @return 跳转目标的class类对象
     */
    public static Class<?> startTargetActivity(String groupName, String pathName) {
        List<PathBean> list = maps.get(groupName);
        if (list == null) {
            Log.d(Config.TAG, "startTargetActivity 此组名得到的信息，并没有注册进来哦...");
            return null;
        }
        // 遍历 寻找 去匹配 “PathBean”对象
        for (PathBean pathBean : list) {
            if (pathName.equalsIgnoreCase(pathBean.getPath())) {
                return pathBean.getClazz();
            }
        }
        return null;
    }
```







### ARouter框架实现



#### APT应用



##### JavaPoet 代码生成

传统方式：包-》类—》方法顺序生成代码

JavaPoet 是面向对象思想生成代码：先写方法 -》 类 -〉 包

1. 生成方法

   ```
   MethodSpec.methodBuilder("findTargetClass")
   					.addModifiers()
   					.addParamters()
   					.build;					
   ```

   

2. 生成类

   ```
   
   ```

   

3. 生成包

```

```































































