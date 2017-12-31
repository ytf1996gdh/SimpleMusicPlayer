package com.example.simplemusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/12/31 0031.
 */

public class MusicAdapter extends BaseAdapter{

    private Context mContext;
    private List<MusicInfo> musicInfos;
    private MusicInfo musicInfo;

    int mPosition;

    public MusicAdapter(Context context,List<MusicInfo> musicInfos){
        this.mContext = context;
        this.musicInfos = musicInfos;
    }

    public int getPosition(){
        return mPosition;
    }

    public void setPosition(int mPosition){
        this.mPosition = mPosition;
    }
    @Override
    public int getCount() {
        // 决定listview有多少个item
        return musicInfos.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music, null);
            holder.title = (TextView)convertView.findViewById(R.id.music_title);
            holder.singer = (TextView)convertView.findViewById(R.id.music_singer);
            holder.time = (TextView)convertView.findViewById(R.id.music_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        musicInfo =musicInfos.get(position);
        holder.title.setText(musicInfo.getTitle());
        holder.singer.setText(musicInfo.getSinger());
        holder.time.setText(Format(musicInfo.getTime()));
        return convertView;
    }

    class ViewHolder {
        public TextView title; //音乐名
        public TextView singer; // 歌手名
        public TextView time; //时间
    }
    /**
     * 时间转化 把音乐时间的long型数据转化为“分：秒”
     */
    public static String Format(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        String hms = format.format(time);
        return hms;
    }
}
