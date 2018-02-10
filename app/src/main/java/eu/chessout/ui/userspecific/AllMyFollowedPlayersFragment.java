package eu.chessout.ui.userspecific;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessout.R;
import eu.chessout.model.Player;
import eu.chessout.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/29/2016.
 */
public class AllMyFollowedPlayersFragment extends Fragment {
    private View mView;
    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_all_my_followed_players,container,false);
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String playerKeys = "/"+ Constants.PLAYER_KEY;
        String followedLoc = Constants.LOCATION_MY_FOLLOWED_PLAYERS_BY_PLAYER
                .replace(Constants.USER_KEY,userKey)
                .replace(playerKeys,"");
        mReference = FirebaseDatabase.getInstance().getReference(followedLoc);
        mListView = (ListView)mView.findViewById(R.id.list_view_followed_players);
        mAdapter = new FirebaseListAdapter<Player>(getActivity(),Player.class,R.layout.list_item_text,mReference) {
            @Override
            protected void populateView(View v, Player model, int position) {
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(model.getName());
            }
        };
        mListView.setAdapter(mAdapter);
        return mView;
    }
}
