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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Ben Lloyd
 */
public class Canvas extends javax.swing.JPanel {
    private Graphics2D g2d;
    private Point point;
    private Point drag;
    private final int diameter = 10;    // TODO: Use for zooming?
    private Goban goban = new Goban();
    private List<Node> selectedNodes = new ArrayList<>();
    private Node currentNode;
    private Node previousNode;
    private boolean grid = true;
    private final Color defaultColor = Color.BLACK;
    private Tool tool = Tool.SELECT;
    private Point pointer = new Point();
    
    public Canvas() {
        setTool(tool);
    }

    private void paintGrid() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        for (int x = 0; x < this.getWidth(); x += diameter*2) {
            g2d.draw(new Line2D.Float(x, 0, x, this.getHeight()));
        }
        for (int y = 0; y < this.getHeight(); y += diameter*2) {
            g2d.draw(new Line2D.Float(0, y, this.getWidth(), y));
        }
        g2d.setColor(defaultColor);
    }
    
    private void paintNodes() {
        for (Node node : goban.getNodes()) {
            if (selectedNodes.contains(node)) g2d.setColor(Color.BLUE);
            g2d.fill(new Ellipse2D.Float(node.x - diameter/2, node.y - diameter/2, diameter, diameter));
            g2d.setColor(defaultColor);
        }
    }
    
    private void paintSelection() {
        g2d.setColor(new Color(0f, 0f, 1f, 0.2f));
        int x = min(point.x, drag.x);
        int y = min(point.y, drag.y);
        int w = abs(drag.x - point.x);
        int h = abs(drag.y - point.y);
        Rectangle2D selection = new Rectangle2D.Float(x, y, w, h);
        g2d.fill(selection);
        g2d.setColor(defaultColor);
    }
    
    private void paintNodeInProgress() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        g2d.fill(new Ellipse2D.Float(pointer.x - diameter/2, pointer.y - diameter/2, diameter, diameter));
        g2d.setColor(defaultColor);
    }
    
    private void paintEdgeInProgress() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Line2D.Float(currentNode.x, currentNode.y, pointer.x, pointer.y));
        g2d.setStroke(new BasicStroke());
        g2d.setColor(defaultColor);
    }
    
    private void paintEdges() {
        g2d.setStroke(new BasicStroke(2));
        for (Node node : goban.getNodes()) {
            for (Node adjacentNode : node.getAdjacentNodes()) {
                g2d.draw(new Line2D.Float(node.x, node.y, adjacentNode.x, adjacentNode.y)); 
            }
        }
        g2d.setStroke(new BasicStroke());
    }
    
    private Point closestOnGrid(Point point) {
        int x = point.x;
        int y = point.y;
        int xmod = x % (diameter * 2);
        int ymod = y % (diameter * 2);
        x -= xmod;
        y -= ymod;
        if (xmod > diameter) x += diameter * 2;
        if (ymod > diameter) y += diameter * 2;
        return new Point(x, y);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (grid) paintGrid();
        paintNodes();
        paintEdges();
        if (drag != null) paintSelection();
        if (tool == Tool.NODE) paintNodeInProgress();
        if (tool == Tool.EDGE && currentNode != null) paintEdgeInProgress();
    }
    
    public void delete() {
        for (Node selectedNode : selectedNodes) {
            goban.removeNode(selectedNode);
        }
        repaint();
    }
    
    public Goban getGoban() {
        return this.goban;
    }
    
    public void setGoban(Goban goban) {
        this.goban = goban;
        repaint();
    }
    
    public void grid(boolean enable) {
        this.grid = enable;
        repaint();
    }
    
    public final void setTool(Tool tool) {  // TODO: Make it so you pass in listnener object?
        this.tool = tool;
        
        // Remove old listeners
        for (MouseListener l : getMouseListeners()) removeMouseListener(l);
        for (MouseMotionListener l : getMouseMotionListeners()) removeMouseMotionListener(l);
        for (MouseWheelListener l : getMouseWheelListeners()) removeMouseWheelListener(l);
        
        // Add new listeners
        Listener listener = new Listener();
        if (tool == Tool.SELECT) {
            listener = new SelectListener();
        } else if (tool == Tool.EDGE) {
            listener = new EdgeListener();
        } else if (tool == Tool.NODE) {
            listener = new NodeListener();
        }
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }
    
    
    private class NodeListener extends Listener {
        
        @Override
        public void mouseClicked(MouseEvent me) {
            point = grid ? closestOnGrid(me.getPoint()) : me.getPoint();
            goban.addNode(point);
            repaint();
        }
        
        @Override
        public void mouseMoved(MouseEvent me) {
            pointer = grid ? closestOnGrid(me.getPoint()) : me.getPoint();
            repaint();
        }
        
    }
    
    
    private class EdgeListener extends Listener {
        
        @Override
        public void mouseClicked(MouseEvent me) {
            if (previousNode != null && currentNode != null && previousNode != currentNode) {
                //clickedNode.addAdjacentNode(currentNode);
                previousNode.addAdjacentNode(currentNode);
                previousNode = null;
                repaint();  // TODO: Is this needed? Tool.EDGE repaints on mouse move anyway.
            } else {
                currentNode = goban.nodeAt(point);
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent me) {
            pointer = grid ? closestOnGrid(me.getPoint()) : me.getPoint();
            repaint();
        }
        
    }
    
    
    private class SelectListener extends Listener {
        
        @Override
        public void mouseDragged(MouseEvent me) {
            if (selectedNodes.isEmpty()) {
                drag = me.getPoint();
            } else {
                int dx = me.getPoint().x - currentNode.x;
                int dy = me.getPoint().y - currentNode.y;
                for (Node node : selectedNodes) {
                    node.translate(dx, dy);
                    if (grid) node.setLocation(closestOnGrid(node));
                }
            }
            repaint();
        }
        
        @Override
        public void mousePressed(MouseEvent me) {
            point = me.getPoint();
            previousNode = currentNode;
            currentNode = goban.nodeAt(point);
            if (!selectedNodes.contains(goban.nodeAt(point))) {
                selectedNodes.clear();
                Node node = goban.nodeAt(point);
                if (node != null) selectedNodes.add(node);
            }
        }
        
    }

    
    private class Listener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent me) {
            point = me.getPoint();
            previousNode = currentNode;
            currentNode = goban.nodeAt(point);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (drag != null) {
                selectedNodes.clear();
                for (Node node : goban.getNodes()) {
                    int x = node.x;
                    int y = node.y;
                    if (x > min(point.x, drag.x) && y > min(point.y, drag.y) && x < max(point.x, drag.x) && y < max(point.y, drag.y)) {
                        selectedNodes.add(node);
                    }
                }
                drag = null;
            }
            repaint();
        }

    }
}