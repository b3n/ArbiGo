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
    private Player[] players;
    private int turn;
    private Node hoverNode;
    private final ArrayList<HashMap<Node, Stone>> history;
    private HashMap<Node, Stone> state;
    private double scaleFactor;

    public Board(Graph g, int numPlayers) {
        this.graph = g;

        if (graph == null) {
            graph = new Graph(9);
        }

        // Initate players
        if (numPlayers < 2 || numPlayers > Stone.colours.length) {
            numPlayers = 2;
        }
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player();
        }

        //graph.removeColourings();
        graph.setColour(new Color(150, 150, 150));

        // Start with blank state in history (important that history.size() > 0).
        state = new HashMap<>();
        history = new ArrayList<>(100); // 100 moves will be common
        history.add(state);

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                Node node = graph.nodeAt(scalePoint(me.getPoint()));
                if (playMove(node)) {
                    history.add(state);
                    turn = (turn + 1) % players.length;
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                hoverNode = graph.nodeAt(scalePoint(me.getPoint()));
            }
        };

        addMouseListener(listener);
        addMouseMotionListener(listener);
        
        scaleFactor = 1;
        addComponentListener(new ComponentAdapter() {
            private int d = 2 * graph.getShortestRadius();
            private Dimension graphSize = graph.getSize();
            
            @Override
            public void componentResized(ComponentEvent e) {
                scaleFactor = Math.min((getSize().getWidth() - d) / graphSize.getWidth(),
                        (getSize().getHeight() - d) / graphSize.getHeight());
            }
        });

        timer = new Timer(20, this);
        timer.start();

    }
    
    private Point scalePoint(Point point) {
        int r = graph.getShortestRadius();
        double newX = (point.x -r ) / scaleFactor;
        double newY = (point.y - r) / scaleFactor;
        return new Point((int)newX, (int)newY);
    }

    private boolean playMove(Node node) {
        state = (HashMap<Node, Stone>) history.get(history.size() - 1).clone();

        if (node == null || state.containsKey(node)) {
            return false;
        }

        state.put(node, players[turn].getStone());
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
            if (state.get(playedNode).equals(state.get(node))) continue;
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

    private void paintHover(Graphics2D g2d) {   // TODO: No need to recompute unless hoverNode is different from last time.
        if (playMove(hoverNode)) {
            int r = graph.getShortestRadius();
            players[turn].getStone().paint(g2d, hoverNode.x, hoverNode.y, r, 99);
        }
    }
    
    public void paintStones(Graphics2D g2d) {
        Stone stone;
        for (Node node : history.get(history.size() - 1).keySet()) {
            stone = history.get(history.size() - 1).get(node);
            if (stone != null) {
                int r = graph.getShortestRadius();
                stone.paint(g2d, node.x, node.y, r);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Clears the graphic.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        int r = graph.getShortestRadius();
        g2d.translate(r, r);
        g2d.scale(scaleFactor, scaleFactor);
        
        graph.paintNodes(g2d);
        graph.paintEdges(g2d);
        paintStones(g2d);
        paintHover(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

}
