package com.codepath.googleimagesearch.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

    // View lookup cache for the view holder pattern
    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }

    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
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
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set the data to the viewHolder
        viewHolder.ivImage.setImageResource(0);
        Picasso.with(getContext()).load(imageInfo.getThumbUrl()).into(viewHolder.ivImage);
        viewHolder.tvTitle.setText(Html.fromHtml(imageInfo.getTitle()));

        // Return the completed view to be displayed
        return convertView;
    }
}
