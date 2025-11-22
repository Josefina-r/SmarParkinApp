package com.example.smarparkinapp.ui.theme.data.model

data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)