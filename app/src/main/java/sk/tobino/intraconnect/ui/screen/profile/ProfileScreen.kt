package sk.tobino.intraconnect.ui.screen.profile

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import sk.tobino.intraconnect.data.model.UserDto
import sk.tobino.intraconnect.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen (
    user: UserDto?,
    onProfileUpdated: () -> Unit,
    onLogout: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState = vm.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(user?.id) {
        if (uiState.user == null) {
            vm.loadProfile()
        }
    }

    var phone by remember { mutableStateOf(uiState.phone) }
    var newPassword by remember { mutableStateOf("") }

    // launcher for picture upload from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && activity != null) {
            vm.uploadAvatar(uri, activity.contentResolver)
        }
    }

    // if state changes from vm, update local state
    LaunchedEffect(uiState) {
        phone = uiState.phone
    }

    // if signal for logout is received (during password change)
    LaunchedEffect(uiState.logout) {
        if (uiState.logout) {
            onLogout()
            vm.resetLogout()
        }
    }

    if (uiState.isLoading) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (uiState.errorMessage != null) {
        AlertDialog (
            onDismissRequest = { vm.clearMessage() },
            title = {
                Text (
                    text = stringResource(R.string.auth_weak_password_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text (
                    text = stringResource(R.string.auth_weak_password_exception),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.clearMessage() }) {
                    Text (
                        text = "OK",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
    uiState.successMessage?.let {
        LaunchedEffect(it) {
            onProfileUpdated()
            vm.clearMessage()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // avatar in the middle
        val avatarPainter = rememberAsyncImagePainter (
            model = uiState.avatarUrl ?: user?.avatarUrl,
        )

        Box (
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.avatarUrl == null && user?.avatarUrl == null) {
                // default avatar
                Image (
                    painter = painterResource(R.drawable.ic_avatar_default),
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                )
            } else {
                Image (
                    painter = avatarPainter,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.background, CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -----    info about user (read only)     -----
        Text (
            text = uiState.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.email?.let { email ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        user?.position?.let { position ->
            Text(
                text = "${stringResource(R.string.position)}: $position",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // department
        uiState.departmentName?.takeIf { it.isNotBlank() }?.let { dept ->
            Text(
                text = "${stringResource(R.string.department)}: $dept",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // company
        uiState.companyName?.takeIf { it.isNotBlank() }?.let { company ->
            Text(
                text = "${stringResource(R.string.company)}: $company",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
        )

        // -----    info about user (editable)     -----

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField (
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.phone)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField (
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(stringResource(R.string.new_password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(24.dp))

        Button (
            onClick = {
                vm.saveProfile(phone = phone, newPassword = newPassword)
                newPassword = ""
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_changes))
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    vm.logout()
                }
            },
            modifier = Modifier
                .width(160.dp)
                .height(60.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}