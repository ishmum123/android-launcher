package com.sduduzog.slimlauncher.ui.options.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jkuester.unlauncher.datasource.DataRepository
import com.jkuester.unlauncher.datastore.proto.CorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val corePreferencesRepo: DataRepository<CorePreferences>
) : ViewModel() {

    val corePreferences: LiveData<CorePreferences> =
        corePreferencesRepo.data.asLiveData()

    fun getCorePreferencesSync(): CorePreferences =
        corePreferencesRepo.get()

    fun updateCorePreferences(block: (CorePreferences) -> CorePreferences) {
        viewModelScope.launch {
            corePreferencesRepo.update(block)
        }
    }
}
