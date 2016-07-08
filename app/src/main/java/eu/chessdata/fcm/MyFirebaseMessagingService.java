package eu.chessdata.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 7/8/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = Constants.LOG_TAG;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "MessageReceived from: " + remoteMessage.getFrom());
        Log.d(TAG, "MessageReceived body: " + remoteMessage.getNotification().getBody());
    }
}
