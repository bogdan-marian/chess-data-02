package eu.chessdata.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import eu.chessdata.chesspairing.algoritms.fideswissduch.Algorithm;
import eu.chessdata.chesspairing.algoritms.javafo.JavafoWrapp;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingRound;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.model.MyPayLoad;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class MyCloudService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GAME_RESULT_UPDATED = "eu.chessdata.utils.ACTION_GAME_RESULT_UPDATED";
    private static final String ACTION_COMPUTE_STANDINGS = "eu.chessdata.util.ACTION_COMPUTE_STANDINGS";
    private static final String ACTION_UPDATE_TOURNAMENT_INITIAL_ORDER = "eu.chessdata.util.ACTION_UPDATE_TOURNAMENT_INITIAL_ORDER";
    private static final String ACTION_REFRESH_TOURNAMENT_INITIAL_ORDER = "eu.chessdata.util.ACTION_REFRESH_TOURNAMENT_INITIAL_ORDER";
    private static final String ACTION_GENERATE_NEXT_ROUND = "eu.chessdata.utils.ACTION_GENERATE_NEXT_ROUND";
    private static final String EXTRA_USER_KEY = "eu.chessdata.utils.EXTRA_USER_KEY";
    private static final String EXTRA_GAME_LOCATION = "eu.chessdata.utils.EXTRA_GAME_LOCATION";
    private static final String EXTRA_TOURNAMENT_KEY = "eu.chessdata.utils.EXTRA_TOURNAMENT_KEY";
    private static final String EXTRA_PLAYER_KEY = "eu.chessdata.utils.EXTRA_PLAYER_KEY";
    private static final String EXTRA_UPDATED_ORDER = "eu.chessdata.utils.EXTRA_UPDATED_ORDER";
    private static final String EXTRA_ROUND_NUMBER = "eu.chessdata.utils.EXTRA_ROUND_NUMBER";
    private static final String EXTRA_CLUB_KEY = "eu.chessdata.utils.EXTRA_CLUB_KEY";
    private static String tag = Constants.LOG_TAG;

    public MyCloudService() {
        super("MyCloudService");
    }


    public static void startActionGameResultUpdated(Context context, String gameLocation) {

        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_GAME_RESULT_UPDATED);
        intent.putExtra(EXTRA_GAME_LOCATION, gameLocation);
        context.startService(intent);
    }

    public static void startActionComputeStandings(Context context, String clubKey, String tournamentKey, int roundNumber) {
        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_COMPUTE_STANDINGS);
        intent.putExtra(EXTRA_CLUB_KEY, clubKey);
        intent.putExtra(EXTRA_TOURNAMENT_KEY, tournamentKey);
        intent.putExtra(EXTRA_ROUND_NUMBER, roundNumber);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGenerateNextRound(Context context, String clubKey, String tournamentKey) {
        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_GENERATE_NEXT_ROUND);
        intent.putExtra(EXTRA_CLUB_KEY, clubKey);
        intent.putExtra(EXTRA_TOURNAMENT_KEY, tournamentKey);
        context.startService(intent);
    }


    public static void startActionUpdateTournamentInitialOrder(Context context,
                                                               String clubKey,
                                                               String userKey,
                                                               String tournamentKey,
                                                               String playerKey,
                                                               String updatedOrder) {
        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_UPDATE_TOURNAMENT_INITIAL_ORDER);
        intent.putExtra(EXTRA_CLUB_KEY, clubKey);
        intent.putExtra(EXTRA_TOURNAMENT_KEY, tournamentKey);
        intent.putExtra(EXTRA_PLAYER_KEY, playerKey);
        intent.putExtra(EXTRA_UPDATED_ORDER, updatedOrder);
        context.startService(intent);
    }

    public static void startActionRefreshTournamentInitialOrder(Context context,
                                                                String clubKey,
                                                                String userKey,
                                                                String tournamentKey) {
        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_REFRESH_TOURNAMENT_INITIAL_ORDER);
        intent.putExtra(EXTRA_CLUB_KEY, clubKey);
        intent.putExtra(EXTRA_TOURNAMENT_KEY, tournamentKey);
        intent.putExtra(EXTRA_USER_KEY, userKey);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GAME_RESULT_UPDATED.equals(action)) {
                final String gameLocation = intent.getStringExtra(EXTRA_GAME_LOCATION);
                handleActionGameResultUpdated(gameLocation);
            } else if (ACTION_GENERATE_NEXT_ROUND.equals(action)) {
                final String clubKey = intent.getStringExtra(EXTRA_CLUB_KEY);
                final String tournamentKey = intent.getStringExtra(EXTRA_TOURNAMENT_KEY);
                handleActionGenerateNextRound(clubKey, tournamentKey);
            } else if (ACTION_COMPUTE_STANDINGS.equals(action)) {
                final String clubKey = intent.getStringExtra(EXTRA_CLUB_KEY);
                final String tournamentKey = intent.getStringExtra(EXTRA_TOURNAMENT_KEY);
                final int roundNumber = intent.getIntExtra(EXTRA_ROUND_NUMBER, 1);
                handleActionComputeStandings(clubKey, tournamentKey, roundNumber);
            } else if (ACTION_UPDATE_TOURNAMENT_INITIAL_ORDER.equals(action)) {
                final String clubKey = intent.getStringExtra(EXTRA_CLUB_KEY);
                final String tournamentKey = intent.getStringExtra(EXTRA_TOURNAMENT_KEY);
                final String playerKey = intent.getStringExtra(EXTRA_PLAYER_KEY);
                final String updateOrder = intent.getStringExtra(EXTRA_UPDATED_ORDER);
                handleActionUpdateTournamentInitialOrder(clubKey, tournamentKey, playerKey, updateOrder);
            } else if (ACTION_REFRESH_TOURNAMENT_INITIAL_ORDER.equals(action)) {
                final String clubKey = intent.getStringExtra(EXTRA_CLUB_KEY);
                final String tournamentKey = intent.getStringExtra(EXTRA_TOURNAMENT_KEY);
                final String userKey = intent.getStringExtra(EXTRA_USER_KEY);
                handleActionRefreshTournamentInitialOrder(clubKey, tournamentKey, userKey);
            }
        }
    }

    private void handleActionComputeStandings(String clubKey, String tournamentKey, int roundNumber) {
        Log.d(tag, "Compute standings initiated:club= " + clubKey + " tournament=" + tournamentKey + " round=" + roundNumber);

        if (!MyFirebaseUtils.isCurrentUserAdmin(clubKey)) {
            return;
        }

        ChesspairingTournament tournament = MyFirebaseUtils.buildChessPairingTournament(clubKey, tournamentKey);
        for (ChesspairingRound round : tournament.getRounds()) {
            //todo implement compute standings
            int rNumber = round.getRoundNumber();
            if (!round.allGamesHaveBeanPlayed()) {
                //if current round still hesse games to play then impossible to compute standings
                break;
            }
            List<ChesspairingPlayer> standings = tournament.computeStandings(rNumber);

            MyFirebaseUtils.persistDefaultStandings(tournamentKey, rNumber, standings);
        }
    }

    private void handleActionUpdateTournamentInitialOrder(String clubKey,
                                                          String tournamentKey,
                                                          String playerKey,
                                                          String updatedOrderString) {

        ChesspairingTournament tournament = MyFirebaseUtils.buildChessPairingTournament(clubKey, tournamentKey);
        Log.d(tag, "handleActionUpdateTournamentInitialOrder newOrder = " + playerKey + "new order = " + updatedOrderString);
        MyFirebaseUtils.updateTournamentInitialOrder(clubKey,
                tournamentKey, playerKey, updatedOrderString, tournament);
    }

    private void handleActionRefreshTournamentInitialOrder(String clubKey,
                                                           String tournamentKey,
                                                           String userKey) {
        ChesspairingTournament tournament = MyFirebaseUtils.buildChessPairingTournament(clubKey,
                tournamentKey);
        MyFirebaseUtils.refreshTournamentInitialOrder(clubKey, tournamentKey, userKey, tournament);

    }

    /**
     * Notifies the backend that a specific game has bean updated
     */
    private void handleActionGameResultUpdated(String gameLocation) {
        String url = "https://chess-data.appspot.com/api/BasicApi";
        try {
            URL object = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) object.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");

            //create gson
            MyPayLoad myPayLoad = new MyPayLoad();
            myPayLoad.setEvent(MyPayLoad.Event.GAME_RESULT_UPDATED);
            myPayLoad.setGameLocation(gameLocation);
            Gson gson = new Gson();
            String jsonMyPayLoad = gson.toJson(myPayLoad);


            //send the data
            Log.e(tag, "Sending the data " + jsonMyPayLoad);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(jsonMyPayLoad);
            outputStreamWriter.flush();

            //show the response
            StringBuilder sb = new StringBuilder();
            int httpResult = connection.getResponseCode();
            if (httpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            }
            Log.d(tag, "Response from server: " + sb.toString());

        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGenerateNextRound(String clubKey, String tournamentKey) {
        if (!MyFirebaseUtils.isCurrentUserAdmin(clubKey)) {
            Log.d(tag, "User is not an admin ");
            return;
        }
        ChesspairingTournament tournament = MyFirebaseUtils.buildChessPairingTournament(clubKey, tournamentKey);
        Algorithm algorithm = new JavafoWrapp();

        //<debug> collect tournament state
        Gson gson = new Gson();
        String tournamentJson = gson.toJson(tournament);

        //<end debug>
        Log.d(tag, "new_game = " + tournamentJson);
        tournament = algorithm.generateNextRound(tournament);
        List<ChesspairingRound> rounds = tournament.getRounds();
        ChesspairingRound round = rounds.get(rounds.size() - 1);
        MyFirebaseUtils.persistNewGames(clubKey, tournamentKey, round);
        Log.d(tag, "persistNewGames has bean initiated");
    }
}
