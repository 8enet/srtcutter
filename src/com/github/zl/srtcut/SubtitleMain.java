package com.github.zl.srtcut;

import java.io.FileNotFoundException;

/**
 * Created by zl on 15/7/12.
 */
public class SubtitleMain {
    private static final String PATH="/your/srt/file/path/test.srt";


    public static void main(String[] args) throws FileNotFoundException {
        SubtitleCutEngine.PRINT_LIST=false;
        SubtitleCutEngine engine=new SubtitleCutEngine();
        engine.setOffset(SubtitleCutEngine.parseTimeLine2Long("00:30:21,030"));
        engine.setPath(PATH,null);
        engine.start();

    }

}
