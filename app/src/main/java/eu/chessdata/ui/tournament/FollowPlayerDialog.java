package eu.chessdata.ui.tournament;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.model.Player;
import eu.chessdata.model.User;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/28/2016.
 */
public class FollowPlayerDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;
    private Player mPlayerToFollow;

    public static FollowPlayerDialog newInstance(Player playerToFollow){
        FollowPlayerDialog vipDialog = new FollowPlayerDialog();
        vipDialog.mPlayerToFollow = playerToFollow;
        return vipDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Would you like to follow "+mPlayerToFollow.getName()+"  ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                persistNewPlayerToFollow();
            }
        });
        return builder.create();
    }

    public void persistNewPlayerToFollow(){
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String followLoc = Constants.LOCATION_MY_FOLLOWED_PLAYERS_BY_PLAYER
                .replace(Constants.USER_KEY,userKey)
                .replace(Constants.PLAYER_KEY, mPlayerToFollow.getPlayerKey());
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference(followLoc);
        followRef.setValue(mPlayerToFollow);

        //set the global followers
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user = new User(name,email,userKey);

        String globalFollowerLoc = Constants.LOCATION_GLOBAL_FOLLOWER_BY_PLAYER
                .replace(Constants.PLAYER_KEY, mPlayerToFollow.getPlayerKey())
                .replace(Constants.USER_KEY,userKey);
        DatabaseReference globalFollowerRef = FirebaseDatabase.getInstance().getReference(globalFollowerLoc);
        globalFollowerRef.setValue(user);

        //dismiss the dialog
        dismiss();
    }
}
