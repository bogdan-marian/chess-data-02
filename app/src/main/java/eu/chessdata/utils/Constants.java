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
    public static final String CLUB_NAME = "clubName";
    public static final String CLUB_MANAGERS = "clubManagers";
    public static final String MANAGER_KEY = "$managerKey";
    public static final String USER_SETTINGS = "userSettings";
    public static final String MY_CLUBS = "myClubs";

    public static final String DEFAULT_CLUB = "defaultClub";
    public static final String TOURNAMENTS = "tournaments";
    public static final String TOURNAMENT_KEY = "$tournamentKey";

    public static final String CLUB_PLAYERS = "clubPlayers";
    public static final String PLAYER_KEY = "$playerKey";

    public static final String TOURNAMENT_PLAYERS = "tournamentPlayers";

    //clubManagers/$clubKey/$managerKey
    public static final String LOCATION_CLUB_MANAGERS = CLUB_MANAGERS + "/" + CLUB_KEY + "/" + MANAGER_KEY;

    //userSettings/$userKey/myClubs
    public static final String LOCATION_MY_CLUBS = USER_SETTINGS + "/" + USER_KEY + "/" + MY_CLUBS;
    public static final String LOCATION_MY_CLUB = LOCATION_MY_CLUBS + "/" + CLUB_KEY;
    public static final String LOCATION_DEFAULT_CLUB = USER_SETTINGS + "/" + USER_KEY + "/" + DEFAULT_CLUB;

    //tournaments/$clubKey/$tournamentKey
    public static final String LOCATION_TOURNAMENTS = TOURNAMENTS + "/" + CLUB_KEY;

    //clubPlayers/$clubKey/playerKey
    public static final String LOCATION_CLUB_PLAYERS = CLUB_PLAYERS + "/" + CLUB_KEY;

    //tournamentPlayers/$tournamentKey/
    public static final String LOCATION_TOURNAMENT_PLAYERS = TOURNAMENT_PLAYERS + "/" + TOURNAMENT_KEY;

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
