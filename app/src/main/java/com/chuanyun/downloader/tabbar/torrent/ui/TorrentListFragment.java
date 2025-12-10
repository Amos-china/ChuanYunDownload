package com.chuanyun.downloader.tabbar.torrent.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.core.IAddMagnetTaskListener;
import com.chuanyun.downloader.core.TTDownloadService;
import com.chuanyun.downloader.dao.TorrentDao;
import com.chuanyun.downloader.eventBusModel.TorrentManagerEvent;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.tabbar.home.ui.TorrentDetailActivity;
import com.chuanyun.downloader.tabbar.torrent.adapter.TorrentHistoryAdapter;
import com.chuanyun.downloader.utils.ClipboardHelper;
import com.chuanyun.downloader.utils.StorageHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TorrentListFragment extends BaseLazyFragment {

    @BindView(R.id.search_et)
    EditText searchEt;

    @BindView(R.id.torrent_rv)
    RecyclerView torrentRv;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int page = 0;

    private TorrentHistoryAdapter torrentHistoryAdapter;

    private int indexType = 0; //0所有 1收藏

    public void setIndexType(int indexType) {
        this.indexType = indexType;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_torrent_list;
    }

    @Override
    protected void initViews() {
        // 注册 EventBus，用于接收列表更新事件
        registerEventBus();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        torrentHistoryAdapter = new TorrentHistoryAdapter(null);
        torrentRv.setLayoutManager(linearLayoutManager);
        torrentRv.setAdapter(torrentHistoryAdapter);

        torrentHistoryAdapter.addEmptyView();

        torrentHistoryAdapter.addChildClickViewIds(R.id.more_im);

        torrentHistoryAdapter.setOnItemClickListener( (adapter,view,position) -> {
            TTTorrentInfo info = torrentHistoryAdapter.getItem(position);
            if (info.getMagnetType() == 1) {
                if (!StorageHelper.doesPathExist(info.getPath())) {
                    if (TextUtils.isEmpty(info.getMagnet())) {
                        showToast("外部导入文件被删除");
                        return;
                    }
                    parseMagnet(info.getMagnet());
                    return;
                }
            }
            openTorrent(info);
        });

        torrentHistoryAdapter.setOnItemChildClickListener((adapter,view , position) -> {
            showMoreSheet(torrentHistoryAdapter.getItem(position));
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getData();
            }
        });

        torrentHistoryAdapter.addLoadMore(()-> {
            page ++;
            getData();
        });

        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 执行发送操作
                page = 0;
                getData();
                hiddenKeyboard();
                return true; // 返回 true 表示已处理该事件
            }
            return false; // 返回 false 表示没有处理该事件
        });
    }


    private void showMoreSheet(TTTorrentInfo torrentInfo) {
        String[] strings = new String[] {"修改文件名","复制链接","删除"};
        showBottomSheet("", strings, (index, text) -> {
            if (index == 0) {
                updateTorrentName(torrentInfo);
            } else if (index == 1) {
                if (TextUtils.isEmpty(torrentInfo.getMagnet())) {
                    showToast("导入文件无法获取链接");
                    return;
                }
                ClipboardHelper.copyTextToClipboard(getContext(), torrentInfo.getMagnet());
                showToast("链接已复制");
            } else {
                // 删除操作：文件 tab 真实删除(标记 is_del=1)，收藏 tab 仅取消收藏
                TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();

                if (indexType == 1) {
                    // 文件栏目：标记删除
                    Disposable disposable = torrentDao.setTorrentInfoDel(torrentInfo.getHash(), 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                torrentHistoryAdapter.remove(torrentInfo);
                                // 通知其他列表刷新
                                EventBus.getDefault().post(new TorrentManagerEvent());
                            }, throwable -> Log.i(TAG, "delete torrent error: " + throwable.getMessage()));
                    addDisposable(disposable);
                } else {
                    // 收藏 tab：只取消收藏
                    Disposable disposable = torrentDao.setTorrentInfoIsLike(0, torrentInfo.getHash())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                torrentHistoryAdapter.remove(torrentInfo);
                                // 通知其他列表刷新（收藏状态变化）
                                EventBus.getDefault().post(new TorrentManagerEvent());
                            }, throwable -> Log.i(TAG, "cancel favorite error: " + throwable.getMessage()));
                    addDisposable(disposable);
                }
            }
        });
    }

    private void updateTorrentName(TTTorrentInfo torrentInfo) {
        showInputAlter("修改文件名",torrentInfo.getTorrentName(),"请输入文件名",text -> {
            TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
            Disposable disposable = torrentDao.updateTorrentName(text,torrentInfo.getHash())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()-> {
                        torrentInfo.setTorrentName(text);
                        int position = torrentHistoryAdapter.getItemPosition(torrentInfo);
                        torrentHistoryAdapter.notifyItemChanged(position);
                        showToast("修改成功");
                    },throwable -> {
                        showToast("修改失败");
                    });
            addDisposable(disposable);
        });
    }

    private void openTorrent(TTTorrentInfo info) {
        Disposable disposable =  Observable.just(info)
                .map(torrentInfo -> {
                    if (torrentInfo.getMagnetType() == 1) {
                        return TTDownloadService.getInstance().openTorrent(torrentInfo.getPath(),torrentInfo.getMagnet(),true);
                    }else {
                        return TTDownloadService.getInstance().openLink(torrentInfo.getMagnet(),true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(torrentInfo -> {
                    Intent intent = new Intent(mActivity, TorrentDetailActivity.class);
                    intent.putExtra(TTTorrentInfo.INTENT_TTTORRENT_INFO,torrentInfo);
                    mActivity.startActivity(intent);
                },throwable -> showToast(throwable.getMessage()));
        addDisposable(disposable);
    }


    public void parseMagnet(String magnet) {
        showDiaLog("正在解析");
        TTDownloadService.getInstance().parseMagnet(magnet, new IAddMagnetTaskListener() {
            @Override
            public void succeed(long j, String str) {
                hideLoadingDialog();
                TTTorrentInfo torrentInfo = TTDownloadService.getInstance().openTorrent(str,magnet,false);
                startTorrentDetailActivity(torrentInfo);
            }

            @Override
            public void failed(long j, int i) {
                hideLoadingDialog();
                showToast("解析文件失败");
            }
        });
    }

    private void startTorrentDetailActivity(TTTorrentInfo ttTorrentInfo) {
        Intent intent = new Intent(getContext(), TorrentDetailActivity.class);
        intent.putExtra(TTTorrentInfo.INTENT_TTTORRENT_INFO,ttTorrentInfo);
        startActivity(intent);
    }

    @OnClick(R.id.search_tv)
    public void searchAction() {
        page = 0;
        getData();
        hiddenKeyboard();
    }

    @OnClick(R.id.clear_im)
    public void clearEtAction() {
        searchEt.setText("");
        page = 0;
        getData();
    }

    private void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void lazyLoad() {
        getData();
    }


    // 接收种子管理/解析/收藏状态变更后的刷新事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTorrentManagerEvent(TorrentManagerEvent event) {
        page = 0;
        getData();
    }


    private void getData() {
        Single<List<TTTorrentInfo>> listSingle = getTorrentListSingle();
        Disposable disposable = listSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (page == 0) {
                        torrentHistoryAdapter.setNewInstance(list);
                    }else {
                        torrentHistoryAdapter.addData(list);
                        torrentHistoryAdapter.endLoadMore(list.size());
                    }

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },throwable -> {
                    Log.i(TAG, "onError: "  + throwable.getMessage());
                    page --;
                });
        addDisposable(disposable);
    }

    private Single<List<TTTorrentInfo>> getTorrentListSingle() {
        TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
        int size = 10;
        String searchText = "%" + searchEt.getText().toString().trim() + "%";

        if (indexType == 1) {
            return torrentDao.getTorrentList(searchText,page * size,size);
        }else {
            return torrentDao.getLikeTorrentList(searchText,page * size,size);
        }
    }
}
