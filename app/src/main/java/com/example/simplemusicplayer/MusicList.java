package com.example.simplemusicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/12/23 0023.
 */

public class MusicList extends Activity implements View.OnClickListener {

    //private static final String TAG = "ln";
    private ListView mListView;
    MusicInfo musicInfo;
    MusicAdapter adapter;
    private MusicService mService;
    private Intent serviceIntent;
    private Button mReturn, mPreviou, mPlayPause, mNext;
    private TextView mTitle;
    Handler handler = new Handler(){};
    Runnable runnable = new Runnable() {
        String title = null;
        @Override
        public void run() {
            // TODO Auto-generated method stub
            title = mService.getTitle();
            String textView = (String) mTitle.getText();
            if (title != null) {
                if (!textView.equals(title)) {
                    mTitle.setText(title);
                }
                int imsg = mService.getPlaySta();
                if (imsg == 0) {
                    mPlayPause.setText(getResources().getString(R.string.pause));
                }else if (imsg == 1) {
                    mPlayPause.setText(getResources().getString(R.string.play));
                }
            }
            handler.postDelayed(this, 500);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_list);
        Log.i("ln", "MusicList onCreate()");
        initView(this);
        bindService();
        handler.postDelayed(runnable, 500);
    }

    void initView(Context context) {
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(new MusicItemCLickListener());
        musicInfo = new MusicInfo();
        adapter = new MusicAdapter(context, getMusicInfos(context));
        mListView.setAdapter(adapter);
        mService = new MusicService();

        mReturn = (Button) findViewById(R.id.btn_return);
        mPreviou = (Button) findViewById(R.id.btn_pre);
        mPlayPause = (Button) findViewById(R.id.btn_playpause);
        mNext = (Button) findViewById(R.id.btn_next);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mReturn.setOnClickListener(this);
        mPreviou.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mTitle.setText(getResources().getString(R.string.unknow));

    }

    /**
     * 这里是读取手机内存卡里的音乐信息
     * @param context
     * @return
     */
    public static List<MusicInfo> getMusicInfos(Context context) {
        Cursor mCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
        for (int i = 0; i < mCursor.getCount(); i++) {
            MusicInfo musicInfo = new MusicInfo();
            mCursor.moveToNext();
            int id = i;
            String title = mCursor.getString((mCursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题
            String artist = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家
            long duration = mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长
            int isMusic = mCursor.getInt(mCursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
            String url = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA)); // 路径
            if (isMusic != 0) {
                musicInfo.setId(id);
                musicInfo.setTitle(title);
                musicInfo.setSinger(artist);
                musicInfo.setTime(duration);
                musicInfo.setUrl(url);
                musicInfos.add(musicInfo);
            }
        }
        return musicInfos;
    }

    /*
     * 这是listview item 的点击事件 点击后调用service的musicplay()方法 通过传入的id来播放音乐
     */
    private class MusicItemCLickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            if (getMusicInfos(getApplicationContext()) != null) {
                MusicInfo musicInfo = getMusicInfos(getApplicationContext()).get(position);
                int  id2 = musicInfo.getId();
                mService.musicplay(id2);
            }
        }
    }
    private void bindService(){
        serviceIntent = new Intent(MusicList.this,MusicService.class);
        bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
    }

    public ServiceConnection connection = new ServiceConnection() {

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

    protected void onRestart(){
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_pre:
                intent.setAction(Constant.PlayMsg.PRE_MSG);
                sendBroadcast(intent);
                break;
            case R.id.btn_playpause:
                intent.setAction(Constant.PlayMsg.PLAYPAUSE_MSG);
                sendBroadcast(intent);
                break;
            case R.id.btn_next:
                intent.setAction(Constant.PlayMsg.NEXT_MSG);
                sendBroadcast(intent);
                break;
            case R.id.btn_return:
                Intent intent2 = new Intent(MusicList.this,MainActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
