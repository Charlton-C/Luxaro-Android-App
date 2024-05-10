package com.example.luxaro.ui.profile

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.luxaro.R
import com.google.firebase.Firebase
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
                color = Color.White,
            )
            DisplayTextInputField(
                input = newName,
                placeHolderTextID = R.string.name,
                editButton = true,
                readOnly = readOnlyName,
                isError = newNameError,
                onDoneClickAction = {
                    if (!readOnlyName) {
                        val checkNewNameResult = checkNewName(name.value.toString(), newName.value)
                        when (checkNewNameResult) {
                            "passed" -> {
                                auth.currentUser!!.updateProfile(
                                    userProfileChangeRequest {
                                        displayName = newName.value
                                    }
                                ).addOnSuccessListener {
                                    Toast.makeText(
                                        localContext,
                                        "Name Updated!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    name.value = auth.currentUser?.displayName
                                    newNameError = false
                                    readOnlyName = true
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        localContext,
                                        "Failed to update name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    newNameError = false
                                }
                            }

                            "old name and new name match" -> {
                                Toast.makeText(
                                    localContext,
                                    "The name is unchanged",
                                    Toast.LENGTH_SHORT
                                ).show()
                                newNameError = false
                                readOnlyName = true
                            }

                            else -> {
                                Toast.makeText(localContext, checkNewNameResult, Toast.LENGTH_SHORT)
                                    .show()
                                newNameError = true
                                readOnlyName = false
                            }
                        }
                    } else {
                        newNameError = false
                        readOnlyName = false
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
                color = Color.White,
            )
            DisplayTextInputField(
                input = newEmail,
                placeHolderTextID = R.string.email,
                editButton = false,
                readOnly = true,
                isError = false,
                onDoneClickAction = {})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayTextInputField(input: MutableState<String>, placeHolderTextID: Int, editButton: Boolean, readOnly: Boolean, isError: Boolean, onDoneClickAction: () -> Unit, modifier: Modifier = Modifier){
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
        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 19.sp),
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        visualTransformation = VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = { onDoneClickAction() }),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = input.value,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            isError = isError,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 8.dp,
                end = 12.dp,
                bottom = 8.dp
            ),
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
                                contentDescription = stringResource(id = R.string.clear_name),
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(20.dp),
                            )
                        }
                    }
                    if(editButton) {
                        IconButton(modifier = modifier.padding(0.dp),
                            onClick = { onDoneClickAction() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = editOrSaveIcon),
                                contentDescription = stringResource(id = R.string.edit_name),
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(25.dp),
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.rich_electric_blue),
                unfocusedContainerColor = colorResource(id = R.color.rich_electric_blue),
                errorContainerColor = colorResource(id = R.color.rich_electric_blue),
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Red,
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = Color.White,
                unfocusedPlaceholderColor = Color.White,
                errorPlaceholderColor = Color.White,
                disabledPlaceholderColor = Color.Transparent,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White,
                errorTrailingIconColor = Color.Red,
                disabledTrailingIconColor = Color.Transparent,
                errorTextColor = Color.White,
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
    if (oldPassword == ""){
        return "no new password"
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