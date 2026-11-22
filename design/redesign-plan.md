# BukuKita — Complete Android UI Transformation Plan

> **For agentic workers:** REQUIRED SUB-SKILL after user approval: Use `superpowers:subagent-driven-development` or `superpowers:executing-plans` to implement this plan task-by-task. **DO NOT execute any code task until the user approves the visual design proposal.**

**Goal:** Transform BukuKita's 8 existing Jetpack Compose screens into a cohesive, commercial-grade **Modern Soft Editorial Bookstore** Android app while preserving 100% of existing Supabase, Room, Retrofit, and Gemini assistant functionality.

**Architecture:** Retain the existing single-module Jetpack Compose + Material Design 3 + MVVM architecture. Introduce a centralized three-layer design token system (`ui/theme/`), offline-bundled `Lora` + `Plus Jakarta Sans` typography, a focused reusable component library (`ui/components/`), single-owner edge-to-edge `Scaffold` inset handling in `MainActivity`, and screen-by-screen editorial upgrades with full Light & Dark mode parity.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (BOM `2024.09.00`), Material Design 3, Material Icons Extended, Navigation Compose `2.7.7`, Coil Compose `2.6.0`, Room `2.8.4`, Retrofit `2.11.0`, Supabase Kotlin SDK `2.6.0`, JUnit 4 + Compose UI Test (`androidx.compose.ui.test.junit4`).

**Spec:** [`design/design-system.md`](design-system.md) + visual reference mockups in [`design/mockups/`](mockups/) and custom spot illustrations in [`design/assets/`](assets/).

---

## Global Constraints

