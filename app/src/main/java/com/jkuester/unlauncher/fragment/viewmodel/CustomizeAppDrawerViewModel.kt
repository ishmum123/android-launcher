package com.jkuester.unlauncher.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jkuester.unlauncher.datasource.DataRepository
import com.jkuester.unlauncher.datastore.proto.CorePreferences
import com.jkuester.unlauncher.datastore.proto.UnlauncherApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomizeAppDrawerViewModel @Inject constructor(
    private val corePreferencesRepo: DataRepository<CorePreferences>,
    private val appsRepo: DataRepository<UnlauncherApps>
) : ViewModel() {

    val corePreferences: LiveData<CorePreferences> =
        corePreferencesRepo.data.asLiveData()

    val unlauncherApps: LiveData<UnlauncherApps> =
        appsRepo.data.asLiveData()

    fun getCorePreferencesSync(): CorePreferences =
        corePreferencesRepo.get()

    fun getUnlauncherAppsSync(): UnlauncherApps =
        appsRepo.get()

    fun updateCorePreferences(block: (CorePreferences) -> CorePreferences) {
        viewModelScope.launch {
            corePreferencesRepo.update(block)
        }
    }

    fun updateApps(block: (UnlauncherApps) -> UnlauncherApps) {
        viewModelScope.launch {
            appsRepo.update(block)
        }
    }
}
