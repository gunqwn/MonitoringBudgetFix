package com.pupukkaltim.monitoringbudget;

/**
 * Created by ROG-STRIX on 01/01/2018.
 */

public class Information {
    private int id;
    private String caption;
    private String fundCenter;
    private String Informasi;
    private String Tanggal;

    public Information(int id, String caption, String fundCenter, String informasi, String tanggal) {
        this.id = id;
        this.caption = caption;
        this.fundCenter = fundCenter;
        Informasi = informasi;
        Tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFundCenter() {
        return fundCenter;
    }

    public void setFundCenter(String fundCenter) {
        this.fundCenter = fundCenter;
    }

    public String getInformasi() {
        return Informasi;
    }

    public void setInformasi(String informasi) {
        Informasi = informasi;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }
}
