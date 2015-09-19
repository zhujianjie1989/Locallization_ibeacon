package com.iot.locallization_ibeacon.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhujianjie on 2015/9/19.
 */
public class DatabaseContext extends ContextWrapper {
    public DatabaseContext(Context context){
        super( context );
    }
    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     * @param	name
     */
    @Override
    public File getDatabasePath(String name)
    {
        File sd= Environment.getExternalStorageDirectory();
        String path=sd.getPath()+"/DB";
        File file=new File(path);
        if(!file.exists())
            file.mkdir();

        File dbf=new File(path+"/"+"BLEdevice.db");


        return  dbf;

    }
    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param	name
     * @param	mode
     * @param	factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     *			  android.database.sqlite.SQLiteDatabase.CursorFactory,
     *			  android.database.DatabaseErrorHandler)
     * @param	name
     * @param	mode
     * @param	factory
     * @param	errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}