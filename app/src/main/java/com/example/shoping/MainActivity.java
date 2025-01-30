package com.example.shoping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        fab.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Add new recipe", Toast.LENGTH_SHORT).show();
        });

        // Check if user is signed in
        if (mAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        }
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
        if (itemId == R.id.navigation_recipes) {
            Toast.makeText(this, "Recipes", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_diets) {
            Toast.makeText(this, "Comments", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_bookmarks) {
            Toast.makeText(this, "Bookmarks", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}