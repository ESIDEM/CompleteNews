package ng.com.techdepo.completenews.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ESIDEM jnr on 2/14/2017.
 */

public class FeedContract {

    private FeedContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "ng.com.techdepo.completenews";

    /**
     * Base URI. (content://ng.com.techdepo.completenews)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.completenews.entries";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.completenews.entry";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "entry";


        public static final String COLUMN_NAME_TITLE = "title";


        public static final String COLUMN_NAME_LINK = "link";
        /**
         * Date article was published.
         */
        public static final String COLUMN_NAME_PUBLISHED = "published";

        public static final String COLUMN_NAME_GUID = "guid";

        public static final String COLUMN_NAME_DESCRIPTION = "description";

        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
    }
}
