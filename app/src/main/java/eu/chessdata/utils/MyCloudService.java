package eu.chessdata.utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import eu.chessdata.model.MyPayLoad;

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
        String url = "https://chess-data.appspot.com/api/BasicApi";
        try {
            URL object = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)object.openConnection();
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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(jsonMyPayLoad);
            outputStreamWriter.flush();

            //show the response
            StringBuilder sb = new StringBuilder();
            int httpResult = connection.getResponseCode();
            if (httpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine())!=null){
                    sb.append(line+"\n");
                }
                br.close();
            }
            Log.d(tag,"Response from server: " + sb.toString());

        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }
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
