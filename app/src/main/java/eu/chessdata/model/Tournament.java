package eu.chessdata.model;

import java.util.HashMap;

/**
 * Created by Bogdan Oloeriu on 6/4/2016.
 */
public class Tournament {
    private String name;
    private String description;
    private String location;
    private int totalRounds;
    //players
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> updateStamp;

}
