package eu.chessdata.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.model.DefaultClub;
import eu.chessdata.model.Tournament;
import eu.chessdata.model.User;
import eu.chessdata.ui.MainActivity;

/**
 * Created by Bogdan Oloeriu on 5/31/2016.
 */
public class MyFirebaseUtils {
    private static final String tag = Constants.LOG_TAG;

    /**
     * it collects the entire required data from firebase in order to build the current state
     * of the tournament
     *
     * @param clubKey
     * @param tournamentKey
     * @return
     */
    public static ChesspairingTournament buildChessPairingTournament(String clubKey, String tournamentKey) {
        final ChesspairingTournament chesspairingTournament = new ChesspairingTournament();
        final CountDownLatch latch1 = new CountDownLatch(1);
        //get the general description
        String tournamentLoc = Constants.LOCATION_TOURNAMENT
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.TOURNAMENT_KEY,tournamentKey);
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference(tournamentLoc);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tournament tournament = dataSnapshot.getValue(Tournament.class);
                if (tournament!=null){
                    chesspairingTournament.setName(tournament.getName());
                    chesspairingTournament.setDescription(tournament.getDescription());
                    chesspairingTournament.setTotalRounds(tournament.getTotalRounds());
                    chesspairingTournament.setTotalRounds(tournament.getTotalRounds());
                }
                latch1.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag,databaseError.getMessage());
                latch1.countDown();
            }
        });
        try {
            latch1.await();
        } catch (InterruptedException e) {
            Log.e(tag, "tournamentDetailsError: " + e.getMessage());
        }
        //populate players


        throw new IllegalStateException("Please finish this");
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
                listener.onClubValue(defaultClub, action);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Identifies if current user is manager for a specific club by clubKey and then notifies the
     * registered listener only for positive results.
     *
     * @param listener
     * @param action
     */
    public static void isManagerForClubKey(final String clubKey, final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String managerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        DatabaseReference managerRef = database.getReference(managerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User manager = dataSnapshot.getValue(User.class);
                if (manager != null) {
                    DefaultClub defaultClub = new DefaultClub();
                    defaultClub.setClubKey(clubKey);
                    Map<String, String> values = new HashMap<String, String>();
                    values.put(Constants.CLUB_KEY, clubKey);
                    listener.onUserIsClubManager(values, action);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "databaseError: " + databaseError.getMessage());
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
                                String clubKey = defaultClub.getClubKey();
                                Map<String, String> values = new HashMap<String, String>();
                                values.put(Constants.CLUB_KEY, clubKey);
                                listener.onUserIsClubManager(values, action);
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

    public static void isCurrentUserAdmin(String clubKey, final OnUserIsAdmin onUserIsAdmin) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clubManagerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference(clubManagerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean manager = false;
                if (dataSnapshot.getValue() != null) {
                    manager = true;
                }
                onUserIsAdmin.onUserIsAdmin(manager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                onUserIsAdmin.onUserIsAdmin(false);
            }
        });
    }

    /**
     * It checks using the clubKey if the user is a manager
     *
     * @param clubKey
     * @return
     */
    public static boolean isCurrentUserAdmin(String clubKey) {
        Log.d(tag, "Tome to implementis_CurrentUser Admin! return boolean");
        final Boolean managers[] = {false};//first boolean holds the result
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clubManagerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference(clubManagerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    managers[0] = true;
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "isCurrentUserAdmin " + e.getMessage());
        }
        return managers[0];
    }


    public interface OnOneTimeResultsListener {
        public void onClubValue(DefaultClub defaultClub, MainActivity.ACTION action);

        public void onClubValue(DefaultClub defaultClub);

        public void onUserIsClubManager(Map<String, String> dataMap, MainActivity.ACTION action);
    }

    public interface OnUserIsAdmin {
        public void onUserIsAdmin(boolean isAdmin);
    }
}
