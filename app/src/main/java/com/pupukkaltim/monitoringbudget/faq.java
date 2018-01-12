package com.pupukkaltim.monitoringbudget;

/**
 * Created by ROG-STRIX on 09/01/2018.
 */

public class faq {
    private String caption, filename;

    public faq() {
    }

    public faq(String caption, String filename) {
        this.caption = caption;
        this.filename = filename;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String name) {
        this.caption = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String genre) {
        this.filename = genre;
    }
}


