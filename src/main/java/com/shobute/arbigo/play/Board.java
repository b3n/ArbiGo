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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author Ben Lloyd
 */
public class Board extends JPanel implements ActionListener {

    private Graph graph;
    private Graphics2D g2d;
    private final Timer timer;
    private Player[] players;
    private int turn = 0;
    private Node hoverNode;
    private final ArrayList<Graph> history;

    public Board(Graph g, int numPlayers) {
        this.graph = g;

        if (graph == null) {
            graph = new Graph(9, 30, 30);
        }

        // Initate players
        if (numPlayers < 2 || numPlayers > Stone.colours.length) {
            numPlayers = 2;
        }
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player();
        }

        graph.removeColourings();
        graph.setColour(new Color(150, 150, 150));

        // Start with initial graph in history (important that history.size() > 0).
        history = new ArrayList<>(100); // 100 moves will be common
        history.add((Graph) SerializationUtils.clone(graph));

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                Node node = graph.nodeAt(me.getPoint());
                if (playMove(node)) {
                    history.add(graph);
                    turn = (turn + 1) % players.length;
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                hoverNode = graph.nodeAt(me.getPoint());
            }
        };

        addMouseListener(listener);
        addMouseMotionListener(listener);

        timer = new Timer(20, this);
        timer.start();

    }

    private boolean playMove(Node node) {
        graph = (Graph) SerializationUtils.clone(history.get(history.size() - 1));
        node = graph.nodeAt(node);

        if (node == null || node.getStone() != null) {
            return false;
        }

        node.setStone(players[turn].getStone());
        removeCaptured(node);

        for (Graph prevState : history) {
            if (graph.equals(prevState)) {
                return false;
            }
        }

        return true;
    }

    private void removeCaptured(Node playedNode) {
        for (Node node : playedNode.getAdjacentNodes()) {
            if (playedNode.getStone().equals(node.getStone())) continue;
            HashSet<Node> group = graph.group(node);
            if (!graph.liberties(group)) {
                for (Node n : group) {
                    n.setStone(null);
                }
            }
        }
        
        // Suicide
        HashSet<Node> group = graph.group(playedNode);
        if (!graph.liberties(group)) {
            for (Node n : group) {
                n.setStone(null);
            }
        }
    }

    private void paintHover(Graphics2D g2d) {   // TODO: No need to recompute unless hoverNode is different from last time.
        if (playMove(hoverNode)) {
            players[turn].getStone().paint(g2d, hoverNode.x, hoverNode.y, 99);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Clears the graphic.
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graph.paintNodes(g2d);
        graph.paintEdges(g2d);
        history.get(history.size() - 1).paintStones(g2d);
        paintHover(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

}
