package eu.chessdata.ui.tournament.players;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.ui.tournament.TournamentPlayersFragment01;
import eu.chessdata.utils.Constants;

public class TournamentPlayersFragment extends Fragment {

    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private View mView;
    private ListView mListView;
    private RecyclerView mRecyclerView;
    private DatabaseReference mReference;
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

        return mView;
    }

    private void setupRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlayerDataListAdapter = new PlayerDataSortedListAdapter();
        mRecyclerView.setAdapter(mPlayerDataListAdapter);
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            String playersLoc = Constants.LOCATION_TOURNAMENT_INITIAL_ORDER
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
            mReference = FirebaseDatabase.getInstance().getReference(playersLoc);



            return null;
        }
    }
}
