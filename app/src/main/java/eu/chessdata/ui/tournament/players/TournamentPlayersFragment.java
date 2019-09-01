package eu.chessdata.ui.tournament.players;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

//tutorial: https://blog.uncommon.is/a-guide-to-sortedlist-part-1-5-replacing-arraylist-with-sortedlist-in-recyclerview-f73eca8bc602
public class TournamentPlayersFragment extends Fragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private View mView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    @BindView(R.id.recycler)
    private RecyclerView recyclerView;
    private Unbinder unbinder;

    private IntegerListAdapter listAdapter;
    private ArrayList<Integer> integerAdditionList = new ArrayList<>();
    private ArrayList<Integer> integerRemovalList = new ArrayList<>();
    private Integer integerToAdd;
    private Integer integerToRemove;



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
        mView = inflater.inflate(R.layout.fragment_tournament_players_v2, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }


    @OnClick(R.id.addButton) public void onAddClick() {
        Log.d(tag, "Add Button Clicked");
    }

    @OnClick(R.id.removeButton) public void onRemoveClick() {
        Log.d(tag, "Remove Button Clicked");
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
