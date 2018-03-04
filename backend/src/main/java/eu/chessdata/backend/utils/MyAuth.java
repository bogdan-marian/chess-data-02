package eu.chessdata.backend.utils;

import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Bogdan Oloeriu on 30/08/2016.
 */
public class MyAuth {
    private static MyAuth ourInstance = new MyAuth();

    private GoogleCredential scooped;
    private MyAuth() {
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentials.json").getFile();
        try {
            GoogleCredential googleCredential = GoogleCredential.fromStream(new FileInputStream(accountPath));
            this.scooped = googleCredential.createScoped(
                    Arrays.asList(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MyAuth getInstance() {
        return ourInstance;
    }

    public static String getAccessToken(){
        GoogleCredential scooped = MyAuth.getInstance().scooped;
        try {
            scooped.refreshToken();
            return scooped.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Not able to refreshToken:"+e.getMessage());
        }
    }
}
