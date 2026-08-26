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
    val purchasedBundleIds by viewModel.purchasedBundleIds.collectAsState()

    val sortedBundles = remember(bundles) { bundles.sortedBy { it.id_bundle } }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ShopHeaderSection(tokens = tokens)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedBundles) { bundle ->
                BundleCard(
                    bundle = bundle,
                    isFirstPurchase = bundle.id_bundle !in purchasedBundleIds,
                    onClick = {
                        showBiometricPurchasePrompt(context, bundle) {
                            viewModel.acquistaBundle(currentUsername, bundle)
                        }
                    }
                )
            }
        }
    }
}