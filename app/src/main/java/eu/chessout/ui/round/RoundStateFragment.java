package eu.chessout.ui.round;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import eu.chessout.R;
import eu.chessout.model.Player;
import eu.chessout.utils.Constants;
import eu.chessout.utils.MyCloudService;
import eu.chessout.utils.MyFabInterface;
import eu.chessout.utils.MyFirebaseUtils;

/**
 * Created by Bogdan Oloeriu on 6/13/2016.
 */
public class RoundStateFragment extends Fragment implements MyFirebaseUtils.OnUserIsAdmin {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private int mRoundNumber;
    private int mFirstTableNumber;
    private List<Player> mPlayers;
    private boolean mShowGames = false;
    private boolean mUserIsAdmin = false;
    private Context mContext;
    private CURRENT_STATE mCurrentState;

    ;

    public static Bundle getBundle(String tournamentKey, int roundNumber, String clubKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER, roundNumber);
        bundle.putString(Constants.CLUB_KEY, clubKey);
        return bundle;
    }

    private void setParameters() {
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
        mClubKey = getArguments().getString(Constants.CLUB_KEY);
        mContext = getActivity().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_round_state, container, false);
        setParameters();
        computeData();
        MyFirebaseUtils.isCurrentUserAdmin(mClubKey, this);
        return view;
    }

    /**
     * compute mShowGames
     */
    private void computeData() {
        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber));
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mShowGames = true;
                }
                configureVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void configureVisibility() {
        if (mShowGames) {
            showGames();
        } else {
            showPresence();
        }
    }

    protected void showGames() {
        RoundGamesFragment fragment = RoundGamesFragment.newInstance(mTournamentKey, mRoundNumber, mClubKey);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games, fragment);
        transaction.commit();
        mShowGames = true;
    }

    protected void showPresence() {
        RoundPlayersFragment fragment = RoundPlayersFragment.newInstance(mTournamentKey, mRoundNumber);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games, fragment);
        transaction.commit();
        mShowGames = false;
    }

    /**
     * this function should only be called from the pager that holds this fragment
     */
    protected void configureFab() {
        MyFabInterface myFabInterface = (MyFabInterface) getActivity();
        myFabInterface.disableFab();

        if (!mShowGames) {
            myFabInterface.enableFab(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(tag, "Configure tournament and round fab for round nr " + mRoundNumber);
                    // add one player dialog
                    RoundAddPlayerDialog roundAddPlayerDialog = RoundAddPlayerDialog.newInstance(mTournamentKey, mRoundNumber);
                    roundAddPlayerDialog.show(getActivity().getSupportFragmentManager(), "roundAddPlayerDialog");

                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        if (!mShowGames) {
            inflater.inflate(R.menu.round_players_fragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_generate_games) {
            if (mUserIsAdmin) {
                (new GamesGenerationTask()).execute();
            } else {
                Toast.makeText(getContext(), "You are not a club administrator", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onUserIsAdmin(boolean isAdmin) {
        mUserIsAdmin = isAdmin;
    }


    private enum CURRENT_STATE {PRESENCE, GAMES}

    /**
     * Int starts the initial flow for paring game players
     */
    class GamesGenerationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MyCloudService.startActionGenerateNextRound(mContext, mClubKey, mTournamentKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mShowGames = true;
            showGames();
        }
    }
}
