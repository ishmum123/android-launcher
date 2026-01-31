# Modernization Guide

This document outlines the modernization efforts applied to the Unlauncher project to align with current Android development best practices.

## Phase 1: Build Configuration (Completed)

### Version Catalog
- Created `gradle/libs.versions.toml` for centralized dependency management
- All dependencies now use version catalog references
- Easier to update dependencies across the project

### Gradle Build Scripts
- Migrated from deprecated `buildscript`/`allprojects` blocks to plugins DSL
- Added `dependencyResolutionManagement` in `settings.gradle.kts`
- Enabled modern Gradle features:
  - Configuration cache
  - Build cache
  - Parallel execution
  - Kotlin incremental compilation

### Dependency Updates
- Removed deprecated `lifecycle-extensions:2.2.0`
- Added individual lifecycle components:
  - `lifecycle-viewmodel-ktx`
  - `lifecycle-runtime-ktx`
  - `lifecycle-livedata-ktx`

## Phase 2: MVVM Architecture (Completed)

### ViewModel Infrastructure
Created ViewModels for all major UI components:

1. **HomeViewModel** (`ui/main/viewmodel/HomeViewModel.kt`)
   - Manages home screen data and business logic
   - Exposes LiveData for core preferences, apps, and quick buttons
   - Provides methods for app management and time formatting

2. **OptionsViewModel** (`ui/options/viewmodel/OptionsViewModel.kt`)
   - Manages options/settings screen
   - Handles core preferences updates

3. **CustomizeHomeViewModel** (`fragment/viewmodel/CustomizeHomeViewModel.kt`)
   - Manages home customization screen
   - Handles quick button and app preferences

4. **CustomizeAppDrawerViewModel** (`fragment/viewmodel/CustomizeAppDrawerViewModel.kt`)
   - Manages app drawer customization
   - Handles app visibility and search preferences

### Architecture Benefits
- **Separation of Concerns**: UI logic separated from business logic
- **Lifecycle Awareness**: ViewModels survive configuration changes
- **Testability**: Business logic can be tested without UI dependencies
- **Reactive UI**: LiveData observers update UI automatically

### Migration Path
Current implementation:
- ViewModels are injected alongside existing repository injection
- Marked with `// ViewModel - Modern MVVM pattern` comments
- Legacy code marked with `// TODO: Migrate fully to ViewModel pattern`

Recommended next steps:
1. Gradually refactor fragments to use ViewModel methods instead of direct repository access
2. Move all business logic from fragments to ViewModels
3. Replace `observe()` calls on repositories with LiveData observation from ViewModels
4. Remove direct repository injection from fragments once migration is complete

### SDK Updates
- **Minimum SDK**: Raised from 21 (Android 5.0) to 24 (Android 7.0)
  - Covers 95%+ of active devices
  - Removes need for many legacy compatibility workarounds
  - Android 7.0 introduced multi-window support, improved Doze mode, and better performance

- **Room**: Updated from 2.6.1 to 2.7.0-alpha12
  - Latest features and bug fixes
  - Improved multimap support
  - Better auto-migrations

## Phase 3: Modern UI with Jetpack Compose (Completed)

### Jetpack Compose Setup
- Added Jetpack Compose BOM (2025.01.00) for dependency management
- Enabled Compose build features in app module
- Integrated Compose with existing View-based UI (interop mode)

### Material 3 Design System
Created complete Material 3 theme infrastructure:

1. **Color System** (`ui/compose/theme/Color.kt`)
   - Light and dark color schemes
   - Material 3 color tokens
   - Semantic color naming

2. **Typography** (`ui/compose/theme/Type.kt`)
   - Material 3 type scale
   - Ubuntu font family integration
   - Responsive text sizing

3. **Theme** (`ui/compose/theme/Theme.kt`)
   - Dynamic color support (Android 12+)
   - Light/dark mode switching
   - System theme detection

### Example Compose Screen
Created `ComposeExampleScreen` demonstrating:
- Material 3 components (Card, Switch, TopAppBar)
- Scaffold layout pattern
- State management with remember
- Preview support for rapid development
- Dark mode support
- Modern UI patterns

### Migration Pattern
Compose can be integrated into existing Fragments using `ComposeView`:

```kotlin
override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    return ComposeView(requireContext()).apply {
        setContent {
            UnlauncherTheme {
                YourComposeScreen()
            }
        }
    }
}
```

### Benefits Achieved
- ✅ Modern declarative UI framework
- ✅ Material 3 design system
- ✅ Reduced boilerplate code
- ✅ Better preview support in IDE
- ✅ Reactive state management
- ✅ Interop with existing Views
- ✅ Dynamic color support (Android 12+)

### Next Steps for Full Compose Migration
1. Convert settings screens to Compose
2. Migrate dialog components
3. Replace RecyclerViews with LazyColumn
4. Convert home screen to Compose
5. Add Compose animations
6. Implement Compose navigation

## Benefits Summary

### Completed (Phases 1, 2 & 3)
- ✅ Faster build times with configuration cache and parallel execution
- ✅ Easier dependency management with version catalog
- ✅ Modern architecture foundation with ViewModels
- ✅ Support for latest Android features with SDK 24+
- ✅ Latest Room database features
- ✅ Jetpack Compose and Material 3 support
- ✅ Modern UI framework ready for gradual migration
- ✅ Better developer experience with Compose previews

### Future Enhancements
- 🚀 Complete migration to Compose UI
- 🚀 Baseline profiles for improved performance
- 🚀 Compose animations and transitions
- 🚀 Screenshot testing infrastructure
- 🚀 Compose navigation integration

## Development Guidelines

### Adding New Features
1. Create a ViewModel for business logic
2. Use LiveData/StateFlow for reactive data
3. Keep fragments/activities focused on UI rendering
4. Inject dependencies via Hilt

### Testing
- Unit test ViewModels without Android dependencies
- Use MockK for mocking in tests
- Leverage existing Kotest assertions

### Code Style
- Follow existing ktlint configuration
- Use Kotlin coroutines for async operations
- Prefer `viewModelScope` for ViewModel operations

## Migration Examples

### Before (Direct Repository Access)
```kotlin
@AndroidEntryPoint
class MyFragment : Fragment() {
    @Inject
    lateinit var repository: DataRepository<Data>

    fun loadData() {
        repository.observe { data ->
            // Update UI
        }
    }
}
```

### After (ViewModel Pattern)
```kotlin
@AndroidEntryPoint
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.data.observe(viewLifecycleOwner) { data ->
            // Update UI
        }
    }
}

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: DataRepository<Data>
) : ViewModel() {
    val data: LiveData<Data> = repository.data.asLiveData()
}
```

## Resources

- [Modern Android Architecture](https://developer.android.com/topic/architecture)
- [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
