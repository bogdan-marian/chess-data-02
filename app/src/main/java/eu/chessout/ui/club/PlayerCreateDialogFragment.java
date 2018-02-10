package eu.chessout.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessout.R;
import eu.chessout.model.Player;
import eu.chessout.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/7/2016.
 */
public class PlayerCreateDialogFragment extends DialogFragment {
    private static String tag = Constants.LOG_TAG;

    private String mClubName;
    private String mClubKey;

    private View mView;

    public static PlayerCreateDialogFragment newInstance(String clubName, String clubKey) {
        PlayerCreateDialogFragment fragment = new PlayerCreateDialogFragment();
        fragment.mClubName = clubName;
        fragment.mClubKey = clubKey;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.dialog_create_player, null);
        builder.setView(mView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton("Create Player,", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                persistPlayer();
            }
        });

        return builder.create();
    }

    private void persistPlayer() {
        Player player = buildPlayer();
        Log.d(tag, "Player name = " + player.getName());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String playersLoc = Constants.LOCATION_CLUB_PLAYERS
                .replace(Constants.CLUB_KEY, mClubKey);
        DatabaseReference playersRef = database.getReference(playersLoc);
        DatabaseReference playerRef = playersRef.push();
        String playerKey = playerRef.getKey();
        player.setPlayerKey(playerKey);
        playerRef.setValue(player);
    }

    private Player buildPlayer() {
        String name = ((EditText) mView.findViewById(R.id.profileName)).getText().toString();
        String email = ((EditText) mView.findViewById(R.id.email)).getText().toString();
        String eloString = ((EditText) mView.findViewById(R.id.elo)).getText().toString();
        int elo = Integer.valueOf(eloString);
        String clubEloString = ((EditText) mView.findViewById(R.id.clubElo)).getText().toString();
        int clubElo = Integer.valueOf(eloString);
        Player player = new Player(name, email, mClubKey, mClubName,elo,clubElo);
        return player;
    }
}
