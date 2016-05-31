package eu.chessdata.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.chessdata.model.DefaultManagedClub;

/**
 * Created by Bogdan Oloeriu on 5/31/2016.
 */
public class MyFirebaseUtils {
    public static void setDefaultManagedClub(DefaultManagedClub defaultManagedClub) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultManagedClubLocation = Constants.LOCATION_DEFAULT_MANAGED_CLUB
                .replace(Constants.USER_KEY,uid);
        DatabaseReference managedClubRef = database.getReference(defaultManagedClubLocation);
        managedClubRef.setValue(defaultManagedClub);
    }
}
