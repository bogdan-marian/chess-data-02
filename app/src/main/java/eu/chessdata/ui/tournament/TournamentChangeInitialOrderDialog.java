package eu.chessdata.ui.tournament;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.NumberPicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.chessdata.model.Player;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyCloudService;

public class TournamentChangeInitialOrderDialog extends DialogFragment {
    private String tag = Constants.LOG_TAG;
    private Player mPlayerToSetOrder;
    private String mTournamentKey;
    private String mClubKey;
    private String mUserKey;
    private List<Player> mPlayerList;
    private NumberPicker mNumberPicker;
    private Context mContext;
    private int mNewOrderValue = 1;

    public static TournamentChangeInitialOrderDialog newInstance(Player playerToChangeOrder,
                                                                 String tournamentKey,
                                                                 String clubKey,
                                                                 String userKey) {
        TournamentChangeInitialOrderDialog vipDialog = new TournamentChangeInitialOrderDialog();
        vipDialog.mPlayerToSetOrder = playerToChangeOrder;
        vipDialog.mTournamentKey = tournamentKey;
        vipDialog.mPlayerList = new ArrayList<>();
        vipDialog.mClubKey = clubKey;
        vipDialog.mUserKey = userKey;

        return vipDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        mNumberPicker = new NumberPicker(getActivity());
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(2);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mNewOrderValue = newValue;
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mNumberPicker);
        builder.setTitle("Would you like to update the initial order for "
                + mPlayerToSetOrder.getName() + " ? ");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton("Update order)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateInitialOrder updateInitialOrder = new UpdateInitialOrder();
                updateInitialOrder.execute();
            }
        });


        (new BuildInitialList()).execute();

        return builder.create();
    }


    private class UpdateInitialOrder extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(tag, "Update initial order fired");
            MyCloudService.startActionUpdateTournamentInitialOrder(
                    mContext,
                    mClubKey,
                    mUserKey,
                    mTournamentKey,
                    mPlayerToSetOrder.getPlayerKey(),
                    String.valueOf(mNewOrderValue));
            return null;
        }
    }

    private class BuildInitialList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String playerLoc = Constants.LOCATION_TOURNAMENT_PLAYERS
                    .replace(Constants.TOURNAMENT_KEY, mTournamentKey);
            DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference(playerLoc);
            playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Player player = item.getValue(Player.class);
                        mPlayerList.add(player);
                        mNumberPicker.setMaxValue(mPlayerList.size());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(tag, "Database error: " + databaseError.getMessage());
                }
            });
            return null;
        }
    }
}
