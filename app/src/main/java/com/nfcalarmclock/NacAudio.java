package com.nfcalarmclock;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

/**
 * Audio container.
 */
public class NacAudio
{

	/**
	 * Audio attributes.
	 */
	public static class Attributes
	{

		/**
		 * Stream.
		 */
		private int mStream;

		/**
		 * Usage.
		 */
		private int mUsage;

		/**
		 * Audio focus.
		 */
		private int mFocus;

		/**
		 * Volume.
		 */
		private int mVolume;

		/**
		 * Previously set volume.
		 */
		private int mPreviousVolume;

		/**
		 * Ducking flag.
		 */
		private boolean mWasDucking;

		/**
		 */
		public Attributes()
		{
			this("");
		}

		/**
		 */
		public Attributes(NacAlarm alarm)
		{
			this(alarm.getAudioSource());
			this.setVolume(alarm.getVolume());
		}

		/**
		 */
		public Attributes(String source)
		{
			int focus = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
				? AudioManager.AUDIOFOCUS_NONE : 0;

			this.setFocus(focus);
			this.setSource(source);
			this.setVolume(-1);

			this.mWasDucking = false;
		}

		/**
		 * Duck the volume.
		 */
		public void duckVolume(Context context)
		{
			this.mPreviousVolume = this.getStreamVolume(context);
			this.mVolume = this.mPreviousVolume / 2;
			this.mWasDucking = true;
		}

		/**
		 * @return The audio attributes.
		 */
		public AudioAttributes getAudioAttributes()
		{
			int stream = this.getStream();
			int usage = this.getUsage();

			return new AudioAttributes.Builder()
				.setLegacyStreamType(stream)
				.setUsage(usage)
				.build();
		}

		/**
		 * @return The audio focus.
		 */
		public int getFocus()
		{
			return this.mFocus;
		}

		/**
		 * @return The audio stream.
		 */
		public int getStream()
		{
			return this.mStream;
		}

		/**
		 * @return The audio usage.
		 */
		public int getUsage()
		{
			return this.mUsage;
		}

		/**
		 * @return The volume.
		 */
		public int getVolume()
		{
			return this.mVolume;
		}

		/**
		 * @return The calculate volume level.
		 */
		public float getVolumeLevel()
		{
			int volume = this.getVolume();

			return (volume >= 0)
				? (float) (1 - (Math.log(100-volume) / Math.log(100)))
				: -1f;
		}

		/**
		 * @return The current stream volume.
		 */
		private int getStreamVolume(Context context)
		{
			int stream = this.getStream();

			return NacAudio.getAudioManager(context).getStreamVolume(stream);
		}

		/**
		 * Revert the effects of ducking.
		 */
		public void revertDucking()
		{
			if (this.wasDucking())
			{
				int temp = this.mPreviousVolume;
				this.mPreviousVolume = this.mVolume;
				this.mVolume = temp;
				this.mWasDucking = false;
			}
		}

		/**
		 * Set the audio focus.
		 */
		public void setFocus(int focus)
		{
			this.mFocus = focus;
		}

		/**
		 * Set the audio stream and usage.
		 */
		public void setSource(String source)
		{
			int stream = AudioManager.STREAM_MUSIC;
			int usage = AudioAttributes.USAGE_MEDIA;

			if ((source == null) || source.isEmpty() || source.equals("Music"))
			{
			}
			else if (source.equals("Alarm"))
			{
				stream = AudioManager.STREAM_ALARM;
				usage = AudioAttributes.USAGE_ALARM;
			}
			else if (source.equals("Notification"))
			{
				stream = AudioManager.STREAM_NOTIFICATION;
				usage = AudioAttributes.USAGE_NOTIFICATION;
			}
			else if (source.equals("Ringer"))
			{
				stream = AudioManager.STREAM_RING;
				usage = AudioAttributes.USAGE_MEDIA; // Might need to be different
			}
			else if (source.equals("System"))
			{
				stream = AudioManager.STREAM_SYSTEM;
				usage = AudioAttributes.USAGE_MEDIA;
			}

			this.setStream(stream);
			this.setUsage(usage);
		}

		/**
		 * Set the audio stream.
		 */
		public void setStream(int stream)
		{
			this.mStream = stream;
		}

		/**
		 * Set the audio usage.
		 */
		public void setUsage(int usage)
		{
			this.mUsage = usage;
		}

		/**
		 * Set the volume.
		 */
		public void setVolume(int volume)
		{
			this.mPreviousVolume = (volume >= 0) ? this.mVolume : volume;
			this.mVolume = volume;
		}

		/**
		 * @return True if the volume was ducked, and False otherwise.
		 */
		public boolean wasDucking()
		{
			return this.mWasDucking;
		}

	}

	/**
	 * Abandon audio focus.
	 */
	@SuppressWarnings("deprecation")
	public static int abandonAudioFocus(Context context,
		AudioManager.OnAudioFocusChangeListener listener)
	{
		AudioManager am = NacAudio.getAudioManager(context);
		int result = 0;

		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		//{
		//	AudioFocusRequest request = this.getAudioFocusRequest();
		//	result = am.abandonAudioFocus(request);
		//}
		//else
		//{
			result = am.abandonAudioFocus(listener);
		//}

		return result;
	}

	/**
	 * @return The audio focus request.
	 */
	public static AudioFocusRequest getAudioFocusRequest(
		AudioManager.OnAudioFocusChangeListener listener,
		NacAudio.Attributes attrs)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			AudioAttributes audioAttributes = attrs.getAudioAttributes();
			int focus = attrs.getFocus();

			return new AudioFocusRequest.Builder(focus)
				.setAudioAttributes(audioAttributes)
				.setOnAudioFocusChangeListener(listener)
				.build();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see getAudioManager
	 */
	//public AudioManager getAudioManager()
	//{
	//	Context context = this.getContext();

	//	return NacMediaPlayer.getAudioManager(context);
	//}

	/**
	 * @return The audio manager.
	 */
	public static AudioManager getAudioManager(Context context)
	{
		return (AudioManager) context.getSystemService(
			Context.AUDIO_SERVICE);
	}

	/**
	 * Request audio focus.
	 */
	@SuppressWarnings("deprecation")
	public static boolean requestAudioFocus(Context context,
		AudioManager.OnAudioFocusChangeListener listener,
		NacAudio.Attributes attrs)
	{
		AudioManager am = NacAudio.getAudioManager(context);
		int result = 0;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			AudioFocusRequest request = NacAudio.getAudioFocusRequest(listener,
				attrs);
			result = am.requestAudioFocus(request);
		}
		else
		{
			int stream = attrs.getStream();
			int focus = attrs.getFocus();
			result = am.requestAudioFocus(listener, stream, focus);
		}

		return (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
	}

	/**
	 * @see requestAudioFocus
	 */
	public static boolean requestAudioFocusGain(Context context,
		AudioManager.OnAudioFocusChangeListener listener,
		NacAudio.Attributes attrs)
	{
		attrs.setFocus(AudioManager.AUDIOFOCUS_GAIN);

		return NacAudio.requestAudioFocus(context, listener, attrs);
	}

	/**
	 * @see requestAudioFocus
	 */
	public static boolean requestAudioFocusTransient(Context context,
		AudioManager.OnAudioFocusChangeListener listener,
		NacAudio.Attributes attrs)
	{
		attrs.setFocus(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

		return NacAudio.requestAudioFocus(context, listener, attrs);
	}

}
