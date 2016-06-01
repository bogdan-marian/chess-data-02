package eu.chessdata.utils;

/**
 * Created by Bogdan Oloeriu on 5/24/2016.
 */
public class Constants {


    /**
     * LOCATIONS
     */
    public static final String USERS = "users";
    public static final String USER_KEY = "$userKey";
    public static final String CLUBS = "clubs";
    public static final String CLUB_KEY = "$clubKey";
    public static final String CLUB_MANAGERS = "clubManagers";
    public static final String MANAGER_KEY = "$managerKey";
    public static final String USER_SETTINGS = "userSettings";
    public static final String MY_CLUBS = "myClubs";
    public static final String DEFAULT_MANAGED_CLUB = "defaultManagedClub";
    public static final String DEFAULT_CLUB = "defaultClub";

    //clubManagers/$clubId/$managerId
    public static final String LOCATION_CLUB_MANAGERS = CLUB_MANAGERS + "/" + CLUB_KEY + "/" + MANAGER_KEY;
    //userSettings/$userId/myClubs
    public static final String LOCATION_MY_CLUBS = USER_SETTINGS + "/" + USER_KEY + "/" + MY_CLUBS;
    public static final String LOCATION_MY_CLUB = LOCATION_MY_CLUBS + "/" + CLUB_KEY;
    public static final String LOCATION_DEFAULT_MANAGED_CLUB = USER_SETTINGS + "/" + USER_KEY + "/" + DEFAULT_MANAGED_CLUB;
    public static final String LOCATION_DEFAULT_CLUB = USER_SETTINGS + "/" + USER_KEY + "/" + DEFAULT_CLUB;


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
