package com.zhx;

import java.util.List;
import java.util.Vector;

public class TestHelper {

    public static final String SOURCE = "aaaaaaaabbbagpaaaaaaaaaabbbbbbabagbagaaaaaaaaaabagaaaaaabbabag";
    public static final String PATTERN = "aaaaaabbabag";

    public void a(int a){
    }

    public void a(char b){

    }

    public static void print(String tag , String... message){
       StringBuilder logStrBuilder = new StringBuilder(tag == null ? "TestHelper" : tag);
       logStrBuilder.append("#");
        for (String msg : message) {
            logStrBuilder.append("-->");
            logStrBuilder.append(msg);
            logStrBuilder.append("\n");
        }

        System.out.print(logStrBuilder.toString());
    }

    public static void print(String tag , List<? extends Object> list ){
        StringBuilder logStrBuilder = new StringBuilder(tag == null ? "TestHelper" : tag);
        logStrBuilder.append("#");
        for (Object msg : list) {
            logStrBuilder.append(String.valueOf(msg));
            logStrBuilder.append(" , ");
        }
        System.out.print(logStrBuilder.toString());
    }
}
