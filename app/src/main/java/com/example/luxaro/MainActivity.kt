package com.example.luxaro

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateListOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.luxaro.databinding.ActivityMainBinding
import com.example.luxaro.model.PropertyModelPackage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
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

        val drawerLayout: DrawerLayout = binding.activityMainNavigationDrawer
        val lightdarkModeDrawerButton = binding.activityMainNavigationView.menu.findItem(R.id.navigation_drawer_light_dark_mode)
        actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        lightdarkModeDrawerButton.setOnMenuItemClickListener {
            Toast.makeText(this@MainActivity, R.string.light_mode_not_added_yet, Toast.LENGTH_SHORT).show()
            true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.ateneo_blue)))


        val navView: BottomNavigationView = binding.navViewBottom

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Apply dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
        lightdarkModeDrawerButton.setChecked(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            // Change the navigation bar color for android phone to match the drawer background color
            if (window.navigationBarColor == resources.getColor(R.color.ocean_boat_blue_1)){
                window.navigationBarColor = resources.getColor(R.color.medium_persian_blue_2)
            }
            else{
                window.navigationBarColor = resources.getColor(R.color.ocean_boat_blue_1)
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}


var propertiesAvailable = mutableStateListOf<PropertyModelPackage>()
var propertiesLikedByUser = mutableStateListOf<PropertyModelPackage>()
var runFunGetPropertiesAvailableFromFirebaseAndAddThemToPropertiesAvailableVariable = getPropertiesAvailableFromFirebaseAndAddThemToPropertiesAvailableVariable()
var runFunGetPropertiesLikedByUserAndAddThemToPropertiesLikedByUserVariable = getPropertiesLikedByUserAndAddThemToPropertiesLikedByUserVariable()

fun getPropertiesAvailableFromFirebaseAndAddThemToPropertiesAvailableVariable() {
    val firebaseDB = Firebase.firestore
    firebaseDB.collection("properties").get()
        .addOnSuccessListener { properties ->
            for (property in properties) {
                val singleProperty = property.toObject(PropertyModelPackage::class.java)
                singleProperty.id = property.id
                propertiesAvailable.add(singleProperty)
                Log.e("something", singleProperty.title)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firestone", "Error getting properties.", exception)
        }
}

fun getPropertiesLikedByUserAndAddThemToPropertiesLikedByUserVariable() {
    val likedPropertiesIds = mutableStateListOf<String>()
    val firebaseRealtimeDatabase = FirebaseDatabase.getInstance()
    val firebaseRealtimeDatabaseReference = firebaseRealtimeDatabase.getReference(FirebaseAuth.getInstance().currentUser?.uid.toString())
    firebaseRealtimeDatabaseReference.addValueEventListener(object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                if(it.getValue(String::class.java) == "true"){
                    likedPropertiesIds.add(it.key.toString())
                }
            }
            for (id in likedPropertiesIds){
                propertiesLikedByUser.add(propertiesAvailable.find{ property -> id == property.id }!!)
            }
            for (propertyOriginal in propertiesAvailable){
                for (property in propertiesLikedByUser){
                    if (property.id == propertyOriginal.id) {
                        propertyOriginal.liked.value = true
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase Realtime DB Error", error.toString())
        }
    })
}