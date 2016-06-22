package eu.chessdata.ui.round;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.chessdata.R;
import eu.chessdata.model.Game;
import eu.chessdata.model.Player;
import eu.chessdata.model.Tournament;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFabInterface;

/**
 * Created by Bogdan Oloeriu on 6/13/2016.
 */
public class RoundStateFragment extends Fragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private int mRoundNumber;
    private int mFirstTableNumber;
    private List<Player> mPlayers;
    private boolean mShowGames = false;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_round_state, container, false);
        setParameters();
        computeData();
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
        RoundGamesFragment fragment = RoundGamesFragment.newInstance(mTournamentKey,mRoundNumber,mClubKey);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games,fragment);
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
            (new GamesGenerationTask()).execute();
        }
        return true;
    }

    /**
     * Int starts the initial flow for paring game players
     */
    class GamesGenerationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String tournamentLoc = Constants.LOCATION_TOURNAMENTS
                    .replace(Constants.CLUB_KEY, mClubKey)
                    .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
            DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference(tournamentLoc);
            tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Tournament tournament = dataSnapshot.getValue(Tournament.class);
                        mFirstTableNumber = tournament.getFirstTableNumber();
                        onTimeToDecideIfWeeNeedToGenerateGames();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(tag, "Firebase error: " + databaseError.getMessage());
                }
            });
            return null;
        }
    }

    /**
     * get the players and then generate the games
     */
    private void onTimeToDecideIfWeeNeedToGenerateGames() {


        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber));
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //time to generate the games;
                    onTimeToCollectRoundPlayers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void onTimeToCollectRoundPlayers() {
        mPlayers = new ArrayList<>();
        Log.d(tag, "onTimeToCollectRoundPlayers round: " + mRoundNumber
                + " / clubKey = " + mClubKey
                + " / firstTableNumber = " + mFirstTableNumber);
        String playersLoc = Constants.LOCATION_ROUND_PLAYERS
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber));
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference(playersLoc);
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> items = dataSnapshot.getChildren();
                for (DataSnapshot item : items) {
                    mPlayers.add(item.getValue(Player.class));
                }
                onTimeToGenerateGames();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void onTimeToGenerateGames() {
        Log.d(tag, "onTimeToGenerateGames round: " + mRoundNumber
                + " / clubKey = " + mClubKey
                + " / firstTableNumber = " + mFirstTableNumber);
        int i = 0;
        List<Game> games = new ArrayList<>();
        Game game = new Game();

        //create the games
        int table = 0;

        for (Player player : mPlayers) {
            i++;
            if (i % 2 == 1) {
                table++;
                game = new Game();
                game.setTableNumber(table);
                game.setActualNumber(table + mFirstTableNumber - 1);
                game.setWhitePlayer(player);
            } else {
                game.setBlackPlayer(player);
                games.add(game);
                game = new Game();
            }
            if (i == mPlayers.size() && i%2==1) {
                game.setResult(4);
                games.add(game);
            }
        }

        //persist the games
        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber));
        DatabaseReference allGamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        for (Game gameItem : games) {
            DatabaseReference gameRef = allGamesRef.getRef().child(String.valueOf(gameItem.getTableNumber()));
            gameRef.setValue(gameItem);
        }
        showGames();


        /*RoundPagerFragment roundPagerFragment =(RoundPagerFragment) getFragmentManager().findFragmentByTag("RoundPagerFragment");
        roundPagerFragment.timeToDecideHowManyRoundsToShow();*/
    }
}
