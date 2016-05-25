package eu.chessdata.model;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class ClubManager {
    private String userId;
    private String name;

    public ClubManager(){}
    public ClubManager(String userId, String name){
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
