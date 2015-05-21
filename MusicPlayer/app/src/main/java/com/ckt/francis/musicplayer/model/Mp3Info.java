package com.ckt.francis.musicplayer.model;

/**
 * Created by wuchangzhi on 15年5月21日.
 */
public class Mp3Info {
    private long id;
    private String path;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private long duration;
    private long size;

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }



    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }
}
