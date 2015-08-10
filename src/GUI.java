/**
 * File: GUI.java
 * Author: Brian Borowski
 * Date created: August 27, 2012
 * Date last modified: August 6, 2014
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ApplicationStarter applicationStarter;
    private final ConnectFourConfig config;
    private final GamePanel gamePanel;
    private final JLabel statusLabel, gameTypeDifficultyLabel;
    private JRadioButtonMenuItem humanComputerItem, computerHumanItem,
            twoPlayerItem, beginnerItem, intermediateItem, advancedItem,
            expertItem, selectedGameType, selectedDifficultyLevel;
    private JCheckBoxMenuItem showMoveNumbersItem;
    private JMenu difficultyLevelMenu;
    private ButtonGroup gameTypeGroup, difficultyLevelGroup;

    public GUI(final ApplicationStarter appStarter) {
        super(Application.NAME);
        this.applicationStarter = appStarter;
        config = new ConnectFourConfig(ConnectFourConfig.HUMAN_COMPUTER,
                                       ConnectFourConfig.EXPERT);

        setJMenuBar(getCreatedMenuBar());

        statusLabel = new JLabel("Welcome to " + Application.NAME + ".");
        gameTypeDifficultyLabel = new JLabel(getGameTypeDifficultyStatus());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 2, 0, 2);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        final JPanel statusPanel1 = new JPanel();
        statusPanel1.setLayout(new GridBagLayout());
        statusPanel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel1.add(statusLabel, gbc);
        statusPanel1.setPreferredSize(new Dimension(250, 25));
        final JPanel statusPanel2 = new JPanel();
        statusPanel2.setLayout(new GridBagLayout());
        statusPanel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel2.add(gameTypeDifficultyLabel, gbc);
        statusPanel2.setPreferredSize(new Dimension(190, 25));

        final JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(1, 0, 0, 0);
        gbc.weightx = 0.7;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.BOTH;
        statusPanel.add(statusPanel1, gbc);
        gbc.weightx = 0.3;
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        statusPanel.add(statusPanel2, gbc);

        gamePanel = new GamePanel(config, statusLabel);

        final Container contentPane = getContentPane();
        contentPane.add(gamePanel, BorderLayout.CENTER);
        contentPane.add(statusPanel, BorderLayout.PAGE_END);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new ClosingWindowListener(gamePanel));

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        requestFocus();
        gamePanel.requestFocusInWindow(); // Must request focus to receive key events.
    }

    private String getGameTypeDifficultyStatus() {
        StringBuilder builder = new StringBuilder();
        int gameType = config.getGameType();
        if (gameType == ConnectFourConfig.HUMAN_COMPUTER) {
            builder.append("Human vs. Computer");
        } else if (gameType == ConnectFourConfig.COMPUTER_HUMAN) {
            builder.append("Computer vs. Human");
        } else {
            builder.append("Two player");
        }
        if (gameType != ConnectFourConfig.HUMAN_HUMAN) {
            int difficultyLevel = config.getDifficultyLevel();
            switch (difficultyLevel) {
                case ConnectFourConfig.BEGINNER:
                    builder.append(", Beginner");
                    break;
                case ConnectFourConfig.INTERMEDIATE:
                    builder.append(", Intermediate");
                    break;
                case ConnectFourConfig.ADVANCED:
                    builder.append(", Advanced");
                    break;
                default:
                    builder.append(", Expert");
                    break;
            }
        }
        return builder.toString();
    }

    private JMenuBar getCreatedMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        KeyStroke ks;
        final JMenuItem newGame = new JMenuItem("New Game");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.ALT_MASK);
        newGame.setAccelerator(ks);
        newGame.setMnemonic(KeyEvent.VK_N);
        newGame.addActionListener(new NewActionListener());
        fileMenu.add(newGame);
        fileMenu.add(new JSeparator());

        final JMenuItem exitItem = new JMenuItem("Exit");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK);
        exitItem.setAccelerator(ks);
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(new ExitActionListener());
        fileMenu.add(exitItem);

        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic('O');

        final JMenu gameTypeMenu = new JMenu("Game Type");
        gameTypeMenu.setMnemonic('G');
        humanComputerItem = new JRadioButtonMenuItem("Human vs. Computer", true);
        humanComputerItem.setMnemonic('H');
        humanComputerItem.addActionListener(new GameTypeActionListener(this));
        selectedGameType = humanComputerItem;
        computerHumanItem = new JRadioButtonMenuItem("Computer vs. Human", false);
        computerHumanItem.setMnemonic('C');
        computerHumanItem.addActionListener(new GameTypeActionListener(this));
        twoPlayerItem = new JRadioButtonMenuItem("Two Player", false);
        twoPlayerItem.setMnemonic('T');
        twoPlayerItem.addActionListener(new GameTypeActionListener(this));

        gameTypeMenu.add(humanComputerItem);
        gameTypeMenu.add(computerHumanItem);
        gameTypeMenu.add(twoPlayerItem);

        gameTypeGroup = new ButtonGroup();
        gameTypeGroup.add(humanComputerItem);
        gameTypeGroup.add(computerHumanItem);
        gameTypeGroup.add(twoPlayerItem);

        difficultyLevelMenu = new JMenu("Difficulty Level");
        difficultyLevelMenu.setMnemonic('D');
        beginnerItem = new JRadioButtonMenuItem("Beginner", false);
        beginnerItem.setMnemonic('B');
        beginnerItem.addActionListener(new DifficultyLevelActionListener(this));
        intermediateItem = new JRadioButtonMenuItem("Intermediate", false);
        intermediateItem.setMnemonic('I');
        intermediateItem.addActionListener(new DifficultyLevelActionListener(
                this));
        advancedItem = new JRadioButtonMenuItem("Advanced", false);
        advancedItem.setMnemonic('A');
        advancedItem.addActionListener(new DifficultyLevelActionListener(this));
        expertItem = new JRadioButtonMenuItem("Expert", true);
        expertItem.setMnemonic('E');
        expertItem.addActionListener(new DifficultyLevelActionListener(this));
        selectedDifficultyLevel = expertItem;

        difficultyLevelMenu.add(beginnerItem);
        difficultyLevelMenu.add(intermediateItem);
        difficultyLevelMenu.add(advancedItem);
        difficultyLevelMenu.add(expertItem);

        difficultyLevelGroup = new ButtonGroup();
        difficultyLevelGroup.add(beginnerItem);
        difficultyLevelGroup.add(intermediateItem);
        difficultyLevelGroup.add(advancedItem);
        difficultyLevelGroup.add(expertItem);

        showMoveNumbersItem = new JCheckBoxMenuItem("Show Move Numbers");
        showMoveNumbersItem.setMnemonic(KeyEvent.VK_N);
        showMoveNumbersItem.addActionListener(new ShowMoveNumbersActionListener());

        optionsMenu.add(gameTypeMenu);
        optionsMenu.add(difficultyLevelMenu);
        optionsMenu.add(showMoveNumbersItem);

        final JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(new AboutActionListener(this));

        final JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void doApplicationClosing() {
        if (applicationStarter != null) {
            gamePanel.stopGame();
            applicationStarter.close();
        } else {
            System.exit(0);
        }
    }

    class ClosingWindowListener implements WindowListener {
        private final GamePanel gamePanel;

        public ClosingWindowListener(final GamePanel gamePanel) {
            this.gamePanel = gamePanel;
        }

        public void windowClosing(final WindowEvent e) {
            gamePanel.stopGame();
            doApplicationClosing();
        }

        public void windowActivated(final WindowEvent e) { }

        public void windowDeactivated(final WindowEvent e) { }

        public void windowDeiconified(final WindowEvent e) { }

        public void windowIconified(final WindowEvent e) { }

        public void windowClosed(final WindowEvent e) { }

        public void windowOpened(final WindowEvent e) { }
    }

    class ExitActionListener implements ActionListener {

        public void actionPerformed(final ActionEvent e) {
            doApplicationClosing();
        }
    }

    class NewActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            gamePanel.reset();
        }
    }

    class GameTypeActionListener implements ActionListener {
        private JFrame parent;

        public GameTypeActionListener(JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(final ActionEvent e) {
            int choice = 0;
            boolean isRunning = gamePanel.isRunning();
            if (isRunning) {
                choice = JOptionPane.showConfirmDialog(parent,
                        "Changing the game type level at this time will\n"
                                + "require you to abort the current game.\n"
                                + "Abort game and apply settings now?\n",
                        "Question", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    selectedGameType.setSelected(true);
                    return;
                }
            }
            Object o = e.getSource();
            if (o == twoPlayerItem) {
                difficultyLevelMenu.setEnabled(false);
                config.setGameType(ConnectFourConfig.HUMAN_HUMAN);
                selectedGameType = twoPlayerItem;
            } else {
                difficultyLevelMenu.setEnabled(true);
                if (o == humanComputerItem) {
                    config.setGameType(ConnectFourConfig.HUMAN_COMPUTER);
                    selectedGameType = humanComputerItem;
                } else {
                    config.setGameType(ConnectFourConfig.COMPUTER_HUMAN);
                    selectedGameType = computerHumanItem;
                }
            }
            gameTypeDifficultyLabel.setText(getGameTypeDifficultyStatus());
            if (isRunning) {
                gamePanel.reset();
            }
        }
    }

    class DifficultyLevelActionListener implements ActionListener {
        private JFrame parent;

        public DifficultyLevelActionListener(JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(final ActionEvent e) {
            int choice = 0;
            boolean isRunning = gamePanel.isRunning();
            if (isRunning) {
                choice = JOptionPane.showConfirmDialog(parent,
                        "Changing the difficulty level at this time will\n"
                                + "require you to abort the current game.\n"
                                + "Abort game and apply settings now?\n",
                        "Question", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    selectedDifficultyLevel.setSelected(true);
                    return;
                }
            }
            Object o = e.getSource();
            if (o == beginnerItem) {
                config.setDifficulty(ConnectFourConfig.BEGINNER);
                selectedDifficultyLevel = beginnerItem;
            } else if (o == intermediateItem) {
                config.setDifficulty(ConnectFourConfig.INTERMEDIATE);
                selectedDifficultyLevel = intermediateItem;
            } else if (o == advancedItem) {
                config.setDifficulty(ConnectFourConfig.ADVANCED);
                selectedDifficultyLevel = advancedItem;
            } else {
                config.setDifficulty(ConnectFourConfig.EXPERT);
                selectedDifficultyLevel = expertItem;
            }
            gameTypeDifficultyLabel.setText(getGameTypeDifficultyStatus());
            if (isRunning) {
                gamePanel.reset();
            }
        }
    }
    
    class ShowMoveNumbersActionListener implements ActionListener {

        public void actionPerformed(final ActionEvent e) {
            gamePanel.setShowMoveNumbers(showMoveNumbersItem.getState());
            gamePanel.repaint();
        }
    }

    class AboutActionListener implements ActionListener {
        private final JFrame parent;

        public AboutActionListener(final JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(final ActionEvent e) {
            final String[] data = { "Version 1.2.0",
                    "\u00a9 2012-2014 Brian S. Borowski", "All Rights Reserved.",
                    "Build: August 6, 2014" };
            new AboutDialog(parent, Application.NAME, data, "images/icon.png");
        }
    }
}
