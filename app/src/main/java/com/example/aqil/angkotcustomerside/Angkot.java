package com.example.aqil.angkotcustomerside;

public class Angkot {
    String nomorAngkot;
    String tujuan;
    String jumlahPenumpang;

    public Angkot(String nomorAngkot, String tujuan, String jumlahPenumpang, String nomorPlat) {
        this.nomorAngkot = nomorAngkot;
        this.tujuan = tujuan;
        this.jumlahPenumpang = jumlahPenumpang;
        this.nomorPlat = nomorPlat;
    }

    String nomorPlat;

    public void setNomorPlat(String nomorPlat) {
        this.nomorPlat = nomorPlat;
    }

    public String getNomorAngkot() {
        return nomorAngkot;
    }

    public void setNomorAngkot(String nomorAngkot) {
        this.nomorAngkot = nomorAngkot;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getJumlahPenumpang() {
        return jumlahPenumpang;
    }

    public void setJumlahPenumpang(String jumlahPenumpang) {
        this.jumlahPenumpang = jumlahPenumpang;
    }
}
