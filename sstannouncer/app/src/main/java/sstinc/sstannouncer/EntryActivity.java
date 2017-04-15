package sstinc.sstannouncer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;

import sstinc.sstannouncer.Feed.Entry;

public class EntryActivity extends AppCompatActivity {

    public static String ENTRY_EXTRA = "sstinc.sstannouncer.EntryActivity.ENTRY_EXTRA";

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
        Entry entry = intent.getParcelableExtra(ENTRY_EXTRA);

        TextView title = (TextView) findViewById(R.id.entry_title);
        TextView author = (TextView) findViewById(R.id.entry_author);
        TextView content = (TextView) findViewById(R.id.entry_content);
        TextView published = (TextView) findViewById(R.id.entry_published);

        title.setText(entry.getTitle());
        author.setText(entry.getAuthorName());
        content.setText(Html.fromHtml(entry.getContent()));
        try {
            published.setText(Entry.toShortDate(entry.getPublished()));
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
        }

        return super.onOptionsItemSelected(item);
    }
}
