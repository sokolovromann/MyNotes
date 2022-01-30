package ru.sokolovromann.mynotepad.screens.deleteaccount

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.mynotepad.data.exception.IncorrectDataException
import ru.sokolovromann.mynotepad.data.exception.NetworkException
import ru.sokolovromann.mynotepad.data.local.account.Account
import ru.sokolovromann.mynotepad.data.repository.AccountRepository
import ru.sokolovromann.mynotepad.data.repository.NoteRepository
import ru.sokolovromann.mynotepad.data.repository.SettingsRepository
import ru.sokolovromann.mynotepad.screens.ScreensEvent
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel(), ScreensEvent<DeleteAccountEvent> {

    private val _deleteAccountState: MutableState<DeleteAccountState> = mutableStateOf(DeleteAccountState())
    val deleteAccountState: State<DeleteAccountState> = _deleteAccountState

    private val _accountState: MutableState<Account> = mutableStateOf(Account.LocalAccount)
    val accountState: State<Account> = _accountState

    private val _deleteAccountUiEvent: MutableSharedFlow<DeleteAccountUiEvent> = MutableSharedFlow()
    val deleteAccountUiEvent: SharedFlow<DeleteAccountUiEvent> = _deleteAccountUiEvent

    init {
        getAccount()
    }

    override fun onEvent(event: DeleteAccountEvent) {
        when (event) {
            is DeleteAccountEvent.OnPasswordChange -> _deleteAccountState.value = _deleteAccountState.value.copy(
                password = event.newPassword
            )

            DeleteAccountEvent.DeleteClick -> if (isCorrectPassword()) {
                deleteAllUserData()
            }

            DeleteAccountEvent.CloseClick -> viewModelScope.launch {
                _deleteAccountUiEvent.emit(DeleteAccountUiEvent.OpenSettings)
            }
        }
    }

    private fun getAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.getAccount().collect { account ->
                withContext(Dispatchers.Main) {
                    _accountState.value = account
                }
            }
        }
    }

    private fun isCorrectPassword(): Boolean {
        val correctPassword = _deleteAccountState.value.password.isNotEmpty()
        _deleteAccountState.value = _deleteAccountState.value.copy(
            incorrectPassword = !correctPassword,
            deleting = false
        )

        return !_deleteAccountState.value.incorrectPassword
    }

    private fun deleteAllUserData() {
        _deleteAccountState.value = _deleteAccountState.value.copy(
            deleting = true
        )

        clearData {
            deleteAccount {
                viewModelScope.launch {
                    _deleteAccountUiEvent.emit(DeleteAccountUiEvent.OpenWelcome)
                }
            }
        }
    }

    private fun clearData(onSuccess: () -> Unit) {
        getTokenId { tokenId ->
            viewModelScope.launch(Dispatchers.IO) {
                noteRepository.clearNotes(_accountState.value.uid, tokenId)
                settingsRepository.clearSettings()
                onSuccess()
            }
        }
    }

    private fun deleteAccount(onSuccess: () -> Unit) {
        accountRepository.deleteAccount(
            currentPassword = _deleteAccountState.value.password
        ) { result ->
            _deleteAccountState.value = _deleteAccountState.value.copy(
                deleting = false
            )
            result
                .onSuccess { onSuccess() }
                .onFailure { exception ->
                    viewModelScope.launch {
                        when (exception) {
                            is NetworkException -> _deleteAccountUiEvent.emit(
                                DeleteAccountUiEvent.ShowNetworkErrorMessage
                            )
                            is IncorrectDataException -> _deleteAccountState.value = _deleteAccountState.value.copy(
                                incorrectPassword = true
                            )
                            else -> _deleteAccountUiEvent.emit(
                                DeleteAccountUiEvent.ShowUnknownErrorMessage
                            )
                        }
                    }
                }
        }
    }

    private fun getTokenId(onCompleted: (tokenId: String) -> Unit) {
        if (_accountState.value.isLocalAccount()) {
            onCompleted(NoteRepository.LOCAL_TOKEN_ID)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                accountRepository.getToken { tokenResult ->
                    val tokenId = tokenResult.getOrDefault(NoteRepository.NO_TOKEN_ID)
                    onCompleted(tokenId)
                }
            }
        }
    }
}