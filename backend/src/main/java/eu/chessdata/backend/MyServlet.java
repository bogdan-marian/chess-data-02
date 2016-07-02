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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.http.*;

import eu.chessdata.backend.model.User;
import eu.chessdata.backend.utils.Constants;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        resp.getWriter().println(getFileContent()+"\n"+listAllAccounts());
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

    private String getFileContent(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("simpleFile.txt").getFile());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuffer sb = new StringBuffer();
            int content;
            while ((content = fileInputStream.read())!=-1){
                sb.append((char)content);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Should not be able to reach this point");//return "No content yet:";
    }

    private String listAllAccounts(){
        ClassLoader classLoader = getClass().getClassLoader();
        final StringBuffer sb = new StringBuffer();
        String filePath = classLoader.getResource("serviceAccountCredentials.json").getFile();
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream(filePath))
                    .setDatabaseUrl(Constants.FIREBASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot item: dataSnapshot.getChildren()){
                        User user = item.getValue(User.class);
                        sb.append(user.getName()+"\n");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw new IllegalStateException(databaseError.getMessage());
                }
            });

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e.getMessage());
        }
        sb.append(filePath+"\n");
        return sb.toString();
    }
}
