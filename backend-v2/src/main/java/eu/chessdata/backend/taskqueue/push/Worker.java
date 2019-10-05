package eu.chessdata.backend.taskqueue.push;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.Device;
import eu.chessdata.backend.model.Game;
import eu.chessdata.backend.model.MyPayLoad;
import eu.chessdata.backend.model.Player;
import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.Constants;
import eu.chessdata.backend.utils.MyAuthImplementation;
import eu.chessdata.backend.utils.MyGson;
import eu.chessdata.backend.utils.MySecurityValues;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
@RestController
public class Worker {
    private static final Logger errorLogger = Logger.getLogger(Worker.class.getName());
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    private List<String> deviceKeys;

    @Autowired
    private MyAuthImplementation myAuthService;



    //@Override
    @PostMapping("/worker")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        deviceKeys = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonPlayLoad = "";
        if (bufferedReader != null) {
            jsonPlayLoad = jsonPlayLoad + bufferedReader.readLine();
        }
        Gson gson = MyGson.getGson();
        MyPayLoad myPayLoad = gson.fromJson(jsonPlayLoad, MyPayLoad.class);

        if (myPayLoad.getEvent() == MyPayLoad.Event.GAME_RESULT_UPDATED) {
            restNotifyUsersGameResultUpdated(myPayLoad);
            //notifyUsersGameResultUpdated(myPayLoad);
        }
//
//        MyFirebase.makeSureEverythingIsInOrder();
//
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    User user = item.getValue(User.class);
//                }
//                latch.countDown();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                errorLogger.info("firebase error: " + databaseError.getMessage());
//                latch.countDown();
//            }
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void notifyUsersGameResultUpdated(MyPayLoad myPayLoad) {
        log.log(Level.INFO, "handling the Payload");
    }

    /**
     * starts the notification process for a specific game
     *
     * @param myPayLoad contains the standard location in the firebase for that game
     */
    private void restNotifyUsersGameResultUpdated(MyPayLoad myPayLoad) {
        if (myPayLoad.getEvent() != MyPayLoad.Event.GAME_RESULT_UPDATED) {
            return;
        }

        String gameUrl = myPayLoad.getGameLocation();
        if (gameUrl == null) {
            return;
        }

        String accessToken = myAuthService.getAccessToken();
        if (accessToken == null) {
            return;
        }
        gameUrl = Constants.FIREBASE_URL + gameUrl + ".json?access_token=" + accessToken;
        Game game = getGame(gameUrl);
        if (game.getWhitePlayer() != null) {
            Player whitePlayer = game.getWhitePlayer();
            restComputeDevicesAndNotify(whitePlayer, game);
        }
        if (game.getBlackPlayer() != null) {
            Player blackPlayer = game.getBlackPlayer();
            restComputeDevicesAndNotify(blackPlayer, game);
        }
    }

    /**
     * identifies what users are interested in this player
     *
     * @param player the player that wee are referring
     * @param game   the game that wee are referring
     */
    private void restComputeDevicesAndNotify(Player player, Game game) {
        String accessToken = myAuthService.getAccessToken();
        if (accessToken == null) {
            return;
        }
        String playerKey = player.getPlayerKey();
        List<String> userKeys = new ArrayList<>();


        String globalFollowersLoc = Constants.LOCATION_GLOBAL_FOLLOWERS_BY_PLAYER
                .replace(Constants.PLAYER_KEY, playerKey);
        globalFollowersLoc = Constants.FIREBASE_URL + globalFollowersLoc + ".json?access_token=" + accessToken;
        List<User> followers = getFollowers(globalFollowersLoc);

        for (User user : followers) {
            String userKey = user.getUserKey();
            String myDevicesLoc = Constants.LOCATION_MY_DEVICES
                    .replace(Constants.USER_KEY, userKey);
            myDevicesLoc = Constants.FIREBASE_URL + myDevicesLoc + ".json?access_token=" + accessToken;

            List<Device> devices = getUserDevices(myDevicesLoc);
            for (Device device : devices) {
                if (device.getDeviceType().equals(String.valueOf(Device.DeviceType.ANDROID))) {
                    sendNotification(device.getDeviceKey(), player, game);
                    log.info("chess-data-notification: userKey=" + userKey + " deviceKey=" + device.getDeviceKey());
                }
            }

        }
    }

