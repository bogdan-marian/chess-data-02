package eu.chessdata.utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class MyCloudService extends IntentService {
    private static String tag = Constants.LOG_TAG;

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GAME_RESULT_UPDATED = "eu.chessdata.utils.ACTION_GAME_RESULT_UPDATED";
    private static final String ACTION_BAZ = "eu.chessdata.utils.action.BAZ";

    private static final String EXTRA_GAME_LOCATION = "eu.chessdata.utils.EXTRA_GAME_LOCATION";
    private static final String EXTRA_PARAM2 = "eu.chessdata.utils.extra.PARAM2";

    public MyCloudService() {
        super("MyCloudService");
    }


    public static void startActionGameResultUpdated(Context context, String gameLocation) {

        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_GAME_RESULT_UPDATED);
        intent.putExtra(EXTRA_GAME_LOCATION, gameLocation);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyCloudService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_GAME_LOCATION, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GAME_RESULT_UPDATED.equals(action)) {
                final String gameLocation = intent.getStringExtra(EXTRA_GAME_LOCATION);
                handleActionGameResultUpdated(gameLocation);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_GAME_LOCATION);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Notifies the backend that a specific game has bean updated
     */
    private void handleActionGameResultUpdated(String gameLocation) {
        Log.d(tag,"please implement handleActionGameResultUpdated, gameLocation = " + gameLocation);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
