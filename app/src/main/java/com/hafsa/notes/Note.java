package com.hafsa.notes;

public class Note {

    private int id;
    private String note;

    public Note(int i, String s) {
        id = i;
        note = s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", note='" + note + '\'' +
                '}';
    }
}
