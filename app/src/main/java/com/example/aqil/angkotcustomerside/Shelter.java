package com.example.aqil.angkotcustomerside;

public class Shelter {
    String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    String Lokasi;

    public Shelter(String name, String lokasi, double lat, double aLong) {
        Name = name;
        Lokasi = lokasi;
        Lat = lat;
        Long = aLong;
    }

    double Lat;
    double Long;


}
