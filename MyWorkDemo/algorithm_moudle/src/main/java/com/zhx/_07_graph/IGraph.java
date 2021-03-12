package com.zhx._07_graph;

/**
 * 定义图接口，图的基本方法
 */
interface IGraph {
    /**
     * 图中节点数量
     * @return
     */
    int V();

    /**
     * 图中边的数量
     * @return
     */
    int E();

    /**
     * 添加一条边
     * @param v
     * @param w
     */
    void addEdge(int v,  int w);

    /**
     * 判断给定两点v，w是否有边相连
     * @param v
     * @param w
     * @return
     */
    boolean hasEdge(int v, int w);

    /**
     * 打印图中所有节点
     */
    void show();

    /**
     * 返回图中一个顶点的所有邻边
     * @param v
     * @return
     */
    Iterable<Integer> adj(int v);
}
