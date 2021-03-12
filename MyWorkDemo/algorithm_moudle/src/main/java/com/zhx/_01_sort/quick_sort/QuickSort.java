package com.zhx._01_sort.quick_sort;

import com.zhx._01_sort.ISort;

/**
 * 快速排序
 * 1. 挑选一个元素，称为 “基准”（pivot）。
 * 2. 比基准数小的放到基准数前面，比基准数大的放到其后面。该过程称为partition（分区）操作。
 * 3. 对左右分区进行递归
 */
public class QuickSort implements ISort {
    /**
     * 对闭区间[0,n-1]排序
     *
     * @param array
     * @return
     */
    @Override
    public int[] sort(int[] array) {
        int length = array.length;
        quickSort(array, 0, length - 1);
        return array;
    }

    /**
     * 自顶向下地对闭区间[r,l]进行递归快速排序
     */
    private void quickSort(int[] arr, int l, int r) {
        if (l >= r) {//终止条件，细分至区间只有一个元素（l，r指针重合）
            return;
        }
        int p = partition(arr, l, r);//进行一次partition分捡，返回基准数位置
        quickSort(arr, l, p - 1);//对比基准数小的元素区间[l,p-1]递归
        quickSort(arr, p + 1, r);//对比基准数大的元素区间[p+1,r]递归
    }

    /**
     * 对arr[l...r]部分进行partition操作
     * @return 返回基准数位置p，使得arr[l,p-1] <arr[p] ;arr[p+1]>arr[p]
     */
    private int partition(int[] arr, int l, int r) {
        int pivot = arr[l]; //选为基准数
        int j = l; //游标j 维护区间[0, j] 使得区间内元素都小于基准数
        for (int i = l + 1; i < r; i++) { //i 表示下一个待考察的元素下标
            if (arr[i] < pivot) { //发现比基准数小
                swap(arr, i, j);  //交换进 [0, j] 区间
                j++;    //j 指针后移，准备放下一个小于基准数的元素
            }
        }
        swap(arr, l, j);//将pivot交换到左半部分结尾处，作为左右分界点
        return j;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
