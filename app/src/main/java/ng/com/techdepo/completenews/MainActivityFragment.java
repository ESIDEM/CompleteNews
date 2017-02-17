package ng.com.techdepo.completenews;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ng.com.techdepo.completenews.accounts.GenericAccountService;
import ng.com.techdepo.completenews.provider.FeedContract;
import ng.com.techdepo.completenews.services.SyncUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EntryListFragment";

    /**
     * Cursor adapter for controlling ListView results.
     */
    private SimpleCursorAdapter mAdapter;
    private Object mSyncObserverHandle;
    private ListView listView;
    private ImageView newsImage;
    private TextView imageUrl;

    private static final String[] PROJECTION = new String[]{
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_LINK,
            FeedContract.Entry.COLUMN_NAME_PUBLISHED,
            FeedContract.Entry.COLUMN_NAME_DESCRIPTION,
            FeedContract.Entry.COLUMN_NAME_IMAGE_URL


    };

//    // Column indexes. The index of a column in the Cursor is the same as its relative position in
//    // the projection.
//    /** Column index for _ID */
//    private static final int COLUMN_ID = 0;
//    /** Column index for title */
//    private static final int COLUMN_TITLE = 1;
//    /** Column index for link */
//    private static final int COLUMN_URL_STRING = 2;
//    /** Column index for published */
//    private static final int COLUMN_PUBLISHED = 3;
//
//    /**
//     * List of Cursor columns to read from when preparing an adapter to populate the ListView.
//     */



    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

                String[] FROM_COLUMNS = new String[]{
                FeedContract.Entry.COLUMN_NAME_TITLE,
                FeedContract.Entry.COLUMN_NAME_PUBLISHED,
               FeedContract.Entry.COLUMN_NAME_DESCRIPTION,
               FeedContract.Entry.COLUMN_NAME_IMAGE_URL,


        };

        newsImage = (ImageView) rootView.findViewById(R.id.image_view);
        imageUrl = (TextView) rootView.findViewById(R.id.image_url);
        /**
         * List of Views which will be populated by Cursor data.
         */
                 int[] TO_FIELDS = new int[]{
               R.id.title_view,
              R.id.date_view,
              R.id.description_view,
                R.id.image_url
                 };

        getLoaderManager().initLoader(0, null, this);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.news_item,
                null,
                FROM_COLUMNS,
                TO_FIELDS,
                0



        );

        listView = (ListView) rootView.findViewById(R.id.listView1);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(i);

                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.Entry._ID));

                Intent feedDetail = new Intent(getContext(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("rowId", rowId);
                feedDetail.putExtras(bundle);
                startActivity(feedDetail);
            }
        });



        return rootView;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Create account, if needed
        SyncUtils.CreateSyncAccount(activity);
    }




    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // We only have one loader, so we can ignore the value of i.
        // (It'll be '0', as set in onCreate().)
        return new CursorLoader(getActivity(),  // Context
                FeedContract.Entry.CONTENT_URI, // URI
                PROJECTION,                // Projection
                null,                           // Selection
                null,                           // Selection args
                FeedContract.Entry.COLUMN_NAME_PUBLISHED + " desc"); // Sort
    }

    /**
     * Move the Cursor returned by the query into the ListView adapter. This refreshes the existing
     * UI with the data in the Cursor.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    /**
     * Called when the ContentObserver defined for the content provider detects that data has
     * changed. The ContentObserver resets the loader, and then re-runs the loader. In the adapter,
     * set the Cursor value to null. This removes the reference to the Cursor, allowing it to be
     * garbage-collected.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }


    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */

        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                      //  setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, FeedContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, FeedContract.CONTENT_AUTHORITY);
                   // setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

}
