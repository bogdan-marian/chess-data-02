package eu.chessout.utils;

import eu.chessdata.chesspairing.model.ChesspairingGame;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingResult;
import eu.chessout.model.Game;
import eu.chessout.model.Player;

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

    public static ChesspairingGame scanGame(Game game) {
        ChesspairingGame chesspairingGame = new ChesspairingGame();
        chesspairingGame.setTableNumber(game.getActualNumber());
        chesspairingGame.setWhitePlayer(scanPlayer(game.getWhitePlayer()));
        if (game.getBlackPlayer()!= null){
            chesspairingGame.setBlackPlayer(scanPlayer(game.getBlackPlayer()));
        }
        ChesspairingResult result = convertResult(game.getResult());
        chesspairingGame.setResult(result);

        return  chesspairingGame;
    }

    public static int scanResult(ChesspairingResult result){
        switch (result){
            case NOT_DECIDED:
                return 0;
            case WHITE_WINS:
                return 1;
            case BLACK_WINS:
                return 2;
            case DRAW_GAME:
                return 3;
            case BYE:
                return 4;
        }
        throw new IllegalStateException("New result type. please convert: " + result);
    }

    public static ChesspairingResult convertResult(int result){
        switch (result){
            case 0:
                return ChesspairingResult.NOT_DECIDED;
            case 1:
                return ChesspairingResult.WHITE_WINS;
            case 2:
                return ChesspairingResult.BLACK_WINS;
            case 3:
                return ChesspairingResult.DRAW_GAME;
            case 4:
                return ChesspairingResult.BYE;
        }
        throw new IllegalStateException("New result type. please convert: " + result);
    }
}