- **Framework Preservation:** Stay 100% Jetpack Compose + Material 3; no XML layout migration, no unnecessary third-party UI libraries added to `gradle/libs.versions.toml`.
- **Zero Backend Disruption:** Do not modify `AppDatabase.kt`, `ChatMessageDao.kt`, `ChatMessageEntity.kt`, `ApiClient.kt`, `ApiConfig.kt`, `SupabaseApiService.kt`, `GeminiApiService.kt`, `BookRepository.kt`, `AuthRepository.kt`, or `ChatRepository.kt`.
- **Strict Feature Scope:** Do not invent shopping cart, checkout, payment, wishlist, order history, or review features. Display only fields present in `Book` (`id`, `title`, `author`, `price`, `description`, `category`, `coverUrl`, `stock`, `createdAt`), `UserProfile` (`id`, `username`, `fullName`, `avatarUrl`), and `ChatMessage` (`text`, `isUser`, `timestamp`).
- **Real Catalog Data Only:** All runtime titles, metadata, and book covers must come live from Supabase `rest/v1/books` via Coil (`book.coverUrl`). Never substitute hardcoded or generated book covers in production code. *(Note: During audit, the offline sandbox could not resolve the Supabase host; mockup covers/titles are illustrative Indonesian bookstore references only.)*
- **Bahasa Indonesia Copy:** Preserve and refine all user-facing strings in `app/src/main/res/values/strings.xml` (no hardcoded screen strings).
- **Accessibility & Contrast:** Minimum `48.dp × 48.dp` Android touch targets (`>= 8.dp` gap), `>= 4.5:1` contrast in both Light and Dark themes, zero emojis as structural icons, localized `contentDescription` semantics, and reduced-motion respect.
- **Target Project Root:** Modify ONLY the active root project `D:\Kampus\Ongoing\BukuKita\app\`. Do NOT touch the stale nested backup directory `D:\Kampus\Ongoing\BukuKita\BukuKita\` or root scratch files (`TestAuth.kt`, `TestAuthUpdate.kt`) unless explicitly instructed.

---

## Review Focus

The five edge cases and failure modes most likely to bite readers on real devices (each pinned to a task test below):

1. **Double Edge-to-Edge Insets & Keyboard Occlusion:** Nested `Scaffold`s on Android 15/16 (`targetSdk = 36`, `enableEdgeToEdge()`) previously doubled top/bottom system bar padding, while `LoginScreen` lacked scroll and `imePadding()`, clipping the primary CTA on compact phones when the soft keyboard opened → pinned in **Task 2** & **Task 8**.
2. **Duplicate `"Semua"` Category Pill & Broken Filter Selection on `SearchScreen`:** `SearchScreen` currently prepends `"Semua"` onto `viewModel.categories` (which already begins with `"Semua"`) and checks `selectedCategory == ""`, breaking active-chip state and rendering duplicate pills → pinned in **Task 5**.
3. **Null / Broken Book Cover URLs & Missing Stock/Description Fields:** `Book.coverUrl`, `Book.category`, `Book.description`, and `Book.stock` are nullable; failed network images or `stock == 0` must render a dignified warm-linen placeholder / `"Stok habis"` warm-terracotta pill without layout shift (`CLS = 0`) → pinned in **Task 3** & **Task 6**.
4. **Empty / Long / Rapid Chat Messages & Edge Clipping on `ChatbotScreen`:** `LazyColumn` previously had `0.dp` horizontal padding (touching screen edges), lacked empty-state guidance, and risked double-sends while `isLoading == true` → pinned in **Task 7**.
5. **Broken Dark Mode & System Font Scaling (`1.3x–1.5x`):** `DarkColorScheme` previously duplicated light colors, making dark mode illegible on dark OS chrome, and fixed-height cards/chips can clip text under Large Display/Font settings → pinned in **Task 1** & **Task 10**.

---

## Project Audit & Complete Screen Inventory

### Active Codebase Summary
- **Active Root:** `D:\Kampus\Ongoing\BukuKita\app\` (last updated July–August 2026).
- **Workspace Anomaly Flagged:** `D:\Kampus\Ongoing\BukuKita\BukuKita\` contains an older snapshot (March–July 16, 2026); `TestAuth.kt`, `TestAuthUpdate.kt`, and `app/src/main/java/TestAuth.kt` are unused scratch files. Left untouched pending user direction.
- **Navigation Graph (`MainActivity.kt`):** Single `NavHost` with `startDestination = if (isLoggedIn) "home" else "login"`. Bottom bar visible strictly on `"home"`, `"search"`, `"chatbot"`, `"profile"`.

### Screen Inventory (8 Existing Screens + 1 Unwired Menu Entry)

| # | Screen & File | Route | ViewModel / Data | Audited Visual & UX Defects | Redesign Mockup |
|---|---|---|---|---|---|
| 1 | **Beranda** (`ui/home/HomeScreen.kt`) | `home` | `BookViewModel` → `BookRepository` (Supabase REST) | Blue 24dp box "logo"; fake `Icons.Default.Menu` inside search bar; confusing `Icons.Default.Email` FAB for Chatbot; indigo primary; no shimmer/empty/retry states; cards lack `key = { it.id }`. | `design/mockups/01-home-screen.jpg` |
| 2 | **Cari Buku** (`ui/search/SearchScreen.kt`) | `search` | `BookViewModel` → `BookRepository` (debounced 500ms) | Duplicate `"Semua"` chip bug (`listOf(all) + categories`); wrong active check (`selectedCategory == ""`); dead `Icons.Default.Tune` decorative icon; plain unstyled empty text; double `Scaffold` insets. | `design/mockups/02-search-screen.jpg` |
| 3 | **Detail Buku** (`ui/detail/BookDetailScreen.kt`) | `detail/{bookId}` | `BookDetailViewModel` → `BookRepository.getBookById` | Flat unframed cover; unstyled stacked metadata lines (`Kategori:`, `Stok:`); weak visual hierarchy; no stock status semantics; raw centered spinner/error without retry. | `design/mockups/03-book-detail-screen.jpg` |
| 4 | **Asisten BukuKita** (`ui/chatbot/ChatbotScreen.kt`) | `chatbot` | `ChatbotViewModel` → Room (`chat_messages`) + Supabase search + Gemini API (`Toko Buku Semut Alas`) | Purple linear-gradient bubbles (`#8E24AA→#6A1B9A`); `🤖` robot emoji avatar; **0dp horizontal padding** so bubbles touch screen edge; no timestamps rendered; no welcome/empty state; existing `clearHistory()` unexposed. | `design/mockups/04-chatbot-screen.jpg` |
| 5 | **Masuk** (`ui/auth/LoginScreen.kt`) | `login` | `AuthViewModel` → `AuthRepository` (Supabase Auth + Postgrest lookup) | No brand identity; not scrollable & no `imePadding()`; missing leading icon on email field; no keyboard types/IME actions; flat template look. | `design/mockups/05-login-screen.jpg` |
| 6 | **Buat Akun** (`ui/auth/RegisterScreen.kt`) | `register` | `AuthViewModel` → `AuthRepository.signUp` + Supabase `avatars` storage | Generic gray avatar circle; repetitive boilerplate `OutlinedTextFieldDefaults.colors` copy-pasted 5×; missing `imePadding()` & `KeyboardOptions`; no back arrow. | `design/mockups/06-register-screen.jpg` |
| 7 | **Profil** (`ui/profile/ProfileScreen.kt`) | `profile` | `AuthViewModel` → `AuthRepository.getUserProfile` | **0dp gap** between `@username` and options card; unused commerce imports (`ShoppingCart`, `FavoriteBorder`, `StarOutline`); `Pengaturan` row is a dead no-op; `Keluar` sits adjacent to normal menu rows instead of separated. | `design/mockups/07-profile-screen.jpg` |
| 8 | **Ubah Profil** (`ui/profile/EditProfileScreen.kt`) | `edit_profile` | `AuthViewModel` → `AuthRepository.updateUserProfile` | Plain gray avatar without edit affordance badge; fields lack leading icons and scrollability; `errorMessage` stale state not cleared on field edits. | `design/mockups/08-edit-profile-screen.jpg` |
| *8b* | **Pengaturan** *(Existing row on `ProfileScreen.kt:121`)* | Sheet on `profile` | Local `SharedPreferences` (`bukukita_prefs`) + Store info | Currently clickable with empty `onClick = {}`. Proposed: wire to a clean **Appearance Theme (`Sistem / Terang / Gelap`) + Store Info (`Toko Buku Semut Alas`)** modal bottom sheet. | Indicated on `07-profile-screen.jpg` |

