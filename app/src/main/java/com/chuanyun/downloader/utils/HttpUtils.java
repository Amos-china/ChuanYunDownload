package com.chuanyun.downloader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpUtils {
    private static Map<String, String> cookiesMap = new HashMap();
    private static int mTimeOut = 10000;

    public static Bitmap getBitmap(String str) {
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                try {
                    httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                    if (httpURLConnection.getResponseCode() == 200) {
                        getCookie(httpURLConnection);
                        Bitmap decodeStream = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        httpURLConnection.disconnect();
                        return decodeStream;
                    }
                    httpURLConnection.disconnect();
                    return null;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    httpURLConnection.disconnect();
                    return null;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                httpURLConnection.disconnect();
                return null;
            }
        } catch (Throwable th) {
            httpURLConnection.disconnect();
            throw th;
        }
    }


    private static synchronized void getCookie(HttpURLConnection httpURLConnection) {
        List<String> list = httpURLConnection.getHeaderFields().get("Set-Cookie");
        if (list != null) {
            for (String str : list) {
                for (String str2 : str.split(";")) {
                    String[] split = str2.split("=");
                    if (split.length == 2 && !split[0].trim().equals("path")) {
                        cookiesMap.put(split[0].trim(), split[1].trim());
                    }
                }
            }
        }
    }

    private static synchronized void buildCookie(HttpURLConnection httpURLConnection) {
        String str = new String();
        for (String str2 : cookiesMap.keySet()) {
            if (!str.equals("")) {
                str = str + ";";
            }
            str = str + str2 + "=" + cookiesMap.get(str2);
        }
        httpURLConnection.setRequestProperty("Cookie", str);
    }

    public static synchronized String Get(String str) {
        return Get(str, null);
    }

    public static synchronized Response GetToResponse(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setReadTimeout(mTimeOut);
            httpURLConnection.setConnectTimeout(mTimeOut);
            httpURLConnection.getInputStream();
            return new Response(httpURLConnection);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static synchronized String Get(String str, List<String> list) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setReadTimeout(mTimeOut);
            httpURLConnection.setConnectTimeout(mTimeOut);
            InputStream inputStream = httpURLConnection.getInputStream();
            if (httpURLConnection.getResponseCode() != 200) {
                return "";
            }
            if (list != null) {
                Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                Log.d("accurate", headerFields.toString());
                List<String> list2 = headerFields.get("Set-Cookie");
                if (list2 != null) {
                    list.addAll(list2);
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read <= 0) {
                    return new String(byteArrayOutputStream.toByteArray());
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.HttpUtils$Request */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/HttpUtils$Request.class */
    public static class Request {
        public static final String WWW_ENCODE = "application/x-www-form-urlencoded";
        public static final String WWW_ENCODE_utf_8 = "application/x-www-form-urlencoded; charset=UTF-8";
        private String url;
        private String method = "GET";
        private byte[] contents = new byte[0];
        private boolean redirect = true;
        private Map<String, String> header = new HashMap();

        public Request url(String str) {
            this.url = str;
            return this;
        }

        public Request redirect(boolean z) {
            this.redirect = z;
            return this;
        }

        public Request timeOut(int i) {
            int unused = HttpUtils.mTimeOut = i;
            return this;
        }

        public Request get() {
            this.method = "GET";
            return this;
        }

        public Request post() {
            this.method = "POST";
            this.header.put("Content-Type", WWW_ENCODE);
            return this;
        }

        public Request contentByte(byte[] bArr) {
            this.contents = bArr;
            return this;
        }

        public Request contentString(String str) {
            this.contents = str.getBytes();
            return this;
        }

        public Request header(String str, String str2) {
            this.header.put(str, str2);
            return this;
        }

        public Request header(Map<String, String> map) {
            this.header = map;
            return this;
        }

        public Response exec() {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.url).openConnection();
                httpURLConnection.setRequestMethod(this.method);
                for (String str : this.header.keySet()) {
                    httpURLConnection.setRequestProperty(str, this.header.get(str));
                }
                httpURLConnection.setDoInput(true);
                if (this.method.equals("POST")) {
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.getOutputStream().write(this.contents);
                    httpURLConnection.getOutputStream().flush();
                    httpURLConnection.getOutputStream().close();
                }
                httpURLConnection.setInstanceFollowRedirects(this.redirect);
                httpURLConnection.setReadTimeout(HttpUtils.mTimeOut);
                httpURLConnection.setConnectTimeout(HttpUtils.mTimeOut);
                httpURLConnection.getInputStream();
                return new Response(httpURLConnection);
            } catch (Exception e) {
                return new Response(e);
            }
        }
    }

    /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.HttpUtils$Response */
    /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/HttpUtils$Response.class */
    public static class Response {
        public int code;
        public String message;
        private Body body;
        private Header header;

        private Response(HttpURLConnection httpURLConnection) throws IOException {
            this.code = 200;
            this.message = "";
            this.code = httpURLConnection.getResponseCode();
            this.message = httpURLConnection.getResponseMessage();
            this.body = new Body(httpURLConnection);
            this.header = new Header(httpURLConnection);
        }

        private Response(Exception exc) {
            this.code = 200;
            this.message = "";
            this.code = -1;
            this.message = exc.getMessage();
            this.body = new Body(exc);
            this.header = new Header(exc);
        }

        public Header header() {
            return this.header;
        }

        public Body body() {
            return this.body;
        }

        public int code() {
            return this.code;
        }

        public String message() {
            return this.message;
        }

        Response(HttpURLConnection httpURLConnection, Object obj) throws IOException {
            this(httpURLConnection);
        }

        Response(Exception exc, Object obj) {
            this(exc);
        }

        /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.HttpUtils$Response$Body */
        /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/HttpUtils$Response$Body.class */
        public static class Body {
            private byte[] bytes;
            private String string;

            private Body(HttpURLConnection httpURLConnection) throws IOException {
                this.bytes = readIO(httpURLConnection.getInputStream());
                this.string = new String(this.bytes);
            }

            private Body(Exception exc) {
                if (this.bytes == null) {
                    this.bytes = new byte[1];
                } else {
                    this.bytes = exc.toString().getBytes();
                }
                if (exc == null) {
                    this.string = "";
                } else {
                    this.string = exc.toString();
                }
            }

            public byte[] bytes() {
                return this.bytes;
            }

            public String string() {
                return this.string;
            }

            public String string(String str) throws UnsupportedEncodingException {
                return new String(this.bytes, str);
            }

            private byte[] readIO(InputStream inputStream) throws IOException {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read <= 0) {
                        return byteArrayOutputStream.toByteArray();
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
            }

            Body(HttpURLConnection httpURLConnection, Object obj) throws IOException {
                this(httpURLConnection);
            }

            Body(Exception exc, Object obj) {
                this(exc);
            }
        }

        /* renamed from: com.e4a.runtime.components.impl.android.清明_迅雷下载器类库.HttpUtils$Response$Header */
        /* loaded from: qingljhedfuyeaiuf.jar:com/e4a/runtime/components/impl/android/清明_迅雷下载器类库/HttpUtils$Response$Header.class */
        public static class Header {
            public Map<String, List<String>> header;
            public List<String> cookies;

            private Header(HttpURLConnection httpURLConnection) throws IOException {
                this.header = httpURLConnection.getHeaderFields();
                this.cookies = this.header.get("Set-Cookie");
            }

            private Header(Exception exc) {
                this.header = new HashMap();
                this.cookies = new ArrayList();
            }

            public Map<String, List<String>> header() {
                return this.header;
            }

            public List<String> header(String str) {
                return this.header.get(str);
            }

            public List<String> cookies() {
                return this.cookies;
            }

            Header(HttpURLConnection httpURLConnection, Object obj) throws IOException {
                this(httpURLConnection);
            }

            Header(Exception exc, Object obj) {
                this(exc);
            }
        }
    }
}
