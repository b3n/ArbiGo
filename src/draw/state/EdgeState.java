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
package draw.state;

import draw.Canvas;
import draw.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

/**
 *
 * @author Ben Lloyd
 */
public class EdgeState extends MouseAdapter implements State {
    
    private Canvas canvas;
    private Node previousNode, currentNode;
    private Point pressed, clicked;
    
    public EdgeState(Canvas canvas) {
        this.canvas = canvas;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        if (currentNode != null) {
            g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new Line2D.Float(currentNode.x, currentNode.y, clicked.x, clicked.y));
            g2d.setStroke(new BasicStroke());
            g2d.setColor(Color.BLACK);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if (previousNode != null && currentNode != null && previousNode != currentNode) {
            currentNode.addAdjacentNode(previousNode);
            previousNode.addAdjacentNode(currentNode);
            previousNode = null;
        } else {
            currentNode = canvas.getGoban().nodeAt(pressed);
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        clicked = canvas.getGrid() ? canvas.getGoban().closestOnGrid(me.getPoint()) : me.getPoint();
    }
    
    @Override
    public void mousePressed(MouseEvent me) {
        pressed = me.getPoint();
        previousNode = currentNode;
        currentNode = canvas.getGoban().nodeAt(pressed);
    }
    
}