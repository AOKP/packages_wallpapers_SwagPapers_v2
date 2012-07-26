package com.fnv.wallpapers;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Bundle;
import android.os.Environment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Wallpaper extends Activity implements OnGesturePerformedListener {
	
	private GestureLibrary gestureLib;
	
	String tag = "FNV Wallpapers";
	
	String link = "https://dl.dropbox.com/u/3270519/Apps/FNV/";
	String pre= "fnv_";
	String post = "_small";
	String ext = ".png";
	public int page = 1;
	int i1 = 1;
	int i2 = 2;
	int i3 = 3;
	int i4 = 4;
	
	String fileDest = null;
	String fileName = null;
	String dlDir = Environment.getExternalStorageDirectory() + "/";
	String svDir = Environment.getExternalStorageDirectory() + "/FNV/";
	
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
				next();
			}
		});
        
        //set the images to look at the previous 4
        Log.i(tag, "BackButton onClickListener()");
        back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				previous();
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
		    	Intent preview = new Intent(Wallpaper.this, Preview.class);
		    	preview.putExtra("wp", url);
		    	startActivity(preview);
				Log.i(tag, url);
			}
		});
        
        v2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = link + pre + i2 + ext;
		    	Intent preview = new Intent(Wallpaper.this, Preview.class);
		    	preview.putExtra("wp", url);
		    	startActivity(preview);
				Log.i(tag, url);
			}
		});

        v3.setOnClickListener(new View.OnClickListener() {
	
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		String url = link + pre + i3 + ext;
		    	Intent preview = new Intent(Wallpaper.this, Preview.class);
		    	preview.putExtra("wp", url);
		    	startActivity(preview);
        		Log.i(tag, url);
        	}
        });

        v4.setOnClickListener(new View.OnClickListener() {
	
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		String url = link + pre + i4 + ext;
		    	Intent preview = new Intent(Wallpaper.this, Preview.class);
		    	preview.putExtra("wp", url);
		    	startActivity(preview);
        		Log.i(tag, url);
        	}
        });
    }
    
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
        final Button back = (Button) findViewById(R.id.backButton);
    	final TextView pageNum = (TextView) findViewById(R.id.textView1);
        final ImageView v1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView v2 = (ImageView) findViewById(R.id.imageView2);
        final ImageView v3 = (ImageView) findViewById(R.id.imageView3);
        final ImageView v4 = (ImageView) findViewById(R.id.imageView4);
        
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
    
    public void previous() {
    	final Button back = (Button) findViewById(R.id.backButton);
    	final TextView pageNum = (TextView) findViewById(R.id.textView1);
        final ImageView v1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView v2 = (ImageView) findViewById(R.id.imageView2);
        final ImageView v3 = (ImageView) findViewById(R.id.imageView3);
        final ImageView v4 = (ImageView) findViewById(R.id.imageView4);
        
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
            case R.id.vote:
            	Intent v = new Intent(Wallpaper.this, Vote.class);
            	startActivity(v);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    //I'll do this later
    public void jumpTo() {
    	View view = getLayoutInflater().inflate(R.layout.dialog_jumpto, null);
    	final EditText e = (EditText) view.findViewById(R.id.pageNumber);
    	AlertDialog.Builder j = new AlertDialog.Builder(this);
    	j.setTitle(R.string.jump2);
    	j.setView(view);
    	j.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				skipPage(Integer.parseInt(e.getText().toString()));
			}
		});
    	j.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
    	j.create().show();
    	
    }
    
    public void skipPage(int p){
    	Log.i(tag, ""+p);
    	final Button back = (Button) findViewById(R.id.backButton);
        final TextView pageNum = (TextView) findViewById(R.id.textView1);
        Log.i(tag, "Buttons and TextView loaded");
        
        final ImageView v1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView v2 = (ImageView) findViewById(R.id.imageView2);
        final ImageView v3 = (ImageView) findViewById(R.id.imageView3);
        final ImageView v4 = (ImageView) findViewById(R.id.imageView4);
        Log.i(tag, "ImageViews loaded");
    	if (p == 1) {
			back.setEnabled(false);
		} else {
			back.setEnabled(true);
		}
    	pageNum.setText(getResources().getString(R.string.page) + " " + p);
    	
    	page = p;
    	
    	i1 = -3;
    	i2 = -2;
    	i3 = -1;
    	i4 = 0;
    	
    	int count = 0;
    	do {
    		addFour();
    		count++;
    	} while (count < p);

    	
    	UrlImageViewHelper.setUrlDrawable(v1, link + pre + i1 + post + ext, R.drawable.ic_placeholder);
		Log.i(tag, link + pre + i1 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v2, link + pre + i2 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i2 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v3, link + pre + i3 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i3 + post + ext);
        UrlImageViewHelper.setUrlDrawable(v4, link + pre + i4 + post + ext, R.drawable.ic_placeholder);
        Log.i(tag, link + pre + i4 + post + ext);
    	
    }
}