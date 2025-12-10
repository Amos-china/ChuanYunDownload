package com.chuanyun.downloader.tabbar.home.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.tools.aplayer.APlayerActivity;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.bat.UmengKeyUtils;
import com.chuanyun.downloader.core.TTDownloadService;
import com.chuanyun.downloader.dao.DownloadDao;
import com.chuanyun.downloader.dao.TorrentDao;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.login.popup.UserLoginPopupView;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.AppSettingsModel;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.popup.OnlinePlayPasrePopup;
import com.chuanyun.downloader.tabbar.home.adapter.TorrentDetailAdapter;
import com.chuanyun.downloader.tabbar.me.ui.VipCenterActivity;
import com.chuanyun.downloader.utils.NetworkUtils;
import com.chuanyun.downloader.eventBusModel.TorrentManagerEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

public class TorrentDetailActivity extends BaseActivity {

    @BindView(R.id.torrent_detail_rv)
    RecyclerView torrentRv;

    @BindView(R.id.file_num_tv)
    TextView fileNumTv;

    @BindView(R.id.select_num_tv)
    TextView selectNumTv;

    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;

    @BindView(R.id.select_all_tv)
    TextView selectAllTv;

    @BindView(R.id.rg_view)
    RadioGroup radioGroup;

    @BindView(R.id.collect_tv)
    SuperTextView collectTv;

    @BindView(R.id.ms_tv)
    TextView msTv;

    private TorrentDetailAdapter torrentDetailAdapter;

    private TTTorrentInfo torrentInfo;

    private DownloadDao downloadDao;

