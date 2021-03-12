package com.zhx.javalib.thread;

public class TestVolatile {

    static volatile boolean isReady = false;

    static int number = 50;

    private static class PrintThread extends Thread{
        @Override
        public void run() {
            System.out.println("PrintThread is running..");
            while (!isReady);
            System.out.println("number = "+number);
        }
    }
    public static void main(String[] args) {
        new PrintThread().start();
        try {
            Thread.sleep(100l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isReady = true;
        System.out.println("main Thread is stop");
    }
}
