# BukuKita — Modern Soft Editorial Bookstore Design System

> **Design System Source of Truth** for the BukuKita Android application (Jetpack Compose + Material Design 3).
> Derived using `ui-ux-pro-max` (Jetpack Compose stack + editorial typography + native mobile accessibility), `design-system` (three-layer token architecture), and `unslop` (quiet chrome, content-first craft).

---

## 1. Creative Direction & Brand Identity

### Five-Line Product Direction
- **Product:** BukuKita — a Bahasa Indonesia bookstore catalog, search, and AI book-assistant Android app for readers of Toko Buku Semut Alas (Jl. Raya Tlogomas No.18, Malang).
- **Character:** Warm, literary, calm, editorial, tactile.
- **World:** Unbleached ivory book paper, natural linen bookcloth, dried sage and eucalyptus leaf bookmarks, warm terracotta clay accents, and charcoal typography ink.
- **Moments:** Real catalog book covers from Supabase framed on warm paper surfaces with soft natural shadows (the book artwork carries visual richness while the UI chrome stays calm), terracotta Rupiah pricing, and restrained ink-and-wash botanical spot illustrations for empty and welcome states.
- **Signature:** Editorial serif display typography (`Lora`) paired with crisp humanist UI sans-serif (`Plus Jakarta Sans`), anchored by a flat open-book-and-eucalyptus-leaf emblem (`ic_bukukita_logo`) recurring quietly across the brand header, login header, and assistant avatar.

### Anti-Patterns Explicitly Eliminated
| Legacy Defect | Redesign Standard |
|---|---|
| Generic Indigo `#3F51B5` Material template colors | Warm Ivory + Soft Sage + Muted Eucalyptus + Terracotta editorial palette |
| Purple linear-gradient chat bubbles (`#8E24AA → #6A1B9A`) | Calm solid Soft Sage user bubbles & warm paper-white assistant bubbles with hairlines |
| Robot emoji (`🤖`) in Chatbot TopAppBar | Custom flat vector emblem (`ic_bukukita_logo`) inside a sage mist container |
| Blue 24dp placeholder square for brand logo | Proper `BukuKita` open-book-and-leaf vector mark + serif wordmark |
| `Icons.Default.Email` for Chatbot FAB & `Icons.Default.Menu` fake filter icon | Semantic chat icon for Chatbot FAB; clean search bar without dead decorative icons |
| Identical Light & Dark `ColorScheme` (broken dark mode) | Purpose-built **Evening Reading Lamp** dark palette with verified contrast parity |
| Uniform `16.dp` radius on every shape token | Hierarchical corner-radius scale with proper nested radius math |
| Blocking centered `CircularProgressIndicator` without context | Warm linen shimmer skeletons + editorial illustrated empty & recoverable error states |

---

## 2. Three-Layer Color Token Architecture

Components **never** reference raw hex values directly. Every screen consumes Layer 2 (`MaterialTheme.colorScheme` + `BukuKitaTheme.extendedColors`) or Layer 3 component aliases.

### Layer 1: Primitive Palette Tokens

