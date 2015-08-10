/**
 * File: ImagePanel.java
 * Author: Brian Borowski
 * Date created: May 1999
 * Date last modified: January 27, 2011
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Image img;

    public ImagePanel(final String filename) {
        this(Utility.getImage(filename));
    }

    public ImagePanel(final Image img) {
        this.img = img;
        final Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    public void paintComponent(final Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
}
