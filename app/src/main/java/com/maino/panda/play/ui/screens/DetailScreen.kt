package com.maino.panda.play.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maino.panda.play.R
import com.maino.panda.play.ui.MainViewModel

@Composable
fun DetailScreen(
    itemId: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    LaunchedEffect(itemId) {
        viewModel.fetchItemDetail(itemId)
    }

    val detail by viewModel.selectedDetail.collectAsState()
    val isDownloading by viewModel.apkInstaller.isDownloading.collectAsState()
    val downloadProgress by viewModel.apkInstaller.downloadProgress.collectAsState()
    val installStatus by viewModel.apkInstaller.installStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Detail ${detail?.type ?: "Item"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        detail?.let { item ->
            // Banner Image Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.img_panda_banner),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Title, Creator, Category
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Oleh: ${item.creator}",
                        fontSize = 13.sp,
                        color = Color(0xFF4F46E5),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEF2FF), shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            color = Color(0xFF4F46E5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Row: Rating, Downloads Count, Price
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Text(text = "${item.rating}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text(text = "Rating", color = Color(0xFF64748B), fontSize = 11.sp)
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                                Text(text = "${item.downloadsCount}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text(text = "Unduhan", color = Color(0xFF64748B), fontSize = 11.sp)
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (item.isPremium) (item.price ?: "PRO") else "GRATIS",
                                color = if (item.isPremium) Color(0xFFF59E0B) else Color(0xFF22C55E),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(text = "Akses", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action / Auto-Install APK Button
                val isPremiumNotBought = item.isPremium && !item.isPurchased

                Button(
                    onClick = {
                        if (isPremiumNotBought) {
                            // Prompt purchase dialog or simulator
                        } else {
                            val apkUrl = item.downloadLinks.firstOrNull()?.url ?: "https://maino.web.id/download/sample.apk"
                            viewModel.downloadAndInstall(apkUrl, "${item.id}_${item.title.take(10)}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPremiumNotBought) Color(0xFFF59E0B) else Color(0xFF4F46E5)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPremiumNotBought) Icons.Default.ShoppingBag else Icons.Default.InstallMobile,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                isDownloading -> "Mengunduh APK ($downloadProgress%)..."
                                isPremiumNotBought -> "Beli Sekarang (${item.price})"
                                else -> "INSTALL AUTOMATIS APK"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                // Progress Bar when downloading
                if (isDownloading || downloadProgress > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF4F46E5),
                        trackColor = Color(0xFFE2E8F0),
                    )
                }

                if (installStatus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = installStatus ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFF4F46E5),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Item Description
                Text(
                    text = "Deskripsi Lengkap",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description ?: "Tidak ada deskripsi tambahan.",
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Download Links List Section
                Text(
                    text = "Link Download Mirror & Server",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                item.downloadLinks.forEach { link ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.downloadAndInstall(link.url, "${item.id}_server")
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = link.serverName,
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Ukuran File: ${link.fileSize}",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "UNDUH",
                                color = Color(0xFF4F46E5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