| Token Group | Primitive Name | Hex | Role / Notes |
|---|---|---|---|
| **Warm Paper Neutrals (Light)** | `Ivory50` | `#FAF7F2` | Primary app background canvas |
| | `Paper0` | `#FFFFFF` | Elevated cards, inputs, bottom nav |
| | `Linen100` | `#F4EFE7` | Secondary surfaces, cover showcase backdrop, unselected chips |
| | `Parchment200` | `#EDE7DD` | Subtle containers, shimmer base, date pills |
| | `Oat300` | `#E4DDD1` | Pressed surface tint, shimmer highlight |
| | `Rule350` | `#DDD6CA` | Hairline borders & subtle dividers |
| | `Stone500` | `#8A8D87` | Input outlines, disabled icons |
| | `Slate700` | `#575C58` | Secondary readable body/meta text |
| | `Charcoal900` | `#222523` | Primary editorial ink |
| **Warm Charcoal Neutrals (Dark)** | `Night950` | `#161917` | Dark background (warm forest-charcoal, never `#000000`) |
| | `Night900` | `#1D211E` | Dark card / bottom nav surface |
| | `Night850` | `#232825` | Dark elevated container / input surface |
| | `Night800` | `#2A302C` | Dark tonal highlight / date pill |
| | `Night700` | `#353B37` | Dark hairline borders & dividers |
| | `Ash500` | `#848B85` | Dark input outlines |
| | `Mist300` | `#B5BBB5` | Dark secondary text |
| | `Cream100` | `#EFECE6` | Dark primary reading ink |
| **Soft Sage (Primary)** | `Sage900` | `#13261B` | Dark-mode `onPrimary` |
| | `Sage800` | `#1D3527` | Light-mode `onPrimaryContainer` |
| | `Sage700` | `#2C4436` | Dark-mode `primaryContainer` |
| | `Sage600` | `#496A57` | Light-mode `primary` (WCAG AA on white & ivory) |
| | `Sage400` | `#789482` | Decorative botanical fills/illustrations only (never small text) |
| | `Sage300` | `#9BC0A9` | Dark-mode `primary` (luminous desaturated sage) |
| | `Sage100` | `#DCE8E0` | Light-mode `primaryContainer` / active nav indicator |
| | `Sage50` | `#D9EBE0` | Dark-mode `onPrimaryContainer` |
| **Muted Eucalyptus (Secondary)** | `Eucalyptus800` | `#203429` | Light `onSecondaryContainer` |
| | `Eucalyptus700` | `#314037` | Dark `secondaryContainer` |
| | `Eucalyptus600` | `#526B5D` | Light `secondary` |
| | `Eucalyptus300` | `#AEC4B6` | Dark `secondary` |
| | `Eucalyptus100` | `#E5EDE7` | Light `secondaryContainer` |
| **Warm Terracotta (Accent)** | `Terracotta900` | `#3D1A0D` | Dark `onTertiary` |
| | `Terracotta800` | `#4A2010` | Light `onTertiaryContainer` |
| | `Terracotta700` | `#592E1E` | Dark `tertiaryContainer` |
| | `Terracotta600` | `#AD5838` | Light `tertiary` (Rupiah prices, warm accent) |
| | `Terracotta300` | `#E5A285` | Dark `tertiary` (warm peach-terracotta) |
| | `Terracotta100` | `#F7E3D9` | Light `tertiaryContainer` |
| | `Terracotta50` | `#FCE7DC` | Dark `onTertiaryContainer` |
| **Semantic Status** | `Brick600` | `#B63A32` | Light `error` / destructive logout |
| | `Brick100` | `#FBE4E2` | Light `errorContainer` |
| | `Brick900` | `#5C1612` | Light `onErrorContainer` |
| | `Coral300` | `#F28E85` | Dark `error` |
| | `Brick850` | `#691F1C` | Dark `errorContainer` |

### Layer 2: Semantic Theme Mapping (Material 3 `ColorScheme`)

