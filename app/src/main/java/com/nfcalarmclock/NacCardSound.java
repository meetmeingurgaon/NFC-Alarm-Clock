package com.nfcalarmclock;

import android.content.Context;
import android.view.View;

/**
 * The sound to play when the alarm is activated. Users can change this by
 * selecting the sound view.
 */
public class NacCardSound
	implements View.OnClickListener,NacSoundDialog.OnItemClickListener
{

	/**
	 * Sound.
	 */
	 private NacImageTextButton mSoundView;

	/**
	 * Alarm.
	 */
	 private NacAlarm mAlarm;

	/**
	 */
	public NacCardSound(View root)
	{
		this.mSoundView = (NacImageTextButton) root.findViewById(R.id.nacSound);
		this.mAlarm = null;

		this.mSoundView.setOnClickListener(this);
	}

	/**
	 * Initialize the sound view.
	 */
	public void init(NacAlarm alarm)
	{
		this.mAlarm = alarm;

		this.setSound();
	}

	/**
	 * Display the dialog that allows users to set the name of the alarm.
	 */
	@Override
	public void onClick(View v)
	{
		Context context = v.getContext();
		NacSoundPromptDialog dialog = new NacSoundPromptDialog();

		dialog.build(context, R.layout.dlg_sound_prompt);
		dialog.setOnItemClickListener(this);
		dialog.show();
	}

	/**
	 * Handle the sound item when it has been selected.
	 */
	@Override
	public void onItemClick(NacSound sound)
	{
		String path = sound.path;

		this.mAlarm.setSound(path);
		this.mAlarm.changed();
		this.setSound();
	}

	/**
	 * Set the sound.
	 */
	public void setSound()
	{
		String path = this.mAlarm.getSound();
		String name = NacAlarm.getSoundNameMessage();
		boolean focus = false;

		if (!path.isEmpty())
		{
			Context context = this.mSoundView.getContext();
			name = this.mAlarm.getSoundName(context);
			focus = true;
		}

		NacUtility.printf("Sound : %s (%s)", path, name);
		this.mSoundView.setText(name);
		this.mSoundView.setFocus(focus);
	}

}