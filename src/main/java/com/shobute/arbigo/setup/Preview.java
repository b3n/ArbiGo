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
package com.shobute.arbigo.setup;

import com.shobute.arbigo.common.Graph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

/**
 *
 * @author Ben Lloyd
 */
public class Preview extends JPanel  {
    
    private Graph graph;
    private double scaleFactor;
    private int r;
    private Dimension graphSize;
    
    public Preview() {
        scaleFactor = 1;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (graph != null) {
                    setscaleFactor();
                    repaint();
                }
            }
        });
    }
    
    private void setscaleFactor() {
        double cWidth = getSize().getWidth();
        double cHeight = getSize().getHeight();
        double gWidth = graphSize.getWidth();
        double gHeight = graphSize.getHeight();
        scaleFactor = Math.min(cWidth / (gWidth + 2 * r),
                cHeight / (gHeight + 2 * r));
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.r = graph.getShortestRadius();
        this.graphSize = graph.getSize();
        setscaleFactor();
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); // clears the graphic
        Graphics2D g2d = (Graphics2D) g;      
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (graph != null) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("Board preview:", 10, 20);
            
            graph.scale(g2d, scaleFactor);
            graph.paintEdges(g2d);
            graph.paintNodes(g2d);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.drawString("No board loaded:", 10, 20);
        }
    }
    
}