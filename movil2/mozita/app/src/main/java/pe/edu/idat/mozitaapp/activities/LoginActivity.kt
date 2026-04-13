package pe.edu.idat.mozitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pe.edu.idat.mozitaapp.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var password: EditText? = null
    private var btnLogin: Button? = null
    private var btnRegister: Button? = null
    private var btnGoogle: Button? = null

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    private var mAuth: FirebaseAuth? = null

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = mAuth?.currentUser
        if (currentUser != null) {
            irAMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.button)
        btnRegister = findViewById(R.id.button2)
        btnGoogle = findViewById(R.id.btnGoogle)

        btnLogin?.setOnClickListener { validarDatos() }

        btnRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnGoogle?.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validarDatos() {
        val correo = email?.text.toString().trim()
        val pass = password?.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth?.signInWithEmailAndPassword(correo, pass)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    irAMain()
                } else {
                    val errorMsg = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login con Google exitoso", Toast.LENGTH_SHORT).show()
                    irAMain()
                } else {
                    Toast.makeText(this, "Error en autenticación Google", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openProfile", true)
        startActivity(intent)
        finish()
    }
}