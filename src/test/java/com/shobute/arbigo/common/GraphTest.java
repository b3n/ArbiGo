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

import java.awt.Point;
import java.util.HashSet;
import org.junit.*;

/**
 *
 * @author Ben Lloyd
 */
public class GraphTest {
    
    private Graph graph;
    private int diameter;
    private Node[][] nodes;
    private static Stone black;
    private static Stone white;
    
    @BeforeClass
    public static void setUpClass() {
        black = new Stone(0);
        white = new Stone(1);
    }
    
    @Before
    public void setUp() {
        graph = new Graph();
        
        diameter = graph.getDiameter();
        
        // Constructs a 3x3 grid with a game state that looks like:
        //
        //     # O .
        //     . O #
        //     . # #
        //
        // Where a '.' is an empty node, a '#' is a black stone, and a 'O' is a
        // white stone. Giving nodes x/y values of 0, 100, 200. 
        
        nodes = new Node[3][3];
        
        for (int x = 0; x < 3 ; x++) {
            for (int y = 0; y < 3; y++) {
                nodes[x][y] = new Node(new Point(x*100, y*100));
            }
        }
        
        nodes[0][0].setStone(black);
        nodes[1][0].setStone(white);
        nodes[1][1].setStone(white);
        nodes[1][2].setStone(black);
        nodes[2][1].setStone(black);
        nodes[2][2].setStone(black);
        
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                if (y+1 < nodes[x].length) {
                    nodes[x][y].addAdjacentNode(nodes[x][y+1]);
                }
                if (y > 0) {
                    nodes[x][y].addAdjacentNode(nodes[x][y-1]);
                }
                if (x+1 < nodes.length) {
                    nodes[x][y].addAdjacentNode(nodes[x+1][y]);
                }
                if (x > 0) {
                    nodes[x][y].addAdjacentNode(nodes[x-1][y]);
                }
                graph.addNode(nodes[x][y]);
            }
        }
        
    }
    
    @Test
    public void testNodeAt() {
        Node node;
        
        node = graph.nodeAt(new Point(99, 99));
        Assert.assertNotNull(node);
        Assert.assertEquals(100, node.x);
        Assert.assertEquals(100, node.y);
        
        node = graph.nodeAt(new Point(50, 50));
        Assert.assertNull(node);
        
        node = graph.nodeAt(new Point(diameter, 0));
        Assert.assertNull(node);
        
        node = graph.nodeAt(new Point(diameter - 1, 0));
        Assert.assertNotNull(node);
    }
    
    @Test
    public void testGroup() {
        HashSet<Node> group1, group2;
        
        // Test sizes.
        group1 = graph.group(nodes[0][0]);
        Assert.assertEquals(1, group1.size());
        group1 = graph.group(nodes[1][1]);
        Assert.assertEquals(2, group1.size());
        group1 = graph.group(nodes[2][2]);
        Assert.assertEquals(3, group1.size());
        
        // Test null node.
        Throwable caught = null;
        try {
            graph.group(null);
        } catch (IllegalArgumentException e) {
            caught = e;
        }
        Assert.assertNotNull(caught);
        
        // Test getting the same group from different nodes.
        group1 = graph.group(nodes[2][2]);
        group2 = graph.group(nodes[1][2]);
        Assert.assertEquals(group1, group2);
    }
    
    @Test
    public void testLiberties() {
        HashSet<Node> group = graph.group(nodes[0][0]);
        Assert.assertTrue(graph.liberties(group));
        
        nodes[0][1].setStone(white);
        Assert.assertFalse(graph.liberties(group));
    }
    
    @Test
    public void testClosestOnGrid() {
        int d = graph.getDiameter();
        Point point = new Point(d, d);
        Assert.assertEquals(point, graph.closestOnGrid(new Point(d+1, d+1)));
        Assert.assertNotEquals(point, graph.closestOnGrid(new Point(d*2, d*2)));
    }
    
}
