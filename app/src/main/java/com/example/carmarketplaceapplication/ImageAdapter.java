package com.example.carmarketplaceapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Uri> imageUris;
    private LayoutInflater inflater;
    private SharedViewModel viewModel; // Add ViewModel reference


    public ImageAdapter(Context context, ArrayList<Uri> imageUris, SharedViewModel viewModel) {
        this.context = context;
        this.imageUris = imageUris;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel; // Initialize ViewModel
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Log.d("ImageAdapter", "Loading URI: " + imageUri);


        if (!imageUri.equals(Uri.EMPTY)) {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_image) // Placeholder image
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ImageAdapter", "Load failed for URI: " + imageUri, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.imageView);
        } else {
            // Handle placeholder URI
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.imageView.setOnClickListener(v -> showRemoveDialog(position, holder));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    private void showRemoveDialog(int position, ViewHolder holder) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Image")
                .setMessage("Do you want to remove this image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    imageUris.remove(position);
                    notifyDataSetChanged();
                    if (viewModel != null) {
                        viewModel.setImageUris(new ArrayList<>(imageUris)); // Update ViewModel
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
