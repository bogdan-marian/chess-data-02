package eu.chessdata.ui.tournament.players;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

//tutorial: https://blog.uncommon.is/a-guide-to-sortedlist-part-1-5-replacing-arraylist-with-sortedlist-in-recyclerview-f73eca8bc602
public class IntegerTournamentPlayersFragment extends Fragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private View mView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Player> mAdapter;

    private RecyclerView recyclerView;
    private Button addButton;
    private Button removeButton;

    private IntegerListAdapter listAdapter;
    private ArrayList<Integer> integerAdditionList = new ArrayList<>();
    private ArrayList<Integer> integerRemovalList = new ArrayList<>();
    private Integer integerToAdd;
    private Integer integerToRemove;


    public static IntegerTournamentPlayersFragment newInstance(String tournamentKey, String clubKey, boolean isAdminUser) {
        IntegerTournamentPlayersFragment fragment = new IntegerTournamentPlayersFragment();
        fragment.mTournamentKey = tournamentKey;
        fragment.mClubKey = clubKey;
        fragment.mIsAdminUser = isAdminUser;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tournament_players_v2, container, false);
        recyclerView = mView.findViewById(R.id.recycler);
        addButton = mView.findViewById(R.id.addButton);
        addButton.setOnClickListener(buildAddListener());
        removeButton = mView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(buildRemoveListener());

        setupSeeds();
        setupRecycler();
        randomizeAddButton();

        return mView;
    }


    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listAdapter = new IntegerSortedListAdapter();
        recyclerView.setAdapter(listAdapter);
    }

    private View.OnClickListener buildAddListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (integerToAdd >= 0) {
                    listAdapter.addInteger(integerToAdd);
                    integerRemovalList.add(integerToAdd);
                    integerAdditionList.remove(integerToAdd);
                    randomizeAddButton();
                    randomizeRemoveButton();
                }
            }
        };
        return onClickListener;
    }

    private View.OnClickListener buildRemoveListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (integerToRemove >= 0) {
                    listAdapter.removeInteger(integerToRemove);
                    integerAdditionList.add(integerToRemove);
                    integerRemovalList.remove(integerToRemove);
                    randomizeRemoveButton();
                    randomizeAddButton();
                }
            }
        };
        return onClickListener;
    }


    private void setupSeeds() {
        for (int i = 0; i < 5; i++) {
            integerAdditionList.add(Integer.valueOf(i));
        }
    }

    private void randomizeRemoveButton() {
        integerToRemove = randIntegerToRemove();
        if (integerToRemove >= 0) {
            removeButton.setText("Remove " + integerToRemove.intValue());
        } else {
            removeButton.setText("Remove");
        }
    }

    private void randomizeAddButton() {
        integerToAdd = randIntegerToAdd();
        if (integerToAdd >= 0) {
            addButton.setText("Add " + integerToAdd.intValue());
        } else {
            addButton.setText("Add");
        }
    }


    private Integer randIntegerToAdd() {
        if (integerAdditionList.size() > 0) {
            int position = (int) (Math.random() * 10) % integerAdditionList.size();
            return integerAdditionList.get(position);
        }
        return -1;
    }

    private Integer randIntegerToRemove() {
        if (integerRemovalList.size() > 0) {
            int position = (int) (Math.random() * 10) % integerRemovalList.size();
            return integerRemovalList.get(position);
        }
        return -1;
    }
}
