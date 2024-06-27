/*
 * Copyright (C) 2024 util2
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
package org.wingate.myauth.view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author util2
 */
public class Menu {
    
    private final List<File> backSounds = new ArrayList<>();
    
    private String video;
    private Chapter chapter;
    
    private Image backgroundImage;
    
    private Image menuImage;
    private Point menuImageLocation;
    private Dimension menuImageDimension;

    public Menu() {
        video = null;
        chapter = null;
        backgroundImage = null;
        menuImage = null;
        menuImageLocation = null;
        menuImageDimension = null;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Image getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(Image menuImage) {
        this.menuImage = menuImage;
    }

    public List<File> getBackSounds() {
        return backSounds;
    }

    public Point getMenuImageLocation() {
        return menuImageLocation;
    }

    public void setMenuImageLocation(Point menuImageLocation) {
        this.menuImageLocation = menuImageLocation;
    }

    public Dimension getMenuImageDimension() {
        return menuImageDimension;
    }

    public void setMenuImageDimension(Dimension menuImageDimension) {
        this.menuImageDimension = menuImageDimension;
    }
    
}
