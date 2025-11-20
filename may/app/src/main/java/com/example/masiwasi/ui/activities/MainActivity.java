package com.example.masiwasi.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.masiwasi.R;
import com.example.masiwasi.ui.fragments.HomeFragment;
import com.example.masiwasi.ui.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        boolean openProfile = getIntent().getBooleanExtra("openProfile", false);

        if (openProfile) {
            loadFragment(new ProfileFragment());
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }


        bottomNavigation = findViewById(R.id.bottom_navigation);


        loadFragment(new HomeFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_profile) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }

            // Cargar Home
            if (id == R.id.nav_home) {
                return loadFragment(new HomeFragment());
            }

            return loadFragment(new HomeFragment());
        });


    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}