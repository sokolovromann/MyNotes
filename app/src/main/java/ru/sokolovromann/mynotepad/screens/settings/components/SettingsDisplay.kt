package ru.sokolovromann.mynotepad.screens.settings.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.sokolovromann.mynotepad.BuildConfig
import ru.sokolovromann.mynotepad.data.local.settings.NotesSyncPeriod
import ru.sokolovromann.mynotepad.ui.theme.MyNotepadTheme

@ExperimentalFoundationApi
@Composable
fun SettingsDisplay(
    appNightTheme: Boolean,
    notesSaveAndClose: Boolean,
    appVersion: String,
    onAppNightThemeChange: (appNightTheme: Boolean) -> Unit,
    onNotesSaveAndClose: (notesSaveAndClose: Boolean) -> Unit,
    onGitHubClick: () -> Unit,
    localAccount: Boolean,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    accountName: String,
    onChangeEmailClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    showSyncPeriodDialog: Boolean,
    onShowSyncPeriodDialogChange: (show: Boolean) -> Unit,
    syncPeriodSelected: NotesSyncPeriod,
    onSyncPeriodSelectedChange: (notesSyncPeriod: NotesSyncPeriod) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.surface)
        .verticalScroll(scrollState)
    ) {
        SettingsGeneralContent(
            appNightTheme = appNightTheme,
            notesSaveAndClose = notesSaveAndClose,
            onAppNightThemeChange = onAppNightThemeChange,
            onNotesSaveAndClose = onNotesSaveAndClose
        )
        Divider()
        SettingsAccountContent(
            localAccount = localAccount,
            onSignUpClick = onSignUpClick,
            onSignInClick = onSignInClick,
            accountName = accountName,
            notesSyncPeriod = syncPeriodSelected,
            onSyncPeriodClick = { onShowSyncPeriodDialogChange(true) },
            onChangeEmailClick = onChangeEmailClick,
            onChangePasswordClick = onChangePasswordClick,
            onSignOutClick = onSignOutClick,
            onDeleteAccountClick = onDeleteAccountClick
        )
        Divider()
        SettingsAboutContent(
            appVersion = appVersion,
            onGitHubClick = onGitHubClick,
            onFeedbackClick = onFeedbackClick,
            onTermsClick = onTermsClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }

    SettingsSyncPeriodDialog(
        showDialog = showSyncPeriodDialog,
        onDismiss = { onShowSyncPeriodDialogChange(false) },
        selected = syncPeriodSelected,
        onSelectedChange = {
            onSyncPeriodSelectedChange(it)
            onShowSyncPeriodDialogChange(false)
        }
    )
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
private fun SettingsDisplayPreview() {
    MyNotepadTheme {
        SettingsDisplay(
            appNightTheme = true,
            notesSaveAndClose = false,
            appVersion = BuildConfig.VERSION_NAME,
            onAppNightThemeChange = {},
            onNotesSaveAndClose = {},
            onGitHubClick = {},
            localAccount = false,
            onSignUpClick = {},
            onSignInClick = {},
            accountName = "email@domain.com",
            onChangeEmailClick = {},
            onChangePasswordClick = {},
            onSignOutClick = {},
            onDeleteAccountClick = {},
            onFeedbackClick = {},
            onTermsClick = {},
            onPrivacyPolicyClick = {},
            showSyncPeriodDialog = false,
            onShowSyncPeriodDialogChange = {},
            syncPeriodSelected = NotesSyncPeriod.FIVE_HOURS,
            onSyncPeriodSelectedChange = {}
        )
    }
}