    private List<Device> getUserDevices(String userDevicesUrl) {
        String jsonDevices = restFirebaseGetContent(userDevicesUrl);


        if (jsonDevices == null) {
            log.info("chess-data-getUserDevices: no devices");
            return new ArrayList<>();
        }

        //special case when reading from firebase
        if (jsonDevices.indexOf("null") == 0) {
            log.info("chess-data-null-text: no devices");
            return new ArrayList<>();
        }
        ;

        Gson gson = MyGson.getGson();
        List<Device> devices = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonDevices.trim());
            Iterator<?> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (jsonObject.get(key) instanceof JSONObject) {
                    JSONObject item = (JSONObject) jsonObject.get(key);
                    String testJson = item.toString();
                    log.info("chess-data-device-text: " + testJson);
                    Device device = gson.fromJson(testJson, Device.class);
                    if (device != null) {
                        devices.add(device);
                    }
                }
            }
            log.info("chess-data-total-devices = " + devices.size());
            return devices;
        } catch (JSONException e) {
            log.info("error-chess-data-message: " + e.getMessage());
            log.info("error-chess-data-userDeviceUrl: " + userDevicesUrl);
            log.info("error-chess-data-jsonDevices: <" + jsonDevices + ">");
        }
        //return empty list;
        return new ArrayList<>();
    }

    /**
     * It decodes the list of followers from firebase referenced by globalFollowersUrl.
     *
     * @param globalFollowersUrl
     * @return the list of users that follows a player
     */
    private List<User> getFollowers(String globalFollowersUrl) {
        String jsonUsers = restFirebaseGetContent(globalFollowersUrl);
        if (jsonUsers == null) {
            return new ArrayList<>();
        }
        int indexNull = jsonUsers.indexOf("null");
        if (indexNull == 0) {
            return new ArrayList<>();
        }


        Gson gson = MyGson.getGson();
        List<User> users = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonUsers);
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                JSONObject item = (JSONObject) jsonObject.get(key);
                String text = item.toString();
                User user = gson.fromJson(text, User.class);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    private Game getGame(String gameUrl) {
        String gameJson = restFirebaseGetContent(gameUrl);
        Gson gson = MyGson.getGson();
        Game game = gson.fromJson(gameJson, Game.class);
        log.info("chess-data-decoded-game: " + gson.toJson(game));
        return game;
    }

    /**
     * it gets the content from firebase using the REST api.
     *
     * @param locationUrl
     * @return
     */
    private String restFirebaseGetContent(String locationUrl) {
        String responseJson = "no-response";

        try {
            URL url = new URL(locationUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            int result = conn.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                responseJson = sb.toString();

            } else {
                errorLogger.info("chess-data-error: " + conn.getResponseMessage() + ", errorCode = " + result);
                throw new IllegalStateException("chess-data-error: response = " + result + ", request = " + locationUrl);
            }

        } catch (MalformedURLException e) {
            errorLogger.info("chess-data-error: " + e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } catch (IOException e) {
            errorLogger.info("chess-data-error: " + e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }

        log.info("chess-data-restFirebaseGetContent locationUrl=" + locationUrl);
        log.info("chess-data-restFirebaseGetContent response=" + responseJson);
        return responseJson;
    }


    private void sendNotification(String deviceKey, Player player, Game game) {
        if (deviceKeys.contains(deviceKey)) {
            return;
        }
        deviceKeys.add(deviceKey);

        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + MySecurityValues.securityValues.getFirebaseServerKey());

            JSONObject simpleNotification = new JSONObject();
            simpleNotification.put("title", "chess-data updates");
            simpleNotification.put("body", "" +
                    game.getWhitePlayer().getName() +
                    formatResult(game.getResult()) +
                    game.getBlackPlayer().getName());


            JSONObject firebaseNotification = new JSONObject();
            firebaseNotification.put("notification", simpleNotification);
            firebaseNotification.put("to", deviceKey);

            String jsonContent = firebaseNotification.toString();
            OutputStreamWriter streamWriter = new OutputStreamWriter(conn.getOutputStream());
            streamWriter.write(jsonContent);
            streamWriter.flush();

            //todo decode the fcm response
            StringBuilder sb = new StringBuilder();
            int result = conn.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                log.info("chess-data-response " + sb.toString());
            } else {
                errorLogger.info("chess-data-error: " + conn.getResponseMessage() + ", errorCode = " + result);
            }
        } catch (MalformedURLException e) {
            errorLogger.info("chess-data-errorMalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            errorLogger.info("chess-data-IOException: " + e.getMessage());
        }
    }

    private String formatResult(int result) {
        if (result == 1) {
            return " 1 - 0 ";
        } else if (result == 2) {
            return " 0 - 1 ";
        } else if (result == 3) {
            return " 1/2 - 1/2 ";
        }
        return " 0 - 0 ";
    }
}
