package com.zhx._02_array;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MyArray0308 {
    /**
     * 旋转矩阵
     * 使用辅助数组
     * 对于矩阵内每一个元素matrix[i,j]
     * 交换后行下标 = 交换前列下标 j
     * 交换后列下标 = n - 交换前行下标i - 1
     */
    void rotate(int[][] matrix) {
        int n = matrix.length;
        int[][] newMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newMatrix[j][n - i - 1] = matrix[i][j];
            }
        }
        //复制回原数组
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = newMatrix[i][j];
            }
        }
    }

    /**
     * 两数之和
     * 有序数组中寻找两个数和为target
     *
     * @return 返回数据对
     */
    public int[] twoSum(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;
        while (low < high) {
            int sum = nums[low] + nums[high];
            if (sum == target) {
                return new int[]{low, high};
            } else if (nums[low] + nums[high] > target) {
                --high;
            } else {
                ++low;
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * 给定一个排序数组，你需要在原地删除重复出现的元素，
     * 使得每个元素只出现一次，返回移除后数组的新长度。
     * 示例：
     * 给定 nums = [0,0,1,1,1,2,2,3,3,4],
     * 函数应该返回新的长度 5, 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4。
     */
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int i = 0;//维护区间[0 , i] 使得 区间内是不重复元素，i指向最新出现的不重复元素
        for (int j = 1; j < nums.length; j++) {
            if (nums[i] != nums[j]) {
                i++;
                nums[i] = nums[j];
            }
        }
        return i + 1;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    /**
     * 旋转数组
     * 最简单直接的方式 ,将nums数据放到临时数组,
     * 再把临时数组的值放回原数组，并且利用偏移量k 和取模运算，计算新下标，从而实现元素向右移动k位。
     *
     * @param nums
     * @param k
     */
    public void rotate1(int[] nums, int k) {
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
     * 数组里有一个数字出现次数超过一半，让你找出来这个数是什么
     * 字典计数
     */
    public int majorityElement(int[] nums) {
        int length = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                int count = map.get(nums[i]);
                map.put(nums[i], count + 1);
            } else {
                map.put(nums[i], 1);
            }
        }
        //遍历Map，看谁value值大于数组一半
        for (Integer i : map.keySet()) {
            if (map.get(i) > nums.length / 2) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 三色球排序
     * 令：整数 0、 1 和 2 分别表示红色、白色和蓝色。
     */
    public void sortColors(int[] nums) {
        sort(nums, 0, nums.length - 1);
    }

    /**
     * 对[low,high]进行排序
     *
     * @param nums
     * @param low
     * @param high
     */
    private void sort(int[] nums, int low, int high) {
        if (low >= high) {
            return;
        }
        //三指针维护三个区间
        int i = low;
        int cur = low + 1;
        int j = high;
        while (cur <= j) {
            if (nums[cur] == 0) {
                swap(nums, i, cur);
                i++;
                cur++;
            } else if (nums[cur] == 2) {
                swap(nums, cur, high);
                high--;
            } else {
                cur++;
            }
        }
    }
}
