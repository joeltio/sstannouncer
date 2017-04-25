package com.sst.anouncements;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;

import java.util.Date;

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

    private void setRefreshRate(int refreshRate) {
        double frequency = 1.0 / refreshRate;

        String eventFrequencyString = getResources().getString(
                R.string.event_resource_service_change_frequency);
        FeedFragment.eventController.raise(new Event(eventFrequencyString, new Date(),
                Double.toString(frequency)));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getResources().getString(R.string.pref_refresh_rate))) {
            int refreshRate = Integer.parseInt(sharedPreferences.getString(
                    getResources().getString(R.string.pref_refresh_rate), ""));

            if (FeedFragment.eventController == null) {
                FeedFragment.eventController = new EventController();
            }
            if (refreshRate != 0) {
                setRefreshRate(refreshRate);
            } else {
                setRefreshRate(-1);
            }
        }
    }
}
