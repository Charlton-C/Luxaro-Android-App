package com.example.luxaro

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.luxaro.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        if(user == null){
            startActivity(Intent(this@MainActivity, LogIn::class.java))
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.ateneo_blue)))

        navView.setOnItemSelectedListener { item ->
            navView.menu.getItem(0).setIcon(R.drawable.home_icon_outline_24)
            navView.menu.getItem(1).setIcon(R.drawable.baseline_favorite_border_24)
            navView.menu.getItem(2).setIcon(R.drawable.baseline_person_outline_24)
            when (item.itemId) {
                R.id.navigation_home -> {
                    item.icon = AppCompatResources.getDrawable(this@MainActivity, R.drawable.home_icon_filled_24)
                }
                R.id.navigation_favorites -> {
                    item.icon = AppCompatResources.getDrawable(this@MainActivity, R.drawable.baseline_favorite_24)
                }
                R.id.navigation_profile -> {
                    item.icon = AppCompatResources.getDrawable(this@MainActivity, R.drawable.baseline_person_24)
                }
            }
            false
        }
    }
}