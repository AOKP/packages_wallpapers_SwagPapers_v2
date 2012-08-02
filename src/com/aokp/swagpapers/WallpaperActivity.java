
package com.aokp.swagpapers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class WallpaperActivity extends Activity implements OnGesturePerformedListener {

    private GestureLibrary gestureLib;

    protected final String TAG = "SwagPapers";

    public int currentPage = -1;
    public int highestExistingIndex = 0;
    Button back;
    Button next;
    TextView pageNum;

    ThumbnailView[] thumbs;
    protected static final int THUMBS_TO_SHOW = 4;

    protected static final String MANIFEST = "wallpaper_manifest.xml";

    ArrayList<WallpaperCategory> categories = null;
    protected int selectedCategory = 0;

    ProgressDialog mLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_wallpaper, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureOverlayView.setGestureColor(Color.TRANSPARENT);
        gestureOverlayView.setUncertainGestureColor(Color.TRANSPARENT);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        setContentView(gestureOverlayView);

        back = (Button) findViewById(R.id.backButton);
        next = (Button) findViewById(R.id.nextButton);
        pageNum = (TextView) findViewById(R.id.textView1);

        thumbs = new ThumbnailView[THUMBS_TO_SHOW];
        thumbs[0] = (ThumbnailView) findViewById(R.id.imageView1);
        thumbs[1] = (ThumbnailView) findViewById(R.id.imageView2);
        thumbs[2] = (ThumbnailView) findViewById(R.id.imageView3);
        thumbs[3] = (ThumbnailView) findViewById(R.id.imageView4);

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

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setMessage("Retreiving wallpapers from server...");

        mLoadingDialog.show();
        new LoadWallpaperManifest().execute();

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

    // react to left and right swipes
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                String gName = prediction.name;
                Log.i(TAG, gName);
                if (gName.equals("gesture_left")) {
                    next();
                } else if (gName.equals("gesture_right")) {
                    previous();
                }
            }
        }
    }

    protected void setThumbs() {
        for (ThumbnailView v : thumbs)
            v.setVisibility(View.INVISIBLE);

        int lastRealIndex = -1;

        for (int i = 0; i < thumbs.length; i++) {
            final int realIndex = (currentPage * thumbs.length + i);

            if (realIndex >= categories.get(selectedCategory).getWallpapers().size()) {
                lastRealIndex = realIndex;
                break;
            }

            Wallpaper w = getWallpaper(realIndex);
            Log.d("WALLPAPER", "real index: " + realIndex);
            Log.d("WALLPAPER", "thumb url: " + w.getThumbUrl());
            thumbs[i].setOnClickListener(null);
            thumbs[i].getName().setText(w.getName());
            thumbs[i].getAuthor().setText(w.getAuthor());
            UrlImageViewHelper.setUrlDrawable(thumbs[i].getThumbnail(), w.getThumbUrl(),
                    R.drawable.ic_placeholder, new ThumbnailCallBack(w, realIndex));
        }

        back.setEnabled(currentPage != 0);
        next.setEnabled(lastRealIndex < categories.get(selectedCategory).getWallpapers().size());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_wallpaper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.jump:
                jumpTo();
                return true;
            case R.id.vote:
                Intent v = new Intent(WallpaperActivity.this, Vote.class);
                startActivity(v);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void jumpTo() {
        View view = getLayoutInflater().inflate(R.layout.dialog_jumpto, null);
        final EditText e = (EditText) view.findViewById(R.id.pageNumber);
        AlertDialog.Builder j = new AlertDialog.Builder(this);
        j.setTitle(R.string.jump2);
        j.setView(view);
        j.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                skipToPage(Integer.parseInt(e.getText().toString()));
            }
        });
        j.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        j.create().show();

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

    protected View getThumbView(int i) {
        if (thumbs != null && thumbs.length > 0)
            return thumbs[i];
        else
            return null;
    }

    protected String getResourceString(int stringId) {
        return getApplicationContext().getResources().getString(stringId);
    }

    public static String getResourceString(Context c, int id) {
        return c.getResources().getString(id);
    }

    protected Button getNextButton() {
        return next;
    }

    protected Button getPreviousButton() {
        return back;
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
            Intent preview = new Intent(WallpaperActivity.this, Preview.class);
            preview.putExtra("wp", wall.getUrl());
            startActivity(preview);
        }
    }

    private class LoadWallpaperManifest extends
            AsyncTask<Void, Boolean, ArrayList<WallpaperCategory>> {

        @Override
        protected ArrayList<WallpaperCategory> doInBackground(Void... v) {
            try {
                URL url = new URL(getResourceString(R.string.config_wallpaper_manifest_url));
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100%
                // progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
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
                return parser.parse(new File(getApplicationContext().getFilesDir(), MANIFEST));
            } catch (Exception e) {
                Log.d(TAG, "Exception!", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<WallpaperCategory> result) {
            categories = result;
            if (result != null)
                next();
            mLoadingDialog.cancel();
            super.onPostExecute(result);
        }
    }

    protected Wallpaper getWallpaper(int realIndex) {
        return categories.get(selectedCategory).getWallpapers().get(realIndex);
    }
}
