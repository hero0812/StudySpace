package com.zhx._07_graph;

/**
 * 深度优先遍历与联通分量
 */
public class Components {

    IGraph iGraph;

    boolean[] visited; //记录节点有没有被遍历过

    private int cCount; //联通分量个数

    int[] id; //保存联通分量id。相连的顶点id取相同值即在一个联通分量里，方便查询任意两顶点是否是相连的

    public Components(IGraph graph) {
        iGraph = graph;
        visited = new boolean[graph.V()];
        id = new int[graph.V()];
        cCount = 0;
        //初始化数组初值
        for (int i = 0; i < graph.V(); i++) {
            visited[i] = false;
            id[i] = -1;
        }
        //求联通分量
        for (int i = 0; i < graph.V(); i++) {
            if (!visited[i]) {
                dfs(i);
                cCount++;
            }
        }
    }

    /**
     * 对顶点v进行深度优先遍历
     *
     * @param v
     */
    private void dfs(int v) {
        visited[v] = true;
        id[v] = cCount;
        //返回图中一个顶点的所有相邻顶点
        for (int i : iGraph.adj(v)) {
            if (!visited[i]){ //递归进去
                dfs(i);
            }
        }
    }

    /**
     * 联通分量个数
     */
    public int count() {
        return cCount;
    }

    boolean isConnected(int v, int w) {
        assert v >= 0 && v < iGraph.V();
        assert w >= 0 && w < iGraph.V();
        return id[v] == id[w];
    }
}
