package eu.chessdata.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.chessdata.model.DefaultClub;
import eu.chessdata.model.User;

/**
 * Created by Bogdan Oloeriu on 5/31/2016.
 */
public class MyFirebaseUtils {
    private static final String tag = Constants.LOG_TAG;

    public interface OnOneTimeResultsListener{
        public void onDefaultClubValue(DefaultClub defaultClub);
        public void onUserIsClubManager(DefaultClub defaultClub);
    }

    public static void setDefaultManagedClub(DefaultClub defaultManagedClub) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultManagedClubLocation = Constants.LOCATION_DEFAULT_MANAGED_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference managedClubRef = database.getReference(defaultManagedClubLocation);
        managedClubRef.setValue(defaultManagedClub);
    }

    public static void setDefaultClub(DefaultClub defaultManagedClub) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference managedClubRef = database.getReference(defaultClubLocation);
        managedClubRef.setValue(defaultManagedClub);
    }

    public static void getDefaultClub(final OnOneTimeResultsListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                Log.d(tag, "default Club found");
                listener.onDefaultClubValue(defaultClub);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void isManagerForDefaultClub(final OnOneTimeResultsListener listener){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                if (defaultClub != null){
                    String clubKey = defaultClub.getClubKey();
                    String managersLocation = Constants.LOCATION_CLUB_MANAGERS
                            .replace(Constants.CLUB_KEY, clubKey)
                            .replace(Constants.MANAGER_KEY, uid);
                    DatabaseReference managerRef = database.getReference(managersLocation);
                    managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User manager = dataSnapshot.getValue(User.class);
                            if (manager != null){
                                listener.onUserIsClubManager(defaultClub);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
