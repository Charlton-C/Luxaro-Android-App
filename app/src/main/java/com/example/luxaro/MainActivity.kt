package com.example.luxaro

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.luxaro.databinding.ActivityMainBinding
import com.example.luxaro.model.PropertyModelPackage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
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
    private lateinit var drawerLayout: DrawerLayout
    private var selectedTheme: Int? = null
    private lateinit var lightdarkModeDrawerButton: MenuItem
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var preferences: SharedPreferences
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

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        selectedTheme = preferences.getInt("selected_theme", 2)
        drawerLayout = binding.activityMainNavigationDrawer
        lightdarkModeDrawerButton = binding.activityMainNavigationView.menu.findItem(R.id.navigation_drawer_theme_item_button)
        actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(MaterialColors.getColor(View(this), android.R.attr.windowBackground)))

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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        }
        else if(item.itemId == R.id.navigation_search){
            startActivity(Intent(this@MainActivity, Search::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}


var areThereAnyPropertiesToShow = mutableStateOf("")
var areThereAnyLikedPropertiesToShow = mutableStateOf("")
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
                singleProperty.title = singleProperty.title.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r")
                singleProperty.shortdescription = singleProperty.shortdescription.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r")
                singleProperty.longdescription = singleProperty.longdescription.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r")
                propertiesAvailable.add(singleProperty)
            }
            areThereAnyPropertiesToShow.value = "true"
        }
        .addOnFailureListener { exception ->
            Log.e("Firestone", "Error getting properties.", exception)
            areThereAnyPropertiesToShow.value = "false"
        }
}

fun getPropertiesLikedByUserAndAddThemToPropertiesLikedByUserVariable() {
    val likedPropertiesIds = mutableStateListOf<String>()
    val firebaseRealtimeDatabase = FirebaseDatabase.getInstance()
    val firebaseRealtimeDatabaseReference = firebaseRealtimeDatabase.getReference(FirebaseAuth.getInstance().currentUser?.uid.toString())
    firebaseRealtimeDatabaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
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
            areThereAnyLikedPropertiesToShow.value = "true"
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase Realtime DB Error", error.toString())
            areThereAnyLikedPropertiesToShow.value = "false"
        }
    })
}

fun likeProperty(property: PropertyModelPackage){
    val propertyID: String = property.id.replace('/', '_').replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_')
    val firebaseRealtimeDatabase = FirebaseDatabase.getInstance()
    val firebaseRealtimeDatabaseReference = firebaseRealtimeDatabase.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child(propertyID)
    firebaseRealtimeDatabaseReference.setValue("true")
    propertiesLikedByUser.add(property)
}

fun unlikeProperty(property: PropertyModelPackage){
    val propertyID: String = property.id.replace('/', '_').replace('.', '_').replace('#', '_').replace('$', '_').replace('[', '_').replace(']', '_')
    val firebaseRealtimeDatabase = FirebaseDatabase.getInstance()
    val firebaseRealtimeDatabaseReference = firebaseRealtimeDatabase.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child(propertyID)
    firebaseRealtimeDatabaseReference.setValue("false")
    propertiesLikedByUser.remove(property)
}