| Material 3 Role | Light (`Paper & Sage`) | Dark (`Evening Reading Lamp`) | Usage |
|---|---|---|---|
| `primary` | `Sage600` `#496A57` | `Sage300` `#9BC0A9` | Primary CTA buttons, active filter chips, user chat bubbles, active links |
| `onPrimary` | `Paper0` `#FFFFFF` | `Sage900` `#13261B` | Text & icons on primary surfaces |
| `primaryContainer` | `Sage100` `#DCE8E0` | `Sage700` `#2C4436` | Active nav pill, availability badge, brand icon badge |
| `onPrimaryContainer` | `Sage800` `#1D3527` | `Sage50` `#D9EBE0` | Text & icons inside primary containers |
| `secondary` | `Eucalyptus600` `#526B5D` | `Eucalyptus300` `#AEC4B6` | Secondary emphasis, supporting controls |
| `onSecondary` | `Paper0` `#FFFFFF` | `#182A20` | Content on secondary surfaces |
| `secondaryContainer` | `Eucalyptus100` `#E5EDE7` | `Eucalyptus700` `#314037` | Category pills on detail page, subtle metadata badges |
| `onSecondaryContainer` | `Eucalyptus800` `#203429` | `#DCE8E0` | Content inside secondary containers |
| `tertiary` | `Terracotta600` `#AD5838` | `Terracotta300` `#E5A285` | **Book prices (`Rp ...`)**, editorial accent dots, low-stock notices |
| `onTertiary` | `Paper0` `#FFFFFF` | `Terracotta900` `#3D1A0D` | Content on terracotta surfaces |
| `tertiaryContainer` | `Terracotta100` `#F7E3D9` | `Terracotta700` `#592E1E` | Out-of-stock / warm highlight chips |
| `onTertiaryContainer` | `Terracotta800` `#4A2010` | `Terracotta50` `#FCE7DC` | Content inside warm highlight containers |
| `background` | `Ivory50` `#FAF7F2` | `Night950` `#161917` | Screen canvas |
| `onBackground` | `Charcoal900` `#222523` | `Cream100` `#EFECE6` | Primary headings & titles |
| `surface` | `Paper0` `#FFFFFF` | `Night900` `#1D211E` | Cards, top bars, bottom bar, assistant bubbles |
| `onSurface` | `Charcoal900` `#222523` | `Cream100` `#EFECE6` | Body text, card titles |
| `surfaceVariant` | `Linen100` `#F4EFE7` | `Night850` `#232825` | Book detail hero stage, unselected chips, colophon card |
| `onSurfaceVariant` | `Slate700` `#575C58` | `Mist300` `#B5BBB5` | Authors, timestamps, input hints, inactive nav icons |
| `surfaceContainerLow` | `Linen100` `#F4EFE7` | `Night850` `#232825` | Form canvas insets, search bar container |
| `surfaceContainer` | `Parchment200` `#EDE7DD` | `Night800` `#2A302C` | Date chips, shimmer skeleton base |
| `outline` | `Stone500` `#8A8D87` | `Ash500` `#848B85` | Outlined input default borders, outlined buttons |
| `outlineVariant` | `Rule350` `#DDD6CA` | `Night700` `#353B37` | Card hairlines, list dividers, bottom nav top border |
| `error` | `Brick600` `#B63A32` | `Coral300` `#F28E85` | Form validation errors, destructive `Keluar` action |
| `errorContainer` | `Brick100` `#FBE4E2` | `Brick850` `#691F1C` | Destructive action highlight container |

### Verified WCAG AA / AAA Contrast Ratios

All 27 semantic foreground/background combinations were mathematically verified against WCAG 2.1 luminance thresholds (`>= 4.5:1` for normal text):

| Pair | Light Ratio | Dark Ratio | Status |
|---|---|---|---|
| `onBackground` / `background` | **14.48:1** (AAA) | **15.02:1** (AAA) | PASS |
| `onSurfaceVariant` / `background` | **6.39:1** (AA+) | **9.06:1** (AAA) | PASS |
| `onSurfaceVariant` / `surfaceVariant` | **5.96:1** (AA+) | **7.66:1** (AAA) | PASS |
| `onPrimary` / `primary` | **6.03:1** (AA+) | **7.95:1** (AAA) | PASS |
| `primary` / `background` | **5.64:1** (AA+) | **8.86:1** (AAA) | PASS |
| `onPrimaryContainer` / `primaryContainer` | **10.47:1** (AAA) | **8.51:1** (AAA) | PASS |
| `onSecondaryContainer` / `secondaryContainer` | **11.11:1** (AAA) | **8.18:1** (AAA) | PASS |
| `tertiary` (Price) / `surface` | **4.96:1** (AA) | **7.64:1** (AAA) | PASS |
| `tertiary` (Price) / `background` | **4.65:1** (AA) | **8.31:1** (AAA) | PASS |
| `onTertiaryContainer` / `tertiaryContainer` | **11.26:1** (AAA) | **9.60:1** (AAA) | PASS |
| `error` / `surface` | **5.77:1** (AA+) | **6.97:1** (AA+) | PASS |

### Layer 3: Component Color Aliases
- **Book Card**: container `surface`, border `1.dp outlineVariant`, cover placeholder `surfaceVariant`, price `tertiary`.
- **Category Chip**: selected `primary` bg / `onPrimary` text; unselected `surfaceVariant` bg / `onSurface` text / `1.dp outlineVariant` border.
- **Chat Bubble (User)**: container `primary`, text `onPrimary`, timestamp `onSurfaceVariant`.
- **Chat Bubble (Assistant)**: container `surface`, border `1.dp outlineVariant`, text `onSurface`, timestamp `onSurfaceVariant`.
- **Stock Badge**: `stock > 0` → `primaryContainer` / `onPrimaryContainer`; `stock == 0` → `tertiaryContainer` / `onTertiaryContainer`.
- **Bottom Navigation Bar**: container `surface`, top hairline `1.dp outlineVariant`, active indicator `primaryContainer`, active icon/label `primary`, inactive icon/label `onSurfaceVariant`.

---

