package com.liuzozo.stepdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liuzozo.stepdemo.utils.ConstantUtils;
import com.liuzozo.stepdemo.utils.PermissionUtils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity implements View.OnClickListener {


    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    private CircleImageView icon;
    String cameraPath;// 拍摄头像照片时SD卡的路径

    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PermissionUtils.verifyStoragePermissions(this);

        editor = getSharedPreferences(
                "settings", MODE_PRIVATE).edit();
        settings = getSharedPreferences(
                "settings", MODE_PRIVATE);

        initView();
        initListener();

    }

    protected void initView() {
        icon = (CircleImageView) findViewById(R.id.iv_head_portrait);
        if (settings.contains("portrait")) {
            String path = settings.getString("portrait", null);
            Uri uri = Uri.parse(path);

            Log.e("savedUri", uri.toString());

            RequestListener mRequestListener = new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    Log.e("GlideFailed", "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
                    icon.setImageResource(R.mipmap.icon_app);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    Log.e("GlideFailed", "model:" + model + " isFirstResource: " + isFirstResource);
                    return false;
                }
            };


            Glide.with(this)
                    .load(uri)
                    .placeholder(R.mipmap.man_pic)
                    .error(R.mipmap.icon_app)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(300, 300)//指定图片大小
                    .listener(mRequestListener)
                    .into(icon);
        }


    }

    protected void initListener() {
        findViewById(R.id.btn_portrait).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_portrait:
                setAvatar();
                break;
            default:
        }
    }

    private void setAvatar() {
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.setType("image/*");

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(), ConstantUtils.HEAD_PICTURE);
        Uri uri = Uri.fromFile(file);

        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Intent chooser = Intent.createChooser(intent1, "选择头像");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
        startActivityForResult(chooser, 101);// --------------->101
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            if (requestCode == 101) {// 获得未裁剪的照片 --------------->101
//
//                if (data != null) {
//                    Log.e("uri",data.getData().getPath());
//                    crop(data.getData());
//                } else {
//                    // 相机拍照
//                    crop(ConstantUtils.bgFile);
//                }
//
//                System.out.println("---111111111------");
//
//            }
            if (requestCode == 101) {// 裁剪点击确定后执行 --------------->102
                // 获得了系统截图程序返回的截取后的图片
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                } else {
                    // 相机拍照
                    uri = Uri.fromFile(ConstantUtils.bgFile);
                }

                RequestListener mRequestListener = new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        Log.e("Glide", "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
                        icon.setImageResource(R.mipmap.icon_app);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("Glide", "model:" + model + " isFirstResource: " + isFirstResource);
                        return false;
                    }
                };

                if (uri != null) {
                    editor.putString("portrait", uri.toString());
                    editor.apply();
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.mipmap.man_pic)
                            .error(R.mipmap.icon_app)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(300, 300)//指定图片大小
                            .addListener(mRequestListener)
                            .into(icon);
                }
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    Bitmap photo = extras.getParcelable("data");
//                    icon.setImageBitmap(photo);
//                }
            }
        }

    }


//    /**
//     * 调用安卓的图片剪裁程序对用户选择的头像进行剪裁
//     *
//     */
//
//    private void crop(Uri uri) {
//        Log.e("URI", uri.getPath());
//        // 隐式intent 
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        // 设置剪裁数据 150*150 
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", true);
//        intent.putExtra("return-data", true);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        startActivityForResult(intent, 102);
//    }
//
//    private void crop(File file) {
//        Log.e("FILE", file.getPath());
//        // 隐式intent 
//        Uri uri = FileProvider.getUriForFile(this, "com.liuzozo.stepdemo.fileprovider", file);
//        // 隐式intent 
//        Intent intent = new Intent("com.android.camera.action.CROP");
//
//        // 设置剪裁数据 150*150 
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", true);
//        intent.putExtra("return-data", true);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        startActivityForResult(intent, 102);
//    }


}
