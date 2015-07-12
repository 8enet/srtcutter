package com.github.zl.srtcut;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 文件编码
 * Created by zl on 15/7/12.
 */
public class FileCharsetDetector {

    /**
     * 默认的一些支持中文编码
     */
    private static final String[] DEFAULT_CHARSETS = {"UTF-8","GB18030","GBK","GB2312","Big5", "windows-1253","UTF-16","UTF-16BE","UTF-16LE"};

    private static final int BUFF_SIZE=4096;


    private static Charset detectCharset(File f, String... charsets) {
        ByteBuffer byteBuffer=null;
        FileInputStream fis=null;
        BufferedInputStream bis=null;
        try {
            fis=new FileInputStream(f);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[BUFF_SIZE];
            int len= bis.read(buffer);
            byteBuffer=ByteBuffer.wrap(buffer,0,len);
        }catch (Exception e){
            e.printStackTrace();
            new RuntimeException(e);
        }finally {
            try {
                if(bis != null){
                    bis.close();
                }
            }catch (Exception e){
            }
            try {
                if(fis != null){
                    fis.close();
                }
            }catch (Exception e){
            }
        }
        Charset charset = null;
        CharsetDecoder decoder =null;

        for (String charsetName : charsets) {
            charset=Charset.forName(charsetName);
            decoder= charset.newDecoder();
            decoder.reset();
           if(verifyDecoder(decoder,byteBuffer)){
               break;
           }else {
               charset=null;
           }
        }
        byteBuffer.clear();
        return charset;
    }

    /**
     * 验证编码
     * @param decoder
     * @param byteBuffer
     * @return
     */
    private static boolean verifyDecoder(CharsetDecoder decoder,ByteBuffer byteBuffer) {
        try {
            decoder.decode(byteBuffer);
            return true;
        } catch (CharacterCodingException e) {
        }
        return false;
    }

    /**
     * {@link FileCharsetDetector#getFileCharset(File, String...)}  }
     */
    public static Charset getFileCharset(File file){
        return detectCharset(file,DEFAULT_CHARSETS);
    }

    /**
     * 获取文本文件的编码格式
     * @param file 文件
     * @param charsets 要检查的编码集，有一些默认{@link FileCharsetDetector#DEFAULT_CHARSETS}
     * @return 如果文件编码再charsets里面着返回对于的charset，否则返回null
     */
    public static Charset getFileCharset(File file,String... charsets){
        return detectCharset(file,charsets);
    }

}