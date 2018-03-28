package com.example.dingdang.youtubenotetaker;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dingdang on 2/23/2018.
 */

public class NoteItem {
    private String time, subject, note,noteId,notebookName,selected;
    private long currentTime;

    public NoteItem(){
    }

    public NoteItem(long currentTime, String time, String subject, String note){
        this.currentTime = currentTime;
        this.time = time;
        this.subject = subject;
        this.note = note;
        this.noteId=null;
        this.notebookName=null;
        this.selected="false";
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

    public String getNoteId() {
        return this.noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNotebookName() {
        return this.notebookName;
    }

    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    public String getSelected() {
        return this.selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String toEmailFormat(){
        return "Time: " + getTime() + "\nSubject: " + getSubject() + "\nNote content: \n" + getNote() + "\n\n";
    }

    @Override
    public String toString() {
        String noteItemString = String.format("%s   %s", this.time, this.subject);
        return noteItemString;
    }



    //the method that put the note into the firebase
    public Map putInToFireBase(){
        Map<String, String> noteItem = new HashMap<String, String>();
        noteItem.put("Note",this.getNote());
        noteItem.put("CurrentTime",Long.toString(this.getCurrentTime()));
        noteItem.put("Subject",this.getSubject());
        noteItem.put("Time",this.getTime());
        noteItem.put("NoteId",this.getNoteId());
        noteItem.put("NotebookName",this.getNotebookName());
        noteItem.put("Selected",this.getSelected());
        return noteItem;

    }
}