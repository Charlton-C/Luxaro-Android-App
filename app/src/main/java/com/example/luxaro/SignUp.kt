package com.example.luxaro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.luxaro.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth


class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener {
            binding.editTextTextNameLayout.isErrorEnabled = false
            binding.editTextTextEmailAddressLayout.isErrorEnabled = false
            binding.editTextTextPasswordLayout.isErrorEnabled = false
            binding.editTextTextConfirmPasswordLayout.isErrorEnabled = false
            val name = binding.editTextTextNameInput.text.toString()
            val email = binding.editTextTextEmailAddressInput.text.toString()
            val password = binding.editTextTextPasswordInput.text.toString()
            if (checkAllFields()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.user?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                        )
                        startActivity(Intent(this@SignUp, MainActivity::class.java))
                        finish()
                    } else {
                        if (it.exception is FirebaseAuthUserCollisionException) {
                            binding.editTextTextEmailAddressLayout.error = "A user with this email already exists"
                        }
                        else{
                            Toast.makeText(this@SignUp, "Sign Up Failed\nPlease try again later", Toast.LENGTH_LONG).show()
                            Log.e("Firebase Auth Error", it.exception.toString())
                        }
                    }
                }
            }

        }

        binding.logInButton.setOnClickListener {
            startActivity(Intent(this@SignUp, LogIn::class.java))
            finish()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun checkAllFields(): Boolean {
        val email = binding.editTextTextEmailAddressInput.text.toString()
        if (binding.editTextTextNameInput.text.toString() == ""){
            binding.editTextTextNameLayout.error = "Please enter your name"
            return false
        }
        if (email == ""){
            binding.editTextTextEmailAddressLayout.error = "Please enter your email"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editTextTextEmailAddressLayout.error = "Please enter your email using the correct format"
            return false
        }
        if (binding.editTextTextPasswordInput.text.toString() == ""){
            binding.editTextTextPasswordLayout.error = "Please enter a password"
            binding.editTextTextPasswordLayout.errorIconDrawable = null
            return false
        }
        if(binding.editTextTextPasswordInput.length() <= 8){
            binding.editTextTextPasswordLayout.error = "Password is too short"
            binding.editTextTextPasswordLayout.errorIconDrawable = null
            return false
        }
        if(binding.editTextTextConfirmPasswordInput.text.toString() != binding.editTextTextPasswordInput.text.toString()){
            binding.editTextTextConfirmPasswordLayout.error = "Passwords don't match"
            binding.editTextTextConfirmPasswordLayout.errorIconDrawable = null
            return false
        }
        return true
    }
}