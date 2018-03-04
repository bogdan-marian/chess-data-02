package eu.chessdata.ui.round;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.chessdata.model.Player;
import eu.chessdata.ui.tournament.PlayerAdapter;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MapUtil;
import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 6/14/2016.
 */
public class RoundAddPlayerDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private int mRoundNumber;

    ArrayList<Player>mPlayers;
    Map<String,Player>mPlayersMap;

    PlayerAdapter mPlayerAdapter;


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

    public static RoundAddPlayerDialog newInstance(String tournamentKey, int roundNumber){
        RoundAddPlayerDialog dialog = new RoundAddPlayerDialog();
        dialog.setArguments(getBundle(tournamentKey,roundNumber));
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setParameters();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add player to round: " + mRoundNumber);
        View view = inflater.inflate(R.layout.dialog_round_add_player,null,false);
        builder.setView(view);
        ListView listView = (ListView)view.findViewById(R.id.list_view_add_round_players);

        mPlayers = new ArrayList<>();
        mPlayersMap = new HashMap<>();

        mPlayerAdapter = new PlayerAdapter(getContext(),mPlayers);
        listView.setAdapter(mPlayerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String,Player>>list = new LinkedList<Map.Entry<String, Player>>(mPlayersMap.entrySet());
                Player player = list.get(position).getValue();
                addPlayer(player);
            }
        });

        (new UpdateListTask()).execute();
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return  builder.create();
    }

    private void addPlayer(final Player player) {
        String playerLoc = Constants.LOCATION_ROUND_ABSENT_PLAYERS
                .replace(Constants.TOURNAMENT_KEY,mTournamentKey)
                .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber))+"/"+player.getPlayerKey();
        final DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference(playerLoc);
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    playerRef.setValue(player);
                }
                getDialog().dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag,"Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String playersLoc = Constants.LOCATION_TOURNAMENT_PLAYERS
                    .replace(Constants.TOURNAMENT_KEY,mTournamentKey);
            DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference(playersLoc);
            playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item: dataSnapshot.getChildren()){
                        Player player = item.getValue(Player.class);
                        mPlayersMap.put(player.getPlayerKey(),player);
                    }
                    mPlayersMap = MapUtil.sortByValue(mPlayersMap);
                    for (Map.Entry<String,Player> entry: mPlayersMap.entrySet()){
                        mPlayerAdapter.add(entry.getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(tag,"Database error: " + databaseError.getMessage());
                }
            });
            return null;
        }
    }
}
