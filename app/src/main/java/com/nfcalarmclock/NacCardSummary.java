package com.nfcalarmclock;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Summary information for an alarm card.
 */
public class NacCardSummary
{

	/**
	 * Context.
	 */
	private Context mContext;

	/**
	 * Alarm.
	 */
	private NacAlarm mAlarm;

	/**
	 * Text of days to repeat.
	 */
	private TextView mDays;

	/**
	 * Name of the alarm.
	 */
	private TextView mName;

	/**
	 * Card padding.
	 */
	private int mCardPadding;

	///**
	// * Summary days.
	// */
	//private class Days
	//{
	//}

	///**
	// * Summary name.
	// */
	//private class Days
	//{
	//}

	/**
	 */
	public NacCardSummary(Context context, View root)
	{
		this.mContext = context;
		this.mAlarm = null;
		this.mDays = (TextView) root.findViewById(R.id.nac_summary_days);
		this.mName = (TextView) root.findViewById(R.id.nac_summary_name);

		RelativeLayout header = root.findViewById(R.id.nac_header);
		this.mCardPadding = header.getPaddingStart() + header.getPaddingEnd();
	}

	/**
	 * Ellipsize the name.
	 */
	private void ellipsize()
	{
		RelativeLayout.LayoutParams params = this.getNameLayoutParams();
		int maxWidth = this.getNameMaxWidth();
		int width = NacUtility.getWidth(this.mName);

		params.width = (width > maxWidth) ? maxWidth : width;

		this.mName.setLayoutParams(params);
	}

	/**
	 * @return The alarm.
	 */
	private NacAlarm getAlarm()
	{
		return this.mAlarm;
	}

	/**
	 * @return The card padding.
	 */
	private int getCardPadding()
	{
		return this.mCardPadding;
	}

	/**
	 * @return The context.
	 */
	private Context getContext()
	{
		return this.mContext;
	}

	/**
	 * @return The layout params of the name.
	 */
	private RelativeLayout.LayoutParams getNameLayoutParams()
	{
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
			this.mName.getLayoutParams();
		
		return (params != null) ? params : new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
	}

	/**
	 * @return The max width of the summary name before it gets ellipsized.
	 */
	private int getNameMaxWidth()
	{
		int screenWidth = this.getScreenWidth();
		int padding = this.getCardPadding();
		int textsize = (int) this.mName.getTextSize();
		int summaryDays = this.mDays.getText().length() * textsize / 2;
		int expandImage = 3 * (int) this.getResources().getDimension(
			R.dimen.isz_expand);

		return screenWidth - summaryDays - padding - expandImage;
	}

	/**
	 * @return The context resources.
	 */
	public Resources getResources()
	{
		return this.getContext().getResources();
	}

	/**
	 * @return The screen width.
	 */
	private int getScreenWidth()
	{
		return this.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * Initialize the summary information.
	 */
	public void init(NacSharedPreferences shared, NacAlarm alarm)
	{
		this.mAlarm = alarm;

		this.set(shared);
	}

	/**
	 * Set the summary name and days.
	 */
	public void set(NacSharedPreferences shared)
	{
		this.setDays(shared);
		this.setName();
	}

	/**
	 * Set the summary colors.
	 */
	public void setColor(NacSharedPreferences shared)
	{
		int daysColor = shared.getDaysColor();
		int nameColor = shared.getNameColor();

		this.mDays.setTextColor(daysColor);
		this.mName.setTextColor(nameColor);
	}

	/**
	 * Set the repeat days text.
	 */
	public void setDays(NacSharedPreferences shared)
	{
		NacAlarm alarm = this.getAlarm();
		String string = NacCalendar.Days.toString(alarm,
			shared.getMondayFirst());

		this.mDays.setText(string);
	}

	/**
	 * Set ellipsis for the summary name if it is too long.
	 */
	public void setName()
	{
		RelativeLayout.LayoutParams params = this.getNameLayoutParams();
		NacAlarm alarm = this.getAlarm();
		String name = alarm.getName();
		String text = name + "  ";
		int margin = this.getResources().getDimensionPixelSize(R.dimen.sp_text);

		if (name.isEmpty())
		{
			text = "";
			margin = 0;
		}

		params.setMarginStart(margin);
		this.mName.setText(text);
		this.mName.setLayoutParams(params);
		this.ellipsize();
	}

}