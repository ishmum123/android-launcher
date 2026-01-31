package com.jkuester.unlauncher.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jkuester.unlauncher.datasource.DataRepository
import com.jkuester.unlauncher.datastore.proto.QuickButtonPreferences
import com.jkuester.unlauncher.datastore.proto.UnlauncherApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomizeHomeViewModel @Inject constructor(
    private val quickButtonPreferencesRepo: DataRepository<QuickButtonPreferences>,
    private val appsRepo: DataRepository<UnlauncherApps>
) : ViewModel() {

    val quickButtonPreferences: LiveData<QuickButtonPreferences> =
        quickButtonPreferencesRepo.data.asLiveData()

    val unlauncherApps: LiveData<UnlauncherApps> =
        appsRepo.data.asLiveData()

    fun getQuickButtonPreferencesSync(): QuickButtonPreferences =
        quickButtonPreferencesRepo.get()

    fun getUnlauncherAppsSync(): UnlauncherApps =
        appsRepo.get()

    fun updateQuickButtonPreferences(block: (QuickButtonPreferences) -> QuickButtonPreferences) {
        viewModelScope.launch {
            quickButtonPreferencesRepo.update(block)
        }
    }

    fun updateApps(block: (UnlauncherApps) -> UnlauncherApps) {
        viewModelScope.launch {
            appsRepo.update(block)
        }
    }
}
