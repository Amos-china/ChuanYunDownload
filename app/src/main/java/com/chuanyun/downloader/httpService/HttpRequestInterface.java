package com.chuanyun.downloader.httpService;

import com.chuanyun.downloader.douyin.VideoInfo;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.ApiRootModel;
import com.chuanyun.downloader.models.RecommendRootModel;
import com.chuanyun.downloader.tabbar.tiktok.TikTokRandomModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpRequestInterface {
//    @FormUrlEncoded
//    @POST("show/subjectInfo")
//    Observable<ResultInfo<SubjectInfoModel>> getShowSubjectInfo(@Field("is_all") int isAll);



//    @POST("user/myUserInfo")"https://api.feichixiazai.com/api/";
//    Observable<ResultInfo<UserInfoIndexModel>> getMyUserInfo();



    @GET("api/api.php")
    Observable<ApiRootModel<ApiIndexModel>> getApiIndex();

    @GET("api/recommend.php")
    Observable<ApiRootModel<RecommendRootModel>> getRecommendList();

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/getCode")
    Observable<ApiRootModel<String>> getCode(@Field("account") String account, @Field("type") String type);


    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/reg")
    Observable<ApiRootModel<String>> userReg(@Field("account") String account, @Field("code") String code,@Field("invid") String invId, @Field("password") String password, @Field("udid") String udid);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/logon")
    Observable<ApiRootModel<String>> userLogin(@Field("account") String account, @Field("password") String password, @Field("udid") String udid);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/info")
    Observable<ApiRootModel<String>> getUserInfo(@Field("token") String token);


    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/vip")
    Observable<ApiRootModel<String>> checkVip(@Field("token") String token);

    //心跳
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/heartbeat")
    Observable<ApiRootModel<String>> heartbeat(@Field("token") String token);

    //修改密码
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/modifyPwd")
    Observable<ApiRootModel<String>> modifyPwd(@Field("token") String token,@Field("password") String password, @Field("newPassword") String newPassword);

    //重置密码
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/resetPwd")
    Observable<ApiRootModel<String>> resetPwd(@Field("account") String account,@Field("code") String code, @Field("newPassword") String newPassword);

    //设置账号
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/setAcctno")
    Observable<ApiRootModel<String>> setAcctno(@Field("token") String token,@Field("acctno") String acctno);

    //绑定邮箱
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/setEmail")
    Observable<ApiRootModel<String>> setEmail(@Field("token") String token,@Field("email") String email,@Field("code") String code);


    //ali=支付宝，wx=微信
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/pay")
    Observable<ApiRootModel<String>> pay(@Field("account") String account,@Field("gid") String gid,@Field("type") String type);

    //修改昵称
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/modifyName")
    Observable<ApiRootModel<String>> modifyName(@Field("token") String token,@Field("name") String name);

    //上传头像 file 二进制
    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/modifyPic")
    Observable<ApiRootModel<String>> modifyPic(@Field("token") String token,@Field("file") String file);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/logout")
    Observable<ApiRootModel<String>> loginOut(@Field("token") String token);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/goods")
    Observable<ApiRootModel<String>> getGoods(@Field("pg") int pg);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/orderQuery")
    Observable<ApiRootModel<String>> orderQuery(@Field("token") String token, @Field("order") String order);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/signIn")
    Observable<ApiRootModel<String>> userSignIn(@Field("token") String token);

    @FormUrlEncoded
    @POST("api/user/1000/chuanyunxiazai/1/fen")
    Observable<ApiRootModel<String>> fen(@Field("token") String token, @Field("fenid") int fenid);

    @GET("video/random.php")
    Observable<ApiRootModel<TikTokRandomModel>> requestVideoRandom(@Query("limit") int limit);

    @GET("video/random.php")
    Observable<ApiRootModel<TikTokRandomModel>> requestVideoCategory(@Query("sort") int sort,@Query("limit") int limit);

    @GET("video/douyin.php")
    Observable<VideoInfo> requestDouYin(@Query("url") String url);

}
