package eu.chessdata.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;


/**
 * note: https://firebase.google.com/docs/cloud-messaging/migrate-v1
 * Inspired by: https://blog.mestwin.net/send-push-notifications-from-spring-boot-server-side-application-using-fcm/?unapproved=167&moderation-hash=d1cae62877c4ce899706839609095096#comment-167
 */
@Service
public class FCMInitializer {

    //@Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath = "serviceAccountCredentialsV2.json";

    Logger logger = Logger.getLogger(FCMInitializer.class.getName());

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

}
