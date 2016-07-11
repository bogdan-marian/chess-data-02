package eu.chessdata.backend.taskqueue.push;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            log.info("Worker threads complete!");
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
                List<Device> devices = getDevicesToNotify(whitePlayer.getPlayerKey());
                log.info("debug 3: device size = " + devices.size()+"for playerKey = " + whitePlayer.getPlayerKey());
                for(Device device: devices) {
                    log.info("I should notify device: " + device.getDeviceType()+":"+ device.getDeviceKey());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Device> getDevicesToNotify(String playerKey) {
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

            for (String userKey: userKeys){
                final CountDownLatch secondLatch = new CountDownLatch(1);
                String myDevicesLoc = Constants.LOCATION_MY_DEVICES
                        .replace(Constants.USER_KEY,userKey);
                DatabaseReference myDevicesRef = FirebaseDatabase.getInstance().getReference(myDevicesLoc);
                myDevicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            for (DataSnapshot item: dataSnapshot.getChildren()){
                                Device device = item.getValue(Device.class);
                                if (device != null){
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

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return devices;
    }
}
