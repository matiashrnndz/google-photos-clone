package com.app.View.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.LocalDataSource.Model.Album;
import com.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder> {

    private List<Album> data;
    private LayoutInflater mInflater;
    private AlbumsRecyclerViewAdapter.ItemClickListener mClickListener;
    public static boolean longClick = false;

    // data is passed into the constructor
    public AlbumsRecyclerViewAdapter(Context context, List<Album> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public AlbumsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.album, parent, false);
        return new AlbumsRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull AlbumsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getName());
        Picasso.get().load("http://fe.uccuyosj.edu.ar/images/gallery-512.png").into(holder.myImageView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView title;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.albumTitle);
            myImageView = itemView.findViewById(R.id.album);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                longClick = false;
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                longClick = true;
                mClickListener.onItemClick(v, getAdapterPosition());
            }
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // convenience method for getting data at click position
    public Album getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(AlbumsRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
