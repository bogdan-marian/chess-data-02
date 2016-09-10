package eu.chessdata.utils;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.model.Player;

/**
 * Created by Bogdan Oloeriu on 11/09/2016.
 */
public class MyChesspairingUtils {

    /**
     * Converts Player onto ChesspairingPlayer
     * @param player
     * @return
     */
    public static ChesspairingPlayer scanPlayer(Player player){
        ChesspairingPlayer chesspairingPlayer = new ChesspairingPlayer();
        chesspairingPlayer.setName(player.getName());
        int elo ;
        if (player.getElo()!=0){
            elo = player.getElo();
        }else{
            elo = player.getClubElo();
        }
        chesspairingPlayer.setElo(elo);
        chesspairingPlayer.setPlayerKey(player.getPlayerKey());
        return chesspairingPlayer;
    }

}
