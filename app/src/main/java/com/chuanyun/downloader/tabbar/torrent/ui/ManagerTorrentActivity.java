package com.chuanyun.downloader.tabbar.torrent.ui;

import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.dao.TorrentDao;
import com.chuanyun.downloader.eventBusModel.TorrentManagerEvent;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.tabbar.torrent.adapter.TorrentManagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ManagerTorrentActivity extends BaseActivity {

    @BindView(R.id.torrent_rv)
    RecyclerView recyclerView;

    @BindView(R.id.delete_tv)
    TextView deleteTv;

    @BindView(R.id.select_all_tv)
    TextView selectAllTv;

    private TorrentManagerAdapter torrentManagerAdapter;

    private TorrentDao torrentDao;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_torrent;
    }

    @Override
    protected void initData() {
        super.initData();

        torrentDao = App.getApp().getAppDataBase().torrentDao();

        getData();
    }

    private void getData() {
        Disposable disposable = torrentDao.getTorrentInfoAtNotDel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    torrentManagerAdapter.setNewInstance(list);
                },throwable -> {});
        addDisposable(disposable);
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        torrentManagerAdapter = new TorrentManagerAdapter(null);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(torrentManagerAdapter);

        torrentManagerAdapter.addEmptyView();

        torrentManagerAdapter.setOnItemClickListener((adapter, view, position) -> {
            selectItemAt(position);
        });

        torrentManagerAdapter.addChildClickViewIds(R.id.check_im);
        torrentManagerAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.check_im) {
                selectItemAt(position);
            }
        });
    }


    private void selectItemAt(int position) {
        TTTorrentInfo torrentInfo = torrentManagerAdapter.getItem(position);
        torrentInfo.setSelect(!torrentInfo.isSelect());
        torrentManagerAdapter.notifyItemChanged(position,TorrentManagerAdapter.RELOAD_SELECT);

        checkSelect();
    }

    private void checkSelect() {
        Disposable disposable =  Observable.fromIterable(torrentManagerAdapter.getData())
                .filter(TTTorrentInfo::isSelect)
                .count()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    deleteTv.setBackgroundResource(aLong > 0 ? R.color.red : R.color.red_crimson_99);
                });
        addDisposable(disposable);
    }

    @OnClick(R.id.delete_tv)
    public void deleteTvAction() {
        if (torrentManagerAdapter.getData().size() == 0) {
            return;
        }

        List<TTTorrentInfo> selectedTorrents = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            selectedTorrents = torrentManagerAdapter.getData()
                    .stream()
                    .filter(TTTorrentInfo::isSelect)
                    .collect(Collectors.toList());
        }

        if (selectedTorrents.isEmpty()) {
            showToast("没有选择任何任务");
            return;
        }

        List<TTTorrentInfo> finalSelectedTorrents = selectedTorrents;
        Disposable disposable = Observable.fromIterable(selectedTorrents)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(ttTorrentInfo ->
                        torrentDao.setTorrentInfoDel(ttTorrentInfo.getHash(), 1)
                                .doOnComplete(() -> Log.d(TAG, "Deleted torrent: " + ttTorrentInfo.getHash()))
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    // 更新 UI 并清空选中项
                    torrentManagerAdapter.getData().removeAll(finalSelectedTorrents);
                    torrentManagerAdapter.notifyDataSetChanged();
                })
                .subscribe(
                        () -> {
                            showToast("删除成功");
                            EventBus.getDefault().post(new TorrentManagerEvent());
                        },
                        throwable -> {
                            Log.e(TAG, "Error deleting torrents", throwable);
                            showToast("删除失败：" + throwable.getMessage());
                        }
                );

        addDisposable(disposable);
    }

    @OnClick(R.id.select_all_tv)
    public void selectAllTvAction() {
        if (torrentManagerAdapter.getData().isEmpty()) {return;}
        Disposable disposable = Observable.fromIterable(torrentManagerAdapter.getData())
                .doOnNext(ttTorrentInfo -> ttTorrentInfo.setSelect(!ttTorrentInfo.isSelect()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(()-> {
                    TTTorrentInfo info = torrentManagerAdapter.getItem(0);
                    selectAllTv.setText(info.isSelect() ? "全不选" : "全选");
                    torrentManagerAdapter.notifyItemRangeChanged(0,
                            torrentManagerAdapter.getData().size(),
                            TorrentManagerAdapter.RELOAD_SELECT);
                    deleteTv.setBackgroundResource(info.isSelect() ? R.color.red : R.color.red_crimson_99);
                })
                .subscribe();
        addDisposable(disposable);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
