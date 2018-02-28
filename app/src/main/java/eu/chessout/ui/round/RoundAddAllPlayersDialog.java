package eu.chessout.ui.round;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import eu.chessout.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 05/02/2018.
 */

public class RoundAddAllPlayersDialog extends DialogFragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    private static Bundle getBundle(String tournamentKey, int roundNumber){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
    }

    public static RoundAddAllPlayersDialog newInstance(String tournamentKey, int roundNumber){
        RoundAddAllPlayersDialog dialog = new RoundAddAllPlayersDialog();
        dialog.setArguments(getBundle(tournamentKey,roundNumber));
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setParameters();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("all players: " + mRoundNumber+" ?");

        return builder.create();
    }
}
