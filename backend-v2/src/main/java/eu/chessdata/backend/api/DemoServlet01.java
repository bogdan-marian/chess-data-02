/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package eu.chessdata.backend.api;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.Constants;

public class DemoServlet01 extends HttpServlet {
    private static final Logger log = Logger.getLogger(DemoServlet01.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        System.out.println(listAllAccounts());
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if (name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }

    private String listAllAccounts() {
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentials.json").getFile();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    User user = item.getValue(User.class);
                    System.out.println(user.getName());
                    log.info("onDataChange: " + user.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return "End of listAllAccounts()";
    }
}
