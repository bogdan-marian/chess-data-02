package eu.chessdata.utils;

/**
 * Created by Bogdan Oloeriu on 5/24/2016.
 */
public class Constants {


    /**
     * LOCATIONS
     */
    public static final String USERS = "users";
    public static final String USER_ID = "$userId";
    public static final String CLUBS = "clubs";
    public static final String CLUB_ID = "$clubId";
    public static final String CLUB_MANAGERS = "clubManagers";
    public static final String MANAGER_ID = "$managerId";
    public static final String USER_SETTINGS = "userSettings";
    public static final String MANAGED_CLUBS = "managedClubs";

    //clubManagers/$clubId/$managerId
    public static final String LOCATION_CLUB_MANAGERS = CLUB_MANAGERS+"/"+CLUB_ID+"/"+MANAGER_ID;
    //userSettings/$userId/managedClubs
    public static final String LOCATION_MANAGED_CLUBS = USER_SETTINGS+"/"+USER_ID+"/"+MANAGED_CLUBS+"/"+CLUB_ID;
    public static final String LOCATION_MY_CLUBS = USER_SETTINGS+"/"+USER_ID+"/"+MANAGED_CLUBS;

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = "https://chess-data.firebaseio.com/";

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    /**
     * Some other constants
     */
    public static final String LOG_TAG = "my-debug";
}
