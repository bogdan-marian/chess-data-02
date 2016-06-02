package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/2/2016.
 */
public class TournamentCreateDialogFragment extends DialogFragment{

    private final String tag = Constants.LOG_TAG;
    private String tournamentLocation;

    public static TournamentCreateDialogFragment newInstance(String clubKey){
        TournamentCreateDialogFragment fragment = new TournamentCreateDialogFragment();
        fragment.tournamentLocation = Constants.LOCATION_TOURNAMENT
                .replace(Constants.CLUB_KEY,clubKey);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(tag,"tournamentLocation: " + tournamentLocation);
        View view = inflater.inflate(R.layout.fragment_tournament_create_dialog,null,false);
        return view;
    }
}
