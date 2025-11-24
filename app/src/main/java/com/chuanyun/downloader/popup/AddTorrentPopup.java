package com.chuanyun.downloader.popup;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.bat.UmengKeyUtils;
import com.chuanyun.downloader.core.IAddMagnetTaskListener;
import com.chuanyun.downloader.core.TTDownloadService;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.tabbar.home.ui.TorrentDetailActivity;
import com.chuanyun.downloader.utils.ClipboardHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class AddTorrentPopup extends TTBaseBottomPopupView {

    @BindView(R.id.parse_tv)
    SuperTextView parseTv;

    @BindView(R.id.torrent_et)
    EditText torrentEt;

    @BindView(R.id.show_copy_tv)
    SuperTextView showCopyTv;

    private String magnet = "";

    public interface AddTorrentPopupListener {
        void openFile();
    }

    private AddTorrentPopupListener listener;

    public AddTorrentPopup(@NonNull Context context,String text, AddTorrentPopupListener listener) {
        super(context);

        this.listener = listener;
        magnet = text;
    }

    public AddTorrentPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_add_torrent;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        torrentEt.setText(magnet);

        String text = ClipboardHelper.getClipboardContent(getContext(),true);
        showCopyTv.setText(text);
    }

    @OnClick(R.id.clear_im)
    public void clearImAction() {
        torrentEt.setText("");
    }

    @OnClick(R.id.paste_tv)
    public void pasteAction() {
        String text = ClipboardHelper.getClipboardContent(getContext(), true);
        if (!TextUtils.isEmpty(text)) {torrentEt.setText(text);}
    }

    @OnClick(R.id.show_copy_tv)
    public void showCopyAction() {
        pasteAction();
    }


    @OnClick(R.id.parse_tv)
    public void parseTvAction() {
        String magnet = torrentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(magnet)) {

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(torrentEt.getWindowToken(), 0);

            showLoading("正在解析");

            if (magnet.startsWith("magnet:?xt=urn")) {
                parseMagnet(magnet);
            }else if (ClipboardHelper.checkLink(magnet)) {
                parseLink(magnet);
            }else {
                dismissLoading();
                showToast("请检查链接是否正确");
            }
            return;
        }

        showToast("请输入链接");
    }

    @OnClick(R.id.open_file_tv)
    public void openFileAction() {
        if (listener != null) {
            listener.openFile();
        }
        dismiss();
    }

    private void parseLink(String magnet) {
        TTTorrentInfo torrentInfo = TTDownloadService.getInstance().openLink(magnet,false);
        if (torrentInfo != null) {
            if (torrentInfo.getMagnet().startsWith("magnet:?")) {
                parseMagnet(torrentInfo.getMagnet());
            }else {
                dismissLoading();
                startTorrentDetailActivity(torrentInfo);
                dismiss();
            }
        }else {
            dismissLoading();
            showToast("解析错误,检查链接");
        }
    }

    private void parseMagnet(String magnet) {
        TTDownloadService.getInstance().parseMagnet(magnet, new IAddMagnetTaskListener() {
            @Override
            public void succeed(long j, String str) {
                dismissLoading();

                TTTorrentInfo torrentInfo = TTDownloadService.getInstance().openTorrent(str,magnet,false);

                startTorrentDetailActivity(torrentInfo);

                UmengKeyUtils.uploadDownload(getContext(),1);

                dismiss();
            }

            @Override
            public void failed(long j, int i) {
                dismissLoading();
                showToast("解析文件失败");
            }
        });
    }

    private void startTorrentDetailActivity(TTTorrentInfo ttTorrentInfo) {
        Intent intent = new Intent(getContext(), TorrentDetailActivity.class);
        intent.putExtra(TTTorrentInfo.INTENT_TTTORRENT_INFO,ttTorrentInfo);
        getContext().startActivity(intent);
    }
}
