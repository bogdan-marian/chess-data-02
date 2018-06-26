package eu.chessdata.ui.crowd;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import eu.chessdata.R;

public class CrowdTournamentCreateDialogFragment extends DialogFragment {
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
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

        return builder.create();
    }
}
