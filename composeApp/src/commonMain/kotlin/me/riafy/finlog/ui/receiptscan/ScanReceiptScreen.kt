package me.riafy.finlog.ui.receiptscan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.riafy.finlog.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReceiptScreen(
    viewModel: ScanReceiptViewModel,
    onReadyToReview: () -> Unit,
    onEnterManuallyClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState

    LaunchedEffect(state.readyToReview) {
        if (state.readyToReview) onReadyToReview()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Scan Receipt") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state.step) {
                ScanReceiptViewModel.Step.PROCESSING -> {
                    CircularProgressIndicator()
                    Text(
                        text = "Reading your receipt…",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = Spacing.md)
                    )
                }

                ScanReceiptViewModel.Step.READY -> {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = Spacing.md)
                    )
                    Text(
                        text = "Photograph or choose a receipt and Finlog will pull out the details for you to check.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = Spacing.lg)
                    )

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = Spacing.xs)
                        )
                        TextButton(onClick = onEnterManuallyClick) {
                            Text("Enter Manually Instead")
                        }
                    }

                    Button(
                        onClick = viewModel::onTakePhotoClick,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.md)
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = "  Take Photo")
                    }

                    OutlinedButton(
                        onClick = viewModel::onChooseFromGalleryClick,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = "  Choose from Gallery")
                    }
                }
            }
        }
    }
}
