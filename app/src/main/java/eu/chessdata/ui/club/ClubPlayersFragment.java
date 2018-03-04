package eu.chessdata.ui.club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.ui.tournament.FollowPlayerDialog;
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

    public static ClubPlayersFragment newInstance(String clubKey) {
        ClubPlayersFragment fragment = new ClubPlayersFragment();
        fragment.mClubKey = clubKey;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_club_players, container, false);

        //Firebase reference
        String clubsLocation = Constants.LOCATION_CLUB_PLAYERS
                .replace(Constants.CLUB_KEY, mClubKey);
        mReference = FirebaseDatabase.getInstance().getReference(clubsLocation);
        mListView = (ListView) mView.findViewById(R.id.list_view_club_players);
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
                followPlayerDialog.show(getActivity().getSupportFragmentManager(),"FollowPlayerDialog");
            }
        });
        return mView;
    }
}
