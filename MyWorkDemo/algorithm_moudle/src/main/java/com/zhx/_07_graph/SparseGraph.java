package com.zhx._07_graph;

import java.util.Vector;

/**
 * 稀疏图 -- 邻接表
 */
public class SparseGraph implements IGraph {
    private int n; //节点数
    private int m; //边数
    private boolean directed; //是否有向图
    private Vector<Integer>[] g; //具体数据

    //构造函数
    public SparseGraph(int n, boolean directed) {
        assert n >= 0;
        this.n = n;
        this.m = 0;
        this.directed = directed;
        //g初始化为几个空的List，表示没有任何边
        g = new Vector[n];
        for (int i = 0; i < n; i++) {
            g[i] = new Vector<>();
        }
    }

    //返回节点数
    @Override
    public int V() {
        return n;
    }

    //返回边的个数
    @Override
    public int E() {
        return m;
    }

    /**
     * 为v、w号节点添加一个边(将图中两个节点相连)
     *
     * @param v 节点编号（数组下标）
     * @param w 节点编号（数组下标）
     */
    @Override
    public void addEdge(int v, int w) {
        assert v >= 0 && v < n;
        assert w >= 0 && w < n;

        g[v].add(w);
        //无向图，联系关系是双向的
        if (v != w && !directed) {
            g[w].add(v);
        }
        m++;
    }

    /**
     * 验证图中是否有v到w的边
     */
    @Override
    public boolean hasEdge(int v, int w) {
        assert v >= 0 && v < n;
        assert w >= 0 && w < n;
        //遍历g[v] 序列
        for (int i = 0; i < g[v].size(); i++) {
            if (g[v].elementAt(i) == w) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void show() {
        for (int i = 0; i < n; i++) {
            System.out.print("vertex " + i + ":\t");
            for (int j = 0; j < g[i].size(); j++)
                System.out.print(g[i].elementAt(j) + "\t");
            System.out.println();
        }
    }

    @Override
    public Iterable<Integer> adj(int v) {
        assert n >= 0 && v < n;
        return g[v];
    }

    public static void main(String[] args) {
        SparseGraph graph = new SparseGraph(7, false);
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 6);
        graph.addEdge(2, 3);
        graph.addEdge(2, 5);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);

        boolean ret = graph.hasEdge(2, 6);
    }
}
