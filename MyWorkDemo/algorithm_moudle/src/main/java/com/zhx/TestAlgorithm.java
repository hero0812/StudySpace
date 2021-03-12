package com.zhx;

import com.zhx._00_string.BruteForce;
import com.zhx._00_string.Kmp;
import com.zhx._01_sort.ISort;
import com.zhx._01_sort.SortTestHelper;
import com.zhx._01_sort.bubble_sort.BubbleSort;
import com.zhx._01_sort.selection_sort.SelectionSort;
import com.zhx._02_array.MyArray;
import com.zhx._03_linked_list_.SingleLinkedList;

/**
 * 算法模块入口
 */
public class TestAlgorithm {
    public static void main(String[] args) {
        // 00_string
//        BruteForce.bruteForce(TestHelper.SOURCE,TestHelper.PATTERN);
//        Kmp.kmp(TestHelper.SOURCE,TestHelper.PATTERN);

        // 01_sort
//        int[] oriArray = SortTestHelper.generateRandomArray(10, 0, 100);
//        SortTestHelper.printArray(oriArray);
//        ISort iSort = new SelectionSort();
//        iSort.sort(oriArray);
//        SortTestHelper.printArray(oriArray);

        // linked list
//        SingleLinkedList linkedList = new SingleLinkedList();
//        linkedList.insertToTail(1);
//        linkedList.insertToTail(2);
//        linkedList.insertToTail(3);
//        linkedList.insertToTail(4);
//
//        linkedList.deleteByValue(3);
//        linkedList.printAll();

        TestHelper.print(" Object.class.isArray()", Object.class.isArray()+"");
        TestHelper.print(" Object[].class.isArray()", Object[].class.isArray()+"");
    }
}
