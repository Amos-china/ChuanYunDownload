package com.chuanyun.downloader.downloadService;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ghost.downengine.FlashDownEngineImpl;
import com.chuanyun.downloader.models.TTTorrentInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FCHelper {
        public static String getIpAddress(Context context) {
            String str;
            try {
                int ipAddress = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress();
                str = String.format("%d.%d.%d.%d", Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255));
            } catch (Exception e) {
                e.printStackTrace();
                str = null;
            }
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement = inetAddresses.nextElement();
                        if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet4Address)) {
                            return nextElement.getHostAddress();
                        }
                    }
                }
                return str;
            } catch (SocketException e2) {
                return "127.0.0.1";
            }
        }

        public static String getFileName(String str) {
            try {
                if (str.startsWith("/")) {
                    TTTorrentInfo infoFromTorrentFile = getInfoFromTorrentFile(str);
                    if (TextUtils.isEmpty(infoFromTorrentFile.getTorrentName())) {
                        return infoFromTorrentFile.getFileModelList().get(0).getName();
                    }
                    String str2 = infoFromTorrentFile.getTorrentName();
                    return str2.length() > 50 ? str2.substring(0, 50) : str2;
                }
                if (str.startsWith("thunder://")) {
                    str = thunderDecode(str);
                }
                String decode = SafeDecodeUrl.decode(str);
                if (decode.startsWith("magnet")) {
                    return getMagnetHash(decode) + ".torrent";
                }
                if (decode.startsWith("ed2k://")) {
                    String[] split = decode.split("\\|");
                    if (split.length >= 5) {
                        return split[2];
                    }
                }
                throw new Exception("unknown");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    String file = new URL(str.split("\\?")[0]).getFile();
                    int lastIndexOf = file.lastIndexOf("/");
                    if (lastIndexOf != -1) {
                        file = file.substring(lastIndexOf + 1);
                    }
                    return URLDecoder.decode(file);
                } catch (MalformedURLException e2) {
                    e2.printStackTrace();
                    return "";
                }
            }
        }

        public static String thunderDecode(String str) {
            if (!str.startsWith("thunder://")) {
                return str;
            }
            String checkAndConvertToUtf8 = FlashDownEngineImpl.checkAndConvertToUtf8(Base64.decode(str.substring(10).getBytes(), 0));
            if (checkAndConvertToUtf8.length() <= 4) {
                return checkAndConvertToUtf8;
            }
            int indexOf = checkAndConvertToUtf8.indexOf("AA");
            int indexOf2 = checkAndConvertToUtf8.indexOf("ZZ");
            return (indexOf == -1 || indexOf2 == -1) ? checkAndConvertToUtf8 : checkAndConvertToUtf8.substring(indexOf + 2, indexOf2);
        }

        public static String getMagnetHash(String str) {
            Matcher matcher = Pattern.compile("\\w{40}").matcher(str);
            if (matcher.find()) {
                return matcher.group().toUpperCase();
            }
            Matcher matcher2 = Pattern.compile("\\w{32}").matcher(str);
            return matcher2.find() ? FlashDownEngineImpl.base32MagnetToHex(matcher2.group()).toUpperCase() : "";
        }

        public static TTTorrentInfo getInfoFromTorrentFile(String str) {
            TTTorrentInfo torrentInfo = new TTTorrentInfo();
            String infoFromTorrentFile = FlashDownEngineImpl.getInfoFromTorrentFile(str);
            if (infoFromTorrentFile != null) {
               torrentInfo = JSON.parseObject(infoFromTorrentFile,TTTorrentInfo.class);
            }
            return torrentInfo;
        }

        public static FCTaskInfo getTaskInfo(long j) {
            JSONObject parseObject = JSONObject.parseObject(FlashDownEngineImpl.getTaskInfo((int) j));
            FCTaskInfo xLTaskInfo = new FCTaskInfo();
            xLTaskInfo.mDownloadSize = parseObject.getLongValue("downSize");
            xLTaskInfo.mDownloadSpeed = parseObject.getIntValue("downSpeed");
            xLTaskInfo.mFileSize = parseObject.getLongValue("fileSize");
            xLTaskInfo.mTaskId = j;
            xLTaskInfo.mTaskStatus = parseObject.getIntValue("status");
            xLTaskInfo.health = parseObject.getIntValue("health");
            return xLTaskInfo;
        }


        public static String byteFormat(long j, boolean z) {
            String[] strArr = {" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB"};
            int log = (int) (Math.log(j) / Math.log(1024));
            double pow = j > 1024 ? j / Math.pow(1024, log) : j / 1024;
            return z ? String.format(Locale.ENGLISH, "%.1f%s", Double.valueOf((double) pow), strArr[log]) : String.format(Locale.ENGLISH, "%.1f", Double.valueOf((double) pow));
        }
}
