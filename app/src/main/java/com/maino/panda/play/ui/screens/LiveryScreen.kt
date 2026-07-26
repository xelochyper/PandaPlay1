package com.maino.panda.play.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maino.panda.play.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveryScreen(
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit
) {
    val liveriesList by viewModel.liveriesList.collectAsState()
    val selectedCategory by viewModel.selectedLiveryCategory.collectAsState()
    val isRefreshing by viewModel.isLiveriesRefreshing.collectAsState()

    val categories = listOf("Semua", "APK Free", "Premium", "Yudistira", "JB3", "XHD", "Vintage")

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshLiveries() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(bottom = 80.dp)
        ) {
            Text(
                text = "Katalog Livery BUSSID HD",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF4F46E5) else Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .then(
                                if (!isSelected) Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                                else Modifier
                            )
                            .clickable { viewModel.fetchLiveries(category) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2-Column Grid of Livery Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(liveriesList) { liveryItem ->
                    PlayStoreGameCard(
                        item = liveryItem,
                        onClick = { onItemClick(liveryItem.id) }
                    )
                }
            }
        }
    }
}

