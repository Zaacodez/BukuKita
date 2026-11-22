package com.example.bukukita.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bukukita.R
import com.example.bukukita.data.model.Book
import com.example.bukukita.ui.components.BookGridShimmer
import com.example.bukukita.ui.components.BukuKitaBrandMark
import com.example.bukukita.ui.components.BukuKitaSearchTrigger
import com.example.bukukita.ui.components.CategoryFilterRow
import com.example.bukukita.ui.components.EditorialBookCard
import com.example.bukukita.ui.components.EditorialEmptyState
import com.example.bukukita.ui.components.EditorialErrorState
import com.example.bukukita.ui.theme.BukuKitaMotion
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.viewmodel.BookUiState
import com.example.bukukita.viewmodel.BookViewModel

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    viewModel: BookViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        selectedCategory = selectedCategory,
        categories = categories,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::retry,
        onResetFilters = viewModel::resetFilters,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToChatbot = onNavigateToChatbot
    )
}

@Composable
internal fun HomeScreenContent(
    uiState: BookUiState,
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onResetFilters: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChatbot: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Editorial Brand Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = BukuKitaSpacing.screenHorizontal,
                        vertical = BukuKitaSpacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BukuKitaBrandMark(
                    contentDescription = stringResource(R.string.brand_logo_desc),
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Quiet Editorial Search Trigger
            BukuKitaSearchTrigger(
                placeholder = stringResource(R.string.search_books),
                onClick = onNavigateToSearch,
                modifier = Modifier.padding(horizontal = BukuKitaSpacing.screenHorizontal)
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

            // Category Filter Pills
            CategoryFilterRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xxs))

            // Catalog Content
            Crossfade(
                targetState = uiState,
                animationSpec = tween(
                    durationMillis = BukuKitaMotion.ContentCrossfadeMs,
                    easing = BukuKitaMotion.DecelerateEasing
                ),
                label = "homeContentCrossfade",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { state ->
                when (state) {
                    is BookUiState.Loading -> {
                        BookGridShimmer()
                    }
                    is BookUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EditorialErrorState(
                                message = state.message,
                                onRetry = onRetry
                            )
                        }
                    }
                    is BookUiState.Success -> {
                        if (state.books.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                EditorialEmptyState(
                                    illustrationRes = R.drawable.ill_empty_search,
                                    title = stringResource(R.string.books_not_found),
                                    subtitle = stringResource(R.string.books_not_found_hint),
                                    actionLabel = stringResource(R.string.reset_search),
                                    onAction = onResetFilters
                                )
                            }
                        } else {
                            HomeCatalogGrid(
                                books = state.books,
                                onBookClick = onNavigateToDetail
                            )
                        }
                    }
                }
            }
        }

        // Restrained Sage Assistant FAB
        FloatingActionButton(
            onClick = onNavigateToChatbot,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 1.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(BukuKitaSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Outlined.Forum,
                contentDescription = stringResource(R.string.chatbot_open),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun HomeCatalogGrid(
    books: List<Book>,
    onBookClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = BukuKitaSpacing.screenHorizontal,
            end = BukuKitaSpacing.screenHorizontal,
            top = BukuKitaSpacing.xs,
            bottom = 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm)
    ) {
        item(key = "recommended_section", span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.padding(bottom = BukuKitaSpacing.sm)) {
                Text(
                    text = stringResource(R.string.recommended_books),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = BukuKitaSpacing.sm)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm)
                ) {
                    items(
                        items = books.take(10),
                        key = { "rec_${it.id}" }
                    ) { book ->
                        EditorialBookCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            modifier = Modifier.width(156.dp)
                        )
                    }
                }
            }
        }

        item(key = "all_books_header", span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.all_books),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = BukuKitaSpacing.xs, bottom = BukuKitaSpacing.xxs)
            )
        }

        items(
            items = books,
            key = { it.id }
        ) { book ->
            EditorialBookCard(
                book = book,
                onClick = { onBookClick(book.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Preserve backwards-compatible symbol name in case any caller imports BookCard from ui.home
@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    EditorialBookCard(
        book = book,
        onClick = onClick,
        modifier = modifier
    )
}
