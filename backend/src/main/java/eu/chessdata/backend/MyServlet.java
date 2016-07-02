/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package eu.chessdata.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.http.*;

import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.Constants;

public class MyServlet extends HttpServlet {
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
        if(name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }

    private String listAllAccounts(){
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentials.json").getFile();
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream(accountPath))
                    .setDatabaseUrl(Constants.FIREBASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot item: dataSnapshot.getChildren()){
                        User user = item.getValue(User.class);
                        System.out.println(user.getName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return "End of listAllAccounts()";
    }
}
