package com.effatheresoft.androidpractice.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.effatheresoft.androidpractice.R
import com.effatheresoft.androidpractice.ui.home.HomeViewModel

class SettingsFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var darkModePreference: SwitchPreferenceCompat
    private lateinit var namePreference: EditTextPreference

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        addPreferencesFromResource(R.xml.setting_preferences)
        darkModePreference = findPreference<SwitchPreferenceCompat>("dark_mode") as SwitchPreferenceCompat
        namePreference = findPreference<EditTextPreference>("settings_key_name") as EditTextPreference
        val sharedPreferences = preferenceManager.sharedPreferences!!
        darkModePreference.isChecked = sharedPreferences.getBoolean("dark_mode", false)
        namePreference.summary = sharedPreferences.getString("settings_key_name", "-")
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when(key) {
            "dark_mode" -> { darkModePreference.isChecked = sharedPreferences.getBoolean("dark_mode", false) }
            "settings_key_name" -> { namePreference.summary = sharedPreferences.getString("settings_key_name", "-") }
        }
    }


}