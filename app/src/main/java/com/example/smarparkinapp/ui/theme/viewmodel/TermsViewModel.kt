package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.model.TermsContent
import com.example.smarparkinapp.ui.theme.data.repository.TermsRepository
import kotlinx.coroutines.launch

class TermsViewModel(private val repository: TermsRepository) : ViewModel() {

    var uiState by mutableStateOf<TermsUiState>(TermsUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var currentHtmlContent = ""

    fun loadTerms(code: Int) {
        viewModelScope.launch {
            uiState = TermsUiState.Loading
            try {
                val content = repository.getTermsContent(code)
                currentHtmlContent = content.htmlContent
                uiState = TermsUiState.Success(content)
            } catch (e: Exception) {
                uiState = TermsUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getHighlightedContent(): String {
        if (searchQuery.isBlank()) return currentHtmlContent

        return try {
            val pattern = Regex(Regex.escape(searchQuery), RegexOption.IGNORE_CASE)
            pattern.replace(currentHtmlContent) {
                "<mark style='background-color: yellow; color: black;'>${it.value}</mark>"
            }
        } catch (e: Exception) {
            currentHtmlContent
        }
    }
}

sealed class TermsUiState {
    object Loading : TermsUiState()
    data class Success(val content: TermsContent) : TermsUiState()
    data class Error(val message: String) : TermsUiState()
}
