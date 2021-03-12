package com.zhx._05_tree;


import com.zhx.TestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * 二叉树算法 - 2021-02-26 二刷
 */
class TreeAlgo20210226 {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int value) {
            this.val = value;
            this.left = null;
            this.right = null;
        }
    }

    //遍历二叉树 -- 递归实现
//    public List<Integer> preOrderTraversal(TreeNode root) {
//        List<Integer> list = new ArrayList<>();
//        preOrderRecurse(root, list);
//        return list;
//    }
//
//    private void preOrderRecurse(TreeNode root, List<Integer> list) {
//        if (root == null) {
//            return;
//        }
//        list.add(root.val);
//        preOrderRecurse(root.left, list);
//        preOrderRecurse(root.right, list);
//    }
//
//    public List<Integer> inOrderTraversal(TreeNode root) {
//        List<Integer> list = new ArrayList<>();
//        inOrderRecurse(root, list);
//        return list;
//    }
//
//    private void inOrderRecurse(TreeNode root, List<Integer> list) {
//        if (root == null){
//            return;
//        }
//        inOrderRecurse(root.left, list);
//        list.add(root.val);
//        inOrderRecurse(root.right, list);
//    }
//
//    public List<Integer> postOrderTraversal(TreeNode root) {
//        List<Integer> list = new ArrayList<>();
//        postOrderRecurse(root,list);
//        return list;
//    }
//
//    private void postOrderRecurse(TreeNode root, List<Integer> list) {
//        if (root == null){
//            return;
//        }
//        postOrderRecurse(root.left,list);
//        postOrderRecurse(root.right,list);
//        list.add(root.val);
//    }

    //遍历二叉树 -- 非递归实现

    /**
     * 规则：根 -> 左 -> 右
     * 思想：
     * 利用辅助数据结构 -- 栈
     * 对于每棵子树，打印根节点后，入栈作为回溯点，深度遍历其左子树，左子树为空，则弹出回溯点，深度遍历其右子树
     *
     * @param root
     * @return
     */
    public List<Integer> preOrderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = root;
        while (node != null || !stack.isEmpty()) {
            //深度优先遍历左子树
            while (node != null) {
                result.add(node.val); //每个入栈的节点可以看作一棵子树的根结点。因为是前序遍历，访问到时即可打印。
                stack.push(node);// 保存为回溯点
                node = node.left;//深度优先遍历左子树
            }
            //到底了，弹出最近回溯点，尝试遍历右子树
            if (!stack.isEmpty()) {
                node = stack.pop();//每个弹出的节点可以看作一棵子树的根结点。
                node = node.right;
            }
        }
        return result;
    }

    public List<Integer> inOrderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = root;
        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.left;//深度遍历左子树
            }
            node = stack.pop();//每个弹出的节点可以看作一棵子树的根结点。
            result.add(node.val);//因为是中序遍历左->根➡>右，根节点弹出了，意味着没有下一层左子树了，所以打印子树根结点。
            node = node.right;
        }
        return result;
    }

    public List<Integer> postOrderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = root;
        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                result.add(node.val);
                stack.push(node);
                node = node.right;
            }
            if (!stack.isEmpty()) {
                node = stack.pop();
                node = node.left;
            }
        }
        Collections.reverse(result);
        return result;
    }

    //层序遍历二叉树
    public List<Integer> traversalBinaryTreeByLevel(TreeNode root) {
        if (root == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.pop();
            result.add(node.val);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        return result;
    }

    //之字型打印二叉树
    public static List<List<Integer>> bfsBaseOnQueueWithZhi(TreeNode root) {
        //我们用可变长的二维数组保存最终层序遍历结果
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        //链表实现了Dequeue接口
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.push(root);
        //保存每层遍历结果
        List<Integer> level;
        while (!queue.isEmpty()) {
            int count = queue.size();
            level = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                TreeNode node = queue.poll();
                if (node == null) {
                    continue;
                }
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                level.add(node.val);
            }

            //一层遍历完，判断是奇层还是偶层
            if (res.size() % 2 == 1) {
                Collections.reverse(level);
            }
            res.add(level);
        }
        return res;
    }


    //反转二叉树（对称二叉树、镜像）
    public TreeNode inverseTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode inverseLeftTree = inverseTree(root.right);
        TreeNode inverseRightTree = inverseTree(root.left);
        root.left = inverseLeftTree;
        root.right = inverseRightTree;
        return root;
    }

    public TreeNode mirrorTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
            TreeNode tmp = node.left;
            node.left = node.right;
            node.right = tmp;
        }

        return root;
    }

    // 二叉树最大深度
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return Math.max(maxDepth(root.left), maxDepth(root.right) + 1);
    }

    /**
     * 广度优先遍历方法，计算层数
     * 实现：
     * 使用queue结构，存节点
     *
     * @return
     */
    public int maxDepthByBFS(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int depth = 0;
        while (!queue.isEmpty()) {
            int count = queue.size();//队列中始终存放一层的节点
            for (int i = 0; i < count; i++) {
                TreeNode node = queue.poll();
                if (node == null) {
                    continue;
                }
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            depth++;
        }
        return depth;
    }

    public static void main(String[] args) {
        /**
         *       8
         *    6    10
         *  5   7 9   11
         *
         */
        TreeNode treeNode = new TreeNode(8);
        treeNode.left = new TreeNode(6);
        treeNode.left.left = new TreeNode(5);
        treeNode.left.right = new TreeNode(7);
        treeNode.right = new TreeNode(10);
        treeNode.right.left = new TreeNode(9);
        treeNode.right.right = new TreeNode(11);

        TreeAlgo20210226 treeAlgo = new TreeAlgo20210226();
        int depth = treeAlgo.maxDepthByBFS(treeNode);
        TestHelper.print("depth", depth + "");
    }
}
