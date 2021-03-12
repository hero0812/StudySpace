package com.zhx._03_linked_list_;

/**
 * 链表相关操作：
 * * 单链表反转
 * * 链表中环的检测
 * * 删除值为val的结点（虚拟头结点技巧应用）
 * * 删除链表倒数第n个结点（双索引技巧应用）
 * 交换相邻两个结点
 * 两个有序的链表合并
 * 求链表的中间结点
 * 判断是否为回文链表
 * LRU算法实现
 */
public class Solution {

    //定义链表结构
    static class Node {
        int val;
        Node next;

        Node(int x) {
            val = x;
        }

        Node(int[] arr) {
            if (arr == null || arr.length == 0) {
                return;
            }
            this.val = arr[0];
            int len = arr.length;
            int pos = 1;
            Node curNode = this;
            while (pos < len) {
                curNode.next = new Node(arr[pos]);
                curNode = curNode.next;
                ++pos;
            }
        }

        // 返回以当前Node为头结点的链表信息字符串
        @Override
        public String toString() {

            StringBuilder s = new StringBuilder("");
            Node curNode = this;
            while (curNode != null) {
                s.append(Integer.toString(curNode.val));
                s.append(" -> ");
                curNode = curNode.next;
            }
            s.append("NULL");
            return s.toString();
        }
    }

