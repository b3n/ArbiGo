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
import org.junit.*;

/**
 *
 * @author Ben Lloyd
 */
public class NodeTest {

    private Node node1, node2;

    @Before
    public void init() {
        Point point = new Point();

        node1 = new Node(point);
        node2 = new Node(point);
    }

    @Test
    public void equality() {
        Assert.assertNotEquals(node1, node2);

        int hash1 = node1.hashCode();
        node1.setLocation(9, 9);
        int hash2 = node1.hashCode();
        Assert.assertEquals(hash1, hash2);
    }

    @Test
    public void adjacentNodes() {
        Assert.assertTrue(node1.addAdjacentNode(node2));
        Assert.assertFalse(node1.addAdjacentNode(node2));
        Assert.assertTrue(node1.getAdjacentNodes().size() == 1);
    }

}
