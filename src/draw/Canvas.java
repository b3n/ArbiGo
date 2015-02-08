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

import draw.state.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 *
 * @author Ben Lloyd
 */
public class Canvas extends JPanel implements ActionListener {
    private Graphics2D g2d;
    private Graph goban = new Graph();
    private final Set<Node> selectedNodes = new HashSet<>();
    private final Set<Point> copied = new HashSet<>();
    private boolean grid = true;
    private final Color defaultColor = Color.BLACK;
    private State state;
    private Timer timer;
    
    public Canvas() {
        state = new SelectState(this);
        
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                state.mouseClicked(me);
            }
            
            @Override
            public void mouseMoved(MouseEvent me) {
                state.mouseMoved(me);
            }
            
            @Override
            public void mousePressed(MouseEvent me) {
                state.mousePressed(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                state.mouseReleased(me);
            }
            
            @Override
            public void mouseDragged(MouseEvent me) {
                state.mouseDragged(me);
            }
        };
        
        addMouseListener(listener);
        addMouseMotionListener(listener);
        
        timer = new Timer(20, this);
        timer.start();
    }

    private void paintGrid() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        for (int x = 0; x < this.getWidth(); x += goban.getZoom()*2) {
            g2d.draw(new Line2D.Float(x, 0, x, this.getHeight()));
        }
        for (int y = 0; y < this.getHeight(); y += goban.getZoom()*2) {
            g2d.draw(new Line2D.Float(0, y, this.getWidth(), y));
        }
        g2d.setColor(defaultColor);
    }
    
    private void paintNodes() {
        for (Node node : goban.getNodes()) {
            if (selectedNodes.contains(node)) g2d.setColor(Color.BLUE);
            g2d.fill(new Ellipse2D.Float(node.x - goban.getZoom()/2, node.y - goban.getZoom()/2, goban.getZoom(), goban.getZoom()));
            g2d.setColor(defaultColor);
        }
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
    
    public Point closestOnGrid(Point point) {
        int x = point.x;
        int y = point.y;
        int xmod = x % (goban.getZoom() * 2);
        int ymod = y % (goban.getZoom() * 2);
        x -= xmod;
        y -= ymod;
        if (xmod > goban.getZoom()) x += goban.getZoom() * 2;
        if (ymod > goban.getZoom()) y += goban.getZoom() * 2;
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
        state.draw(g2d);
    }
    
    public void delete() {
        for (Node selectedNode : selectedNodes) {
            goban.removeNode(selectedNode);
        }
        repaint();
    }
    
    public void copy() {
        copied.clear();
        for (Node selectedNode : selectedNodes) {
            copied.add(selectedNode.getLocation());
        }
    }
    
    public void paste() {
        Node node;
        selectedNodes.clear();
        for (Point copiedPoint : copied) {
            copiedPoint.translate(goban.getZoom(), goban.getZoom());
            node = new Node(copiedPoint);
            selectedNodes.add(node);
            goban.addNode(node);
        }
        repaint();
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public Set<Node> getSelectedNodes() {
        return selectedNodes;
    }
    
    public Graph getGoban() {
        return this.goban;
    }
    
    public void setGoban(Graph goban) {
        this.goban = goban;
        repaint();
    }
    
    public void setGrid(boolean enable) {
        this.grid = enable;
    }
    
    public boolean getGrid() {
        return grid;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }
    
}