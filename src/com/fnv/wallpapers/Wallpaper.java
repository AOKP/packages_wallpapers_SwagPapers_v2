package com.fnv.wallpapers;

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

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Wallpaper extends Activity {
	
	String tag = "FNV Wallpapers";
	
	String link = "https://dl.dropbox.com/u/3270519/Apps/FNV/";
	String pre= "fnv_";
	String post = "_small";
	String ext = ".png";
	int page = 1;
	int i1 = 1;
	int i2 = 2;
	int i3 = 3;
	int i4 = 4;
	
	ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	String fileDest = null;
	String fileName = null;
	String dlDir = Environment.getExternalStorageDirectory() + "/";
	String svDir = Environment.getExternalStorageDirectory() + "/FNV/";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        Log.i(tag, "onCreate()");
        
        
        final Button back = (Button) findViewById(R.id.backButton);
        final Button next = (Button) findViewById(R.id.nextButton);
        final TextView pageNum = (TextView) findViewById(R.id.textView1);
        Log.i(tag, "Buttons and TextView loaded");
        
        final ImageView v1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView v2 = (ImageView) findViewById(R.id.imageView2);
        final ImageView v3 = (ImageView) findViewById(R.id.imageView3);
        final ImageView v4 = (ImageView) findViewById(R.id.imageView4);
        Log.i(tag, "ImageViews loaded");
        
        v1.setClickable(true);
        v2.setClickable(true);
        v3.setClickable(true);
        v4.setClickable(true);
        Log.i(tag, "ImageView.setClickable(true)");
        
        //Check if the FNV folder exists already
        Log.i(tag, "Check for FNV folder");
        File f = new File(Environment.getExternalStorageDirectory() + "/FNV/");
        if (f.isDirectory() && f.exists()) {
        	Log.i(tag, "FNV folder exists");
        } else {
        	Log.i(tag, "FNV folder does not exist. Creating...");
        	f.mkdirs();
        }

        //Set the page number text
        Log.i(tag, "Set page number text");
        pageNum.setText(getResources().getString(R.string.page) + " " + page);
        back.setEnabled(false);
        
        //Load the images to the four imageviews
        Log.i(tag, "Load images");
        UrlImageViewHelper.setUrlDrawable(v1, link + pre + i1 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i1 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v2, link + pre + i2 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i2 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v3, link + pre + i3 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i3 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v4, link + pre + i4 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i4 + post + ext);
        
        //Set the images to look at the next 4
        Log.i(tag, "NextButton setOnClickListener");
        next.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addFour();
				page = page + 1;
				pageNum.setText(getResources().getString(R.string.page) + " " + page);
				UrlImageViewHelper.setUrlDrawable(v1, link + pre + i1 + post + ext, R.drawable.ic_placeholder);
				Log.i(tag, link + pre + i1 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v2, link + pre + i2 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i2 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v3, link + pre + i3 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i3 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v4, link + pre + i4 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i4 + post + ext);
		        if (page != 1) {
		        	back.setEnabled(true);
		        }
			}
		});
        
        //set the images to look at the previous 4
        Log.i(tag, "BackButton onClickListener()");
        back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				subtractFour();
				page = page -1;
				pageNum.setText(getResources().getString(R.string.page) + " " + page);
				if (page == 1) {
					back.setEnabled(false);
				}
				UrlImageViewHelper.setUrlDrawable(v1, link + pre + i1 + post + ext, R.drawable.ic_placeholder);
				Log.i(tag, link + pre + i1 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v2, link + pre + i2 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i2 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v3, link + pre + i3 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i3 + post + ext);
		        UrlImageViewHelper.setUrlDrawable(v4, link + pre + i4 + post + ext, R.drawable.ic_placeholder);
		        Log.i(tag, link + pre + i4 + post + ext);
			}
		});
        
        //Each of these open the same dialog
        //Except with different links
        //Saves lines of code
        //So many lines
        v1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i1 + ext;
				setDialog(url);
				Log.i(tag, url);
			}
		});
        
        v2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i2 + ext;
				setDialog(url);
				Log.i(tag, url);
			}
		});

        v3.setOnClickListener(new View.OnClickListener() {
	
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		String url = link + pre + i3 + ext;
        		setDialog(url);
        		Log.i(tag, url);
        	}
        });

        v4.setOnClickListener(new View.OnClickListener() {
	
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		String url = link + pre + i4 + ext;
        		setDialog(url);
        		Log.i(tag, url);
        	}
        });
        
        //Same deal as with clicking, but this time do download them
        v1.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i1 + ext;
				saveDialog(url);
				Log.i(tag, url);			
				return true;
			}
		});
        v2.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i2 + ext;
				saveDialog(url);
				Log.i(tag, url);
				return true;
			}
		});
        v3.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i3 + ext;
				saveDialog(url);
				Log.i(tag, url);
				return true;
			}
		});
        v4.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i4 + ext;
				saveDialog(url);
				Log.i(tag, url);
				return true;
			}
		});
    }
    
    //Dialog for setting the wallpapers
    public void setDialog(final String url) {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setMessage(R.string.set_wallpaper)
    	.setCancelable(true)
    	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new DownloadFileAsync().execute(url);
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
    	b.create();
    	b.show();
    }
    
    //Dialog for saving the wallpaper
    public void saveDialog(final String url) {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setMessage(R.string.save_wallpaper)
    	.setCancelable(true)
    	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new StoreFileAsync().execute(url);
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		})
		.create()
		.show();
    }
    
    //Change the ints to look at the next four wallpapers
    public void addFour() {
    	i1 = i1 + 4;
    	i2 = i2 + 4;
    	i3 = i3 + 4;
    	i4 = i4 + 4;
    }
    
    //Change the ints to look at the previous four wallpapers
    public void subtractFour() {
    	i1 = i1 - 4;
    	i2 = i2 - 4;
    	i3 = i3 - 4;
    	i4 = i4 - 4;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    //I'll do this later
    public void jumpTo() {

    }
        
    //Dialog box for saving and setting
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getResources().getString(R.string.downloading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
        }
    }
    
    //Downloads the wallpaper in Async with a dialog
    //This one just sets the wallpaper
