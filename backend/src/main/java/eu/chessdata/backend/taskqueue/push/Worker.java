package eu.chessdata.backend.taskqueue.push;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.MyPayLoad;
import eu.chessdata.backend.utils.MyFirebase;
import eu.chessdata.backend.utils.MyGson;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
public class Worker extends HttpServlet{
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
        
    }
}
