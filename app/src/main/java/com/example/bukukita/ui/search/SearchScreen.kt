package com.example.bukukita.ui.search

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bukukita.R
import com.example.bukukita.ui.components.BookGridShimmer
import com.example.bukukita.ui.components.CategoryFilterRow
import com.example.bukukita.ui.components.EditorialBookCard
import com.example.bukukita.ui.components.EditorialEmptyState
import com.example.bukukita.ui.components.EditorialErrorState
import com.example.bukukita.ui.theme.BukuKitaMotion
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.viewmodel.BookUiState
import com.example.bukukita.viewmodel.BookViewModel

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: BookViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    SearchScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        categories = categories,
        selectedCategory = selectedCategory,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onClearSearch = viewModel::clearSearch,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::retry,
        onResetFilters = viewModel::resetFilters,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
internal fun SearchScreenContent(
    uiState: BookUiState,
    searchQuery: String,
    categories: List<String>,
    selectedCategory: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onResetFilters: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Editorial Top Bar
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
                text = stringResource(R.string.search_screen_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Focused Editorial Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BukuKitaSpacing.screenHorizontal),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_books),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            )
        )

        Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

        // Deduplicated Category Filter Pills
        CategoryFilterRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        // Results Section
        Crossfade(
            targetState = uiState,
            animationSpec = tween(
                durationMillis = BukuKitaMotion.ContentCrossfadeMs,
                easing = BukuKitaMotion.DecelerateEasing
            ),
            label = "searchContentCrossfade",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { state ->
            when (state) {
                is BookUiState.Loading -> {
                    BookGridShimmer(showSectionHeaders = false)
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
                    val books = state.books
                    if (books.isEmpty()) {
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
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Quiet editorial count + rule matching mockup 02
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = BukuKitaSpacing.screenHorizontal,
                                        vertical = BukuKitaSpacing.xs
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.results_count, books.size),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(BukuKitaSpacing.sm))
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 156.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = BukuKitaSpacing.screenHorizontal,
                                    end = BukuKitaSpacing.screenHorizontal,
                                    top = BukuKitaSpacing.xxs,
                                    bottom = BukuKitaSpacing.lg
                                ),
                                verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm)
                            ) {
                                items(
                                    items = books,
                                    key = { it.id }
                                ) { book ->
                                    EditorialBookCard(
                                        book = book,
                                        showCategory = true,
                                        onClick = { onNavigateToDetail(book.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
