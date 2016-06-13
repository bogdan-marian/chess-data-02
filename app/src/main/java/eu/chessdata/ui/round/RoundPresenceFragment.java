package eu.chessdata.ui.round;

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
 * Created by Bogdan Oloeriu on 6/13/2016.
 */
public class RoundPresenceFragment extends Fragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    private static Bundle getBundle(String tournamentKey, int roundNumber){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        return bundle;
    }
    public static RoundPresenceFragment newInstance(String tournamentKey, int roundNumber){
        RoundPresenceFragment roundPresenceFragment = new RoundPresenceFragment();
        roundPresenceFragment.setArguments(getBundle(tournamentKey,roundNumber));
        return roundPresenceFragment;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_presence,container,false);
        setParameters();
        Log.d(tag,"RoundPresenceFragment: " + mTournamentKey + "/"+mRoundNumber);
        return view;
    }
}
