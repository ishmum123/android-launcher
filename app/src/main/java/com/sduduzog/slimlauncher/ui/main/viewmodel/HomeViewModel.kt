package com.sduduzog.slimlauncher.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jkuester.unlauncher.datasource.DataRepository
import com.jkuester.unlauncher.datasource.getHomeApps
import com.jkuester.unlauncher.datasource.setApps
import com.jkuester.unlauncher.datasource.setDisplayInDrawer
import com.jkuester.unlauncher.datastore.proto.CorePreferences
import com.jkuester.unlauncher.datastore.proto.QuickButtonPreferences
import com.jkuester.unlauncher.datastore.proto.UnlauncherApp
import com.jkuester.unlauncher.datastore.proto.UnlauncherApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val corePreferencesRepo: DataRepository<CorePreferences>,
    private val unlauncherAppsRepo: DataRepository<UnlauncherApps>,
    private val quickButtonPreferencesRepo: DataRepository<QuickButtonPreferences>
) : ViewModel() {

    // Expose data as LiveData for the UI
    val corePreferences: LiveData<CorePreferences> = corePreferencesRepo.data.asLiveData()
    val unlauncherApps: LiveData<UnlauncherApps> = unlauncherAppsRepo.data.asLiveData()
    val quickButtonPreferences: LiveData<QuickButtonPreferences> = quickButtonPreferencesRepo.data.asLiveData()

    // Direct access for synchronous reads (use sparingly, prefer LiveData)
    fun getCorePreferencesSync(): CorePreferences = corePreferencesRepo.get()
    fun getUnlauncherAppsSync(): UnlauncherApps = unlauncherAppsRepo.get()
    fun getQuickButtonPreferencesSync(): QuickButtonPreferences = quickButtonPreferencesRepo.get()

    // Business logic methods
    fun getHomeApps(): List<UnlauncherApp> = unlauncherAppsRepo.get().getHomeApps()

    fun updateAppsList(apps: List<UnlauncherApp>) {
        viewModelScope.launch {
            unlauncherAppsRepo.update {
                it.toBuilder().setApps(apps).build()
            }
        }
    }

    fun setAppDisplayInDrawer(app: UnlauncherApp, display: Boolean) {
        viewModelScope.launch {
            unlauncherAppsRepo.update {
                it.toBuilder().setDisplayInDrawer(app.packageName, display).build()
            }
        }
    }

    fun renameApp(packageName: String, newDisplayName: String) {
        viewModelScope.launch {
            unlauncherAppsRepo.update { currentApps ->
                val updatedApps = currentApps.appsList.map { app ->
                    if (app.packageName == packageName) {
                        app.toBuilder().setDisplayName(newDisplayName).build()
                    } else {
                        app
                    }
                }
                currentApps.toBuilder().clearApps().addAllApps(updatedApps).build()
            }
        }
    }

    // Clock and date formatting methods
    fun getFormattedTime(format: String): String {
        return android.text.format.DateFormat.format(format, System.currentTimeMillis()).toString()
    }

    fun getFormattedDate(format: String): String {
        return android.text.format.DateFormat.format(format, System.currentTimeMillis()).toString()
    }
}
