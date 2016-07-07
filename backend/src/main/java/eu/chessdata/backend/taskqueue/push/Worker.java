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
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.MyPayLoad;
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
            log.info("chess-data Worker decoded payload: " + myPayLoad.getGameLocation());
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
}
