package com.nfcalarmclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;

/**
 * Preference that allows the user to choose how frequent the time should be
 * said.
 */
public class NacSpeakFrequencyPreference
	extends Preference
	implements Preference.OnPreferenceClickListener,
		NacDialog.OnDismissListener
{

	/**
	 * Preference value.
	 */
	private int mValue;

	/**
	 */
	public NacSpeakFrequencyPreference(Context context)
	{
		this(context, null);
	}

	/**
	 */
	public NacSpeakFrequencyPreference(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	/**
	 */
	public NacSpeakFrequencyPreference(Context context, AttributeSet attrs,
		int style)
	{
		super(context, attrs, style);
		setLayoutResource(R.layout.nac_preference);
		setOnPreferenceClickListener(this);
	}

	/**
	 */
	@Override
	public CharSequence getSummary()
	{
		Context context = getContext();
		return NacSharedPreferences.getSpeakFrequencySummary(context,
			this.mValue);
	}

	/**
	 * Save the spinner index value.
	 */
	@Override
	public boolean onDismissDialog(NacDialog dialog)
	{
		this.mValue = ((NacSpinnerDialog)dialog).getValue();

		persistInt(this.mValue);
		notifyChanged();
		return true;
	}

	/**
	 * @return The default value.
	 */
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		Context context = getContext();
		NacSharedDefaults defaults = new NacSharedDefaults(context);
		return (Integer) a.getInteger(index,
			defaults.getSpeakFrequencyIndex());
	}

	/**
	 */
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		Context context = getContext();
		NacSpeakFrequencyDialog dialog = new NacSpeakFrequencyDialog();

		dialog.saveData(this.mValue);
		dialog.build(context);
		dialog.addOnDismissListener(this);
		dialog.show();
		return true;
	}

	/**
	 * Set the initial preference value.
	 */
	@Override
	protected void onSetInitialValue(Object defaultValue)
	{
		if (defaultValue == null)
		{
			this.mValue = getPersistedInt(this.mValue);
		}
		else
		{
			this.mValue = (Integer) defaultValue;
			persistInt(this.mValue);
		}
	}

}
