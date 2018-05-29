package com.hafsa.notes.rxjavaretrofit.utils;

public class Note {

    int id;
    String note;
    String timestamp;

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", note='" + note + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
