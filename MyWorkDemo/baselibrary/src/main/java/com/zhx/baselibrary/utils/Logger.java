package com.zhx.baselibrary.utils;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.util.LruCache;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 可以显示行数的log,减少打log的复杂度
 * Created by panxinghai on 2017/9/20.
 */

public class Logger {
    private static boolean sShowLog = true;
    /**
     * 是否保存为日志文件，debug apk专用，且自行预先解决外部存储权限
     */
    private static boolean sStoreLog = true;

    public static void p(String message, Object... params) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(message);
        sb.append(" ");
        for (Object p : params) {
            sb.append("=");
            sb.append(String.valueOf(p));
        }
        Logger.d(sb.toString());
    }

    public static void v(String msg) {
        doLog(1, null, msg);
    }

    public static void v(String tag, String msg) {
        doLog(1, tag, msg);
    }

    public static void e(String msg) {
        doLog(2, null, msg);
    }

//    public static void e(String tag, String msg) {
//        doLog(2, tag, msg);
//    }

    public static void e(String format, Object... param) {
        doLog(2, null, String.format(Locale.getDefault(), format, param));
    }

    public static void d(String msg) {
        doLog(3, null, msg);
    }

    public static void v(String format, Object... param) {
        doLog(1, null, String.format(Locale.getDefault(), format, param));
    }

    private static void doLog(int id, String tag, String message) {
        if (!sShowLog) {
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int index = 4;
        String className = stackTraceElements[index].getFileName();
        String methodName = stackTraceElements[index].getMethodName();
        int lineNumber = stackTraceElements[index].getLineNumber();

        String t = tag == null ? "WMOTP" : tag;
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder sb = new StringBuilder();
        if (sShowLog) {
            sb.append(time()).append("[(").append(className).append(":").append(lineNumber).append(")#").append(methodName).append("]");
        }
        sb.append(message);
        switch (id) {
            case 1:
                Log.v(t, sb.toString());
                break;
            case 2:
                Log.e(t, sb.toString());
                break;
            case 3:
                Log.d(t, sb.toString());
                break;
            default:
        }
        //写入外部存储
        write(sb.append("\n").toString());
    }

    /**
     * 标识每条日志产生的时间
     *
     * @return
     */
    private static String time() {
        return "["
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
                System.currentTimeMillis())) + "]";
    }

    /**
     * 保存到日志文件
     *
     * @param content
     */
    public static synchronized void write(String content) {
        if (!sStoreLog) {
            return;
        }

        try {
            FileWriter writer = new FileWriter(getFile(), true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日志文件路径
     *
     * @return
     */
    public static String getFile() {
        File cacheDir = new File(Environment.getExternalStorageDirectory() + File.separator + "com.zhx.wmsecuritytoken" + File.separator + "log");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        File filePath = new File(cacheDir, "log_" + date() + ".txt");

        return filePath.toString();
    }

    /**
     * 以年月日作为日志文件名称
     *
     * @return
     */
    private static String date() {
        return "["
                + new SimpleDateFormat("yyyy-MM-dd").format(new Date(
                System.currentTimeMillis())) + "]";
    }
}
