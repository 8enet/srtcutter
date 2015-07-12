package com.github.zl.srtcut;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 字幕剪切
 * Created by zl on 15/7/12.
 */
public class SubtitleCutEngine {

    /* 输出list */
    public static boolean PRINT_LIST=false;

    /**
     * 原始srt文件路径
     */
    private String srtFilePath;

    /**
     * 输出srt文件路径
     */
    private String outPath;
    /**
     * srt文件编码
     */
    private Charset charset;
    /**
     * 开始剪切的偏移量ms，如00:01:30:456=90456 可以用 {@link SubtitleCutEngine#parseTimeLine2Long(String)} 方法计算得到
     */
    private long offset;

    public String getSrtFilePath() {
        return srtFilePath;
    }

    public String getOutPath() {
        return outPath;
    }

    /**
     * 设置字幕路径
     * @param srcPath {@link SubtitleCutEngine#srtFilePath}
     * @param outPath {@link SubtitleCutEngine#outPath} 如果为null，则再当前目录下新建
     */
    public void setPath(String srcPath, String outPath) {
        this.srtFilePath = srcPath;
        this.outPath = outPath;
        if (this.outPath == null) {
            this.outPath=FileUtils.insertSuffix(new File(srtFilePath), "_cut");
        }
    }

    public Charset getCharset() {
        return charset;
    }

    /**
     * 设置字幕文件读取和生成的字符编码
     * @param charset {@link SubtitleCutEngine#charset}
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public long getOffset() {
        return offset;
    }

    /**
     * 设置时间轴的偏移量
     * @param offset {@link SubtitleCutEngine#offset}
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * 开始剪切字幕
     */
    public void start(){
        File file=new File(srtFilePath);
        if(!file.exists()){
            throw new IllegalArgumentException(" srt subtiles file  "+srtFilePath+"     not found !!");
        }
        //先检查文件编码
        if(charset == null){
            charset=FileCharsetDetector.getFileCharset(file);
        }
        if(charset == null){
            charset=Charset.defaultCharset();
        }
        System.out.println("the charset is "+charset);
        List<SubtitleItem> items=readAllItems(file);
        offset(offset,items);
        exportSubtitle(items);
    }

    /**
     * 读取原始srt文件中的字幕信息
     * @param file 原始的srt文件
     * @return
     */
    private List<SubtitleItem> readAllItems(File file){
        List<SubtitleItem> items=new ArrayList<>();
        FileInputStream fis=null;
        InputStreamReader isr=null;
        BufferedReader reader=null;
        try {
            fis=new FileInputStream(file);
            isr=new InputStreamReader(fis,charset);
            reader=new BufferedReader(isr);
            int s=0; //最小字幕信息
            SubtitleItem item=null;
            String line;
            while ((line=reader.readLine()) != null){
                line=line.trim();
                if(line.length() == 0){
                    //此处表示3行单位的字幕读完了，进行下一轮的读取
                    items.add(item);
                    s=0;
                }else {
                    switch (s){
                        case 0:
                            item=new SubtitleItem();
                            item.setIndex(Integer.parseInt(line));
                            break;
                        case 1:
                            setTimeLines(line, item);
                            break;
                        case 2:
                            item.setSubtitle(line);
                            break;
                    }
                    ++s;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            FileUtils.closeAll(reader, isr, fis);
        }
        return items;
    }


    /**
     * 计算剪切
     * @param offset
     * @param items
     */
    private  void offset(long offset,List<SubtitleItem> items){
        Collections.sort(items);
        //得到所有数据中和偏移量最近的索引
        final int stIndex = Math.abs(Collections.binarySearch(items,new SubtitleItem(0,offset,0,null)));
        System.out.println("------------  find index   "+stIndex);
        SubtitleItem item=null;
        long st=0;
        long firstOffset=-1; //记录最近点与偏移量的间隔
        int size=items.size();
        for (int i=0,j=0;i<size;i++,j++){
            if(j<stIndex){
                //将最近点之前的数据删除
                items.remove(i);
                i--;
            }else {
                size=items.size();
                item=items.get(i);
                if(j==stIndex && firstOffset==-1){
                    firstOffset=item.getStartTimeLine()-offset;
                }
                if(j == stIndex){
                    st=firstOffset;
                }else {
                    st=item.getStartTimeLine()-offset-firstOffset;
                }
                item.setStartTimeLine(st);
                item.setEndTimeLine(st+item.getTimeLength());
                item.setIndex(j-stIndex);
            }
        }
        printList(items);
    }

    /**
     * 设置时间轴毫秒
     * @param line
     * @param item
     */
    private void setTimeLines(String line, SubtitleItem item){
        //00:00:03,600 --> 00:00:05,066
        //分割起始时间
        final String[] strings = line.split(SubtitleItem.TIME_LINE_SEPARATOR);
        item.setStartTimeLine(parseTimeLine2Long(strings[0].trim()));
        item.setEndTimeLine(parseTimeLine2Long(strings[1].trim()));
        item.calTimeLength();
    }

    /**
     * 将字幕时间轴信息转换成对于的毫秒时间
     * @param time 需要转换的时间 如00:10:04,366
     * @return
     */
    public static long parseTimeLine2Long(String time){
        long ret=0;
        ret=60*60*1000*Integer.parseInt(time.substring(0,2));
        ret+=60*1000*Integer.parseInt(time.substring(3,5));
        ret+=1000*Integer.parseInt(time.substring(6,8));
        ret+=Integer.parseInt(time.substring(9,12));
        return ret;
    }

    /**
     * 导出剪切后的字幕
     * @param items 剪切后在字幕
     */
    private void exportSubtitle(List<SubtitleItem> items){
        File file=new File(outPath);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos=null;
        OutputStreamWriter osw=null;
        BufferedWriter writer=null;
        try {
            System.out.println("start  export size "+items.size());
            fos=new FileOutputStream(file);
            osw=new OutputStreamWriter(fos,charset);
            writer=new BufferedWriter(osw);
            for (SubtitleItem item:items){
                writer.write(String.valueOf(item.getIndex()));
                writer.write("\n");
                writer.write(item.getFormatTimeLine());
                writer.write("\n");
                writer.write(item.getSubtitle());
                writer.write("\n");
                writer.write("\n");
            }
            writer.flush();
            System.out.println("export success !! save in "+file.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            FileUtils.closeAll(writer, osw, fos);
        }

    }


    /**
     * 输出集合数据
     * @param collection
     */
    private static void printList(Collection collection){
        if(!PRINT_LIST){
            return;
        }
        if(collection != null){
            for (Object obj:collection){
                System.out.println(obj);
            }
        }
    }
}
