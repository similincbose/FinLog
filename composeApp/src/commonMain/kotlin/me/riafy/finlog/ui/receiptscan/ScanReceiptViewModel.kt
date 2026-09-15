package me.riafy.finlog.ui.receiptscan

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.utils.image.ImagePicker
import me.riafy.finlog.utils.receipt.PendingReceiptHolder
import me.riafy.finlog.utils.receipt.ReceiptParser
import me.riafy.finlog.utils.receipt.ReceiptTextRecognizer
import me.riafy.finlog.utils.receipt.ScannedReceipt

class ScanReceiptViewModel(
    private val imagePicker: ImagePicker,
    private val textRecognizer: ReceiptTextRecognizer,
    private val pendingReceiptHolder: PendingReceiptHolder,
    private val prefs: AppPreference
) : ViewModel() {

    enum class Step { READY, PROCESSING }

    data class ScanReceiptUiState(
        val step: Step = Step.READY,
        val errorMessage: String? = null,
        val readyToReview: Boolean = false
    )

    var uiState = mutableStateOf(ScanReceiptUiState())
        private set

    fun onTakePhotoClick() = runPickerThenProcess { imagePicker.captureFromCamera() }

    fun onChooseFromGalleryClick() = runPickerThenProcess { imagePicker.pickFromGallery() }

    fun dismissError() {
        uiState.value = uiState.value.copy(step = Step.READY, errorMessage = null)
    }

    private fun runPickerThenProcess(pick: suspend () -> String?) {
        viewModelScope.launch {
            val imagePath = pick() ?: return@launch
            uiState.value = uiState.value.copy(step = Step.PROCESSING, errorMessage = null)

            textRecognizer.recognize(imagePath)
                .onSuccess { recognized ->
                    val parsed = ReceiptParser.parse(recognized, prefs.currencyCode)
                    pendingReceiptHolder.set(ScannedReceipt(imagePath, parsed))
                    uiState.value = uiState.value.copy(step = Step.READY, readyToReview = true)
                }
                .onFailure {
                    uiState.value = uiState.value.copy(
                        step = Step.READY,
                        errorMessage = "Couldn't read this receipt. You can enter the details manually instead."
                    )
                }
        }
    }
}
