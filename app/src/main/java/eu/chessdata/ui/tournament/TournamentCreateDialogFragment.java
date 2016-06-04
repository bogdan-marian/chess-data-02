package eu.chessdata.ui.tournament;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 6/2/2016.
 */
public class TournamentCreateDialogFragment extends DialogFragment{

    private final String tag = Constants.LOG_TAG;
    private String tournamentLocation;

    private View mView;

    public static TournamentCreateDialogFragment newInstance(String clubKey){
        TournamentCreateDialogFragment fragment = new TournamentCreateDialogFragment();
        fragment.tournamentLocation = Constants.LOCATION_TOURNAMENT
                .replace(Constants.CLUB_KEY,clubKey);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Log.d(tag,"tournamentLocation: " + tournamentLocation);
        mView = inflater.inflate(R.layout.fragment_tournament_create_dialog,null,false);
        builder.setView(mView);
        NumberPicker numberPicker = (NumberPicker)mView.findViewById(R.id.tournamentTotalRounds);
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(14);
        numberPicker.setValue(7);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setPositiveButton(R.string.create_tournament, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(tag, "Time to create tournament");
            }
        });

        return builder.create();
    }
}
