package ru.sokolovromann.mynotepad.screens.addeditnote.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.mynotepad.R
import ru.sokolovromann.mynotepad.ui.components.DefaultSnackbar
import ru.sokolovromann.mynotepad.ui.theme.MyNotepadTheme

@Composable
fun AddEditNoteDisplay(
    title: String,
    text: String,
    onTitleChange: (newTitle: String) -> Unit,
    onTextChange: (newText: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    showEmptyNoteMessage: Boolean,
    onShowEmptyMessageChange: (isShow: Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Box {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { onTitleChange(it) },
                textStyle = MaterialTheme.typography.h6,
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        TitleLabel()
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            BasicTextField(
                value = text,
                onValueChange = { onTextChange(it) },
                textStyle = MaterialTheme.typography.body1,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        NoteLabel(showEmptyNoteMessage)
                    } else {
                        onShowEmptyMessageChange(false)
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }

        DefaultSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TitleLabel() {
    Text(
        text = stringResource(id = R.string.add_edit_note_title_label),
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun NoteLabel(
    showEmptyNoteMessage: Boolean
) {
    if (showEmptyNoteMessage) {
        Text(
            text = stringResource(id = R.string.add_edit_note_empty_note_label),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.error
        )
    } else {
        Text(
            text = stringResource(id = R.string.add_edit_note_text_label),
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditNoteDisplayPreview() {
    MyNotepadTheme {
        AddEditNoteDisplay(
            title = "Note Title",
            text = "Note Text ".repeat(50),
            onTitleChange = {},
            onTextChange = {},
            snackbarHostState = rememberScaffoldState().snackbarHostState,
            showEmptyNoteMessage = false,
            onShowEmptyMessageChange = {}
        )
    }
}