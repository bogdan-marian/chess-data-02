package eu.chessdata.ui.round;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/14/2016.
 */
public class RoundAddPlayerDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    ArrayList<Player>mPlayers;
    Map<String,Player>mPlayersMap;

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

    public static RoundAddPlayerDialog newInstance(String tournamentKey, int roundNumber){
        RoundAddPlayerDialog dialog = new RoundAddPlayerDialog();
        dialog.setArguments(getBundle(tournamentKey,roundNumber));
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setParameters();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add player to round: " + mRoundNumber);
        View view = inflater.inflate(R.layout.dialog_round_add_player,null,false);
        builder.setView(view);
        ListView listView = (ListView)view.findViewById(R.id.list_view_add_round_players);

        return  builder.create();
    }
}
