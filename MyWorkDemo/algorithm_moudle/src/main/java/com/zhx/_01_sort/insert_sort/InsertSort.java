package com.zhx._01_sort.insert_sort;

import com.zhx._01_sort.ISort;

/**
 * 插入排序
 */
public class InsertSort implements ISort {

    @Override
    public int[] sort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n ;i++){
            int m = array[i];//待插入元素
            int j = i;// 从已排好序的元素中，从后向前比对
            for (; j > 0 && array[j-1] > m; j--) {
                array[j] = array[j-1];//m 较j-1处元素小，j-1元素向后移
            }
            array[j] = m;//当m较j-1处大，m应该插入在j-1位置之后，即j位置
        }
        return array;
    }
}
