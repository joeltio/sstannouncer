package com.sst.announcements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.sst.announcements.Feed.Entry;

public class EntryActivity extends AppCompatActivity {

    public static final String ENTRY_EXTRA = "com.sst.announcements.EntryActivity.ENTRY_EXTRA";

    private Entry entryShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        this.entryShown = intent.getParcelableExtra(ENTRY_EXTRA);

        TextView title = (TextView) findViewById(R.id.entry_title);
        TextView author = (TextView) findViewById(R.id.entry_author);
        WebView content = (WebView) findViewById(R.id.entry_content);
        TextView published = (TextView) findViewById(R.id.entry_published);

        title.setText(this.entryShown.getTitle());
        author.setText(this.entryShown.getAuthorName());
        String addedCss = "<style>img{display: inline;height: auto;max-width: 100%}</style>";
        String addedJs = "<script>" +
                "document.addEventListener('DOMContentLoaded', function(event) {" +
                "    var imgs = document.getElementsByTagName('img');" +
                "    for (var i=0; i<imgs.length; i++) {" +
                "        console.log(imgs[i].parentElement.style.marginLeft);" +
                "        imgs[i].parentElement.style.marginLeft = '0px';" +
                "    }" +
                "});" +
                "</script>";
        content.loadData(addedCss + addedJs +
                this.entryShown.getContent(), "text/html; charset=utf-8", "utf-8");

        content.getSettings().setJavaScriptEnabled(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showImages = preferences.getBoolean(
                getResources().getString(R.string.pref_show_images), false);
        content.getSettings().setLoadsImagesAutomatically(showImages);

        published.setText(Entry.toShortDate(this.entryShown.getPublished()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_web_post) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.entryShown.getBloggerLink()));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
