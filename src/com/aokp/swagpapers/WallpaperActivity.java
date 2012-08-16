
package com.aokp.swagpapers;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WallpaperActivity extends Activity {

    public final String TAG = "SwagPapers";
    protected static final String MANIFEST = "wallpaper_manifest.xml";
    protected static final int THUMBS_TO_SHOW = 4;

    /*
     * pull the manifest from the web server specified in config.xml or pull
     * wallpaper_manifest.xml from local assets/ folder for testing
     */
    public static final boolean USE_LOCAL_MANIFEST = false;

    ArrayList<WallpaperCategory> categories = null;
    ProgressDialog mLoadingDialog;
    WallpaperPreviewFragment mPreviewFragment;
    NavigationBarCategoryAdapater mCategoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setMessage("Retreiving wallpapers from server...");

        mLoadingDialog.show();
        new LoadWallpaperManifest().execute();
    }

    protected void loadPreviewFragment() {
        mPreviewFragment = new WallpaperPreviewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(android.R.id.content, mPreviewFragment);
        ft.commit();

        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ab.setListNavigationCallbacks(mCategoryAdapter = new NavigationBarCategoryAdapater(
                getApplicationContext(),
                categories),
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        setCategory(itemPosition);
                        return true;
                    }
                });
        ab.setDisplayShowTitleEnabled(false);
    }

    protected void setCategory(int cat) {
        mPreviewFragment.setCategory(cat);
    }

    public static class WallpaperPreviewFragment extends Fragment {

        static final String TAG = "PreviewFragment";
        WallpaperActivity mActivity;
        View mView;

        public int currentPage = -1;
        Button back;
        Button next;
        TextView pageNum;
        ThumbnailView[] thumbs;
        public int selectedCategory = 0; // *should* be <ALL> wallpapers

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mActivity = (WallpaperActivity) getActivity();
            next(); // load initial page
        }

        public void setCategory(int cat) {
            selectedCategory = cat;
            currentPage = -1;
            next();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            mView = inflater.inflate(R.layout.activity_wallpaper, container, false);

            back = (Button) mView.findViewById(R.id.backButton);
            next = (Button) mView.findViewById(R.id.nextButton);
            pageNum = (TextView) mView.findViewById(R.id.textView1);

            thumbs = new ThumbnailView[THUMBS_TO_SHOW];
            thumbs[0] = (ThumbnailView) mView.findViewById(R.id.imageView1);
            thumbs[1] = (ThumbnailView) mView.findViewById(R.id.imageView2);
            thumbs[2] = (ThumbnailView) mView.findViewById(R.id.imageView3);
            thumbs[3] = (ThumbnailView) mView.findViewById(R.id.imageView4);

            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    next();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    previous();
                }
            });

            return mView;
        }

        public ArrayList<WallpaperCategory> getCategories() {
            return mActivity.mCategoryAdapter.getCategories();
        }

        protected Wallpaper getWallpaper(int realIndex) {
            return getCategories().get(selectedCategory).getWallpapers().get(realIndex);
        }

        protected void setThumbs() {
            for (ThumbnailView v : thumbs)
                v.setVisibility(View.INVISIBLE);

            final int numWallpapersInCategory = getCategories().get(selectedCategory)
                    .getWallpapers().size();
            boolean enableForward = true;

            for (int i = 0; i < thumbs.length; i++) {
                final int realIndex = (currentPage * thumbs.length + i);
                if (realIndex >= (numWallpapersInCategory - 1)) {
                    enableForward = false;
                    break;
                }

                Wallpaper w = getWallpaper(realIndex);
                thumbs[i].setOnClickListener(null);
                thumbs[i].getName().setText(w.getName());
                thumbs[i].getAuthor().setText(w.getAuthor());
                UrlImageViewHelper.setUrlDrawable(thumbs[i].getThumbnail(), w.getThumbUrl(),
                        R.drawable.ic_placeholder, new ThumbnailCallBack(w, realIndex));
            }

            back.setEnabled(currentPage != 0);
            next.setEnabled(enableForward);
        }

        public void next() {
            getNextButton().setEnabled(false);
            pageNum.setText(getResources().getString(R.string.page) + " " + (++currentPage + 1));

            setThumbs();
        }

        public void previous() {
            pageNum.setText(getResources().getString(R.string.page) + " " + (--currentPage + 1));

            setThumbs();
        }

        protected void skipToPage(int page) {
            if (page < currentPage) {
                while (page < currentPage) {
                    previous(); // should subtract page
                }
            } else if (page > currentPage) {
                while (page > currentPage) {
                    next();
                }
            }
        }

        public void jumpTo() {
            // View view = getLayoutInflater().inflate(R.layout.dialog_jumpto,
            // null);
            // final EditText e = (EditText) view.findViewById(R.id.pageNumber);
            // AlertDialog.Builder j = new AlertDialog.Builder(this);
            // j.setTitle(R.string.jump2);
            // j.setView(view);
            // j.setPositiveButton(android.R.string.ok, new
            // DialogInterface.OnClickListener() {
            //
            // public void onClick(DialogInterface dialog, int which) {
            // skipToPage(Integer.parseInt(e.getText().toString()));
            // }
            // });
            // j.setNegativeButton(android.R.string.no, new
            // DialogInterface.OnClickListener() {
            //
            // public void onClick(DialogInterface dialog, int which) {
            // dialog.cancel();
            // }
            // });
            // j.create().show();
        }

        protected View getThumbView(int i) {
            if (thumbs != null && thumbs.length > 0)
                return thumbs[i];
            else
                return null;
        }

        protected Button getNextButton() {
            return next;
        }

        protected Button getPreviousButton() {
            return back;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.jump:
                    jumpTo();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        class ThumbnailCallBack implements UrlImageViewCallback {

            Wallpaper wall;
            int index;

            public ThumbnailCallBack(Wallpaper wall, int index) {
                this.wall = wall;
                this.index = index;
            }

            @Override
            public void onLoaded(ImageView imageView, Drawable loadedDrawable, String url,
                    boolean loadedFromCache) {

                final int relativeIndex = index % 4;
                if (loadedDrawable != null) {
                    getThumbView(relativeIndex).setOnClickListener(
                            new ThumbnailClickListener(wall));
                    getThumbView(relativeIndex).setVisibility(View.VISIBLE);
                }
                if (loadedDrawable != null && relativeIndex == 3)
                    getNextButton().setEnabled(true);
            }
        }

        class ThumbnailClickListener implements View.OnClickListener {
            Wallpaper wall;

            public ThumbnailClickListener(Wallpaper wallpaper) {
                this.wall = wallpaper;
            }

            @Override
            public void onClick(View v) {
                Intent preview = new Intent(mActivity, Preview.class);
                preview.putExtra("wp", wall.getUrl());
                startActivity(preview);
            }
        }
    }

    public static String getDlDir(Context c) {
        String configFolder = getResourceString(c, R.string.config_wallpaper_download_loc);
        if (configFolder != null && !configFolder.isEmpty()) {
            return new File(Environment.getExternalStorageDirectory(), configFolder)
                    .getAbsolutePath() + "/";
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    public static String getSvDir(Context c) {
        String configFolder = getResourceString(c, R.string.config_wallpaper_sdcard_dl_location);
        if (configFolder != null && !configFolder.isEmpty()) {
            return new File(Environment.getExternalStorageDirectory(), configFolder)
                    .getAbsolutePath() + "/";
        } else {
            return null;
        }
    }

    protected String getWallpaperDestinationPath() {
        String configFolder = getResourceString(R.string.config_wallpaper_sdcard_dl_location);
        if (configFolder != null && !configFolder.isEmpty()) {
            return new File(Environment.getExternalStorageDirectory(), configFolder)
                    .getAbsolutePath();
        }
        // couldn't find resource?
        return null;
    }

    protected String getResourceString(int stringId) {
        return getApplicationContext().getResources().getString(stringId);
    }

    public static String getResourceString(Context c, int id) {
        return c.getResources().getString(id);
    }

    private class LoadWallpaperManifest extends
            AsyncTask<Void, Boolean, ArrayList<WallpaperCategory>> {

        @Override
        protected ArrayList<WallpaperCategory> doInBackground(Void... v) {

            try {
                InputStream input = null;

                if (USE_LOCAL_MANIFEST) {
                    input = getApplicationContext().getAssets().open(MANIFEST);
                } else {
                    URL url = new URL(getResourceString(R.string.config_wallpaper_manifest_url));
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    // this will be useful so that you can show a typical
                    // 0-100%
                    // progress bar
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = new BufferedInputStream(url.openStream());
                }
                OutputStream output = getApplicationContext().openFileOutput(
                        MANIFEST, MODE_PRIVATE);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                // file finished downloading, parse it!
                ManifestXmlParser parser = new ManifestXmlParser();
                return parser.parse(new File(getApplicationContext().getFilesDir(), MANIFEST),
                        getApplicationContext());
            } catch (Exception e) {
                Log.d(TAG, "Exception!", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<WallpaperCategory> result) {
            categories = result;
            if (categories != null)
                loadPreviewFragment();

            mLoadingDialog.cancel();
            super.onPostExecute(result);
        }
    }

}
