package eu.chessdata.ui.tournament;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Tournament;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFirebaseUtils;

/**
 * Created by Bogdan Oloeriu on 6/2/2016.
 */
public class TournamentCreateDialogFragment extends DialogFragment {

    private final String tag = Constants.LOG_TAG;
    private String mClubKey;

    private View mView;

    public static TournamentCreateDialogFragment newInstance(String clubKey) {

        TournamentCreateDialogFragment fragment = new TournamentCreateDialogFragment();
        fragment.mClubKey = clubKey;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.fragment_tournament_create_dialog, null, false);
        builder.setView(mView);
        NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.tournamentTotalRounds);
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
                persistTournament();
            }
        });

        return builder.create();
    }

    private void persistTournament() {
        Tournament tournament = new Tournament(
                ((EditText) mView.findViewById(R.id.tournamentName)).getText().toString(),
                ((EditText) mView.findViewById(R.id.tournamentDescription)).getText().toString(),
                ((EditText) mView.findViewById(R.id.tournamentLocation)).getText().toString(),
                ((NumberPicker) mView.findViewById(R.id.tournamentTotalRounds)).getValue()
        );

        String tournamentLocation = Constants.LOCATION_TOURNAMENTS
                .replace(Constants.CLUB_KEY, mClubKey);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference tournaments = database.getReference(tournamentLocation);
        DatabaseReference tournamentRef = tournaments.push();

        tournamentRef.setValue(tournament);

        //update reversed order
        String tournamentKey = tournamentRef.getKey();
        MyFirebaseUtils.updateTournamentReversedOrder(mClubKey,tournamentKey);
    }
}
