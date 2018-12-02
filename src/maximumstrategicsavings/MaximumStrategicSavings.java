/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maximumstrategicsavings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author zhuan
 */
public class MaximumStrategicSavings {

    /**
     * @param args the command line arguments
     */
    static int N, M, P, Q;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // TODO code application logic here
        Graph graph = new Graph();

        graph.N = sc.nextInt();
        graph.M = sc.nextInt();
        graph.P = sc.nextInt();
        graph.Q = sc.nextInt();
        graph.readInGraph(sc);

        Tree tree = graph.getShortestTree();

        long save=graph.getCost() - tree.getCost();
        if (save==1) {
            System.out.println(graph.N+" "+graph.M+" "+graph.P+" "+graph.Q);
        }
        System.out.println(save);

    }

}

class Graph {

    int N;
    int M;
    int P;
    int Q;
    int portalCounter = 0;
    int cityCounter = 0;

    ArrayList<Line> cityLines = new ArrayList();
    ArrayList<Line> portalLines = new ArrayList();

    Tree getShortestTree() {
        Tree retTree = new Tree();

        resetLineCounters();
        Line line = new Line(0, 0, 0);
        int linesIdx = nextLine(line);

        while (linesIdx >= 0) {
            if (this.allNodesResolved(retTree)) break;
            int count = 0;
            if (linesIdx == 0) {
                count = this.M;
            } else {
                count = this.N;
            }
            Node node1 = null;
            Node node2 = null;
            for (int i = 1; i <= count; i++) {
                if (this.allNodesResolved(retTree)) break;
                if (linesIdx == 0) {
                    node1 = new Node(line.id1, i);
                    node2 = new Node(line.id2, i);
                } else {
                    node1 = new Node(i, line.id1);
                    node2 = new Node(i, line.id2);
                }
                retTree.addLine(new NodeLine(node1, node2, line.cost));
            }
            linesIdx=nextLine(line);
        }

        return retTree;
    }

    long getCost() {
        long cost = 0;
        for (Line line : this.cityLines) {
            cost += line.cost * N;
        }
        for (Line line : this.portalLines) {
            cost += line.cost * M;
        }
        return cost;
    }

    void readInGraph(Scanner sc) {
        for (int i = 0; i < P; i++) {
            this.cityLines.add(new Line(sc.nextInt(), sc.nextInt(), sc.nextLong()));
        }
        for (int i = 0; i < Q; i++) {
            this.portalLines.add(new Line(sc.nextInt(), sc.nextInt(), sc.nextLong()));
        }
        Collections.sort(this.cityLines);
        Collections.sort(this.portalLines);
    }

    private void resetLineCounters() {
        this.portalCounter = 0;
        this.cityCounter = 0;
    }

    private int nextLine(Line line) {
        if (this.portalCounter >= this.portalLines.size()) {
            if (this.cityCounter >= this.cityLines.size()) {
                return -1;
            } else {
                line.copy(this.cityLines.get(this.cityCounter));
                this.cityCounter++;
                return 1;
            }
        } else {
            if (this.cityCounter >= this.cityLines.size()) {
                line.copy(this.portalLines.get(this.portalCounter));
                this.portalCounter++;
                return 0;
            } else {
                Line line1 = this.portalLines.get(this.portalCounter);
                Line line2 = this.cityLines.get(this.cityCounter);
                if (line1.cost < line2.cost) {
                    line.copy(line1);
                    this.portalCounter++;
                    return 0;
                } else {
                    line.copy(line2);
                    this.cityCounter++;
                    return 1;
                }
            }
        }
    }

    private boolean allNodesResolved(Tree tree) {
        if (tree.forest.size() != 1) {
            return false;
        }
        HashSet<Node> nodes = (HashSet<Node>) tree.forest.toArray()[0];
        if (nodes.size() == this.M * this.N) {
            return true;
        }
        return false;
    }
}

class Line implements Comparable {

    Long cost;
    int id1;
    int id2;

    public Line(int a, int b, long c) {
        this.cost = c;
        this.id1 = a;
        this.id2 = b;
    }

    public void copy(Line line) {
        this.id1 = line.id1;
        this.id2 = line.id2;
        this.cost = line.cost;
    }

    @Override
    public int compareTo(Object o) {
        return this.cost.compareTo(((Line) o).cost);
    }
}

class Tree {

    ArrayList<HashSet<Node>> forest = new ArrayList();
    //ArrayList<NodeLine> lines = new ArrayList();
    long cost=0;

    public Tree() {
        super();
    }

    HashSet getTreeById(Node node) {
        for (HashSet tree : this.forest) {
            if (tree.contains(node)) {
                return tree;
            }
        }
        return null;
    }

    long getCost() {
        /*long cost = 0;
        for (NodeLine line : this.lines) {
            cost += line.cost;
        }*/
        return cost;
    }

    void addLine(NodeLine line) {
        if (line.node1.equals(line.node2)) return;
        HashSet<Node> tree1 = this.getTreeById(line.node1);
        HashSet<Node> tree2 = this.getTreeById(line.node2);
        if (tree1 != null) {
            if (tree2 != null) {
                if (tree1 == tree2) {
                    return;
                } else {
                    tree1.addAll(tree2);
                    boolean ret=this.forest.remove(tree2);
                    //this.lines.add(line);
                    cost+=line.cost;
                }
            } else {
                tree1.add(line.node2);
                //this.lines.add(line);
                cost+=line.cost;
            }
        } else {
            if (tree2 != null) {
                tree2.add(line.node1);
                //this.lines.add(line);
                cost+=line.cost;
            } else {
                tree1 = new HashSet();
                tree1.add(line.node1);
                tree1.add(line.node2);
                this.forest.add(tree1);
                //this.lines.add(line);
                cost+=line.cost;
            }
        }
    }
}

class Node {

    int id1;
    int id2;

    public Node(int node1, int node2) {
        this.id1 = node1;
        this.id2 = node2;
    }

    @Override
    public boolean equals(Object o) {
        return this.id1 == ((Node) o).id1 && this.id2 == ((Node) o).id2;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + this.id1;
        hash = 43 * hash + this.id2;
        return hash;
    }

}

class NodeLine {

    Node node1;
    Node node2;
    long cost;

    public NodeLine(Node n1, Node n2, long cost) {
        this.node1 = n1;
        this.node2 = n2;
        this.cost = cost;
    }
}
