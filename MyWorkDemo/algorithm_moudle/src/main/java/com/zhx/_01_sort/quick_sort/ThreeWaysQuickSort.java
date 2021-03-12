package com.zhx._01_sort.quick_sort;

import com.zhx._01_sort.ISort;
import com.zhx._01_sort.insert_sort.InsertSort;

public class ThreeWaysQuickSort implements ISort {

    @Override
    public int[] sort(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void quickSort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        if (arr.length <= 15) {
            // TODO: 优化点：小规模(长度15以内)的数组，使用插入排序
        }
        //优化点：从arr[l,r]的范围中随机选一个作为pivot，交换到l位置去
        swap(arr, l, (int) (Math.random() * (r - l + 1) + l));

        //用如下几个变量维护三个区间：
        int v = arr[l];
        int lt = l; // 游标lt维护区间[l , lt] 使得区间内元素都不大于基准数v，从前向后移
        int gt = r + 1; // 游标gt维护区间[gt , r] 使得区间内元素都不小于基准数v，从后向前移
        int i = l + 1;// i表示下一个待考察的元素游标。
        while (i < gt) {
            if (arr[i] < v) { //交换到[0,lt]区间内
                swap(arr, i, lt + 1); // 第一个位置l 暂时放着基准数，所以从第二个位置开始
                lt++; //左半区游标后移
                i++; //左半区肯定是比基准数小的，所以 i 后移，考察下一个元素
            } else if (arr[i] > v) {
                swap(arr, i, gt - 1); //从arr[i] 元素换到后半区
                gt--; //右半区指针前移，因为从后面交换到 i 处的元素大小不确定，所以i 不变，继续比较
            } else {
                i++; //与基准数相等，i后移
            }
        }
        swap(arr, l, lt); //区间[l,lt]收尾交换位置，使得将l处pivot基准值放回等于v分区内

        quickSort(arr, l, lt - 1);
        quickSort(arr, gt, r);
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
