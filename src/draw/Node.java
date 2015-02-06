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
package draw;

import java.awt.Point;
import java.io.Serializable;
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
    
    /**
     *
     * @param point
     */
    public Node(Point point) {
        super(point);
        hashCode = hash++;
    }

    /**
     *
     * @return
     */
    public Set<Node> getAdjacentNodes() {
        return adjacentNodes;
    }
    
    /**
     *
     * @param node
     * @return
     */
    public boolean removeAdjacentNode(Node node) {
        return adjacentNodes.remove(node);
    }
        
    /**
     *
     * @param node
     * @return
     */
    public boolean addAdjacentNode(Node node) {
        return adjacentNodes.add(node);
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        return this.hashCode() == obj.hashCode();
    }

}