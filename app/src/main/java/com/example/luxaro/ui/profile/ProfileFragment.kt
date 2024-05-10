package com.example.luxaro.ui.profile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.luxaro.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DisplayProfile()
            }
        }
    }
}

@Composable
fun DisplayProfile(modifier: Modifier = Modifier){
    val auth = Firebase.auth
    val localContext = (LocalContext.current as? Activity)
    val name = remember { mutableStateOf(auth.currentUser?.displayName) }
    val email = remember { mutableStateOf(auth.currentUser?.email) }
    var passwordTitle by remember { mutableStateOf(R.string.password) }
    var passwordPlaceHolder by remember { mutableStateOf(R.string.password_hidden) }
    var displayChangePassword by remember { mutableStateOf(false) }
    val newName = remember { mutableStateOf("") }
    var readOnlyName by remember { mutableStateOf(true) }
    var newNameError by remember { mutableStateOf(false) }
    val newEmail = remember { mutableStateOf("") }
    val oldPassword = remember { mutableStateOf("") }
    var readOnlyPassword by remember { mutableStateOf(true) }
    var oldPasswordError by remember { mutableStateOf(false) }
    val newPassword = remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf(false) }
    val confirmNewPassword = remember { mutableStateOf("") }
    var confirmNewPasswordError by remember { mutableStateOf(false) }
    var displayDeleteAccount by remember { mutableStateOf(false) }
    newName.value = name.value.toString()
    newEmail.value = email.value.toString()
}