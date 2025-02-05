package com.example.shoping;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText titleEditText, ingredientsEditText, instructionsEditText, cookingTimeEditText;
    private Button submitButton;
    private ImageView backButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        cookingTimeEditText = findViewById(R.id.cookingTimeEditText);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setClickListeners() {
        submitButton.setOnClickListener(v -> saveRecipe());
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void saveRecipe() {
        String title = titleEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();
        String cookingTime = cookingTimeEditText.getText().toString().trim();

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty() || cookingTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to add a recipe", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipeId = UUID.randomUUID().toString();
        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName();
        if (userName == null || userName.isEmpty()) {
            userName = currentUser.getEmail();
        }

        Recipe recipe = new Recipe(
                recipeId,
                title,
                ingredients,
                instructions,
                userId,
                userName,
                cookingTime,
                ""  // Empty string for imageUrl
        );

        db.collection("recipes").document(recipeId)
                .set(recipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddRecipeActivity.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddRecipeActivity.this, "Error adding recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}