package com.example.bukukita.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.bukukita.R
import com.example.bukukita.ui.components.BukuKitaPrimaryButton
import com.example.bukukita.ui.components.BukuKitaTextField
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val isUpdating by authViewModel.isUpdating.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val currentEmail = remember(authViewModel) { authViewModel.getCurrentEmail() ?: "" }

    DisposableEffect(Unit) {
        onDispose { authViewModel.clearError() }
    }

    EditProfileScreenContent(
        initialUsername = userProfile?.username ?: "",
        initialFullName = userProfile?.fullName ?: "",
        initialEmail = currentEmail,
        avatarUrl = userProfile?.avatarUrl,
        isUpdating = isUpdating,
        errorMessage = errorMessage,
        onClearError = authViewModel::clearError,
        onSaveChanges = { username, fullName, avatarBytes, newEmail ->
            authViewModel.updateProfile(
                username = username,
                fullName = fullName,
                avatarBytes = avatarBytes,
                newEmail = newEmail,
                onSuccess = onNavigateBack
            )
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
internal fun EditProfileScreenContent(
    initialUsername: String,
    initialFullName: String,
    initialEmail: String,
    avatarUrl: String?,
    isUpdating: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
    onSaveChanges: (username: String, fullName: String, avatarBytes: ByteArray?, newEmail: String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf(initialUsername) }
    var fullName by rememberSaveable { mutableStateOf(initialFullName) }
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val isLightSurface = MaterialTheme.colorScheme.surface.luminance() > 0.5f

    LaunchedEffect(initialUsername, initialFullName) {
        if (username.isBlank() && initialUsername.isNotBlank()) {
            username = initialUsername
        }
        if (fullName.isBlank() && initialFullName.isNotBlank()) {
            fullName = initialFullName
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
    }

    val submitChanges: () -> Unit = {
        focusManager.clearFocus()
        coroutineScope.launch {
            val avatarBytes = avatarUri?.let { uri ->
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }
            }
            onSaveChanges(
                username.trim(),
                fullName.trim(),
                avatarBytes,
                email.trim().takeIf { it.isNotBlank() }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Editorial Top Bar matching mockup 08
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BukuKitaSpacing.xs, vertical = BukuKitaSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(BukuKitaSpacing.xxs))
                Text(
                    text = stringResource(R.string.edit_profile),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BukuKitaSpacing.lg, vertical = BukuKitaSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Editable Circular Avatar with floating sage camera edit badge
                Box(
                    modifier = Modifier.size(112.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(role = Role.Button) { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            avatarUri != null -> {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = stringResource(R.string.selected_avatar),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            avatarUrl != null -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(avatarUrl)
                                        .crossfade(true)
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .memoryCachePolicy(CachePolicy.DISABLED)
                                        .build(),
                                    contentDescription = stringResource(R.string.current_avatar),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Outlined.PersonOutline,
                                    contentDescription = stringResource(R.string.default_avatar),
                                    modifier = Modifier.size(52.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp)
                            .border(2.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .clip(CircleShape)
                            .clickable(role = Role.Button) { imagePicker.launch("image/*") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = stringResource(R.string.tap_to_change_photo),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BukuKitaSpacing.sm))

                Text(
                    text = stringResource(R.string.tap_to_change_photo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(BukuKitaSpacing.xl))

                // Paper Form Card matching mockup 08
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = if (isLightSurface) 1.dp else 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(BukuKitaSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.md)
                    ) {
                        BukuKitaTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                onClearError()
                            },
                            label = stringResource(R.string.username),
                            leadingIcon = Icons.Outlined.PersonOutline,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        BukuKitaTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                onClearError()
                            },
                            label = stringResource(R.string.full_name),
                            leadingIcon = Icons.Outlined.Badge,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        BukuKitaTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                onClearError()
                            },
                            label = stringResource(R.string.email),
                            leadingIcon = Icons.Outlined.Email,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (!isUpdating && username.isNotBlank() && fullName.isNotBlank() && email.isNotBlank()) {
                                        submitChanges()
                                    }
                                }
                            )
                        )

                        AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BukuKitaSpacing.xl))

                BukuKitaPrimaryButton(
                    text = stringResource(R.string.save_changes),
                    onClick = submitChanges,
                    enabled = username.isNotBlank() && fullName.isNotBlank() && email.isNotBlank(),
                    isLoading = isUpdating
                )
            }
        }
    }
}
