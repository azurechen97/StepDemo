package com.liuzozo.stepdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.liuzozo.stepdemo.adapter.HomeListAdapter;
import com.liuzozo.stepdemo.bean.HomeListBean;
import com.liuzozo.stepdemo.utils.TuLinConstants;
import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.liuzozo.stepdemo.utils.TuLinConstants.MSG_SPEECH_START;
import static com.liuzozo.stepdemo.utils.TuLinConstants.TURING_APIKEY;
import static com.liuzozo.stepdemo.utils.TuLinConstants.TURING_SECRET;

/**
 * 调用图灵API 和 图灵机器聊天, 参考ppt 备注
 * 制作类似QQ对话框的界面
 */
public class TuLinTalk_Activity extends AppCompatActivity {

    private final String TAG = TuLinTalk_Activity.class.getSimpleName();
    @InjectView(R.id.recy_message_list)
    RecyclerView recyclerMessageList;
    @InjectView(R.id.et_input)
    EditText etInput;
    @InjectView(R.id.btn_send)
    ImageButton btnSend;

    private List<HomeListBean> mData;
    private HomeListAdapter adapter;

    private TuringManager mTuringManager;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SPEECH_START:
                    addData(TuLinConstants.APP, (String) msg.obj);
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tulin_talk);

        ButterKnife.inject(this);
        init();

        if (mData == null) {
            mData = new ArrayList<HomeListBean>();
        }

        addData(TuLinConstants.APP, "你好，请问有什么可以帮到你？");
        adapter = new HomeListAdapter(TuLinTalk_Activity.this, mData);
        recyclerMessageList.setLayoutManager(new LinearLayoutManager(TuLinTalk_Activity.this));
        recyclerMessageList.setAdapter(adapter);
    }

    /**
     * 添加讲话内容到列表
     *
     * @param teller
     * @param content
     */
    private void addData(int teller, String content) {
        HomeListBean homeListBean = new HomeListBean();
        homeListBean.setTeller(teller);
        homeListBean.setContent(content);
        mData.add(homeListBean);
        if (null != adapter) {
            adapter.notifyItemInserted(mData.size() - 1);
            recyclerMessageList.scrollToPosition(mData.size() - 1);
            etInput.setText("");
            etInput.clearFocus();
        }
    }

    /**
     * 初始化turingSDK
     */
    private void init() {
        mTuringManager = new TuringManager(this, TURING_APIKEY, TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);

    }

    /**
     * 网络请求回调
     */
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {

        @Override
        public void onSuccess(String result) {
            if (result != null) {
                try {
                    Log.d(TAG, "result" + result);
                    JSONObject result_obj = new JSONObject(result);
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        mHandler.obtainMessage(MSG_SPEECH_START,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onFail(int code, String error) {
            Log.d(TAG, "onFail code:" + code + "|error:" + error);
            mHandler.obtainMessage(MSG_SPEECH_START, "网络慢脑袋不灵了").sendToTarget();
        }
    };

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        mTuringManager.requestTuring(etInput.getText().toString().trim());
        addData(TuLinConstants.USER, etInput.getText().toString().trim());
        hideSoftInput(TuLinTalk_Activity.this);
    }


    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    public static void hideSoftInput(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


