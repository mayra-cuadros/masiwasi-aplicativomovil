package com.example.masiwasimovil.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.masiwasimovil.R;
import com.example.masiwasimovil.fragments.HomeFragment;
import com.example.masiwasimovil.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigation;


    private Button btnOpenCamara;
    private Button btnOpenGalery;
    private ImageView imvPhoto;


    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imvPhoto != null) imvPhoto.setImageBitmap(imageBitmap);
                    }
                }
            }
    );


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imvPhoto != null) imvPhoto.setImageURI(imageUri);
                }
            }
    );


    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        bottomNavigation = findViewById(R.id.bottom_navigation);


        btnOpenCamara = findViewById(R.id.btnOpenCamara);
        btnOpenGalery = findViewById(R.id.btnOpenGalery);
        imvPhoto = findViewById(R.id.imvPhoto);

        setupHardwareButtons();


        loadFragment(new HomeFragment());

        if (getIntent() != null && getIntent().hasExtra("navigateTo")) {
            String navigateTo = getIntent().getStringExtra("navigateTo");
            if ("profile".equals(navigateTo)) {
                loadFragment(new ProfileFragment());
                bottomNavigation.setSelectedItemId(R.id.nav_profile);
            }
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if(id == R.id.nav_profile2){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("openProfile", true);
                startActivity(intent);
            }
            return loadFragment(selectedFragment);
        });
    }



    private void setupHardwareButtons() {
        if (btnOpenCamara != null) {
            btnOpenCamara.setOnClickListener(v -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA));
        }

        if (btnOpenGalery != null) {
            btnOpenGalery.setOnClickListener(v -> {

                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            });
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
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