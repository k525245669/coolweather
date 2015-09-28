package com.zhukun.coolweather.db;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/9/15.
 */
public class RestoreData {
    private static String rootDirectory = "/data/data/com.zhukun.coolweather/databases/";
    private static final String DATABASE_PATH = "/data/data/com.zhukun.coolweather/databases/";
    private static final String DATABASE_FILENAME = "cool_weather";
    private static final String DATABASE_FILE2NAME = "cool_weather-journal";

    public  static void createDatabase(Context context) throws IOException {
        try
        {
            // 获得.db文件的绝对路径
            String databaseFilename = DATABASE_PATH + DATABASE_FILENAME;
            String databaseFilename2 = DATABASE_PATH +DATABASE_FILE2NAME;
            File dir = new File(rootDirectory);
            // 如果目录不存在，创建这个目录
            if (!dir.exists())
                dir.mkdir();
            CopyFile(databaseFilename, "cool_weather", context);
            CopyFile(databaseFilename2, "cool_weather-journal", context);
        }
        catch (Exception e){
        }
    }

    private static void CopyFile(String dbFile, String assetFile, Context context) throws IOException {
        if (!(new File(dbFile)).exists()){
            // 获得封装.db文件的InputStream对象
            InputStream is = context.getAssets().open(assetFile);
            FileOutputStream fos = new FileOutputStream(dbFile);
            byte[] buffer = new byte[7168];
            int count = 0;
            // 开始复制.db文件
            while ((count = is.read(buffer)) > 0){
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        }

    }
}
