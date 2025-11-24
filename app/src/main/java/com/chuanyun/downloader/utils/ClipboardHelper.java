package com.chuanyun.downloader.utils;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

public class ClipboardHelper {
    private ClipboardManager clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener listener;

    private static final String MMKV_NEW_CLIPBOARD_VALUE = "MMKV_NEW_CLIPBOARD_VALUE";

    public interface OnClipboardChangeListener {
        void onClipboardContentChanged(String content);
    }

    private OnClipboardChangeListener clipboardChangeListener;

    public ClipboardHelper(Context context) {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * 设置监听器
     *
     * @param listener 回调接口
     */
    public void setClipboardChangeListener(OnClipboardChangeListener listener) {
        this.clipboardChangeListener = listener;
    }

    /**
     * 开始监听剪切板
     */
    public void startListening() {
        if (clipboardManager == null) {
            Log.e("ClipboardListenerUtil", "ClipboardManager is null!");
            return;
        }
        if (listener == null) {
            listener = () -> {
                if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClip() != null) {
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        CharSequence content = clipData.getItemAt(0).getText();
                        if (content != null && clipboardChangeListener != null) {
                            String oldContent = getOldContent();
                            String newContent = content.toString();
                            if (!oldContent.equals(newContent)) {
                                setNewContent(newContent);
                                clipboardChangeListener.onClipboardContentChanged(content.toString());
                            }
                        }
                    }
                }
            };
            clipboardManager.addPrimaryClipChangedListener(listener);
            Log.d("ClipboardListenerUtil", "Clipboard listening started.");
        }
    }

    /**
     * 停止监听剪切板
     */
    public void stopListening() {
        if (clipboardManager != null && listener != null) {
            clipboardManager.removePrimaryClipChangedListener(listener);
            listener = null;
            Log.d("ClipboardListenerUtil", "Clipboard listening stopped.");
        }
    }

    public static String getClipboardContent(Context context,boolean isShow) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence content = clipData.getItemAt(0).getText();
                if (content != null) {
                    String newContent = content.toString();
                    if (isShow) {
                        return newContent;
                    }

                    String oldContent = getOldContent();

                    if (oldContent.equals(newContent)) {
                        return "";
                    }
                    setNewContent(newContent);

                    return newContent;
                }
            }
        }
        return ""; // 如果剪切板没有内容
    }

    public static String getOldContent() {
        return MMKV.defaultMMKV().getString(MMKV_NEW_CLIPBOARD_VALUE,"");
    }

    public static void setNewContent(String text) {
        MMKV.defaultMMKV().putString(MMKV_NEW_CLIPBOARD_VALUE,text);
    }

    public static boolean checkTextIsTorrent(String text) {
        return text.contains("magnet:?xt=urn:") || text.contains("ed2k://") || text.contains("thunder://");
    }

    public static boolean checkLink(String url) {
        return url.startsWith("ed2k://")
                || url.startsWith("ftp://")
                || url.startsWith("http://")
                || url.startsWith("https://")
                || url.startsWith("thunder://");
    }

    public static void copyTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("label", text));
    }
}
