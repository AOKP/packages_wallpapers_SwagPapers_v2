
package com.aokp.swagpapers;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Preview extends Activity {

    final static String TAG = "Preview";

    String link = "";
    ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    String fileDest = null;
    String fileName = null;
    Drawable loadedWallpaper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        ImageView p = (ImageView) findViewById(R.id.preView);
        Button d = (Button) findViewById(R.id.set_button);
        Button s = (Button) findViewById(R.id.save_button);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progress_bar);
        progress.setVisibility(View.VISIBLE);
        progress.setIndeterminate(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            link = extras.getString("wp");
            Log.d("PREVIEW", "url: " + link);
            UrlImageViewHelper.setUrlDrawable(p, link, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Drawable loadedDrawable, String url,
                        boolean loadedFromCache) {
                    progress.setIndeterminate(false);
                    progress.setVisibility(View.GONE);
                    loadedWallpaper = loadedDrawable;
                }
            });
        } else {
            Intent home = new Intent(Preview.this, WallpaperActivity.class);
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            startActivity(home);
            finish();
        }

        File f = new File(WallpaperActivity.getSvDir(getApplicationContext()));
        Log.i(TAG, "Check for external SD: " + f.getAbsolutePath());
        if (f.isDirectory() && f.exists()) {
        } else {
            f.mkdirs();
        }

        d.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DownloadFileAsync().execute(link);
            }
        });
        s.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new StoreFileAsync().execute(link);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // FEATURE_NO_TITLE crashes because setContentView() has already been
        // called
        // I'll look into fixes later
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_preview_land);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ImageView v = (ImageView) findViewById(R.id.imageView1);
            UrlImageViewHelper.setUrlDrawable(v, link);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent restart = new Intent(Preview.this, Preview.class);
            restart.putExtra("wp", link);
            startActivity(restart);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getResources().getString(R.string.setting_wallpaper));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            WallpaperManager wallpaperManager = WallpaperManager
                    .getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(drawableToBitmap(loadedWallpaper));
            } catch (IOException e) {
                e.printStackTrace();
            }
            File file = new File(getDlDir() + fileName);
            if (file.exists() == true) {
                file.delete();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.cancel();
            Toast.makeText(getApplicationContext(), R.string.set, Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

    }

    class StoreFileAsync extends AsyncTask<String, String, String> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                fileName = formatter.format(now)
                        + aurl[0].substring(aurl[0].lastIndexOf("."), aurl[0].length());
                // TODO better way of finding the extention

                int lengthOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(getSvDir() + fileName);
                fileDest = getSvDir() + fileName;

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.saved) + " " + getSvDir() + fileName,
                    Toast.LENGTH_LONG).show();
        }
    }

    String getDlDir() {
        return WallpaperActivity.getDlDir(getApplicationContext());
    }

    String getSvDir() {
        return WallpaperActivity.getSvDir(getApplicationContext());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
