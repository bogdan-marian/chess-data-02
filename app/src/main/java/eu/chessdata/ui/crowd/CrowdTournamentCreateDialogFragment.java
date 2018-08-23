package eu.chessdata.ui.crowd;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.CrowdTournament;
import eu.chessdata.utils.Constants;

public class CrowdTournamentCreateDialogFragment extends DialogFragment {
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.fragment_crowd_tournament_create_dialog, null, false);
        builder.setView(mView);
        NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.tournamentTotalRounds);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(14);
        numberPicker.setValue(0);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton("Create crowd tournament", myClickListener());

        return builder.create();
    }


    private DialogInterface.OnClickListener myClickListener() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb = new StringBuilder();

                String tournamentName = ((EditText) mView.findViewById(R.id.tournamentName)).getText().toString();
                if (tournamentName == null || tournamentName.isEmpty()) {
                    sb.append("Tournament name is empty\n");
                }

                String tournamentDescription = ((EditText) mView.findViewById(R.id.tournamentDescription)).getText().toString();
                if (tournamentDescription == null || tournamentDescription.isEmpty()){
                    sb.append("Tournament description is empty\n");
                }

                if (!sb.toString().isEmpty()) {
                    sb.append(" Please try again");
                    Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
                } else {
                    persistTournament();
                }
            }
        };
        return clickListener;
    }

    private void persistTournament() {
        String name = ((EditText) mView.findViewById(R.id.tournamentName)).getText().toString();
        String description = ((EditText) mView.findViewById(R.id.tournamentDescription)).getText().toString();
        String location = ((EditText)mView.findViewById(R.id.tournamentLocation)).getText().toString();
        int totalRownds = ((NumberPicker) mView.findViewById(R.id.tournamentTotalRounds)).getValue();
        CrowdTournament tournament = new CrowdTournament(name,description,location,totalRownds);

        String croudTournamentLocation = Constants.LOCATION_CROWD_TOURNAMENTS;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tournaments = database.getReference(croudTournamentLocation);
        DatabaseReference tournamentRef = tournaments.push();
        tournamentRef.setValue(tournament);

        //persist user reference
        String tournamentKey = tournamentRef.getKey();
        String whoFollowsLoc = Constants.LOCATION_CROWD_WHO_FOLLOWS_TOURNAMENT
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference whoFollowsRef = database.getReference(whoFollowsLoc);
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        whoFollowsRef.push().setValue(userKey);

        //persist tournament reference
        String locCrowdUserTournament = Constants.LOCATION_USER_SETTINGS_CROWD_TOURNAMENTS
                .replace(Constants.USER_KEY, userKey)
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference userTournament = database.getReference(locCrowdUserTournament);
        userTournament.setValue(tournament);
    }
}
