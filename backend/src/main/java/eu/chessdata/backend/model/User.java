package eu.chessdata.backend.model;

import com.firebase.client.ServerValue;

import java.util.HashMap;

import eu.chessdata.backend.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 5/24/2016.
 */
public class User {
    private String name;
    private String email;
    private HashMap<String, Object> dateCreated;

    public User() {
    }


    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param name
     * @param email
     */
    public User(String name, String email) {
        HashMap<String, Object> timeStamp = new HashMap<>();
        timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        this.name = name;
        this.email = email;
        this.dateCreated = timeStamp;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Object> getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(HashMap<String, Object> dateCreated) {
        this.dateCreated = dateCreated;
    }
}