## 3. Typography Scale

### Font Pairing Strategy (Offline-Safe Bundling)
- **Editorial Display / Heading Family:** `Lora` (Regular 400, Medium 500, SemiBold 600, Bold 700) — warm calligraphic curves ideal for a literary bookstore without feeling antique.
- **Functional UI / Body Family:** `Plus Jakarta Sans` (Regular 400, Medium 500, SemiBold 600, Bold 700) — crisp geometric-humanist sans-serif with stellar Indonesian readability and Android Dynamic Type scaling.
- **Bundling Decision:** Bundle static `.ttf` files in `app/src/main/res/font/` (`lora_*.ttf`, `plus_jakarta_sans_*.ttf`) rather than runtime Downloadable Fonts so typography renders identically offline, in Compose Previews, and on first launch without FOUT.
- **Typography Rules:**
  - Sentence case everywhere (no wide-tracked ALL CAPS section headers).
  - Never hardcode `.sp` in screen composables — always reference `MaterialTheme.typography.*`.
  - Apply `fontFeatureSettings = "tnum"` (tabular numerals) on all Rupiah prices, stock counts, and chat timestamps.
  - No italic for UI emphasis; reserve weights `400–600` for UI and `600–700` only for display/serif titles and prices.

| Material 3 Role | Family | Weight | Size | Line Height | Tracking | Primary Usage |
|---|---|---|---|---|---|---|
| `displaySmall` | `Lora` | SemiBold (600) | 28.sp | 34.sp | -0.25.sp | Login/Register hero greeting (`Selamat Datang Kembali`) |
| `headlineMedium` | `Lora` | Bold (700) | 26.sp | 32.sp | -0.2.sp | Book Detail title, Profile page title |
| `headlineSmall` | `Lora` | SemiBold (600) | 22.sp | 28.sp | 0.sp | `BukuKita` top wordmark, Edit Profile / Search / Chatbot bar titles, Profile full name |
| `titleLarge` | `Lora` | SemiBold (600) | 20.sp | 26.sp | 0.sp | Home sections (`Rekomendasi Buku`, `Semua Buku`), Detail `Deskripsi` heading |
| `titleMedium` | `Plus Jakarta Sans` | SemiBold (600) | 16.sp | 22.sp | 0.sp | Primary buttons, Detail price (`headlineSmall` sans variant), Menu row labels |
| `titleSmall` | `Plus Jakarta Sans` | SemiBold (600) | 14.sp | 20.sp | 0.sp | BookCard title (max 2 lines), BookCard Rupiah price (`tnum`), Auth links |
| `bodyLarge` | `Plus Jakarta Sans` | Regular (400) | 15.sp | 24.sp | 0.1.sp | Book synopsis paragraph (1.6x line height), Chat bubble text, Input text |
| `bodyMedium` | `Plus Jakarta Sans` | Regular (400) | 14.sp | 20.sp | 0.15.sp | Subtitles, author on Detail screen, filter chips, error descriptions |
| `bodySmall` | `Plus Jakarta Sans` | Regular (400) | 12.sp | 16.sp | 0.2.sp | BookCard author name, result counts, store address colophon |
| `labelLarge` | `Plus Jakarta Sans` | SemiBold (600) | 14.sp | 20.sp | 0.1.sp | Button labels, category pills |
| `labelMedium` | `Plus Jakarta Sans` | Medium (500) | 12.sp | 16.sp | 0.2.sp | Bottom nav labels, status pills (`Tersedia • 14 stok`), input field labels |
| `labelSmall` | `Plus Jakarta Sans` | Medium (500) | 11.sp | 14.sp | 0.25.sp | Chat timestamps (`19:31`), small metadata captions |

---

## 4. Spacing, Layout Grid & Edge-to-Edge Strategy

All dimensions follow a strict **4.dp base grid** (`BukuKitaSpacing` object):

| Token | Value | Usage |
|---|---|---|
| `xxs` | `4.dp` | Tight icon-text micro gaps, stacked title-to-author gap |
| `xs` | `8.dp` | Gap between adjacent chips, chat message vertical spacing, icon-to-label gap |
| `sm` | `12.dp` | Grid gutter between book cards, card internal padding, form field vertical gap |
| `md` | `16.dp` | Standard phone horizontal page margin, section internal padding, card padding |
| `lg` | `20.dp` | Form panel inner padding, horizontal padding on Book Detail reading body |
| `xl` | `24.dp` | Vertical separation between major sections (`Rekomendasi Buku` → `Semua Buku`) |
| `xxl` | `32.dp` | Hero top breathing room on Auth & Profile screens |

