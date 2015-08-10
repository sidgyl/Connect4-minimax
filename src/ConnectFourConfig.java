/**
 * File: ConnectFourConfig.java
 * Author: Brian Borowski
 * Date created: April 9, 2012
 * Date last modified: September 1, 2012
 */
public class ConnectFourConfig {
    public static final int
        HUMAN_HUMAN = 1,
        HUMAN_COMPUTER = 2,
        COMPUTER_HUMAN = 3,
        COMPUTER_COMPUTER = 4,
        
        BEGINNER = 1,
        INTERMEDIATE = 2,
        ADVANCED = 3,
        EXPERT = 4;
    private int gameType, maxDepth;

    public ConnectFourConfig(int gameType, int difficultyLevel) {
        setGameType(gameType);
        setDifficulty(difficultyLevel);
    }

    public void setGameType(int gameType) throws IllegalArgumentException {
        if (gameType < HUMAN_HUMAN || gameType > COMPUTER_COMPUTER) {
            throw new IllegalArgumentException("Invalid value '" + gameType
                    + "' for game type.");
        }
        this.gameType = gameType;
    }

    public int getGameType() {
        return gameType;
    }
    
    public int getDifficultyLevel() {
        return maxDepth >> 1;
    }
    
    public void setDifficulty(int difficultyLevel) throws IllegalArgumentException {
        if (difficultyLevel < BEGINNER || difficultyLevel > EXPERT) {
            throw new IllegalArgumentException("Invalid value '"
                    + difficultyLevel + "' for difficulty level.");
        }
        this.maxDepth = difficultyLevel << 1;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
