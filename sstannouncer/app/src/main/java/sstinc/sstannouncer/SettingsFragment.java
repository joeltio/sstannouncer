package sstinc.sstannouncer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;

import sstinc.sstannouncer.event.Event;
import sstinc.sstannouncer.event.EventController;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.pref_data_usage);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getResources().getString(R.string.pref_refresh_rate))) {
            int refreshRateMilliseconds = Integer.parseInt(sharedPreferences.getString(
                    getResources().getString(R.string.pref_refresh_rate), ""))*1000;

            if (FeedFragment.eventController == null) {
                FeedFragment.eventController = new EventController();
            }
            // frequency in milliseconds
            double frequency = 1/refreshRateMilliseconds;
            FeedFragment.eventController.raise(new Event(getString(
                    R.string.event_resource_service_change_frequency), new Date(), Double.toString(frequency)));
        }
    }
}
