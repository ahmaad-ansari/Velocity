package com.example.carmarketplaceapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RegularCarsAdapter extends RecyclerView.Adapter<RegularCarsAdapter.ViewHolder> {

    private List<CarListModel> carListings;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CarListModel item);
    }

    public RegularCarsAdapter(OnItemClickListener listener) {
        this.carListings = new ArrayList<>();
        this.listener = listener;
    }

    public void setCarListings(List<CarListModel> newCarListings) {
        this.carListings = newCarListings;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.regular_car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarListModel item = carListings.get(position);
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

        void bind(final CarListModel item, final OnItemClickListener listener) {
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
