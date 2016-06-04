package eu.chessdata.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.R;
import eu.chessdata.model.Club;
import eu.chessdata.model.DefaultClub;
import eu.chessdata.model.User;
import eu.chessdata.utils.Constants;
import eu.chessdata.utils.MyFirebaseUtils;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class ClubCreateDialogFragment extends DialogFragment {
    private static final String LOG_TAG = Constants.LOG_TAG;
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.club_create_dialog, null);
        builder.setView(mView)
                .setPositiveButton("Create club", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        persistClub();
                    }
                });

        return builder.create();
    }

    private Club buildClub() {
        String name = ((EditText) mView.findViewById(R.id.clubName)).getText().toString();
        String shortName = ((EditText) mView.findViewById(R.id.shortName)).getText().toString();
        String email = ((EditText) mView.findViewById(R.id.email)).getText().toString();
        String country = ((EditText) mView.findViewById(R.id.country)).getText().toString();
        String city = ((EditText) mView.findViewById(R.id.city)).getText().toString();
        String homePage = ((EditText) mView.findViewById(R.id.homePage)).getText().toString();
        String description = ((EditText) mView.findViewById(R.id.clubDescription)).getText().toString();
        Club club = new Club(name, shortName, email, country, city, homePage, description);

        return club;
    }

    private void persistClub() {
        Club club = buildClub();

        FirebaseApp app = FirebaseApp.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String uid = firebaseUser.getUid();
        String displayName = firebaseUser.getDisplayName();

        //create the club
        final DatabaseReference clubs = database.getReference(Constants.CLUBS);
        DatabaseReference clubRef = clubs.push();
        clubRef.setValue(club);
        String clubId = clubRef.getKey();
        Log.d(LOG_TAG, "clubId = " + clubId);

        //set manager
        String managersLocation = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubId)
                .replace(Constants.MANAGER_KEY, uid);
        User clubManager = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        final DatabaseReference managersRef = database.getReference(managersLocation);
        managersRef.setValue(clubManager);

        //add to my_clubs club
        String managedClubsLocation = Constants.LOCATION_MY_CLUB
                .replace(Constants.USER_KEY, uid)
                .replace(Constants.CLUB_KEY, clubId);
        DatabaseReference myClubsRef = database.getReference(managedClubsLocation);
        myClubsRef.setValue(club);

        //settings set defaultClub key;
        DefaultClub defaultClub = new DefaultClub(clubId, club.getShortName());
        MyFirebaseUtils.setDefaultClub(defaultClub);
    }
}
