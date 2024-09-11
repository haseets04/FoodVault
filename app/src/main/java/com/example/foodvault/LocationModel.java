package com.example.foodvault;

public class LocationModel {
   private Integer location_id;
   private String location_name;

    public Integer getLocation_id() {
        return location_id;
    }

    public void setLocation_id(Integer location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public LocationModel(Integer location_id, String location_name) {
        this.location_id = location_id;
        this.location_name = location_name;
    }
}
