package com.nfcalarmclock;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;;

/**
 * Preference that indicates whether easy snooze should be enabled or not.
 */
public class NacPreferenceEasySnooze
	extends Preference
	implements Preference.OnPreferenceClickListener,
		CompoundButton.OnCheckedChangeListener
{

	/**
	 * Check box button.
	 */
	private CheckBox mCheckBox;

	/**
	 * Check value.
	 */
	protected boolean mValue;

	/**
	 * Summary text when enabling/disabling the preference.
	 */
	protected String[] mSummaryState;

	/**
	 */
	public NacPreferenceEasySnooze(Context context)
	{
		this(context, null);
	}

	/**
	 */
	public NacPreferenceEasySnooze(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	/**
	 */
	public NacPreferenceEasySnooze(Context context, AttributeSet attrs, int style)
	{
		super(context, attrs, style);
		setLayoutResource(R.layout.nac_preference_widget);
		setOnPreferenceClickListener(this);

		Resources.Theme theme = context.getTheme();
		TypedArray a = theme.obtainStyledAttributes(attrs,
			R.styleable.NacPreference, 0, 0);
		this.mSummaryState = new String[2];
		this.mSummaryState[0] = a.getString(R.styleable.NacPreference_summaryEnabled);
		this.mSummaryState[1] = a.getString(R.styleable.NacPreference_summaryDisabled);

		if (this.mSummaryState[0] == null || this.mSummaryState[1] == null)
		{
			throw new Resources.NotFoundException();
		}
	}

	/**
	 * @return The desired summary text.
	 */
	@Override
	public CharSequence getSummary()
	{
		return (this.mValue) ? this.mSummaryState[0] : this.mSummaryState[1];
	}

	/**
	 * Setup the checkbox and summary text.
	 */
	@Override
	protected void onBindView(View v)
	{
		super.onBindView(v);

		Context context = v.getContext();
		NacSharedPreferences shared = new NacSharedPreferences(context);
		int[][] states = new int[][] {
			new int[] { android.R.attr.state_checked},
			new int[] {-android.R.attr.state_checked}};
		int[] colors = new int[] {shared.getThemeColor(), Color.LTGRAY};
		ColorStateList colorStateList = new ColorStateList(states, colors);
		this.mCheckBox = (CheckBox) v.findViewById(R.id.widget);

		this.mCheckBox.setChecked(this.mValue);
		this.mCheckBox.setOnCheckedChangeListener(this);
		this.mCheckBox.setButtonTintList(colorStateList);

		this.setSummary();
	}

	/**
	 * Handle checkbox changes.
	 */
	@Override
	public void onCheckedChanged(CompoundButton button, boolean state)
	{
		this.mValue = state;

		this.mCheckBox.setChecked(this.mValue);
		this.setSummary();
		persistBoolean(this.mValue);
	}

	/**
	 * @return The default value.
	 */
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return (boolean) a.getBoolean(index,
			NacSharedPreferences.DEFAULT_REQUIRE_NFC);
	}

	/**
	 * Allow users to select the whole preference to change the checkbox.
	 */
	@Override
	public boolean onPreferenceClick(Preference pref)
	{
		this.mCheckBox.performClick();

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
			this.mValue = getPersistedBoolean(this.mValue);
		}
		else
		{
			this.mValue = (boolean) defval;

			persistBoolean(this.mValue);
		}
	}

	/**
	 * Set the summary text.
	 */
	public void setSummary()
	{
		View root = (View) this.mCheckBox.getParent();
		TextView tv = root.findViewById(android.R.id.summary);
		CharSequence summary = this.getSummary();

		if (tv != null)
		{
			tv.setText(summary);
		}
	}

}