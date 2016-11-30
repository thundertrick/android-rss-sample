package com.magician.xuyang.androidrsssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

public class MainActivity extends AppCompatActivity implements RssManager.feedCallback {

    private final static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RssManager.getInstance(this).loadTest(this);
    }

    @Override
    public void onFeedUpdate(RSSFeed feed) {
        if (feed == null) {
            return;
        }
        Log.d(TAG, feed.getDescription());
        for (RSSItem item : feed.getItems()) {
            Log.d(TAG, item.getTitle());
        }
    }
}
