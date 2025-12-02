package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smarparkinapp.ui.theme.components.TermsWebView
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.repository.TermsRepository
import com.example.smarparkinapp.ui.theme.viewmodel.TermsUiState
import com.example.smarparkinapp.ui.theme.viewmodel.TermsViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.TermsViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    navController: NavController,
    code: Int,
    onBackClick: () -> Unit
) {
    // Repositorio y ViewModel
    val apiService = remember { RetrofitInstance.apiService }
    val repository = remember { TermsRepository(apiService) }

    val viewModel: TermsViewModel = viewModel(
        factory = TermsViewModelFactory(repository)
    )

    // Correcto: NO usar collectAsState()
    val uiState = viewModel.uiState
    val searchQuery = viewModel.searchQuery

    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(code) {
        viewModel.loadTerms(code)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { query ->
                                viewModel.updateSearchQuery(query)
                            },
                            placeholder = { Text("Buscar en términos...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Términos - Código $code")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                viewModel.updateSearchQuery("")
                            } else {
                                onBackClick()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSearchActive)
                                Icons.Default.Close
                            else
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSearchActive) "Cerrar búsqueda" else "Volver"
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {

                is TermsUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando términos...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is TermsUiState.Success -> {
                    val highlighted = viewModel.getHighlightedContent()
                    TermsWebView(
                        htmlContent = highlighted,
                        searchQuery = searchQuery,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is TermsUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Error al cargar",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { viewModel.loadTerms(code) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}
