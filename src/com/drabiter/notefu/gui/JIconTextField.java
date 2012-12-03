package com.drabiter.notefu.gui;

/*
 * Copyright 2010 Georgios Migdos <cyberpython@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * 
 *@authorGeorgios Migdos <cyberpython@gmail.com> 
 */


/**
 * @author Hendra <drabiter@gmail.com>
 * 
 * CHANGES:
 * - path to icon
 * - setMargin() method moved to constructor
 * - remove textX usage
 * - change icon's location to right
 */
public class JIconTextField extends JTextField{

    private Icon icon;
    private Insets dummyInsets;

    public JIconTextField(){
        super();
        this.icon = createImageIcon("com/drabiter/notefu/gui/find.png");

        Border border = UIManager.getBorder("TextField.border");
        JTextField dummy = new JTextField();
        this.dummyInsets = border.getBorderInsets(dummy);
        setMargin(new Insets(2, 2, 2, 2));
    }

    public void setIcon(Icon icon){
        this.icon = icon;
    }

    public Icon getIcon(){
        return this.icon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(this.icon!=null){
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            int x = this.getWidth() - dummyInsets.right - iconWidth; //this x is our icon's x
            int y = (this.getHeight() - iconHeight)/2;
            icon.paintIcon(this, g, x, y);
        }

    }
    
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = this.getClass().getClassLoader().getResource(path);
        if (imgURL != null)
            return new ImageIcon(imgURL);
        else
        	return null;
    }

}

