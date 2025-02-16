package com.dicoding.courseschedule.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder
import com.dicoding.courseschedule.util.NightMode

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var dailyReminder: DailyReminder
    private var notificationEnabled = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                dailyReminder.setDailyReminder(requireContext())
            } else {
                val notificationPreference =
                    findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
                notificationPreference?.isChecked = false
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        dailyReminder = DailyReminder()

        val themePreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            val selectedMode = NightMode.valueOf(newValue.toString().uppercase())
            updateTheme(selectedMode.value)
        }

        val notificationPreference =
            findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
        notificationPreference?.setOnPreferenceChangeListener { _, newValue ->
            notificationEnabled = newValue as Boolean
            if (notificationEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            dailyReminder.setDailyReminder(requireContext())
                        }

                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                            // Optionally show rationale to the user
                        }

                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                } else {
                    dailyReminder.setDailyReminder(requireContext())
                }
            } else {
                dailyReminder.cancelAlarm(requireContext())
            }
            true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        requireActivity().let {
            AppCompatDelegate.setDefaultNightMode(nightMode)
            it.recreate()
        }
        return true
    }
}
