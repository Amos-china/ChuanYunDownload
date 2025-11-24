package com.chuanyun.downloader.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class VideoInfo {
   private String msg;
   private String title;
   @JSONField(name = "aweme_id")
   private String awemeId;
   private String cover;
   private String type;
   private boolean error;

   public void setError(boolean error) {
      this.error = error;
   }

   public boolean isError() {
      return error;
   }

   @JSONField(name = "user")
   private VideoInfoUser user;

   @JSONField(name = "video")
   private VideoInfoVideo video;

   @JSONField(name = "music")
   private VideoInfoMusic music;

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getAwemeId() {
      return awemeId;
   }

   public void setAwemeId(String awemeId) {
      this.awemeId = awemeId;
   }

   public String getCover() {
      return cover;
   }

   public void setCover(String cover) {
      this.cover = cover;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public VideoInfoUser getUser() {
      return user;
   }

   public void setUser(VideoInfoUser user) {
      this.user = user;
   }

   public VideoInfoVideo getVideo() {
      return video;
   }

   public void setVideo(VideoInfoVideo video) {
      this.video = video;
   }

   public VideoInfoMusic getMusic() {
      return music;
   }

   public void setMusic(VideoInfoMusic music) {
      this.music = music;
   }
}
