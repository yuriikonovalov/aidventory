package com.aidventory.feature.settings.presentation.data

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.ClearDataUseCase
import com.aidventory.core.domain.usecases.ImportBackupUseCase
import com.aidventory.core.domain.usecases.SaveBackupUseCase
import com.aidventory.core.domain.usecases.SendBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val sendBackupUseCase: SendBackupUseCase,
    private val saveBackupUseCase: SaveBackupUseCase,
    private val importBackup: ImportBackupUseCase,
    private val clearData: ClearDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DataUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableStateFlow<DataSideEffect?>(null)
    val sideEffect = _sideEffect.asStateFlow()

    private var job: Job? = null

    fun consumeSideEffect() {
        _sideEffect.value = null
    }

    fun importData(uri: Uri) {
        job = viewModelScope.launch {
            _uiState.value = DataUiState.ImportUiState.Working
            val result = importBackup(uri)
            _uiState.value = when (result) {
                Result.Loading -> DataUiState.ImportUiState.Working
                is Result.Error -> DataUiState.ImportUiState.Failure
                is Result.Success -> DataUiState.ImportUiState.Success
            }
        }
    }

    fun sendExportData() {
        job = viewModelScope.launch {
            _uiState.value = DataUiState.SendExportUiState.Working
            val result = sendBackupUseCase()
            when (result) {
                Result.Loading -> _uiState.value = DataUiState.SendExportUiState.Working
                is Result.Error -> _uiState.value = DataUiState.SendExportUiState.Failure
                is Result.Success -> {
                    _uiState.value = null
                    _sideEffect.value = DataSideEffect.SendDataIntent(result.data)
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
        _uiState.value = null
    }

    fun saveExportData(uri: Uri) {
        job = viewModelScope.launch {
            _uiState.value = DataUiState.SaveExportUiState.Working
            val result = saveBackupUseCase(uri)
            _uiState.value = when (result) {
                Result.Loading -> DataUiState.SaveExportUiState.Working
                is Result.Error -> DataUiState.SaveExportUiState.Failure
                is Result.Success -> DataUiState.SaveExportUiState.Success
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            clearData()
        }
    }
}

sealed interface DataUiState {
    sealed interface ImportUiState : DataUiState {
        object Working : ImportUiState
        object Failure : ImportUiState
        object Success : ImportUiState
    }

    sealed interface SaveExportUiState : DataUiState {
        object Working : SaveExportUiState
        object Failure : SaveExportUiState
        object Success : SaveExportUiState
    }

    sealed interface SendExportUiState : DataUiState {
        object Working : DataUiState
        object Failure : DataUiState
    }
}

sealed interface DataSideEffect {
    data class SendDataIntent(val uri: Uri) : DataSideEffect
}