package com.zhx.baselibrary.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * 插件化加载类
 */
public class LoadUtil {
    private static final String pluginApkPath = "android:///assets_captcha/x5.apk";
    public static void loadClass(Context context) {
        /**
         * 1. 获取宿主dexElements
         * 2. 获取插件dexElements
         * 3. 合并两个dexElements
         * 4. 将新的dexElements赋给宿主dexElements
         */
        try {
            Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListFiled = clazz.getDeclaredField("pathList");
            pathListFiled.setAccessible(true);

            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            //宿主的类加载器
            ClassLoader pathClassLoader = context.getClassLoader();
            //DexPathList 类的对象
            Object hostPathList = pathListFiled.get(pathClassLoader);
            //宿主的dexElements
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            //插件的类加载器，自定义DexClassLoader
            ClassLoader dexClassLoader = new DexClassLoader(pluginApkPath,context.getCacheDir().getAbsolutePath(),null,pathClassLoader);
            //DexPathList对象
            Object pluginPathList = pathListFiled.get(dexClassLoader);
            Object[] pluginDexElements = (Object[]) dexElementsField.get(pluginPathList);

            //新的宿主dexElements = 宿主dexElements + 插件dexElements
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(),
                    hostDexElements.length+pluginDexElements.length);
            //合并数组
            System.arraycopy(hostDexElements, 0, newDexElements,
                    0, hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, newDexElements,
                    hostDexElements.length, pluginDexElements.length);
            //赋值回给宿主dexElements
            dexElementsField.set(hostPathList,newDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startPluginActivity(){

    }
}
