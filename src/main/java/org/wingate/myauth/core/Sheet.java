/*
 * Copyright (C) 2023 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.myauth.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author util2
 */
public class Sheet extends JPanel {
    
    private final Dimension sheetSize;
    private float scale = 1f;
    private Point xy = new Point();

    public Sheet(Dimension sheetSize) {
        this.sheetSize = sheetSize;
        setSize(sheetSize);
        setDoubleBuffered(true);
    }

    public Dimension getSheetSize() {
        return sheetSize;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Point getXY() {
        return xy;
    }

    public void setXY(Point xy) {
        this.xy = xy;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.white);
        g.fillRect(
                Math.round(xy.x * scale),
                Math.round(xy.y * scale),
                Math.round(getWidth() * scale),
                Math.round(getHeight() * scale)
        );
        
        
    }
    
}
