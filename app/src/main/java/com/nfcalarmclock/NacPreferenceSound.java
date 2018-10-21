package com.nfcalarmclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Preference that displays the sound prompt dialog.
 */
public class NacPreferenceSound
	extends Preference
	implements Preference.OnPreferenceClickListener,NacSoundDialog.OnItemClickListener
{

	/**
	 * Path of the sound.
	 */
	protected String mValue;

	/**
	 */
	public NacPreferenceSound(Context context)
	{
		this(context, null);
	}

	/**
	 */
	public NacPreferenceSound(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	/**
	 */
	public NacPreferenceSound(Context context, AttributeSet attrs, int style)
	{
		super(context, attrs, style);
		setLayoutResource(R.layout.pref_sound);
		setOnPreferenceClickListener(this);
	}

	/**
	 * @return The summary text.
	 */
	@Override
	public CharSequence getSummary()
	{
		Context context = getContext();
		String path = this.mValue;
		Alarm alarm = new Alarm();

		alarm.setSound(path);

		String name = alarm.getSoundName(context);

		return (!name.isEmpty()) ? name : Alarm.getSoundNameDefault();
	}

	/**
	 * Set the summary text.
	 */
	@Override
	protected void onBindView(View v)
	{
		super.onBindView(v);
		this.setSummary(this.getSummary());
	}

	/**
	 * @return The default value.
	 */
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return (String) a.getString(index);
	}

	/**
	 * Capture which item in the list was selected.
	 */
	@Override
	public void onItemClick(NacSound sound)
	{
		String path = sound.path;
		//String name = sound.name;

		//if (path.isEmpty() || name.isEmpty())
		if (path.isEmpty())
		{
			return;
		}

		this.mValue = path;

		this.setSummary(this.getSummary());
		persistString(this.mValue);
	}

	/**
	 * When the preference is clicked, display the dialog.
	 */
	@Override
	public boolean onPreferenceClick(Preference pref)
	{
		Context context = getContext();
		NacSoundPromptDialog dialog = new NacSoundPromptDialog();

		dialog.build(context, R.layout.dlg_sound_prompt);
		dialog.setOnItemClickListener(this);
		dialog.show();

		return true;
	}

	/**
	 * Set the initial preference value.
	 */
	@Override
	protected void onSetInitialValue(boolean restore, Object defval)
	{
		if (restore)
		{
			this.mValue = getPersistedString(this.mValue);
		}
		else
		{
			this.mValue = (String) defval;

			persistString(this.mValue);
		}
	}

}
