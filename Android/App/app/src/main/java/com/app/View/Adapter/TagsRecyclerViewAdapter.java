package com.app.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;
import com.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TagsRecyclerViewAdapter  extends RecyclerView.Adapter<TagsRecyclerViewAdapter.ViewHolder> {

    private List<Tag> data;
    private LayoutInflater mInflater;
    private TagsRecyclerViewAdapter.ItemClickListener mClickListener;
    public static boolean longClick = false;

    // data is passed into the constructor
    public TagsRecyclerViewAdapter(Context context, List<Tag> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public TagsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tag, parent, false);
        return new TagsRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull TagsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.myTextView.setText(data.get(position).getName());
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tag);
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
    public Tag getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(TagsRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
