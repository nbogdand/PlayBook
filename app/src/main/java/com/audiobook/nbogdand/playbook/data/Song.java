package com.audiobook.nbogdand.playbook.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String author = "";
    private long length;
    private String songPath;

    public Song() {
    }

    public Song(String title, String author, long length, String songPath) {
        this.title = title;
        this.author = author;
        this.length = length;
        this.songPath = songPath;
    }

    protected Song(Parcel in) {
        title = in.readString();
        author = in.readString();
        length = in.readLong();
        songPath = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeLong(length);
        dest.writeString(songPath);

    }
}
