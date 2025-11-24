package com.chuanyun.downloader.tabbar.me.ui;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.models.AppSettingsModel;
import com.chuanyun.downloader.utils.StorageHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {


    @BindView(R.id.download_num_tv)
    TextView downloadNumTv;

    @BindView(R.id.path_tv)
    TextView pathTv;

    @BindView(R.id.ring_notice_im)
    ImageView ringNoticeIm;

    @BindView(R.id.low_battery_im)
    ImageView lowBatteryIm;

    @BindView(R.id.status_notice_im)
    ImageView statusNoticeIm;

    @BindView(R.id.mobile_im)
    ImageView mobileIm;

    @BindView(R.id.withe_list_im)
    ImageView witheListIm;

    private AppSettingsModel settingsModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initData() {
        super.initData();

        settingsModel = AppSettingsModel.getSettingsModel();
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        downloadNumTv.setText(settingsModel.getDownloadTaskCount() + "");
        ringNoticeIm.setImageResource(getMipmapSrc(settingsModel.isRingNotice()));
        lowBatteryIm.setImageResource(getMipmapSrc(settingsModel.isLowBatteryDownload()));
        statusNoticeIm.setImageResource(getMipmapSrc(settingsModel.isStatusBarNotice()));
        mobileIm.setImageResource(getMipmapSrc(settingsModel.isStatusBarNotice()));
        witheListIm.setImageResource(getMipmapSrc(settingsModel.isWhiteList()));
        pathTv.setText(StorageHelper.createDownloadDir());
    }

    private int getMipmapSrc(boolean checked) {
        return checked ? R.mipmap.check_s : R.mipmap.check_n;
    }

    @OnClick(R.id.minus_im)
    public void downloadTaskMinusImAction() {
        int count = settingsModel.getDownloadTaskCount();
        if (count == 1) {
            return;
        }
        count --;
        downloadNumTv.setText(count + "");
        settingsModel.setDownloadTaskCount(count);
        AppSettingsModel.saveSettings(settingsModel);
    }

    @OnClick(R.id.sum_im)
    public void downloadTaskSumImAction() {
        int count = settingsModel.getDownloadTaskCount();
        if (count == 9) {
            return;
        }
        count ++;
        downloadNumTv.setText(count + "");
        settingsModel.setDownloadTaskCount(count);
        AppSettingsModel.saveSettings(settingsModel);
    }


    @OnClick(R.id.mobile_im)
    public void useMobileDataDownloadAction() {
        settingsModel.setUseMobileDownload(!settingsModel.isUseMobileDownload());
        mobileIm.setImageResource(getMipmapSrc(settingsModel.isUseMobileDownload()));
        AppSettingsModel.saveSettings(settingsModel);
    }

    @OnClick(R.id.low_battery_im)
    public void lowBatteryDownloadAction() {
        settingsModel.setLowBatteryDownload(!settingsModel.isLowBatteryDownload());
        lowBatteryIm.setImageResource(getMipmapSrc(settingsModel.isLowBatteryDownload()));
        AppSettingsModel.saveSettings(settingsModel);
    }


    @OnClick(R.id.ring_notice_im)
    public void ringNoticeAction() {
        settingsModel.setRingNotice(!settingsModel.isRingNotice());
        ringNoticeIm.setImageResource(getMipmapSrc(settingsModel.isRingNotice()));
        AppSettingsModel.saveSettings(settingsModel);
    }

    @OnClick(R.id.status_notice_im)
    public void statusBarNoticeAction() {
        settingsModel.setStatusBarNotice(!settingsModel.isStatusBarNotice());
        statusNoticeIm.setImageResource(getMipmapSrc(settingsModel.isStatusBarNotice()));
        AppSettingsModel.saveSettings(settingsModel);
    }


    @OnClick(R.id.withe_list_im)
    public void witheListSettingAction() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        boolean set = !settingsModel.isWhiteList();
        settingsModel.setWhiteList(set);
        witheListIm.setImageResource(getMipmapSrc(settingsModel.isWhiteList()));
        AppSettingsModel.saveSettings(settingsModel);
    }


    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
