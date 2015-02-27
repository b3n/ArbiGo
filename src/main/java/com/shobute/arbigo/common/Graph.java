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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.HashSet;
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
     *
     * @param point
     * @return
     */
    public Node nodeAt(Point point) {
        for (Node node : nodes) {
            if (node.distance(point) < diameter) return node;
        }
        return null;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }
    
    public void removeColourings() {
        for (Node node : getNodes()) {
            node.setStone(null);
        }
    }
    
    /**
     *
     * @param point
     * @return
     */
    public Boolean addNode(Point point) {
        if (this.nodeAt(point) == null) {
            nodes.add(new Node(point));
            return true;
        }
        return false;
    }
    
    public Boolean addNode(Node node) {
        return nodes.add(node);
    }
    
    public Boolean removeNode(Node nodeToRemove) {
        for (Node node : nodes) {   // TODO: Is there a better data structure to make this faster?
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
    
    public HashSet<Node> group(Node node) {
        if (node == null) throw new IllegalArgumentException();
        return group(node, new HashSet<Node>());
    }
    
    private HashSet<Node> group(Node node, HashSet<Node> group) {
        if (group.contains(node)) return null;
        group.add(node);
        for (Node n : node.getAdjacentNodes()) {
            if (n.getStone() == node.getStone()) group(n, group);
        }
        return group;
    }
    
    // TODO: This can probably be optomized.
    public boolean liberties(HashSet<Node> group) {
        for (Node node : group) {
            for (Node adjNode : node.getAdjacentNodes()) {
                if (adjNode.getStone() == null) return true;
            }
        }
        return false;
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
        if (xmod > getDiameter()) x += getDiameter() * 2;
        if (ymod > getDiameter()) y += getDiameter() * 2;
        return new Point(x, y);
    }
    
    public void paintNodes(Graphics2D g2d) {
        g2d.setColor(colour);
        int z = getDiameter();
        for (Node node : getNodes()) {
            //if (selectedNodes.contains(node)) g2d.setColor(Color.BLUE);
            g2d.fill(new Ellipse2D.Float(node.x - z/2, node.y - z/2, z, z));
            g2d.setColor(colour);
        }
    }
    
    public void paintEdges(Graphics2D g2d) {
        g2d.setColor(colour);
        g2d.setStroke(new BasicStroke(2));
        for (Node node : getNodes()) {
            for (Node adjacentNode : node.getAdjacentNodes()) {
                g2d.draw(new Line2D.Float(node.x, node.y, adjacentNode.x, adjacentNode.y)); 
            }
        }
        g2d.setStroke(new BasicStroke());
    }
    
    
    public void paintStones(Graphics2D g2d) {
        Stone stone;
        for (Node node : getNodes()) {
            stone = node.getStone();
            if (stone != null) {
                stone.paint(g2d, node.x, node.y);
            }
        }
    }
    
}