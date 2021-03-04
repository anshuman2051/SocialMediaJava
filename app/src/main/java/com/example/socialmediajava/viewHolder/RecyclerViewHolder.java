package com.example.socialmediajava.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.socialmediajava.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView txtIndex, txtPreviousHash, txtTimeStamp, txtData, txtHash;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        txtIndex = itemView.findViewById( R.id.txt_index );
        txtPreviousHash = itemView.findViewById( R.id.txt_previous_hash );
        txtTimeStamp = itemView.findViewById( R.id.txt_timestamp );
        txtData = itemView.findViewById( R.id.btn_send_data );
        txtHash = itemView.findViewById( R.id.txt_hash);
    }
}

