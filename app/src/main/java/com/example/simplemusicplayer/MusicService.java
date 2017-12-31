package com.example.simplemusicplayer;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/23 0023.
 */

public class MusicService extends Service {

    private static String TAG = "ln";
    private IBinder iBinder = new myBinder();
    private MusicInfo musicInfo;
    public MediaPlayer mediaPlayer;
    private int id = 0;
    private int curplaytime = 0;
    private int mPlaySta;
    private int mRandomSta = 1;
    private int mLoopSta = 0;
    private int mCount;
    private long musictime = 0;
    private String title = null;
    BroadcastReceiver mBrodcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (Constant.PlayMsg.RANDOMON_MSG.equals(action)) {
                mRandomSta = 1;
            } else if (Constant.PlayMsg.RANDOMOFF_MSG.equals(action)) {
                mRandomSta = 0;
                if (mLoopSta == 1) {
                    mLoopSta = 2;
                }
            }else if (Constant.PlayMsg.LOOP_MSG.equals(action)) {
                mLoopSta = 1;
                if (mRandomSta == 0) {
                    mRandomSta = 1;
                }
            } else if (Constant.PlayMsg.SINGLESLOOP_MSG.equals(action)) {
                mLoopSta = 2;
            } else if (Constant.PlayMsg.LOOPOFF_MSG.equals(action)) {
                mLoopSta = 0;
            }else if (Constant.PlayMsg.NEXT_MSG.equals(action)) {
                next();
            }else if(Constant.PlayMsg.PRE_MSG.equals(action)){
                previous();
            }else if (Constant.PlayMsg.PAUSE_MSG.equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }else if (Constant.PlayMsg.PLAY_MSG.equals(action)) {
                if (curplaytime != 0) {
                    musicplay(id);
                    mediaPlayer.seekTo(curplaytime);
                } else {
                    musicplay(id);
                }
            }else if(Constant.PlayMsg.PLAYPAUSE_MSG.equals(action)){
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }else{
                    if (curplaytime != 0) {
                        musicplay(id);
                        mediaPlayer.seekTo(curplaytime);
                    } else {
                        musicplay(id);
                    }
                }
            }
        }
    };
    Handler handler = new Handler() {};
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mPlaySta = 0;
                } else {
                    mPlaySta = 1;
                }
            }
            title = musicInfo.getTitle();
            if (mediaPlayer != null && title != null) {
                curplaytime = mediaPlayer.getCurrentPosition();
                musictime = musicInfo.getTime();
                id = musicInfo.getId();
                autoPlay();
            }
            handler.postDelayed(this, 500);
        }
    };

    public void onCreate() {
        Log.i(TAG, "MusicService onCreate()");
        musicInfo = new MusicInfo();
        mediaPlayer = new MediaPlayer();
        mCount = MusicList.getMusicInfos(this).size();
        handler.postDelayed(runnable, 500);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PlayMsg.RANDOMON_MSG);
        filter.addAction(Constant.PlayMsg.RANDOMOFF_MSG);
        filter.addAction(Constant.PlayMsg.LOOP_MSG);
        filter.addAction(Constant.PlayMsg.LOOPOFF_MSG);
        filter.addAction(Constant.PlayMsg.SINGLESLOOP_MSG);
        filter.addAction(Constant.PlayMsg.NEXT_MSG);
        filter.addAction(Constant.PlayMsg.PRE_MSG);
        filter.addAction(Constant.PlayMsg.PLAY_MSG);
        filter.addAction(Constant.PlayMsg.PAUSE_MSG);
        filter.addAction(Constant.PlayMsg.PLAYPAUSE_MSG);
        registerReceiver(mBrodcast, filter);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "MusicService onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return new myBinder();
    }

    public class myBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void next() {
        if (mRandomSta == 1) {
            if (id >= 0 && id < mCount - 1) {
                id = id + 1;
                musicplay(id);
            } else {
                musicplay(0);
            }
        } else {
            id = getRandomNumber();
            musicplay(id);
        }
    }

    public void previous() {
        if (mRandomSta == 1) {
            if (id > 0 && id < mCount) {
                id = id - 1;
                musicplay(id);
            } else if (id == 0) {
                musicplay(mCount - 1);
            }
        } else {
            id = getRandomNumber();
            musicplay(id);
        }
    }

    public void musicplay(int id) {
        try {
            musicInfo = MusicList.getMusicInfos(getApplicationContext()).get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = musicInfo.getUrl();
        if (url != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "没有播放的音乐", Toast.LENGTH_SHORT).show();
        }
    }

    public int getRandomNumber() {
        Random random = new Random();
        int location = random.nextInt(mCount);
        return location;
    }

    public void autoPlay() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setAutoPlay();
            }
        });
    }
    private void setAutoPlay(){
        if (mRandomSta == 0) {
            id = getRandomNumber();
            musicplay(id);
        } else if (mLoopSta == 0) {
            id = id + 1;
            if (id <= mCount - 1) {
                Log.i(TAG, "loop all list : " +  id);
                musicplay(id);
            } else {
                id = 0;
                musicplay(0);
            }
        } else if (mLoopSta == 1) {
            musicplay(id);
        } else if (mLoopSta == 2) {
            id = id + 1;
            if (id <= mCount - 1) {
                musicplay(id);
            } else {
                Toast.makeText(this,getResources().getString(R.string.music_list_end),Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
            }
        }
    }
    /**
     * 返回实时播放时间
     * @return
     */
    public int getCurPlaytime() {
        return curplaytime;
    }

    /**
     * 返回单曲总时间 long musictime
     * @return
     */
    public long getTime() {
        return musictime;
    }

    /**
     * 返回 单曲标题 String
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 返回单曲id int
     * @return
     */
    public int getId() {
        return id;
    }
    /**
     * Uers 拖动播放进度条
     * @param progress
     */
    public void setUserTime(int progress) {
        mediaPlayer.seekTo(progress);
    }

    /**
     * 返回播放状态 :0- 播放,1- 暂停
     * @return
     */
    public int getPlaySta() {
        return mPlaySta;
    }

    /**
     * 返回随机状态 :0-随机开,1- 随机关
     * @return
     */
    public int getRandomSta() {
        return mRandomSta;
    }

    /**
     * 返回循环状态 : 0-全部循环， 1-单曲循环， 2-关闭循环
     * @return
     */
    public int getLoopSta() {
        return mLoopSta;
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBrodcast); //解除广播注册
    }
}

