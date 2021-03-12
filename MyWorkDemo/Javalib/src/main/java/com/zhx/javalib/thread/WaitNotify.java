package com.zhx.javalib.thread;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 */
public class WaitNotify {

    public static void main(String[] args) {
        Queue<Integer> list = new ArrayDeque<>();
        for (int i = 0; i < 1; i++) {
            new Thread(new Producer(list, 10)).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new Consumer(list)).start();
        }
    }

    static class Producer implements Runnable {

        private Queue<Integer> list;

        private int max;

        public Producer(Queue<Integer> list, int maxLength) {
            this.list = list;
            this.max = maxLength;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (list) {
                    while (list.size() == max) {
                        try {
                            System.out.println("生产者" + Thread.currentThread().getName() + "  list达到最大容量，进行wait");
                            list.wait();
                            System.out.println("生产者" + Thread.currentThread().getName() + "  唤醒，继续生产");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.offer(1);
                    list.notifyAll();
                    System.out.println("生产者" + Thread.currentThread().getName() + "  生产数据 " + 1);
                }
            }
        }
    }

    static class Consumer implements Runnable {

        private Queue<Integer> list;

        public Consumer(Queue<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (list) {
                    while (list.isEmpty()) {
                        try {
                            System.out.println("消费者" + Thread.currentThread().getName() + "  list为空，进行wait");
                            list.wait();
                            System.out.println("消费者" + Thread.currentThread().getName() + "  退出wait");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Integer element = (Integer) list.poll();
                    System.out.println("消费者" + Thread.currentThread().getName() + "  消费数据：" + element);
                    list.notifyAll();
                }
            }
        }
    }
}
