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
package com.shobute.arbigo.main;

import com.shobute.arbigo.setup.FrameSetup;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.GroupLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author Ben Lloyd
 */
public class GUI extends JFrame {
    
    private FrameSetup frameSetup;
    private JDesktopPane jDesktopPane;

    /**
     * Creates new form GUI
     */
    public GUI() {
        jDesktopPane = new JDesktopPane() {
            // Set the background.
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                //g.setColor(Color.WHITE);
                //g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        frameSetup = new FrameSetup(jDesktopPane);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("ArbiGo");

        frameSetup.setVisible(true);
        jDesktopPane.add(frameSetup);
        frameSetup.setBounds(110, 60, 431, 354);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane, GroupLayout.DEFAULT_SIZE, 644,
                    Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane, GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
        );

        pack();
    }
    
}