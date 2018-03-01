package eu.chessout.ui.standings;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessout.R;
import eu.chessout.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class StandingsFragment extends Fragment {

    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;
    private String mClubKey;
    private Context mContext;

    public StandingsFragment() {
        // Required empty public constructor
    }

    public static Bundle buildBundle(String tournamentKey, int roundNumber, String clubKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER, roundNumber);
        bundle.putString(Constants.CLUB_KEY, clubKey);
        return bundle;
    }

    public static StandingsFragment newInstance(String tournamentKey, int roundNumber, String clubKey) {
        StandingsFragment standingsFragment = new StandingsFragment();
        Bundle bundle = buildBundle(tournamentKey, roundNumber, clubKey);
        standingsFragment.setArguments(bundle);
        return standingsFragment;
    }

    private void setParameters() {
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
        mClubKey = getArguments().getString(Constants.CLUB_KEY);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setParameters();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_standings, container, false);
        TextView headerView = (TextView) view.findViewById(R.id.standings_simple_header);
        headerView.setText("Ranking round " + mRoundNumber);

        return view;
    }


}
