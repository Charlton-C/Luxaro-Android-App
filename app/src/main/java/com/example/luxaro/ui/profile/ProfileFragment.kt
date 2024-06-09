package com.example.luxaro.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.luxaro.LogIn
import com.example.luxaro.R
import com.example.luxaro.SignUp
import com.example.luxaro.ui.theme.LocalCustomColors
import com.example.luxaro.ui.theme.LuxaroTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LuxaroTheme {
                    DisplayProfile()
                }
            }
        }
    }
}

@Composable
fun DisplayProfile(modifier: Modifier = Modifier) {
    val auth = Firebase.auth
    val localContext = (LocalContext.current as? Activity)
    val focusManager = LocalFocusManager.current
    val name = remember { mutableStateOf(auth.currentUser?.displayName) }
    val email = remember { mutableStateOf(auth.currentUser?.email) }
    var passwordTitle by remember { mutableStateOf(R.string.password) }
    var passwordPlaceHolder by remember { mutableStateOf(R.string.password_hidden) }
    var displayChangePassword by remember { mutableStateOf(false) }
    var displaySavingPassword by remember { mutableStateOf(false) }
    val newName = remember { mutableStateOf("") }
    var readOnlyNewName by remember { mutableStateOf(true) }
    var displaySavingNewName by remember { mutableStateOf(false) }
    var newNameError by remember { mutableStateOf(false) }
    val newEmail = remember { mutableStateOf("") }
    val oldPassword = remember { mutableStateOf("") }
    var readOnlyPassword by remember { mutableStateOf(true) }
    var oldPasswordError by remember { mutableStateOf(false) }
    val newPassword = remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf(false) }
    val confirmNewPassword = remember { mutableStateOf("") }
    var confirmNewPasswordError by remember { mutableStateOf(false) }
    val confirmDeleteAccountPassword = remember { mutableStateOf("") }
    var confirmDeleteAccountPasswordError by remember { mutableStateOf(false) }
    var displayDeleteAccount by remember { mutableStateOf(false) }
    var displayLogOutAnimation by remember { mutableStateOf(false) }
    var displayDeleteAccountAnimation by remember { mutableStateOf(false) }
    newName.value = name.value.toString()
    newEmail.value = email.value.toString()

    // To handel back button clicks
    BackHandler (
        enabled = (!readOnlyNewName or !readOnlyPassword or displayDeleteAccount)
    ) {
        if (displayDeleteAccount){
            displayDeleteAccount = false
        }
        else if (!readOnlyPassword){
            oldPasswordError = false
            newPasswordError = false
            confirmNewPasswordError = false
            readOnlyPassword = true
            displayChangePassword = false
            passwordTitle = R.string.password
            passwordPlaceHolder = R.string.password_hidden
            oldPassword.value = ""
        }
        else if (!readOnlyNewName){
            newNameError = false
            readOnlyNewName = true
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 58.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(start = 30.dp, top = 8.dp, end = 30.dp, bottom = 12.dp),
        ) {
            Text(
                text = stringResource(id = R.string.name),
                modifier = modifier.padding(0.dp),
                fontSize = 19.sp,
                color = LocalCustomColors.current.profileTextFieldTitleTextColor,
            )
            DisplayTextInputField(
                input = newName,
                placeHolderTextID = R.string.name,
                editButton = true,
                displaySavingAnimation = displaySavingNewName,
                readOnly = readOnlyNewName,
                isError = newNameError,
                editContentDescription = R.string.edit_name,
                clearContentDescription = R.string.clear_name,
                onDoneClickAction = {
                    if (!readOnlyNewName) {
                        val checkNewNameResult = checkNewName(name.value.toString(), newName.value)
                        displaySavingNewName = true
                        when (checkNewNameResult) {
                            "passed" -> {
                                auth.currentUser!!.updateProfile(
                                    userProfileChangeRequest {
                                        displayName = newName.value
                                    }
                                ).addOnSuccessListener {
                                    Toast.makeText(localContext, localContext?.getString(R.string.name_updated), Toast.LENGTH_SHORT).show()
                                    name.value = auth.currentUser?.displayName
                                    displaySavingNewName = false
                                    newNameError = false
                                    readOnlyNewName = true
                                }.addOnFailureListener {
                                    Toast.makeText(localContext, localContext?.getString(R.string.failed_to_update_name), Toast.LENGTH_SHORT).show()
                                    displaySavingNewName = false
                                    newNameError = false
                                }
                            }

                            "old name and new name match" -> {
                                displaySavingNewName = false
                                newNameError = false
                                readOnlyNewName = true
                            }

                            else -> {
                                Toast.makeText(localContext, localContext?.getString(R.string.failed_to_update_name), Toast.LENGTH_SHORT).show()
                                displaySavingNewName = false
                                newNameError = true
                                readOnlyNewName = false
                            }
                        }
                    } else {
                        displaySavingNewName = false
                        newNameError = false
                        readOnlyNewName = false
                    }
                }
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(30.dp, 12.dp),
        ) {
            Text(
                text = stringResource(id = R.string.email),
                modifier = modifier.padding(0.dp),
                fontSize = 19.sp,
                color = LocalCustomColors.current.profileTextFieldTitleTextColor,
            )
            DisplayTextInputField(
                input = newEmail,
                placeHolderTextID = R.string.email,
                editButton = false,
                displaySavingAnimation = false,
                readOnly = true,
                isError = false,
                editContentDescription = R.string.edit_email,
                clearContentDescription = R.string.clear_email,
                onDoneClickAction = {})

        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(30.dp, 12.dp),
        ) {
            Text(
                text = stringResource(id = passwordTitle),
                modifier = modifier.padding(0.dp),
                fontSize = 19.sp,
                color = LocalCustomColors.current.profileTextFieldTitleTextColor,
            )
            DisplayTextInputField(input = oldPassword, placeHolderTextID = passwordPlaceHolder, editButton = true, displaySavingAnimation = displaySavingPassword, readOnly = readOnlyPassword, isError = oldPasswordError, editContentDescription = R.string.change_password, clearContentDescription = R.string.clear_old_password, onDoneClickAction = {
                if (!readOnlyPassword){
                    val checkNewPasswordResult = checkNewPassword(oldPassword.value, newPassword.value, confirmNewPassword.value)
                    displaySavingPassword = true
                    when (checkNewPasswordResult){
                        "passed" -> {
                            val credential = EmailAuthProvider.getCredential(email.value.toString(), oldPassword.value)
                            auth.currentUser!!.reauthenticate(credential)
                                .addOnSuccessListener {
                                    auth.currentUser?.updatePassword(newPassword.value)!!
                                        .addOnSuccessListener {
                                            Toast.makeText(localContext, localContext?.getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
                                            displaySavingPassword = false
                                            oldPasswordError = false
                                            newPasswordError = false
                                            confirmNewPasswordError = false
                                            readOnlyPassword = true
                                            displayChangePassword = false
                                            passwordTitle = R.string.password
                                            passwordPlaceHolder = R.string.password_hidden
                                            oldPassword.value = ""
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(localContext, localContext?.getString(R.string.failed_to_update_password), Toast.LENGTH_SHORT).show()
                                            displaySavingPassword = false
                                            oldPasswordError = false
                                            newPasswordError = false
                                            confirmNewPasswordError = false
                                        }
                                }.addOnFailureListener {
                                    Toast.makeText(localContext, localContext?.getString(R.string.old_password_is_wrong), Toast.LENGTH_SHORT).show()
                                    displaySavingPassword = false
                                    oldPasswordError = true
                                    oldPassword.value = ""
                                }
                        }
                        "cancel" -> {
                            displaySavingPassword = false
                            oldPasswordError = false
                            newPasswordError = false
                            confirmNewPasswordError = false
                            readOnlyPassword = true
                            displayChangePassword = false
                            passwordTitle = R.string.password
                            passwordPlaceHolder = R.string.password_hidden
                            displayDeleteAccount = false
                        }
                        "old password and new password match" -> {
                            val credential = EmailAuthProvider.getCredential(email.value.toString(), oldPassword.value)
                            auth.currentUser!!.reauthenticate(credential)
                                .addOnSuccessListener {
                                    displaySavingPassword = false
                                    oldPasswordError = false
                                    newPasswordError = false
                                    confirmNewPasswordError = false
                                    readOnlyPassword = true
                                    displayChangePassword = false
                                    passwordTitle = R.string.password
                                    passwordPlaceHolder = R.string.password_hidden
                                    oldPassword.value = ""
                                }.addOnFailureListener {
                                    Toast.makeText(localContext, localContext?.getString(R.string.old_password_is_wrong), Toast.LENGTH_SHORT).show()
                                    displaySavingPassword = false
                                    oldPasswordError = true
                                }
                        }
                        "no new password" -> {
                            Toast.makeText(localContext, localContext?.getString(R.string.no_new_password), Toast.LENGTH_SHORT).show()
                            displaySavingPassword = false
                            oldPasswordError = false
                            newPasswordError = true
                            confirmNewPasswordError = true
                            displayDeleteAccount = false
                        }
                        "new password is too short" -> {
                            Toast.makeText(localContext, localContext?.getString(R.string.new_password_is_too_short), Toast.LENGTH_SHORT).show()
                            displaySavingPassword = false
                            oldPasswordError = false
                            newPasswordError = true
                            confirmNewPasswordError = true
                            displayDeleteAccount = false
                        }
                        "new password and confirm new password do not match" -> {
                            Toast.makeText(localContext, localContext?.getString(R.string.new_password_and_confirm_new_password_do_not_match), Toast.LENGTH_SHORT).show()
                            displaySavingPassword = false
                            oldPasswordError = false
                            newPasswordError = false
                            confirmNewPasswordError = true
                            displayDeleteAccount = false
                        }
                        else -> {
                            Toast.makeText(localContext, localContext?.getString(R.string.failed_to_update_password), Toast.LENGTH_SHORT).show()
                            displaySavingPassword = false
                            oldPasswordError = false
                            newPasswordError = false
                            confirmNewPasswordError = false
                            readOnlyPassword = false
                            displayChangePassword = true
                            passwordTitle = R.string.old_password
                            passwordPlaceHolder = R.string.old_password
                            displayDeleteAccount = false
                        }
                    }
                }
                else{
                    displaySavingPassword = false
                    oldPasswordError = false
                    newPasswordError = false
                    confirmNewPasswordError = false
                    readOnlyPassword = false
                    displayChangePassword = true
                    passwordTitle = R.string.old_password
                    passwordPlaceHolder = R.string.old_password
                    displayDeleteAccount = false
                }
            })
        }
        AnimatedVisibility(
            visible = displayChangePassword,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(30.dp, 12.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.new_password),
                        modifier = modifier.padding(0.dp),
                        fontSize = 19.sp,
                        color = LocalCustomColors.current.profileTextFieldTitleTextColor,
                    )
                    DisplayTextInputField(input = newPassword, placeHolderTextID = R.string.new_password, editButton = false, displaySavingAnimation = false, readOnly = false, isError = newPasswordError, editContentDescription = R.string.new_password, clearContentDescription = R.string.clear_new_password, onDoneClickAction = {})
                }
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(30.dp, 12.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_password),
                        modifier = modifier.padding(0.dp),
                        fontSize = 19.sp,
                        color = LocalCustomColors.current.profileTextFieldTitleTextColor,
                    )
                    DisplayTextInputField(input = confirmNewPassword, placeHolderTextID = R.string.confirm_password, editButton = false, displaySavingAnimation = false, readOnly = false, isError = confirmNewPasswordError, editContentDescription = R.string.confirm_new_password, clearContentDescription = R.string.clear_confirm_new_password, onDoneClickAction = {})
                }
            }
        }
        Column(
            modifier = modifier.padding(top = 65.dp)
        ) {
            if(!displayLogOutAnimation) {
                Button(
                    onClick = {
                        displayLogOutAnimation = true
                        auth.signOut()
                        localContext?.startActivity(Intent(localContext, LogIn::class.java))
                        localContext?.finish()
                    },
                    border = BorderStroke(1.dp, LocalCustomColors.current.logOutButtonBorderStrokeColor),
                    colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.logOutButtonBackground)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        fontSize = 14.sp,
                        color = LocalCustomColors.current.logOutButtonTextColor,
                    )
                }
            }
            if(displayLogOutAnimation){
                CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally), color = LocalCustomColors.current.windowLoadingAnimationColor)
            }
        }
        Column(
            modifier = modifier.padding(top = 200.dp)
        ) {
            Button(onClick = {
                displayDeleteAccount = !displayDeleteAccount
                passwordTitle = R.string.password
                passwordPlaceHolder = R.string.password_hidden
                readOnlyPassword = true
                displayChangePassword = false
            },
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.deleteAccountPreviewButtonBackground)
            ) {
                Text(
                    text = stringResource(id = R.string.delete_account),
                    fontSize = 14.sp,
                    color = LocalCustomColors.current.deleteAccountPreviewButtonTextColor,
                )
            }
        }
        AnimatedVisibility(
            visible = displayDeleteAccount,
            enter = expandVertically(),
            exit = shrinkVertically(),
            modifier = modifier.padding(top = 8.dp, bottom = 70.dp),
        ) {
            Column {
                Column(modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .padding(start = 30.dp, top = 0.dp, end = 30.dp, bottom = 22.dp)) {
                    DisplayTextInputField(input = confirmDeleteAccountPassword, placeHolderTextID = R.string.confirm_password, editButton = false, displaySavingAnimation = false, readOnly = false, isError = confirmDeleteAccountPasswordError, editContentDescription = R.string.confirm_password, clearContentDescription = R.string.clear_confirm_password, onDoneClickAction = { focusManager.clearFocus() })
                }
                if(!displayDeleteAccountAnimation){
                    Button(
                        onClick = {
                            if (confirmDeleteAccountPassword.value.isNotEmpty()){
                                confirmDeleteAccountPasswordError = false
                                displayDeleteAccountAnimation = true
                                val credential = EmailAuthProvider.getCredential(email.value.toString(), confirmDeleteAccountPassword.value)
                                auth.currentUser!!.reauthenticate(credential)
                                    .addOnSuccessListener {
                                        auth.currentUser!!.delete()
                                            .addOnSuccessListener {
                                                confirmDeleteAccountPasswordError = false
                                                displayDeleteAccountAnimation = false
                                                localContext?.startActivity(Intent(localContext, SignUp::class.java))
                                                localContext?.finish()
                                            }.addOnFailureListener {
                                                Toast.makeText(localContext, localContext?.getString(R.string.failed_to_delete_account), Toast.LENGTH_SHORT).show()
                                                confirmDeleteAccountPasswordError = false
                                                displayDeleteAccountAnimation = false
                                            }
                                    }.addOnFailureListener {
                                        Toast.makeText(localContext, localContext?.getString(R.string.please_enter_the_correct_password), Toast.LENGTH_SHORT).show()
                                        confirmDeleteAccountPasswordError = true
                                        displayDeleteAccountAnimation = false
                                    }
                            }
                            else{
                                confirmDeleteAccountPasswordError = true
                            }
                        },
                        modifier = modifier.align(Alignment.CenterHorizontally),
                        border = BorderStroke(1.dp, LocalCustomColors.current.deleteAccountButtonBorderStrokeColor),
                        colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.deleteAccountButtonBackground)
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete_account),
                            fontSize = 15.sp,
                            color = LocalCustomColors.current.deleteAccountButtonTextColor,
                        )
                    }
                }
                if (displayDeleteAccountAnimation){
                    CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally), color = LocalCustomColors.current.windowLoadingAnimationColor)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayTextInputField(input: MutableState<String>, placeHolderTextID: Int, editButton: Boolean, displaySavingAnimation: Boolean, readOnly: Boolean, isError: Boolean, editContentDescription: Int, clearContentDescription: Int, onDoneClickAction: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    var editOrSaveIcon by remember { mutableStateOf(R.drawable.baseline_edit_square_24) }
    editOrSaveIcon = if(readOnly){ R.drawable.baseline_edit_square_24 } else { R.drawable.baseline_save_24 }
    BasicTextField(
        value = input.value,
        onValueChange = { input.value = it },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 12.dp),
        interactionSource = interactionSource,
        singleLine = true,
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(color = LocalCustomColors.current.profileTextFieldTextColor, fontSize = 19.sp),
        cursorBrush = SolidColor(LocalCustomColors.current.profileTextFieldCursorColor),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        visualTransformation = VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = { onDoneClickAction() }),
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = input.value,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            isError = isError,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            placeholder = { Text(text = stringResource(id = placeHolderTextID)) },
            trailingIcon = {
                Row {
                    if (!TextUtils.isEmpty(input.value) and !readOnly) {
                        IconButton(modifier = modifier
                            .fillMaxWidth(.1f)
                            .padding(0.dp),
                            onClick = { input.value = "" }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                                contentDescription = stringResource(id = clearContentDescription),
                                tint = LocalCustomColors.current.profileTextFieldIconColor,
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(20.dp),
                            )
                        }
                    }
                    if(editButton and !displaySavingAnimation) {
                        IconButton(modifier = modifier.padding(0.dp),
                            onClick = { onDoneClickAction() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = editOrSaveIcon),
                                contentDescription = stringResource(id = editContentDescription),
                                tint = LocalCustomColors.current.profileTextFieldIconColor,
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(25.dp),
                            )
                        }
                    }
                    if (displaySavingAnimation){
                        CircularProgressIndicator(
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 10.dp)
                                .size(25.dp),
                            color = LocalCustomColors.current.profileTextFieldLoadingAnimationColor)
                    }
                }
            },
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 8.dp,
                end = 12.dp,
                bottom = 8.dp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                unfocusedTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                disabledTextColor = LocalCustomColors.current.profileTextFieldTextColor.copy(alpha = 0.5f),
                errorTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                focusedContainerColor = LocalCustomColors.current.profileTextFieldBackground,
                unfocusedContainerColor = LocalCustomColors.current.profileTextFieldBackground,
                disabledContainerColor = LocalCustomColors.current.profileTextFieldBackground.copy(alpha = 0.5f),
                errorContainerColor = LocalCustomColors.current.profileTextFieldBackground,
                cursorColor = LocalCustomColors.current.profileTextFieldCursorColor,
                errorCursorColor = LocalCustomColors.current.profileTextFieldCursorColor,
                focusedBorderColor = LocalCustomColors.current.profileTextFieldBorderColor,
                unfocusedBorderColor = LocalCustomColors.current.profileTextFieldBorderColor,
                disabledBorderColor = LocalCustomColors.current.profileTextFieldBorderColor.copy(alpha = 0.5f),
                errorBorderColor = LocalCustomColors.current.profileTextFieldBorderColor,
                focusedLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                unfocusedLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                disabledLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor.copy(alpha = 0.5f),
                errorLeadingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                focusedTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                unfocusedTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                disabledTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor.copy(alpha = 0.5f),
                errorTrailingIconColor = LocalCustomColors.current.searchTextFieldIconColor,
                focusedLabelColor = LocalCustomColors.current.profileTextFieldTitleTextColor,
                unfocusedLabelColor = LocalCustomColors.current.profileTextFieldTitleTextColor,
                disabledLabelColor = LocalCustomColors.current.profileTextFieldTitleTextColor.copy(alpha = 0.5f),
                errorLabelColor = LocalCustomColors.current.profileTextFieldTitleTextColor,
                focusedPlaceholderColor = LocalCustomColors.current.profileTextFieldTextColor,
                unfocusedPlaceholderColor = LocalCustomColors.current.profileTextFieldTextColor,
                disabledPlaceholderColor = LocalCustomColors.current.profileTextFieldTextColor.copy(alpha = 0.5f),
                errorPlaceholderColor = LocalCustomColors.current.profileTextFieldTextColor,
                focusedSupportingTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                unfocusedSupportingTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                disabledSupportingTextColor = LocalCustomColors.current.profileTextFieldTextColor.copy(alpha = 0.5f),
                errorSupportingTextColor = LocalCustomColors.current.profileTextFieldTextColor,
                focusedPrefixColor = LocalCustomColors.current.profileTextFieldTextColor,
                unfocusedPrefixColor = LocalCustomColors.current.profileTextFieldTextColor,
                disabledPrefixColor = LocalCustomColors.current.profileTextFieldTextColor.copy(alpha = 0.5f),
                errorPrefixColor = LocalCustomColors.current.profileTextFieldTextColor,
                focusedSuffixColor = LocalCustomColors.current.profileTextFieldTextColor,
                unfocusedSuffixColor = LocalCustomColors.current.profileTextFieldTextColor,
                disabledSuffixColor = LocalCustomColors.current.profileTextFieldTextColor.copy(alpha = 0.5f),
                errorSuffixColor = LocalCustomColors.current.profileTextFieldTextColor,
            ),
        )
    }
}

fun checkNewName(oldName: String, newName: String): String {
    if (newName == ""){
        return "no new name"
    }
    if (newName == oldName){
        return "old name and new name match"
    }
    return "passed"
}

fun checkNewPassword(oldPassword: String, newPassword: String, confirmPassword: String): String {
    if ((oldPassword == "") and (newPassword == "") and (confirmPassword == "")){
        return "cancel"
    }
    if (oldPassword == ""){
        return "no old password"
    }
    if (newPassword == "") {
        return "no new password"
    }
    if (newPassword.length <= 8) {
        return "new password is too short"
    }
    if (newPassword == oldPassword){
        return "old password and new password match"
    }
    if (newPassword != confirmPassword) {
        return "new password and confirm new password do not match"
    }
    return "passed"
}