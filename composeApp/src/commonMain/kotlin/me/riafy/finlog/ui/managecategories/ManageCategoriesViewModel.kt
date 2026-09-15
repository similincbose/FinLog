package me.riafy.finlog.ui.managecategories

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.repo.CategoryRepository

class ManageCategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    data class ManageCategoriesUiState(
        val isLoading: Boolean = true,
        val categories: List<Category> = emptyList()
    )

    var uiState = mutableStateOf(ManageCategoriesUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val categories = categoryRepository.getAll()
            uiState.value = uiState.value.copy(isLoading = false, categories = categories)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            categoryRepository.delete(id)
            load()
        }
    }
}
