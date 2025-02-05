package com.example.shoping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private ImageButton menuButton;
    private MaterialCardView breakfastCategory, lunchCategory, dinnerCategory, dessertCategory;
    private MaterialCardView selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore.setLoggingEnabled(true); // Enable Firestore logging for debugging
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fab = findViewById(R.id.fab);
        menuButton = findViewById(R.id.menuButton);

        // Set up menu button click listener
        menuButton.setOnClickListener(v -> {
            showPopupMenu(v);
        });

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set up FAB click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
                startActivity(intent);
            }
        });


        // Check if user is signed in
        if (mAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        }

        // After checking if user is signed in
        if (mAuth.getCurrentUser() != null) {
            TextView userGreeting = findViewById(R.id.userGreeting);
            FirebaseUser user = mAuth.getCurrentUser();
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                userGreeting.setText("Hello, " + displayName);
            } else {
                // Fallback to email if name is not set
                String email = user.getEmail();
                String username = email != null ? email.split("@")[0] : "User";
                userGreeting.setText("Hello, " + username);
            }
        }

        initializeCategories();
    }

    private void initializeCategories() {
        breakfastCategory = findViewById(R.id.breakfastCategory);
        lunchCategory = findViewById(R.id.lunchCategory);
        dinnerCategory = findViewById(R.id.dinnerCategory);
        dessertCategory = findViewById(R.id.dessertCategory);

        // Set initial selection
        selectedCategory = breakfastCategory;

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory != null) {
                    // Reset previous selection
                    selectedCategory.setCardBackgroundColor(Color.WHITE);
                    ((TextView) ((LinearLayout) selectedCategory.getChildAt(0)).getChildAt(1))
                            .setTextColor(Color.BLACK);
                }

                // Update new selection
                MaterialCardView clicked = (MaterialCardView) v;
                clicked.setCardBackgroundColor(getResources().getColor(R.color.green, null));
                ((TextView) ((LinearLayout) clicked.getChildAt(0)).getChildAt(1))
                        .setTextColor(Color.WHITE);
                selectedCategory = clicked;
            }
        };

        // Set click listeners
        breakfastCategory.setOnClickListener(categoryClickListener);
        lunchCategory.setOnClickListener(categoryClickListener);
        dinnerCategory.setOnClickListener(categoryClickListener);
        dessertCategory.setOnClickListener(categoryClickListener);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            // Show main content, hide recipe fragment
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.setVisibility(View.VISIBLE);
            }
            Fragment recipeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (recipeFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(recipeFragment)
                        .commit();
            }
            return true;
        } else if (itemId == R.id.navigation_recipe) {
            // Hide main content, show recipe fragment
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.setVisibility(View.GONE);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecipeFragment())
                    .commit();
            return true;
        } else if (itemId == R.id.navigation_bookmarks) {
            Toast.makeText(this, "Bookmarks", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
