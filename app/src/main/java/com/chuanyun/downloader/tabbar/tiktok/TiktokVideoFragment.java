package com.chuanyun.downloader.tabbar.tiktok;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.LazyLoadFragment;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.login.popup.UserLoginPopupView;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.ApiRootModel;
import com.chuanyun.downloader.tabbar.me.ui.VipCenterActivity;
import com.chuanyun.downloader.utils.NetworkUtils;
import com.chuanyun.downloader.web.WebViewController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.doikki.videoplayer.player.VideoView;

public class TiktokVideoFragment extends LazyLoadFragment {


    @BindView(R.id.vvp)
    VerticalViewPager mViewPager;

    private int mCurPos;
    private int needLoadMoreDataCount;
    private boolean isLoadData;
    private List<TikTokRandomDataModel> mVideoList = new ArrayList<>();
    private TikTokAdapter mTiktokAdapter;

    private PreloadManager mPreloadManager;
    private TikTokController mController;
    private VideoView mVideoView;

    private TikTokEngine tikTokEngine;

    private TiktokSortModel sortModel;

    public void setSortModel(TiktokSortModel sortModel) {
        this.sortModel = sortModel;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_tiktok_video;
    }

    @Override
    protected void init() {
        initVideoView();
        initViewPager();
    }

    @Override
    public void onFirstUserVisible() {
        super.onFirstUserVisible();

        if (mVideoView != null) {
            mVideoView.resume();
        }

        NetworkUtils.showMobileDataToast(getContext());

        tikTokEngine = new TikTokEngine(getContext());

        needLoadMoreDataCount = 0;
        isLoadData = false;

        mCurPos = 0;

        mPreloadManager = PreloadManager.getInstance(App.getApp());

        showLoadingDialog("在正在加载数据中");
        parseRequestData();
    }

    private Observable<TikTokRandomModel> requestData() {
//        if (sortModel.getSort() == 0) {
//            return tikTokEngine.requestRandom(5).map(ApiRootModel::getData).retry(2);
//        }else {
//            return tikTokEngine.requestCategory(sortModel.getSort()).map(ApiRootModel::getData).retry(2);
//        }
        return tikTokEngine.requestRandom(5).map(ApiRootModel::getData).retry(2);
    }

