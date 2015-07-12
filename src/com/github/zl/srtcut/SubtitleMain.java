package com.github.zl.srtcut;

import java.io.FileNotFoundException;

/**
 * Created by zl on 15/7/12.
 */
public class SubtitleMain {

    public static void main(String[] args) throws FileNotFoundException {
        SubtitleCutEngine.PRINT_LIST=true;

        SubtitleCutEngine engine=new SubtitleCutEngine();
        engine.setStartOffset(SubtitleCutEngine.parseTimeLine2Long("00:00:59,000"));
        engine.setEndOffset(SubtitleCutEngine.parseTimeLine2Long("00:02:00,000"));
        String path="/your/srt/file/path/test.srt";
        engine.setPath(path,null);
        engine.start();

    }

}
