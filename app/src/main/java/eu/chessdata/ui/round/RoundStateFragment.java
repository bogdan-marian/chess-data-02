package eu.chessdata.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/13/2016.
 */
public class RoundStateFragment extends Fragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;
    private boolean mShowGames = false;

    public static Bundle getBundle(String tournamentKey, int roundNumber){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_state,container,false);
        setParameters();
        computeData();
        return view;
    }

    /**
     * compute mShowGames
     */
    private void computeData(){
        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY,mTournamentKey)
                .replace(Constants.ROUND_NUMBER,String.valueOf(mRoundNumber));
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null){
                    mShowGames = true;
                }
                configureVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag,"Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void configureVisibility(){
        if (mShowGames){
            showGames();
        }else{
            showPresence();
        }
    }

    protected void showGames() {
        Log.d(tag,"Show games " + mTournamentKey + " / " + mRoundNumber);
    }

    protected void showPresence(){
        RoundPlayersFragment fragment = RoundPlayersFragment.newInstance(mTournamentKey,mRoundNumber);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games, fragment);
        transaction.commit();
    }

    /**
     * this function should only be called from the pager that holds this fragment
     */
    protected void configureFab(){
        Log.d(tag,"Time to configureFab() from state fragment: round = " + mRoundNumber);
    }
}
