package eu.chessdata.backend.utils;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

@Service
public class MyAuthImplementation {
    private GoogleCredentials googleCredential;


    public String getAccessToken() {
        if (null == googleCredential) {
            googleCredential = buildCredential();
        }
        try {
            AccessToken accessToken = googleCredential.refreshAccessToken();
            String token = accessToken.getTokenValue();
            return token;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to refresh token [" + e.getMessage() + "]", e);
        }
    }

    @PostConstruct
    public void initialize() {
        googleCredential = buildCredential();
    }

    private GoogleCredentials buildCredential() {
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentialsV2.json").getFile();
        try {
            GoogleCredentials googleCredential = GoogleCredentials.fromStream(new FileInputStream(accountPath));
            googleCredential = googleCredential.createScoped(
                    Arrays.asList(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"
                    )
            );
            return googleCredential;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to initialize credentials: ", e);
        }
    }
}
