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
package com.shobute.arbigo.setup.draw;

import com.shobute.arbigo.common.Node;
import com.shobute.arbigo.common.Graph;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.apache.commons.lang.SerializationUtils;
import com.shobute.arbigo.setup.draw.state.*;


/**
 *
 * @author Ben Lloyd
 */
public class Canvas extends JPanel implements ActionListener {
    private Graphics2D g2d;
    private Graph board = new Graph();
    private final Set<Node> selectedNodes = new HashSet<>();
    private final Set<Point> copied = new HashSet<>();
    private boolean grid = true;
    private final Color defaultColor = Color.BLACK;
    private State state;
    private Timer timer;
    private ArrayList<Graph> history = new ArrayList<>();
    private int historyIndex = 0;
    
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
        
        history.add(new Graph());
    }

    private void paintGrid() {
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        for (int x = 0; x < this.getWidth(); x += board.getZoom()*2) {
            g2d.draw(new Line2D.Float(x, 0, x, this.getHeight()));
        }
        for (int y = 0; y < this.getHeight(); y += board.getZoom()*2) {
            g2d.draw(new Line2D.Float(0, y, this.getWidth(), y));
        }
        g2d.setColor(defaultColor);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (grid) paintGrid();
        board.paintNodes(g2d);
        board.paintEdges(g2d);
        state.draw(g2d);
    }
    
    public void delete() {
        for (Node selectedNode : selectedNodes) {
            board.removeNode(selectedNode);
        }
        checkpoint();
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
            copiedPoint.translate(board.getZoom(), board.getZoom());
            node = new Node(copiedPoint);
            selectedNodes.add(node);
            board.addNode(node);
        }
        checkpoint();
    }
    
    public void checkpoint() {
        history.add(++historyIndex, (Graph) SerializationUtils.clone(board));
    }
    
    public void undo() {
        if (historyIndex > 0) board = history.get(--historyIndex);
        selectedNodes.clear();
    }
    
    public void redo() {
        if (historyIndex < history.size() - 1) board = history.get(++historyIndex);
        selectedNodes.clear();
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public Set<Node> getSelectedNodes() {
        return selectedNodes;
    }
    
    public Graph getBoard() {
        return this.board;
    }
    
    public void setBoard(Graph board) {
        this.board = board;
        checkpoint();
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