package com.example.carmarketplaceapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Uri> imageUris;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, ArrayList<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view);
        imageView.setImageURI(imageUris.get(position));

        imageView.setOnClickListener(v -> {
            // Trigger the removal logic
            showRemoveDialog(position);
        });

        return convertView;
    }

    private void showRemoveDialog(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Image")
                .setMessage("Do you want to remove this image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the image from the list and notify the adapter
                    imageUris.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
