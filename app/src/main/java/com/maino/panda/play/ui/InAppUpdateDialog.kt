package com.maino.panda.play.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun InAppUpdateDialog(
    updateState: UpdateState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val downloadProgress by viewModel.apkInstaller.downloadProgress.collectAsState()
    val isDownloading by viewModel.apkInstaller.isDownloading.collectAsState()
    val installStatus by viewModel.apkInstaller.installStatus.collectAsState()

    Dialog(
        onDismissRequest = {
            if (!updateState.isMandatory && !isDownloading) {
                viewModel.dismissUpdateDialog()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !updateState.isMandatory && !isDownloading,
            dismissOnClickOutside = !updateState.isMandatory && !isDownloading
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (updateState.isMandatory) Color(0xFFEF4444) else Color(0xFF4F46E5),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (updateState.isMandatory) Icons.Default.NewReleases else Icons.Default.SystemUpdate,
                        contentDescription = "Update Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (updateState.isMandatory) "Pembaruan Wajib!" else "Pembaruan Tersedia!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Version Tag
                Box(
                    modifier = Modifier
                        .background(
                            if (updateState.isMandatory) Color(0xFFFEE2E2) else Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Versi ${updateState.latestVersionName} Tersedia",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (updateState.isMandatory) Color(0xFFDC2626) else Color(0xFF4F46E5)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Release Notes Card
                if (updateState.releaseNotes.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Catatan Pembaruan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = updateState.releaseNotes,
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Mandatory Notice
                if (updateState.isMandatory) {
                    Text(
                        text = "Pembaruan ini diperlukan untuk dapat terus menggunakan aplikasi Panda Play.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Download Progress indicator
                if (isDownloading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)),
                            color = Color(0xFF4F46E5)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${installStatus ?: "Mengunduh..."} ($downloadProgress%)",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!updateState.isMandatory) {
                            OutlinedButton(
                                onClick = { viewModel.dismissUpdateDialog() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B))
                            ) {
                                Text(text = "Nanti Saja", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (updateState.updateUrl.isNotEmpty()) {
                                    viewModel.downloadAndInstall(
                                        apkUrl = updateState.updateUrl,
                                        fileName = "PandaPlay_v${updateState.latestVersionName}.apk"
                                    )
                                } else {
                                    Toast.makeText(context, "URL pembaruan tidak valid.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (updateState.isMandatory) Color(0xFFEF4444) else Color(0xFF4F46E5)
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = "Update",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Perbarui",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
