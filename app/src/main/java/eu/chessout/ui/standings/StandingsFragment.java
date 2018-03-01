package eu.chessout.ui.standings;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessout.R;
import eu.chessout.model.Game;
import eu.chessout.model.RankedPlayer;
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

    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<RankedPlayer> mAdapter;

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

        //standings/$tournamentKey/$roundNumber/$categoryNumber/$standingNumber
        String standingsLocation = Constants.LOCATION_STANDINGS
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber))
                .replace(Constants.CATEGORY_NUMBER, Constants.CATEGORY_ABSOLUTE_NUMBER)
                .replace("/" + Constants.STANDING_NUMBER, "");
        mReference = FirebaseDatabase.getInstance().getReference(standingsLocation);
        mListView = (ListView) view.findViewById(R.id.list_view_standings);
        mAdapter = buildAdapter();
        mListView.setAdapter(mAdapter);

        return view;
    }

    private FirebaseListAdapter<RankedPlayer> buildAdapter() {
        FirebaseListAdapter<RankedPlayer> adapter = new FirebaseListAdapter<RankedPlayer>(getActivity(), RankedPlayer.class, R.layout.list_item_text, mReference) {


            @Override
            protected void populateView(View v, RankedPlayer model, int position) {
                StringBuffer sb = new StringBuffer();
                sb.append(model.getRankNumber());
                sb.append(". ");
                sb.append(model.getPlayerName());
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(sb.toString());
            }
        };

        return adapter;
    }


}
