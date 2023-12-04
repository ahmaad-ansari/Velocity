package com.example.carmarketplaceapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final List<Object> imageSources; // Stores a list of image sources (URIs or Strings)
    private final Context context; // Context for inflating views
    private final LayoutInflater inflater; // Inflater for inflating layout for items
    private final SharedViewModel viewModel; // ViewModel for communication between fragments

    public ImageAdapter(Context context, List<Object> imageSources, SharedViewModel viewModel) {
        this.context = context;
        this.imageSources = imageSources;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for individual image items
        View view = inflater.inflate(R.layout.grid_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object imageSource = imageSources.get(position);
        // Handling different types of image sources: Uri or String
        if (imageSource instanceof Uri) {
            Uri imageUri = (Uri) imageSource;
            // Load image from Uri using Glide library
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        } else if (imageSource instanceof String) {
            String imageUrl = (String) imageSource;
            if (!TextUtils.isEmpty(imageUrl) && !imageUrl.equals("placeholder")) {
                // Load image from URL using Glide library if not a placeholder
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.imageView);
            } else {
                // Set a placeholder image if the URL is empty or represents a placeholder
                holder.imageView.setImageResource(R.drawable.placeholder_image);
            }
        }
        // Handle click on an image to prompt for removal
        holder.imageView.setOnClickListener(v -> showRemoveDialog(position, holder));
    }

    @Override
    public int getItemCount() {
        return imageSources.size();
    }

    // Update the image paths in the adapter
    public void updateImagePaths(List<String> newImagePaths) {
        this.imageSources.clear();
        this.imageSources.addAll(newImagePaths);
        notifyDataSetChanged();
    }

    // Display a dialog to confirm image removal
    private void showRemoveDialog(int position, ViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Image")
                .setMessage("Do you want to remove this image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    imageSources.remove(position);
                    notifyItemRemoved(position);

                    // Add a placeholder if the list size is less than MAX_IMAGES
                    if (imageSources.size() < 10) {
                        imageSources.add(""); // Assuming "" is a placeholder
                        notifyItemInserted(imageSources.size() - 1);
                    }

                    if (viewModel != null) {
                        viewModel.setImageSources(new ArrayList<>(imageSources)); // Update ViewModel
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ViewHolder for the adapter to hold ImageView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // ImageView to display images

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}