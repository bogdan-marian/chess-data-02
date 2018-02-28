package eu.chessout.backend.utils;

import com.google.gson.Gson;

/**
 * Created by Bogdan Oloeriu on 06/07/2016.
 */
public class MyGson {
    private static MyGson ourInstance = new MyGson();

    private Gson gson = new Gson();

    private static MyGson getInstance() {
        return ourInstance;
    }

    private MyGson() {
    }

    public static Gson getGson(){
        return  ourInstance.gson;
    }
}
