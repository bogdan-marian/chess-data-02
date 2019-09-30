package eu.chessdata.backend.model;

import java.text.Collator;

/**
 * Created by Bogdan Oloeriu on 6/4/2016.
 */
public class Player implements Comparable<Player> {
    private String name;
    private String email;
    private String elo;

    private String userKey;
    private String playerKey;
    private String clubKey;
    /**
     * when updating guestClubKey wee should change also the club name to the one associated with
     * the guestClubKey
     */
    private String clubName;
    private String guestClubKey;

    public Player() {
    }

    public Player(String name, String email, String clubKey, String clubName) {
        this.name = name;
        this.email = email;
        this.clubKey = clubKey;
        this.clubName = clubName;
    }

    @Override
    public int compareTo(Player another) {
        Collator defaultCollator = Collator.getInstance();
        return defaultCollator.compare(this.getName(), another.getName());
    }

    public String getEmail() {
        return email;
    }

    public String getGuestClubKey() {
        return guestClubKey;
    }

    public void setGuestClubKey(String guestClubKey) {
        this.guestClubKey = guestClubKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setElo(String elo) {
        this.elo = elo;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public void setClubKey(String clubKey) {
        this.clubKey = clubKey;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getName() {
        return name;
    }

    public String getElo() {
        return elo;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getPlayerKey() {
        return playerKey;
    }

    public String getClubKey() {
        return clubKey;
    }

    public String getClubName() {
        return clubName;
    }


}
