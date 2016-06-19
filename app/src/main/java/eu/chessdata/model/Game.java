package eu.chessdata.model;

/**
 * Created by Bogdan Oloeriu on 6/19/2016.
 */
public class Game {
    private int tableNumber;
    private int actualNumber;
    private Player whitePlayer;
    private Player blackPlayer;
    /**
     * 0 still not decided
     * 1 white player wins
     * 2 black player wins
     * 3 draw game
     * 4 no partner
     */
    private int result = 0;

    public Game(){}

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
