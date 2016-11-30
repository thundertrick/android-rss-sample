package com.magician.xuyang.androidrsssample;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
        rx.Observable
                .create(new rx.Observable.OnSubscribe<RSSFeed>() {
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
                            callback.onFeedUpdate(rssFeed);
                        }
                        Log.d(TAG, rssFeed.getDescription());
                    }
                });
    }

    public void loadTest(final feedCallback callback) {
        load(uri_test, callback);
    }

    interface feedCallback {
        void onFeedUpdate(RSSFeed feed);
    }
}
