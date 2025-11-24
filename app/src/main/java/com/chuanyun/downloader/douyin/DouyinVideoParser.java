package com.chuanyun.downloader.douyin;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DouyinVideoParser extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "DouyinParser";
    private final OkHttpClient client = new OkHttpClient();

    public interface Callback {
        void onResult(JSONObject result);
        void onError(Exception e);
    }

    private final Callback callback;

    public DouyinVideoParser(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String url = URLDecoder.decode(params[0], "UTF-8");
            String videoId = extractVideoId(url);

            String apiUrl = "https://www.iesdouyin.com/share/video/" + videoId + "/";
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36")
                    .addHeader("Referer", "https://www.douyin.com/?is_from_mobile_home=1&recommend=1")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Pattern pattern = Pattern.compile("_ROUTER_DATA\\s*=\\s*(\\{.*?\\});");
            Matcher matcher = pattern.matcher(responseBody);
            if (!matcher.find()) {
                throw new IOException("Could not find ROUTER_DATA");
            }

            String jsonData = matcher.group(1);
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject itemList = jsonObject
                    .getJSONObject("loaderData")
                    .getJSONObject("video_(id)/page")
                    .getJSONObject("videoInfoRes")
                    .getJSONArray("item_list")
                    .getJSONObject(0);

            // Extract author information
            JSONObject author = itemList.getJSONObject("author");
            String nickname = author.getString("nickname");
            String uniqueId = author.getString("unique_id");
            String secUid = author.getString("sec_uid");
            String shortId = author.getString("short_id");
            String signature = author.getString("signature");
            String avatar = author.getJSONObject("avatar_thumb")
                    .getJSONArray("url_list")
                    .getString(0);

            // Extract video information
            String title = itemList.getString("desc");
            String awemeId = itemList.getString("aweme_id");
            String videoUri = itemList.getJSONObject("video")
                    .getJSONObject("play_addr")
                    .getString("uri");
            String cover = itemList.getJSONObject("video")
                    .getJSONObject("cover")
                    .getJSONArray("url_list")
                    .getString(0);

            // Construct video URL
            String videoUrl = "https://www.douyin.com/aweme/v1/play/?video_id=" + videoUri;
            String playUrl = getPlayUrl(videoUrl);

            // Extract music information
            JSONObject music = itemList.getJSONObject("music");
            String musicTitle = music.getString("title");
            String musicAuthor = music.getString("author");
            String musicCover = music.getJSONObject("cover_hd")
                    .getJSONArray("url_list")
                    .getString(0);

            // Handle images (for notes)
            List<String> images = new ArrayList<>();
            if (itemList.has("images") && !itemList.isNull("images")) {
                JSONArray imagesArray = itemList.getJSONArray("images");
                for (int i = 0; i < imagesArray.length(); i++) {
                    String imageUrl = imagesArray.getJSONObject(i)
                            .getJSONArray("url_list")
                            .getString(0);
                    images.add(imageUrl);
                }
            }

            // Build output JSON
            JSONObject output = new JSONObject();
            output.put("msg", nickname.isEmpty() ? "false" : "true");
            output.put("title", title);
            output.put("aweme_id", awemeId);
            output.put("cover", cover);
            output.put("type", images.isEmpty() ? "video" : "note");

            JSONObject user = new JSONObject();
            user.put("nickname", nickname);
            user.put("unique_id", uniqueId);
            user.put("sec_uid", secUid);
            user.put("short_id", shortId);
            user.put("signature", signature);
            user.put("avatar", avatar);
            output.put("user", user);

            JSONObject video = new JSONObject();
            video.put("url", videoUrl);
            video.put("play_url", playUrl);
            output.put("video", video);

            JSONArray imagesJson = new JSONArray(images);
            output.put("images", imagesJson);

            JSONObject musicJson = new JSONObject();
            musicJson.put("title", musicTitle);
            musicJson.put("author", musicAuthor);
            musicJson.put("cover", musicCover);
            musicJson.put("url", JSONObject.NULL);
            output.put("music", musicJson);

            return output;

        } catch (Exception e) {
            Log.e(TAG, "Error parsing Douyin URL", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result != null) {
            callback.onResult(result);
        } else {
            callback.onError(new Exception("Failed to parse Douyin URL"));
        }
    }

    private String extractVideoId(String url) throws IOException {
        if (url.matches("\\d+")) {
            return url;
        }

        Pattern urlPattern = Pattern.compile("https?://[^\\s]+");
        Matcher urlMatcher = urlPattern.matcher(url);
        if (!urlMatcher.find()) {
            throw new IOException("Invalid URL");
        }

        String videoUrl = urlMatcher.group(0);
        String redirectedUrl = getRedirectedUrl(videoUrl);
        Pattern idPattern = Pattern.compile("(\\d+)");
        Matcher idMatcher = idPattern.matcher(redirectedUrl);
        if (idMatcher.find()) {
            return idMatcher.group(1);
        }

        throw new IOException("Could not extract video ID");
    }

    private String getRedirectedUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.request().url().toString();
    }

    private String getPlayUrl(String videoUrl) throws IOException {
        Request request = new Request.Builder()
                .url(videoUrl)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        Pattern hrefPattern = Pattern.compile("href=\"([^\"]+)\"");
        Matcher hrefMatcher = hrefPattern.matcher(responseBody);
        if (hrefMatcher.find()) {
            return hrefMatcher.group(1);
        }

        return "";
    }
}