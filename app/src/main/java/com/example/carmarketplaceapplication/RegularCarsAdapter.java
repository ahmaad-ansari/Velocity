package com.example.carmarketplaceapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

// Adapter class for the RecyclerView displaying regular car listings
public class RegularCarsAdapter extends RecyclerView.Adapter<RegularCarsAdapter.ViewHolder> {
    private List<CarListModel> carListings; // List of car listings
    private final OnItemClickListener listener; // Item click listener interface

    // Interface to handle item click events
    public interface OnItemClickListener {
        void onItemClick(CarListModel item);
    }

    // Constructor for the adapter
    public RegularCarsAdapter(OnItemClickListener listener) {
        this.carListings = new ArrayList<>();
        this.listener = listener;
    }

    // Set method to update the car listings data
    public void setCarListings(List<CarListModel> newCarListings) {
        this.carListings = newCarListings;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    // Create ViewHolder instances
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.regular_car_item, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to the ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarListModel item = carListings.get(position);
        holder.bind(item, listener);
    }

    // Get the total number of items in the data set
    @Override
    public int getItemCount() {
        return carListings.size();
    }

    // ViewHolder class for the RecyclerView items
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView modelTextView, priceTextView, odometerTextView;
        private final ImageView carImageView;

        // Constructor to initialize views inside each item
        ViewHolder(View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.regularListingImage);
            modelTextView = itemView.findViewById(R.id.regularCarMakeModelYear);
            odometerTextView = itemView.findViewById(R.id.regularCarOdometer);
            priceTextView = itemView.findViewById(R.id.regularCarPrice);
        }

        // Bind data to the views inside each item
        void bind(final CarListModel item, final OnItemClickListener listener) {
            modelTextView.setText(item.getMake() + " " + item.getModel());
            odometerTextView.setText(String.format("%,.0f km", item.getOdometer()));
            priceTextView.setText(String.format("$%,.0f", item.getPrice()));

            List<String> imageUrls = item.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image URL using Glide
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0)) // Load the first image URL
                        .placeholder(R.drawable.placeholder_image) // Placeholder image resource
                        .error(R.drawable.error_image) // Error image resource if Glide fails to load
                        .into(carImageView);
            } else {
                // If there are no image URLs, display a placeholder
                carImageView.setImageResource(R.drawable.placeholder_image);
            }

            // Handle item click event
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(item);
                    }
                }
            });
        }
    }
}
