package com.example.progettodisistemimobile.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettodisistemimobile.data.Bundle

@Composable
fun BundleCard(
    bundle: Bundle,
    isFirstPurchase: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Token,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "${bundle.token}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "TOKEN", fontSize = 12.sp, fontWeight = FontWeight.Light)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${bundle.prezzo} €",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (isFirstPurchase) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).offset(x = (4).dp, y = (-4).dp),
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("+25%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(4.dp))
                    Text("primo acquisto", color = Color.White, fontSize = 9.sp)
                }
            }
        }
    }
}