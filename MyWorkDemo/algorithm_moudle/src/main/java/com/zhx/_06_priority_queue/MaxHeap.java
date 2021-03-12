package com.zhx._06_priority_queue;

/**
 * 最大堆
 */
public class MaxHeap {

    private int[] data;

    //容量大小
    private int capacity;

    //元素个数
    private int size;

    public MaxHeap(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void insert(int value) {
        assert size + 1 <= capacity;
        //添加到数组末尾，表示完全二叉树最后一个叶子节点
        data[size++] = value;
        shiftUp(size); //对最后添加进数组的元素进行shift up操作
    }

    private void shiftUp(int k) {
        int index = k;
        //递归与父节点比较，找到父节点
        while (k > 1 && data[index] > data[index / 2]) {
            swap(data, index, index / 2);
            index /= 2;
        }
    }

    public int extractMax() {
        assert size > 0;
        //取出数组第一个元素（堆中最大值）
        int ret = data[1];
        //shift down
        swap(data, 1, size);
        size--;
        shiftDown(1);
        return ret;
    }

    private void shiftDown(int k) {
        while (2 * k <= size) {
            int j = 2 * k; //左孩子，完全二叉树一定存在左孩子

            //如果右孩子比较大
            if (j + 1 <= size && data[j + 1] > data[j]) {
                j += 1; //更新 j为右孩子下标，使变量j表示左右孩子中较大者的位置
            }

            //父节点比最大的孩子节点还大，直接break
            if (data[k] > data[j]) {
                return;
            }

            //父节点与较大的孩子节点交换位置
            swap(data, data[k], data[j]);
            k = j; //下一层
        }
    }

    /**
     * 交换数组中两位置的元素
     */
    private void swap(int[] arr, int left, int right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}
