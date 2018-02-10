package eu.chessout.ui.round;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessout.model.Game;
import eu.chessout.utils.Constants;
import eu.chessout.utils.MyCloudService;

/**
 * Created by Bogdan Oloeriu on 6/20/2016.
 */
public class GameSetResultDialog extends DialogFragment {
    private String tag = Constants.LOG_TAG;
    private String mTournamentKey;
    private int mRoundNumber;
    private int mTableNumber;
    private String mWhitePlayer = "";
    private String mBlackPlayer = "";
    private boolean mNoPartner = true;
    private int mCurrentResult;
    private boolean mPreventUpdateResult = true;
    private Context mContext;

    private static Bundle getBundle(String tournamentKey, int roundNumber, Game game) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOURNAMENT_KEY, tournamentKey);
        bundle.putInt(Constants.ROUND_NUMBER, roundNumber);
        bundle.putInt(Constants.TABLE_NUMBER, game.getTableNumber());
        bundle.putString(Constants.WHITE_PLAYER_NAME, game.getWhitePlayer().getName());
        if (game.getBlackPlayer() != null) {
            bundle.putString(Constants.BLACK_PLAYER_NAME, game.getBlackPlayer().getName());
        } else {
            bundle.putString(Constants.BLACK_PLAYER_NAME, "");
        }
        boolean noPartner = false;
        if (game.getResult() == 4) {
            noPartner = true;
        }
        bundle.putBoolean(Constants.NO_PARTNER, noPartner);
        bundle.putInt(Constants.CURRENT_RESULT, game.getResult());
        return bundle;
    }
    protected void disablePreventUpdateResults(){
        mPreventUpdateResult = false;
    }

    private void setParameters() {
        mTournamentKey = getArguments().getString(Constants.TOURNAMENT_KEY);
        mRoundNumber = getArguments().getInt(Constants.ROUND_NUMBER);
        mTableNumber = getArguments().getInt(Constants.TABLE_NUMBER);
        mWhitePlayer = getArguments().getString(Constants.WHITE_PLAYER_NAME);
        mBlackPlayer = getArguments().getString(Constants.BLACK_PLAYER_NAME);
        mNoPartner = getArguments().getBoolean(Constants.NO_PARTNER);
        mCurrentResult = getArguments().getInt(Constants.CURRENT_RESULT);
    }

    public static GameSetResultDialog newInstance(String tournamentKey, int roundNumber, Game game) {
        GameSetResultDialog dialog = new GameSetResultDialog();
        dialog.setArguments(getBundle(tournamentKey, roundNumber, game));
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        setParameters();
        if (mNoPartner) {
            dismiss();
        } else if (mCurrentResult != 0 && mPreventUpdateResult) {
            //different then not decided
            Toast.makeText(getContext(), "Use long press if you would like to change the current result",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set result for table " + mTableNumber);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        final CharSequence[] items = {mWhitePlayer, mBlackPlayer, "1/2 = 1/2"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = which + 1;
                (new PersistResult()).execute(new Integer(result));
            }
        });
        return builder.create();
    }

    private class PersistResult extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            int result = params[0];
            String resultLoc = Constants.LOCATION_GAME_RESULT
                    .replace(Constants.TOURNAMENT_KEY, mTournamentKey)
                    .replace(Constants.ROUND_NUMBER, String.valueOf(mRoundNumber))
                    .replace(Constants.TABLE_NUMBER, String.valueOf(mTableNumber));
            DatabaseReference resultRef = FirebaseDatabase.getInstance().getReference(resultLoc);
            resultRef.setValue(result);

            //notify the backend
            String gameLoc = Constants.LOCATION_GAME
                    .replace(Constants.TOURNAMENT_KEY,mTournamentKey)
                    .replace(Constants.ROUND_NUMBER,String.valueOf(mRoundNumber))
                    .replace(Constants.TABLE_NUMBER,String.valueOf(mTableNumber));

            MyCloudService.startActionGameResultUpdated(mContext,gameLoc);
            return null;
        }

        @Override
        protected void onPreExecute() {
            //all that is left to do is dismiss the dialog
            dismiss();
        }
    }


}
