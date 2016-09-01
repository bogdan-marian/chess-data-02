package eu.chessdata.backend.taskqueue.push;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

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
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.Device;
import eu.chessdata.backend.model.Game;
import eu.chessdata.backend.model.MyPayLoad;
import eu.chessdata.backend.model.Player;
import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.Constants;
import eu.chessdata.backend.utils.MyAuth;
import eu.chessdata.backend.utils.MyFirebase;
import eu.chessdata.backend.utils.MyGson;
import eu.chessdata.backend.utils.MySecurityValues;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
public class Worker extends HttpServlet {
    private static final Logger errorLogger = Logger.getLogger(Worker.class.getName());
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    private List<String> deviceKeys;

    @Override
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

    private void restNotifyUsersGameResultUpdated(MyPayLoad myPayLoad) {
        if (myPayLoad.getEvent() != MyPayLoad.Event.GAME_RESULT_UPDATED) {
            return;
        }

        String gameUrl = myPayLoad.getGameLocation();
        if (gameUrl == null) {
            return;
        }

        String accessToken = MyAuth.getAccessToken();
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

    private void restComputeDevicesAndNotify(Player player, Game game) {
        String accessToken = MyAuth.getAccessToken();
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
                }
            }

        }
    }

    private List<Device> getUserDevices(String userDevicesUrl) {

        String jsonDevices = restFirebaseGetContent(userDevicesUrl);
        if (jsonDevices == null) {
            return new ArrayList<>();
        }

        Gson gson = MyGson.getGson();
        List<Device> devices = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonDevices);
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

    @Deprecated
    private void notifyUsersGameResultUpdated(MyPayLoad myPayLoad) {
        if (myPayLoad.getEvent() != MyPayLoad.Event.GAME_RESULT_UPDATED) {
            return;
        }
        String gameLocation = myPayLoad.getGameLocation();
        if (gameLocation == null) {
            return;
        }
        try {
            MyFirebase.makeSureEverythingIsInOrder();
            //get the game
            final Game game = new Game();
            game.setTableNumber(-1);
            final CountDownLatch latch01 = new CountDownLatch(1);
            DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(gameLocation);
            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Game newGame = dataSnapshot.getValue(Game.class);
                        if (newGame != null) {
                            game.copyValues(newGame);
                        }
                    }
                    latch01.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    errorLogger.info("firebase error: " + databaseError.getMessage());
                    latch01.countDown();
                }
            });

            latch01.await();
            if (game.getTableNumber() == -1) {
                return;
            }
            //wee have data
            if (game.getWhitePlayer() != null) {
                Player whitePlayer = game.getWhitePlayer();
                computeDevicesAndNotify(whitePlayer, game);
            }
            if (game.getBlackPlayer() != null) {
                Player blackPlayer = game.getBlackPlayer();
                computeDevicesAndNotify(blackPlayer, game);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private List<Device> computeDevicesAndNotify(Player player, Game game) {
        String playerKey = player.getPlayerKey();
        final List<String> userKeys = new ArrayList<>();
        final List<Device> devices = new ArrayList<>();
        String globalFollowersLoc = Constants.LOCATION_GLOBAL_FOLLOWERS_BY_PLAYER
                .replace(Constants.PLAYER_KEY, playerKey);
        DatabaseReference globalFollowersRef = FirebaseDatabase.getInstance().getReference(globalFollowersLoc);
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            globalFollowersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            User user = item.getValue(User.class);
                            userKeys.add(user.getUserKey());
                        }
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    latch.countDown();
                }
            });

            latch.await();

            for (String userKey : userKeys) {
                final CountDownLatch secondLatch = new CountDownLatch(1);
                String myDevicesLoc = Constants.LOCATION_MY_DEVICES
                        .replace(Constants.USER_KEY, userKey);
                DatabaseReference myDevicesRef = FirebaseDatabase.getInstance().getReference(myDevicesLoc);
                myDevicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                Device device = item.getValue(Device.class);
                                if (device != null) {
                                    devices.add(device);
                                }
                            }
                        }
                        secondLatch.countDown();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        secondLatch.countDown();
                    }
                });
                secondLatch.await();
            }
            for (Device device : devices) {
                if (device.getDeviceType().equals(String.valueOf(Device.DeviceType.ANDROID))) {
                    sendNotification(device.getDeviceKey(), player, game);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return devices;
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