    private void parseRequestData() {
        isLoadData = true;
        Disposable disposable = requestData()
                .subscribeOn(Schedulers.io())
                .map(TikTokRandomModel::getData)
                .flatMapIterable(list -> list)
                .flatMap(this::isUrlReachable)
                .filter(TikTokRandomDataModel::isCanPlay)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    isLoadData = false;
                    mVideoList.addAll(list);
                    if (list.size() < 2) {
                        parseRequestData();
                        return;
                    }
                    mTiktokAdapter.notifyDataSetChanged();
                    needLoadMoreDataCount = needLoadMoreDataCount + list.size() / 2;
                    if (mCurPos == 0) {
                        startPlay(mCurPos);
                    }
                    dismissLoadingDialog();
                },throwable -> {
                    isLoadData = false;
                    dismissLoadingDialog();
                });
        addDisposable(disposable);
    }

    private Observable<TikTokRandomDataModel> isUrlReachable(TikTokRandomDataModel dataModel) {
        dataModel.setCanPlay(true);
        return Observable.just(dataModel)
                .map(model -> {
                    URL url = new URL(model.getUrl());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(true);
                    connection.setInstanceFollowRedirects(false);
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    try {
                        connection.connect();
                    } catch (Exception e) {
                        model.setCanPlay(false);
                        connection.disconnect();
                        return model;
                    }

                    model.setCanPlay(connection.getResponseCode() != 404);
                   connection.disconnect();
                    return model;
                });
    }

    private void initVideoView() {
        mVideoView = new VideoView(getContext());
        mVideoView.setLooping(true);

        //以下只能二选一，看你的需求
        mVideoView.setRenderViewFactory(TikTokRenderViewFactory.create());
//        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);

        mController = new TikTokController(getContext());
        mVideoView.setVideoController(mController);
        mViewPager.setCurrentItem(0);
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(4);
        mTiktokAdapter = new TikTokAdapter(mVideoList);
        mTiktokAdapter.setImActionCallBack(new TikTokAdapter.ImActionCallBack() {
            @Override
            public void downloadImAction(TikTokRandomDataModel dataModel) {
                if (checkUser(0)) {
                    downloadVideo(dataModel);
                }
            }

            @Override
            public void userHomeImAction(TikTokRandomDataModel dataModel) {
                if (checkUser(1)) {
                    String url = "https://www.douyin.com/user/" + dataModel.getSecUid();
                    WebViewController.loadWeb(getContext(),url,"详情");
                }
            }
        });
        mViewPager.setAdapter(mTiktokAdapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                checkLoadMoreData(position);
                if (position == mCurPos) return;
                startPlay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == VerticalViewPager.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }

                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
                } else {
                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
                }
            }
        });
    }

    private void showLoginView() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
        //未登录
        String[] items = new String[] {"立即注册","已有账户"};
        showBottomSheet("登录成为会员才能下载",items,(index, text) -> {
            UserLoginPopupView userLoginPopupView = new UserLoginPopupView(getContext());
            userLoginPopupView.setLoginType(index);
            userLoginPopupView.setLoginListener(new UserLoginPopupView.LoginListener() {
                @Override
                public void loginSuccess(LoginModel loginModel) {

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

    private boolean checkUser(int type) {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel == null) {
            showLoginView();
            return false;
        }else {
            if (loginModel.getInfo().getVipStatus() != 1) {
                showBuyVipAlert(type);
                return false;
            }else {
                return true;
            }
        }
    }

    private void showBuyVipAlert(int type) {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        String nr = type == 0 ? indexModel.getSpxznr() : indexModel.getSpzynr();
        showAlertView("",
                Html.fromHtml(nr),
                "取消",
                indexModel.getKaitong(),
                ()->{},()->{
                    Intent intent = new Intent(getContext(), VipCenterActivity.class);
                    startActivity(intent);
                });
    }

    //https://www.douyin.com/user/sec_uid
    private void downloadVideo(TikTokRandomDataModel dataModel) {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel == null) {
            showLoginView();
            return;
        }else {
            if (loginModel.getInfo().getVipStatus() != 1) {
                showToast("会员才可以下载哦");
                return;
            }
        }

        NetworkUtils.showMobileDataToast(getContext());

        showLoadingDialog("正在下载");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(dataModel.getUrl())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("下载失败");
                dismissLoadingDialog();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                saveFile(response.body().byteStream());
                dismissLoadingDialog();
            }
        });
    }

    private void saveFile(InputStream inputStream) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/download_video.mp4";
        File file = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer,0,len);
            }
            fileOutputStream.flush();
            MediaScannerConnection.scanFile(getContext(),new String[]{file.getAbsolutePath()},null,null);
            showToast("已保存到相册");
        } catch (IOException e) {
            showToast("保存失败");
        }
    }

    private void checkLoadMoreData(int position) {
        if (!isLoadData) {
            if (position >= needLoadMoreDataCount) {
                parseRequestData();
            }
        }
    }

    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i ++) {
            View itemView = mViewPager.getChildAt(i);
            TikTokAdapter.ViewHolder viewHolder = (TikTokAdapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoView.release();
                Utils.removeViewFormParent(mVideoView);

                TikTokRandomDataModel tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.getUrl());
                mVideoView.setUrl(playUrl);
                //请点进去看isDissociate的解释
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                mCurPos = position;
                break;
            }
        }
    }

    public void addData(View view) {
//        mVideoList.addAll(DataUtils.getTiktokDataFromAssets(this));
        mTiktokAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mVideoView != null) {
            mVideoView.release();
        }

        if (mPreloadManager != null) {
            mPreloadManager.removeAllPreloadTask();
        }

        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(getContext());
    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();
        stopPlay();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    public void stopPlay(){
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

}
