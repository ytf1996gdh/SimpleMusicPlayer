package com.example.simplemusicplayer;

/**
 * Created by Administrator on 2017/12/31 0031.
 */

public class MusicInfo {

    private int id; //歌曲ID
    private String title; //音乐标题
    private String singer; //歌手名字
    private long time; //音乐时长
    private String url; //音乐路径
    public MusicInfo(){}

    public MusicInfo(int id, String title, String singer, long time, String url){
        super();
        this.id=id;
        this.title = title;
        this.singer = singer;
        this.time = time;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
