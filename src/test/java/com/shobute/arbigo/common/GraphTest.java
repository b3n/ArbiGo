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

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashSet;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Ben Lloyd
 */
public class GraphTest {
    
    private Graph graph;
    private int diameter;
    private static Stone black;
    private static Stone white;
    
    @BeforeClass
    public static void setUpClass() {
        black = new Stone(0);
        white = new Stone(1);
    }
    
    @Before
    public void setUp() {        
        // Constructs a 3x3 grid with a game state that looks like:
        //
        //     # O .
        //     . O #
        //     . # #
        //
        // Where a '.' is an empty node, a '#' is a black stone, and a 'O' is a
        // white stone. Giving nodes x/y values of 0, 100, 200. 
        graph = new Graph(3, 100, 0);
        graph.nodeAt(0, 0).setStone(black);
        graph.nodeAt(100, 0).setStone(white);
        graph.nodeAt(100, 100).setStone(white);
        graph.nodeAt(100, 200).setStone(black);
        graph.nodeAt(200, 100).setStone(black);
        graph.nodeAt(200, 200).setStone(black);
        
        diameter = graph.getDiameter();
    }
    
    @Test
    public void testNodeAt() {
        Node node;
        
        node = graph.nodeAt(new Point(99, 99));
        assertNotNull(node);
        assertEquals(100, node.x);
        assertEquals(100, node.y);
        
        node = graph.nodeAt(new Point(50, 50));
        assertNull(node);
        
        node = graph.nodeAt(new Point(diameter, 0));
        assertNull(node);
        
        node = graph.nodeAt(new Point(diameter - 1, 0));
        assertNotNull(node);
    }
    
    @Test
    public void testremoveColourings() {
        graph.removeColourings();
        for (Node node : graph.getNodes()) {
            assertNull(node.getStone());
        }
    }
    
    @Test
    public void testAddNode() {
        int numNodes = graph.getNodes().size();
        assertTrue(graph.addNode(new Point(999, 999)));
        assertFalse(graph.addNode(new Point(999, 999)));
        assertEquals(numNodes + 1, graph.getNodes().size());
    }
    
    @Test
    public void testGroup() {
        HashSet<Node> group1, group2;
        
        // Test sizes.
        group1 = graph.group(graph.nodeAt(0, 0));
        assertEquals(1, group1.size());
        group1 = graph.group(graph.nodeAt(100, 100));
        assertEquals(2, group1.size());
        group1 = graph.group(graph.nodeAt(200, 200));
        assertEquals(3, group1.size());
        
        // Test null node.
        Throwable caught = null;
        try {
            graph.group(null);
        } catch (IllegalArgumentException e) {
            caught = e;
        }
        assertNotNull(caught);
        
        // Test getting the same group from different nodes.
        group1 = graph.group(graph.nodeAt(200, 200));
        group2 = graph.group(graph.nodeAt(100, 200));
        assertEquals(group1, group2);
    }
    
    @Test
    public void testLiberties() {
        HashSet<Node> group = graph.group(graph.nodeAt(0, 0));
        assertTrue(graph.liberties(group));
        
        graph.nodeAt(0, 100).setStone(white);
        assertFalse(graph.liberties(group));
    }
    
    @Test
    public void testClosestOnGrid() {
        int d = graph.getDiameter();
        int g = d * 2;    // Grid width is twice the diameter.
        Point point = new Point(g, g);
        
        assertEquals(point, graph.closestOnGrid(new Point(g+1, g+1)));
        assertEquals(point, graph.closestOnGrid(new Point(g-1, g-1)));
        assertEquals(point, graph.closestOnGrid(new Point(g+d, g+d)));
        assertEquals(point, graph.closestOnGrid(new Point(g-d+1, g-d+1)));
        
        assertNotEquals(point, graph.closestOnGrid(new Point(g+d+1, g+d+1)));
        assertNotEquals(point, graph.closestOnGrid(new Point(g-d, g-d)));
    }
    
    @Test
    public void testPaintNodes() {
        Graphics2D g2d = mock(Graphics2D.class);
        graph.paintNodes(g2d);
        //verify(g2d).fill();
    }
    
}