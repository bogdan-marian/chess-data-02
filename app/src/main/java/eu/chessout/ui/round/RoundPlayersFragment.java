package eu.chessout.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import eu.chessout.model.Player;
import eu.chessout.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/13/2016.
 */
public class RoundPlayersFragment extends Fragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    private static Bundle getBundle(String tournamentKey, int roundNumber){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        return bundle;
    }
    public static RoundPlayersFragment newInstance(String tournamentKey, int roundNumber){
        RoundPlayersFragment roundPlayersFragment = new RoundPlayersFragment();
        roundPlayersFragment.setArguments(getBundle(tournamentKey,roundNumber));
        return roundPlayersFragment;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setParameters();
        View view = inflater.inflate(R.layout.fragment_round_players,container,false);
        TextView textView = (TextView)view.findViewById(R.id.round_players_simple_header);
        textView.setText("Round " + mRoundNumber+": Absent players");

        String roundPlayersLoc = Constants.LOCATION_ROUND_ABSENT_PLAYERS
                .replace(Constants.TOURNAMENT_KEY,mTournamentKey)
                .replace(Constants.ROUND_NUMBER,String.valueOf(mRoundNumber));
        mReference = FirebaseDatabase.getInstance().getReference(roundPlayersLoc);
        mListView = (ListView) view.findViewById(R.id.list_view_round_players);
        mAdapter = new FirebaseListAdapter<Player>(getActivity(), Player.class, R.layout.list_item_text,mReference) {
            @Override
            protected void populateView(View v, Player model, int position) {
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(model.getName());

            }
        };
        mListView.setAdapter(mAdapter);
        return view;
    }
}
