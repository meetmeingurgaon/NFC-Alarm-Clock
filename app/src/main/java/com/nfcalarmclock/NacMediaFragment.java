package com.nfcalarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Media fragment for ringtones and music files.
 */
public class NacMediaFragment
	extends Fragment
	implements View.OnClickListener
{

	/**
	 * Alarm.
	 */
	private NacAlarm mAlarm;

	/**
	 * Media path.
	 */
	private String mMediaPath;

	/**
	 * Media player.
	 */
	private NacMediaPlayer mPlayer;

	/**
	 * The initial selection flag.
	 */
	private boolean mInitialSelection;

	/**
	 */
	public NacMediaFragment()
	{
		super();

		this.mAlarm = null;
		this.mMediaPath = null;
		this.mPlayer = null;
		this.mInitialSelection = true;
	}

	/**
	 * @return The alarm.
	 */
	protected NacAlarm getAlarm()
	{
		return this.mAlarm;
	}

	/**
	 * @return The alarm.
	 */
	protected String getMedia()
	{
		return this.mMediaPath;
	}

	/**
	 * @return The media path.
	 */
	protected String getMediaPath()
	{
		NacAlarm alarm = this.getAlarm();
		String media = this.getMedia();

		if (alarm != null)
		{
			return alarm.getMediaPath();
		}
		else if (media != null)
		{
			return media;
		}
		else
		{
			return "";
		}
	}

	/**
	 * @return The media player.
	 */
	protected NacMediaPlayer getMediaPlayer()
	{
		return this.mPlayer;
	}

	/**
	 * @return True if the ID corresponds to an action button, and False
	 * otherwise.
	 */
	public boolean isActionButton(int id)
	{
		return ((id == R.id.clear) || (id == R.id.cancel) || (id == R.id.ok));
	}

	/**
	 * @return True if this is the first time the fragment is being selected,
	 *         and False otherwise.
	 */
	public boolean isInitialSelection()
	{
		return this.mInitialSelection;
	}

	/**
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();

		if (args != null)
		{
			this.mAlarm = NacBundle.getAlarm(args);
			this.mMediaPath = NacBundle.getMedia(args);
		}
	}

	/**
	 */
	@Override
	public void onClick(View view)
	{
		FragmentActivity activity = getActivity();
		int id = view.getId();

		if (id == R.id.clear)
		{
			this.setMedia("");
			this.safeReset();
		}
		else if (id == R.id.cancel)
		{
			activity.finish();
		}
		else if (id == R.id.ok)
		{
			Context context = getContext();
			NacAlarm alarm = this.getAlarm();
			String media = this.getMedia();

			if (alarm != null)
			{
				NacDatabase.BackgroundService.updateAlarm(context, alarm);
			}
			else
			{
				Intent intent = NacIntent.toIntent(media);
				activity.setResult(Activity.RESULT_OK, intent);
			}

			activity.finish();
		}
	}

	/**
	 * Called when the fragment is first selected by the user.
	 */
	protected void onInitialSelection()
	{
		this.mInitialSelection = false;
	}

	/**
	 */
	@Override
	public void onPause()
	{
		this.releasePlayer();
		super.onPause();
	}

	/**
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		this.setupMediaPlayer();
	}

	/**
	 */
	@Override
	public void onStop()
	{
		//this.releasePlayer();
		super.onStop();
	}

	/**
	 * Called when the fragment is selected by the user.
	 */
	protected void onSelected()
	{
		if (this.isInitialSelection())
		{
			this.onInitialSelection();
		}
	}

	/**
	 * Release the player.
	 */
	protected void releasePlayer()
	{
		NacMediaPlayer player = this.getMediaPlayer();

		if (player != null)
		{
			player.releaseWrapper();

			this.mPlayer = null;
		}
	}

	/**
	 * Play audio from the media player safely.
	 */
	protected int safePlay(Uri contentUri, boolean repeat)
	{
		String path = contentUri.toString();

		if (path.isEmpty() || !path.startsWith("content://"))
		{
			return -1;
		}

		this.safeReset();
		this.setMedia(path);
		NacMediaPlayer player = this.getMediaPlayer();

		if (player == null)
		{
			player = this.setupMediaPlayer();

			if (player == null)
			{
				return -1;
			}
		}

		player.play(contentUri, repeat);

		return 0;
	}

	/**
	 * Reset the media player safely.
	 */
	protected int safeReset()
	{
		NacMediaPlayer player = this.getMediaPlayer();

		if (player == null)
		{
			player = this.setupMediaPlayer();

			return (player != null) ? 0 : -1;
		}

		player.reset();

		return 0;
	}

	/**
	 * Set the alarm sound.
	 *
	 * Use Alarm when editing an alarm card, and use media path when editing a
	 * preference.
	 */
	protected void setMedia(String media)
	{
		Context context = getContext();
		NacAlarm alarm = this.getAlarm();

		if (alarm != null)
		{
			alarm.setMedia(context, media);
		}
		else
		{
			this.mMediaPath = media;
		}
	}

	/**
	 * Setup action buttons.
	 */
	protected void setupActionButtons(View root)
	{
		NacSharedPreferences shared = new NacSharedPreferences(getContext());
		Button clear = (Button) root.findViewById(R.id.clear);
		Button cancel = (Button) root.findViewById(R.id.cancel);
		Button ok = (Button) root.findViewById(R.id.ok);

		clear.setTextColor(shared.getThemeColor());
		cancel.setTextColor(shared.getThemeColor());
		ok.setTextColor(shared.getThemeColor());
		clear.setOnClickListener(this);
		cancel.setOnClickListener(this);
		ok.setOnClickListener(this);
	}

	/**
	 * Setup the media player.
	 */
	protected NacMediaPlayer setupMediaPlayer()
	{
		Context context = getContext();
		int focus = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
		this.mPlayer = new NacMediaPlayer(context, focus);

		return this.mPlayer;
	}

}
