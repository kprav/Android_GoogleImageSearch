package com.codepath.googleimagesearch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.adapters.ImageResultsAdapter;
import com.codepath.googleimagesearch.fragments.SettingsFragment;
import com.codepath.googleimagesearch.helpers.EndlessScrollListener;
import com.codepath.googleimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter adapterImageResults;
    private int offset = 0;

    private static String searchQuery = "Any";
    private static String imageSize = "Any";
    private static String colorFilter = "Any";
    private static String imageType = "Any";
    private static String siteFilter = "Any";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.app_icon);
        }
        // Setup the search query edit text and the search button
        setupViews();
        // Create the data source
        imageResults = new ArrayList<>();
        // Link the data source to the adapter
        adapterImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to the gridview
        gvResults.setAdapter(adapterImageResults);
        // Perform an initial search to fill up the grid
        performImageSearch(true);
    }

    // Setup all views and listeners in current activity
    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        // Define a listener for items in the grid (when clicked)
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create an intent and call the activity to display image in full screen
                Intent intent = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                intent.putExtra("result", result);
                if (isNetworkAvailable())
                    startActivity(intent);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            // Triggered only when new data needs to be appended to the list
            // Load more data for paginating and append the new data items to the adapter.
            // Use the page/totalItemsCount value to retrieve paginated data.
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount <= 56) {
                    // Google allows only 64 results per image search.
                    // We query 8 results per page. Hence only 8 pages max.
                    // Images 0-7 => Page 1
                    // Images 8-15 => Page 2
                    // ...
                    // Images 56 - 63 => Page 8
                    offset = totalItemsCount;
                    performImageSearch(false);

                    // True ONLY if more data is actually being loaded; false otherwise.
                    return true;
                }
                return false;
            }
        });
    }

    private String constructUrl() {
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&start=" + offset;
        if (!searchQuery.equalsIgnoreCase("Any"))
            url = url + "&q=" + searchQuery;
        else
            url = url + "&q=random";
        if (!imageSize.equalsIgnoreCase("Any"))
            url = url + "&imgsz=" + imageSize;
        if (!colorFilter.equalsIgnoreCase("Any"))
            url = url + "&imgcolor=" + colorFilter;
        if (!imageType.equalsIgnoreCase("Any"))
            url = url + "&imgtype=" + imageType;
        if (!siteFilter.equalsIgnoreCase("Any"))
            url = url + "&as_sitesearch=" + siteFilter;
        Log.i("URL", url);
        return url;
    }

    private void performImageSearch(boolean reset) {
        // Perform search only if the network is available
        if (!isNetworkAvailable()) {
            return;
        }
        String searchUrl = constructUrl();
        final boolean clear = reset;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson;
                try {
                    if (response.optJSONObject("responseData") != null) {
                        if (response.getJSONObject("responseData").optJSONArray("results") != null) {
                            imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                            // Clear the existing results from the array
                            // Note that this is done only on a new search, should not be done when paginating
                            if (clear) {
                                // imageResults.clear();
                                adapterImageResults.clear();
                            }
                            // Add all the images to the array
                            // imageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                            // adapterImageResults.notifyDataSetChanged();
                            // Instead of the above two lines, the adapter can be directly changed.
                            // Making changes to the adapter will also modify the underlying data.
                            adapterImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // TODO: onFailure
        });
    }

    // Check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting())
            return true;
        AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this).create();
        alertDialog.setTitle("No Internet Access!");
        alertDialog.setMessage("Please check your connection and try again.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return false;
    }

    // Called when the search button is pressed
    // This is done by setting the android:onClick property in the XML
    public void onImageSearch(View view) {
        offset = 0;
        searchQuery = null;
        searchQuery = etQuery.getText().toString().trim();
        if (searchQuery.equalsIgnoreCase(""))
            searchQuery = "Any";
        // Construct URL and fire the network request
        performImageSearch(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();
            SettingsFragment settingsFragment = SettingsFragment.newInstance(imageSize, colorFilter, imageType, siteFilter);
            settingsFragment.show(fm, "fragment_settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishSettingsFragment(String imageSize, String colorFilter, String imageType, String siteFilter) {
        SearchActivity.imageSize = imageSize;
        SearchActivity.colorFilter = colorFilter;
        SearchActivity.imageType = imageType;
        SearchActivity.siteFilter = siteFilter;
        performImageSearch(true);
    }
}
