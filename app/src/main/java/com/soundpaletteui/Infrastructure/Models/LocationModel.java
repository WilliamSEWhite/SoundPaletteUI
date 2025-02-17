package com.soundpaletteui.Infrastructure.Models;

public class LocationModel {
    private int LocationId;
    private String LocationName;

    public LocationModel(int locationId, String locationName)
    {
        LocationId = locationId;
        LocationName = locationName;
    }

    public int getLocationId() {
        return LocationId;
    }

    public String getLocationName() {
        return LocationName;
    }

}
