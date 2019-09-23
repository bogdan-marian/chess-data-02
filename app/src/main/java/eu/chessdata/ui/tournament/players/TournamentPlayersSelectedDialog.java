package eu.chessdata.ui.tournament.players;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import eu.chessdata.model.Player;
import eu.chessdata.ui.tournament.TournamentChangeInitialOrderDialog;
import eu.chessdata.utils.Constants;


public class TournamentPlayersSelectedDialog extends DialogFragment {
    private String tag = Constants.LOG_TAG;

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private Player mPlayer;
    private Context mContext;
    private CharSequence[] mItems;
    private static final int folow_player = 0;
    private static final int change_initial_order = 1;

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
        builder.setItems(mItems, getDialogListener());

        return builder.create();
    }

    DialogInterface.OnClickListener getDialogListener() {
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> {

            (new ProcessSelection()).execute(new Integer(i));
        };
        return listener;
    }

    private class ProcessSelection extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int selection = params[0];
            switch (selection) {
                case folow_player:
                    Log.d(tag, "selected " + mItems[folow_player]);
                    break;
                case change_initial_order:
                    showChangeInitialOrderDialog();
                    break;
                default:
                    throw new IllegalStateException("Not recognized selection " + selection);
            }
            dismiss();
            return null;
        }
    }

    private void folowPlayer() {
        //todo: please implement this
    }


    private void showChangeInitialOrderDialog() {
        TournamentChangeInitialOrderDialog tournamentChangeInitialOrderDialog
                = TournamentChangeInitialOrderDialog.newInstance(
                mPlayer, mTournamentKey, mClubKey, mPlayer.getUserKey()
        );
        tournamentChangeInitialOrderDialog.show(
                getActivity().getSupportFragmentManager(), "TournamentChangeInitialOrderDialog");
    }
}
