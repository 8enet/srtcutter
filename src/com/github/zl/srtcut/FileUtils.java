package com.github.zl.srtcut;

import java.io.Closeable;
import java.io.File;

/**
 * Created by zl on 15/7/12.
 */
public class FileUtils {

    /**
     * 关闭流
     * @param closeables
     */
    public static void closeAll(Closeable... closeables){
        if(closeables != null){
            for (Closeable closeable:closeables){
                try {
                    if(closeable != null){
                        closeable.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 文件名后缀前插入字符串，
     * @param srcFile 原始文件
     * @param str 要插入的字符串
     * @return 最终生产文件名
     */
    public static String insertSuffix(File srcFile,String str){
        StringBuilder stringBuilder=new StringBuilder(srcFile.getParent());
        stringBuilder.append(File.separator);

        final String name=srcFile.getName();
        final int suffixIndex=name.lastIndexOf('.');

        stringBuilder.append(name.substring(0,suffixIndex));
        stringBuilder.append(str);
        stringBuilder.append(name.substring(suffixIndex));
        return stringBuilder.toString();
    }


}
