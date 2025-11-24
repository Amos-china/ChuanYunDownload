package com.chuanyun.downloader.bat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class TestConfigModel  {

    public <T> T getData(String json) {
        return JSON.parseObject(json,new TypeReference<T>(){});
    }

    static void a(String s) {
        TestConfigModel configModel = new TestConfigModel();
        configModel.getData(s);

    }
}
