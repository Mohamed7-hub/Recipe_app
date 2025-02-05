package com.example.shoping;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;
    private EditText searchEditText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allRecipes = new ArrayList<>();
        filteredRecipes = new ArrayList<>();
        adapter = new RecipeAdapter(filteredRecipes, FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupSearchListener();
        loadAllRecipes();

        return view;
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterRecipes(s.toString());
            }
        });
    }

    private void filterRecipes(String query) {
        filteredRecipes.clear();
        if (query.isEmpty()) {
            filteredRecipes.addAll(allRecipes);
        } else {
            String lowercaseQuery = query.toLowerCase();
            for (Recipe recipe : allRecipes) {
                if (recipeMatchesSearch(recipe, lowercaseQuery)) {
                    filteredRecipes.add(recipe);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean recipeMatchesSearch(Recipe recipe, String query) {
        // Check if query matches recipe title
        if (recipe.getTitle().toLowerCase().contains(query)) {
            return true;
        }

        // Check if query matches any ingredient
        String ingredients = recipe.getIngredients().toLowerCase();
        return ingredients.contains(query);
    }

    private void loadAllRecipes() {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allRecipes.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Recipe recipe = document.toObject(Recipe.class);
                                allRecipes.add(recipe);
                            } catch (Exception e) {
                                Toast.makeText(getContext(),
                                        "Error loading recipe: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        // Initialize filtered recipes with all recipes
                        filteredRecipes.clear();
                        filteredRecipes.addAll(allRecipes);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(),
                                "Error loading recipes: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

