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

import com.shobute.arbigo.common.Graph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 *
 * @author Ben Lloyd
 */
public class FramePlay extends JInternalFrame {

    private Graph graph;
    private int numPlayers;
    private Board board;
    private JPanel sideBar;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;

    /**
     * Creates new form FamePlay
     *
     * @param graph
     * @param numPlayers
     */
    public FramePlay(Graph graph, int numPlayers) {
        this.graph = graph == null ? new Graph(9) : graph;
        this.numPlayers = numPlayers;

        initComponents();
    }

    public JPanel getSideBar() {
        return this.sideBar;
    }

    public Board getBoard() {
        return this.board;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    private void initComponents() {
        board = new Board(this);
        sideBar = new SideBar(this);

        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();

        setSize(100, 100);
        setResizable(true);
        setMaximizable(true);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        board.setBackground(new java.awt.Color(255, 255, 255));
        board.setPreferredSize(new Dimension(400, 400));

        sideBar.setBackground(new java.awt.Color(200, 200, 200));
        sideBar.setPreferredSize(new Dimension(150, 400));

        add(board, BorderLayout.CENTER);
        add(sideBar, BorderLayout.EAST);

        pack();
    }

}
