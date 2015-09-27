package com.codepath.googleimagesearch.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends AppCompatActivity {

    private ImageView ivImage;
    private MenuItem progressBar;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        // Get Parcelable data from the intent
        ImageResult result = getIntent().getParcelableExtra("result");
        // Extract the URL from the Parcelable
        url = result.getFullUrl();
        // Find the image view
        ivImage = (ImageView) findViewById(R.id.ivImageFullScreen);
    }

    private void loadImage() {
        showProgressBar();
        // Load the image into the image view using Picasso
        Picasso.with(this).load(url).fit().into(ivImage, new Callback() {
            @Override
            public void onSuccess() {
                hideProgressBar();
                // Remove action bar to get a true full screen view
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            }

            @Override
            public void onError() {
                hideProgressBar();
                AlertDialog alertDialog = new AlertDialog.Builder(ImageDisplayActivity.this).create();
                alertDialog.setTitle("Error!");
                alertDialog.setMessage("Couldn't load full screen photo.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        // Store instance of the menu item containing progress
        progressBar = menu.findItem(R.id.action_progress_display);
        loadImage();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        progressBar = menu.findItem(R.id.action_progress_display);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(progressBar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar() {
        // Show progress item
        progressBar.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        progressBar.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        progressBar.setVisible(false);
    }
}
