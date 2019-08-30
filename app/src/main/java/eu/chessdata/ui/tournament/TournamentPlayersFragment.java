package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/9/2016.
 */
public class TournamentPlayersFragment extends Fragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private View mView;
    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    public static TournamentPlayersFragment newInstance(String tournamentKey, String clubKey, boolean isAdminUser) {
        TournamentPlayersFragment fragment = new TournamentPlayersFragment();
        fragment.mTournamentKey = tournamentKey;
        fragment.mClubKey = clubKey;
        fragment.mIsAdminUser = isAdminUser;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tournament_players, container, false);

        String playersLoc = Constants.LOCATION_TOURNAMENT_PLAYERS
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
        mReference = FirebaseDatabase.getInstance().getReference(playersLoc);
        mListView = (ListView) mView.findViewById(R.id.list_view_tournament_players);
        mAdapter = new FirebaseListAdapter<Player>(getActivity(), Player.class, R.layout.list_item_text, mReference) {
            @Override
            protected void populateView(View v, Player model, int position) {
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(model.getName());
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = mAdapter.getItem(position);
                FollowPlayerDialog followPlayerDialog = FollowPlayerDialog.newInstance(player);
                followPlayerDialog.show(getActivity().getSupportFragmentManager(), "FollowPlayerDialog");
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (mIsAdminUser) {
                    Player player = mAdapter.getItem(position);
                    TournamentChangeInitialOrderDialog tournamentChangeInitialOrderDialog
                            = TournamentChangeInitialOrderDialog.newInstance(
                            player, mTournamentKey, mClubKey, "noUserKey");
                    tournamentChangeInitialOrderDialog.show(
                            getActivity().getSupportFragmentManager(), "FollowPlayerDialog");
                    return true;
                } else {
                    Toast.makeText(
                            getContext(),
                            "You are not authorized to update the tournament order",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
        return mView;
    }
}
