package com.example.dingdang.youtubenotetaker;

/**
 * Created by Dingdang on 2/23/2018.
 */

public class NoteItem {
    private String time, subject, note;
    private long currentTime;

    public NoteItem(long currentTime, String time, String subject, String note){
        this.currentTime = currentTime;
        this.time = time;
        this.subject = subject;
        this.note = note;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
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

    public String toEmailFormat(){
        return "Time: " + getTime() + "\nSubject: " + getSubject() + "\nNote content: \n" + getNote() + "\n\n";
    }

    @Override
    public String toString() {
        String noteItemString = String.format("%s   %s", this.time, this.subject);
        return noteItemString;
    }
}