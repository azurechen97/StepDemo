package com.liuzozo.stepdemo.utils;

import android.os.Environment;

import java.io.File;

/**
 * 保存 app 的 一些常量
 */
public class ConstantUtils {

    public String APP_NAME = "icon_app";
    public String AUTHOR = "陈傲雪";

    public static final String HEAD_PICTURE = "head_portrait.jpg";

    public static File bgFile = new File(Environment.getExternalStorageDirectory(), HEAD_PICTURE);


}