    private boolean showVideo = true;

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_torrent_detail;
    }


    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);

        downloadDao = App.getApp().getAppDataBase().downloadDao();
        Intent intent = getIntent();
        torrentInfo = (TTTorrentInfo) intent.getSerializableExtra(TTTorrentInfo.INTENT_TTTORRENT_INFO);
        torrentInfo.setCurrentPlayIndex(-1);
        createData();
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        msTv.setVisibility(View.VISIBLE);
        msTv.setText(indexModel.getJfxhsm());

        if (UserLoginManager.checkUserLogin()) {
            LoginModel loginModel = UserLoginManager.getLoginInfo();
            if (loginModel.getInfo().getVipStatus() == 1) {
                msTv.setVisibility(View.GONE);
            }
        }

        fileNumTv.setText("文件数:" + torrentInfo.getFileCount());
        fileSizeTv.setText("总大小:" + torrentInfo.getSize());
        selectNumTv.setText("已选择:" + 0);

        TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
        Disposable disposable = torrentDao.getTorrentInfoBy(torrentInfo.getHash())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> {
                    torrentInfo.setIsLike(info.getIsLike());
                    configCollectIm();
                },throwable -> {
                    Log.i(TAG, "configCollectIm: " + throwable.getMessage());
                });
        addDisposable(disposable);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        torrentDetailAdapter = new TorrentDetailAdapter(null);
        torrentRv.setLayoutManager(linearLayoutManager);
        torrentRv.setAdapter(torrentDetailAdapter);

        torrentDetailAdapter.addEmptyView();

        torrentDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            TorrentFileInfoModel fileModel = torrentDetailAdapter.getData().get(position);

            if (!fileModel.isDownload()) {
                fileModel.setSelect(!fileModel.isSelect());
                adapter.notifyItemChanged(position,TorrentDetailAdapter.RELOAD_SELECT);
                setSelectTotalNumUi();
            }
        });

        torrentDetailAdapter.addChildClickViewIds(R.id.check_im,R.id.play_tv);
        torrentDetailAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            TorrentFileInfoModel fileModel = torrentDetailAdapter.getData().get(position);
            if (view.getId() == R.id.check_im) {
                if (!fileModel.isDownload()) {
                    fileModel.setSelect(!fileModel.isSelect());
                    adapter.notifyItemChanged(position,TorrentDetailAdapter.RELOAD_SELECT);
                    setSelectTotalNumUi();
                }
            } else {
                if (TTDownloadService.getInstance().checkFileDownload(fileModel)) {
                    checkUser(fileModel);
                }else {
                    showToast("种子文件已被删除");
                }
            }
        });

        radioGroup.setOnCheckedChangeListener((group,checkedId) -> {
            showVideo = checkedId == R.id.video_rb;
            createData();
        });
    }

    private void checkUser(TorrentFileInfoModel torrentFileInfoModel) {
        if (UserLoginManager.checkUserLogin()) {
            showDiaLog("");
            Disposable disposable = userEngine.fen(1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rootModel -> {
                        hideLoadingDialog();
                        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                            torrentInfo.setCurrentPlayIndex(torrentFileInfoModel.getIndex());
                            playFileModel(torrentFileInfoModel);
                        }else {
                            showBuyVipAlert();
                        }
                    },throwable -> {
                        hideLoadingDialog();
                        showToast(throwable.getMessage());
                    });
            addDisposable(disposable);

        }else {
            //未登录
            showLoginView();
        }
    }

    private void configCollectIm() {
        String str = torrentInfo.getIsLike() == 0 ? "收藏" : "取消收藏";
        collectTv.setText(str);
    }

    private void playFileModel(TorrentFileInfoModel torrentFileInfoModel) {
        String videoPath = "";
        if (!torrentFileInfoModel.isDownload()
                || torrentFileInfoModel.getDownloadStatus() != TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
            NetworkUtils.showMobileDataToast(this);
        }
        if (torrentFileInfoModel.isDownload()) {

            if (torrentFileInfoModel.getDownloadStatus() != 2) {
                List<TorrentFileInfoModel> fileInfoModelList = TTDownloadService.getInstance().getFileInfoModelList();
                TorrentFileInfoModel taskModel = null;
                for (TorrentFileInfoModel fileModel : fileInfoModelList) {
                    if (fileModel.getInfoId().equals(torrentFileInfoModel.getInfoId())) {
                        taskModel = fileModel;
                        break;
                    }
                }

                if (taskModel == null) {
                    videoPath = TTDownloadService.getInstance().getVideFileUrl(torrentFileInfoModel);
                    playVideo(videoPath,torrentFileInfoModel.getName());
                    return;
                }

                if (taskModel.getDownloadStatus() != 1) {
                    TTDownloadService.getInstance().downloadTorrent(taskModel);
                }
            }
            videoPath = TTDownloadService.getInstance().getVideFileUrl(torrentFileInfoModel);
            playVideo(videoPath,torrentFileInfoModel.getName());
        }else {
            videoPath = TTDownloadService.getInstance().getOnlinePlayUrl(torrentFileInfoModel);
            String tempPath = videoPath;
            TorrentFileInfoModel tempModel = torrentFileInfoModel;

            OnlinePlayPasrePopup logView = new OnlinePlayPasrePopup(this);
            logView.setDelayDismiss(20,along -> {
                if (along >= 5) {
                    if (getOnlineTaskSpeed(tempModel) > 0) {
                        logView.disposeTimer();
                        logView.dismiss();
                        playVideo(tempPath,tempModel.getName());
                    }
                }
            },()->{
                logView.dismiss();
                if (getOnlineTaskSpeed(tempModel) == 0) {
                    TTDownloadService.getInstance().deleteTempTask(tempModel);
                    showToast("资源解析失败!");
                } else {
                    playVideo(tempPath,tempModel.getName());
                }
            });

            showCustomPopupView(logView,false);

        }
    }

    private long getOnlineTaskSpeed(TorrentFileInfoModel fileInfoModel) {
        return TTDownloadService.getInstance().getDownloadTaskSpeed(fileInfoModel.getTaskId());
    }

    private void playVideo(String url, String title) {
        APlayerActivity.playVideo(TorrentDetailActivity.this,title,url,false,0,1);
//        VideoPlayerActivity.openPlayer(this,url,title);
    }


    private void createData() {
        Disposable disposable = Observable.fromIterable(torrentInfo.getFileModelList())
                .subscribeOn(Schedulers.io())
                .flatMapSingle(fileInfoModel ->
                        downloadDao.getDownloadTaskInfo(fileInfoModel.getInfoId())
                                .onErrorReturnItem(fileInfoModel)
                )
                .map(fileInfoModel -> {
                    if (fileInfoModel.getFileSuffixType() == 1 && !fileInfoModel.isDownload()) {fileInfoModel.setSelect(true);}
                    return fileInfoModel;
                })
                .filter(fileInfoModel -> {
                    if (showVideo) {
                        return fileInfoModel.getFileSuffixType() == 1;
                    }else {
                        return true;
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    torrentDetailAdapter.setNewInstance(list);
                    setSelectTotalNumUi();
                }, Throwable::printStackTrace);
        addDisposable(disposable);
    }

    private void showBuyVipAlert() {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        showAlertView("",
                indexModel.getJxynr(),
                "取消",
                indexModel.getKaitong(),
                ()->{},()->{
                    Intent intent = new Intent(this, VipCenterActivity.class);
                    startActivity(intent);
                });
    }

    private void setSelectTotalNumUi() {
        Disposable disposable =  Observable.fromIterable(torrentDetailAdapter.getData())
                .filter(TorrentFileInfoModel::isSelect)
                .count()
                .subscribe(selectNum -> {
                    selectNumTv.setText("已选择:" + selectNum);
                }, Throwable::printStackTrace);
        addDisposable(disposable);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }


    private boolean isSelectAll = true;
    @OnClick(R.id.select_all_tv)
    public void selectAllTvAction() {
        isSelectAll = !isSelectAll;
        Disposable disposable = Observable.fromIterable(torrentDetailAdapter.getData())
                .filter(fileInfoModel -> !fileInfoModel.isDownload())
                .doOnNext(fileInfoModel -> fileInfoModel.setSelect(isSelectAll))
                .subscribeOn(Schedulers.newThread())
                .count()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    selectAllTv.setText(!isSelectAll ? "全选" : "全不选");
                    torrentDetailAdapter.notifyItemRangeChanged(0,torrentDetailAdapter.getItemCount(),TorrentDetailAdapter.RELOAD_SELECT);
                    setSelectTotalNumUi();
                });
        addDisposable(disposable);
    }


    @OnClick(R.id.download_tv)
    public void downloadTask() {
        if (!TTDownloadService.getInstance().checkTorrentFile(torrentInfo)) {
            showToast("种子文件被删除");
            return;
        }

        if (UserLoginManager.checkUserLogin()) {
            download();
        }else {
            showLoginView();
        }
    }

    private void showLoginView() {
        //未登录
        String[] items = new String[] {"立即注册","已有账户"};
        showBottomSheet("请登录",items,(index, text) -> {
            UserLoginPopupView userLoginPopupView = new UserLoginPopupView(this);
            userLoginPopupView.setLoginType(index);
            userLoginPopupView.setLoginListener(new UserLoginPopupView.LoginListener() {
                @Override
                public void loginSuccess(LoginModel loginModel) {
                    userEngine.setToken(loginModel.getToken());
                }

                @Override
                public void canRegisterCallBack() {
                    ApiIndexModel indexModel = App.getApp().getApiIndexModel();
                    showBottomSheet("可注册邮箱列表",indexModel.getCanRegisterMailList(),(index,text)-> {

                    });
                }
            });
            showCustomPopupView(userLoginPopupView, true);
        });
    }

    private void download() {
        showDiaLog("正在添加任务",false);
        Disposable disposable = userEngine.fen(2)
                .subscribeOn(Schedulers.io())
                .flatMap(rootModel -> {
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        return Observable.fromIterable(torrentDetailAdapter.getData());
                    }else {
                        return Observable.error(new Throwable("" + "-1"));
                    }
                })
                .filter(fileInfoModel -> !fileInfoModel.isDownload() && fileInfoModel.isSelect())
                .map(fileInfoModel -> {
                    TTDownloadService.getInstance().downloadTorrent(fileInfoModel);
                    fileInfoModel.setSelect(false);
                    return fileInfoModel;
                })
                .count()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    hideLoadingDialog();
                    if (aLong > 0) {
                        torrentDetailAdapter.notifyItemRangeChanged(0,torrentDetailAdapter.getItemCount(),TorrentDetailAdapter.RELOAD_SELECT);
                        showToast("已添加至任务列表");
                        setSelectTotalNumUi();

                        UmengKeyUtils.uploadDownload(this,2);

                        AppSettingsModel settingsModel = AppSettingsModel.getSettingsModel();
                        if (settingsModel.isUseMobileDownload()) {
                            NetworkUtils.showMobileDataToast(this);
                        }
                    }
                }, throwable -> {
                    hideLoadingDialog();
                    if (throwable.getMessage().equals("-1")) {
                        showBuyVipAlert();
                    }else {
                        showToast(throwable.getMessage());
                    }
                });
        addDisposable(disposable);
    }

    @OnClick(R.id.collect_tv)
    public void collectImAction() {
        TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
        int isLike = torrentInfo.getIsLike() == 1 ? 0 : 1;
        torrentInfo.setIsLike(isLike);
        Disposable disposable = torrentDao.setTorrentInfoIsLike(isLike,torrentInfo.getHash())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    configCollectIm();
                    // 通知文件/收藏列表刷新
                    EventBus.getDefault().post(new TorrentManagerEvent());
                }, throwable -> {
                    Log.i(TAG, "collectImAction: " + throwable.getMessage());
                });
        addDisposable(disposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1) {
                Disposable disposable = Observable.fromIterable(torrentDetailAdapter.getData())
                        .filter(fileInfoModel -> fileInfoModel.getIndex() == torrentInfo.getCurrentPlayIndex())
                        .doOnNext(fileInfoModel -> {
                            if (!fileInfoModel.isDownload()) {
                                TTDownloadService.getInstance().deleteTempTask(fileInfoModel);
                            }
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribeOn(Schedulers.io()).subscribe();
                addDisposable(disposable);
            }
        }
    }
}
