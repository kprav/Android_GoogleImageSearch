package com.codepath.googleimagesearch.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.helpers.DeviceDimensionsHelper;
import com.codepath.googleimagesearch.models.ImageResult;
import com.codepath.googleimagesearch.touchimageview.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDisplayActivity extends AppCompatActivity {

    private TouchImageView ivImageFullScreen;
    private MenuItem progressBar;
    private MenuItem shareMenuItem;
    private Intent shareIntent;
    private ShareActionProvider shareActionProvider;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.app_icon);
        }
        // Get Parcelable data from the intent
        ImageResult result = getIntent().getParcelableExtra("result");
        // Extract the URL from the Parcelable
        url = result.getFullUrl();
        Log.i("URL", url);
        // Find the image view
        ivImageFullScreen = (TouchImageView) findViewById(R.id.ivImageFullScreen);
    }

    // Load the full screen image using Picasso
    private void loadImage() {
        showProgressBar();
        int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(this) - 1;
        int deviceHeight = DeviceDimensionsHelper.getDisplayHeight(this) - 285;
        // Load the image into the image view using Picasso
        Picasso.with(this).load(url).resize(deviceWidth, deviceHeight).into(ivImageFullScreen, new Callback() {
            @Override
            public void onSuccess() {
                hideProgressBar();
                showShareItem();
                setupShareIntent();
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

    // Gets the image URI and setup the associated share intent to hook into the provider
    private void setupShareIntent() {
        // Fetch Bitmap Uri locally
        Uri bmpUri = getLocalBitmapUri(ivImageFullScreen);
        // Create share intent
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    private Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        // Store instance of the menu item containing progress
        progressBar = menu.findItem(R.id.action_progress_display);
        // Store instance of the menu item containing share
        shareMenuItem = menu.findItem(R.id.action_share);
        // Load the image into the image view
        loadImage();
        // Fetch reference to the share action provider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        // Attach share event to the menu item provider
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        progressBar = menu.findItem(R.id.action_progress_display);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(progressBar);
        // Locate MenuItem with ShareActionProvider
        shareMenuItem = menu.findItem(R.id.action_share);
        // Fetch reference to the share action provider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        // Attach share event to the menu item provider
        shareActionProvider.setShareIntent(shareIntent);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_share) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

        return super.onOptionsItemSelected(item);
    }

    private void showShareItem() {
        // Show share item
        shareMenuItem.setVisible(true);
    }

    private void showProgressBar() {
        // Show progress bar
        progressBar.setVisible(true);
    }

    private void hideProgressBar() {
        // Hide progress bar
        progressBar.setVisible(false);
    }
}
