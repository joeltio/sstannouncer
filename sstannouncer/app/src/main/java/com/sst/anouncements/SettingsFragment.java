package com.sst.anouncements;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.toolbar_settings));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
//        addPreferencesFromResource(R.xml.pref_data_usage);
    }

    private void setRefreshRate(int refreshRate) {
        UpdateService.schedule(getActivity(), refreshRate);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (isAdded() && s.equals(getResources().getString(R.string.pref_refresh_rate))) {
            int refreshRate = Integer.parseInt(sharedPreferences.getString(
                    getResources().getString(R.string.pref_refresh_rate), ""));

            if (refreshRate != 0) {
                setRefreshRate(refreshRate);
            } else {
                setRefreshRate(-1);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_data_usage);
    }
}
