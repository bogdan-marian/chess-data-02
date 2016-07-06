package eu.chessdata.backend.utils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
public class MyFirebase {
    public static void makeSureEverythingIsInOrder(){
        if (ourInstance == null){
            throw new IllegalStateException("Firebase is not OK");
        }
    }

    private static MyFirebase ourInstance = new MyFirebase();

    public static MyFirebase getInstance() {
        return ourInstance;
    }

    private MyFirebase() {
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentials.json").getFile();
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream(accountPath))
                    .setDatabaseUrl(Constants.FIREBASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
