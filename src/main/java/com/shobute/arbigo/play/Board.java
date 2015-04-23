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
package com.shobute.arbigo.play;

import com.shobute.arbigo.common.Stone;
import com.shobute.arbigo.common.Graph;
import com.shobute.arbigo.common.Node;
import java.awt.Color;
import java.awt.Dimension;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Ben Lloyd
 */
public class Board extends JPanel implements ActionListener {

    private Graph graph;
    private final Timer timer;
    private Node hoverNode, validHover;
    private final ArrayList<HashMap<Node, Stone>> history;
    private HashMap<Node, Stone> state;
    private double scaleFactor;
    private FramePlay framePlay;
    private MouseAdapter listener;
    private boolean gameOver;

    public Board(final FramePlay framePlay) {
        this.framePlay = framePlay;
        this.graph = framePlay.getGraph();

        if (graph == null) {
            graph = new Graph(9);
        }

        // Start with blank state in history (important that history.size() > 0).
        state = new HashMap<>();
        history = new ArrayList<>(100); // 100 moves will be common
        history.add(state);

        this.listener = new MouseAdapter() {
            int radius = graph.getShortestRadius();

            @Override
            public void mouseClicked(MouseEvent me) {
                Node node = graph.nodeAt(scalePoint(me.getPoint()), radius);
                if (playMove(node)) {
                    history.add(state);
                    framePlay.nextTurn();
                    validHover = null;
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                hoverNode = graph.nodeAt(scalePoint(me.getPoint()), radius);
            }
        };

        addMouseListener(listener);
        addMouseMotionListener(listener);

        scaleFactor = 1;
        addComponentListener(new ComponentAdapter() {
            private int r = graph.getShortestRadius();
            private Dimension graphSize = graph.getSize();

            @Override
            public void componentResized(ComponentEvent e) {
                double cWidth = getSize().getWidth();
                double cHeight = getSize().getHeight();
                double gWidth = graphSize.getWidth();
                double gHeight = graphSize.getHeight();
                scaleFactor = Math.min(cWidth / (gWidth + 2 * r),
                        cHeight / (gHeight + 2 * r));
            }
        });

        timer = new Timer(20, this);
        timer.start();

    }

    private Point scalePoint(Point point) {
        int r = graph.getShortestRadius();
        Point origin = graph.getOrigin();
        double newX = point.x / scaleFactor + origin.x - r;
        double newY = point.y / scaleFactor + origin.y - r;
        return new Point((int) newX, (int) newY);
    }

    private boolean playMove(Node node) {
        state = (HashMap<Node, Stone>) history.get(history.size() - 1).clone();

        if (node == null || state.containsKey(node)) {
            return false;
        }

        state.put(node, framePlay.getStone());
        removeCaptured(node);

        for (HashMap<Node, Stone> prevState : history) {
            if (state.equals(prevState)) {
                return false;
            }
        }

        return true;
    }

    private HashSet<Node> group(Node node, HashSet<Node> group) {
        if (group.contains(node)) {
            return null;
        }
        group.add(node);
        Stone stone = state.get(node);
        for (Node n : node.getAdjacentNodes()) {
            if (stone != null && stone.equals(state.get(n))) {
                group(n, group);
            }
        }
        return group;
    }

    private boolean surrounded(HashSet<Node> group) {
        for (Node node : group) {
            for (Node adjNode : node.getAdjacentNodes()) {
                if (!state.containsKey(adjNode)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeCaptured(Node playedNode) {
        for (Node node : playedNode.getAdjacentNodes()) {
            if (state.get(playedNode).equals(state.get(node))) {
                continue;
            }
            HashSet<Node> group = group(node, new HashSet<Node>());
            if (surrounded(group)) {
                for (Node n : group) {
                    state.remove(n);
                }
            }
        }

        // Suicide
        HashSet<Node> group = group(playedNode, new HashSet<Node>());
        if (surrounded(group)) {
            for (Node n : group) {
                state.remove(n);
            }
        }
    }

    private void paintHover(Graphics2D g2d) {
        if (hoverNode != null && hoverNode == validHover || playMove(hoverNode)) {
            // Set this so playMove() does not recompute unnecessarily.
            validHover = hoverNode;
            
            int r = graph.getShortestRadius();
            framePlay.getStone().paint(g2d, hoverNode.x, hoverNode.y, r, 50);
        }
    }

    public void paintStones(Graphics2D g2d) {
        Stone stone;
        for (Node node : history.get(history.size() - 1).keySet()) {
            stone = history.get(history.size() - 1).get(node);
            if (stone != null) {
                int r = graph.getShortestRadius();
                stone.paint(g2d, node.x, node.y, r, 220);
            }
        }
    }
    
    public void gameOver() {
        timer.stop();
        removeMouseListener(listener);
        gameOver = true;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Clears the graphic.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        graph.scale(g2d, scaleFactor);
        graph.paintNodes(g2d);
        graph.paintEdges(g2d);
        paintStones(g2d);

        if (gameOver) {
            Color color = framePlay.getStone().getColour();
            g2d.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 200));
            // TODO: Do it like this, http://stackoverflow.com/a/2244285/3780738?
            Point origin = graph.getOrigin();
            int r = graph.getShortestRadius();
            g2d.translate(origin.x, origin.y);
            g2d.translate(-1 * r, -1 * r);
            g2d.scale(1 / scaleFactor, 1 / scaleFactor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            paintHover(g2d);
        }
    }
    
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

}
