package com.example.simplemusicplayer;

/**
 * Created by Administrator on 2017/12/31 0031.
 */

public interface Constant {

     class PlayMsg {

        public static final String PLAY_MSG = "com.music.PLAY";// 开始播放
        public static final String PAUSE_MSG = "com.music.PAUSE";// 暂停播放
        public static final String PLAYPAUSE_MSG = "com.music.PLAYPAUSE"; //主界面的播放暂停按钮

        public static final String PRE_MSG = "com.music.PREVIOUS"; //上一首
        public static final String NEXT_MSG = "com.music.NEXT"; //下一首

        public static final String LOOP_MSG = "com.music.LOOPON"; //全部循环
        public static final String SINGLESLOOP_MSG = "com.music.SINGLESLOOP"; //单曲循环
        public static final String LOOPOFF_MSG = "com.music.LOOPOFF"; //关闭循环

        public static final String RANDOMON_MSG = "com.music.RandomON";//随机开
        public static final String RANDOMOFF_MSG = "com.music.RandomOFF";//随机关

    }
}
