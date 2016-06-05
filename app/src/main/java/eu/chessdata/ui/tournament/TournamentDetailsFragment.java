package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/5/2016.
 */
public class TournamentDetailsFragment extends Fragment {
    private final String tag = Constants.LOG_TAG;

    private String mClubKey;
    private String mTournamentKey;
    private String mTournamentName;

    public static TournamentDetailsFragment newInstance(String clubKey, String tournamentKey, String tournamentName){
        TournamentDetailsFragment vip = new TournamentDetailsFragment();
        vip.mClubKey = clubKey;
        vip.mTournamentKey = tournamentKey;
        vip.mTournamentName = tournamentName;
        return vip;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tournament_details,container,false);
        Log.d(tag, "TournamentDetailsFragment: " + mClubKey + "/" + mTournamentKey + "/" + mTournamentName);
        return fragmentView;
    }
}
