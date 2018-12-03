/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maximumstrategicsavings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author zhuan
 */
public class MaximumStrategicSavings {

    /**
     * @param args the command line arguments
     */
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Graph graph = new Graph();
            String[] tokens = in.readLine().split(" ");
            graph.N = Integer.parseInt(tokens[0]);
            graph.M = Integer.parseInt(tokens[1]);
            graph.P = Integer.parseInt(tokens[2]);
            graph.Q = Integer.parseInt(tokens[3]);
            graph.readInGraph(in);

            Tree tree = graph.getShortestTree();

            long save = graph.getCost() - tree.getCost();
            System.out.println(save);
        } catch (Exception e) {
            System.out.println("IO Error");
        }

    }

}

class Graph {

    int N;
    int M;
    int P;
    int Q;
    int portalCounter = 0;
    int cityCounter = 0;

    LinkedList<Line> lines = new LinkedList();

    Tree getShortestTree() {
        Tree retTree = new Tree(N, M);

        for (Line line : this.lines) {
            if (this.allNodesResolved(retTree)) {
                break;
            }
            retTree.addLine(line);
        }
        return retTree;
    }

    long getCost() {
        long cost = 0;
        for (Line line : this.lines) {
            cost += line.cost * (line.idx == 0 ? M : N);
        }

        return cost;
    }

    void readInGraph(BufferedReader in) throws IOException {
        for (int i = 0; i < P; i++) {
            String[] tokens=in.readLine().split(" ");
            addLine(new Line(1, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Long.parseLong(tokens[2])));
        }
        for (int i = 0; i < Q; i++) {
            String[] tokens=in.readLine().split(" ");
            addLine(new Line(0, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Long.parseLong(tokens[2])));
        }
        Collections.sort(this.lines);
    }

    void addLine(Line line) {
        this.lines.add(line);
    }

    private boolean allNodesResolved(Tree tree) {

        if (tree.forests[0].treeCount() != 1 || tree.forests[1].treeCount() != 1) {
            return false;
        }
        return tree.forests[0].elementCount() == this.N && tree.forests[1].elementCount() == this.M;
    }
}

class Line implements Comparable<Line> {

    Long cost;
    int id1;
    int id2;
    int idx = 0;

    public Line(int lineIdx, int a, int b, long c) {
        this.cost = c;
        this.id1 = a;
        this.id2 = b;
        this.idx = lineIdx;
    }

    public void copy(Line line) {
        this.id1 = line.id1;
        this.id2 = line.id2;
        this.cost = line.cost;
    }

    @Override
    public int compareTo(Line o) {
        return this.cost.compareTo(((Line) o).cost);
    }
}

class Tree {

    Forest[] forests = new Forest[2];
    long cost = 0;
    int N, M;

    public Tree(int n, int m) {
        this.N = n;
        this.M = m;
        
        this.forests[0]=new Forest(this.N);
        this.forests[1]=new Forest(this.M);
    }

    long getCost() {
        return cost;
    }

    void addLine(Line line) {
        if (line.id1 == line.id2) {
            return;
        }
        Forest forest=this.forests[line.idx];
        int tree1 = forest.getRoot(line.id1);
        int tree2 = forest.getRoot(line.id2);

        if (tree1==tree2) return;
         forest.linkNode(tree1, tree2);

        forest = this.forests[(line.idx + 1) % 2];
        int linesToAdd = forest.treeCount();
        cost += line.cost * linesToAdd;
    }

}

class Forest {

    //ArrayList<HashSet<Integer>> trees = new ArrayList();
    int[] nodes;
    int elementCount = 1;
    int treeCount=0;


    public Forest(int n) {
        nodes=new int[n+1];
        for (int i=0;i<nodes.length;i++) {
            nodes[i]=i;
        }
        this.treeCount=n;
    }
    int getRoot(int id) {
        int node=id;
        while (nodes[node]!=node)
            node=nodes[node];
        return node;
    }


    void linkNode(int node1,int node2) {
        nodes[node2]=node1;
        elementCount++;
        treeCount--;
    }

    int treeCount() {
        return treeCount;
    }

    int elementCount() {
        return elementCount;
    }
}
