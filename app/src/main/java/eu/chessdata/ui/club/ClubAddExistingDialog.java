package eu.chessdata.ui.club;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.chessdata.R;
import eu.chessdata.model.Club;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MapUtil;

/**
 * Created by Bogdan Oloeriu on 6/26/2016.
 */
public class ClubAddExistingDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;
    private ArrayList<Club> mClubs;
    private Map<String, Club> mClubsMap;

    private View mView;
    private ClubAdapter mClubAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a club to join");
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.dialog_club_add_existing,null,false);
        builder.setView(mView);

        ListView listView = (ListView) mView.findViewById(R.id.list_view_clubs);

        mClubsMap = new HashMap<>();
        mClubs = new ArrayList<>();

        mClubAdapter = new ClubAdapter(getContext(),mClubs);
        listView.setAdapter(mClubAdapter);
        (new UpdateListTask()).execute();

        return builder.create();
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String clubsLoc = Constants.CLUBS;
            DatabaseReference clubsRef = FirebaseDatabase.getInstance().getReference(clubsLoc);
            clubsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item: dataSnapshot.getChildren()){
                        Club club = item.getValue(Club.class);
                        mClubsMap.put(club.valMapKey(),club);
                    }
                    mClubsMap = MapUtil.sortByValue(mClubsMap);
                    for (Map.Entry<String,Club> entry: mClubsMap.entrySet()){
                        mClubAdapter.add(entry.getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(tag,"Database error: " + databaseError.getMessage());
                }
            });
            return null;
        }
    }
}
