package com.chuanyun.downloader.utils;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.TorrentFileInfoModel;

public class FileTypeUtils {
    //0：未知  1：视频 2：图片 3.音频文件 4.安装包 5：压缩文件 6.文本文件 7.url 8:apk
    static public int getFileTypeAt(String fileName) {
        String[] fruits = fileName.split("\\.");
        if (fruits.length < 2) {return 0;}
        String suffix = fruits[fruits.length-1];
        suffix = suffix.toLowerCase();
        switch (suffix) {
            case "mp4":
            case "m4v":
            case "mpeg":
            case "mov":
            case "avi":
            case "wmv":
            case "mkv":
            case "flv":
            case "rm":
            case "rmvb":
            case "3gp":
            case "ogv":
            case "webm":
            case "mts":
            case "m2ts":
            case "divx":
            case "xvid":
            case "hevc":
            case "h265":
            case "mj2":
            case "dv":
            case "asf":
            case "avchd":
            case "vob":
            case "mjpg":
            case "mxf":
            case "3g2":
            case "mpg":
            case "f4v":
            case "f4p":
            case "f4a":
            case "f4b":
            case "ts":
            case "mpe":
            case "mpv":
            case "m1v":
            case "m2v":
            case "qt":
            case "yuv":
            case "mk3d":
            case "ogm":
                return 1;
            case "jpeg":
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
            case "tiff":
            case "tif":
            case "svg":
            case "webp":
            case "heic":
            case "raw":
            case "psd":
            case "ai":
            case "eps":
            case "pdf":
            case "ico":
            case "icns":
            case "tga":
            case "dds":
            case "xcf":
            case "cr2":
            case "nef":
            case "orf":
            case "arw":
            case "dng":
            case "rw2":
            case "raf":
            case "pef":
                return 2;
            case "mp3":
            case "wav":
            case "aac":
            case "flac":
            case "ogg":
            case "wma":
            case "aiff":
            case "m4a":
            case "opus":
            case "amr":
            case "mid":
            case "midi":
            case "ra":
            case "tta":
            case "ac3":
            case "dts":
            case "aif":
            case "caf":
            case "mka":
            case "spx":
            case "voc":
            case "wv":
                return 3;
            case "exe":
            case "msi":
            case "dmg":
            case "pkg":
            case "app":
            case "jar":
            case "deb":
            case "rpm":
            case "bin":
            case "run":
            case "sh":
            case "appx":
            case "appxbundle":
            case "xap":
            case "msix":
            case "msixbundle":
            case "flatpak":
            case "ebuild":
            case "pup":
            case "slp":
                return 4;
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
            case "bz2":
            case "xz":
            case "tgz":
            case "tbz2":
            case "iso":
            case "cab":
            case "lz":
            case "lzma":
            case "z":
            case "lzh":
            case "arj":
            case "ace":
            case "uue":
            case "war":
            case "ear":
                return 5;
            case "txt":
                return 6;
            case "url":
            case "html":
                return 7;
            case "apk":
                return 8;
            default:
                return 0;
        }
    }

    static public int getFileTypeRes(TorrentFileInfoModel fileModel) {
        switch (fileModel.getFileSuffixType()) {
            case 1:
                return R.mipmap.video;
            case 2:
                return R.mipmap.bt_pic;
            case 3:
                return R.mipmap.bt_music;
            case 4:
                return R.mipmap.bt_exe;
            case 5:
                return R.mipmap.bt_zip;
            case 6:
                return R.mipmap.bt_txt;
            case 7:
                return R.mipmap.bt_google;
            case 8:
                return R.mipmap.bt_apk;
            default:
                return R.mipmap.bt_weizhi_1;
        }
    }
}