---

## Architectural Approaches Considered

| Approach | Summary | Trade-offs | Decision |
|---|---|---|---|
| **A. Token-First M3 Theme + Editorial Component Library (Recommended)** | Build 3-layer tokens (`Color`, `Type`, `Shape`, `Spacing`, `Motion`), bundle `Lora` + `Plus Jakarta Sans` in `res/font/`, create focused `ui/components/`, fix inset ownership in `MainActivity`, and transform all 8 screens in-place while keeping ViewModels/Repositories untouched. | Zero backend risk, fast incremental verification, easy rollback per screen, idiomatic Compose Material 3. | **Selected** |
| **B. Full Architecture Rewrite (Hilt DI + MVI + Multi-Module)** | Introduce Hilt, split into `:core:designsystem` and `:feature:*`, rewrite all ViewModels and repositories. | High risk of breaking Supabase auth/Room KSP build; directly violates "do not modify authentication, database, or API logic unnecessarily." | Rejected |
| **C. Theme-Only Reskin (`Color.kt` + `Theme.kt` swap)** | Keep existing layouts and only swap hex colors. | Leaves broken double insets, purple chat gradients, duplicate `"Semua"` bug, emoji icons, dead `Pengaturan` row, and zero editorial hierarchy. | Rejected |

---

## Open Design Calls for User Confirmation Before Implementation

Before Task 1 starts, please confirm or adjust these 4 lightweight calls:

1. **`Pengaturan` Row on `ProfileScreen`:** Wire the existing unwired `Pengaturan` row to an in-app **Theme Picker (`Ikuti Sistem / Mode Terang / Mode Gelap`) + Store Info (`Toko Buku Semut Alas`)** bottom sheet? *(Recommended: Yes — eliminates a dead clickable button without inventing commerce features.)*
2. **`Tanya Asisten tentang Buku Ini` on `BookDetailScreen`:** Include the quiet outlined secondary button at the bottom of Book Detail that navigates to existing `ChatbotScreen` with an optional prefilled prompt about that book? *(Recommended: Yes — connects two existing core features cleanly.)*
3. **Expose Existing `ChatbotViewModel.clearHistory()`:** Wire a top-bar reset icon with a confirmation dialog (`Hapus riwayat percakapan?`) to the already-implemented `clearHistory()` function? *(Recommended: Yes.)*
4. **Update App Launcher Icon:** Replace the default green Android robot `ic_launcher_foreground/background` with the new `BukuKita` open-book-and-sage-leaf adaptive vector icon? *(Recommended: Yes.)*

---

## File Structure Map