### Adaptive & Safe-Area Layout Rules
- **Single Edge-to-Edge Inset Owner:** `MainActivity` keeps `enableEdgeToEdge()` and dynamically syncs status/navigation bar icon brightness (`SystemBarStyle.auto` / `WindowInsetsControllerCompat`) with Light & Dark theme. The outer `Scaffold` owns bottom navigation bar insets; child screens **do not** stack redundant `Scaffold` top status-bar padding on top of outer `innerPadding` (eliminating the current double-inset gap).
- **Keyboard & IME Handling:** Auth screens (`Login`, `Register`), `EditProfile`, and `Chatbot` apply `Modifier.imePadding()` and vertical scrollability so fields and CTA buttons remain visible on compact 360dp–375dp devices and in landscape orientation.
- **Responsive Grid & Measure:**
  - Compact phones (`< 600.dp`): `16.dp` horizontal margins, 2-column book grid (`GridCells.Fixed(2)`), `148.dp` width carousel cards.
  - Medium/Expanded tablets (`>= 600.dp`): `24.dp` horizontal margins, adaptive grid `GridCells.Adaptive(minSize = 160.dp)`, prose description & auth forms constrained to `widthIn(max = 520.dp)` centered for comfortable reading measure (55–70 chars/line).

---

## 5. Corner-Radius & Elevation System

### Corner-Radius Scale (`BukuKitaShapes`)
Nested radii obey `innerRadius = max(6.dp, outerRadius - padding)`:

| Token | Value | Usage |
|---|---|---|
| `extraSmall` | `6.dp` | Small subtle corner tails (chat bubble anchor corner = `4.dp`) |
| `small` | `10.dp` | Book cover thumbnails inside cards (`14.dp` card − `4..10.dp` inset → `10.dp`) |
| `medium` | `14.dp` | Book cards, search bar, primary CTA buttons, outlined inputs (`12.dp`), FAB (`16.dp`) |
| `large` | `20.dp` | Auth form containers, Profile identity card, modal bottom sheets (top corners) |
| `extraLarge` | `24.dp` | Book Detail hero linen backdrop bottom corners, chat composer pill |
| `full` | `CircleShape` (`50%`) | Category filter chips, status badges, avatars, chat send button |

### Elevation & Hairline Strategy
- **Light Mode:** Depth comes primarily from crisp `1.dp` `outlineVariant` (`#DDD6CA`) hairlines + ultra-subtle neutral ambient shadow (`elevation = 1.dp–2.dp` on cards, `4.dp` on FAB, `8.dp` soft drop shadow exclusively beneath the hero book cover on `BookDetailScreen`). Never tint shadows with primary colors!
- **Dark Mode:** Zero dark shadows on flat cards. Hierarchy comes from tonal surface elevation (`#161917` → `#1D211E` → `#232825` → `#2A302C`) paired with `1.dp` `#353B37` hairlines.

---

## 6. Core Component Specifications

### 6.1 Buttons (`BukuKitaButton`)
| Variant | Container | Content | Border | Height | Radius | Disabled |
|---|---|---|---|---|---|---|
| **Primary Filled** | `primary` | `onPrimary` | None | `52.dp` | `14.dp` | `surfaceVariant` / `onSurfaceVariant` @ 0.45 alpha |
| **Secondary Outlined** | `Transparent` (pressed: `primaryContainer` @ 0.35) | `primary` | `1.dp primary` (0.6 alpha) | `50.dp` | `14.dp` | `outlineVariant` border, 0.45 alpha |
| **Text Link** | `Transparent` | `primary` | None | min `48.dp` touch target | `10.dp` | 0.45 alpha |
| **Destructive Row** | `surface` (pressed: `errorContainer`) | `error` | `1.dp outlineVariant` | `56.dp` | `16.dp` | 0.45 alpha |

