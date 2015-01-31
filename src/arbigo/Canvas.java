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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.*;


/**
 *
 * @author Ben Lloyd
 */
public class Canvas extends javax.swing.JPanel implements MouseListener, MouseMotionListener {
    private Graphics2D g2d;
    private Point point;
    private Point drag;
    private final int diameter = 10;
    private Goban goban = new Goban();
    private List<Node> selectedNodes = new ArrayList<>();
    private Node pressedNode;
    private boolean grid = true;
    private final Color defaultColor = Color.BLACK;
    private Tool tool = Tool.POINTER;
    private Point pointer;
    
    public Canvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void paintGrid() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.1f));
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
        g2d.setColor(new Color(0f, 0f, 1f, 0.1f));
        int x = min(point.x, drag.x);
        int y = min(point.y, drag.y);
        int w = abs(drag.x - point.x);
        int h = abs(drag.y - point.y);
        Rectangle2D selection = new Rectangle2D.Float(x, y, w, h);
        g2d.fill(selection);
        g2d.setColor(defaultColor);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (grid) paintGrid();
        paintNodes();
        if (drag != null) paintSelection();
        if (tool == Tool.NODE && pointer != null) {
            g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
            g2d.fill(new Ellipse2D.Float(pointer.x - diameter/2, pointer.y - diameter/2, diameter, diameter));
            g2d.setColor(defaultColor);
        }
    }
    
    public void delete() {
        for (Node node : selectedNodes) {
            goban.removeNode(node);
        }
        repaint();
    }
    
    public void grid(boolean enable) {
        this.grid = enable;
        repaint();
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        System.out.println("clicked");
        point = me.getPoint();
        if (tool == Tool.NODE) goban.addNode(point);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        System.out.println("pressed");
        point = me.getPoint();
        pressedNode = goban.nodeAt(point);
        if (tool == Tool.POINTER && !selectedNodes.contains(goban.nodeAt(point))) {
            selectedNodes.clear();
            Node node = goban.nodeAt(point);
            if (node != null) selectedNodes.add(node);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        System.out.println("released");
        if (drag != null) {
            selectedNodes.clear();
            for (Node node : goban.getNodes()) {
                int x = node.x;
                int y = node.y;
                if (x > min(point.x, drag.x) && y > min(point.y, drag.y) && x < max(point.x, drag.x) && y < max(point.y, drag.y)) {
                    selectedNodes.add(node);
                }
            }
            System.out.println(point.x + " " + point.y + " " + drag.x + " " + drag.y);
            drag = null;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (tool == Tool.POINTER) {
            if (selectedNodes.isEmpty()) {
                drag = me.getPoint();
            } else {
                int dx = me.getPoint().x - pressedNode.x;
                int dy = me.getPoint().y - pressedNode.y;
                for (Node node : selectedNodes) {
                    node.translate(dx, dy);
                }
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (tool == Tool.NODE) {
            pointer = me.getPoint();
            repaint();
        }
    }
    
}