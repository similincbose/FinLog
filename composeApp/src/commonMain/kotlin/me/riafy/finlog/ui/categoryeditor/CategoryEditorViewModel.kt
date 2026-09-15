package me.riafy.finlog.ui.categoryeditor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.models.CategoryColorPalette
import me.riafy.finlog.data.models.CategoryIconKey
import me.riafy.finlog.data.repo.CategoryRepository

/**
 * Backs both Add Category and Edit Category - mirrors AddExpenseViewModel's
 * nullable-id reuse: [categoryId] resolves to something to preload, or null
 * for a fresh category.
 */
class CategoryEditorViewModel(
    private val categoryId: Long?,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    data class CategoryEditorUiState(
        val isLoading: Boolean = true,
        val isEditMode: Boolean = false,
        val name: String = "",
        val iconKey: String = CategoryIconKey.OTHER,
        val colorHex: String = CategoryColorPalette.hexColors.first(),
        val isSaving: Boolean = false,
        val nameError: String? = null,
        val isSaved: Boolean = false
    )

    var uiState = mutableStateOf(CategoryEditorUiState(isEditMode = categoryId != null))
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val existing = categoryId?.let { categoryRepository.findById(it) }
            uiState.value = if (existing != null) {
                uiState.value.copy(
                    isLoading = false,
                    isEditMode = true,
                    name = existing.name,
                    iconKey = existing.iconKey,
                    colorHex = existing.colorHex
                )
            } else {
                uiState.value.copy(isLoading = false, isEditMode = false)
            }
        }
    }

    fun onNameChange(value: String) {
        uiState.value = uiState.value.copy(name = value, nameError = null)
    }

    fun onIconSelected(iconKey: String) {
        uiState.value = uiState.value.copy(iconKey = iconKey)
    }

    fun onColorSelected(colorHex: String) {
        uiState.value = uiState.value.copy(colorHex = colorHex)
    }

    fun save() {
        val state = uiState.value
        val name = state.name.trim()
        if (name.isEmpty()) {
            uiState.value = state.copy(nameError = "Enter a name")
            return
        }

        uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            if (categoryId != null) {
                categoryRepository.update(categoryId, name, state.iconKey, state.colorHex)
            } else {
                categoryRepository.add(name, state.iconKey, state.colorHex)
            }
            uiState.value = uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}
