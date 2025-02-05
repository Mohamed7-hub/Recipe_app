package com.example.shoping;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private String currentUserId;

    public RecipeAdapter(List<Recipe> recipes, String currentUserId) {
        this.recipes = recipes;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.titleTextView.setText(recipe.getTitle());
        holder.authorTextView.setText("By " + recipe.getUserName());
        holder.cookingTime.setText(recipe.getCookingTime());

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(holder.recipeImage.getContext())
                    .load(recipe.getImageUrl())
                    .centerCrop()
                    .into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(R.drawable.creamy_pasta);
        }

        holder.likeButton.setImageResource(R.drawable.ic_heart_filled);
        holder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe);
            v.getContext().startActivity(intent);
        });

        holder.likeButton.setOnClickListener(v -> {
            recipe.setLiked(!recipe.isLiked());
            notifyItemChanged(position);
        });

        holder.bookmarkButton.setOnClickListener(v -> {
            recipe.setBookmarked(!recipe.isBookmarked());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView titleTextView;
        TextView authorTextView;
        TextView cookingTime;
        ImageButton likeButton;
        ImageButton bookmarkButton;
        ImageButton commentButton;
        ImageButton shareButton;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            cookingTime = itemView.findViewById(R.id.cookingTime);
            likeButton = itemView.findViewById(R.id.likeButton);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}