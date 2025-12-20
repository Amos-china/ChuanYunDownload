package com.chuanyun.downloader.httpService;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.chuanyun.downloader.utils.RC4Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    private Type mType;


    FastJsonResponseBodyConverter(Type type) {
        this.mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(@NonNull ResponseBody value) throws IOException {

        try {

            String body = "";
            String valueData = value.string().trim();

            if (TextUtils.isEmpty(valueData)) {
                body = "{"
                        + "\"code\":0,"
                        + "\"msg\":\"请求数据是空的\""
                        + "}";
            }else {

                if (isJson(valueData)) {
                    body = valueData;
                }else {
                    String jsonValue = RC4Utils.rc4Decrypt(valueData,"6903");
                    body = "{"
                            + "\"code\":200,"
                            + "\"msg\":\"请求成功\","
                            + "\"data\":" + jsonValue
                            + "}";
                }
            }

            return parseObjectWhitBody(body);

        } catch (Exception exception){
            value.close();

           String body = "{"
                    + "\"code\":500,"
                    + "\"msg\": \"请求超时或找不到服务器\""
                    + "}";
           return parseObjectWhitBody(body);
        }
    }

    private boolean isJson(String string) {
        try {
            new JSONObject(string);
            return true;
        }catch (JSONException exception) {
            try {
                new JSONArray(string);
                return true;
            }catch (JSONException e) {
                return false;
            }
        }
    }

    private T parseObjectWhitBody(String body) {
        T resultInfo;
        if (mType != null) {
            resultInfo = JSON.parseObject(body, mType);
        } else {
            resultInfo = JSON.parseObject(body, new TypeReference<T>() {
            }); //范型已被擦除 --！
        }
        return resultInfo;
    }
}
