package ng.com.techdepo.completenews;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ng.com.techdepo.completenews.provider.FeedContract;

public class DetailsActivity extends AppCompatActivity {

    private WebView mWebView;

    private Context context;
    private  long id;
    public String title;
    public String url;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getLong("rowId");
        getStaffDetail();


        setTitle(title);


        mWebView = (WebView) findViewById(R.id.detail_web_view);


        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(context, "Cannot load page", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        mWebView.loadUrl(url);
    }


    private void getStaffDetail(){

        String[] projection = {
                FeedContract.Entry._ID,
                FeedContract.Entry.COLUMN_NAME_TITLE,
                FeedContract.Entry.COLUMN_NAME_LINK};

        Uri uri = Uri.parse(FeedContract.Entry.CONTENT_URI + "/" + id);
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.Entry.COLUMN_NAME_TITLE));
            String mUrl = cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.Entry.COLUMN_NAME_LINK));

            title = mTitle;
            url = mUrl;

        }


    }

}
