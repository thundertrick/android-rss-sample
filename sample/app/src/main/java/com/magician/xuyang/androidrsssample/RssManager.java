package com.magician.xuyang.androidrsssample;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xuyang on 16/10/8.
 */
public class RssManager {

    private static RssManager sInstance;
    private static final String TAG = "RssManager";
    private Context mCtx;
    private RSSReader mReader;
    private String uri_test = "http://feeds.bbci.co.uk/news/world/rss.xml";
    private String uri_test_base = "http://feeds.bbci.co.uk";

    private RssManager(Context context) {
        mCtx = context;
        mReader = new RSSReader();
        Log.d(TAG, "RssManager init");
    }

    public static RssManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RssManager.class) {
                Context appCtx = context.getApplicationContext();
                sInstance = new RssManager(appCtx);
            }
        }
        return sInstance;
    }

    public void load(final String url, final feedCallback callback) {
        Observable
                .create(new Observable.OnSubscribe<RSSFeed>() {
                    @Override
                    public void call(Subscriber<? super RSSFeed> subscriber) {
                        try {
                            RSSFeed feed = mReader.load(url);
                            Log.d(TAG, feed.getDescription());
                            subscriber.onNext(feed);
                        } catch (RSSReaderException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RSSFeed>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(RSSFeed rssFeed) {
                        Log.d(TAG, "onNext");
                        if (callback != null) {
                            callback.onFeedUpdate(rssFeed.getDescription());
                        }
                        Log.d(TAG, rssFeed.getDescription());
                    }
                });
    }

    public void loadTest(final feedCallback callback) {
//        load(uri_test, callback);
        final String url = uri_test;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url(uri_test)
                .build();
        //new call
        final Call call = mOkHttpClient.newCall(request);
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {
                        call.enqueue(new Callback()
                        {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                subscriber.onNext(response.body().string());
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                subscriber.onError(e);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(String respondBody) {
                        Log.d(TAG, "onNext");
                        if (callback != null) {
                            callback.onFeedUpdate(respondBody);
                        }
                        Log.d(TAG, respondBody);
                    }
                });
    }

    interface feedCallback {
        void onFeedUpdate(String feed);
    }
}
