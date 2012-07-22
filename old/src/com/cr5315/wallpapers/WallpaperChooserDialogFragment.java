package com.cr5315.wallpapers;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import com.cr5315.wallpapers.R;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class WallpaperChooserDialogFragment extends DialogFragment implements
AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

	private static final String TAG = "Launcher.WallpaperChooserDialogFragment";
	private static final String EMBEDDED_KEY = "com.cr5315.wallpapers."
			+ "WallpaperChooserDialogFragment.EMBEDDED_KEY";

	private boolean mEmbedded;
	private Bitmap mBitmap = null;

	private ArrayList<Integer> mThumbs;
	private ArrayList<Integer> mImages;
	private WallpaperLoader mLoader;
	private WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();

	public static WallpaperChooserDialogFragment newInstance() {
		WallpaperChooserDialogFragment fragment = new WallpaperChooserDialogFragment();
		fragment.setCancelable(true);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(EMBEDDED_KEY)) {
			mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY);
		} else {
			mEmbedded = isInLayout();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EMBEDDED_KEY, mEmbedded);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			mLoader.cancel(true);
			mLoader = null;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		Activity activity = getActivity();
		if (activity != null) {
			activity.finish();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		findWallpapers();

		// TODO: The following code is not exercised right now and may be removed
		// if the dialog version is not needed.
		/*
	final View v = getActivity().getLayoutInflater().inflate(
        R.layout.wallpaper_chooser, null, false);

	GridView gridView = (GridView) v.findViewById(R.id.gallery);
	gridView.setOnItemClickListener(this);
	gridView.setAdapter(new ImageAdapter(getActivity()));

	final int viewInset =
        getResources().getDimensionPixelSize(R.dimen.alert_dialog_content_inset);

	FrameLayout wallPaperList = (FrameLayout) v.findViewById(R.id.wallpaper_list);
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	builder.setNegativeButton(R.string.wallpaper_cancel, null);
	builder.setTitle(R.string.wallpaper_dialog_title);
	builder.setView(wallPaperList,
	viewInset, viewInset, viewInset, viewInset); return builder.create();
		 */
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		findWallpapers();

		if (mEmbedded) {
			View view = inflater.inflate(R.layout.wallpaper_chooser, container, false);
			view.setBackgroundDrawable(mWallpaperDrawable);

			final Gallery gallery = (Gallery) view.findViewById(R.id.gallery);
			gallery.setCallbackDuringFling(false);
			gallery.setOnItemSelectedListener(this);
			gallery.setAdapter(new ImageAdapter(getActivity()));

			View setButton = view.findViewById(R.id.set);
			setButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectWallpaper(gallery.getSelectedItemPosition());
				}
			});
			return view;
		}
		return null;
	}

	private void selectWallpaper(int position) {
		try {
			WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(
					Context.WALLPAPER_SERVICE);
			wpm.setResource(mImages.get(position));
			Activity activity = getActivity();
			activity.setResult(Activity.RESULT_OK);
			activity.finish();
		} catch (IOException e) {
			Log.e(TAG, "Failed to set wallpaper: " + e);
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectWallpaper(position);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			mLoader.cancel();
		}
		mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	private void findWallpapers() {
		mThumbs = new ArrayList<Integer>(24);
		mImages = new ArrayList<Integer>(24);

		final Resources resources = getResources();
		final String packageName = resources.getResourcePackageName(R.array.wallpapers);

		addWallpapers(resources, packageName, R.array.wallpapers);
		addWallpapers(resources, packageName, R.array.extra_wallpapers);
	}

	private void addWallpapers(Resources resources, String packageName, int list) {
		final String[] extras = resources.getStringArray(list);
		for (String extra : extras) {
			int res = resources.getIdentifier(extra, "drawable", packageName);
			if (res != 0) {
				final int thumbRes = resources.getIdentifier(extra + "_small",
						"drawable", packageName);

				if (thumbRes != 0) {
					mThumbs.add(thumbRes);
					mImages.add(res);
				}
			}
		}
	}

	private class ImageAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
		private LayoutInflater mLayoutInflater;

		ImageAdapter(Activity activity) {
			mLayoutInflater = activity.getLayoutInflater();
		}

		public int getCount() {
			return mThumbs.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
			} else {
				view = convertView;
			}

			ImageView image = (ImageView) view.findViewById(R.id.wallpaper_image);

			int thumbRes = mThumbs.get(position);
			image.setImageResource(thumbRes);
			Drawable thumbDrawable = image.getDrawable();
			if (thumbDrawable != null) {
				thumbDrawable.setDither(true);
			} else {
				Log.e(TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
						+ position);
			}
			
			return view;
		}
	}

	class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
		BitmapFactory.Options mOptions;

		WallpaperLoader() {
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
			mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			if (isCancelled()) return null;
			try {
				return BitmapFactory.decodeResource(getResources(),
						mImages.get(params[0]), mOptions);
			} catch (OutOfMemoryError e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap b) {
			if (b == null) return;

			if (!isCancelled() && !mOptions.mCancel) {
				if (mBitmap != null) {
					mBitmap.recycle();
				}

				View v = getView();
				if (v != null) {
					mBitmap = b;
					mWallpaperDrawable.setBitmap(b);
					v.postInvalidate();
				} else {
					mBitmap = null;
					mWallpaperDrawable.setBitmap(null);
				}
				mLoader = null;
			} else {
				b.recycle();
			}
		}

		void cancel() {
			mOptions.requestCancelDecode();
			super.cancel(true);
		}
	}
	
	static class WallpaperDrawable extends Drawable {

		Bitmap mBitmap;
		int mIntrinsicWidth;
		int mIntrinsicHeight;

		void setBitmap(Bitmap bitmap) {
			mBitmap = bitmap;
			if (mBitmap == null)
				return;
			mIntrinsicWidth = mBitmap.getWidth();
			mIntrinsicHeight = mBitmap.getHeight();
		}

		@Override
		public void draw(Canvas canvas) {
			if (mBitmap == null) return;
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			int x = (width - mIntrinsicWidth) / 2;
			int y = (height - mIntrinsicHeight) / 2;
			canvas.drawBitmap(mBitmap, x, y, null);
		}

		@Override
		public int getOpacity() {
			return android.graphics.PixelFormat.OPAQUE;
		}

		@Override
		public void setAlpha(int alpha) {

		}

		@Override
		public void setColorFilter(ColorFilter cf) {

		}
	}
}