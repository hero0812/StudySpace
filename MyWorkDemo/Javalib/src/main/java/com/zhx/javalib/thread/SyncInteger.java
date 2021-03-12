package com.zhx.javalib.thread;

/**
 * 给Integer加锁
 */
public class SyncInteger {

    static class Worker implements Runnable {
        private Integer i;

        public Worker(Integer i) {
            this.i = i;
        }

        @Override
        public void run() {
            synchronized (i){
                i++;
                System.out.println(Thread.currentThread() + " i = "+i+" id@" + System.identityHashCode(i));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Worker worker = new Worker(1);
        for (int j = 0; j < 5; j++) {
            new Thread(worker).start();
        }
    }
}
