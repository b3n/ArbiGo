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
package com.shobute.arbigo.setup.draw.state;

import com.shobute.arbigo.setup.draw.Canvas;
import com.shobute.arbigo.common.Node;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author Ben Lloyd
 */
public class SelectState extends MouseAdapter implements State {

    private final Canvas canvas;
    private Point drag, point;
    private Node currentNode;

    public SelectState(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (drag != null) {
            g2d.setColor(new Color(0f, 0f, 1f, 0.2f));
            int x = min(point.x, drag.x);
            int y = min(point.y, drag.y);
            int w = abs(drag.x - point.x);
            int h = abs(drag.y - point.y);
            Rectangle2D selection = new Rectangle2D.Float(x, y, w, h);
            g2d.fill(selection);
            g2d.setColor(Color.BLACK);
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (canvas.getSelectedNodes().isEmpty()) {
            drag = me.getPoint();
        } else {
            int dx = me.getPoint().x - currentNode.x;
            int dy = me.getPoint().y - currentNode.y;
            for (Node node : canvas.getSelectedNodes()) {
                node.translate(dx, dy);
                if (canvas.getGrid()) {
                    node.setLocation(canvas.getGraph().closestOnGrid(node));
                }
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent me) {
        point = me.getPoint();
        currentNode = canvas.getGraph().nodeAt(point, 10);
        if (!canvas.getSelectedNodes().contains(currentNode)) {
            canvas.unSelectNodes();
            if (currentNode != null) {
                canvas.getSelectedNodes().add(currentNode);
                currentNode.setColour(Color.BLUE);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (drag != null) {
            canvas.unSelectNodes();
            for (Node node : canvas.getGraph().getNodes()) {
                int x = node.x;
                int y = node.y;
                if (x > min(point.x, drag.x) && y > min(point.y, drag.y)
                        && x < max(point.x, drag.x) && y < max(point.y, drag.y)) {
                    node.setColour(Color.BLUE);
                    canvas.getSelectedNodes().add(node);
                }
            }
            drag = null;
        } else if (!canvas.getSelectedNodes().isEmpty()) {
            canvas.checkpoint();
        }
    }

}
