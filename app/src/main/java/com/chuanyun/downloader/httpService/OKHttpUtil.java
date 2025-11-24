package com.chuanyun.downloader.httpService;
import java.util.Map;

public final class OKHttpUtil {

    //设置默认参数
    private static void addDefaultParams(Map<String, String> params) {
        if (defaultParams != null) {
            params.putAll(defaultParams);
        }
    }


    public static void setDefaultParams(Map<String, String> params) {
        OKHttpUtil.defaultParams = params;
    }

    private static Map<String, String> defaultParams;
}
