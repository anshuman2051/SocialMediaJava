package com.example.socialmediajava.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.socialmediajava.Models.BlockModel;
import com.example.socialmediajava.R;
import com.example.socialmediajava.viewHolder.RecyclerViewHolder;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class BlockAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<BlockModel> blocks;
    private Context mcontext;
    private int lastPosition = -1;

    public BlockAdapter(@Nullable List<BlockModel> blocks, @NonNull Context mcontext) {
        this.blocks = blocks;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from( parent.getContext()).inflate(getItemViewType(), parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Log.d("holder", ""+holder + " " + position);
        holder.txtIndex.setText( String.format(
                mcontext.getString(R.string.title_block_number), blocks.get(position).getIndex()
        ));

        holder.txtPreviousHash.setText(blocks.get(position).getPreviousHash() != null ?
                blocks.get(position).getPreviousHash() : "null");
        holder.txtTimeStamp.setText( String.valueOf( new Date(blocks.get(position).getTimestamp())));
        holder.txtData.setText(blocks.get(position).getData());
        holder.txtHash.setText(blocks.get(position).getHash());
        setAnimation( holder.itemView, position);
    }

    private void setAnimation(View itemView, int position) {
        if( position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation( mcontext, android.R.anim.fade_in);
            itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    public int getItemViewType( ){
        return R.layout.item_block_data;
    }

    @Override
    public int getItemCount() {
        return blocks == null ? 0 : blocks.size();
    }
}
