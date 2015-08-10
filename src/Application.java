/**
 * File: Application.java
 * Author: Brian Borowski
 * Date created: May 1999
 * Date last modified: August 31, 2012
 */

/**
 * Main class for starting the application.
 */
public class Application {
    public static final String NAME = "Connect Four";

    /**
     * Default constructor.
     * Creates an instance of the gui.
     */
    public Application() {
        new GUI(null);
    }

    /**
     * Main method.
     * Creates an instance of the application.
     */
    public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                Application.NAME);
        new Application();
    }
}
