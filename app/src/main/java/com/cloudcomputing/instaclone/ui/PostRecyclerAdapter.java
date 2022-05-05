package com.cloudcomputing.instaclone.ui;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudcomputing.instaclone.R;
import com.cloudcomputing.instaclone.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter for recycler view
public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;

    public PostRecyclerAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    //Inflates view holder
    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.image_row, parent, false);
        return new ViewHolder(view, context);
    }

    //Sets image in recycler view
    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ViewHolder holder, int position) {
        Post post = postList.get(position);
        String imageUrl = post.getImageUrl();
        //Loads image
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_placeholder).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //View holder to represent a row
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context viewHolderContext) {
            super(itemView);
            context = viewHolderContext;
            imageView = itemView.findViewById(R.id.postedImageView);
        }
    }
}
