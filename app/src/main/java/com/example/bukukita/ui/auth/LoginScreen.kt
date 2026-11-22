package com.example.bukukita.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bukukita.R
import com.example.bukukita.ui.components.BukuKitaBrandMark
import com.example.bukukita.ui.components.BukuKitaPasswordField
import com.example.bukukita.ui.components.BukuKitaPrimaryButton
import com.example.bukukita.ui.components.BukuKitaTextField
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var emailOrUsername by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val loginEmptyError = stringResource(R.string.login_empty_error)
    val focusManager = LocalFocusManager.current
    val isLightSurface = MaterialTheme.colorScheme.surface.luminance() > 0.5f

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onNavigateToHome()
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearError() }
    }

    val submitLogin: () -> Unit = {
        if (!isLoading) {
            focusManager.clearFocus()
            if (emailOrUsername.isBlank() || password.isBlank()) {
                validationError = loginEmptyError
            } else {
                validationError = null
                viewModel.login(emailOrUsername.trim(), password)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BukuKitaSpacing.xl, vertical = BukuKitaSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Editorial Brand Emblem Badge
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    BukuKitaBrandMark(
                        contentDescription = stringResource(R.string.brand_logo_desc),
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(BukuKitaSpacing.sm))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

            Text(
                text = stringResource(R.string.login_welcome),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 320.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Elevated Paper Form Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = if (isLightSurface) 2.dp else 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(BukuKitaSpacing.lg)
                ) {
                    BukuKitaTextField(
                        value = emailOrUsername,
                        onValueChange = {
                            emailOrUsername = it
                            validationError = null
                            viewModel.clearError()
                        },
                        label = stringResource(R.string.email_or_username),
                        leadingIcon = Icons.Outlined.PersonOutline,
                        isError = validationError != null && emailOrUsername.isBlank(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(BukuKitaSpacing.md))

                    BukuKitaPasswordField(
                        value = password,
                        onValueChange = {
                            password = it
                            validationError = null
                            viewModel.clearError()
                        },
                        label = stringResource(R.string.password),
                        passwordVisible = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible },
                        isError = validationError != null && password.isBlank(),
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = { submitLogin() })
                    )

                    val activeError = validationError ?: errorMessage
                    AnimatedVisibility(visible = !activeError.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = BukuKitaSpacing.sm)
                        ) {
                            Text(
                                text = activeError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))

                    BukuKitaPrimaryButton(
                        text = stringResource(R.string.login_button),
                        onClick = submitLogin,
                        isLoading = isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = BukuKitaSpacing.touchTargetMin),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.login_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.register_link),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable(role = Role.Button, onClick = onNavigateToRegister)
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                )
            }
        }
    }
}
