package pe.edu.idat.mozitaapp.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.edu.idat.mozitaapp.R
import pe.edu.idat.mozitaapp.fragments.HomeFragments
import pe.edu.idat.mozitaapp.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {

    private var bottomNavigation: BottomNavigationView? = null

    private var btnOpenCamara: Button? = null
    private var btnOpenGalery: Button? = null
    private var imvPhoto: ImageView? = null

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val extras = result.data!!.extras
            val imageBitmap = extras?.get("data") as Bitmap?
            imvPhoto?.setImageBitmap(imageBitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUri = result.data!!.data
            imvPhoto?.setImageURI(imageUri)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        btnOpenCamara = findViewById(R.id.btnOpenCamara)
        btnOpenGalery = findViewById(R.id.btnOpenGalery)
        imvPhoto = findViewById(R.id.imvPhoto)

        setupHardwareButtons()

        loadFragment(HomeFragments())

        // Navegación desde intent
        if (intent?.hasExtra("navigateTo") == true) {
            if (intent.getStringExtra("navigateTo") == "profile") {
                loadFragment(ProfileFragment())
                bottomNavigation?.selectedItemId = R.id.nav_profile
            }
        }

        bottomNavigation?.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {

                R.id.nav_home -> {
                    loadFragment(HomeFragments())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                R.id.nav_profile2 -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("openProfile", true)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }


    private fun setupHardwareButtons() {
        btnOpenCamara?.setOnClickListener {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        btnOpenGalery?.setOnClickListener {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        return if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        } else {
            false
        }
    }
}