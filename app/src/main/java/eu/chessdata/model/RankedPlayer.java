package eu.chessdata.model;

/**
 * Created by Bogdan Oloeriu on 01/03/2018.
 */

public class RankedPlayer {
    private String tournamentKey;
    private String playerKey;
    /**
     * used only for round standings. It represents the round rank
     */
    private int rankNumber;
    private int tournamentOrderNumber;
    private int elo;
    private String playerName;

    public String getTournamentKey() {
        return tournamentKey;
    }

    public void setTournamentKey(String tournamentKey) {
        this.tournamentKey = tournamentKey;
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public int getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTournamentOrderNumber() {
        return tournamentOrderNumber;
    }

    public void setTournamentOrderNumber(int tournamentOrderNumber) {
        this.tournamentOrderNumber = tournamentOrderNumber;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }
}
