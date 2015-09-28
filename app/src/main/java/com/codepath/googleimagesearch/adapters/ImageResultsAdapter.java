package com.codepath.googleimagesearch.adapters;

import android.content.Context;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.helpers.RoundedTransformation;
import com.codepath.googleimagesearch.models.ImageResult;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

    private Random mRandom;
    private ArrayList<Integer> mBackgroundColors;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    // View lookup cache for the view holder pattern
    private static class ViewHolder {
        DynamicHeightImageView ivImage;
        DynamicHeightTextView tvTitle;
    }

    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
        mRandom = new Random();
        mBackgroundColors = new ArrayList<Integer>();
        mBackgroundColors.add(android.R.color.holo_orange_dark);
        mBackgroundColors.add(android.R.color.holo_green_dark);
        mBackgroundColors.add(android.R.color.holo_blue_dark);
        mBackgroundColors.add(android.R.color.holo_red_dark);
        mBackgroundColors.add(android.R.color.holo_purple);
    }

    // Get the data item and display it using the layout XML
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data for this position
        ImageResult imageInfo = getItem(position);

        // Cache Object
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate a new view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);

            // Create a new viewHolder cache
            viewHolder = new ViewHolder();
            viewHolder.ivImage = (DynamicHeightImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (DynamicHeightTextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        int backgroundIndex = position >= mBackgroundColors.size() ?
                position % mBackgroundColors.size() : position;

        // Set the data to the viewHolder
        viewHolder.ivImage.setImageResource(0);
        viewHolder.ivImage.setHeightRatio(positionHeight);
        Picasso.with(getContext()).load(imageInfo.getThumbUrl()).transform(new RoundedTransformation(5, 0)).into(viewHolder.ivImage);
        // Picasso.with(getContext()).load(imageInfo.getThumbUrl()).into(viewHolder.ivImage);
        viewHolder.tvTitle.setText(Html.fromHtml(imageInfo.getTitle()));
        // viewHolder.tvTitle.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

        // Return the completed view to be displayed
        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }
}
