package com.zhx._01_sort;

import com.zhx.TestHelper;
import com.zhx._01_sort.insert_sort.InsertSort;
import com.zhx._01_sort.merge_sort.MergeSort;
import com.zhx._01_sort.quick_sort.QuickSort;

public class SortTest {

    public static void main(String[] args) {
        int[] array = SortTestHelper.generateRandomArray(10,1,1000);
        SortTestHelper.printArray(array);
        ISort sort = null;
//        sort = new InsertSort();
//        sort = new MergeSort();
        sort = new QuickSort();
        sort.sort(array);
        SortTestHelper.printArray(array);
    }
}
