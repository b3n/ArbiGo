/*
 * The MIT License
 *
 * Copyright 2015 Ben Lloyd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.shobute.arbigo.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Ben Lloyd
 */
public class Graph implements Serializable {

    private final Set<Node> nodes;
    private final int diameter;
    private Color colour;

    public Graph() {
        nodes = new HashSet<>();
        diameter = 10;
        colour = Color.BLACK;
    }

    /**
     * Constructs a graph made of an nxn grid, where each node is 100 pixels
     * apart, starting at (0, 0).
     * 
     * @param n The number of rows and columns the grid should contain.
     */
    public Graph(int n) {
        this();

        Node[][] myNodes = new Node[n][n];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                myNodes[x][y] = new Node(new Point(x * 100, y * 100));
            }
        }

        for (int x = 0; x < myNodes.length; x++) {
            for (int y = 0; y < myNodes[x].length; y++) {
                if (y + 1 < myNodes[x].length) {
                    myNodes[x][y].addAdjacentNode(myNodes[x][y + 1]);
                }
                if (y > 0) {
                    myNodes[x][y].addAdjacentNode(myNodes[x][y - 1]);
                }
                if (x + 1 < myNodes.length) {
                    myNodes[x][y].addAdjacentNode(myNodes[x + 1][y]);
                }
                if (x > 0) {
                    myNodes[x][y].addAdjacentNode(myNodes[x - 1][y]);
                }
                addNode(myNodes[x][y]);
            }
        }

    }

    public Dimension getSize() {
        if (nodes.size() < 2) {
            return new Dimension();
        }
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;
        for (Node node : nodes) {
            if (node.x < xmin) {
                xmin = node.x;
            } else if (node.x > xmax) {
                xmax = node.x;
            }
            
            if (node.y < ymin) {
                ymin = node.y;
            } else if (node.y > ymax) {
                ymax = node.y;
            }
        }
        return new Dimension(xmax - xmin, ymax - ymin);
    }
    
    /**
     * Returns the shortest distance between any two points.
     * @return
     */
        
    public int getShortestRadius() {      
        Node[] nodesByX = new Node[nodes.size()];
        Node[] nodesByY = new Node[nodes.size()];
        int i = 0;
        for (Node n : nodes) {
            nodesByX[i] = n;
            nodesByY[i] = n;
            i++;
        }
        Arrays.sort(nodesByX, Node.xComparator());
        Arrays.sort(nodesByY, Node.yComparator());
        
        return (int) (getShortestDistance(nodesByX, nodesByY) / 2);
    }
    
    // https://en.wikipedia.org/wiki/Closest_pair_of_points_problem
    private double getShortestDistance(Node[] nodesByX, Node[] nodesByY) {
     
        if (nodesByX.length <= 1) return Integer.MAX_VALUE;
        if (nodesByX.length <= 2) return nodesByX[0].distance(nodesByX[1]);
        
        int mid = nodesByX.length/2;
        
        Node[] leftX = Arrays.copyOfRange(nodesByX, 0, mid);
        Node[] rightX = Arrays.copyOfRange(nodesByX, mid + 1, nodesByX.length - 1);
        
        int midX = leftX[leftX.length - 1].x;
        
        Node[] leftY = new Node[nodesByY.length];
        Node[] rightY = new Node[nodesByY.length];
        int i = 0;
        int j = 0;
        for (Node node : nodesByY) {
            if (node != null) {
                if (node.x <= midX) leftY[i++] = node;
                else rightY[j++] = node;
            }
        }
        
        double distLeft = getShortestDistance(leftX, leftY);
        double distRight = getShortestDistance(rightX, rightY);
        
        double distMin = Math.min(distLeft, distRight);
        
        Node[] yStrip = new Node[nodesByY.length];
        i = 0;
        for (Node node : nodesByY) {
            if (node != null && Math.abs(node.x - midX) < distMin) yStrip[i++] = node;
        }
        
        for (i = 0; i < mid; i++) {
            j = i + 1;
            while (j <= yStrip.length && yStrip[j] != null && yStrip[j].y - yStrip[i].y < distMin) {
                double dist = yStrip[j].distance(yStrip[i]);
                if (dist < distMin) distMin = dist;
                j++;
            }
        }
        
        return distMin;
    }

    /**
     *
     * @param point
     * @return
     */
    public Node nodeAt(Point point) {
        if (point == null) {
            return null;
        }

        for (Node node : nodes) {
            if (node.distance(point) < getShortestRadius()) {
                return node;
            }
        }
        return null;
    }

    public Node nodeAt(int x, int y) {
        return nodeAt(new Point(x, y));
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     *
     * @param point
     * @return
     */
    public Boolean addNode(Point point) {
        if (this.nodeAt(point) == null) {
            return addNode(new Node(point));
        }
        return false;
    }

    public final Boolean addNode(Node node) {
        return nodes.add(node);
    }

    public Boolean removeNode(Node nodeToRemove) {
        // TODO: Is there a better data structure to make this faster?
        for (Node node : nodes) {
            node.removeAdjacentNode(nodeToRemove);
        }
        return nodes.remove(nodeToRemove);
    }

    /**
     *
     * @return
     */
    public Set<Node> getNodes() {
        return nodes;
    }

    public int getDiameter() {
        return diameter;
    }

    public Point closestOnGrid(Point point) {
        int x = point.x;
        int y = point.y;
        int xmod = x % (getDiameter() * 2);
        int ymod = y % (getDiameter() * 2);
        x -= xmod;
        y -= ymod;
        if (xmod > getDiameter()) {
            x += getDiameter() * 2;
        }
        if (ymod > getDiameter()) {
            y += getDiameter() * 2;
        }
        return new Point(x, y);
    }

    public void paintNodes(Graphics2D g2d) {
        g2d.setColor(colour);
        int z = getDiameter();
        for (Node node : getNodes()) {
            //if (selectedNodes.contains(node)) g2d.setColor(Color.BLUE);
            g2d.fill(new Ellipse2D.Float(node.x - z / 2, node.y - z / 2, z, z));
            g2d.setColor(colour);
        }
    }

    public void paintEdges(Graphics2D g2d) {
        g2d.setColor(colour);
        g2d.setStroke(new BasicStroke(2));
        for (Node node : getNodes()) {
            for (Node adjNode : node.getAdjacentNodes()) {
                g2d.draw(new Line2D.Float(node.x, node.y, adjNode.x, adjNode.y));
            }
        }
        g2d.setStroke(new BasicStroke());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.nodes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (hashCode() != obj.hashCode()) {
            return false;
        }

        final Graph other = (Graph) obj;
        return nodes.equals(other.getNodes());
    }

}
