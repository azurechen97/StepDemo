package com.aoxue.stepdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.bean.HomeListBean;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * <pre>
 *     author : lisheny
 *     e-mail : 1020044519@qq.com
 *     time   : 2017/07/10
 *     desc   : 聊天信息列表适配器
 *     version: 1.0
 * </pre>
 */
public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {

    private HomeListBean homeListBeen;
    private List<HomeListBean> mDatas;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;

    private SharedPreferences settings;

    public HomeListAdapter(Context context, List<HomeListBean> datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setIcon(ImageView icon) {

        settings = mContext.getSharedPreferences(
                "settings", MODE_PRIVATE);
        if (settings.contains("portrait")) {
            String path = settings.getString("portrait", null);
            Uri uri = Uri.parse(path);

            Log.e("savedUri", uri.toString());


            Glide.with(mContext)
                    .load(uri)
                    .placeholder(R.mipmap.akkarin)
                    .error(R.mipmap.icon_app)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(100, 100)//指定图片大小
                    .into(icon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_home_list, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        homeListBeen = mDatas.get(position);

        ImageView icon = holder.ivHomeRightIc;

        setIcon(icon);

        //test 仅供显示
        if (position == 0) {
            holder.tvHomeTime.setVisibility(View.GONE);
        }
        if (homeListBeen.getTeller() == 2) {
            holder.tvHomeLeftContent.setVisibility(View.GONE);
            holder.ivHomeLeftIc.setVisibility(View.GONE);
            holder.tvHomeRightContent.setVisibility(View.VISIBLE);
            holder.ivHomeRightIc.setVisibility(View.VISIBLE);
            holder.tvHomeRightContent.setText(homeListBeen.getContent());
        } else if (homeListBeen.getTeller() == 1) {
            holder.tvHomeLeftContent.setVisibility(View.VISIBLE);
            holder.ivHomeLeftIc.setVisibility(View.VISIBLE);
            holder.tvHomeRightContent.setVisibility(View.GONE);
            holder.ivHomeRightIc.setVisibility(View.GONE);
            holder.tvHomeLeftContent.setText(homeListBeen.getContent());
        } else {
            //dosomething
        }

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_home_time)
        TextView tvHomeTime;
        @InjectView(R.id.iv_home_left_ic)
        CircleImageView ivHomeLeftIc;
        @InjectView(R.id.tv_home_left_content)
        TextView tvHomeLeftContent;
        @InjectView(R.id.iv_home_right_ic)
        CircleImageView ivHomeRightIc;
        @InjectView(R.id.tv_home_right_content)
        TextView tvHomeRightContent;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
