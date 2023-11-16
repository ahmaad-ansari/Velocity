package com.example.carmarketplaceapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeaturedCarsAdapter extends RecyclerView.Adapter<FeaturedCarsAdapter.ViewHolder> {

    private final List<CarListing> carListings;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CarListing item);
    }

    public FeaturedCarsAdapter(List<CarListing> carListings, OnItemClickListener listener) {
        this.carListings = carListings;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarListing item = carListings.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return carListings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView modelTextView;
        private final TextView priceTextView;
        private final ImageView carImageView;

        ViewHolder(View itemView) {
            super(itemView);
            modelTextView = itemView.findViewById(R.id.featuredCarMakeModel);
            priceTextView = itemView.findViewById(R.id.featuredCarPrice);
            carImageView = itemView.findViewById(R.id.featuredCarImage);
        }

        void bind(final CarListing item, final OnItemClickListener listener) {
            modelTextView.setText(item.getMake() + " " + item.getModel());
            priceTextView.setText(String.format("$%,.2f", item.getPrice()));
            // Here, you should use an image loading library like Glide to load the image
            // Glide.with(itemView.getContext()).load(item.getImageUrl()).into(carImageView);

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
