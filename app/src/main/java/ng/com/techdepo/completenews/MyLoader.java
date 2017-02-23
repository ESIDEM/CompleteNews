package ng.com.techdepo.completenews;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ng.com.techdepo.completenews.provider.FeedContract;

/**
 * Created by ESIDEM jnr on 2/20/2017.
 */

public class MyLoader extends CursorLoader {

    public static MyLoader newAllArticlesInstance(Context context) {
        return new MyLoader(context, FeedContract.Entry.buildDirUri());
    }

    public static MyLoader newInstanceForItemId(Context context, long itemId) {
        return new MyLoader(context, FeedContract.Entry.buildItemUri(itemId));
    }

    private MyLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, FeedContract.Entry.COLUMN_NAME_PUBLISHED+ " DESC");
    }

    public interface Query {
        String[] PROJECTION = {
                FeedContract.Entry._ID,
                FeedContract.Entry.COLUMN_NAME_TITLE,
                FeedContract.Entry.COLUMN_NAME_PUBLISHED,
                FeedContract.Entry.COLUMN_NAME_LINK,
                FeedContract.Entry.COLUMN_NAME_DESCRIPTION,
                FeedContract.Entry.COLUMN_NAME_IMAGE_URL,
                FeedContract.Entry.COLUMN_NAME_IMAGE_URL_2,
        };

        int COLUMN_ID = 0;
        int COLUMN_TITLE = 1;
        int COLUMN_PUB_DATE = 2;
        int COLUMN_LINK = 3 ;
        int COLUMN_DESC = 4;
        int COLUMN_PHOTO_URL = 5;
        int COLUMN_PHOTO_URL2 = 6;
    }
}