### 6.2 Editorial Book Card (`EditorialBookCard`)
- **Container:** `Surface` (`surface`), `14.dp` radius, `1.dp` `outlineVariant` hairline, `1.dp` subtle shadow (light only).
- **Cover Presentation:** Centered or full-bleed framed `2:3` (`aspectRatio(0.67f)`) cover with `10.dp` rounded corners on a `surfaceVariant` linen mat (`10.dp` top/side inset so every real book cover from Supabase looks curated regardless of edge color!), soft cover elevation (`3.dp`), `ContentScale.Crop`, Coil `crossfade(200)` with linen placeholder & book-icon fallback if cover URL fails/is null.
- **Metadata Stack (`12.dp` padding):**
  1. Book title: `titleSmall`, `onSurface`, `maxLines = 2`, `TextOverflow.Ellipsis`, fixed min-height for 2 lines in grids so rows align cleanly.
  2. Author: `bodySmall`, `onSurfaceVariant`, `maxLines = 1`, `TextOverflow.Ellipsis`.
  3. Price: `titleSmall` (`Bold`, `tnum`), `tertiary` (`#AD5838` light / `#E5A285` dark). Optional small category label on Search cards.
- **Strict Scope:** No star ratings, no cart buttons, no heart icons.

### 6.3 Inputs & Search Field (`BukuKitaTextField`, `BukuKitaSearchBar`)
- **Height:** `>= 52.dp` touch-friendly; `12.dp` corner radius (inputs) / `14.dp` (search bar).
- **States:** Default `surface` fill + `1.dp outlineVariant` border; Focused `1.5.dp primary` border + `primary` label/leading icon; Error `1.5.dp error` border + inline `supportingText` directly below field.
- **Semantics:** Visible label always (never placeholder-only for form fields), semantic `KeyboardType` (`Email`, `Password`, `Text`) and `ImeAction` (`Next`, `Done`, `Search`), password visibility toggle (`>= 48.dp` touch target) with Indonesian `contentDescription`.

### 6.4 Category Filter Chips (`EditorialCategoryChip`)
- Single horizontal `LazyRow` (`horizontalArrangement = spacedBy(8.dp)`, `contentPadding = PaddingValues(horizontal = 16.dp)`).
- Chip height visually `36.dp` wrapped in `minimumInteractiveComponentSize()` (`48.dp` Android touch target).
- Selected: `primary` container, `onPrimary` SemiBold label, smooth `animateColorAsState` (180ms).
- Unselected: `surfaceVariant` container, `onSurface` Regular label, `1.dp outlineVariant` hairline.

### 6.5 Bottom Navigation Bar (`BukuKitaBottomBar`)
- Shown on the 4 top-level tabs ONLY: `Beranda` (`home`), `Cari` (`search`), `Chatbot` (`chatbot`), `Profil` (`profile`). Hidden on `login`, `register`, `detail/{bookId}`, `edit_profile`.
- Surface `surface`, top `1.dp outlineVariant` divider (replacing heavy 16.dp drop shadow), `tonalElevation = 0.dp`.
- Active tab: Filled icon (`Icons.Rounded.*` or `Icons.Filled.*`) in `primary` inside `primaryContainer` pill indicator + SemiBold `primary` label.
- Inactive tabs: Outlined icon (`Icons.Outlined.*`) in `onSurfaceVariant` + Regular `onSurfaceVariant` label.

### 6.6 Chat Interface Components (`ChatBubble`, `ChatComposer`, `TypingIndicator`)
- **Horizontal Margin:** `16.dp` on `LazyColumn` (fixing the current 0dp edge bleed!).
- **Assistant Bubble:** Left-aligned, `widthIn(max = 296.dp)`, `surface` fill, `1.dp outlineVariant` border, `RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)`, `14.dp` padding, `bodyLarge` `onSurface` text, formatted `HH:mm` timestamp in `labelSmall` (`tnum`) underneath.
- **User Bubble:** Right-aligned, `widthIn(max = 280.dp)`, `primary` solid fill, `RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)`, `14.dp` padding, `bodyLarge` `onPrimary` text, formatted `HH:mm` timestamp underneath.
- **Typing Indicator:** Quiet assistant bubble holding 3 gentle staggered sage dots (subtle opacity wave respecting reduced-motion — replaces raw spinning progress circle).
- **Empty Welcome State:** Editorial spot illustration (`ill_chatbot_welcome`) + warm Toko Buku Semut Alas greeting + 2–3 tappable starter query chips (e.g., *"Buku fiksi yang tersedia?"*, *"Jam buka Toko Buku Semut Alas"*) that send directly via existing `viewModel.sendMessage(...)`!

---

## 7. Iconography Conventions

