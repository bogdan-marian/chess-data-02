package eu.chessdata.backend.utils;

import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

//@Component
public class MyAuthImplementation implements MyAuthService {
    private GoogleCredential googleCredential;


    //  @Autowired
    public MyAuthImplementation() {
        initialize();
    }

    // @Override
    public String getAccessToken() {
        if (null == googleCredential) {
            googleCredential = buildCredential();
        }
        try {
            googleCredential.refreshToken();
            return googleCredential.getAccessToken();
        } catch (IOException e) {
            throw new IllegalStateException("Not able to refresh token", e);
        }
    }

    // @Override
    public void initialize() {
        if (null == googleCredential) {
            googleCredential = buildCredential();
        }
    }

    private GoogleCredential buildCredential() {
        ClassLoader classLoader = getClass().getClassLoader();
        String accountPath = classLoader.getResource("serviceAccountCredentials.json").getFile();
        try {
            GoogleCredential googleCredential = GoogleCredential.fromStream(new FileInputStream(accountPath));
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
