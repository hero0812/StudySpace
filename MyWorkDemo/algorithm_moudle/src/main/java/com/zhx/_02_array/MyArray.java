package com.zhx._02_array;

import com.zhx.TestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MyArray {

    /**
     * 数组里有一个数字出现次数超过一半，让你找出来这个数是什么
     */
    public static int majorityElement(int[] nums) {
        int length = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            if (map.containsKey(nums[i])) {
                int tmp = map.get(nums[i]);
                map.put(nums[i], tmp + 1);
            } else {
                map.put(nums[i], 1);
            }
        }

        for (Integer i : map.keySet()) {
            if (map.get(i) > length / 2) {
                return i;
            }
        }
        return 0;
    }

    public static void moveAllZerosToEnd(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                list.add(nums[i]);
            }
        }

        for (int j = 0; j < list.size(); j++) {
            nums[j] = list.get(j);
        }

        for (int k = list.size(); k < nums.length; k++) {
            nums[k] = 0;
        }
    }

    /**
     * 输出整数n的二进制表示中1的个数
     * 9（1001）>>> 1 = 4(100)
     *
     * @param num
     */
    public static int print1(int num) {
        int n = 0;
        while (num != 0) {
            n += num & 1;
            num >>>= 1; //向右移动一位
        }
        return n;
    }
//    public static int print1(int num) {
//        int n = 0;
//        for (; num != 0; n++) {
//            num &= (num - 1);
//        }
//        return n;
//    }

    /**
     * @param nums
     */
    public static void moveAllZerosToEnd1(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }

        int k = 0; //定义变量k表示[0，k)区间里都是非0元素


    }

    /**
     * 三色球问题
     */
    public static void sortColors(int[] nums) {
        sort(nums, 0, nums.length - 1);
    }

    private static void sort(int[] nums, int low, int high) {
        if (high <= low) {
            return;
        }
        int left = low, current = low + 1, right = high;
        int v = nums[low];
        //以current指针指向的元素为基准，
        while (current <= right) {
            if (nums[current] < v) { //low处元素大于current处元素，交换
                swap(nums, left++, current++);
            } else if (nums[current] > v) {
                swap(nums, current, right--);
            } else {
                current++;
            }
        }
    }

    /**
     * 旋转数组
     *
     * @param nums
     * @param k
     */
    public static void rotate1(int[] nums, int k) {
        int length = nums.length;
        int[] temp = new int[length];

        //将nums数据放到临时数组
        for (int i = 0; i < length; i++) {
            temp[i] = nums[i];
        }
        //再把临时数组的值放回原数组，并且向右移动k位
        for (int i = 0; i < length; i++) {
            nums[(i + k) % length] = temp[i];
        }
    }

    /**
     * 多次反转
     *
     * @param nums
     * @param k
     */
    public static void rotate2(int[] nums, int k) {
        int length = nums.length;
        k %= length;
        reverse(nums, 0, length - 1);
        reverse(nums, 0, k - 1);//再反转前k个元素
        reverse(nums, k, length - 1);
    }

    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start++] = nums[end];
            nums[end--] = temp;
        }
    }

    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int i = 0;//nums[0,i]区间记录不重复元素
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != nums[i]) {
                //发现新元素
                i++;
                nums[i] = nums[j];
            }
        }
        return i + 1;
    }

    public static int[] merge(int[] arr, int[] arr2) {
        int[] temp = new int[arr.length + arr2.length];
        int i = 0, j = 0;
        for (int k = 0; k < temp.length; k++) {
            if (i >= arr.length) {
                temp[k] = arr2[j];
                j++;
            } else if (j >= arr2.length) {
                temp[k] = arr[i];
                i++;
            } else if (arr[i] < arr2[j]) {
                temp[k] = arr[i];
                i++;
            } else {
                temp[k] = arr2[j];
                j++;
            }
        }
        return temp;
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 5, 7};
        int[] nums1 = {3, 4, 6, 8};
//        rotate2(nums, 3);
        merge(nums, nums1);
    }
}
