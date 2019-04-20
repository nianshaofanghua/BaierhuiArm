package com.tsminfo.android.baierhuiarm.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.tsminfo.android.baierhuiarm.config.ArmConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 读写文件
 */
public class PropertiesUtil {

    private final static String TAG = "PropertiesUtil";

    private Context mContext;
    private String mPath;
    private String mFile;
    private Properties mProp;
    private static PropertiesUtil mPropUtil = null;

    public static PropertiesUtil getInstance(Context context) {
        if (mPropUtil == null) {
            mPropUtil = new PropertiesUtil();
            mPropUtil.mContext = context;
            mPropUtil.mPath = ArmConstants.MACHINENO_FILEPATH;
            mPropUtil.mFile = ArmConstants.MACHINENO_FILENAME;
        }
        return mPropUtil;
    }


    public PropertiesUtil init() {
        try {
            File dir = new File(mPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, mFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream is = new FileInputStream(file);
            mProp = new Properties();
            mProp.load(is);
            is.close();
        } catch (Exception e) {
            Log.e("sss",e.getLocalizedMessage());
            e.printStackTrace();
        }
        return this;
    }

    public void commit() {
        try {
            File file = new File(mPath + "/" + mFile);
            OutputStream os = new FileOutputStream(file);
            mProp.store(os, "");
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mProp.clear();
    }

    public void open() {
        mProp.clear();
        try {
            File file = new File(mPath + "/" + mFile);
            InputStream is = new FileInputStream(file);
            mProp = new Properties();
            mProp.load(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeString(String name, String value) {
        mProp.setProperty(name, value);
    }

    public String readString(String name) {
        return mProp.getProperty(name, "");
    }

    public void writeInt(String name, int value) {
        mProp.setProperty(name, ""+value);
    }

    public int readInt(String name) {
        return Integer.parseInt(mProp.getProperty(name, "" + 0));
    }

    public void writeLong(String name, long value) {
        mProp.setProperty(name, ""+value);
    }

    public long readLong(String name) {
        return Long.parseLong(mProp.getProperty(name, "" + 0));
    }

    public void writeBoolean(String name, boolean value) {
        mProp.setProperty(name, "" + value);
    }

    public boolean readBoolean(String name, boolean defaultValue) {
        return Boolean.parseBoolean(mProp.getProperty(name, "" + defaultValue));
    }

    public void removeItem(String name) {
        mProp.remove(name);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void replaceItem(String name, String value) {
        mProp.replace(name,value);
    }
    public void clearItems() {
        mProp.clear();
    }
}
