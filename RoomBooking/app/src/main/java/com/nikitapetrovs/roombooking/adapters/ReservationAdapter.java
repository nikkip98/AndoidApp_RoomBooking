package com.nikitapetrovs.roombooking.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.repository.models.Reservation;


public class ReservationAdapter extends ListAdapter<Reservation, ReservationAdapter.ReservationHolder> {

    private Context context;
    private OnButtonClickedListener onButtonClickedListener;

    public ReservationAdapter(Context context, OnButtonClickedListener onButtonClickedListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.onButtonClickedListener = onButtonClickedListener;
    }

    public static final DiffUtil.ItemCallback<Reservation> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reservation>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
//            return Comparator.compareBooking(oldItem, newItem);
            return false;
        }

    };

    @NonNull
    @Override
    public ReservationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        itemView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        return new ReservationHolder(itemView, onButtonClickedListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ReservationHolder holder, int position) {
        Reservation currentReservation = getItem(position);
        String time = "From: " + currentReservation.getTimeFrom() + " To: " + currentReservation.getTimeTo();
        holder.textViewReservation.setText(time);
    }

    public Reservation getReservationAt(int position) {
        return getItem(position);
    }


    class ReservationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewReservation;
        OnButtonClickedListener onButtonClickedListener;

        private ReservationHolder(@NonNull View itemView, OnButtonClickedListener onButtonClickedListener) {
            super(itemView);
            textViewReservation = itemView.findViewById(R.id.textReservation);
            this.onButtonClickedListener = onButtonClickedListener;

            ImageButton button = itemView.findViewById(R.id.imageButton);
            button.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onButtonClickedListener.onButtonClicked(getAdapterPosition());
        }
    }

    public interface OnButtonClickedListener {
        void onButtonClicked(int id);
    }

}

