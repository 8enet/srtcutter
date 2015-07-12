package com.github.zl.srtcut;

/**
 * 字幕信息单元
 *
 * -----------------------------------
 * |   0                              |  -----> 字幕索引，从0开始
 * |   00:01:52,900 --> 00:01:58,400  |  -----> 字幕时间轴信息hh:mm:ss:SSS ，开始 --> 结束
 * |   hello                          |  -----> 字幕显示内容
 * -----------------------------------
 * 每个字幕信息之间用\n分割
 *
 * Created by zl on 15/7/12.
 */
public class SubtitleItem implements Comparable<SubtitleItem>{

    /**
     * 字幕时间轴的分割标志
     */
    public static final String TIME_LINE_SEPARATOR=" --> ";

    /**
     * 字幕索引
     */
    private int index;

    /**
     * 开始时间 ms
     */
    private long startTimeLine;

    /**
     * 结束时间 ms
     */
    private long endTimeLine;

    /**
     * 时间跨度ms  endTimeLine-startTimeLine
     */
    private long timeLength;

    /**
     * 显示内容
     */
    private String subtitle;

    public SubtitleItem(){

    }

    public SubtitleItem(int index, long startTimeLine, long endTimeLine, String subtitle) {
        this.index = index;
        this.startTimeLine = startTimeLine;
        this.endTimeLine = endTimeLine;
        this.subtitle = subtitle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStartTimeLine() {
        return startTimeLine;
    }

    public void setStartTimeLine(long startTimeLine) {
        this.startTimeLine = startTimeLine;
    }

    public long getEndTimeLine() {
        return endTimeLine;
    }

    public void setEndTimeLine(long endTimeLine) {
        this.endTimeLine = endTimeLine;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * 计算时间间隔
     */
    public void calTimeLength(){
        this.timeLength=endTimeLine-startTimeLine;
    }

    /**
     * 字幕文件时间轴信息
     * @return
     */
    public String getFormatTimeLine(){
        return parse2String(startTimeLine)+TIME_LINE_SEPARATOR+parse2String(endTimeLine);
    }


    /**
     * 将时间转换成字幕文件的时间格式 hh:mm:ss:SSS
     * @param time
     * @return
     */
    public static final String parse2String(long time){
        StringBuilder sb=new StringBuilder();
        //h
        int ofs=60*60*1000;
        int d= (int) (time/ofs);
        if(d<10){
            sb.append('0').append(Character.forDigit(d,10));
        }else {
            sb.append(d);
        }
        sb.append(':');

        //min
        d= (int) ((time=time-d*ofs)/(ofs=60*1000));
        if(d<10){
            sb.append('0').append(Character.forDigit(d,10));
        }else {
            sb.append(d);
        }
        sb.append(':');

        //s
        d= (int) ((time=time-d*ofs)/(ofs=1000));
        if(d<10){
            sb.append('0').append(Character.forDigit(d,10));
        }else {
            sb.append(d);
        }
        sb.append(',');


        //ms
        d= (int) (time-d*ofs);
        if(d>=100){
            sb.append(d);
        }else if(d<100 && d>=10){
            sb.append('0').append(d);
        }else {
            sb.append('0').append('0').append(Character.forDigit(d,10));
        }

        return sb.toString();
    }


    @Override
    public String toString() {
        return "SubtitleItem{" +
                "index=" + index +
                ", startTimeLine=" + startTimeLine +
                ", endTimeLine=" + endTimeLine +
                ", timeLength=" + timeLength +
                ", subtitle='" + subtitle + '\'' +
                '}';
    }


    @Override
    public int compareTo(SubtitleItem o) {
        if(startTimeLine > o.startTimeLine){
            return 1;
        }else if(startTimeLine == o.startTimeLine){
            return 0;
        }
        return -1;
    }
}