### New Files to Create
- `app/src/main/res/font/` — Static `.ttf` files for `lora_regular`, `lora_medium`, `lora_semibold`, `lora_bold`, `plus_jakarta_sans_regular`, `plus_jakarta_sans_medium`, `plus_jakarta_sans_semibold`, `plus_jakarta_sans_bold`
- `app/src/main/res/drawable/ic_bukukita_logo.xml` — Flat editorial open-book-and-sage-leaf `VectorDrawable`
- `app/src/main/res/drawable/ill_empty_search.webp` (or `.jpg`) — Optimized spot illustration from `design/assets/empty-search-illustration.jpg`
- `app/src/main/res/drawable/ill_chatbot_welcome.webp` (or `.jpg`) — Optimized spot illustration from `design/assets/chatbot-welcome-illustration.jpg`
- `app/src/main/java/com/example/bukukita/ui/theme/Spacing.kt` — `BukuKitaSpacing` 4dp grid tokens
- `app/src/main/java/com/example/bukukita/ui/theme/Motion.kt` — `BukuKitaMotion` durations, easings & reduced-motion helper
- `app/src/main/java/com/example/bukukita/ui/theme/ThemePreference.kt` — `ThemeMode` enum (`SYSTEM`, `LIGHT`, `DARK`) persisted in `bukukita_prefs`
- `app/src/main/java/com/example/bukukita/ui/components/EditorialBookCard.kt` — Reusable Carousel & Grid book cards + shimmer skeleton
- `app/src/main/java/com/example/bukukita/ui/components/EditorialChips.kt` — `CategoryFilterRow` & `StatusBadge`
- `app/src/main/java/com/example/bukukita/ui/components/BukuKitaInputs.kt` — `BukuKitaTextField`, `BukuKitaPasswordField`, `BukuKitaSearchBar`, `BukuKitaPrimaryButton`
- `app/src/main/java/com/example/bukukita/ui/components/EditorialStates.kt` — `EditorialEmptyState`, `EditorialErrorState`, `BookGridShimmer`
- `app/src/main/java/com/example/bukukita/ui/components/BukuKitaBottomBar.kt` — Unified 4-tab bottom navigation bar
- `app/src/main/java/com/example/bukukita/ui/profile/SettingsSheet.kt` — Theme mode picker + Toko Buku Semut Alas info sheet *(if Decision 1 approved)*
- `app/src/test/java/com/example/bukukita/ui/DesignTokenContrastTest.kt` — JVM unit test verifying Light & Dark WCAG AA contrast ratios (`>= 4.5:1`) and `FormatUtils` / timestamp formatting
- `app/src/androidTest/java/com/example/bukukita/ui/BukuKitaEditorialUiTest.kt` — Compose UI instrumentation tests covering key screens, duplicate-chip fix, error states, and semantics

### Existing Files to Modify
- `app/src/main/java/com/example/bukukita/ui/theme/Color.kt`
- `app/src/main/java/com/example/bukukita/ui/theme/Type.kt`
- `app/src/main/java/com/example/bukukita/ui/theme/Theme.kt`
- `app/src/main/java/com/example/bukukita/MainActivity.kt`
- `app/src/main/java/com/example/bukukita/ui/home/HomeScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/search/SearchScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/detail/BookDetailScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/chatbot/ChatbotScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/auth/LoginScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/auth/RegisterScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/profile/ProfileScreen.kt`
- `app/src/main/java/com/example/bukukita/ui/profile/EditProfileScreen.kt`
- `app/src/main/java/com/example/bukukita/viewmodel/BookViewModel.kt` *(minor UI-helper fixes only: add `retry()` and remove spurious empty-error conflation while keeping all `BookRepository` calls identical)*
- `app/src/main/res/values/strings.xml`, `app/src/main/res/values/themes.xml`, `app/src/main/res/drawable/ic_launcher_*.xml`

---

## Implementation Plan (Task-by-Task)

### Task 1: Design Token Foundation, Typography & Verified Light/Dark Theme

