package com.audiobook.nbogdand.playbook.data;

public class Song {
    String title;
    String author = "";
    long length;

    public Song() {
    }

    public Song(String title, String author, long length) {
        this.title = title;
        this.author = author;
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
