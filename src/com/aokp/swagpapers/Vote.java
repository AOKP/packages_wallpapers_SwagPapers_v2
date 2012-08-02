package com.aokp.swagpapers;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class Vote extends WallpaperActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);
		
		ActionBar bar = getActionBar();
		bar.setTitle(R.string.wars);
		bar.setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences s = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor e = s.edit();
		Boolean run = s.getBoolean("firstRun", false);
		if (run != true) {
			aboutDialog();
			e.putBoolean("firstRun", true);
			e.commit();
		} 
	}
	
	public void aboutDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.wars);
		b.setMessage("Choose the next wallpaper to be added to FNV Wallpapers.\n\n" +
				"Every week, two wallpapers will be selected. All you have to do is choose which one you want to see in FNV Wallpapers!\n\n" +
				"After four new wallpapers have been choosen, they will appear in the app.");
		b.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		b.create();
		b.show();
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_vote, menu);
        return true;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
    			finish();
                return true;
            case R.id.about:
            	aboutDialog();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