**Files:**
- Create: `app/src/main/res/font/*.ttf`, `app/src/main/res/drawable/ic_bukukita_logo.xml`, `app/src/main/java/com/example/bukukita/ui/theme/Spacing.kt`, `app/src/main/java/com/example/bukukita/ui/theme/Motion.kt`, `app/src/main/java/com/example/bukukita/ui/theme/ThemePreference.kt`
- Modify: `app/src/main/java/com/example/bukukita/ui/theme/Color.kt`, `Type.kt`, `Theme.kt`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values/themes.xml`
- Test: `app/src/test/java/com/example/bukukita/ui/DesignTokenContrastTest.kt`

**Interfaces:**
- Produces: `BukuKitaLightColorScheme`, `BukuKitaDarkColorScheme`, `BukuKitaTypography`, `BukuKitaShapes`, `BukuKitaSpacing`, `BukuKitaMotion`, `ThemeMode`, `BukuKitaTheme(themeMode: ThemeMode, content: @Composable () -> Unit)`.

- [ ] **Step 1:** Write `DesignTokenContrastTest.kt` asserting WCAG 2.1 contrast ratio `>= 4.5` for all 11 semantic pairs in both `BukuKitaLightColorScheme` and `BukuKitaDarkColorScheme`, plus non-identical light/dark background colors.
- [ ] **Step 2:** Run `./gradlew :app:testDebugUnitTest --tests com.example.bukukita.ui.DesignTokenContrastTest` — verify it fails before tokens exist.
- [ ] **Step 3:** Bundle `Lora` and `Plus Jakarta Sans` font files into `res/font/`, create `ic_bukukita_logo.xml` VectorDrawable (and adaptive launcher icon if approved), implement `Color.kt`, `Type.kt`, `Spacing.kt`, `Motion.kt`, `ThemePreference.kt`, and update `Theme.kt` (syncing status bar light/dark icons via `WindowCompat.getInsetsController`).
- [ ] **Step 4:** Add missing localized Indonesian strings to `strings.xml` (e.g., `retry`, `available_stock`, `out_of_stock`, `ask_assistant_about_book`, `clear_chat_title`, `clear_chat_confirm`, `store_name`, `store_address_hours`, `theme_system`, `theme_light`, `theme_dark`).
- [ ] **Step 5:** Run `./gradlew :app:testDebugUnitTest` — verify `DesignTokenContrastTest` PASSES. Commit `"feat(theme): establish Modern Soft Editorial design tokens and typography"`.

---

### Task 2: App Shell, Edge-to-Edge Inset Architecture & Bottom Navigation Bar

**Files:**
- Create: `app/src/main/java/com/example/bukukita/ui/components/BukuKitaBottomBar.kt`
- Modify: `app/src/main/java/com/example/bukukita/MainActivity.kt`

**Interfaces:**
- Consumes: `BukuKitaTheme`, `ThemeMode`, `BukuKitaMotion` from Task 1.
- Produces: `BukuKitaBottomBar(currentDestination: NavDestination?, onNavigate: (String) -> Unit)`, `BukuKitaApp()` with single `Scaffold` inset ownership, smooth `NavHost` enter/exit transitions (`fadeIn(280ms)` / `fadeOut(180ms)`), and optional query parameter route `"chatbot?prefill={query}"`.

- [ ] **Step 1:** Implement `BukuKitaBottomBar` with 4 top-level tabs (`Beranda`, `Cari`, `Chatbot`, `Profil`), `1.dp` `outlineVariant` top hairline, zero heavy shadow, `Icons.Outlined.*` inactive vs `Icons.Filled.*` active inside `primaryContainer` pill indicator, and `navigationBarsPadding()`.
- [ ] **Step 2:** Update `MainActivity.kt` so `BukuKitaApp()` owns system bar insets cleanly (passing `contentWindowInsets` consistently without double-padding child screens), shares `ThemeMode` state via `ThemePreference`, and uses `BukuKitaMotion` transitions on `NavHost`.
- [ ] **Step 3:** Run `./gradlew :app:compileDebugKotlin` — verify clean compilation with zero warnings on `MainActivity.kt`. Commit `"feat(nav): unify edge-to-edge scaffold and editorial bottom bar"`.

---

### Task 3: Shared Editorial Component Library (Cards, Chips, Inputs, Shimmers & Empty/Error States)

**Files:**
- Create: `app/src/main/res/drawable/ill_empty_search.jpg`, `app/src/main/res/drawable/ill_chatbot_welcome.jpg`, `app/src/main/java/com/example/bukukita/ui/components/EditorialBookCard.kt`, `EditorialChips.kt`, `BukuKitaInputs.kt`, `EditorialStates.kt`
- Test: `app/src/androidTest/java/com/example/bukukita/ui/EditorialComponentsTest.kt`

**Interfaces:**
- Consumes: `BukuKitaSpacing`, `BukuKitaMotion`, `MaterialTheme` tokens from Task 1; `Book`, `formatRupiah`.
- Produces:
  - `EditorialBookCard(book: Book, onClick: () -> Unit, modifier: Modifier = Modifier, showCategory: Boolean = false)`
  - `CategoryFilterRow(categories: List<String>, selectedCategory: String, onCategorySelected: (String) -> Unit, modifier: Modifier = Modifier)`
  - `StockStatusBadge(stock: Int?, modifier: Modifier = Modifier)`
  - `BukuKitaTextField(...)`, `BukuKitaPasswordField(...)`, `BukuKitaPrimaryButton(...)`, `BukuKitaSearchTrigger(...)`
  - `BookGridShimmer(modifier: Modifier = Modifier)`, `EditorialEmptyState(...)`, `EditorialErrorState(message: String, onRetry: () -> Unit)`

- [ ] **Step 1:** Copy optimized spot illustrations from `design/assets/` into `res/drawable/` (`ill_empty_search`, `ill_chatbot_welcome`).
- [ ] **Step 2:** Implement `EditorialBookCard` with `2:3` cover ratio on warm linen mat, `10.dp` inner / `14.dp` outer nested radii, Coil crossfade + graceful fallback icon when `coverUrl` is null/fails, `tnum` terracotta price, press scale (`0.98f` / `100ms`), and `Modifier.semantics(mergeDescendants = true)`.
- [ ] **Step 3:** Implement `CategoryFilterRow` (deduplicating input categories defensively, `>= 48.dp` touch target), `StockStatusBadge` (`stock > 0` sage mist vs `stock == 0` terracotta tint), `BukuKitaInputs`, and `EditorialStates` (shimmer skeleton + illustrated empty + retryable error state).
- [ ] **Step 4:** Write Compose UI test verifying `EditorialBookCard` renders null `coverUrl` safely, `StockStatusBadge` shows `"Stok habis"` when `stock == 0`, and `CategoryFilterRow` enforces single active selection.
- [ ] **Step 5:** Run `./gradlew :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin` — verify clean compilation. Commit `"feat(ui): build shared editorial bookstore components"`.

---

### Task 4: Redesign Home Screen (`Beranda`)

**Files:**
- Modify: `app/src/main/java/com/example/bukukita/ui/home/HomeScreen.kt`, `app/src/main/java/com/example/bukukita/viewmodel/BookViewModel.kt`

**Interfaces:**
- Consumes: `EditorialBookCard`, `CategoryFilterRow`, `BukuKitaSearchTrigger`, `BookGridShimmer`, `EditorialEmptyState`, `EditorialErrorState` from Task 3; `ic_bukukita_logo`.
- Produces: Redesigned `HomeScreen(onNavigateToDetail: (String) -> Unit, onNavigateToSearch: () -> Unit, onNavigateToChatbot: () -> Unit, viewModel: BookViewModel)` matching `design/mockups/01-home-screen.jpg`.

- [ ] **Step 1:** In `BookViewModel.kt`, distinguish empty search/category results (`BookUiState.Success(emptyList())`) from actual network errors (`BookUiState.Error(...)`) and add `fun retry()` that re-triggers the active query/category fetch without changing any `BookRepository` call.
- [ ] **Step 2:** Rebuild `HomeScreen.kt`: editorial top bar (`ic_bukukita_logo` + `Lora` `"BukuKita"`), clean `BukuKitaSearchTrigger` (removing dead `Icons.Default.Menu`), `CategoryFilterRow`, `Crossfade` over Loading (`BookGridShimmer`) / Error (`EditorialErrorState` with `retry`) / Empty (`EditorialEmptyState`) / Success (`LazyVerticalGrid` with adaptive 2-col phone / adaptive tablet columns, stable `key = { it.id }`, `"Rekomendasi Buku"` horizontal carousel + `"Semua Buku"` grid), and refined sage Chatbot FAB using `Icons.Outlined.ChatBubbleOutline` (replacing `Icons.Default.Email`).
- [ ] **Step 3:** Add `@Preview` composables for Light, Dark, and 1.3x font scale; compile with `./gradlew :app:compileDebugKotlin`. Commit `"feat(home): transform HomeScreen to Modern Soft Editorial layout"`.

---

### Task 5: Redesign Search & Filter Screen (`Cari Buku`)

**Files:**
- Modify: `app/src/main/java/com/example/bukukita/ui/search/SearchScreen.kt`
- Test: `app/src/androidTest/java/com/example/bukukita/ui/SearchScreenUiTest.kt`

**Interfaces:**
- Consumes: `EditorialBookCard`, `CategoryFilterRow`, `BookGridShimmer`, `EditorialEmptyState` (`ill_empty_search`), `EditorialErrorState` from Task 3.
- Produces: Redesigned `SearchScreen(onNavigateBack: () -> Unit, onNavigateToDetail: (String) -> Unit, viewModel: BookViewModel)` matching `design/mockups/02-search-screen.jpg`.

- [ ] **Step 1:** Write `SearchScreenUiTest` asserting `"Semua"` appears **exactly once** in the category chip row and that empty results display `R.string.books_not_found` with a `"Reset Pencarian"` recovery action.
- [ ] **Step 2:** Rebuild `SearchScreen.kt`: remove nested `Scaffold` double-inset, use editorial top row (`ArrowBack` + `Lora` `"Cari Buku"`), focused sage-bordered search field with clear `×` button (no fake `Icons.Default.Tune` icon), bind `CategoryFilterRow` directly to `categories` (eliminating duplicate `"Semua"` and fixing `selectedCategory` match), show quiet result count (`Menampilkan X hasil`), and render 2-column `EditorialBookCard(..., showCategory = true)` grid with `key = { it.id }` and `ill_empty_search` empty state.
- [ ] **Step 3:** Run `./gradlew :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin`. Commit `"feat(search): redesign SearchScreen and fix duplicate category chip bug"`.

---

### Task 6: Redesign Book Detail Screen (`Detail Buku`)

**Files:**
- Modify: `app/src/main/java/com/example/bukukita/ui/detail/BookDetailScreen.kt`

**Interfaces:**
- Consumes: `StockStatusBadge`, `EditorialErrorState`, `BukuKitaSpacing`, `formatRupiah`.
- Produces: Redesigned `BookDetailScreen(bookId: String, onNavigateBack: () -> Unit, onAskAssistant: ((String) -> Unit)? = null, viewModel: BookDetailViewModel)` matching `design/mockups/03-book-detail-screen.jpg`.

- [ ] **Step 1:** Rebuild `BookDetailScreen.kt` with warm linen hero showcase backdrop (`RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)`), centered `2:3` book cover with soft diffused shadow, category badge pill, `Lora` Bold book title, muted author subtitle, split white summary card (`Harga` in `tnum` terracotta | vertical hairline | `Ketersediaan` with `StockStatusBadge`), comfortable `15.sp / 24.sp` `Deskripsi` section (constrained to `widthIn(max = 520.dp)` on tablets), and optional bottom outlined `"Tanya Asisten tentang Buku Ini"` CTA bar (strictly zero buy/cart/rating elements).
- [ ] **Step 2:** Add skeleton loading state and retryable `EditorialErrorState` (`onRetry = { viewModel.fetchBookDetail(bookId) }`).
- [ ] **Step 3:** Compile & check previews via `./gradlew :app:compileDebugKotlin`. Commit `"feat(detail): redesign BookDetailScreen with editorial hero showcase"`.

---

### Task 7: Redesign Chatbot Screen (`Asisten BukuKita`)

**Files:**
- Modify: `app/src/main/java/com/example/bukukita/ui/chatbot/ChatbotScreen.kt`

**Interfaces:**
- Consumes: `ic_bukukita_logo`, `ill_chatbot_welcome`, `BukuKitaSpacing`, `BukuKitaMotion`, `ChatbotViewModel` (`messages`, `isLoading`, `sendMessage`, `clearHistory`).
- Produces: Redesigned `ChatbotScreen(onNavigateBack: () -> Unit, prefillQuery: String? = null, viewModel: ChatbotViewModel)` matching `design/mockups/04-chatbot-screen.jpg`.

- [ ] **Step 1:** Remove purple gradient imports (`ChatbotBubblePurpleStart/End`) and `🤖` emoji. Build calm top app bar with `ic_bukukita_logo` in a `36.dp` sage mist circle, `Lora` `"Asisten BukuKita"` title + `"Toko Buku Semut Alas • Katalog Langsung"` subtitle, and optional clear-history icon opening a confirmation `AlertDialog` wired to `viewModel.clearHistory()`.
- [ ] **Step 2:** Fix `LazyColumn` padding (`PaddingValues(horizontal = 16.dp, vertical = 16.dp)`), add stable `key = { "${it.timestamp}_${it.isUser}_${it.text.hashCode()}" }`, group messages with clean date pills (`Hari ini` / formatted date), render calm solid sage user bubbles and warm white hairlined assistant bubbles with `HH:mm` `tnum` timestamps, replace spinner with 3-dot typing bubble, and add illustrated `ill_chatbot_welcome` empty state with tappable Bahasa Indonesia starter prompts.
- [ ] **Step 3:** Style bottom composer bar (`RoundedCornerShape(24.dp)` pill + `48.dp` circular sage send button, disabled cleanly while blank or `isLoading`, with `imePadding()`).
- [ ] **Step 4:** Compile via `./gradlew :app:compileDebugKotlin`. Commit `"feat(chatbot): transform ChatbotScreen into editorial assistant UI"`.

---

### Task 8: Redesign Auth Screens (`Masuk` & `Buat Akun`)

**Files:**
- Modify: `app/src/main/java/com/example/bukukita/ui/auth/LoginScreen.kt`, `app/src/main/java/com/example/bukukita/ui/auth/RegisterScreen.kt`

**Interfaces:**
- Consumes: `ic_bukukita_logo`, `BukuKitaTextField`, `BukuKitaPasswordField`, `BukuKitaPrimaryButton` from Task 3; `AuthViewModel`.
- Produces: Redesigned `LoginScreen` (`design/mockups/05-login-screen.jpg`) and `RegisterScreen` (`design/mockups/06-register-screen.jpg`).

- [ ] **Step 1:** Redesign `LoginScreen.kt` with scrollable layout + `imePadding()`, centered brand emblem badge + `"BukuKita"` wordmark + `Lora` `"Selamat Datang Kembali"`, elevated white `20.dp` form card (`widthIn(max = 480.dp)`), `Email atau Username` with leading person icon (`ImeAction.Next`), `Kata Sandi` with lock + eye toggle (`ImeAction.Done`), inline error surface, sage `Masuk` CTA, and quiet `Belum punya akun? Daftar` footer.
- [ ] **Step 2:** Redesign `RegisterScreen.kt` with subtle back arrow, `Lora` `"Buat Akun"`, dashed sage circular avatar picker (`88.dp`) with camera icon & `+` badge, unified `20.dp` paper card housing all 5 existing fields with semantic `KeyboardOptions` (`Email`, `Password`, `Next`/`Done`), `imePadding()`, sage `Daftar` CTA, and `Sudah punya akun? Masuk` footer. Preserve exact avatar `ByteArray` upload flow via `viewModel.register(...)`.
- [ ] **Step 3:** Compile via `./gradlew :app:compileDebugKotlin`. Commit `"feat(auth): transform LoginScreen and RegisterScreen with editorial styling"`.

---

### Task 9: Redesign Profile, Edit Profile & Settings Sheet (`Profil` & `Ubah Profil`)

**Files:**
- Create: `app/src/main/java/com/example/bukukita/ui/profile/SettingsSheet.kt` *(if Decision 1 approved)*
- Modify: `app/src/main/java/com/example/bukukita/ui/profile/ProfileScreen.kt`, `app/src/main/java/com/example/bukukita/ui/profile/EditProfileScreen.kt`

**Interfaces:**
- Consumes: `BukuKitaTextField`, `BukuKitaPrimaryButton`, `ThemeMode`, `ic_bukukita_logo`, `AuthViewModel`.
- Produces: Redesigned `ProfileScreen` (`design/mockups/07-profile-screen.jpg`), `EditProfileScreen` (`design/mockups/08-edit-profile-screen.jpg`), and `SettingsSheet`.

- [ ] **Step 1:** Redesign `ProfileScreen.kt`: `Lora` `"Profil"` header, warm white `20.dp` reader identity card (`88.dp` avatar with sage mist ring, `Lora` full name, `@username`, subtle `"Pembaca BukuKita"` pill), clean grouped menu card (`Ubah Profil`, `Pengaturan` with `"Tema tampilan & info toko"` subtitle), spatially separated blush-tinted `Keluar` destructive card (with confirmation dialog before `logout()`), and warm linen `"Toko Buku Semut Alas"` colophon card. Remove unused commerce icon imports.
- [ ] **Step 2:** Implement `SettingsSheet.kt` (`ModalBottomSheet`) with Theme selector (`Ikuti Sistem`, `Mode Terang`, `Mode Gelap`) and store location/hours info.
- [ ] **Step 3:** Redesign `EditProfileScreen.kt` with editorial top bar, `108.dp` circular avatar + sage camera edit badge overlay + `"Ketuk untuk mengganti foto"`, `20.dp` paper card with `Username`, `Nama Lengkap`, `Email` fields (all with leading icons, scrollable + `imePadding()`), inline error display, and `Simpan Perubahan` sage CTA preserving existing `viewModel.updateProfile(...)` flow.
- [ ] **Step 4:** Compile via `./gradlew :app:compileDebugKotlin`. Commit `"feat(profile): redesign ProfileScreen, EditProfileScreen, and settings sheet"`.

---

### Task 10: Final Verification, Accessibility Audit & Release Check

**Files:**
- Verify all modified files across `app/src/main/` and `app/src/test/`

- [ ] **Step 1:** Run full unit test suite & debug APK build:
  `./gradlew :app:testDebugUnitTest :app:assembleDebug`
  Expected: `BUILD SUCCESSFUL`, all unit tests PASS.
- [ ] **Step 2:** Run lint check:
  `./gradlew :app:lintDebug`
  Verify zero new accessibility (`ContentDescription`), hardcoded text, or Compose errors.
- [ ] **Step 3:** Verify `design/design-system.md` §10 Pre-Delivery Checklist across all 8 screens in both Light & Dark themes (and 1.3x font scale / compact width via Compose Previews or connected emulator if available).
- [ ] **Step 4:** Confirm zero unintended edits to backend/data/auth files (`BookRepository`, `AuthRepository`, `ChatRepository`, `AppDatabase`, `ApiConfig`, `SupabaseApiService`, `GeminiApiService`).
