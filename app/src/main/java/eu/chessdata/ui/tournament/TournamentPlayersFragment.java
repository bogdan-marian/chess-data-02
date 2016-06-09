package eu.chessdata.ui.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/9/2016.
 */
public class TournamentPlayersFragment extends Fragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private View mView;
    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    public static TournamentPlayersFragment newInstance(String tournamentKey){
        TournamentPlayersFragment fragment = new TournamentPlayersFragment();
        fragment.mTournamentKey = tournamentKey;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tournament_players,container,false);
        return mView;
    }
}
