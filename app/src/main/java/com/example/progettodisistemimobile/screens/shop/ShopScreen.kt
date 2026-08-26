package com.example.progettodisistemimobile.screens.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun ShopScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentUsername by viewModel.currentUser.collectAsState()
    val tokens by viewModel.getTokens(currentUsername).collectAsState(initial = 0)
    val bundles by viewModel.tuttiIBundle.collectAsState(initial = emptyList())

    val sortedBundles = remember(bundles) { bundles.sortedBy { it.id_bundle } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- HEADER E TOKEN ---
        ShopHeaderSection(tokens = tokens)

        Spacer(modifier = Modifier.height(16.dp))

        // --- GRID BUNDLE ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sortedBundles) { bundle ->
                BundleCard(
                    bundle = bundle,
                    onClick = {
                        // Logica acquisto con biometrico (dal file BiometricPurchaseHelper.kt)
                        showBiometricPurchasePrompt(context, bundle) {
                            viewModel.acquistaBundle(currentUsername, bundle)
                        }
                    }
                )
            }
        }
    }
}
