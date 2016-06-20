package eu.chessdata.ui.round;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import eu.chessdata.model.Game;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/20/2016.
 */
public class GameSetResultDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;
    private String mTournamentKey;
    private int mRoundNumber;
    private int mTableNumber;
    private String mWhitePlayer;
    private String mBlackPlayer;
    private boolean mNoPartner = false;

    private static Bundle getBundle(String tournamentKey,int roundNumber,Game game){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY,tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        bundle.putInt(Constants.TABLE_NUMBER, game.getTableNumber());
        bundle.putString(Constants.WHITE_PLAYER_NAME, game.getWhitePlayer().getName());
        bundle.putString(Constants.BLACK_PLAYER_NAME, game.getBlackPlayer().getName());
        boolean noPartner = false;
        if (game.getResult() == 4){
            noPartner = true;
        }
        bundle.putBoolean(Constants.NO_PARTNER,noPartner);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
        mTableNumber = getArguments().getInt(Constants.TABLE_NUMBER);
        mWhitePlayer = getArguments().getString(Constants.WHITE_PLAYER_NAME);
        mBlackPlayer = getArguments().getString(Constants.BLACK_PLAYER_NAME);
        mNoPartner = getArguments().getBoolean(Constants.NO_PARTNER);
    }

    public static GameSetResultDialog newInstance(String tournamentKey, int roundNumber, Game game){
        GameSetResultDialog dialog = new GameSetResultDialog();
        dialog.setArguments(getBundle(tournamentKey,roundNumber,game));
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setParameters();
        if (mNoPartner){
            this.dismiss();
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set result for table " + mTableNumber);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final CharSequence[] items = {mWhitePlayer,mBlackPlayer,"1/2 = 1/2"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = which+1;
                Log.d(tag,"Clicked on " + items[which] + " / " + which);
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
