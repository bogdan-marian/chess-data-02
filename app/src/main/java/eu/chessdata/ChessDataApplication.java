package eu.chessdata;

import com.firebase.client.Firebase;

/**
 * Created by Bogdan Oloeriu on 5/24/2016.
 */
public class ChessDataApplication extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
