package eu.chessdata.ui.club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
 * Created by Bogdan Oloeriu on 6/6/2016.
 */
public class ClubPlayersFragment extends Fragment {
    private String tag = Constants.LOG_TAG;

    private String mClubKey;
    private View mView;
    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    public static ClubPlayersFragment newInstance(String clubKey){
        ClubPlayersFragment fragment = new ClubPlayersFragment();
        fragment.mClubKey = clubKey;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_club_players,container,false);
        Log.d(tag,"Club key = " + mClubKey);
        return mView;
    }
}
