package com.hafsa.notes;

public class FirstTable  {

    public static final String TABLE_NAME = "first_table";

    public static final String COLUMN_ID = "COLUMN_ID";
    public static final String COLUMN_WORD = "COLUMN_WORD";
    public static final String COLUMN_DEFINITION = "COLUMN_DEFINITION";
    public static final String COLUMN_SYNONYM = "COLUMN_SYNONYM";
    public static final String COLUMN_SENTENCE = "COLUMN_SENTENCE";
    public static final String COLUMN_FORM = "COLUMN_FORM";
    public static final String COLUMN_NOTIFY = "COLUMN_NOTIFY";
    public static final String COLUMN_BOOKMARK = "COLUMN_BOOKMARK";
    public static final String COLUMN_RECENT = "COLUMN_RECENT";

    private int id;
    private String word;
    private String definition;
    private String synonym;
    private String sentence;
    private String form;
    private int notify;
    private int bookmark;
    private int recent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public int getRecent() {
        return recent;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    @Override
    public String toString() {
        return "FirstTable{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", definition='" + definition + '\'' +
                ", synonym='" + synonym + '\'' +
                ", sentence='" + sentence + '\'' +
                ", form='" + form + '\'' +
                ", notify=" + notify +
                ", bookmark=" + bookmark +
                ", recent=" + recent +
                '}';
    }
}
