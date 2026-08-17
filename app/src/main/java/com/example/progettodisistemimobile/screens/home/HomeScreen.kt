package com.example.progettodisistemimobile.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progettodisistemimobile.data.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel = viewModel()) {
    // 0 = Sanremo, 1 = Fantasanremo
    var selectedTab by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- TOP NAVIGATION BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sanremo",
                fontSize = 22.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { selectedTab = 0 }
            )

            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))

            Text(
                text = "Fantasanremo",
                fontSize = 22.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { selectedTab = 1 }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        // --- CONTENUTO DELLE PAGINE ---
        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 1) {
                FantasanremoView(viewModel)
            } else {
                SanremoView(viewModel)
            }
        }
    }
}
