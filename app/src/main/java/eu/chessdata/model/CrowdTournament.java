package eu.chessdata.model;

import com.firebase.client.ServerValue;

import java.util.HashMap;

import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 27/06/2018.
 */
public class CrowdTournament {
    private String name;
    private String description;
    private String location;
    private int totalRounds;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> updateStamp;

    public CrowdTournament (String name,
                            String description,
                            String location,
                            int totalRounds){
        HashMap<String, Object> timeStamp = new HashMap<>();
        timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        this.name = name;
        this.description = description;
        this.location = location;
        this.totalRounds = totalRounds;
        this.dateCreated = timeStamp;
        this.updateStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public HashMap<String, Object> getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(HashMap<String, Object> dateCreated) {
        this.dateCreated = dateCreated;
    }

    public HashMap<String, Object> getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(HashMap<String, Object> updateStamp) {
        this.updateStamp = updateStamp;
    }
}
