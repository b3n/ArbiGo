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
package com.shobute.arbigo.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Ben Lloyd
 */
public class Stone {
    
    private Color colour;
    public static final Color[] colours = new Color[]{
        Color.BLACK, Color.WHITE, Color.RED, Color.BLUE, Color.GREEN,
        Color.YELLOW, Color.PINK, Color.CYAN, Color.ORANGE, Color.GRAY
    };
    
    public Stone(int colour) {
        this.colour = colours[colour];
    }
    
    public Color getColour() {
        return colour;
    }
    
    public void paint(Graphics2D g2d, float x, float y) {
        this.paint(g2d, x, y, 255);
    }
    
    public void paint(Graphics2D g2d, float x, float y, int alpha) {
        int z = 10;
        g2d.setColor(new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha));
        Shape circle = new Ellipse2D.Float(x - z, y - z, z*2, z*2);
        g2d.fill(circle);

        // Stroke
        g2d.setPaint(new Color(0, 0, 0, alpha));
        g2d.draw(circle);
    }
    
}
