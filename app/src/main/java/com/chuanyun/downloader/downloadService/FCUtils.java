package com.chuanyun.downloader.downloadService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.json.JSONException;
import org.json.JSONObject;

public class FCUtils {

    /* renamed from: a */
    private static final int f0a = 15;

    /* renamed from: b */
    private static final String f1b = "XLUtil";

    /* renamed from: c */
    private static final C0008a f2c = new C0008a();

    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.XLUtils$GUID_TYPE */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/XLUtils$GUID_TYPE.class */
    public enum GUID_TYPE {
        DEFAULT,
        JUST_IMEI,
        JUST_MAC,
        ALL
    }

    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.XLUtils$NetWorkCarrier */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/XLUtils$NetWorkCarrier.class */
    public enum NetWorkCarrier {
        UNKNOWN,
        CMCC,
        CU,
        CT
    }

    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.XLUtils$b */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/XLUtils$b.class */
    public static class C0009b {

        /* renamed from: a */
        public String f8a = null;

        /* renamed from: b */
        public GUID_TYPE f9b = GUID_TYPE.DEFAULT;
    }

    /* renamed from: a */
    public static long m20a() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getPeerid(Context context) {
        if (context == null) {
            return null;
        }
        String a = f2c.m3a(context, "peerid", null);
        if (!TextUtils.isEmpty(a)) {
            return a;
        }
        String b = m16b(context);
        if (TextUtils.isEmpty(b)) {
            String c = m14c(context);
            if (!TextUtils.isEmpty(c)) {
                a = c + "X";
            }
        } else {
            a = b + "889X";
        }
        Log.d("getPeerid", a + "");
        if (TextUtils.isEmpty(a)) {
            return null;
        }
        f2c.m1b(context, "peerid", a);
        System.out.println("peerid:" + a);
        return a;
    }

    /* renamed from: b */
    public static String m16b(Context context) {
        String str = null;
        if (context != null) {
            str = f2c.m3a(context, "MAC", null);
            if (TextUtils.isEmpty(str)) {
                str = m17b();
                if (!TextUtils.isEmpty(str)) {
                    f2c.m1b(context, "MAC", str);
                }
            }
        }
        return str;
    }

    @SuppressLint({"NewApi"})
    /* renamed from: b */
    public static String m17b() {
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface = (NetworkInterface) it.next();
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress == null) {
                        return null;
                    }
                    StringBuilder sb = new StringBuilder();
                    int length = hardwareAddress.length;
                    for (int i = 0; i < length; i++) {
                        sb.append(String.format("%02X", Byte.valueOf(hardwareAddress[i])));
                    }
                    return sb.toString();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* renamed from: c */
    public static String m14c(Context context) {
        TelephonyManager telephonyManager;
        if (context == null) {
            return null;
        }
        String a = f2c.m3a(context, "IMEI", null);
        if (!TextUtils.isEmpty(a) || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null) {
            return a;
        }
        try {
            String deviceId = telephonyManager.getDeviceId();
            if (deviceId == null) {
                return deviceId;
            }
            try {
                if (deviceId.length() < f0a) {
                    try {
                        int length = f0a - deviceId.length();
                        a = deviceId;
                        while (true) {
                            int i = length - 1;
                            if (length <= 0) {
                                break;
                            }
                            a = a + "M";
                            length = i;
                        }
                        deviceId = a;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return deviceId;
                    }
                }
                f2c.m1b(context, "IMEI", deviceId);
                return deviceId;
            } catch (Exception e2) {
                e2.printStackTrace();
                return deviceId;
            }
        } catch (Exception e3) {
            return a;
        }
    }

    /* renamed from: d */
    public static C0009b m13d(Context context) {
        C0009b bVar = new C0009b();
        GUID_TYPE guid_type = GUID_TYPE.DEFAULT;
        String c = m14c(context);
        if (TextUtils.isEmpty(c)) {
            c = "000000000000000";
        } else {
            guid_type = GUID_TYPE.JUST_IMEI;
        }
        String b = m16b(context);
        if (TextUtils.isEmpty(b)) {
            b = "000000000000";
        } else if (guid_type == GUID_TYPE.JUST_IMEI) {
            guid_type = GUID_TYPE.ALL;
        } else {
            guid_type = GUID_TYPE.JUST_MAC;
        }
        bVar.f8a = c + "_" + b;
        bVar.f9b = guid_type;
        return bVar;
    }

    /* renamed from: e */
    public static String m12e(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("SDKV = " + Build.VERSION.RELEASE);
        sb.append("_MANUFACTURER = " + Build.MANUFACTURER);
        sb.append("_MODEL = " + Build.MODEL);
        sb.append("_PRODUCT = " + Build.PRODUCT);
        sb.append("_FINGERPRINT = " + Build.FINGERPRINT);
        sb.append("_CPU_ABI = " + Build.CPU_ABI);
        sb.append("_ID = " + Build.ID);
        return sb.toString();
    }

