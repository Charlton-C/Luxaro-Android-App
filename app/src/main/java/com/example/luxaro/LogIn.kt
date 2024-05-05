package com.example.luxaro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.luxaro.databinding.ActivityLogInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LogIn : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.logInButton.setOnClickListener {
            val email = binding.editTextTextEmailAddressInput.text.toString()
            val password = binding.editTextTextPasswordInput.text.toString()
            if (checkAllFields()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this@LogIn, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LogIn, "Log In Failed", Toast.LENGTH_LONG).show()
                        Log.e("Error: ", it.exception.toString())
                    }
                }.addOnFailureListener{exception ->
                    Log.e("Firebase auth", "Error logging in", exception)
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@LogIn, SignUp::class.java))
            finish()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun checkAllFields():Boolean{
        val email = binding.editTextTextEmailAddressInput.text.toString()
        if (email == ""){
            binding.editTextTextEmailAddressLayout.error = "Please enter your email"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editTextTextEmailAddressLayout.error = "Please enter your email using the correct format"
            return false
        }
        if (binding.editTextTextPasswordInput.text.toString() == ""){
            binding.editTextTextPasswordLayout.error = "Please enter your password"
            binding.editTextTextPasswordLayout.errorIconDrawable = null
            return false
        }
        return true
    }
}