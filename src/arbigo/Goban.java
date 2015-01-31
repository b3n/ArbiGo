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
package arbigo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ben Lloyd
 */
public class Goban {
    
    private final List<Node> nodes = new ArrayList<>();
    private final int nodeDiameter = 10;
    
    /**
     *
     * @param point
     * @return
     */
    public Node nodeAt(Point point) {
        for (Node node : nodes) {
            if (node.distance(point) < nodeDiameter) return node;
        }
        return null;
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
    
    public Boolean removeNode(Node node) {
        return nodes.remove(node);
    }
    
    /**
     *
     * @return
     */
    public List<Node> getNodes() {
        return nodes;
    }
    
}
