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
import com.shobute.arbigo.setup.draw.state.SelectState;
import com.shobute.arbigo.setup.draw.state.State;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.apache.commons.lang.SerializationUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

/**
 *
 * @author Ben Lloyd
 */
public class Canvas extends JPanel implements ActionListener {

    private Graphics2D g2d;
    private Graph graph;
    private final Set<Node> selectedNodes;
    private final Set<Point> copied;
    private boolean grid;
    private final Color defaultColor;
    private State state;
    private final Timer timer;
    private final ArrayList<Graph> history;
    private int historyIndex;
    private ArrayList<Line2D.Float> gridLines;

    /**
     * The canvas to draw a graph on.
     */
    public Canvas() {
        history = new ArrayList<>();
        defaultColor = Color.BLACK;
        grid = true;
        copied = new HashSet<>();
        selectedNodes = new HashSet<>();
        graph = new Graph();
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                constructGrid();
            }
        });
    }

    private void constructGrid() {
        int distance = graph.getNodeSize() * 2;
        gridLines = new ArrayList<>();
        for (int x = 0; x < this.getWidth(); x += distance) {
            gridLines.add(new Line2D.Float(x, 0, x, this.getHeight()));
        }
        for (int y = 0; y < this.getHeight(); y += distance) {
            gridLines.add(new Line2D.Float(0, y, this.getWidth(), y));
        }
    }

    private void paintGrid() {
        if (gridLines == null) {
            constructGrid();
        }
        g2d.setColor(new Color(0f, 0f, 0f, 0.2f));
        for (Line2D.Float line : gridLines) {
            g2d.draw(line);
        }
        g2d.setColor(defaultColor);
    }

    /**
     * Invoked by Swing to draw components. Applications should not invoke paint
     * directly, but should instead use the repaint method to schedule the
     * component for redrawing.
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        g2d = (Graphics2D) g;

        if (grid) {
            paintGrid();
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graph.paintNodes(g2d);
        graph.paintEdges(g2d);
        state.draw(g2d);
    }

    /**
     * Delete the selected nodes.
     */
    public void delete() {
        for (Node selectedNode : selectedNodes) {
            graph.removeNode(selectedNode);
        }
        checkpoint();
    }

    /**
     * Copy the selected nodes.
     */
    public void copy() {
        copied.clear();
        for (Node selectedNode : selectedNodes) {
            copied.add(selectedNode.getLocation());
        }
    }

    /**
     * Paste the copied nodes.
     */
    public void paste() {
        Node node;
        selectedNodes.clear();
        for (Point copiedPoint : copied) {
            copiedPoint.translate(graph.getNodeSize(), graph.getNodeSize());
            node = new Node(copiedPoint);
            selectedNodes.add(node);
            graph.addNode(node);
        }
        checkpoint();
    }

    /**
     * Create a checkpoint in history that can be undoed to.
     */
    public void checkpoint() {
        history.add(++historyIndex, (Graph) SerializationUtils.clone(graph));
    }

    /**
     * Revert back to the previous checkpoint in history.
     */
    public void undo() {
        if (historyIndex > 0) {
            graph = history.get(--historyIndex);
        }
        selectedNodes.clear();
    }

    /**
     * Revert back to the next checkpoint in history.
     */
    public void redo() {
        if (historyIndex < history.size() - 1) {
            graph = history.get(++historyIndex);
        }
        selectedNodes.clear();
    }

    /**
     * Set the current state. Used by all mouse listeners.
     *
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the nodes which have been selected.
     *
     * @return A set of nodes.
     */
    public Set<Node> getSelectedNodes() {
        return selectedNodes;
    }

    /**
     * Get the graph which has been drawn.
     *
     * @return A graph.
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Set the graph.
     *
     * @param graph The graph to set.
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        checkpoint();
    }

    /**
     * Set the grid and "snap to grid" functionality.
     *
     * @param enable True to enable, false to disable.
     */
    public void setGrid(boolean enable) {
        this.grid = enable;
    }

    /**
     * Get the grid and "snap to grid" functionality.
     *
     * @return True if enabled, false if disabled.
     */
    public boolean getGrid() {
        return grid;
    }

    /**
     * Unselect any selected nodes.
     */
    public void unSelectNodes() {
        for (Node node : getSelectedNodes()) {
            node.setColour(Color.BLACK);
        }
        getSelectedNodes().clear();
    }

    /**
     * Action method for Timer to repaint the image, do not call this directly.
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

}
