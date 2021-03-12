package com.zhx._01_sort.merge_sort;

import com.zhx._01_sort.ISort;

import java.util.Arrays;

/**
 * 归并排序基础实现
 */
public class MergeSort implements ISort {

    @Override
    public int[] sort(int[] array) {
        sort(array, 0, array.length - 1);
        return array;
    }

    /**
     * 归并排序使用递归思想
     *
     * @param arr
     * @param left
     * @param right
     */
    private void sort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        //优化1：对于小规模数组，使用插入排序
        //优化2：arr[mid]代表左边区间最大元素， arr[mid+1] 代表右侧区间最小元素，只有arr[mid]>arr[mid+1]才需要合并
        int mid = left + (right - left) / 2;
        sort(arr, left, mid);
        sort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    /**
     * 目的：为 arr[l , r] 区间排序
     * 思路：将 arr[l , mid] 和 arr[mid+1 ,r] 这两个有序数组合并
     */
    private void merge(int[] arr, int l, int mid, int r) {
        //申请辅助空间，复制arr[l,r]值
        int[] aux = Arrays.copyOfRange(arr, l, r + 1);
        //定义指针 i，j初始时指向两个数组起始位置
        int i = l;
        int j = mid + 1;
        //定义指针k ，指向原数组真实位置,区间[r,l]
        int k = l;
        for (; k <= r; k++) {
            //边界判断，i或j指针到结尾了的情况
            if (i > mid){
                arr[k] = aux[j - l];
                j++;
            }
            else if (j > r){
                arr[k] = aux[j - l];
                i++;
            }
            //比较i , j指向元素 ,这里注意aux数组下标要考虑偏移量 l
            else if (aux[ i - l] < aux[j - l]){
                arr[k] = aux[i - l];
                i++;
            }
            else{
                arr[k] = aux[j - l];
                j++;
            }
        }
    }


//    private void merge(int[] arr, int l, int mid, int r) {
//        int[] aux = Arrays.copyOfRange(arr, l, r + 1);
//        //初始化，i指向左半部分的起始索引位置l，j指向右半部分起始索引位置mid+1
//        int i = l;
//        int j = mid + 1;
//        for (int k = l; k <= r; k++) {
//            if (i > mid) {//左半部分元素已经全部处理完毕
//                arr[k] = aux[j - l];
//                j++;
//            } else if (j > r) {//右半部分已经全部处理完毕
//                arr[k] = aux[i - l];
//                i++;
//            } else if (aux[i - l] < aux[j - l]) {//左半部分元素小于右半部分元素
//                arr[k] = aux[i - l];
//                i++;
//            } else {//左半部分元素大于右半部分元素
//                arr[k] = aux[j - l];
//                j++;
//            }
//        }
//    }
}
