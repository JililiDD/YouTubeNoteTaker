package com.example.dingdang.youtubenotetaker;

/**
 * Created by Dingdang on 2/23/2018.
 */

public class NoteItem {
    private String time, subject, note;

    public NoteItem(String time, String subject, String note){
        this.time = time;
        this.subject = subject;
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        String noteItemString = String.format("%s   %s", this.time, this.subject);
        return noteItemString;
    }
}
