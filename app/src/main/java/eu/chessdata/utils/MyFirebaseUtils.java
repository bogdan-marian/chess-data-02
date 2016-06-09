package eu.chessdata.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.chessdata.model.DefaultClub;
import eu.chessdata.model.Tournament;
import eu.chessdata.model.User;
import eu.chessdata.ui.MainActivity;

/**
 * Created by Bogdan Oloeriu on 5/31/2016.
 */
public class MyFirebaseUtils {
    private static final String tag = Constants.LOG_TAG;

    public interface OnOneTimeResultsListener {
        public void onDefaultClubValue(DefaultClub defaultClub, MainActivity.ACTION action);

        public void onDefaultClubValue(DefaultClub defaultClub);

        public void onUserIsClubManager(DefaultClub defaultClub, MainActivity.ACTION action);
    }

    public static void setDefaultClub(DefaultClub defaultManagedClub) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference managedClubRef = database.getReference(defaultClubLocation);
        managedClubRef.setValue(defaultManagedClub);
    }


    public static void getDefaultClub(final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
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
                listener.onDefaultClubValue(defaultClub, action);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Identifies if current user is manager for a specific club by clubKey and then notifies the
     * registered listener only for positive results.
     * @param listener
     * @param action
     */
    public static void isManagerForClubKey(final String clubKey, final OnOneTimeResultsListener listener, final MainActivity.ACTION action){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final  String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String managerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY,clubKey)
                .replace(Constants.MANAGER_KEY,uid);
        DatabaseReference managerRef = database.getReference(managerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User manager = dataSnapshot.getValue(User.class);
                if (manager != null){
                    DefaultClub defaultClub = new DefaultClub();
                    defaultClub.setClubKey(clubKey);
                    listener.onUserIsClubManager(defaultClub,action);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag,"databaseError: " + databaseError.getMessage());
            }
        });
    }

    public static void isManagerForDefaultClub(final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                if (defaultClub != null) {
                    String clubKey = defaultClub.getClubKey();
                    String managersLocation = Constants.LOCATION_CLUB_MANAGERS
                            .replace(Constants.CLUB_KEY, clubKey)
                            .replace(Constants.MANAGER_KEY, uid);
                    DatabaseReference managerRef = database.getReference(managersLocation);
                    managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User manager = dataSnapshot.getValue(User.class);
                            if (manager != null) {
                                listener.onUserIsClubManager(defaultClub,action);
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

    public static void updateTournamentReversedOrder(String clubKey, String tournamentKey) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String tournamentLocation = Constants.LOCATION_TOURNAMENTS
                .replace(Constants.CLUB_KEY, clubKey) + "/" + tournamentKey;

        final DatabaseReference tournamentRef = database.getReference(tournamentLocation);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tournament tournament = dataSnapshot.getValue(Tournament.class);
                if (tournament != null) {
                    long timeStamp = tournament.dateCreatedGetLong();
                    long reversedDateCreated = 0 - timeStamp;
                    tournamentRef.child("reversedDateCreated").setValue(reversedDateCreated);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
