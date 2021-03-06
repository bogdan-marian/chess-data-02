package eu.chessdata.backend.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
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
import eu.chessdata.backend.utils.MyGson;

/**
 * Created by Bogdan Oloeriu on 05/07/2016.
 */
public class BasicApi extends HttpServlet {
    private static final Logger log = Logger.getLogger(BasicApi.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonPlayLoad = "";
        if (bufferedReader != null) {
            jsonPlayLoad = jsonPlayLoad + bufferedReader.readLine();
        }
        Gson gson = MyGson.getGson();
        MyPayLoad myPayLoad = gson.fromJson(jsonPlayLoad, MyPayLoad.class);
        if (myPayLoad.getEvent() == MyPayLoad.Event.GAME_RESULT_UPDATED) {
            resp.getWriter().print("Time to notify for game: " + myPayLoad.getGameLocation());
        }

        //add the task to the default queue
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/worker").payload(jsonPlayLoad));
        resp.getWriter().print("Task added to default queue: " + bufferedReader);
        log.info("bogdanTag Task added to default queue: " + bufferedReader);

//        String firebaseId = MySecurityValues.securityValues.getFirebaseServerKey();
//        System.out.println("Firebase id = " + firebaseId);

    }
}
