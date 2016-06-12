package eu.chessdata.ui.tournament;

import android.app.Dialog;
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

import eu.chessdata.R;
import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MapUtil;

/**
 * Created by Bogdan Oloeriu on 6/11/2016.
 */
public class TournamentAddPlayerDialog extends DialogFragment {
    private String tag = Constants.LOG_TAG;
    private String mTournamentKey;
    private String mClubKey;

    private View mView;

    ArrayList<Player> mPlayers;
    Map<String,Player> mPlayersMap;

    PlayerAdapter mPlayerAdapter;

    public static TournamentAddPlayerDialog newInstance(String tournamentKey, String clubKey) {
        TournamentAddPlayerDialog fragment = new TournamentAddPlayerDialog();
        fragment.mTournamentKey = tournamentKey;
        fragment.mClubKey = clubKey;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Select player");

        mView = inflater.inflate(R.layout.dialog_tournament_add_player, null, false);
        builder.setView(mView);
        ListView listView = (ListView) mView.findViewById(R.id.list_view_tournament_players);

        mPlayers = new ArrayList<>();
        mPlayersMap = new HashMap<>();

        mPlayerAdapter = new PlayerAdapter(getContext(),mPlayers);
        listView.setAdapter(mPlayerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String,Player>> list = new LinkedList<Map.Entry<String, Player>>(mPlayersMap.entrySet());
                Player player = list.get(position).getValue();
                addPlayer(player);
            }
        });

        (new UpdateListTask()).execute();
        return builder.create();
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String clubPlayersLoc = Constants.LOCATION_CLUB_PLAYERS
                    .replace(Constants.CLUB_KEY,mClubKey);
            DatabaseReference clubPlayersRef = FirebaseDatabase.getInstance().getReference(clubPlayersLoc);
            clubPlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(tag, "End of onPostExecute");
        }
    }

    private void addPlayer(final Player player){
        String tournamentPlayerLoc = Constants.LOCATION_TOURNAMENT_PLAYERS
                .replace(Constants.TOURNAMENT_KEY, mTournamentKey)+"/"+player.getPlayerKey();
        final DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference(tournamentPlayerLoc);
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
}