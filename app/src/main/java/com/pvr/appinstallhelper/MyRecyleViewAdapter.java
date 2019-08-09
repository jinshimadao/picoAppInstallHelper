package com.pvr.appinstallhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

/**
 * Created by goodman.ye on 2019/8/8.
 */
public class MyRecyleViewAdapter extends RecyclerView.Adapter<MyRecyleViewAdapter.MyHolder> {
    public Context mContext;
    public List<APKInfo> mAPKInfoList;

    public MyRecyleViewAdapter(Context context, List<APKInfo> list) {
        mContext = context;
        mAPKInfoList = list;
    }

    @NonNull
    @Override
    public MyRecyleViewAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.apk_item, viewGroup, false);
        MyHolder mMyHolder = new MyHolder(v);
        return mMyHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyleViewAdapter.MyHolder myHolder, int i) {
        APKInfo apkInfo = mAPKInfoList.get(i);
        //myHolder.mIvAppIcon.setBackground(apkInfo.getAPKIcon());
        myHolder.mIvAppIcon.setImageDrawable(apkInfo.getAPKIcon());
        myHolder.mTvAppName.setText(apkInfo.getAPKName());
        myHolder.mTvInstallResult.setText(apkInfo.getmInstallResult());
        if (apkInfo.ismInstalling()) {
            myHolder.mAVInstallationState.show();
            myHolder.mTvInstallResult.setVisibility(View.INVISIBLE);
        } else {
            myHolder.mAVInstallationState.hide();
            myHolder.mTvInstallResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mAPKInfoList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        CustomRoundAngleImageView mIvAppIcon;
        TextView mTvAppName;
        TextView mTvInstallResult;
        AVLoadingIndicatorView mAVInstallationState;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mIvAppIcon = itemView.findViewById(R.id.iv_app_icon);
            mTvAppName = itemView.findViewById(R.id.tv_app_name);
            mTvInstallResult = itemView.findViewById(R.id.tv_app_install_result);
            mAVInstallationState = itemView.findViewById(R.id.AVLIV_installing);
        }
    }
}
