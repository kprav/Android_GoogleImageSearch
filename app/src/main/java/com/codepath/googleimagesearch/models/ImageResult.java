package com.codepath.googleimagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageResult implements Parcelable {
    private String fullUrl;
    private String thumbUrl;
    private String title;
    private int width;
    private int height;

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // Constructor
    public ImageResult(JSONObject json) {
        try {
            this.fullUrl = json.getString("unescapedUrl");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
            this.width = json.getInt("width");
            this.height = json.getInt("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get an array list of images from the JSON Array
    public static ArrayList<ImageResult> fromJSONArray(JSONArray... jsonArray) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (JSONArray array : jsonArray) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    results.add(new ImageResult(array.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullUrl);
        dest.writeString(this.thumbUrl);
        dest.writeString(this.title);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected ImageResult(Parcel in) {
        this.fullUrl = in.readString();
        this.thumbUrl = in.readString();
        this.title = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<ImageResult> CREATOR = new Creator<ImageResult>() {
        public ImageResult createFromParcel(Parcel source) {
            return new ImageResult(source);
        }

        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };
}
