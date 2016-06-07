package eu.chessdata.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 6/7/2016.
 */
public class PlayerCreateDialogFragment extends DialogFragment{
    private String mClubName;

    private View mView;

    public static PlayerCreateDialogFragment newInstance(String clubName){
        PlayerCreateDialogFragment fragment = new PlayerCreateDialogFragment();
        fragment.mClubName = clubName;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.dialog_create_player,null);
        builder.setView(mView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