- **Family:** Material Icons (`androidx.compose.material.icons.outlined.*` for default/unselected chrome; `filled.*` exclusively for active bottom-nav icon state).
- **Brand Mark:** Custom flat `VectorDrawable` (`res/drawable/ic_bukukita_logo.xml`) recreated cleanly from `design/assets/asset-brand-emblem.png` + updated adaptive launcher icon (`ic_launcher_foreground.xml` & `ic_launcher_background.xml` in warm ivory `#FAF7F2` + sage `#496A57`).
- **Sizing Tokens:** `iconSm = 16.dp` (inline badges), `iconMd = 20.dp` (input leading/trailing icons), `iconLg = 24.dp` (navigation bar, top app bar, FAB).
- **Zero Emojis:** No `🤖`, `📚`, `✨`, or any Unicode emoji anywhere in UI chrome.
- **Accessibility Semantics:** Decorative icons adjacent to text pass `contentDescription = null`; standalone icon buttons always provide localized Indonesian `stringResource(...)` descriptions.

---

## 8. Motion & Micro-Interaction Conventions

Defined centrally in `ui/theme/Motion.kt` (`BukuKitaMotion`):

| Token | Duration | Easing | Usage |
|---|---|---|---|
| `TapFeedback` | `100ms` | `FastOutSlowInEasing` | Card press subtle scale (`0.98f`) & Material ripple state layer |
| `StateChange` | `200ms` | `FastOutSlowInEasing` | Filter chip color/border toggle, password icon crossfade, error reveal |
| `ScreenEnter` | `280ms` | `FastOutSlowInEasing` | NavHost forward slide+fade (`fadeIn` + subtle `slideInHorizontally(4%)`) |
| `ScreenExit` | `180ms` | `FastOutLinearInEasing` | Exit faster than enter (~65% ratio per Material motion) |
| `ContentCrossfade` | `240ms` | `LinearOutSlowInEasing` | Loading shimmer → catalog grid `Crossfade`, cover image Coil crossfade |

- **Restraint Rules:** Never animate layout bounds (`width`/`height`) in tight loops; no bouncy springs; no infinite pulsing/breathing glows; guard optional stagger/dot animations with Android `Settings.Global.ANIMATOR_DURATION_SCALE` (reduced motion).

---

## 9. Theme & Settings Strategy

- **Light & Dark Support:** `BukuKitaTheme` provides true `LightColorScheme` (`Paper & Sage`) and `DarkColorScheme` (`Evening Reading Lamp`), defaulting to system theme (`isSystemInDarkTheme()`).
- **Optional Theme Toggle via Existing `Pengaturan` Row:** `ProfileScreen` already renders a `Pengaturan` (`Settings`) menu item that currently does nothing. Wiring it to a clean `ModalBottomSheet` allowing users to pick **Ikuti Sistem / Mode Terang / Mode Gelap** (persisted in existing `bukukita_prefs` SharedPreferences) plus displaying official store details (**Toko Buku Semut Alas**, Jl. Raya Tlogomas No.18, Malang, 09:00–21:00) turns a dead control into real utility without inventing commerce features. *(Marked for user approval in `redesign-plan.md`.)*

---

## 10. Pre-Delivery Quality & Accessibility Checklist

Derived from `ui-ux-pro-max` (`pro-rules.md`) and `unslop`:
- [ ] Zero hardcoded hex colors or `.sp` sizes inside screen composables — all tokens flow from `BukuKitaTheme`.
- [ ] Every interactive element meets `>= 48.dp × 48.dp` Android touch target with `>= 8.dp` spacing.
- [ ] Zero emojis in structural UI; consistent Material Outlined/Filled icon pairing.
- [ ] All Light and Dark text/surface pairs verified `>= 4.5:1` contrast; dividers visible in both themes.
- [ ] Edge-to-edge safe areas (`statusBars`, `navigationBars`, `ime`) verified with zero double-padding or overlap.
- [ ] All `LazyRow` / `LazyVerticalGrid` / `LazyColumn` items provide stable `key = { ... }` for smooth recomposition.
- [ ] Screen reader `contentDescription` on all standalone icons/images, `null` on decorative icons beside text.
- [ ] Tested at 360dp compact width, landscape orientation, 1.3x–1.5x system font scale, and both Light & Dark themes.
- [ ] Real Supabase catalog data, covers, auth flows, Room chat persistence, and Gemini assistant remain 100% intact.