    /* renamed from: f */
    public static String m11f(Context context) {
        ConnectivityManager connectivityManager;
        NetworkInfo networkInfo;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null || (networkInfo = connectivityManager.getNetworkInfo(0)) == null) {
            return null;
        }
        return networkInfo.getExtraInfo();
    }

    /* renamed from: g */
    public static String m10g(Context context) {
        WifiManager wifiManager;
        WifiInfo connectionInfo;
        if (context == null || (wifiManager = (WifiManager) context.getSystemService("wifi")) == null || (connectionInfo = wifiManager.getConnectionInfo()) == null) {
            return null;
        }
        return connectionInfo.getSSID();
    }

    /* renamed from: h */
    public static String m9h(Context context) {
        WifiManager wifiManager;
        if (context == null || (wifiManager = (WifiManager) context.getSystemService("wifi")) == null) {
            return null;
        }
        try {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                return connectionInfo.getBSSID();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /* renamed from: i */
    public static int m8i(Context context) {
        ConnectivityManager connectivityManager;
        NetworkInfo activeNetworkInfo;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return 0;
        }
        int type = activeNetworkInfo.getType();
        if (type == 1) {
            return 9;
        }
        if (type != 0) {
            return 5;
        }
        switch (activeNetworkInfo.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return 2;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case f0a /* 15 */:
                return 3;
            case 13:
                return 4;
            default:
                return 0;
        }
    }

    /* renamed from: j */
    public static int m7j(Context context) {
        NetworkInfo activeNetworkInfo;
        if (context == null) {
            return 0;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return 1;
        }
        int type = activeNetworkInfo.getType();
        if (type == 1) {
            return 2;
        }
        if (type != 0) {
            return 1;
        }
        switch (activeNetworkInfo.getSubtype()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case f0a /* 15 */:
                return 3;
            default:
                return 1;
        }
    }

    /* renamed from: a */
    public static String m19a(String str) {
        if (str == null) {
            return null;
        }
        try {
            char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(cArr[(b >> 4) & f0a]);
                sb.append(cArr[(b >> 0) & f0a]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return str;
        }
    }

    /* renamed from: a */
    public static String m18a(String str, short s, byte b) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        int i = length + 1;
        byte[] bArr = new byte[i + 2 + 1];
        byte[] bytes = str.getBytes();
        for (int i2 = 0; i2 < bytes.length; i2++) {
            bArr[i2] = bytes[i2];
        }
        bArr[length] = 0;
        bArr[i] = (byte) (s & 255);
        bArr[length + 2] = (byte) ((s >> 8) & 255);
        bArr[length + 3] = b;
        return new String(Base64.encode(bArr, 0)).trim();
    }

    /* renamed from: b */
    public static Map<String, Object> m15b(String str) {
        if (str == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                hashMap.put(next, jSONObject.getString(next));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /* renamed from: k */
    public static NetWorkCarrier m6k(Context context) {
        TelephonyManager telephonyManager;
        if (!(context == null || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null)) {
            try {
                String subscriberId = telephonyManager.getSubscriberId();
                if (!subscriberId.startsWith("46000") && !subscriberId.startsWith("46002")) {
                    if (subscriberId.startsWith("46001")) {
                        return NetWorkCarrier.CU;
                    }
                    if (subscriberId.startsWith("46003")) {
                        return NetWorkCarrier.CT;
                    }
                }
                return NetWorkCarrier.CMCC;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NetWorkCarrier.UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.XLUtils$a */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/XLUtils$a.class */
    public static class C0008a {

        /* renamed from: a */
        private static final String f5a = "Identify.txt";

        /* renamed from: b */
        private Map<String, String> f6b;

        /* renamed from: c */
        private ReadWriteLock f7c;

        private C0008a() {
            this.f6b = new HashMap();
            this.f7c = new ReentrantReadWriteLock();
        }

        /* renamed from: a */
        public String m3a(Context context, String str, String str2) {
            String str3;
            this.f7c.readLock().lock();
            String str4 = this.f6b.get(str);
            if (str4 == null) {
                m5a(context);
                str3 = this.f6b.get(str);
            } else {
                str3 = str4;
            }
            this.f7c.readLock().unlock();
            return str3 != null ? str3 : str2;
        }

        /* renamed from: b */
        public void m1b(Context context, String str, String str2) {
            this.f7c.writeLock().lock();
            this.f6b.put(str, str2);
            m2b(context);
            this.f7c.writeLock().unlock();
        }

        /* renamed from: a */
        private void m5a(Context context) {
            String a;
            if (!(context == null || (a = m4a(context, f5a)) == null)) {
                this.f6b.clear();
                for (String str : a.split("\n")) {
                    String[] split = str.split("=");
                    if (split.length == 2) {
                        this.f6b.put(split[0], split[1]);
                    }
                }
            }
        }

        /* renamed from: b */
        private void m2b(Context context) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : this.f6b.entrySet()) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            m0c(context, sb.toString(), f5a);
        }

        /* renamed from: c */
        private void m0c(Context context, String str, String str2) {
            if (context != null && str != null && str2 != null) {
                try {
                    FileOutputStream openFileOutput = context.openFileOutput(str2, 0);
                    try {
                        openFileOutput.write(str.getBytes("utf-8"));
                        openFileOutput.close();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                } catch (FileNotFoundException e3) {
                    e3.printStackTrace();
                }
            }
        }

        /* renamed from: a */
        private String m4a(Context context, String str) {
            String str2 = null;
            if (!(context == null || str == null)) {
                try {
                    FileInputStream openFileInput = context.openFileInput(str);
                    byte[] bArr = new byte[256];
                    try {
                        int read = openFileInput.read(bArr);
                        if (read > 0) {
                            str2 = new String(bArr, 0, read, "utf-8");
                        }
                        openFileInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e2) {
                }
            }
            return str2;
        }

        C0008a(Object obj) {
            this();
        }
    }
}
