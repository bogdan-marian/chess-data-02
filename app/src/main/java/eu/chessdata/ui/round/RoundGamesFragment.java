package eu.chessdata.ui.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Game;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/19/2016.
 */
public class RoundGamesFragment extends Fragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    private ListView mListView;
    private DatabaseReference mReference;
    private FirebaseListAdapter<Game> mAdapter;

    private static Bundle getBundle(String tournamentKey, int roundNumber){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER,roundNumber);
        return bundle;
    }

    private void setParameters(){
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
    }

    public static RoundGamesFragment newInstance(String tournamentKey, int roundNumber){
        RoundGamesFragment roundGamesFragment = new RoundGamesFragment();
        roundGamesFragment.setArguments(getBundle(tournamentKey,roundNumber));
        return roundGamesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_games,container,false);
        setParameters();

        String roundGamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                .replace(Constants.ROUND_NUMBER,String.valueOf(mRoundNumber));
        mReference = FirebaseDatabase.getInstance().getReference(roundGamesLoc);
        mListView = (ListView)view.findViewById(R.id.list_view_round_games);
        mAdapter = new FirebaseListAdapter<Game>(getActivity(),Game.class,R.layout.list_item_text,mReference) {
            @Override
            protected void populateView(View v, Game model, int position) {
                StringBuffer sb = new StringBuffer();
                Player whitePlayer = model.getWhitePlayer();
                Player blackPlayer = model.getBlackPlayer();
                sb.append(model.getActualNumber()+". ");
                sb.append(whitePlayer.getName()+" ");
                sb.append(getResult( model.getResult())+" ");
                if (blackPlayer != null) {
                    sb.append(blackPlayer.getName() + " ");
                }
                ((TextView) v.findViewById(R.id.list_item_text_simple_view)).setText(sb.toString());
            }

            private String getResult(int result){
                String format = null;
                if (result == 1){
                    format = "1-0";
                }else if (result == 2){
                    format = "0-1";
                }else if (result == 0){
                    format = "---";
                }else if (result == 3){
                    format = "1/2-1/2";
                }else if (result == 4){
                    format = "1";
                }
                return format;
            }
        };
        mListView.setAdapter(mAdapter);
        return view;
    }
}
