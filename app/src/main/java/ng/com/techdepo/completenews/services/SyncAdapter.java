package ng.com.techdepo.completenews.services;

import android.accounts.Account;
import android.annotation.TargetApi;

import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


import ng.com.techdepo.completenews.net.RSSItem;
import ng.com.techdepo.completenews.net.RSSParser;
import ng.com.techdepo.completenews.provider.FeedContract;

/**
 * Created by ESIDEM jnr on 2/21/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter{

    public static final String TAG = "SyncAdapter";


    RSSParser rssParser = new RSSParser();

    List<RSSItem> rssItems = new ArrayList<RSSItem>();



    /**
     * URL to fetch content from during a sync.
     *
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;


    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();

    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link android.content.AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {


        rssItems = rssParser.parse();
        mContentResolver.delete(FeedContract.Entry.CONTENT_URI,null,null);

        HashMap<String, RSSItem> entryMap = new HashMap<String, RSSItem>();
        for(RSSItem item : rssItems){

            entryMap.put(item.getGuid(), item);

            insertEntry(item);


        }


    }




    private void insertEntry(RSSItem entry) {




        ContentValues values = new ContentValues();
        values.clear();
        values.put(FeedContract.Entry.COLUMN_NAME_GUID, entry.getGuid());
        values.put(FeedContract.Entry.COLUMN_NAME_TITLE, entry.getTitle());
        values.put(FeedContract.Entry.COLUMN_NAME_LINK, entry.getLink());
        values.put(FeedContract.Entry.COLUMN_NAME_DESCRIPTION, entry.getDescription());
        values.put(FeedContract.Entry.COLUMN_NAME_IMAGE_URL, entry.getImageUrl());
        values.put(FeedContract.Entry.COLUMN_NAME_PUBLISHED, entry.getPubDate());
        values.put(FeedContract.Entry.COLUMN_NAME_COMPLETE_DES, entry.getCom_description());
        values.put(FeedContract.Entry.COLUMN_NAME_IMAGE_URL_2, entry.getImageUrl2());

        mContentResolver.insert(FeedContract.Entry.CONTENT_URI, values);
    }


}
