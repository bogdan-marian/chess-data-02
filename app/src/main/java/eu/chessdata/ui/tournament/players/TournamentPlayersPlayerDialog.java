package eu.chessdata.ui.tournament.players;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import eu.chessdata.model.Player;


public class TournamentPlayersPlayerDialog extends DialogFragment {

    private String mTournamentKey;
    private String mClubKey;
    private boolean mIsAdminUser;
    private Player mPlayer;
    private Context mContext;

    public static TournamentPlayersPlayerDialog newInstance(String tournamentKey,
                                                            String clubKey,
                                                            boolean isAdminUser,
                                                            Player player){

        TournamentPlayersPlayerDialog vipDialog = new TournamentPlayersPlayerDialog();
        vipDialog.mTournamentKey = tournamentKey;
        vipDialog.mClubKey = clubKey;
        vipDialog.mIsAdminUser = isAdminUser;
        vipDialog.mPlayer = player;

        return vipDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tournament player: ");

        return builder.create();
    }
}
