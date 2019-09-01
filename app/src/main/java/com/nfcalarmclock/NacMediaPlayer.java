package com.nfcalarmclock;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import android.media.AudioDeviceInfo;

/**
 * Wrapper for the MediaPlayer class.
 */
public class NacMediaPlayer
	extends MediaPlayer
	implements MediaPlayer.OnCompletionListener,
		AudioManager.OnAudioFocusChangeListener
{

	/**
	 * Playlist object.
	 */
	public static class Playlist
	{

		/**
		 * List of music files.
		 */
		private List<NacSound> mPlaylist;

		/**
		 * Index corresponding to the current place in the playlist.
		 */
		private int mIndex;

		/**
		 * Repeat the playlist.
		 */
		private boolean mRepeat;

		/**
		 */
		public Playlist(Context context, String path)
		{
			this(context, path, false, false);
		}

		/**
		 */
		public Playlist(Context context, String path, boolean repeat,
			boolean shuffle)
		{
			this.mPlaylist = NacSound.getFiles(context, path);
			this.mIndex = 0;
			this.mRepeat = repeat;

			if (shuffle)
			{
				this.shuffle();
			}
		}

		/**
		 * @return The playlist index.
		 */
		public int getIndex()
		{
			return this.mIndex;
		}

		/**
		 * @return The next playlist track.
		 */
		public NacSound getNextTrack()
		{
			int size = this.getSize();
			int index = this.getIndex();
			int nextIndex = this.repeat() ? (index+1) % size : index+1;

			if ((size == 0) || (nextIndex >= size))
			{
				return null;
			}

			this.setIndex(nextIndex);

			return this.getTrack();
		}

		/**
		 * @return The playlist.
		 */
		public List<NacSound> getPlaylist()
		{
			return this.mPlaylist;
		}

		/**
		 * @return The size of the playlist.
		 */
		public int getSize()
		{
			List<NacSound> playlist = this.getPlaylist();

			return (playlist == null) ? 0 : playlist.size();
		}

		/**
		 * @return The current playlist track.
		 */
		public NacSound getTrack()
		{
			return this.getTrack(this.getIndex());
		}

		/**
		 * @return The playlist track.
		 */
		public NacSound getTrack(int index)
		{
			List<NacSound> playlist = this.getPlaylist();
			int size = this.getSize();

			return ((size > 0) && (index < size)) ? playlist.get(index) : null;
		}

		/**
		 * @return True if the playlist should be repeated and False otherwise.
		 */
		public boolean repeat()
		{
			return this.mRepeat;
		}

		/**
		 * Set the playlist index.
		 */
		public void setIndex(int index)
		{
			this.mIndex = index;
		}

		/**
		 * @return True if the playlist is shuffled and False otherwise.
		 */
		public void shuffle()
		{
			Collections.shuffle(this.mPlaylist);
		}

	}

	/**
	 * Application context.
	 */
	private Context mContext;

	/**
	 * Playlist container.
	 */
	private Playlist mPlaylist;

	/**
	 * Audio attributes.
	 */
	private NacAudio.Attributes mAttributes;

	/**
	 * Check if player was playing (caused by losing audio focus).
	 */
	private boolean mWasPlaying;

	/**
	 * Result values.
	 */
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ILLEGAL_ARGUMENT_EXCEPTION = -1;
	public static final int RESULT_ILLEGAL_STATE_EXCEPTION = -2;
	public static final int RESULT_IO_EXCEPTION = -3;
	public static final int RESULT_SECURITY_EXCEPTION = -4;

	/**
	 * Set the context.
	 */
	public NacMediaPlayer(Context context)
	{
		super();

		this.mContext = context;
		this.mPlaylist = null;
		this.mAttributes = new NacAudio.Attributes();
		this.mWasPlaying = false;
	}

	/**
	 * @return The context.
	 */
	private Context getContext()
	{
		return this.mContext;
	}

	/**
	 * @return The playlist.
	 */
	private Playlist getPlaylist()
	{
		return this.mPlaylist;
	}

	/**
	 * Check if the media player is playing.
	 */
	public boolean isPlayingWrapper()
	{
		try
		{
			return isPlaying();
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in isPlaying()");
			return false;
		}
	}

	/**
	 * Change media state when audio focus changes.
	 */
	@Override
	public void onAudioFocusChange(int focusChange)
	{
		//this.zPrintInfo(focusChange);
		Context context = this.getContext();
		NacAudio.Attributes attrs = this.mAttributes;
		this.mWasPlaying = this.isPlayingWrapper();

		attrs.setFocus(focusChange);
		attrs.revertDucking();

		if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
		{
			this.mWasPlaying = true;
			this.setVolume();
			this.startWrapper();
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
		{
			this.stopWrapper();
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
		{
			this.pauseWrapper();
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
		{
			attrs.duckVolume(context);
		}
	}

	/**
	 */
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		this.stopWrapper();

		Context context = this.getContext();
		Playlist playlist = this.getPlaylist();
		NacSound track = (playlist != null) ? playlist.getNextTrack() : null;

		if (track != null)
		{
			this.resetWrapper();
			this.play(track);
			return;
		}
		else
		{
			this.mPlaylist = null;
		}

		if (!isLooping())
		{
			this.resetWrapper();
			NacAudio.abandonAudioFocus(context, this);
		}
		else
		{
			this.prepareWrapper();
			this.startWrapper();
		}
	}

	/**
	 * Pause the media player.
	 */
	private int pauseWrapper()
	{
		try
		{
			pause();
			return this.RESULT_SUCCESS;
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in pause()");
			return this.RESULT_ILLEGAL_STATE_EXCEPTION;
		}
	}

	/**
	 * Play the media.
	 */
	public void play(NacAlarm alarm, boolean repeat, boolean shuffle)
	{
		String path = alarm.getSoundPath();

		this.mAttributes.setSource(alarm.getAudioSource());
		this.mAttributes.setVolume(alarm.getVolume());

		if (NacSound.isFilePlaylist(alarm.getSoundType()))
		{
			this.playPlaylist(path, repeat, shuffle);
		}
		else
		{
			this.play(path, repeat);
		}
	}

	/**
	 * @see play
	 */
	public void play(NacSound sound)
	{
		this.play(sound.getPath(), sound.getRepeat());
	}

	/**
	 * @see play
	 */
	//public void play(NacSound sound, boolean repeat)
	//{
	//	this.play(sound.getPath(), repeat);
	//}

	/**
	 * @see play
	 */
	//public void play(NacSound sound, String audioSource, int volume,
	//	boolean repeat)
	//{
	//	this.play(sound.getPath(), repeat);
	//}

	/**
	 * @see play
	 */
	//public void play(String media)
	//{
	//	int volume = this.getStreamVolume();

	//	this.play(media, volume, false);
	//}

	/**
	 * @see play
	 */
	public void play(String media, boolean repeat)
	{
		Context context = this.getContext();
		NacAudio.Attributes attrs = this.mAttributes;

		if (media.isEmpty())
		{
			return;
		}

		if(!NacAudio.requestAudioFocusGain(context, this, attrs))
		{
			//NacUtility.printf("Audio Focus NOT Granted!");
			return;
		}

		AudioAttributes audioAttributes = attrs.getAudioAttributes();
		String path = NacSound.getPath(context, media);

		// Can log each step for better granularity in case errors occur.
		try
		{
			if (this.isPlayingWrapper())
			{
				reset();
			}

			setDataSource(path);
			setLooping(repeat);
			setAudioAttributes(audioAttributes);
			setOnCompletionListener(this);
			prepare();
			this.setVolume();
			start();
		}
		catch (IllegalStateException | IOException | IllegalArgumentException | SecurityException e)
		{
			NacUtility.quickToast(context, "Unable to play selected file");
		}
	}

	/**
	 * Play a playlist.
	 */
	//public void playPlaylist(NacAlarm alarm, boolean repeat, boolean shuffle)
	public void playPlaylist(String path, boolean repeat, boolean shuffle)
	{
		Context context = this.getContext();
		this.mPlaylist = new Playlist(context, path, repeat, shuffle);
		Playlist playlist = this.getPlaylist();
		NacSound track = playlist.getTrack();

		if (track != null)
		{
			this.play(track);
		}
	}

	/**
	 * Prepare the media player
	 */
	private int prepareWrapper()
	{
		try
		{
			prepare();
			return this.RESULT_SUCCESS;
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in prepare()");
			return this.RESULT_ILLEGAL_STATE_EXCEPTION;
		}
		catch (IOException e)
		{
			NacUtility.printf("NacMediaPlayer : IOException caught in prepare()");
			return this.RESULT_IO_EXCEPTION;
		}
	}

	/**
	 * Reset the media player.
	 */
	private int resetWrapper()
	{
		try
		{
			reset();
			return this.RESULT_SUCCESS;
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in reset()");
			return this.RESULT_ILLEGAL_STATE_EXCEPTION;
		}
	}

	/**
	 * Set the volume.
	 */
	private void setVolume()
	{
		NacAudio.Attributes attrs = this.mAttributes;
		int volume = attrs.getVolume();
		float level = (float) (1 - (Math.log(100-volume) / Math.log(100)));

		if (volume < 0)
		{
			return;
		}

		try
		{
			setVolume(level, level);
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in setVolume : Unable to set volume %d (%f)", volume, level);
		}
	}

	/**
	 * Start the media player.
	 */
	private int startWrapper()
	{
		try
		{
			start();
			return this.RESULT_SUCCESS;
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in start()");
			return this.RESULT_ILLEGAL_STATE_EXCEPTION;
		}
	}

	/**
	 * Stop the media player
	 */
	public int stopWrapper()
	{
		try
		{
			stop();
			return this.RESULT_SUCCESS;
		}
		catch (IllegalStateException e)
		{
			NacUtility.printf("NacMediaPlayer : IllegalStateException caught in stop()");
			return this.RESULT_ILLEGAL_STATE_EXCEPTION;
		}
	}

	/**
	 * @return True if the player was playing before losing audio focus, and
	 *         False otherwise.
	 */
	public boolean wasPlaying()
	{
		return this.mWasPlaying;
	}

	// Temp method
	private void zPrintInfo(int focusChange)
	{
		NacUtility.printf("onAudioFocusChange! %d", focusChange);
		String change = "UNKOWN";

		if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
		{
			change = "GAIN";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
		{
			change = "LOSS";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
		{
			change = "LOSS_TRANSIENT";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
		{
			change = "LOSS_TRANSIENT_CAN_DUCK";
		}

		NacUtility.printf("NacMediaPlayer : onAudioFocusChange : AUDIOFOCUS_%s",
			change);
	}

}
