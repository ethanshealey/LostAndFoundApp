package com.example.lostandfound;

/***
 * Locations
 *
 * A class to store location data
 */

public class Locations implements java.io.Serializable {

    private String ID;
    private String Name;
    private String Lat;
    private String Lng;
    private String Details;

    public void setID(String id) { this.ID = id; }

    public void setName(String name) {
        this.Name = name;
    }

    public void setLat(String lat) {
        this.Lat = lat;
    }

    public void setLng(String lng) {
        this.Lng = lng;
    }

    public void setDetails(String details) {
        this.Details = details;
    }

    public String getID() { return this.ID; }

    public String getName() {
        return this.Name;
    }

    public String getCoords() { return this.Lat + ", " + this.Lng; }

    public String getLat() {
        return this.Lat;
    }

    public String getLng() {
        return this.Lng;
    }

    public String getDetails() {
        return this.Details;
    }

}
