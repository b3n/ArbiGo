package com.shobute.arbigo.common;

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


import java.awt.Point;


/**
 *
 * @author bl12aay
 */
public class Helper {
    
    public static Graph createGrid(int size) {
        Graph graph = new Graph();
        
        Node[][] nodes = new Node[size][size];
        
        for (int x = 0; x < size ; x++) {
            for (int y = 0; y < size; y++) {
                nodes[x][y] = new Node(new Point(x*100, y*100));
            }
        }
        
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                if (y + 1 < nodes[x].length) {
                    nodes[x][y].addAdjacentNode(nodes[x][y+1]);
                }
                if (y > 0) {
                    nodes[x][y].addAdjacentNode(nodes[x][y-1]);
                }
                if (x +  1 < nodes.length) {
                    nodes[x][y].addAdjacentNode(nodes[x+1][y]);
                }
                if (x > 0) {
                    nodes[x][y].addAdjacentNode(nodes[x-1][y]);
                }
                graph.addNode(nodes[x][y]);
            }
        }
        
        return graph;
    }
    
}