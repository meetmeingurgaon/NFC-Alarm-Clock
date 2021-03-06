package com.nfcalarmclock;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

/**
 */
public class NacMediaActivity
	extends FragmentActivity
	implements TabLayout.OnTabSelectedListener,
		ActivityCompat.OnRequestPermissionsResultCallback
{

	/**
	 * View pager.
	 */
	private ViewPager mPager;

	/**
	 * Adapter for the view pager.
	 */
	private NacPagerAdapter mAdapter;

	/**
	 * Tab layout.
	 */
	private TabLayout mTabLayout;

	/**
	 * List of fragments.
	 */
	private Fragment[] mFragments;

	/**
	 * Alarm.
	 */
	private NacAlarm mAlarm;

	/**
	 * Media path.
	 */
	private String mMediaPath;

	/**
	 * Current tab position.
	 */
	private int mPosition;

	/**
	 * The tab titles.
	 */
	//private static final String[] mTitles = new String[] { "Browse",
	//	"Ringtone", "Spotify" };
	private final String[] mTitles = new String[2];

	/**
	 * Select a fragment.
	 */
	private void fragmentSelected(Fragment selectedFragment)
	{
		int position = this.getPosition();
		Fragment tabFragment = this.getFragment(position);

		if ((tabFragment == null) || (selectedFragment == null)
			|| (selectedFragment != tabFragment))
		{
			return;
		}

		((NacMediaFragment)selectedFragment).onSelected();
	}

	/**
	 * @return The alarm.
	 */
	private NacAlarm getAlarm()
	{
		return this.mAlarm;
	}

	/**
	 * @return The fragment at the given position.
	 */
	private Fragment getFragment(int position)
	{
		return this.getFragments()[position];
	}

	/**
	 * @return The list of fragments.
	 */
	private Fragment[] getFragments()
	{
		return this.mFragments;
	}

	/**
	 * @return The view page adapter.
	 */
	private NacPagerAdapter getPagerAdapter()
	{
		return this.mAdapter;
	}

	/**
	 * @return The current tab position.
	 */
	private int getPosition()
	{
		return this.mPosition;
	}

	/**
	 * @return The media path.
	 */
	private String getMedia()
	{
		return this.mMediaPath;
	}

	/**
	 * @return The media type.
	 */
	private int getMediaType()
	{
		NacAlarm alarm = this.getAlarm();
		String media = this.getMedia();

		if (alarm != null)
		{
			return alarm.getMediaType();
		}
		else if (media != null)
		{
			return NacMedia.getType(this, media);
		}
		else
		{
			return NacMedia.TYPE_NONE;
		}
	}

	/**
	 * @return The tab layout.
	 */
	private TabLayout getTabLayout()
	{
		return this.mTabLayout;
	}

	/**
	 * @return The array of titles.
	 */
	private String[] getTitles()
	{
		return this.mTitles;
	}

	/**
	 * @return The view pager.
	 */
	private ViewPager getViewPager()
	{
		return this.mPager;
	}

	/**
	 */
	@Override
	public void onAttachFragment(Fragment fragment)
	{
		super.onAttachFragment(fragment);

		Fragment[] list = this.getFragments();

		if (list == null)
		{
			this.mFragments = new Fragment[this.mTitles.length];
			list = this.mFragments;
		}

		if (fragment instanceof NacMusicFragment)
		{
			list[0] = fragment;
		}
		else if (fragment instanceof NacRingtoneFragment)
		{
			list[1] = fragment;
		}
		//else if (fragment instanceof NacSpotifyFragment)
		//{
		//	list[2] = fragment;
		//}

		this.fragmentSelected(fragment);
	}

	/**
	 */
	@Override
	public void onBackPressed()
	{
		int position = this.getPosition();
		NacMusicFragment musicFragment = (NacMusicFragment)
			this.getFragments()[0];

		if (position == 0)
		{
			if ((musicFragment != null) && musicFragment.backPressed())
			{
				return;
			}
		}

		super.onBackPressed();
	}

	/**
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sound);

		Intent intent = getIntent();
		FragmentManager manager = getSupportFragmentManager();
		NacSharedConstants cons = new NacSharedConstants(this);
		this.mAlarm = NacIntent.getAlarm(intent);
		this.mMediaPath = NacIntent.getMedia(intent);
		this.mPager = (ViewPager) findViewById(R.id.act_sound);
		this.mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
		this.mTitles[0] = cons.getActionBrowse();
		this.mTitles[1] = cons.getAudioSources().get(3);
		//this.mAdapter = new NacPagerAdapter(manager, this.mAlarm, this.mTitles);
		this.mAdapter = new NacPagerAdapter(manager,
			FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.mFragments = new Fragment[this.mTitles.length];
		this.mPosition = 0;

		this.setupPager();
		this.setTabColors();
		this.selectTab();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
		String[] permissions, int[] grantResults)
	{
		if (requestCode == NacMusicFragment.READ_REQUEST_CODE)
		{
			if ((grantResults.length > 0)
				&& (grantResults[0] == PackageManager.PERMISSION_GRANTED))
			{
				Fragment fragment = this.getFragment(0);

				if (fragment != null)
				{
					getSupportFragmentManager().beginTransaction()
						.detach(fragment)
						.attach(fragment)
						.commitAllowingStateLoss();
					return;
				}
			}

			this.selectTab(1);
		}
	}

	/**
	 */
	@Override
	public void onTabReselected(Tab tab)
	{
	}

	/**
	 */
	@Override
	public void onTabSelected(Tab tab)
	{
		int position = tab.getPosition();
		Fragment fragment = this.getFragment(position);
		this.mPosition = position;

		this.fragmentSelected(fragment);
	}

	/**
	 */
	@Override
	public void onTabUnselected(Tab tab)
	{
	}

	/**
	 * Select the tab that the fragment activity should start on.
	 */
	private void selectTab()
	{
		int type = this.getMediaType();

		if (NacMedia.isNone(type))
		{
			this.selectTab(1);
		}
		else if (NacMedia.isFile(type) || NacMedia.isDirectory(type))
		{
			this.selectTab(0);
		}
		else if (NacMedia.isRingtone(type))
		{
			this.selectTab(1);
		}
		else if (NacMedia.isSpotify(type))
		{
			this.selectTab(2);
		}
	}

	/**
	 * Select the tab at the given index.
	 */
	private void selectTab(int position)
	{
		TabLayout tabLayout = this.getTabLayout();

		if (tabLayout != null)
		{
			Tab tab = tabLayout.getTabAt(position);

			if (tab != null)
			{
				tab.select();
				onTabSelected(tab);
			}
		}
	}

	/**
	 * Set the tab colors.
	 */
	private void setTabColors()
	{
		NacSharedPreferences shared = new NacSharedPreferences(this);
		NacSharedDefaults defaults = new NacSharedDefaults(this);
		TabLayout tabLayout = this.getTabLayout();

		tabLayout.setSelectedTabIndicatorColor(shared.getThemeColor());
		tabLayout.setTabTextColors(defaults.getColor(),
			shared.getThemeColor());
	}

	/**
	 * Setup the pager.
	 */
	@SuppressWarnings("deprecation")
	private void setupPager()
	{
		ViewPager pager = this.getViewPager();
		NacPagerAdapter adapter = this.getPagerAdapter();
		TabLayout tabLayout = this.getTabLayout();

		pager.setAdapter(adapter);
		tabLayout.setupWithViewPager(pager);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			tabLayout.addOnTabSelectedListener(this);
		}
		else
		{
			tabLayout.setOnTabSelectedListener(this);
		}
	}

	/**
	 */
	//public static class NacPagerAdapter
	public class NacPagerAdapter
		extends FragmentPagerAdapter
	{

		///**
		// * Alarm.
		// */
		//private final NacAlarm mAlarm;

		///**
		// * Tab titles.
		// */
		//private final String[] mTitles;

		/**
		 */
		//public NacPagerAdapter(FragmentManager fragmentManager, NacAlarm alarm,
		//	String[] titles)
		public NacPagerAdapter(FragmentManager fragmentManager, int behavior)
		{
			super(fragmentManager, behavior);
		}

		/**
		 * @return The number of items to swipe through.
		 */
		@Override
		public int getCount()
		{
			//return this.getTitles().length;
			return getTitles().length;
		}

		/**
		 */
		@Override
		public Fragment getItem(int position)
		{
			NacAlarm alarm = getAlarm();
			String media = getMedia();

			if (position == 0)
			{
				if (alarm != null)
				{
					return NacMusicFragment.newInstance(alarm);
				}
				else if (media != null)
				{
					return NacMusicFragment.newInstance(media);
				}
			}
			else if (position == 1)
			{
				if (alarm != null)
				{
					return NacRingtoneFragment.newInstance(alarm);
				}
				else if (media != null)
				{
					return NacRingtoneFragment.newInstance(media);
				}
			}
			//else if (position == 2)
			//{
			//	if (alarm != null)
			//	{
			//		return NacSpotifyFragment.newInstance(alarm);
			//	}
			//	else if (media!= null)
			//	{
			//		return NacSpotifyFragment.newInstance(media);
			//	}
			//}

			return null;
		}

		/**
		 */
		@Override
		public CharSequence getPageTitle(int position)
		{
			//return this.getTitles()[position];
			return getTitles()[position];
		}

		///**
		// * @return The tab titles.
		// */
		//private String[] getTitles()
		//{
		//	return this.mTitles;
		//}

	}

}