    /**
     * while 循环方式反转
     * 时间复杂度: O(n)
     * 空间复杂度: O(1)
     *
     * @param head
     * @return
     */
    public Node reverseList(Node head) {
        Node pre = null;
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;//改变指针指向前，先保存好next结点
            cur.next = pre;//反转next指向
            pre = cur;
            cur = next;
        }
        return pre;
    }

    /**
     * 检测环
     * 快慢指针，快指针追上慢指针，则为有环
     *
     * @param head
     */
    public boolean checkCircle(Node head) {
        if (head == null) {
            return false;
        }
        Node slow = head;
        Node fast = head.next;
        while (slow != null && fast != null) {
            slow = slow.next;
            fast = (fast.next != null ? fast.next.next : null);//每次走两步
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除值为val 的结点
     * 考虑到被删除的是head结点
     * 考虑删完之后null的情况
     *
     * @param head
     * @param val
     * @return
     */
//    public Node removeElement(Node head, int val) {
//        while (head != null && head.val == val) {
//            head = head.next;
//        }
//
//        if (head == null) {
//            return null;
//        }
//
//        Node cur = head;
//
//        while (cur.next != null) {
//            if (cur.next.val == val) {
//                Node delNode = cur.next;
//                cur.next = delNode.next;
//            }else{
//                cur = cur.next;
//            }
//        }
//        return head;
//    }

    /**
     * 使用虚拟头结点技巧，简化处理逻辑
     *
     * @param head
     * @param val
     * @return
     */
    public Node removeElement(Node head, int val) {
        Node dummyHeadNode = new Node(9999);//值随意指定，不会访问到
        dummyHeadNode.next = head;//让头结点能像普通结点一样处理

        Node cur = dummyHeadNode;
        while (cur.next != null) {
            if (cur.next.val == val) {
                Node delNode = cur.next;
                cur.next = delNode.next;
            } else {
                cur = cur.next;
            }
        }
        //虚拟头结点后边的部分就是结果
        return dummyHeadNode.next;
    }

    /**
     * 删除倒数第n个结点
     *
     * @param n
     */
//    public Node removeNthFromEnd(Node head, int n) {
//
//        Node dummyHead = new Node(0);
//        dummyHead.next = head;
//
//        Node cur = dummyHead.next;
//        int length = 0;
//        while (cur != null) {
//            cur = cur.next;
//            length++;
//        }
//        System.out.println("length = " + length);
//
//        int k = length - n;
//        if (k < 0){
//            System.out.println("非法索引 k = " + k);
//            return null;
//        }
//        cur = dummyHead;//找到要删除结点的前一个结点
//        for (int i = 0 ;i < k ; i ++){
//            cur = cur.next;
//        }
//        cur.next = cur.next.next;
//        return dummyHead.next;
//    }

    /**
     * 删除倒数第k个结点
     *
     * @param head
     * @param k
     * @return
     */
    public Node removeNthFromEnd(Node head, int k) {
        if (k <= 0) {
            //倒数第0个或负数个没有意义
            return head;
        }
        Node fast = head;
        int i = 1;
        while (fast != null && i < k) {
            fast = fast.next;
            ++i;
        }
        if (fast == null) {
            return head;
        }
        Node slow = head;
        Node pre = null;
        while (fast.next != null) {
            fast = fast.next;
            pre = slow;
            slow = slow.next;
        }

        if (pre == null) {
            head = head.next;
        } else {
            pre.next = pre.next.next;
        }
        return head;
    }

    /**
     * 交换相邻两个结点
     *
     * @param head
     * @return
     */
    public Node swapInPairs(Node head) {
        Node dummyHead = new Node(0);
        dummyHead.next = head;
        Node prev = dummyHead;
        while (prev.next != null && prev.next.next != null) {
            Node cur = prev.next;
            Node p = cur.next;
            Node next = p.next;
            //交换指向
            p.next = cur;
            cur.next = next;
            prev.next = p;
            //prev指针移到已经交换完位置的cur结点，开始下一对结点的互换
            prev = cur;
        }
        return dummyHead.next;
    }

    /**
     * 合并两个有序链表
     * 两个链表同时向后遍历，比对，将较小结点取出放到最终链表，
     * 后所在链表指针后移
     *
     * @param l1
     * @param l2
     * @return
     */
    public Node mergeTwoLists(Node l1, Node l2) {
        //构建最终链表，虚拟头结点（哨兵结点）
        Node dummyHead = new Node(0);
        Node p = dummyHead;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                p.next = l1;
                l1 = l1.next;
            } else {
                p.next = l2;
                l2 = l2.next;
            }
            p = p.next;
        }

        //一个链表遍历完成，将另一个链表剩下部分直接接到最终链表上
        if (l1 != null) {
            p.next = l1;
        }
        if (l2 != null) {
            p.next = l2;
        }
        return dummyHead.next;
    }

    /**
     * 求中间结点
     * 快慢指针从头开始跑，快指针每次走两步，慢指针每次走一步，快指针走完，慢指针就停在中间
     *
     * @param list
     * @return
     */
    public Node findMiddleNode(Node list) {
        if (list == null) {
            return null;
        }
        Node fast = list;
        Node slow = list;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    /**
     * 删除排序链表中重复元素
     * 当前结点与后继结点值相同，当前结点坐等后继结点指针后移，
     * 直到值不同的结点，一下子跳过去，达到删掉所有重复元素效果
     *
     * @param list
     * @return
     */
    public Node removeDuplicateElements(Node list) {
        Node current = list;
        while (current != null && current.next != null) {
            if (current.next.val == current.val) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
        return list;
    }

    /**
     * 大数相加
     */
    public Node numberAddAsList(Node l1, Node l2) {
        //head指针记录结果链表头，用作函数返回值；
        // tail指针参与全程按位相加计算过程，记录计算结果
        Node head = null, tail = null;
        int up = 0;
        while (l1 != null || l2 != null || up > 0) {
            //按位计算相加
            int n1 = l1 != null ? l1.val : 0;
            int n2 = l2 != null ? l2.val : 0;
            int sum = n1 + n2 + up;
            int ret = sum % 10;
            up = sum / 10;
            if (head == null) {
                head = tail = new Node(ret);
            } else {
                tail.next = new Node(ret);
                tail = tail.next;
            }
            //计算下一位
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null){
                l2 = l2.next;
            }
        }
        return head;
    }


    public static void main(String[] args) {


        Solution solution = new Solution();

        //test reverse linkde list
        int[] nums = {9, 9, 9, 9, 9};
        int[] nums2 = {9, 9, 9};
        Node head = new Node(nums);
        Node head2 = new Node(nums2);
        System.out.println(head);
        System.out.println(head2);

//        Node headReversed = solution.reverseList(head);
//        System.out.println(headReversed);

        // test check circle
//        Node head = new Node(0);
//        Node node1 = new Node(1);
//        Node node2 = new Node(2);
//        Node node3 = new Node(3);
//        head.next = node1;
//        node1.next = node2;
//        node2.next = node3;
//        node3.next = null;
//        boolean checkCircle = solution.checkCircle(head);
//        System.out.println("checkCircle = " + checkCircle);

        //test remove element with val
//        head = solution.removeElement(head, 3);
//        System.out.println(head);

        //test remove Nth From End
//        head = solution.removeNthFromEnd(head, -2);
//        System.out.println(head);

        //swap in pairs
//        head = solution.swapInPairs(head);
//        System.out.println(head);

        //merge two sorted linked list
//        Node mergedList = solution.mergeTwoLists(head, head2);
//        System.out.println(mergedList);

        //find Middle Node
        Node middleNode = solution.numberAddAsList(head,head2);
        System.out.println(middleNode);
    }
}
