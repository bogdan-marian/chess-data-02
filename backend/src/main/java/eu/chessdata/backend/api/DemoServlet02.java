package eu.chessdata.backend.api;

import com.google.appengine.repackaged.com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.MySecurityValues;

/**
 * Created by Bogdan Oloeriu on 03/07/2016.
 */
public class DemoServlet02 extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Debug message from: DemoServlet02\n\n");

        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String json = "";
        if (br != null){
            json = json+ br.readLine();
        }
        System.out.println("Jons = " + json);
        resp.getWriter().println("Json: " + json+"\n");

        Gson gson = new Gson();
        User user = gson.fromJson(json,User.class);
        String fromObject = "Decoded values: name = " + user.getName()+" email = " + user.getEmail();
        System.out.println(fromObject);
        resp.getWriter().println(fromObject);

        User user1 = new User();
        user1.setName("Bogdan");
        user1.setEmail("bogdan@mail.com");
        String json2 = gson.toJson(user1);
        System.out.println("New json: " + json2);

        String firebaseId = MySecurityValues.securityValues.getFirebaseServerKey();
        System.out.println("Firebase id = " + firebaseId);
    }
}
