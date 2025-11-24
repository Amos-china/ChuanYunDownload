package com.chuanyun.downloader.models;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class BehaviorFileInfoModel {

    private BehaviorSubject<TorrentFileInfoModel> fileModelSubject;

    private PublishSubject<TorrentFileInfoModel> infoModelPublishSubject;

    private io.reactivex.rxjava3.subjects.PublishSubject<List<TorrentFileInfoModel>> listPublishSubject;

    public void setInfoModelPublishSubject(PublishSubject<TorrentFileInfoModel> infoModelPublishSubject) {
        this.infoModelPublishSubject = infoModelPublishSubject;
    }

    public PublishSubject<TorrentFileInfoModel> getInfoModelPublishSubject() {
        return infoModelPublishSubject;
    }

    public void setFileModelSubject(BehaviorSubject<TorrentFileInfoModel> fileModelSubject) {
        this.fileModelSubject = fileModelSubject;
    }

    public BehaviorSubject<TorrentFileInfoModel> getFileModelSubject() {
        return fileModelSubject;
    }


    private void createData() {

    }


    public static class DataViewModel  {

        public void loadData() {
            Single<List<AppInfoModel>> appInfoModelSingle = AppInfoModel.getAppInfoModel();
            Single<List<AppMessageInfo>> messageSingle = appInfoModelSingle.toObservable().flatMapIterable(list -> list)
                    .flatMapSingle(appInfoModel -> AppMessageInfo.getMessageInfoModel(appInfoModel).onErrorReturnItem(new AppMessageInfo()))
                    .toList();

        }

        public void requestNewMessage() {
           Disposable disposable = getNewMessage().subscribe(AppMessageInfo::insertNewMessage);

        }

        public Observable<List<AppMessageInfo>> getNewMessage() {
            return Observable.create(emitter -> {
                List<AppMessageInfo> appMessageInfoList = new ArrayList<>();
                emitter.onNext(appMessageInfoList);
                emitter.onComplete();
            });
        }

    }

    public static class AppInfoModel {
        private int id;
        private AppMessageInfo message;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setMessage(AppMessageInfo message) {
            this.message = message;
        }

        public AppMessageInfo getMessage() {
            return message;
        }

        public static Single<List<AppInfoModel>> getAppInfoModel() {
            return Single.create(si -> {
                List<AppInfoModel> appInfoModels = new ArrayList<>();
                si.onSuccess(appInfoModels);
            });
        }
    }

    public static class AppMessageInfo {
        private int id;
        private long time;
        private int appId;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public int getAppId() {
            return appId;
        }

        public static Single<AppMessageInfo> getMessageInfoModel(AppInfoModel appInfoModel) {
            return Single.just(appInfoModel)
                    .map(infoModel -> new AppMessageInfo());
        }

        public static void insertNewMessage(List<AppMessageInfo> appMessageInfoList) {

        }
    }


}
