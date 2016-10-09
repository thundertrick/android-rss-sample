package com.magician.xuyang.androidrsssample;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

/**
 * Created by xuyang on 16/10/8.
 */
public class RssManager {

    private static RssManager sInstance;
    private static final String TAG = "RssManager";
    private Context mCtx;
    private Handler mHandler;
    private RSSReader mReader;

    private RssManager(Context context) {
        mCtx = context;
        mHandler = new Handler();
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

    public void loadTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri = "http://feeds.bbci.co.uk/news/world/rss.xml";
                try {
                    RSSFeed feed = mReader.load(uri);
                    Log.d(TAG, feed.getDescription());
                } catch (RSSReaderException e) {
                    //pass
                }
            }
        }).start();
    }
}
