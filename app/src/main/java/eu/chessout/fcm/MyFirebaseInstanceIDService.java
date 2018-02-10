package eu.chessout.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import eu.chessout.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 7/8/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = Constants.LOG_TAG;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refreshed token: " + refreshedToken);
    }
}
