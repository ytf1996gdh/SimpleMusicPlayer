package com.example.simplemusicplayer;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener,
        Constant {

    private String TAG = "ln";
    private MusicInfo musicInfo;
    private Button mButton, mPre, mPlay, mNext, mRandom, mLoop;
    private SeekBar mSeekBar;
    private int mRandomNum;
    private TextView mName, mCurtime, mAllTime, mCount;
    private int musicCount;
    private String title = null;
    private int curloop = 0;
    private MusicService mService = null;
    private boolean isPlay = false;
    private Intent serviceIntent;
    private int playId;


    Handler handler = new Handler() {
    };
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            title = mService.getTitle();
            if (title != null) {
                mName.setText(title);
                mAllTime.setText(MusicAdapter.Format(mService.getTime()));
                int time = new Long(mService.getTime()).intValue();
                mSeekBar.setMax(time);
                mCurtime.setText(MusicAdapter.Format(mService.getCurPlaytime()));
                mSeekBar.setProgress(mService.getCurPlaytime());
                playId = mService.getId() + 1;
                //mCount.setText(playId + "/" + musicCount);
                int imsg = mService.getPlaySta();
                if (imsg == 0) {
                    mPlay.setText(getResources().getString(R.string.pause));
                    isPlay = true;
                }else if (imsg == 1) {
                    mPlay.setText(getResources().getString(R.string.play));
                    isPlay = false;
                }
                mRandomNum = mService.getRandomSta();
                if (mRandomNum == 0) {
                    mRandom.setText(getResources().getString(R.string.randomon));
                }else if (mRandomNum == 1) {
                    mRandom.setText(getResources().getString(R.string.randomoff));
                }
                curloop = mService.getLoopSta();
                if (curloop == 0) {
                    mLoop.setText(getResources().getString(R.string.loopon));
                }else if(curloop == 1){
                    mLoop.setText(getResources().getString(R.string.singlesloop));
                }else if(curloop == 2){
                    mLoop.setText(getResources().getString(R.string.loopoff));
                }
            }else {
                mName.setText(getResources().getString(R.string.unknow));
            }
            handler.postDelayed(this, 500);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        bindService();
    }

    //绑定service
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mService = ((MusicService.myBinder) service).getService();
        }
    };
    private void bindService() {
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
    }
    private void initView() {
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(MainActivity.this, MusicService.class);
        musicInfo = new MusicInfo();
        mName = (TextView) findViewById(R.id.name);
        mButton = (Button) findViewById(R.id.btn_list);
        mPre = (Button) findViewById(R.id.pre);
        mPlay = (Button) findViewById(R.id.play);
        mNext = (Button) findViewById(R.id.next);
        mRandom = (Button) findViewById(R.id.btn_random);
        mLoop = (Button) findViewById(R.id.loop);
        mSeekBar = (SeekBar) findViewById(R.id.time_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    if (title != null) {
                        mService.setUserTime(progress);
                    }
                }
            }
        });
        mCurtime = (TextView) findViewById(R.id.music_currenttime);
        mAllTime = (TextView) findViewById(R.id.music_alltime);
        //mCount = (TextView) findViewById(R.id.music_count);
        mAllTime.setText("00:00");
        mCurtime.setText("00:00");
        //mCount.setText("0/0");
        mButton.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mRandom.setOnClickListener(this);
        mLoop.setOnClickListener(this);
        musicCount = MusicList.getMusicInfos(this).size();
        handler.postDelayed(runnable, 500);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent2 =new Intent();
        switch (v.getId()) {
            //音乐列表
            case R.id.btn_list:
                Intent intent = new Intent(MainActivity.this, MusicList.class);
                startActivity(intent);
                break;
            //上一曲按钮事件
            case R.id.pre:
                intent2.setAction(Constant.PlayMsg.PRE_MSG);
                sendBroadcast(intent2);
                break;
            //播放按钮事件
            case R.id.play:
                //如果没有正在播放，设置广播信息为播放
                if (isPlay == false) {
                    intent2.setAction(Constant.PlayMsg.PLAY_MSG);
                    sendBroadcast(intent2);
                    isPlay = true;
                } else if (isPlay == true) {
                    //如果播放是正在播放则设置广播信息未暂停
                    intent2.setAction(Constant.PlayMsg.PAUSE_MSG);
                    sendBroadcast(intent2);
                    isPlay = false;
                }
                break;
            case R.id.next:
                intent2.setAction(Constant.PlayMsg.NEXT_MSG);
                sendBroadcast(intent2);
                break;
            case R.id.btn_random:
                if (mRandomNum == 0) {
                    intent2.setAction(Constant.PlayMsg.RANDOMON_MSG);
                    sendBroadcast(intent2);
                }else if (mRandomNum == 1){
                    intent2.setAction(Constant.PlayMsg.RANDOMOFF_MSG);
                    sendBroadcast(intent2);
                }
                break;
            case R.id.loop:
                if (curloop == 0) {
                    intent2.setAction(Constant.PlayMsg.LOOP_MSG);
                    sendBroadcast(intent2);
                }else if (curloop == 1) {
                    intent2.setAction(Constant.PlayMsg.SINGLESLOOP_MSG);
                    sendBroadcast(intent2);
                }else if (curloop == 2) {
                    intent2.setAction(Constant.PlayMsg.LOOPOFF_MSG);
                    sendBroadcast(intent2);
                }
                break;
            default:
                break;
        }
    }
    protected void onRestart() {
        super.onRestart();
    }
    public void onDestroy() {
        super.onDestroy();
    }
}

