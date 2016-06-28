package eu.chessdata.ui.tournament;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import eu.chessdata.model.Player;
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
                Log.d(tag,"Time to implement follow "+ mPlayerToFollow.getName());
            }
        });
        return builder.create();
    }
}
