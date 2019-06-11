package com.liuzozo.stepdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.liuzozo.stepdemo.utils.ConstantUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.liuzozo.stepdemo.PlanSetting_Activity.getContext;

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

                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    icon.setImageBitmap(photo);
                }
            }
        }

    }

    /**
     * 调用安卓的图片剪裁程序对用户选择的头像进行剪裁
     *
     */

    private void crop(String filePath) {
        // 隐式intent
        Intent intent = new Intent("com.android.camera.action.CROP");


        String[] dataStr = filePath.split("/");
        String fileTruePath = "sdcard0";
        fileTruePath = filePath.substring(0, 13) + fileTruePath + filePath.substring(24);

        Uri data = Uri.fromFile(new File(fileTruePath));

        Log.e("VirtualPath", filePath);
        Log.e("TruePath", data.getPath());

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
