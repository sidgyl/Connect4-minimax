/**
 * File: ConnectFour.java
 * Author: Brian Borowski
 * Date created: April 9, 2012
 * Date last modified: September 1, 2012
 */
import gnu.getopt.Getopt;

import java.text.NumberFormat;
import java.util.Scanner;

public class ConnectFour {
    private static final NumberFormat nf = NumberFormat.getInstance();
    private final Board board;
    private char player;
    private final int gameType, maxDepth;
    private int plays;
    private static Scanner input = new Scanner(System.in);

    public ConnectFour(int gameType, int maxDepth)
            throws IllegalArgumentException {
        this.gameType = gameType;
        this.maxDepth = maxDepth;
        player = Board.MARK_RED;
        plays = 0;
        board = new Board();
    }

    public int getRows() {
        return Board.ROWS;
    }

    public int getGameType() {
        return gameType;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getPlays() {
        return plays;
    }

    public int getMaxPlays() {
        return Board.ROWS * Board.COLUMNS;
    }

    public char getPlayer() {
        return player;
    }

    public Board getBoard() {
        return board;
    }

    public void displayBoard() {
        board.display();
    }

    public void switchPlayers() {
        if (player == Board.MARK_BLACK) {
            player = Board.MARK_RED;
        } else {
            player = Board.MARK_BLACK;
        }
    }

    public char getWinner() {
        return board.getWinner();
    }

    public void dropChecker(int col, char player)
            throws IllegalArgumentException {
        if (player != Board.MARK_RED && player != Board.MARK_BLACK) {
            throw new IllegalArgumentException(
                "Invalid player '" + player + "' attempting to mark board.");
        }
        if (col < 0 || col >= Board.COLUMNS) {
            throw new IllegalArgumentException(
                "Invalid column " + col + " received.");
        }
        board.set(col, player);
        plays++;
    }

    public static int getColumn(char player, int maxCol)
            throws IllegalArgumentException {

        String color = Board.getColorOfPlayer(player);

        System.out.print(
                "Drop a " + color + " checker at column (1.." + maxCol
                + "): ");
        int column = 0;
        String strColumn = input.nextLine().trim();
        try {
            column = Integer.parseInt(strColumn);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(
                "Invalid column '" + strColumn + "' received.");
        }
        if (column < 1 || column > maxCol) {
            throw new IllegalArgumentException(
                "Invalid column " + column + " received.");
        }
        return column - 1;
    }

    public static String getHelp(String programName) {
        StringBuilder builder = new StringBuilder("Usage: java " + programName
                + " [options]\n");
        builder.append("   -g game type [1-4], where\n");
        builder.append("      1 = HUMAN vs. HUMAN\n");
        builder.append("      2 = HUMAN vs. COMPUTER [default]\n");
        builder.append("      3 = COMPUTER vs. HUMAN\n");
        builder.append("      4 = COMPUTER vs. COMPUTER\n");
        builder.append("   -m difficulty level [1-4], where\n");
        builder.append("      1 = BEGINNER\n");
        builder.append("      2 = INTERMEDIATE\n");
        builder.append("      3 = ADVANCED\n");
        builder.append("      4 = EXPERT [default]");
        return builder.toString();
    }

    private static ConnectFourConfig parseArgs(String programName, String[] args) {
        Getopt g = new Getopt(programName, args, "g:hm:");
        g.setOpterr(false);
        int c,
            gameType = ConnectFourConfig.HUMAN_COMPUTER,
            difficultyLevel = ConnectFourConfig.EXPERT;
        String arg;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'g':
                    arg = g.getOptarg();
                    try {
                        gameType = Integer.parseInt(arg);
                        if (gameType < ConnectFourConfig.HUMAN_HUMAN ||
                            gameType > ConnectFourConfig.COMPUTER_COMPUTER) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException nfe) {
                        System.err.println(programName
                                + ": Invalid game type '" + arg
                                + "'.");
                        System.exit(1);
                    }
                    break;
                case 'h':
                    System.out.println(getHelp(programName));
                    System.exit(0);
                case 'd':
                    arg = g.getOptarg();
                    try {
                        difficultyLevel = Integer.parseInt(arg);
                        if (difficultyLevel < ConnectFourConfig.BEGINNER ||
                            difficultyLevel > ConnectFourConfig.EXPERT) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException nfe) {
                        System.err.println(programName +
                                ": Invalid max depth '" + arg + "'.");
                        System.exit(1);
                    }
                    break;
                case '?':
                    System.err.println(programName + ": Unknown option '"
                            + (char)g.getOptopt() + "' received.");
                    System.exit(1);
                default:
                    break;
            }
        }
        return new ConnectFourConfig(gameType, difficultyLevel);
    }

    private static void doPlayerMove(char player, ConnectFour game) {
        while (true) {
            try {
                int col = getColumn(player, Board.COLUMNS);
                game.dropChecker(col, player);
                break;
            } catch (IllegalArgumentException iae) {
                System.out.println("Error: " + iae.getMessage());
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

    private static void doComputerMove(char player, ConnectFour game) {
        Minimax minimax = new Minimax(game.getBoard(), game.getMaxDepth());
        long start = System.currentTimeMillis();
        int col = minimax.alphaBeta(player);
        double elapsed = (System.currentTimeMillis() - start) / 1000.0;
        int boardsAnalyzed = minimax.getBoardsAnalyzed();
        System.out.print("Computer chose column " + (col + 1) + "; " +
                nf.format(boardsAnalyzed));
        if (boardsAnalyzed != 1) {
            System.out.println(" boards analyzed in " + elapsed + " seconds.");
        } else {
            System.out.println(" board analyzed in " + elapsed + " seconds.");
        }
        game.dropChecker(col, player);
    }

    public static void main(String[] args) {
        ConnectFourConfig config = parseArgs("ConnectFour", args);
        ConnectFour connectFour = null;
        try {
            connectFour = new ConnectFour(config.getGameType(),
                                          config.getMaxDepth());
        } catch (IllegalArgumentException iae) {
            System.err.println("Error: " + iae.getMessage());
            System.exit(1);
        }
        connectFour.displayBoard();
        int maxPlays = connectFour.getMaxPlays(),
            numPlays = connectFour.getPlays(),
            gameType = connectFour.getGameType();
        while (numPlays < maxPlays) {
            char player = connectFour.getPlayer();
            if (numPlays % 2 == 0) {
                if (gameType == ConnectFourConfig.HUMAN_HUMAN ||
                    gameType == ConnectFourConfig.HUMAN_COMPUTER) {
                    doPlayerMove(player, connectFour);
                } else {
                    doComputerMove(player, connectFour);
                }
            } else {
                if (gameType == ConnectFourConfig.HUMAN_HUMAN ||
                    gameType == ConnectFourConfig.COMPUTER_HUMAN) {
                    doPlayerMove(player, connectFour);
                } else {
                    doComputerMove(player, connectFour);
                }
            }

            connectFour.displayBoard();
            char winner = connectFour.getWinner();
            if (winner != Board.UNMARKED) {
                System.out.println("Player " + winner + " wins.");
                return;
            }
            connectFour.switchPlayers();
            numPlays = connectFour.getPlays();
        }
        System.out.println("Tie.");
        input.close();
    }
}
