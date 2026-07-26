package com.maino.panda.play.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maino.panda.play.R
import kotlinx.coroutines.launch

data class SplashPageData(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector?,
    val badgeText: String,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateNext: () -> Unit
) {
    val splashPages = listOf(
        SplashPageData(
            id = 1,
            title = "Selamat Datang di Panda Play",
            subtitle = "PORTAL MOD & LIVERY BUSSID",
            description = "Pusat koleksi MOD dan Livery Bus Simulator Indonesia terlengkap, terupdate, dan 100% aman digunakan.",
            icon = null, // Will use app logo image
            badgeText = "BUSSID V4.2 READY",
            gradientColors = listOf(Color(0xFF4F46E5), Color(0xFF6366F1))
        ),
        SplashPageData(
            id = 2,
            title = "Ribuan MOD Kendaraan & Peta",
            subtitle = "JB5, TRUK OLENG & KELOK 44",
            description = "Nikmati keseruan mengemudi dengan MOD Bus Jetbus 5 terbaru, Truck Canter, Mobil Mewah, hingga Map Jalur Ekstrem.",
            icon = Icons.Default.DirectionsBus,
            badgeText = "UPDATE KHUSUS",
            gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF9333EA))
        ),
        SplashPageData(
            id = 3,
            title = "Koleksi Livery Ultra HD",
            subtitle = "DESAIN JERNIH & DETAIL",
            description = "Download gratis ratusan skin bus Po. Haryanto, Sinar Jaya, Sugeng Rahayu, dan PO ternama Indonesia dengan kualitas jernih.",
            icon = Icons.Default.Palette,
            badgeText = "LIVERY HD 4K",
            gradientColors = listOf(Color(0xFF0284C7), Color(0xFF2563EB))
        ),
        SplashPageData(
            id = 4,
            title = "Unduh & Pasang Cepat",
            subtitle = "SATU KLIK LANGSUNG JALAN",
            description = "Server download lokal super cepat disertai petunjuk pemasangan otomatis ke folder BUSSID smartphone kamu.",
            icon = Icons.Default.Download,
            badgeText = "SERVER LOKAL CEPAT",
            gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981))
        )
    )

    val pagerState = rememberPagerState(pageCount = { splashPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Indicator Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Langkah ${pagerState.currentPage + 1} dari ${splashPages.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    )
                }

                // Skip Button
                if (pagerState.currentPage < splashPages.size - 1) {
                    TextButton(onClick = onNavigateNext) {
                        Text(
                            text = "LEWATI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }

            // Horizontal Pager for 4 Splash Screens
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = splashPages[pageIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Illustration Card with Gradient Header
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = page.gradientColors
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Badge
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = page.badgeText,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Main Graphic / Icon
                                if (page.icon != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                            .clip(CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = page.icon,
                                            contentDescription = page.title,
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_panda_logo),
                                        contentDescription = "Panda Play Logo",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape)
                                            .border(3.dp, Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Text Content
                    Text(
                        text = page.subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = page.gradientColors.first(),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = page.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Bottom Navigation Area: Dots & Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(splashPages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) splashPages[pagerState.currentPage].gradientColors.first()
                                    else Color(0xFFE2E8F0)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Button
                val isLastPage = pagerState.currentPage == splashPages.size - 1

                Button(
                    onClick = {
                        if (isLastPage) {
                            onNavigateNext()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = splashPages[pagerState.currentPage].gradientColors.first()
                    ),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) "MULAI SEKARANG" else "LANJUTKAN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isLastPage) Icons.Default.RocketLaunch else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

