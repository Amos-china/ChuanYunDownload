package com.chuanyun.downloader.web;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.dao.WebHistoryDao;
import com.chuanyun.downloader.models.WebHistoryInfo;
import com.chuanyun.downloader.utils.BitmapUtil;
import com.chuanyun.downloader.web.adapter.WebHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WebHistoryListFragment extends BaseLazyFragment {

    @BindView(R.id.history_rv)
    RecyclerView historyRv;

    private WebHistoryAdapter webHistoryAdapter;

    private WebHistoryDao webHistoryDao;

    private int index = 0;
    private int page = 0;


    public void setIndex(int index) {
        this.index = index;
    }

    public interface OnHistoryItemClickListener {
        void selectItem(WebHistoryInfo webHistoryInfo);
    }

    public interface OnCollectItemClickListener extends OnHistoryItemClickListener {

    }

    private OnHistoryItemClickListener historyItemClickListener;
    private OnCollectItemClickListener collectItemClickListener;

    public void setHistoryItemClickListener(OnHistoryItemClickListener historyItemClickListener) {
        this.historyItemClickListener = historyItemClickListener;
    }

    public void setCollectItemClickListener(OnCollectItemClickListener collectItemClickListener) {
        this.collectItemClickListener = collectItemClickListener;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_history_list;
    }

    @Override
    protected void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        webHistoryAdapter = new WebHistoryAdapter(null);
        historyRv.setLayoutManager(linearLayoutManager);
        historyRv.setAdapter(webHistoryAdapter);

        webHistoryAdapter.addEmptyView();

        webHistoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            WebHistoryInfo info = webHistoryAdapter.getItem(position);
            if (index == 0) {
                historyItemClickListener.selectItem(info);
            }else {
                collectItemClickListener.selectItem(info);
            }
        });

        webHistoryAdapter.addLoadMore(() -> {
            page ++;
            createData();
        });
    }

    @Override
    protected void lazyLoad() {
        webHistoryDao = App.getApp().getAppDataBase().webHistoryDao();
        createData();
    }

    private void createData() {
        Disposable disposable = getWebHistoryInfoListSingle().
                subscribeOn(Schedulers.io())
                .flatMapObservable(this::setBitmapObservable)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(webHistoryInfos -> {
                    if (page == 0) {
                        webHistoryAdapter.setNewInstance(webHistoryInfos);
                    }else {
                        webHistoryAdapter.endLoadMore(webHistoryInfos.size());
                        webHistoryAdapter.addData(webHistoryInfos);
                    }
                }, throwable -> {});
        addDisposable(disposable);
    }

    private Single<List<WebHistoryInfo>> getWebHistoryInfoListSingle() {
        return index == 0 ? webHistoryDao.getHistoryInfo(page * 20,20) : webHistoryDao.getCollectWebHistoryInfo(page * 20,20);
    }

    private Observable<WebHistoryInfo> setBitmapObservable(List<WebHistoryInfo> data) {
        return Observable.fromIterable(data)
                .doOnNext(webHistoryInfo -> {
                    if (!TextUtils.isEmpty(webHistoryInfo.getByteImage())) {
                        Bitmap bitmap = BitmapUtil.base64ToBitmap(webHistoryInfo.getByteImage());
                        webHistoryInfo.setImageBitmap(bitmap);
                    }
                })
                .doOnError(throwable -> {});
    }

    public void clearList() {
        showAlertView("提示信息","是否清空记录?",this::clearHistoryList);
    }

    private void clearHistoryList() {
        Disposable disposable = deleteHistoryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()-> {
                    webHistoryAdapter.setNewInstance(new ArrayList<>());
                },throwable -> {});
        addDisposable(disposable);
    }

    private Completable deleteHistoryList() {
        return index == 0 ? webHistoryDao.deleteHistory()
                .andThen(webHistoryDao.setWebHistoryInfoAtDelete()) : webHistoryDao.updateWebHistoryInfoCollect(0);
    }

}
