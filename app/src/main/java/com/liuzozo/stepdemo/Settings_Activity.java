package com.liuzozo.stepdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity implements View.OnClickListener {

    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    private CircleImageView icon;
    String cameraPath;// 拍摄头像照片时SD卡的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        initListener();

    }

    protected void initView() {
        icon = (CircleImageView) findViewById(R.id.iv_head_portrait);
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
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setType("image/*");

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(
                Environment
                        .getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        cameraPath = file.getAbsolutePath();

        Uri uri = FileProvider.getUriForFile(Settings_Activity.this,
                "com.liuzozo.stepdemo.fileprovider", file);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Intent chooser = Intent.createChooser(intent1, "选择头像");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
        startActivityForResult(chooser, 101);// --------------->101
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {// 获得未裁剪的照片 --------------->101
                String filePath;
                if (data != null) {
                    Uri uri = data.getData();
                    filePath = uri.getPath();
                } else {
                    // 相机拍照
                    filePath = cameraPath;
                }
                crop(filePath);// 裁剪
            }
            if (requestCode == 102) {// 裁剪点击确定后执行 --------------->102
                // 获得了系统截图程序返回的截取后的图片
//                    final Bitmap bitmap = data.getParcelableExtra("data");
//                    // 上传前，要将bitmap保存到SD卡
//                    // 获得保存路径后，再上传
//                    final File file = new File(
//                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                            System.currentTimeMillis() + ".jpg");
//                    OutputStream stream = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                // 上传头像数据并显示头像操作
                // showProgressDialog();
                // uploading();
                // Glide.with(SetActivity.this).load(file.getPath()).into(icon);
                // closeProgressDialog();
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    icon.setImageBitmap(photo);
                }
            }
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 调用安卓的图片剪裁程序对用户选择的头像进行剪裁
     *
     * @param filePath 用户选取的头像在SD上的地址
     */
    private void crop(String filePath) {
        // 隐式intent
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri data = FileProvider.getUriForFile(Settings_Activity.this,
                "com.liuzozo.stepdemo.fileprovider", new File(filePath));
        // 设置剪裁数据 150*150
        intent.setDataAndType(data, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", true);
        intent.putExtra("return-data", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        startActivityForResult(intent, 102);// --------------->102
    }


}
