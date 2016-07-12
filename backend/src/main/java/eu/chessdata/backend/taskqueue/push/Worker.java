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
import eu.chessdata.backend.utils.MyFirebase;
import eu.chessdata.backend.utils.MyGson;
import eu.chessdata.backend.utils.MySecurityValues;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
public class Worker extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonPlayLoad = "";
        if (bufferedReader != null) {
            jsonPlayLoad = jsonPlayLoad + bufferedReader.readLine();
        }
        log.info("Decoded payload: " + jsonPlayLoad);
        Gson gson = MyGson.getGson();
        MyPayLoad myPayLoad = gson.fromJson(jsonPlayLoad, MyPayLoad.class);

        if (myPayLoad.getEvent() == MyPayLoad.Event.GAME_RESULT_UPDATED) {
            log.info("Time to call: notifyUsersGameResultUpdated");
            notifyUsersGameResultUpdated(myPayLoad);
        }

        MyFirebase.makeSureEverythingIsInOrder();


        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    User user = item.getValue(User.class);
                    log.info("onDataChange: " + user.getName());
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log.info("firebase error: " + databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void notifyUsersGameResultUpdated(MyPayLoad myPayLoad) {
        if (myPayLoad.getEvent() != MyPayLoad.Event.GAME_RESULT_UPDATED) {
            return;
        }
        String gameLocation = myPayLoad.getGameLocation();
        if (gameLocation == null) {
            return;
        }
        log.info("notifyUsersGameResultUpdated: debug1");
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
                    log.info("firebase error: " + databaseError.getMessage());
                    latch01.countDown();
                }
            });

            latch01.await();
            log.info("notifyUsersGameResultUpdated: debug2 end of await: ");
            if (game.getTableNumber() == -1) {
                return;
            }
            //wee have data
            if (game.getWhitePlayer() != null) {
                Player whitePlayer = game.getWhitePlayer();
                List<Device> devices = getDevicesToNotify(whitePlayer, game);
                log.info("debug 3: device size = " + devices.size() + "for playerKey = " + whitePlayer.getPlayerKey());
                for (Device device : devices) {
                    log.info("I should notify device: " + device.getDeviceType() + ":" + device.getDeviceKey());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Device> getDevicesToNotify(Player player, Game game) {
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
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + MySecurityValues.securityValues.getFirebaseServerKey());

            JSONObject message = new JSONObject();
            message.put("data", "chess-data! " + player.getName() + " just finished his game");
            message.put("to", deviceKey);

            String data = "chess-data! "+player.getName()+" just finished his game";
            String myMessage = "{\n" +
                    "\t\"data\": {\n" +
                    "    \"score\": \"5x1\",\n" +
                    "    \"time\": \"15:10\"\n" +
                    "  },\n" +
                    "\t\"to\": \"$myKey\"\n" +
                    "}";
            myMessage = myMessage.replace("$myData",data).replace("$myKey",deviceKey);
            log.info("myMessage = " + myMessage);

            String jsonContent = message.toString();
            log.info("chess-data: json content = " + jsonContent);
            OutputStreamWriter streamWriter = new OutputStreamWriter(conn.getOutputStream());
            streamWriter.write(myMessage);
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
                log.info("chess-data-error: " + conn.getResponseMessage()+", errorCode = " + result);
            }
        } catch (MalformedURLException e) {
            log.info("chess-data-errorMalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            log.info("chess-data-IOException: " + e.getMessage());
        }
    }
}
