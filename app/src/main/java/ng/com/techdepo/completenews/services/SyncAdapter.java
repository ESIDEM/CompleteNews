package ng.com.techdepo.completenews.services;

import android.accounts.Account;
import android.annotation.TargetApi;

import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.SyncResult;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import ng.com.techdepo.completenews.Utils;
import ng.com.techdepo.completenews.net.RSSFeed;
import ng.com.techdepo.completenews.net.RSSItem;
import ng.com.techdepo.completenews.net.RSSParser;
import ng.com.techdepo.completenews.provider.FeedContract;

/**
 * Created by ESIDEM jnr on 2/14/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "SyncAdapter";

    // Array list for list view
    ArrayList<HashMap<String, String>> rssItemList = new ArrayList<HashMap<String,String>>();

    RSSParser rssParser = new RSSParser();

    List<RSSItem> rssItems = new ArrayList<RSSItem>();

    RSSFeed rssFeed;

   String[] mSelectionArgs = new String[] { "title" };

    private static final String[] PROJECTION = new String[] {
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_GUID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_LINK,
            FeedContract.Entry.COLUMN_NAME_PUBLISHED,
            FeedContract.Entry.COLUMN_NAME_DESCRIPTION,
            FeedContract.Entry.COLUMN_NAME_IMAGE_URL};



    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ENTRY_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_LINK = 3;
    public static final int COLUMN_PUBLISHED = 4;


    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_GUID = "guid"; // not used


    private String rss_link = "http://www.thesundaily.my/rss/latest";
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
        new loadRSSFeedItems().execute(rss_link);
    }


    class loadRSSFeedItems extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(
//                    ListRSSItemsActivity.this);
//            pDialog.setMessage("Loading recent articles...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];


            // list of rss items
            rssItems = rssParser.getRSSFeedItems(rss_url);

            // Build hash table of incoming entries
            HashMap<String, RSSItem> entryMap = new HashMap<String, RSSItem>();

            // looping through each item
            for(RSSItem item : rssItems){
             // HashMap<String, RSSItem> entryMap = new HashMap<String, RSSItem>();
                entryMap.put(item.getGuid(), item);

               insertEntry(item);


            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            //  pDialog.dismiss();
        }
    }

    private void insertEntry(RSSItem entry) {

        if (newsExist(entry.getLink(), entry.getTitle()))
            return;


        ContentValues values = new ContentValues();
        values.clear();
        values.put(FeedContract.Entry.COLUMN_NAME_GUID, entry.getGuid());
        values.put(FeedContract.Entry.COLUMN_NAME_TITLE, entry.getTitle());
        values.put(FeedContract.Entry.COLUMN_NAME_LINK, entry.getLink());
        values.put(FeedContract.Entry.COLUMN_NAME_DESCRIPTION, entry.getDescription());
        values.put(FeedContract.Entry.COLUMN_NAME_IMAGE_URL, entry.get_image());
        values.put(FeedContract.Entry.COLUMN_NAME_PUBLISHED, entry.getPubdate());

        mContentResolver.insert(FeedContract.Entry.CONTENT_URI, values);
    }





    private boolean newsExist(String link, String title) {
        Cursor c = mContentResolver.query(
                FeedContract.Entry.CONTENT_URI,
                new String[]{FeedContract.Entry.COLUMN_NAME_GUID},
                FeedContract.Entry.COLUMN_NAME_LINK + " = ?",
                new String[]{link},
                null
        );


        boolean exists = c.getCount() > 0;
        if (exists) {
            c.moveToFirst();
            String guid = c.getString(c.getColumnIndex(FeedContract.Entry.COLUMN_NAME_GUID));
            String[] tits = guid.split("|");
            List<String> strings = Arrays.asList(tits);
            if (!strings.contains(title)) {
                String[] catList = new String[strings.size() + 1];
                for (int i = 0; i < strings.size(); i++) {
                    catList[i] = strings.get(i);
                }
                catList[strings.size()] = title;
                String newTitle = Utils.join(catList, "|");
                ContentValues values = new ContentValues();
                values.put(FeedContract.Entry.COLUMN_NAME_GUID, newTitle);
                mContentResolver.update(FeedContract.Entry.CONTENT_URI, values, FeedContract.Entry.COLUMN_NAME_LINK + " = ?", new String[]{link});
            }
        }
        c.close();
        return exists;
    }
}
