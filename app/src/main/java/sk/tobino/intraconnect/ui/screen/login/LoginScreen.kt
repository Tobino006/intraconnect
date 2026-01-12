package sk.tobino.intraconnect.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sk.tobino.intraconnect.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }

    // if login was successful, navigate to home screen
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onLoginSuccess()
        }
    }

    // error dialog
    if (uiState.error != null) {
        AlertDialog (
            onDismissRequest = {
                viewModel.clearError()
            },
            title = { Text (
                stringResource(R.string.login_fail),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold)
                },
            text = { Text (
                stringResource(R.string.login_fail_text),
                style = MaterialTheme.typography.bodyLarge
                ) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text (
                        text = "OK",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // cannot login bottom sheet
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Text(
                    text = stringResource(R.string.cannot_login_message),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(150.dp))

            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // EMAIL
            TextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.email),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PASSWORD
            TextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.password),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
            )

            TextButton(onClick = { showBottomSheet = true }) {
                Text(
                    text = stringResource(R.string.cannot_login),
                    fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                    textDecoration = TextDecoration.Underline,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LOGIN BUTTON
            Button(
                onClick = { viewModel.login() },
                enabled = !uiState.loading,
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.sign_in),
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}