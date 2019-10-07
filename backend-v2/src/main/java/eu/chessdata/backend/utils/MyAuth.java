package eu.chessdata.backend.utils;

import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

/**
 * Created by Bogdan Oloeriu on 30/08/2016.
 */
@Service
public class MyAuth {

    private GoogleCredential scooped;

    @PostConstruct
    private void initialize() {
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


    public String getAccessToken() {
        try {
            scooped.refreshToken();
            return scooped.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Not able to refreshToken:" + e.getMessage());
        }
    }
}
