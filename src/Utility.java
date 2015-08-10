/**
 * File: Utility.java
 * Author: Brian Borowski
 * Date created: August 1, 2011
 * Date last modified: April 13, 2012
 */
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class Utility {

    public static Image getImage(final String filename) {
        ImageIcon icon;
        final URL url = Utility.class.getResource(filename);
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            // Read from file.
            icon = new ImageIcon(filename);

            // Try to read from URL.
            if ((icon == null) ||
                (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
                try {
                    icon = new ImageIcon(new URL(filename));
                } catch (final MalformedURLException murle) {
                    // Not a URL.
                    return null;
                }
            }
        }
        return icon.getImage();
    }
}
