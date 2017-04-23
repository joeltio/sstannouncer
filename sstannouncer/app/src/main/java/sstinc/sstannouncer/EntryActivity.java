package sstinc.sstannouncer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import java.text.ParseException;

import sstinc.sstannouncer.Feed.Entry;

public class EntryActivity extends AppCompatActivity {

    public static String ENTRY_EXTRA = "sstinc.sstannouncer.EntryActivity.ENTRY_EXTRA";
    public static String ENTRY_ACTIVITY_PREFERENCE = "entry_activity_preference";
    public static String SHOW_IMAGES_PREFERENCE = "show_images";

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
        content.loadData(this.entryShown.getContent(), "text/html; charset=utf-8", "utf-8");

        content.getSettings().setJavaScriptEnabled(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showImages = preferences.getBoolean(
                getResources().getString(R.string.pref_show_images), false);
        content.getSettings().setLoadsImagesAutomatically(showImages);

        try {
            published.setText(Entry.toShortDate(this.entryShown.getPublished()));
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
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
