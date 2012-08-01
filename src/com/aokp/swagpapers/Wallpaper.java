
package com.aokp.swagpapers;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.aokp.swagpapers.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.File;
import java.util.ArrayList;

public class Wallpaper extends Activity implements OnGesturePerformedListener {

    private GestureLibrary gestureLib;

    String tag = "SwagPapers";

    String link;
    String pre;
    String post;
    String ext;
    public int currentPage = 0;
    public int highestExistingIndex = 0;
    Button back;
    Button next;
    TextView pageNum;
    ImageView v1;
    ImageView v2;
    ImageView v3;
    ImageView v4;

    String fileDest = null;
    String fileName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        link = getResourceString(R.string.config_wallpaper_url_prefix);
        pre = getResourceString(R.string.config_wallpaper_name_prefix);
        post = getResourceString(R.string.config_wallpaper_thumbnail_suffix);
        ext = getResourceString(R.string.config_wallpaper_extention);

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
        Log.i(tag, "onCreate()");

        back = (Button) findViewById(R.id.backButton);
        next = (Button) findViewById(R.id.nextButton);
        pageNum = (TextView) findViewById(R.id.textView1);
        v1 = (ImageView) findViewById(R.id.imageView1);
        v2 = (ImageView) findViewById(R.id.imageView2);
        v3 = (ImageView) findViewById(R.id.imageView3);
        v4 = (ImageView) findViewById(R.id.imageView4);

        next();

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

    }

    protected String getDlDir() {
        String configFolder = getResourceString(R.string.config_wallpaper_download_loc);
        if (configFolder != null && !configFolder.isEmpty()) {
            return new File(Environment.getExternalStorageDirectory(), configFolder)
                    .getAbsolutePath();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    protected String getSvDir() {
        String configFolder = getResourceString(R.string.config_wallpaper_sdcard_dl_location);
        if (configFolder != null && !configFolder.isEmpty()) {
            return new File(Environment.getExternalStorageDirectory(), configFolder)
                    .getAbsolutePath();
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
                Log.i(tag, gName);
                if (gName.equals("gesture_left")) {
                    next();
                } else if (gName.equals("gesture_right")) {
                    previous();
                }
            }
        }
    }

    public void next() {
        getNextButton().setEnabled(false);
        pageNum.setText(getResources().getString(R.string.page) + " " + ++currentPage);
        ImageView[] thumbs = new ImageView[] {
                v1, v2, v3, v4
        };

        for (int i = 0; i < thumbs.length; i++) {
            final int realIndex = ((currentPage - 1) * thumbs.length + i);
            final String url = link + pre + realIndex + post + ext;
            Log.d("WALLPAPER", "real index: " + realIndex);
            Log.d("WALLPAPER", "thumb url: " + url);
            thumbs[i].setOnClickListener(null);
            UrlImageViewHelper.setUrlDrawable(thumbs[i], url,
                    R.drawable.ic_placeholder, new ThumbnailCallBack(realIndex));
        }

        if ((currentPage - 1) == 0) {
            back.setEnabled(false);
        } else {
            back.setEnabled(true);
        }
    }

    public void previous() {
        pageNum.setText(getResources().getString(R.string.page) + " " + --currentPage);
        ImageView[] thumbs = new ImageView[] {
                v1, v2, v3, v4
        };

        for (int i = 0; i < thumbs.length; i++) {
            final int realIndex = (currentPage * thumbs.length + i);
            UrlImageViewHelper.setUrlDrawable(thumbs[i], link + pre + realIndex
                    + post + ext,
                    R.drawable.ic_placeholder);
        }

        if (currentPage == 0) {
            back.setEnabled(false);
            next.setEnabled(true);
        } else {
            back.setEnabled(true);
            next.setEnabled(false);
        }
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
                Intent v = new Intent(Wallpaper.this, Vote.class);
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
        switch (i) {
            default:
            case 0:
                return v1;
            case 1:
                return v2;
            case 2:
                return v3;
            case 3:
                return v4;
        }
    }

    protected String getResourceString(int stringId) {
        return getApplicationContext().getResources().getString(stringId);
    }

    protected Button getNextButton() {
        return next;
    }

    protected Button getPreviousButton() {
        return back;
    }

    class ThumbnailCallBack implements UrlImageViewCallback {

        int imageIndex;

        public ThumbnailCallBack(int imageIndex) {
            this.imageIndex = imageIndex;
        }

        @Override
        public void onLoaded(ImageView imageView, Drawable loadedDrawable, String url,
                boolean loadedFromCache) {

            final int relativeIndex = imageIndex % 4;
            if (loadedDrawable != null)
                getThumbView(relativeIndex).setOnClickListener(
                        new ThumbnailClickListener(imageIndex));
            if (loadedDrawable != null && relativeIndex == 3)
                getNextButton().setEnabled(true);
        }
    }

    class ThumbnailClickListener implements View.OnClickListener {
        int linkId;

        public ThumbnailClickListener(int linkId) {
            this.linkId = linkId;
        }

        @Override
        public void onClick(View v) {
            String url = link + pre + linkId + ext;
            Intent preview = new Intent(Wallpaper.this, Preview.class);
            preview.putExtra("wp", url);
            startActivity(preview);
        }
    }
}
