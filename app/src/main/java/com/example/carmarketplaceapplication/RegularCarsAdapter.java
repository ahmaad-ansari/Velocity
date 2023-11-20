package com.example.carmarketplaceapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegularCarsAdapter extends RecyclerView.Adapter<RegularCarsAdapter.ViewHolder> {

    private final List<CarListingModel> carListings;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CarListingModel item);
    }

    public RegularCarsAdapter(List<CarListingModel> carListings, OnItemClickListener listener) {
        this.carListings = carListings;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.regular_car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarListingModel item = carListings.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return carListings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView modelTextView, priceTextView, odometerTextView;
        private final ImageView carImageView;

        ViewHolder(View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.regularListingImage);
            modelTextView = itemView.findViewById(R.id.regularCarMakeModelYear);
            odometerTextView = itemView.findViewById(R.id.regularCarOdometer);
            priceTextView = itemView.findViewById(R.id.regularCarPrice);
        }

        void bind(final CarListingModel item, final OnItemClickListener listener) {
            modelTextView.setText(item.getMake() + " " + item.getModel());
            odometerTextView.setText(String.format("%,.0f km", item.getOdometer()));
            priceTextView.setText(String.format("$%,.2f", item.getPrice()));

            // Again, use an image loading library for the image
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
