package sstinc.sstannouncer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import sstinc.sstannouncer.Feed.Entry;

public class FeedArrayAdapter extends ArrayAdapter<Entry> {
    public FeedArrayAdapter(Activity context, List<Entry> list) {
        super(context, R.layout.list_entry_row, list);
    }

    private static class ViewHolder {
        TextView author_textView;
        TextView title_textView;
        TextView description_textView;
        TextView date_textView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Entry entry = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_entry_row,
                    parent, false);

            viewHolder = new ViewHolder();
            viewHolder.author_textView = (TextView) convertView.findViewById(R.id.author_textView);
            viewHolder.title_textView = (TextView) convertView.findViewById(R.id.title_textView);
            viewHolder.description_textView = (TextView) convertView.findViewById(R.id.description_textView);
            viewHolder.date_textView = (TextView) convertView.findViewById(R.id.date_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.author_textView.setText(entry.getAuthorName());

        // Reduce the amount of text in the textView by splitting before newline if possible
        if (entry.getContent().contains("\n")) {
            viewHolder.title_textView.setText(entry.getTitle());
            viewHolder.description_textView.setText(entry.getContent().split("\n")[0]);
        } else {
            viewHolder.title_textView.setText(entry.getTitle());
            viewHolder.description_textView.setText(entry.getContent());
        }

        try {
            viewHolder.date_textView.setText(Entry.toShortDate(entry.getPublished()));
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), e.getMessage());
            viewHolder.date_textView.setText("ERROR");
        }
        return convertView;
    }
}
