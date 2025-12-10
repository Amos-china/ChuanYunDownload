package com.chuanyun.downloader.tabbar.download.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ThreadUtils;
import com.tools.aplayer.APlayerActivity;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.core.IDownloadTaskListener;
import com.chuanyun.downloader.core.TTDownloadService;
import com.chuanyun.downloader.dao.DownloadDao;
import com.chuanyun.downloader.models.AppSettingsModel;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.popup.ShowFilePathPopupView;
import com.chuanyun.downloader.tabbar.download.adapter.DownloadIndexAdapter;
import com.chuanyun.downloader.utils.ClipboardHelper;
import com.chuanyun.downloader.utils.NetworkUtils;
import com.chuanyun.downloader.utils.OpenFileUtils;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadTaskFragment extends BaseLazyFragment {

    @BindView(R.id.download_rv)
    RecyclerView recyclerView;

    private int taskStatus;

    private DownloadIndexAdapter downloadIndexAdapter;

    private DownloadDao downloadDao;


    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_download_task;
    }

    @Override
    protected void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        downloadIndexAdapter = new DownloadIndexAdapter(null);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(downloadIndexAdapter);

        downloadIndexAdapter.addEmptyView();

        downloadIndexAdapter.addChildClickViewIds(R.id.play_tv,R.id.download_im);
        downloadIndexAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            TorrentFileInfoModel downloadInfo = downloadIndexAdapter.getItem(position);
            if (view.getId() == R.id.play_tv) {
                if (downloadInfo.getFileSuffixType() == 1) {
                    showSelectPlayerAlert(downloadInfo);
                }else {
//                    showToast("暂无打开方式");
                    OpenFileUtils.openFile(getContext(),downloadInfo.getFilePath() + downloadInfo.getName());
                }
            }else if (view.getId() == R.id.download_im) {
                if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_STOP
                        || downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FAIL
                        || downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_WAIT) {
                    if (TTDownloadService.getInstance().checkFileDownload(downloadInfo)) {
                        TTDownloadService.getInstance().downloadTorrent(downloadInfo);
                        AppSettingsModel settingsModel = AppSettingsModel.getSettingsModel();
                        if (settingsModel.isUseMobileDownload()) {
                            NetworkUtils.showMobileDataToast(getContext());
                        }
                    }else {
                        showToast("种子文件已被删除");
                    }
                }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                    TTDownloadService.getInstance().stopDownloadTask(downloadInfo);
                }else {
                    showMoreSheet(downloadInfo);
                    return;
                }
                downloadIndexAdapter.reloadItem(position);
            }
        });

        downloadIndexAdapter.setOnItemClickListener((adapter, view, position) -> {
            TorrentFileInfoModel fileInfoModel = downloadIndexAdapter.getItem(position);
            showMoreSheet(fileInfoModel);
        });
    }

    private void playVideo(String url, String title) {
        APlayerActivity.playVideo(getContext(),title,url,false,0);
    }


    private void deleteTask(TorrentFileInfoModel fileInfoModel,boolean deleteFile) {
        TTDownloadService.getInstance().deleteTask(fileInfoModel,deleteFile);
        downloadDao.deleteDownloadInfo(fileInfoModel.getInfoId()).subscribeOn(Schedulers.io()).subscribe();
        downloadIndexAdapter.remove(fileInfoModel);
    }

    @Override
    protected void lazyLoad() {
        downloadDao = App.getApp().getAppDataBase().downloadDao();
        createData();
    }

    private void createData() {
        if (taskStatus == 1) {
            List<TorrentFileInfoModel> fileInfoModelList = TTDownloadService.getInstance().getFileInfoModelList();

            downloadIndexAdapter.setNewInstance(fileInfoModelList);

            TTDownloadService.getInstance().setDownloadTaskListener(new IDownloadTaskListener() {
                @Override
                public void addDownloadTask(TorrentFileInfoModel fileInfoModel) {
                    ThreadUtils.runOnUiThread(() -> downloadIndexAdapter.addData(fileInfoModel));
                }

                @Override
                public void removeDownloadTask(TorrentFileInfoModel fileInfoModel) {
                    ThreadUtils.runOnUiThread(() -> downloadIndexAdapter.remove(fileInfoModel));
                }

                @Override
                public void reloadList() {
                    ThreadUtils.runOnUiThread(() -> downloadIndexAdapter.notifyDataSetChanged());
                }

                @Override
                public void reloadItem(TorrentFileInfoModel fileInfoModel) {
                    int index = downloadIndexAdapter.getItemPosition(fileInfoModel);
                    ThreadUtils.runOnUiThread(() -> downloadIndexAdapter.reloadItem(index));

                }
            });
        }else {

            TTDownloadService.getInstance().setDownloadSuccessListener(fileInfoModel -> ThreadUtils.runOnUiThread(() -> downloadIndexAdapter.addData(0, fileInfoModel)));

            Disposable disposable = downloadDao.getCompleteTaskInfoList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> downloadIndexAdapter.setNewInstance(list), Throwable::printStackTrace);
            addDisposable(disposable);
        }
    }

    public void downloadAllTask() {
        AppSettingsModel settingsModel = AppSettingsModel.getSettingsModel();
        if (settingsModel.isUseMobileDownload()) {
            NetworkUtils.showMobileDataToast(getContext());
        }
        TTDownloadService.getInstance().downloadAllTask();
    }

    public void stopAllTask() {
        TTDownloadService.getInstance().stopAllTask();
    }

    public void deleteAllTask(boolean deleteFile) {
        Disposable disposable = Observable.fromIterable(downloadIndexAdapter.getData())
                .subscribeOn(Schedulers.io())
                .map(torrentFileInfoModel ->  {
                    TTDownloadService.getInstance().deleteTask(torrentFileInfoModel,deleteFile);
                    return torrentFileInfoModel;
                })
                .flatMapCompletable(torrentFileInfoModel -> downloadDao.deleteDownloadInfo(torrentFileInfoModel.getInfoId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()-> {
                    downloadIndexAdapter.getData().clear();
                    downloadIndexAdapter.notifyDataSetChanged();
                });
        addDisposable(disposable);
    }

    private void showMoreSheet(TorrentFileInfoModel fileInfoModel) {
        DownloadTaskMoreSheet sheet = new DownloadTaskMoreSheet(getContext(),index -> {
            if (index == 0) {
                if (fileInfoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
                    showToast("文件已下载完成");
                }else if (fileInfoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                    showToast("文件正在下载");
                }else {
                    if (TTDownloadService.getInstance().checkFileDownload(fileInfoModel)) {
                        TTDownloadService.getInstance().downloadTorrent(fileInfoModel);
                        AppSettingsModel settingsModel = AppSettingsModel.getSettingsModel();
                        if (settingsModel.isUseMobileDownload()) {
                            NetworkUtils.showMobileDataToast(getContext());
                        }
                    } else {
                        showToast("种子文件已被删除");
                    }
                }
            }else if (index == 1) {
                showDeleteFileAlert(fileInfoModel);
            }else if (index == 2) {
                if (TextUtils.isEmpty(fileInfoModel.getMagnet())) {
                    showToast("外部文件无法获取链接");
                    return;
                }
                ClipboardHelper.copyTextToClipboard(getContext(),fileInfoModel.getMagnet());
                showToast("已复制到剪切板");
            }else {
                showPathPopupView(fileInfoModel);
            }
        });
        showCustomPopupView(sheet,true);
    }

    private void showPathPopupView(TorrentFileInfoModel fileInfoModel) {
        ShowFilePathPopupView showFilePathPopupView = new ShowFilePathPopupView(getContext(),fileInfoModel);
        showCustomPopupView(showFilePathPopupView,true);
    }

    private void showDeleteFileAlert(TorrentFileInfoModel fileInfoModel) {
        String[] strings = new String[] {"删除任务","删除任务和文件"};

        showBottomSheet("",strings,(index, text) -> {
            if (index == 0) {
                deleteTask(fileInfoModel,false);
            }else {
                deleteTask(fileInfoModel,true);
            }
            showToast("删除成功");
        });
    }

    private void showSelectPlayerAlert(TorrentFileInfoModel downloadInfo) {
        String[] strings = new String[] {"内置播放器","外部播放器"};
        showBottomSheet("请选择播放器",strings,(index,text) -> {
            if (downloadInfo.getDownloadStatus() != TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                if (downloadInfo.getDownloadStatus() != TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
                    if (TTDownloadService.getInstance().checkFileDownload(downloadInfo)) {
                        TTDownloadService.getInstance().downloadTorrent(downloadInfo);
                    }
                }
            }
            String videoPath = TTDownloadService.getInstance().getVideFileUrl(downloadInfo);
            if (index == 0) {
                playVideo(videoPath,downloadInfo.getName());
            }else {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse(videoPath);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
}