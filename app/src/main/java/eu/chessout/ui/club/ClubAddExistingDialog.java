package eu.chessout.ui.club;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.chessout.R;
import eu.chessout.model.Club;
import eu.chessout.model.DefaultClub;
import eu.chessout.utils.Constants;
import eu.chessout.utils.MapUtil;
import eu.chessout.utils.MyFirebaseUtils;

/**
 * Created by Bogdan Oloeriu on 6/26/2016.
 */
public class ClubAddExistingDialog extends DialogFragment{
    private String tag = Constants.LOG_TAG;
    private ArrayList<Club> mClubs;
    private Map<String, Club> mClubsMap;
    private Map<String, String> mClubKeys;

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
        mClubKeys = new HashMap<>();

        mClubAdapter = new ClubAdapter(getContext(),mClubs);
        listView.setAdapter(mClubAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String,Club>>list = new LinkedList<Map.Entry<String, Club>>(mClubsMap.entrySet());
                Club club = list.get(position).getValue();
                addClub(club);
            }
        });
        (new UpdateListTask()).execute();

        return builder.create();
    }

    private void addClub(Club club) {
        String clubKey = mClubKeys.get(club.valMapKey());
        if (clubKey!= null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String newClubLoc = Constants.LOCATION_MY_CLUB
                    .replace(Constants.CLUB_KEY,clubKey)
                    .replace(Constants.USER_KEY, uid);
            DatabaseReference newClubRef = FirebaseDatabase.getInstance().getReference(newClubLoc);
            newClubRef.setValue(club);

            //settings set defaultClub key;
            DefaultClub defaultClub = new DefaultClub(clubKey,club.getShortName());
            MyFirebaseUtils.setDefaultClub(defaultClub);
            dismiss();
        }
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
                        mClubKeys.put(club.valMapKey(),item.getKey());
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
