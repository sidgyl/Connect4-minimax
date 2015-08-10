/**
 * File: GamePanel.java
 * Author: Brian Borowski
 * Date created: August 27, 2012
 * Date last modified: August 6, 2014
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final long serialVersionUID = 1L;
    private static final NumberFormat nf = NumberFormat.getInstance();

    private final Image cellImage, blackCheckerImage, redCheckerImage,
                        grayCheckerImage, pinkCheckerImage;
    private final int cellSize, width, height;
    private final Font largeFont = new Font("Dialog", Font.PLAIN, 40),
                       smallFont = new Font("Dialog", Font.BOLD, 15),
                       numberFont = new Font("Dialog", Font.BOLD, 14);
    private final String[] help = {
            "Use the LEFT and RIGHT arrows to position the checker.",
            "Use the DOWN arrow to drop the checker.",
            "Go to File -> New Game to start playing." };

    private ConnectFour connectFour;
    private char[][] grid;
    private boolean isPlayerTurn, isComputerTurn, displayHelp, showMoveNumbers;
    private volatile boolean isRunning, isChoiceMade;
    private int checkerColumn, yCoord;
    private ConnectFourConfig config;
    private Thread thread;
    private JLabel statusLabel;
    private String resultString;

    public GamePanel(ConnectFourConfig config, JLabel statusLabel) {
        this.config = config;
        this.statusLabel = statusLabel;
        cellImage = Utility.getImage("images/cell.png");
        blackCheckerImage = Utility.getImage("images/blackchecker.png");
        redCheckerImage = Utility.getImage("images/redchecker.png");
        grayCheckerImage = Utility.getImage("images/graychecker.png");
        pinkCheckerImage = Utility.getImage("images/pinkchecker.png");
        cellSize = cellImage.getHeight(null);
        width = cellImage.getWidth(null) * Board.COLUMNS;
        height = cellSize * (Board.ROWS + 1);
        displayHelp = true;
        setPreferredSize(new Dimension(width, height));
        addKeyListener(this);
        setFocusable(true);
        repaint();
    }

    public void startGame() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        grid = null;
        displayHelp = false;
        isRunning = true;
    }

    public void stopGame() {
        isRunning = false;
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (final InterruptedException ie) { }
        thread = null;
    }

    public void reset() {
        stopGame();
        statusLabel.setText("Welcome to " + Application.NAME + ".");
        resultString = null;
        connectFour = new ConnectFour(config.getGameType(), config.getMaxDepth());
        startGame();
        repaint();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setShowMoveNumbers(boolean showMoveNumbers) {
        this.showMoveNumbers = showMoveNumbers;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) { }
    }

    private void animateMove(int checkerColumn) {
        int maxCoord = cellSize *
                (connectFour.getBoard().getFirstAvailableRow(checkerColumn) + 1);
        while (isRunning) {
            yCoord++;
            repaint();
            if (yCoord >= maxCoord) {
                break;
            }
            sleep(1);
        }
    }

    private void doPlayerMove(char player) {
        isPlayerTurn = true;
        isChoiceMade = false;
        yCoord = 0;
        checkerColumn = 3;
        repaint();
        while (isRunning) {
            sleep(50);
            if (isChoiceMade) {
                if (connectFour.getBoard().isColumnAvailable(checkerColumn)) {
                    animateMove(checkerColumn);
                    connectFour.dropChecker(checkerColumn, player);
                    grid = connectFour.getBoard().getGrid();
                    break;
                } else {
                    isChoiceMade = false;
                }
            }
        }
        isPlayerTurn = false;
    }

    private void doComputerMove(char player) {
        isComputerTurn = true;
        yCoord = 0;
        checkerColumn = 3;
        repaint();
        Minimax minimax = new Minimax(connectFour.getBoard(),
                                      connectFour.getMaxDepth());
        long start = System.currentTimeMillis();
        int col = minimax.alphaBeta(player);
        double elapsed = (System.currentTimeMillis() - start) / 1000.0;
        int boardsAnalyzed = minimax.getBoardsAnalyzed();
        String status;
        if (boardsAnalyzed != 1) {
            status = nf.format(boardsAnalyzed) + " boards analyzed in "
                    + elapsed + " seconds.";
        } else {
            status = nf.format(boardsAnalyzed) + " board analyzed in "
                    + elapsed + " seconds.";
        }
        statusLabel.setText(status);
        while (checkerColumn < col) {
            sleep(200);
            checkerColumn++;
            repaint();
        }
        while (checkerColumn > col) {
            sleep(200);
            checkerColumn--;
            repaint();
        }
        sleep(200);
        checkerColumn = col;
        animateMove(col);
        connectFour.dropChecker(col, player);
        grid = connectFour.getBoard().getGrid();
        isComputerTurn = false;
    }

    public void run() {
        connectFour = new ConnectFour(config.getGameType(),
                                      config.getMaxDepth());
        int maxPlays = connectFour.getMaxPlays(),
            numPlays = connectFour.getPlays(),
            gameType = connectFour.getGameType();
        while (numPlays < maxPlays && isRunning) {
            char player = connectFour.getPlayer();
            if (numPlays % 2 == 0) {
                if (gameType == ConnectFourConfig.HUMAN_HUMAN ||
                    gameType == ConnectFourConfig.HUMAN_COMPUTER) {
                    doPlayerMove(player);
                } else {
                    doComputerMove(player);
                }
            } else {
                if (gameType == ConnectFourConfig.HUMAN_HUMAN ||
                    gameType == ConnectFourConfig.COMPUTER_HUMAN) {
                    doPlayerMove(player);
                } else {
                    doComputerMove(player);
                }
            }

            char winner = connectFour.getWinner();
            if (winner != Board.UNMARKED) {
                Cell[] winningCells = connectFour.getBoard().getWinningCells();
                for (Cell cell : winningCells) {
                    if (winner == Board.MARK_BLACK) {
                        grid[cell.row][cell.column] = Board.MARK_GRAY;
                    } else {
                        grid[cell.row][cell.column] = Board.MARK_PINK;
                    }
                }
                if (gameType == ConnectFourConfig.HUMAN_HUMAN) {
                    resultString = Board.getColorOfPlayer(winner) + " wins!";
                } else if (gameType == ConnectFourConfig.HUMAN_COMPUTER
                        && winner == Board.MARK_RED
                        || gameType == ConnectFourConfig.COMPUTER_HUMAN
                        && winner == Board.MARK_BLACK) {
                    resultString = "You win!";
                } else if (gameType == ConnectFourConfig.HUMAN_COMPUTER
                        && winner == Board.MARK_BLACK
                        || gameType == ConnectFourConfig.COMPUTER_HUMAN
                        && winner == Board.MARK_RED) {
                    resultString = "Computer wins.";
                }
                repaint();
                isRunning = false;
                return;
            }
            connectFour.switchPlayers();
            numPlays = connectFour.getPlays();
        }
        if (isRunning) {
            resultString = "Tie.";
            repaint();
        }
        isRunning = false;
    }

    private void displayDirections(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(smallFont);
        g2d.setColor(Color.BLACK);
        int y = 24;
        for (String s : help) {
            final int strWidth =
                    g2d.getFontMetrics().charsWidth(s.toCharArray(), 0,
                    s.length());
            g2d.drawString(s, (width - strWidth) >> 1, y);
            y += 20;
        }
    }

    private void displayResult(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(largeFont);
        g2d.setColor(Color.BLACK);
        final int strWidth =
                g2d.getFontMetrics().charsWidth(
                        resultString.toCharArray(), 0, resultString.length());
        g2d.drawString(resultString, (width - strWidth) >> 1, 50);
    }

    private void displayBoard(final Graphics2D g2d) {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLUMNS; col++) {
                g2d.drawImage(cellImage, cellSize * col, cellSize * (row + 1),
                        this);
            }
        }
    }
    
    private void displayMoveNumbers(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(numberFont);
        g2d.setColor(Color.WHITE);
        final int strHeight = g2d.getFontMetrics().getAscent() -
                              g2d.getFontMetrics().getDescent();
        int[][] moveNumbers = connectFour.getBoard().getMoveNumbers();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLUMNS; col++) {
                int moveNumber = moveNumbers[row][col];
                if (moveNumber != 0) {
                    String moveNumberStr = String.valueOf(moveNumber);
                    final int strWidth = 
                            g2d.getFontMetrics().charsWidth(
                                    moveNumberStr.toCharArray(), 0,
                                    moveNumberStr.length());
                    g2d.drawString(moveNumberStr,
                        cellSize * col + ((cellSize - strWidth) >> 1),
                        cellSize * (row + 1) + ((cellSize + strHeight) >> 1));
                }
            }
        }        
    }

    protected void paintComponent(final Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        if (isPlayerTurn || isComputerTurn) {
            Image checker;
            if (connectFour.getPlayer() == Board.MARK_BLACK) {
                checker = blackCheckerImage;
            } else {
                checker = redCheckerImage;
            }
            g2d.drawImage(checker, cellSize * checkerColumn, yCoord, this);
        }
        if (grid != null) {
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLUMNS; col++) {
                    char c = grid[row][col];
                    if (c == Board.MARK_BLACK) {
                        g2d.drawImage(blackCheckerImage, cellSize * col,
                                cellSize * (row + 1), this);
                    } else if (c == Board.MARK_RED) {
                        g2d.drawImage(redCheckerImage, cellSize * col, cellSize
                                * (row + 1), this);
                    } else if (c == Board.MARK_GRAY) {
                        g2d.drawImage(grayCheckerImage, cellSize * col,
                                cellSize * (row + 1), this);
                    } else if (c == Board.MARK_PINK) {
                        g2d.drawImage(pinkCheckerImage, cellSize * col,
                                cellSize * (row + 1), this);
                    }
                }
            }
        }
        if (displayHelp) {
            displayDirections(g2d);
        } else if (resultString != null) {
            displayResult(g2d);
        }
        displayBoard(g2d);
        if (showMoveNumbers && grid != null) {
            displayMoveNumbers(g2d);
        }
    }

    public void keyPressed(final KeyEvent keyEvent) {
        if (isPlayerTurn && !isChoiceMade) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (checkerColumn != 0) {
                        checkerColumn--;
                    }
                    repaint();
                    break;
                case KeyEvent.VK_RIGHT:
                    if (checkerColumn != 6) {
                        checkerColumn++;
                    }
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    isChoiceMade = true;
                    break;
                default:
                    break;
            }
        }
    }

    public void keyReleased(final KeyEvent keyEvent) { }

    public void keyTyped(final KeyEvent keyEvent) { }
}
