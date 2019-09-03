package eu.chessdata.ui.tournament.players;

import eu.chessdata.model.Player;
import eu.chessdata.model.RankedPlayer;

public class PlayerData {
    String playerKey;
    String playerName;
    int tournamentInitialOrder;

    public PlayerData(RankedPlayer player, String name){
        this.playerKey = player.getPlayerKey();
        this.playerName = name;
        this.tournamentInitialOrder = player.getTournamentInitialOrder();
    }
}
