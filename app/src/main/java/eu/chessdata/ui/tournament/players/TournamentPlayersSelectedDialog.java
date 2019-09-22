package eu.chessdata.ui.tournament.players;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import eu.chessdata.model.Player;


public class TournamentPlayersSelectedDialog extends DialogFragment {

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private Player mPlayer;
    private Context mContext;
    private CharSequence[] mItems;

    public static TournamentPlayersSelectedDialog newInstance(String tournamentKey,
                                                              String clubKey,
                                                              boolean isAdminUser,
                                                              Player player) {

        TournamentPlayersSelectedDialog vipDialog = new TournamentPlayersSelectedDialog();
        vipDialog.mTournamentKey = tournamentKey;
        vipDialog.mClubKey = clubKey;
        vipDialog.mIsAdminUser = isAdminUser;
        vipDialog.mPlayer = player;

        if (isAdminUser) {
            vipDialog.mItems = new CharSequence[]{"Follow player", "Change initial order"};
        } else {
            vipDialog.mItems = new CharSequence[]{"Follow player"};
        }

        return vipDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tournament player: " + mPlayer.getName());
        builder.setItems(mItems, getDialogListenr());

        return builder.create();
    }

    DialogInterface.OnClickListener getDialogListenr() {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> dismiss();
        return listener;
    }

}
