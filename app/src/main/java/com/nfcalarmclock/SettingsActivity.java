package com.nfcalarmclock;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * @brief Display all the configurable settings for the app.
 */
public class SettingsActivity
	extends AppCompatActivity
{

	/**
	 * @see SettingsFragment
	 *
	 * @detail Use a fragment to display the settings in order to allow the
	 *         back button in the action bar to be used
	 *         (from AppCompatActivity). This allows the user to go back to
	 *         the main activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		FragmentManager manage = getFragmentManager();
		FragmentTransaction trans = manage.beginTransaction();

		trans.replace(android.R.id.content, new SettingsFragment());
		trans.commit();
	}

	/**
	 * @brief Settings fragment.
	 */
	public static class SettingsFragment
		extends PreferenceFragment
	{

		/**
		 * @brief Construct the settings page.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}

	}

}
