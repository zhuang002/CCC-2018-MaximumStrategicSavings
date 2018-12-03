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
import java.util.Scanner;

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
            if (save == 1) {
                System.out.println(graph.N + " " + graph.M + " " + graph.P + " " + graph.Q);
            }
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

    ArrayList<Line> lines = new ArrayList();

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
        for (int i = 0; i < this.forests.length; i++) {
            this.forests[i] = new Forest();
        }
    }

    long getCost() {
        return cost;
    }

    void addLine(Line line) {
        if (line.id1 == line.id2) {
            return;
        }
        Forest forest=this.forests[line.idx];
        HashSet<Integer> tree1 = forest.getTreeById(line.id1);
        HashSet<Integer> tree2 = forest.getTreeById(line.id2);

        if (tree1 != null) {
            if (tree2 != null) {
                if (tree1 == tree2) {
                    return;
                } else {
                    if (tree1.size()>=tree2.size()) {
                        tree1.addAll(tree2);
                        forest.remove(tree2);
                    } else {
                        tree2.addAll(tree1);
                        forest.remove(tree1);
                    }
                }
            } else {
                forest.elementCount++;
                tree1.add(line.id2);
            }
        } else {
            if (tree2 != null) {
                forest.elementCount++;
                tree2.add(line.id1);
            } else {
                tree1 = new HashSet();
                tree1.add(line.id1);
                tree1.add(line.id2);
                forest.add(tree1);

            }
        }
        forest = this.forests[(line.idx + 1) % 2];
        int linesToAdd = forest.trees.size() + (line.idx == 0 ? M : N) - forest.elementCount();
        cost += line.cost * linesToAdd;
    }

}

class Forest {

    LinkedList<HashSet<Integer>> trees = new LinkedList();
    int elementCount = 0;

    HashSet<Integer> getTreeById(int id) {
        for (HashSet tree : this.trees) {
            if (tree.contains(id)) {
                return tree;
            }
        }
        return null;
    }

    boolean remove(HashSet<Integer> tree) {
        return trees.remove(tree);
    }

    boolean add(HashSet<Integer> tree) {
        this.elementCount+=tree.size();
        return trees.add(tree);
    }

    int treeCount() {
        return trees.size();
    }

    int elementCount() {
        /*int count = 0;
        for (HashSet<Integer> tree : trees) {
            count += tree.size();
        }
        return count;*/
        return elementCount;
    }
}
