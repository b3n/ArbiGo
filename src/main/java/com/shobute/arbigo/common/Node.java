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

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Ben Lloyd
 */
public class Node extends Point implements Serializable {

    private final Set<Node> adjacentNodes = new HashSet<>();
    private static int hash = 0;
    private final int hashCode;
    private Color colour;

    /**
     * Constructs a new Node positioned at point.
     *
     * @param point The Point where this Node should be positioned.
     */
    public Node(Point point) {
        super(point);
        hashCode = hash++;
        colour = Color.BLACK;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     * Get all Nodes adjacent to this.
     *
     * @return A set of adjacent nodes.
     */
    public Set<Node> getAdjacentNodes() {
        return adjacentNodes;
    }

    /**
     * Remove an adjacent node.
     *
     * @param node The adjacent node to remove.
     * @return True on success, or false on failure.
     */
    public boolean removeAdjacentNode(Node node) {
        return adjacentNodes.remove(node);
    }

    /**
     * Add an adjacent node.
     *
     * @param node The adjacent node to add.
     * @return True on success, or false on failure.
     */
    public boolean addAdjacentNode(Node node) {
        return adjacentNodes.add(node);
    }

    /**
     * Compare Nodes by their x-axis.
     *
     * @return A Comparator which compares Nodes by their x-axis.
     */
    public static Comparator<Node> xComparator() {
        return new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return Integer.compare(n1.x, n2.x);
            }
        };
    }

    /**
     * Compare Nodes by their y-axis.
     *
     * @return A Comparator which compares Nodes by their y-axis.
     */
    public static Comparator<Node> yComparator() {
        return new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return Integer.compare(n1.y, n2.y);
            }
        };
    }

    /**
     * Get this Node's hash code. All Nodes have a unique hash code.
     * @return This Node's hash code.
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        return super.equals(obj);
    }

}
