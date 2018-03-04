package eu.chessdata.model;

import com.firebase.client.ServerValue;

import java.text.Collator;
import java.util.HashMap;

import eu.chessdata.utils.Constants;

/**
 * Created by Bogdan Oloeriu on 5/25/2016.
 */
public class Club implements Comparable<Club>{
    private String name;
    private String shortName;
    private String email;
    private String country;
    private String city;
    private String homePage;
    private String description;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> updateStamp;

    public Club(){

    }

    public Club(String name, String shortName, String email, String country, String city, String homePage, String description){
        HashMap<String, Object> timeStamp = new HashMap<>();
        timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        this.name = name;
        this.shortName = shortName;
        this.email = email;
        this.country = country;
        this.city = city;
        this.homePage = homePage;
        this.description = description;
        this.dateCreated = timeStamp;
        this.updateStamp = timeStamp;
    }

    public long dateCreatedGetLong(){
        return (long) dateCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
    public String valMapKey(){
        return this.getName()+dateCreatedGetLong();
    }

    @Override
    public int compareTo(Club another) {
        Collator defaultCollator = Collator.getInstance();
        return defaultCollator.compare(this.getName(), another.getName());
    }

    //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
