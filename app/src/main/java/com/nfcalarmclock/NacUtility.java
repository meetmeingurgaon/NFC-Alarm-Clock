package com.nfcalarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import java.util.Locale;

/**
 * @brief NFC Alarm Clock Utility class.
 * 
 * @details Composed of static methods that can be used for various things.
 */
public class NacUtility
{

	/**
	 * Capitalize the first letter in the string.
	 */
	public static String capitalize(String word)
	{
		Locale locale = Locale.getDefault();
		return String.format(locale, "%1$s%2$s",
			word.substring(0, 1).toUpperCase(locale),
			word.substring(1));
	}

	/**
	 * Determine the height of the view.
	 * 
	 * @param  v  The view.
	 * 
	 * @return The height of the view.
	 */
	public static int getHeight(View view)
	{
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
			view.getLayoutParams();
		int margins = lp.topMargin + lp.bottomMargin;
		int height = view.getMeasuredHeight();

		return height+margins;
	}

	/**
	 * Convert the id to a theme attribute color.
	 * 
	 * @return A theme attribute color.
	 */
	public static int getThemeAttrColor(Context context, int id)
	{
		TypedValue tv = new TypedValue();
		Resources.Theme theme = context.getTheme();
		boolean success = theme.resolveAttribute(id, tv, true);

		return (tv.resourceId == 0)
			? tv.data
			: ContextCompat.getColor(context, tv.resourceId);
	}

	/**
	 * Determine the width of the view.
	 * 
	 * @param  v  The view.
	 * 
	 * @return The width of the view.
	 */
	public static int getWidth(View view)
	{
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
			view.getLayoutParams();
		int margins = lp.leftMargin + lp.rightMargin;
		int width = view.getMeasuredWidth();

		return width+margins;
	}

	/**
	 * @brief Wrapper for Log object to print to the logcat easily.
	 */
	public static void print(String name, String string)
	{
		Log.i(name, string);
	}

	/**
	 * Print the string to logcat using this class' name.
	 */
	public static void print(String string)
	{
		NacUtility.print("NacUtility", string);
	}

	/**
	 * Format a print statement to logcat.
	 */
	public static void printf(String format, Object... args)
	{
		NacUtility.print(String.format(format, args));
	}

	/**
	 * Print to logcat with default string formatting.
	 */
	public static void printf(String string)
	{
		NacUtility.printf("%s", string);
	}

	/**
	 * Set background color of the view.
	 */
	public static void setBackground(View view, int id)
	{
		Context context = view.getContext();
		int bg = NacUtility.getThemeAttrColor(context, id);

		view.setBackground(null);
		view.setBackgroundColor(bg);
	}

	/**
	 * Display a snackbar with the given message.
	 *
	 * @param  root  The root view.
	 * @param  message  The message to display.
	 * @param  action  The action message.
	 * @param  listener  The callback to run when the action is clicked.
	 */
	public static Snackbar snackbar(View root, String message, String action,
		View.OnClickListener listener)
	{
		Context context = root.getContext();
		int color = NacUtility.getThemeAttrColor(context,
			R.attr.colorCardAccent);
		Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);

		if (!action.isEmpty())
		{
			if (listener == null)
			{
				listener = new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
					}
				};
			}

			snackbar.setAction(action, listener);
		}

		snackbar.setActionTextColor(color);
		snackbar.show();

		return snackbar;
	}

	/**
	 * Display a snackbar with the given message.
	 *
	 * @param  activity  The main activity.
	 * @param  message  The message to display.
	 * @param  action  The action message.
	 * @param  listener  The callback to run when the action is clicked.
	 */
	public static Snackbar snackbar(Activity activity, String message, String action,
		View.OnClickListener listener)
	{
		CoordinatorLayout root = activity.findViewById(R.id.activity_main);
		return NacUtility.snackbar(root, message, action, listener);
	}

	/**
	 * Create a toast that displays for a short period of time.
	 */
	public static Toast quickToast(View view, String message)
	{
		Context context = view.getContext();
		return NacUtility.quickToast(context, message);
	}

	/**
	 * Create a toast that displays for a short period of time.
	 */
	public static Toast quickToast(Context context, String message)
	{
		return NacUtility.toast(context, message, Toast.LENGTH_SHORT);
	}

	/**
	 * Create a toast.
	 */
	public static Toast toast(View view, String message)
	{
		Context context = view.getContext();
		return NacUtility.toast(context, message);
	}

	/**
	 * Create a toast.
	 */
	public static Toast toast(Context context, String message)
	{
		return NacUtility.toast(context, message, Toast.LENGTH_LONG);
	}

	/**
	 * Create a toast.
	 */
	public static Toast toast(Context context, String message, int duration)
	{
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
		return toast;
	}

}
