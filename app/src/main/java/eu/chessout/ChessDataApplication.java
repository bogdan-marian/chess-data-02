package eu.chessout;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Bogdan Oloeriu on 5/24/2016.
 */
public class ChessDataApplication extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
