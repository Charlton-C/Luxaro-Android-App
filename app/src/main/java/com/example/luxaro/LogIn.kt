package com.example.luxaro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.luxaro.databinding.ActivityLogInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
            binding.editTextTextEmailAddressLayout.isErrorEnabled = false
            binding.editTextTextPasswordLayout.isErrorEnabled = false
            val email = binding.editTextTextEmailAddressInput.text.toString()
            val password = binding.editTextTextPasswordInput.text.toString()
            if (checkAllFields()) {
                showLoggingInAnimation(true)
                // Check if the user has an account
                auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
                    if (it.signInMethods!!.size > 0){
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                            showLoggingInAnimation(false)
                            startActivity(Intent(this@LogIn, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener {it2 ->
                            when (it2){
                                is FirebaseAuthInvalidCredentialsException -> {
                                    binding.editTextTextPasswordLayout.error = resources.getString(R.string.wrong_password)
                                }
                                else -> {
                                    Toast.makeText(this@LogIn, resources.getString(R.string.log_in_failed), Toast.LENGTH_SHORT).show()
                                    Log.e(resources.getString(R.string.firebase_auth_error_colon_log_in), it2.toString())
                                }
                            }
                            showLoggingInAnimation(false)
                        }
                    }
                    else {
                        binding.editTextTextEmailAddressLayout.error = resources.getString(R.string.no_user_found)
                        showLoggingInAnimation(false)
                    }
                }.addOnFailureListener {
                    showLoggingInAnimation(false)
                    Toast.makeText(this@LogIn, resources.getString(R.string.log_in_failed), Toast.LENGTH_SHORT).show()
                    Log.e(resources.getString(R.string.firebase_auth_error_colon_log_in), it.toString())
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@LogIn, SignUp::class.java))
            finish()
        }
    }

    private fun checkAllFields():Boolean{
        val email = binding.editTextTextEmailAddressInput.text.toString()
        if (email == ""){
            binding.editTextTextEmailAddressLayout.error = resources.getString(R.string.please_enter_your_email)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editTextTextEmailAddressLayout.error = resources.getString(R.string.please_enter_your_email_using_the_correct_format)
            return false
        }
        if (binding.editTextTextPasswordInput.text.toString() == ""){
            binding.editTextTextPasswordLayout.error = resources.getString(R.string.please_enter_your_password)
            binding.editTextTextPasswordLayout.errorIconDrawable = null
            return false
        }
        return true
    }

    private fun showLoggingInAnimation(show: Boolean){
        // Start logging in animation
        if (show){
            binding.logInButton.visibility = View.GONE
            binding.logInProgressBar.visibility = View.VISIBLE
            binding.signUpButton.isEnabled = false
            binding.signUpButton.isClickable = false
        }
        // End logging in animation
        else{
            binding.logInButton.visibility = View.VISIBLE
            binding.logInProgressBar.visibility = View.GONE
            binding.signUpButton.isEnabled = true
            binding.signUpButton.isClickable = true
        }
    }
}