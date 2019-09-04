package eu.chessdata.ui.tournament.players;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.model.RankedPlayer;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFirebaseUtils;

public class TournamentPlayersFragment extends Fragment {

    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private View mView;
    private ListView mListView;
    private RecyclerView mRecyclerView;
    private ValueEventListener mValueEventListener;
    private Map<String, PlayerData> oldData = new HashMap<>();
    private Map<String, PlayerData> newData = new HashMap<>();
    private Map<String, Player> clubPlayers = new HashMap<>();

    private PlayerDataListAdapter mPlayerDataListAdapter;

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
        mView = inflater.inflate(R.layout.fragment_tournament_players_v3, container, false);


        mRecyclerView = mView.findViewById(R.id.recycler_view_tournament_players);

        setupRecycler();
        mValueEventListener = buildValueEventListener();

        (new UpdateListTask()).execute();

        return mView;
    }

    private void setupRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mPlayerDataListAdapter = new PlayerDataSortedListAdapter();
        mRecyclerView.setAdapter(mPlayerDataListAdapter);
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            List<Player> players = MyFirebaseUtils.getClubPlayers(mClubKey);
            players.forEach(player -> clubPlayers.put(player.getPlayerKey(), player));

            String playersLoc = Constants.LOCATION_TOURNAMENT_INITIAL_ORDER
                    .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference(playersLoc);
            mReference.addValueEventListener(mValueEventListener);

            return null;
        }
    }

    ValueEventListener buildValueEventListener() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oldData.clear();
                oldData.putAll(newData);
                newData.clear();
                int i = 0;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    RankedPlayer rankedPlayer = item.getValue(RankedPlayer.class);

                    Optional<Player> optionalPlayer = Optional.ofNullable(clubPlayers.get(
                            rankedPlayer.getPlayerKey()));
                    if (optionalPlayer.isPresent()) {


                        PlayerData playerData = new PlayerData(rankedPlayer,
                                optionalPlayer.get().getName());
                        String playerKey = playerData.playerKey;
                        newData.put(playerKey, playerData);
                        mPlayerDataListAdapter.addPlayer(playerData);
                        if (oldData.containsKey(playerKey)) {
                            oldData.remove(playerKey);
                        }

                    }
                }
                for (Map.Entry<String, PlayerData> entry : oldData.entrySet()) {
                    mPlayerDataListAdapter.removePlayer(entry.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return valueEventListener;
    }

}