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
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Ben Lloyd
 */
public class Board extends JPanel implements ActionListener {
    
    private Graph board;
    private Graphics2D g2d;
    private Timer timer;
    private Player[] players;
    private int turn = 0;
    private Node hoverNode;
    
    public Board(Graph graph, int numPlayers) {
        this.board = graph;
        
        if (board == null) {
            board  = new Graph();
            Node[][] nodes = new Node[9][9];
            for (int x = 0; x < 9 ; x++) {
                for (int y = 0; y < 9; y++) {
                    nodes[x][y] = new Node(new Point(30+x*30, 30+y*30));
                }
            }
            for (int x = 0; x < nodes.length; x++) {
                for (int y = 0; y < nodes[x].length; y++) {
                    if (y+1 < nodes[x].length) nodes[x][y].addAdjacentNode(nodes[x][y+1]);
                    if (y > 0) nodes[x][y].addAdjacentNode(nodes[x][y-1]);
                    if (x+1 < nodes.length) nodes[x][y].addAdjacentNode(nodes[x+1][y]);
                    if (x > 0) nodes[x][y].addAdjacentNode(nodes[x-1][y]);
                    board.addNode(nodes[x][y]);
                }
            }
        }
        
        // Initate players
        if (numPlayers < 2 || numPlayers > Stone.colours.length) numPlayers = 2;
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) players[i] = new Player();
        
        board.removeColourings();
        board.setColour(new Color(150, 150, 150));
        
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                Node node = board.nodeAt(me.getPoint());
                if (node != null && valid(node)) {
                    node.setStone(players[turn].getStone());
                    removeCaptured(node);
                    turn = (turn + 1) % players.length;
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent me) {
                hoverNode = board.nodeAt(me.getPoint());
            }
        };
        
        addMouseListener(listener);
        addMouseMotionListener(listener);

        timer = new Timer(20, this);
        timer.start();
        
    }
    
    private boolean valid(Node node) {
        return true;    // TODO: superko
    }
    
    private void removeCaptured(Node playedNode) {
        for (Node node : playedNode.getAdjacentNodes()) {
            HashSet<Node> group = board.group(node);
            if (!board.liberties(group)) {
                for (Node n : group) n.setStone(null);
            }
        }
    }
    
    private void paintHover(Graphics2D g2d) {
        if (hoverNode != null) {
            players[turn].getStone().paint(g2d, hoverNode.x, hoverNode.y, 99);
        }
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        board.paintNodes(g2d);
        board.paintEdges(g2d);
        board.paintStones(g2d);
        paintHover(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }
    
}
