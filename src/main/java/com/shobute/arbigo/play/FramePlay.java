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

import com.shobute.arbigo.common.Colour;
import com.shobute.arbigo.common.Graph;
import com.shobute.arbigo.common.Stone;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author Ben Lloyd
 */
public class FramePlay extends JInternalFrame implements InternalFrameListener {

    private Graph graph;
    private int timeInterval;
    private Board board;
    private SideBar sideBar;
    private ArrayList<Player> players;
    private int turn;

    /**
     * Creates new form FamePlay
     *
     * @param graph
     * @param numPlayers
     * @param timeInterval
     */
    public FramePlay(Graph graph, int numPlayers, int timeInterval) {
        this.graph = graph == null ? new Graph(9) : graph;
        this.timeInterval = timeInterval;
        this.board = new Board(this);
        this.sideBar = new SideBar(this);
        
        // Initate players
        if (numPlayers < 2 || numPlayers > Colour.colours.length) {
            numPlayers = 2;
        }
        players = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(timeInterval));
        }

        setVisible(true);
        setSize(100, 100);
        setResizable(true);
        setMaximizable(true);
        setClosable(true);
        setTitle("Play Game");
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(this);

        board.setBackground(new Color(255, 255, 255));
        board.setPreferredSize(new Dimension(400, 400));

        sideBar.setBackground(new Color(200, 200, 200));
        sideBar.setPreferredSize(new Dimension(150, 400));

        add(board, BorderLayout.CENTER);
        add(sideBar, BorderLayout.EAST);

        pack();
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public Player getPlayer() {
        return players.get(turn);
    }
    
    public void resign() {
        players.remove(turn);
        turn = turn % players.size();

        if (players.size() == 1) {
            board.gameOver();
            sideBar.gameOver();
            JOptionPane.showMessageDialog(this,
                    getPlayer().getName() + " wins!");
        }
    }
    
    public Stone getStone() {
        return getPlayer().getStone();
    }
    
    public void nextTurn() {
        getPlayer().incrementTime();
        turn = (turn + 1) % players.size();
        sideBar.repaint();
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent ife) {
        if (board.isGameOver()) {
            dispose();
        } else {
            int close = JOptionPane.showConfirmDialog(null,
                    "Really Close? Game is still in progress", "Exit",
                    JOptionPane.YES_NO_OPTION);  
            if (close == JOptionPane.YES_OPTION) {
                dispose();  
            }
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent ife) {
        sideBar.gameOver();
    }
        
    @Override
    public void internalFrameOpened(InternalFrameEvent ife) {
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent ife) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent ife) {
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent ife) {
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent ife) {
    }

}
