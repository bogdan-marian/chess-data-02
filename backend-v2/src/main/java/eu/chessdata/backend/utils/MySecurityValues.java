package eu.chessdata.backend.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import eu.chessdata.backend.model.SecurityValues;

/**
 * Created by Bogdan Oloeriu on 7/12/2016.
 */
public class MySecurityValues {
    public static final SecurityValues securityValues = readSecurityValues();
    private static MySecurityValues ourInstance = new MySecurityValues();

    public static MySecurityValues getInstance() {
        return ourInstance;
    }

    private MySecurityValues() {

    }

    private static SecurityValues readSecurityValues() {
        InputStream inputStream = MySecurityValues.class.getClassLoader().getResourceAsStream("mySecurityValues.json");
        try {
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            SecurityValues securityValues = gson.fromJson(reader, SecurityValues.class);
            return securityValues;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Not able to read from mySecurityValues.json");
        }
    }
}