class DownloadFileAsync extends AsyncTask<String, String, String> {
    	
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
    			
        		mProgressDialog.setProgress(0);
    			
    			URL url = new URL (aurl[0]);
    			URLConnection conexion = url.openConnection();
    			conexion.connect();
    			
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    			Date now = new Date();
    			fileName = formatter.format(now) + ".jpg";

    			
    			int lengthOfFile = conexion.getContentLength();
    			Log.d("ANDRO_ASYNC", "Lenght of file: " + lengthOfFile);
    			
    			InputStream input = new BufferedInputStream(url.openStream());
    			OutputStream output = new FileOutputStream(dlDir + fileName);
    			fileDest  = dlDir + fileName;
    			
    			byte data[] = new byte[1024];
    			
    			long total = 0;
    			
    			while ((count = input.read(data)) != -1) {
    				total += count;
    				publishProgress(""+(int)((total*100)/lengthOfFile));
    				output.write(data, 0, count);
    			}
    			
    			output.flush();
    			output.close();
    			input.close();
    		} catch (Exception e) {}
			
    		return null;    		
    	}
    	
    	protected void onProgressUpdate(String... progress) {
   		 Log.d("ANDRO_ASYNC",progress[0]);
   		 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
   	}
    	
    	@SuppressWarnings("deprecation")
		@Override
    	protected void onPostExecute(String unused) {
    		dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
    		mProgressDialog.setProgress(0);
    		WallpaperManager wallpaperManager = WallpaperManager.getInstance(Wallpaper.this);
    		getResources().getDrawable(R.drawable.ic_launcher);
    		Bitmap wallpaper = BitmapFactory.decodeFile(fileDest);
    		try {
				wallpaperManager.setBitmap(wallpaper);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Toast.makeText(getApplicationContext(), R.string.set, Toast.LENGTH_LONG).show();
    		File file = new File(dlDir + fileName);
    		if (file.exists() == true) {
    			file.delete();
    			
    		}
    	}   	
	}

	//Downloads the wallpaper in Async with a dialog
	//This one saves the wallpaper
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
			
			URL url = new URL (aurl[0]);
			URLConnection conexion = url.openConnection();
			conexion.connect();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			Date now = new Date();
			fileName = formatter.format(now) + ext;

			
			int lengthOfFile = conexion.getContentLength();
			Log.d("ANDRO_ASYNC", "Lenght of file: " + lengthOfFile);
			
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(svDir + fileName);
			fileDest  = svDir + fileName;
			
			byte data[] = new byte[1024];
			
			long total = 0;
			
			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+(int)((total*100)/lengthOfFile));
				output.write(data, 0, count);
			}
			
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {}
		
		return null;    		
	}
	
	protected void onProgressUpdate(String... progress) {
		 Log.d("ANDRO_ASYNC",progress[0]);
		 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(String unused) {
		dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved) + " " + svDir + fileName, Toast.LENGTH_LONG).show();
	}   	
}
}