package me.riafy.finlog.ui.managepaymentmethods

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.data.repo.PaymentMethodRepository

/**
 * A payment method is just a name, so add/edit is a dialog on this same screen
 * rather than a separate route+screen+viewmodel the way Category's icon+colour
 * form needs.
 */
class ManagePaymentMethodsViewModel(
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {

    data class ManagePaymentMethodsUiState(
        val isLoading: Boolean = true,
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val isEditorOpen: Boolean = false,
        val editorId: Long? = null,
        val editorName: String = "",
        val editorNameError: String? = null
    )

    var uiState = mutableStateOf(ManagePaymentMethodsUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val methods = paymentMethodRepository.getAll()
            uiState.value = uiState.value.copy(isLoading = false, paymentMethods = methods)
        }
    }

    fun openAddEditor() {
        uiState.value = uiState.value.copy(
            isEditorOpen = true,
            editorId = null,
            editorName = "",
            editorNameError = null
        )
    }

    fun openEditEditor(method: PaymentMethod) {
        uiState.value = uiState.value.copy(
            isEditorOpen = true,
            editorId = method.id,
            editorName = method.name,
            editorNameError = null
        )
    }

    fun closeEditor() {
        uiState.value = uiState.value.copy(isEditorOpen = false)
    }

    fun onEditorNameChange(value: String) {
        uiState.value = uiState.value.copy(editorName = value, editorNameError = null)
    }

    fun saveEditor() {
        val state = uiState.value
        val name = state.editorName.trim()
        if (name.isEmpty()) {
            uiState.value = state.copy(editorNameError = "Enter a name")
            return
        }

        viewModelScope.launch {
            if (state.editorId != null) {
                paymentMethodRepository.update(state.editorId, name)
            } else {
                paymentMethodRepository.add(name)
            }
            uiState.value = uiState.value.copy(isEditorOpen = false)
            load()
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            paymentMethodRepository.delete(id)
            load()
        }
    }
}
