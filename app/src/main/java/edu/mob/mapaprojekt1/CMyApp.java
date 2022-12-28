package edu.mob.mapaprojekt1;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class CMyApp extends Application{

    private static CMyApp singleton;

    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public CMyApp getInstance(){
        return singleton;
    }

    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
