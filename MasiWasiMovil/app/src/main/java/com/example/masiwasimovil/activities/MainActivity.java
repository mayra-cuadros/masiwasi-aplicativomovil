package com.example.masiwasimovil.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.masiwasimovil.R;
import com.example.masiwasimovil.fragments.HomeFragment;
import com.example.masiwasimovil.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        loadFragment(new HomeFragment());

        String navigateTo = getIntent().getStringExtra("navigateTo");

        if ("profile".equals(navigateTo)) {
            loadFragment(new ProfileFragment());
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }


        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            }else if(id == R.id.nav_profile2){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("openProfile", true);
                startActivity(intent);
            }

            return loadFragment(selectedFragment);
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