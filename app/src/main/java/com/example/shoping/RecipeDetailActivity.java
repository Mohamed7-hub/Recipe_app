package com.example.shoping;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView titleText, authorText, cookingTimeText, ingredientsText, instructionsText;
    private ImageView backButton, likeButton, bookmarkButton, shareButton;
    private Recipe recipe;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private boolean isLiked = false;
    private boolean isBookmarked = false;

    public static class LikeData {
        public String recipeId;
        public String userId;
        public long timestamp;

        public LikeData() {} // No-argument constructor for Firestore

        public LikeData(String recipeId, String userId, long timestamp) {
            this.recipeId = recipeId;
            this.userId = userId;
            this.timestamp = timestamp;
        }
    }

    public static class BookmarkData {
        public String recipeId;
        public String userId;
        public long timestamp;

        public BookmarkData() {} // No-argument constructor for Firestore

        public BookmarkData(String recipeId, String userId, long timestamp) {
            this.recipeId = recipeId;
            this.userId = userId;
            this.timestamp = timestamp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;

        initializeViews();

        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        if (recipe != null) {
            displayRecipe();
            checkUserInteractions();
        }

        setClickListeners();
    }

    private void initializeViews() {
        recipeImage = findViewById(R.id.recipeImage);
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        cookingTimeText = findViewById(R.id.cookingTimeText);
        ingredientsText = findViewById(R.id.ingredientsText);
        instructionsText = findViewById(R.id.instructionsText);
        backButton = findViewById(R.id.backButton);
        likeButton = findViewById(R.id.likeButton);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        shareButton = findViewById(R.id.shareButton);
    }

    private void displayRecipe() {
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .centerCrop()
                    .into(recipeImage);
        }

        titleText.setText(recipe.getTitle());
        authorText.setText("By " + recipe.getUserName());
        cookingTimeText.setText(recipe.getCookingTime());
        ingredientsText.setText("Ingredients:\n\n" + recipe.getIngredients());
        instructionsText.setText("Instructions:\n\n" + recipe.getInstructions());
    }

    private void checkUserInteractions() {
        if (currentUserId == null) return;

        String likeId = currentUserId + "_" + recipe.getId();
        String bookmarkId = currentUserId + "_" + recipe.getId();

        db.collection("likes").document(likeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    isLiked = documentSnapshot.exists();
                    updateLikeButton();
                });

        db.collection("bookmarks").document(bookmarkId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    isBookmarked = documentSnapshot.exists();
                    updateBookmarkButton();
                });
    }

    private void setClickListeners() {
        backButton.setOnClickListener(v -> finish());
        likeButton.setOnClickListener(v -> toggleLike());
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        shareButton.setOnClickListener(v -> shareRecipe());
    }

    private void updateLikeButton() {
        likeButton.setImageResource(isLiked ? R.drawable.ic_heart : R.drawable.ic_heart_filled);
    }

    private void updateBookmarkButton() {
        bookmarkButton.setImageResource(isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_filled);
    }

    private void toggleLike() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please sign in to like recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        String likeId = currentUserId + "_" + recipe.getId();
        boolean newLikeState = !isLiked;

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot recipeDoc = transaction.get(db.collection("recipes").document(recipe.getId()));
            Long currentLikes = recipeDoc.getLong("likes");
            if (currentLikes == null) {
                currentLikes = 0L;
            }

            if (newLikeState) {
                LikeData likeData = new LikeData(recipe.getId(), currentUserId, System.currentTimeMillis());
                transaction.set(db.collection("likes").document(likeId), likeData);
                transaction.update(db.collection("recipes").document(recipe.getId()), "likes", currentLikes + 1);
            } else {
                transaction.delete(db.collection("likes").document(likeId));
                transaction.update(db.collection("recipes").document(recipe.getId()), "likes", Math.max(0, currentLikes - 1));
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            isLiked = newLikeState;
            updateLikeButton();
            Toast.makeText(this, isLiked ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to update like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleBookmark() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please sign in to bookmark recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookmarkId = currentUserId + "_" + recipe.getId();
        if (isBookmarked) {
            db.collection("bookmarks").document(bookmarkId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        isBookmarked = !isBookmarked;
                        updateBookmarkButton();
                        Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this,
                            "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            BookmarkData bookmarkData = new BookmarkData(recipe.getId(), currentUserId, System.currentTimeMillis());
            db.collection("bookmarks").document(bookmarkId)
                    .set(bookmarkData)
                    .addOnSuccessListener(aVoid -> {
                        isBookmarked = !isBookmarked;
                        updateBookmarkButton();
                        Toast.makeText(this, "Added to bookmarks", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this,
                            "Failed to bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void shareRecipe() {
        // Implement share functionality
        Toast.makeText(this, "Share functionality coming soon!", Toast.LENGTH_SHORT).show();
    }
}

