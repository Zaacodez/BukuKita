package com.example.bukukita.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bukukita.R
import com.example.bukukita.data.model.Book
import com.example.bukukita.ui.components.BookCoverArt
import com.example.bukukita.ui.components.BookDetailShimmer
import com.example.bukukita.ui.components.BukuKitaOutlinedButton
import com.example.bukukita.ui.components.EditorialErrorState
import com.example.bukukita.ui.components.StockStatusBadge
import com.example.bukukita.ui.theme.BukuKitaMotion
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.ui.theme.PlusJakartaSansFontFamily
import com.example.bukukita.utils.formatRupiah
import com.example.bukukita.viewmodel.BookDetailUiState
import com.example.bukukita.viewmodel.BookDetailViewModel

@Composable
fun BookDetailScreen(
    bookId: String,
    onNavigateBack: () -> Unit,
    onAskAssistant: ((String) -> Unit)? = null,
    viewModel: BookDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.fetchBookDetail(bookId)
    }

    BookDetailScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onRetry = { viewModel.fetchBookDetail(bookId) },
        onAskAssistant = onAskAssistant
    )
}

@Composable
internal fun BookDetailScreenContent(
    uiState: BookDetailUiState,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit,
    onAskAssistant: ((String) -> Unit)? = null
) {
    val currentBook = (uiState as? BookDetailUiState.Success)?.book

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Crossfade(
            targetState = uiState,
            animationSpec = tween(
                durationMillis = BukuKitaMotion.ContentCrossfadeMs,
                easing = BukuKitaMotion.DecelerateEasing
            ),
            label = "bookDetailCrossfade",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { state ->
            when (state) {
                is BookDetailUiState.Loading -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DetailTopBar(
                            onNavigateBack = onNavigateBack,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        BookDetailShimmer()
                    }
                }
                is BookDetailUiState.Error -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DetailTopBar(
                            onNavigateBack = onNavigateBack,
                            backgroundColor = MaterialTheme.colorScheme.background
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            EditorialErrorState(
                                message = state.message,
                                onRetry = onRetry
                            )
                        }
                    }
                }
                is BookDetailUiState.Success -> {
                    BookDetailContent(
                        book = state.book,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }

        // Quiet bottom assistant handoff bar (no invented commerce buttons)
        if (currentBook != null && onAskAssistant != null) {
            val prefillQuestion = stringResource(
                R.string.ask_assistant_prefill,
                currentBook.title,
                currentBook.author
            )
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(
                                horizontal = BukuKitaSpacing.lg,
                                vertical = BukuKitaSpacing.sm
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BukuKitaOutlinedButton(
                            text = stringResource(R.string.ask_assistant_about_book),
                            leadingIcon = Icons.Outlined.ChatBubbleOutline,
                            shape = CircleShape,
                            onClick = { onAskAssistant(prefillQuestion) },
                            modifier = Modifier
                                .widthIn(max = BukuKitaSpacing.maxReadableWidth)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    onNavigateBack: () -> Unit,
    backgroundColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = BukuKitaSpacing.xs, vertical = BukuKitaSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(R.string.book_detail_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        // Optical balance opposite back button
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    onNavigateBack: () -> Unit
) {
    val isLightSurface = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    val formattedPrice = remember(book.price) { formatRupiah(book.price) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warm Linen Hero Showcase bleeding cleanly behind status bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DetailTopBar(
                onNavigateBack = onNavigateBack,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))
            BookCoverArt(
                coverUrl = book.coverUrl,
                title = book.title,
                isLightSurface = isLightSurface,
                cornerRadiusDp = 12,
                elevationDp = 10,
                modifier = Modifier
                    .width(136.dp)
                    .aspectRatio(0.67f)
            )
            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))
        }

        // Editorial Metadata & Synopsis
        Column(
            modifier = Modifier
                .widthIn(max = BukuKitaSpacing.maxReadableWidth)
                .fillMaxWidth()
                .padding(horizontal = BukuKitaSpacing.lg, vertical = BukuKitaSpacing.lg)
        ) {
            if (!book.category.isNullOrBlank()) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = book.category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(BukuKitaSpacing.sm))
            }

            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xxs))

            Text(
                text = stringResource(R.string.by_author, book.author),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))

            // Split Price & Availability Summary Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(horizontal = BukuKitaSpacing.md, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(0.84f),
                        verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.xxs)
                    ) {
                        Text(
                            text = stringResource(R.string.price_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                            maxLines = 1
                        )
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 10.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1.16f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.availability_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        StockStatusBadge(stock = book.stock)
                    }
                }
            }

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xl))

            Text(
                text = stringResource(R.string.description_label),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

            Text(
                text = book.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.no_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))
        }
    }
}
