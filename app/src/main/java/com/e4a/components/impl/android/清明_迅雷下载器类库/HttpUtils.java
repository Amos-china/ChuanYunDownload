package com.e4a.runtime.components.impl.android.清明_迅雷下载器类库;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class HttpUtils {
    private static Map<String, String> cookiesMap = new HashMap<>();
    private static int mTimeOut = 10000;
    private static final int CHUNK_SIZE = 40960;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36";

    public static Bitmap getBitmap(String url) {
        HttpURLConnection connection = null;
        try {
            URL imageUrl = new URL(url);
            connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            buildCookie(connection);
            
            if (connection.getResponseCode() == 200) {
                getCookie(connection);
                return BitmapFactory.decodeStream(connection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static synchronized String Post(String url, String params) {
        return Post(url, params, new HashMap<>());
    }

    public static synchronized String Post(String url, String params, Map<String, String> headers) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        String result = "";
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            //connection.setRequestProperty("Origin", "https://pan.quark.cn");
            
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            buildCookie(connection);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(mTimeOut);
            connection.setReadTimeout(mTimeOut);

            outputStream = connection.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.flush();

            int statusCode = connection.getResponseCode();
            inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            getCookie(connection);
            
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            result = response.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static synchronized String Put(String url, String params) {
        return Put(url, params, new HashMap<>());
    }

    public static synchronized String Put(String url, String params, Map<String, String> headers) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        String result = "";
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            buildCookie(connection);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(mTimeOut);
            connection.setReadTimeout(mTimeOut);
            outputStream = connection.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.flush();
            int statusCode = connection.getResponseCode();
            inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            getCookie(connection);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            result = response.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static synchronized boolean PutFile(String url, File file, Map<String, String> headers, ProgressListener progressListener) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            buildCookie(connection);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(mTimeOut);
            connection.setReadTimeout(mTimeOut);
            connection.setChunkedStreamingMode(CHUNK_SIZE);
            fileInputStream = new FileInputStream(file);
            outputStream = connection.getOutputStream();
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            long totalBytesRead = 0;
            long fileLength = file.length();

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                if (progressListener != null) {
                    progressListener.onProgress(totalBytesRead, fileLength);
                }
            }
            outputStream.flush();
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (outputStream != null) outputStream.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ProgressListener {
        void onProgress(long current, long total);
    }

    private static synchronized void getCookie(HttpURLConnection connection) {
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                String[] parts = cookie.split(";");
                for (String part : parts) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2 && !keyValue[0].trim().equals("path")) {
                        cookiesMap.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        }
    }

    private static synchronized void buildCookie(HttpURLConnection connection) {
        StringBuilder cookieBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
            if (cookieBuilder.length() > 0) {
                cookieBuilder.append(";");
            }
            cookieBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        if (cookieBuilder.length() > 0) {
            connection.setRequestProperty("Cookie", cookieBuilder.toString());
        }
    }

    public static synchronized String Get(String url) {
        return Get(url, null);
    }

    public static synchronized String Get(String url, List<String> cookies) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setDoInput(true);
            connection.setReadTimeout(mTimeOut);
            connection.setConnectTimeout(mTimeOut);
            buildCookie(connection);
            int statusCode = connection.getResponseCode();
            inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            if (cookies != null) {
                Map<String, List<String>> headers = connection.getHeaderFields();
                List<String> setCookies = headers.get("Set-Cookie");
                if (setCookies != null) {
                    cookies.addAll(setCookies);
                }
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Request {
        public static final String WWW_ENCODE = "application/x-www-form-urlencoded";
        public static final String WWW_ENCODE_UTF_8 = "application/x-www-form-urlencoded; charset=UTF-8";
        private String url;
        private String method = "GET";
        private byte[] contents = new byte[0];
        private File uploadFile;
        private boolean redirect = true;
        private Map<String, String> headers = new HashMap<>();
        private ProgressListener progressListener;

        public Request url(String url) {
            this.url = url;
            return this;
        }

        public Request redirect(boolean redirect) {
            this.redirect = redirect;
            return this;
        }

        public Request timeOut(int timeOut) {
            HttpUtils.mTimeOut = timeOut;
            return this;
        }

        public Request get() {
            this.method = "GET";
            return this;
        }

        public Request post() {
            this.method = "POST";
            this.headers.put("Content-Type", "application/json");
            return this;
        }

        public Request put() {
            this.method = "PUT";
            this.headers.put("Content-Type", "application/octet-stream");
            return this;
        }

        public Request file(File file) {
            this.uploadFile = file;
            return this;
        }

        public Request contentByte(byte[] contents) {
            this.contents = contents;
            return this;
        }

        public Request contentString(String contents) {
            this.contents = contents.getBytes();
            return this;
        }

        public Request header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Request header(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Request progressListener(ProgressListener listener) {
            this.progressListener = listener;
            return this;
        }

        public Response exec() {
            HttpURLConnection connection = null;
            OutputStream outputStream = null;
            FileInputStream fileInputStream = null;
            try {
                URL requestUrl = new URL(this.url);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod(this.method);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(this.redirect);
                connection.setReadTimeout(HttpUtils.mTimeOut);
                connection.setConnectTimeout(HttpUtils.mTimeOut);
                connection.setUseCaches(false);

                if (this.uploadFile != null && ("POST".equals(this.method) || "PUT".equals(this.method))) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Length", String.valueOf(this.uploadFile.length()));
                    connection.setChunkedStreamingMode(CHUNK_SIZE);
                    outputStream = connection.getOutputStream();
                    fileInputStream = new FileInputStream(this.uploadFile);
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead;
                    long totalBytesRead = 0;
                    long fileLength = this.uploadFile.length();
                    
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        if (this.progressListener != null) {
                            this.progressListener.onProgress(totalBytesRead, fileLength);
                        }
                    }
                    outputStream.flush();
                } else if (this.contents != null && this.contents.length > 0 && ("POST".equals(this.method) || "PUT".equals(this.method))) {
                    connection.setDoOutput(true);
                    outputStream = connection.getOutputStream();
                    outputStream.write(this.contents);
                    outputStream.flush();
                }
                
                int statusCode = connection.getResponseCode();
                InputStream respStream = (statusCode >= 400) ? connection.getErrorStream() : connection.getInputStream();
                byte[] respBytes;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = respStream.read(buf)) != -1) {
                        baos.write(buf, 0, n);
                    }
                    respBytes = baos.toByteArray();
                }
                // return new Response(connection, responseBody);
                return new Response(connection, respBytes);
            } catch (Exception e) {
                return new Response(e);
            } finally {
                try {
                    if (fileInputStream != null) fileInputStream.close();
                    if (outputStream != null) outputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Response {
        public int code;
        public String message;
        private Body body;
        private Header header;

        private Response(HttpURLConnection connection, String responseBody) throws IOException {
            this.code = connection.getResponseCode();
            this.message = connection.getResponseMessage();
            this.body = new Body(responseBody);
            this.header = new Header(connection);
        }

        private Response(HttpURLConnection connection, byte[] respBytes) throws IOException {
            this.code = connection.getResponseCode();
            this.message = connection.getResponseMessage();
            this.body = new Body(respBytes);
            this.header = new Header(connection);
        }

        private Response(Exception exception) {
            this.code = -1;
            this.message = exception.getMessage();
            this.body = new Body(exception);
            this.header = new Header(exception);
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

        public static class Body {
            private byte[] bytes;
            private String string;

            private Body(String responseBody) {
                this.string = responseBody;
                this.bytes = responseBody != null ? responseBody.getBytes() : new byte[0];
            }

            private Body(Exception exception) {
                this.string = exception.toString();
                this.bytes = this.string.getBytes();
            }

            private Body(byte[] respBytes) {
                this.bytes = (respBytes != null) ? respBytes : new byte[0];
                this.string = new String(this.bytes, java.nio.charset.StandardCharsets.UTF_8);
            }

            public byte[] bytes() {
                return this.bytes;
            }

            public String string() {
                return this.string;
            }

            public String string(String charset) throws UnsupportedEncodingException {
                return new String(this.bytes, charset);
            }
        }

        public static class Header {
            public Map<String, List<String>> headers;
            public List<String> cookies;

            private Header(HttpURLConnection connection) {
                this.headers = connection.getHeaderFields();
                this.cookies = this.headers.get("Set-Cookie");
            }

            private Header(Exception exception) {
                this.headers = new HashMap<>();
                this.cookies = new ArrayList<>();
            }

            public Map<String, List<String>> headers() {
                return this.headers;
            }

            public List<String> header(String key) {
                return this.headers.get(key);
            }

            public List<String> cookies() {
                return this.cookies;
            }
        }
    }
}