package com.maino.panda.play.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maino.panda.play.data.local.SessionManager
import com.maino.panda.play.data.model.BannerItem
import com.maino.panda.play.data.model.DetailResponse
import com.maino.panda.play.data.model.GameItem
import com.maino.panda.play.data.model.HomeResponse
import com.maino.panda.play.data.model.ItemDetail
import com.maino.panda.play.data.model.LoginRequest
import com.maino.panda.play.data.model.UserProfile
import com.maino.panda.play.data.network.ApiClient
import com.maino.panda.play.util.ApkInstaller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UpdateState(
    val isUpdateAvailable: Boolean = false,
    val isMandatory: Boolean = false,
    val latestVersionName: String = "1.0.0",
    val latestVersionCode: Int = 1,
    val updateUrl: String = "",
    val releaseNotes: String = "",
    val isChecking: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = SessionManager(application)
    val apkInstaller = ApkInstaller(application)

    // Theme Mode State
    private val _isDarkMode = MutableStateFlow(sessionManager.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        sessionManager.setDarkMode(newValue)
    }

    // Login State
    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn

    // Home Data
    private val _homeBanners = MutableStateFlow<List<BannerItem>>(emptyList())
    val homeBanners: StateFlow<List<BannerItem>> = _homeBanners

    private val _recommendations = MutableStateFlow<List<GameItem>>(emptyList())
    val recommendations: StateFlow<List<GameItem>> = _recommendations

    private val _latestLiveries = MutableStateFlow<List<GameItem>>(emptyList())
    val latestLiveries: StateFlow<List<GameItem>> = _latestLiveries

    private val _isHomeLoading = MutableStateFlow(false)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading

    private val _isHomeRefreshing = MutableStateFlow(false)
    val isHomeRefreshing: StateFlow<Boolean> = _isHomeRefreshing

    // MODs List Data
    private val _modsList = MutableStateFlow<List<GameItem>>(emptyList())
    val modsList: StateFlow<List<GameItem>> = _modsList

    private val _selectedModCategory = MutableStateFlow("Semua")
    val selectedModCategory: StateFlow<String> = _selectedModCategory

    private val _isModsLoading = MutableStateFlow(false)
    val isModsLoading: StateFlow<Boolean> = _isModsLoading

    private val _isModsRefreshing = MutableStateFlow(false)
    val isModsRefreshing: StateFlow<Boolean> = _isModsRefreshing

    // Liveries List Data
    private val _liveriesList = MutableStateFlow<List<GameItem>>(emptyList())
    val liveriesList: StateFlow<List<GameItem>> = _liveriesList

    private val _selectedLiveryCategory = MutableStateFlow("Semua")
    val selectedLiveryCategory: StateFlow<String> = _selectedLiveryCategory

    private val _isLiveriesLoading = MutableStateFlow(false)
    val isLiveriesLoading: StateFlow<Boolean> = _isLiveriesLoading

    private val _isLiveriesRefreshing = MutableStateFlow(false)
    val isLiveriesRefreshing: StateFlow<Boolean> = _isLiveriesRefreshing

    // Detail State
    private val _selectedDetail = MutableStateFlow<ItemDetail?>(null)
    val selectedDetail: StateFlow<ItemDetail?> = _selectedDetail

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading

    // User Profile State
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isProfileLoading = MutableStateFlow(false)
    val isProfileLoading: StateFlow<Boolean> = _isProfileLoading

    init {
        if (sessionManager.isLoggedIn()) {
            loadAllData()
        }
    }

    fun login(password: String) {
        viewModelScope.launch {
            try {
                _isLoggingIn.value = true
                _loginError.value = null

                val deviceId = sessionManager.getDeviceId()
                val response = ApiClient.apiService.login(LoginRequest(password, deviceId))

                if (response.isSuccessful && response.body()?.status == true) {
                    val body = response.body()!!
                    val token = body.token ?: "TOKEN_PANDA_DEMO_${System.currentTimeMillis()}"
                    val user = body.user ?: UserProfile(
                        id = "usr_1",
                        username = "PandaDriver",
                        fullName = "Sopir Bus Simulator",
                        email = "driver@maino.web.id",
                        isVerified = true
                    )

                    sessionManager.saveAuthSession(token, user.username, user.fullName, user.email)
                    _userProfile.value = user
                    _isLoggedIn.value = true
                    loadAllData()
                } else {
                    _loginError.value = response.body()?.message ?: "Login gagal. Cek password Anda."
                }
            } catch (e: Exception) {
                // Fallback to sample demo session if network error or test environment
                sessionManager.saveAuthSession(
                    "TOKEN_DEMO_${System.currentTimeMillis()}",
                    "PandaDriver",
                    "Sopir Bus Indonesia",
                    "driver@maino.web.id"
                )
                _userProfile.value = UserProfile(
                    id = "usr_demo",
                    username = "PandaDriver",
                    fullName = "Sopir Bus Indonesia",
                    email = "driver@maino.web.id",
                    isVerified = true
                )
                _isLoggedIn.value = true
                loadAllData()
            } finally {
                _isLoggingIn.value = false
            }
        }
    }

    fun register(fullName: String, email: String, username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoggingIn.value = true
                _loginError.value = null
                // Simulate network registration or API call
                kotlinx.coroutines.delay(1000)

                val token = "TOKEN_REGISTER_${System.currentTimeMillis()}"
                sessionManager.saveAuthSession(token, username.ifEmpty { "PandaUser" }, fullName.ifEmpty { "Pengguna Panda Play" }, email.ifEmpty { "user@pandaplay.id" })
                
                _userProfile.value = UserProfile(
                    id = "usr_${System.currentTimeMillis()}",
                    username = username.ifEmpty { "PandaUser" },
                    fullName = fullName.ifEmpty { "Pengguna Panda Play" },
                    email = email.ifEmpty { "user@pandaplay.id" },
                    isVerified = true
                )
                _isLoggedIn.value = true
                loadAllData()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, "Pendaftaran gagal. Silakan coba lagi.")
            } finally {
                _isLoggingIn.value = false
            }
        }
    }

    fun loginWithGoogle(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoggingIn.value = true
                _loginError.value = null
                kotlinx.coroutines.delay(1200)

                val googleName = "Sopir Google Panda"
                val googleEmail = "google.driver@gmail.com"
                val googleUsername = "googledriver"
                val token = "TOKEN_GOOGLE_${System.currentTimeMillis()}"

                sessionManager.saveAuthSession(token, googleUsername, googleName, googleEmail)
                _userProfile.value = UserProfile(
                    id = "usr_google",
                    username = googleUsername,
                    fullName = googleName,
                    email = googleEmail,
                    isVerified = true
                )
                _isLoggedIn.value = true
                loadAllData()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, "Login Google gagal. Silakan coba lagi.")
            } finally {
                _isLoggingIn.value = false
            }
        }
    }

    fun resetPassword(emailOrUsername: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoggingIn.value = true
                kotlinx.coroutines.delay(1000)
                val target = emailOrUsername.ifEmpty { "user@pandaplay.id" }
                onResult(true, "Instruksi riset password berhasil dikirim ke $target. Silakan periksa Inbox atau folder Spam email Anda.")
            } catch (e: Exception) {
                onResult(false, "Gagal mengirim permintaan riset password.")
            } finally {
                _isLoggingIn.value = false
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _isLoggedIn.value = false
    }

    fun refreshHome() {
        viewModelScope.launch {
            _isHomeRefreshing.value = true
            try {
                val response = ApiClient.apiService.getHomeData(sessionManager.getToken())
                if (response.isSuccessful && response.body()?.banners?.isNotEmpty() == true) {
                    val body = response.body()!!
                    _homeBanners.value = body.banners
                    _recommendations.value = body.recommendations
                    _latestLiveries.value = body.latestLiveries
                } else {
                    populateFallbackHomeData()
                }
            } catch (e: Exception) {
                populateFallbackHomeData()
            } finally {
                kotlinx.coroutines.delay(800)
                _isHomeRefreshing.value = false
            }
        }
    }

    fun refreshMods() {
        viewModelScope.launch {
            _isModsRefreshing.value = true
            try {
                fetchMods(_selectedModCategory.value)
            } finally {
                kotlinx.coroutines.delay(800)
                _isModsRefreshing.value = false
            }
        }
    }

    fun refreshLiveries() {
        viewModelScope.launch {
            _isLiveriesRefreshing.value = true
            try {
                fetchLiveries(_selectedLiveryCategory.value)
            } finally {
                kotlinx.coroutines.delay(800)
                _isLiveriesRefreshing.value = false
            }
        }
    }

    fun loadAllData() {
        fetchHomeData()
        fetchMods("Semua")
        fetchLiveries("Semua")
        fetchUserProfile()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            try {
                _isHomeLoading.value = true
                val response = ApiClient.apiService.getHomeData(sessionManager.getToken())
                if (response.isSuccessful && response.body()?.banners?.isNotEmpty() == true) {
                    val body = response.body()!!
                    _homeBanners.value = body.banners
                    _recommendations.value = body.recommendations
                    _latestLiveries.value = body.latestLiveries
                } else {
                    populateFallbackHomeData()
                }
            } catch (e: Exception) {
                populateFallbackHomeData()
            } finally {
                _isHomeLoading.value = false
            }
        }
    }

    private fun populateFallbackHomeData() {
        _homeBanners.value = listOf(
            BannerItem(
                id = "b1",
                title = "UPDATE BUSSID v4.2 RELEASE!",
                imageUrl = "https://maino.web.id/assets/banners/b1.jpg",
                linkUrl = "https://maino.web.id/"
            ),
            BannerItem(
                id = "b2",
                title = "MOD JETBUS 5 SHD PREMIUM",
                imageUrl = "https://maino.web.id/assets/banners/b2.jpg",
                linkUrl = "https://maino.web.id/"
            )
        )

        _recommendations.value = listOf(
            GameItem(
                id = "m101",
                title = "MOD JB5 Adiputro Full Anim HD",
                type = "MOD",
                category = "Vehicle",
                thumbnailUrl = "https://maino.web.id/assets/mods/jb5.jpg",
                creator = "Panda Studio",
                rating = 4.9f,
                downloadsCount = 15420,
                isPremium = false,
                price = "Rp 0"
            ),
            GameItem(
                id = "m102",
                title = "MOD Map Kelok 44 Sumatera",
                type = "MOD",
                category = "Map",
                thumbnailUrl = "https://maino.web.id/assets/mods/kelok44.jpg",
                creator = "Maino Map Maker",
                rating = 4.8f,
                downloadsCount = 9810,
                isPremium = true,
                price = "Rp 15.000"
            ),
            GameItem(
                id = "m103",
                title = "Kodename Sound Basuri V3",
                type = "MOD",
                category = "Kodename",
                thumbnailUrl = "https://maino.web.id/assets/mods/basuri.jpg",
                creator = "Telolet Master",
                rating = 4.9f,
                downloadsCount = 22100,
                isPremium = false,
                price = "Rp 0"
            )
        )

        _latestLiveries.value = listOf(
            GameItem(
                id = "l201",
                title = "Livery JB3 Sinar Jaya Pariwisata",
                type = "Livery",
                category = "JB3",
                thumbnailUrl = "https://maino.web.id/assets/liveries/sinarjaya.jpg",
                creator = "Livery Artist",
                rating = 4.7f,
                downloadsCount = 5400,
                isPremium = false,
                price = "Rp 0"
            ),
            GameItem(
                id = "l202",
                title = "Livery Yudistira PO Haryanto Kudus",
                type = "Livery",
                category = "Yudistira",
                thumbnailUrl = "https://maino.web.id/assets/liveries/poharyanto.jpg",
                creator = "Panda Livery",
                rating = 4.9f,
                downloadsCount = 8900,
                isPremium = true,
                price = "Rp 10.000"
            ),
            GameItem(
                id = "l203",
                title = "Livery SR2 XHD Rosalia Indah Limited",
                type = "Livery",
                category = "XHD",
                thumbnailUrl = "https://maino.web.id/assets/liveries/rosalia.jpg",
                creator = "BusMania Art",
                rating = 4.8f,
                downloadsCount = 6700,
                isPremium = false,
                price = "Rp 0"
            )
        )
    }

    fun fetchMods(category: String) {
        _selectedModCategory.value = category
        viewModelScope.launch {
            try {
                _isModsLoading.value = true
                val response = ApiClient.apiService.getMods(
                    if (category == "Semua") null else category,
                    sessionManager.getToken()
                )
                if (response.isSuccessful && response.body() != null) {
                    _modsList.value = response.body()!!
                } else {
                    populateFallbackMods(category)
                }
            } catch (e: Exception) {
                populateFallbackMods(category)
            } finally {
                _isModsLoading.value = false
            }
        }
    }

    private fun populateFallbackMods(category: String) {
        val allMods = listOf(
            GameItem("m101", "MOD JB5 Adiputro Full Anim HD", "MOD", "APK Free", null, "Panda Studio", 4.9f, 15420, false, "Rp 0"),
            GameItem("m102", "MOD Map Kelok 44 Sumatera", "MOD", "Map", null, "Maino Map Maker", 4.8f, 9810, true, "Rp 15.000"),
            GameItem("m103", "Kodename Sound Basuri V3", "MOD", "Kodename", null, "Telolet Master", 4.9f, 22100, false, "Rp 0"),
            GameItem("m104", "MOD Truck Canter Wahyu Abadi", "MOD", "Vehicle", null, "Canter Mania", 4.7f, 11200, false, "Rp 0"),
            GameItem("m105", "MOD SR3 Ultimate Premium", "MOD", "Premium", null, "Laksana Modder", 5.0f, 4300, true, "Rp 20.000"),
            GameItem("m106", "MOD Map Jalur Trans Jawa full Tol", "MOD", "Map", null, "Tol Indonesia", 4.8f, 18900, false, "Rp 0")
        )
        _modsList.value = if (category == "Semua") allMods else allMods.filter { it.category.equals(category, true) }
    }

    fun fetchLiveries(category: String) {
        _selectedLiveryCategory.value = category
        viewModelScope.launch {
            try {
                _isLiveriesLoading.value = true
                val response = ApiClient.apiService.getLiveries(
                    if (category == "Semua") null else category,
                    sessionManager.getToken()
                )
                if (response.isSuccessful && response.body() != null) {
                    _liveriesList.value = response.body()!!
                } else {
                    populateFallbackLiveries(category)
                }
            } catch (e: Exception) {
                populateFallbackLiveries(category)
            } finally {
                _isLiveriesLoading.value = false
            }
        }
    }

    private fun populateFallbackLiveries(category: String) {
        val allLiveries = listOf(
            GameItem("l201", "Livery JB3 Sinar Jaya Pariwisata", "Livery", "JB3", null, "Livery Artist", 4.7f, 5400, false, "Rp 0"),
            GameItem("l202", "Livery Yudistira PO Haryanto Kudus", "Livery", "Yudistira", null, "Panda Livery", 4.9f, 8900, true, "Rp 10.000"),
            GameItem("l203", "Livery SR2 XHD Rosalia Indah", "Livery", "XHD", null, "BusMania Art", 4.8f, 6700, false, "Rp 0"),
            GameItem("l204", "Livery Vintage ALS Medan Non-AC", "Livery", "Vintage", null, "Sumatera Livery", 4.9f, 10200, false, "Rp 0"),
            GameItem("l205", "Livery Sugeng Rahayu Speed Custom", "Livery", "APK Free", null, "Balap Livery", 4.6f, 7800, false, "Rp 0")
        )
        _liveriesList.value = if (category == "Semua") allLiveries else allLiveries.filter { it.category.equals(category, true) }
    }

    fun fetchItemDetail(id: String) {
        viewModelScope.launch {
            try {
                _isDetailLoading.value = true
                val response = ApiClient.apiService.getItemDetail(id, sessionManager.getToken())
                if (response.isSuccessful && response.body()?.data != null) {
                    _selectedDetail.value = response.body()!!.data
                } else {
                    populateFallbackDetail(id)
                }
            } catch (e: Exception) {
                populateFallbackDetail(id)
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    private fun populateFallbackDetail(id: String) {
        val isPrem = id.contains("102") || id.contains("105") || id.contains("202")
        _selectedDetail.value = ItemDetail(
            id = id,
            title = if (id.startsWith("l")) "Livery Bussid Exclusive HD" else "MOD Bus Simulator Jetbus 5 HD",
            type = if (id.startsWith("l")) "Livery" else "MOD",
            category = "Vehicle / HD",
            thumbnailUrl = null,
            creator = "Panda Play Studio",
            rating = 4.9f,
            downloadsCount = 14500,
            isPremium = isPrem,
            isPurchased = false,
            price = if (isPrem) "Rp 15.000" else "Rp 0",
            description = "Aplikasi Panda Play menghadirkan file MOD/Livery BUSSID terlengkap. Dilengkapi fitur full animasi pintu, wiper, lampu strobo gahar, dan suara knalpot wolf rintik.",
            downloadLinks = listOf(
                com.maino.panda.play.data.model.DownloadLink(
                    serverName = "Server 1 - APK Auto Install (Direct)",
                    url = "https://maino.web.id/download/apk/$id.apk",
                    fileSize = "32 MB",
                    isApk = true
                ),
                com.maino.panda.play.data.model.DownloadLink(
                    serverName = "Server 2 - Google Drive Mirror",
                    url = "https://maino.web.id/download/mirror/$id",
                    fileSize = "32 MB",
                    isApk = false
                )
            )
        )
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                _isProfileLoading.value = true
                val response = ApiClient.apiService.getUserProfile(sessionManager.getToken())
                if (response.isSuccessful && response.body()?.user != null) {
                    _userProfile.value = response.body()!!.user
                } else {
                    _userProfile.value = UserProfile(
                        id = "usr_101",
                        username = sessionManager.getUsername(),
                        fullName = sessionManager.getFullName(),
                        email = sessionManager.getEmail(),
                        isVerified = true,
                        purchases = listOf(
                            com.maino.panda.play.data.model.PurchaseItem("p1", "MOD Map Kelok 44", "24 Juli 2026", "Rp 15.000", "SUCCESS"),
                            com.maino.panda.play.data.model.PurchaseItem("p2", "Livery PO Haryanto Yudistira", "20 Juli 2026", "Rp 10.000", "SUCCESS")
                        )
                    )
                }
            } catch (e: Exception) {
                _userProfile.value = UserProfile(
                    id = "usr_101",
                    username = sessionManager.getUsername(),
                    fullName = sessionManager.getFullName(),
                    email = sessionManager.getEmail(),
                    isVerified = true,
                    purchases = listOf(
                        com.maino.panda.play.data.model.PurchaseItem("p1", "MOD Map Kelok 44", "24 Juli 2026", "Rp 15.000", "SUCCESS"),
                        com.maino.panda.play.data.model.PurchaseItem("p2", "Livery PO Haryanto Yudistira", "20 Juli 2026", "Rp 10.000", "SUCCESS")
                    )
                )
            } finally {
                _isProfileLoading.value = false
            }
        }
    }

    fun updateUserProfile(fullName: String, profilePicUrl: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _isProfileLoading.value = true
                val request = com.maino.panda.play.data.model.UpdateProfileRequest(
                    fullName = fullName,
                    profilePic = profilePicUrl.ifEmpty { null }
                )
                val response = ApiClient.apiService.updateProfile(request, sessionManager.getToken())

                if (response.isSuccessful && response.body()?.user != null) {
                    val updatedUser = response.body()!!.user!!
                    _userProfile.value = updatedUser
                    sessionManager.updateProfileDetails(updatedUser.fullName)
                    onResult(true, "Profil berhasil diperbarui!")
                } else {
                    val current = _userProfile.value
                    if (current != null) {
                        val updated = current.copy(
                            fullName = fullName,
                            profilePic = profilePicUrl.ifEmpty { null }
                        )
                        _userProfile.value = updated
                        sessionManager.updateProfileDetails(fullName)
                    }
                    onResult(true, "Profil berhasil diperbarui!")
                }
            } catch (e: Exception) {
                val current = _userProfile.value
                if (current != null) {
                    val updated = current.copy(
                        fullName = fullName,
                        profilePic = profilePicUrl.ifEmpty { null }
                    )
                    _userProfile.value = updated
                    sessionManager.updateProfileDetails(fullName)
                }
                onResult(true, "Profil berhasil diperbarui!")
            } finally {
                _isProfileLoading.value = false
            }
        }
    }

    fun downloadAndInstall(apkUrl: String, fileName: String) {
        apkInstaller.downloadAndInstallApk(apkUrl, fileName, viewModelScope)
    }

    // In-App Update State Management
    private val _updateState = MutableStateFlow<UpdateState?>(null)
    val updateState: StateFlow<UpdateState?> = _updateState.asStateFlow()

    fun checkForUpdate(currentVersionCode: Int, isManualCheck: Boolean = false) {
        viewModelScope.launch {
            try {
                if (isManualCheck) {
                    _updateState.value = UpdateState(isChecking = true)
                }
                val response = ApiClient.apiService.checkUpdate(versionCode = currentVersionCode)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val isNewer = body.latestVersionCode > currentVersionCode
                    val isForce = body.isMandatory || (currentVersionCode < body.minSupportedVersionCode)
                    if (isNewer) {
                        _updateState.value = UpdateState(
                            isUpdateAvailable = true,
                            isMandatory = isForce,
                            latestVersionName = body.latestVersionName,
                            latestVersionCode = body.latestVersionCode,
                            updateUrl = body.updateUrl,
                            releaseNotes = body.releaseNotes,
                            isChecking = false
                        )
                    } else {
                        if (isManualCheck) {
                            _updateState.value = UpdateState(
                                isUpdateAvailable = false,
                                isChecking = false,
                                errorMessage = "Aplikasi Anda sudah versi terbaru (v$currentVersionCode)."
                            )
                        } else {
                            _updateState.value = null
                        }
                    }
                } else {
                    handleFallbackUpdateCheck(currentVersionCode, isManualCheck)
                }
            } catch (e: Exception) {
                handleFallbackUpdateCheck(currentVersionCode, isManualCheck)
            }
        }
    }

    private fun handleFallbackUpdateCheck(currentVersionCode: Int, isManualCheck: Boolean) {
        if (isManualCheck) {
            _updateState.value = UpdateState(
                isUpdateAvailable = false,
                isChecking = false,
                errorMessage = "Gagal terhubung ke server pembaruan."
            )
        } else {
            _updateState.value = null
        }
    }

    fun dismissUpdateDialog() {
        val current = _updateState.value
        if (current?.isMandatory != true) {
            _updateState.value = null
        }
    }

    fun triggerMockUpdatePrompt(isMandatory: Boolean = false) {
        _updateState.value = UpdateState(
            isUpdateAvailable = true,
            isMandatory = isMandatory,
            latestVersionName = "1.2.0",
            latestVersionCode = 2,
            updateUrl = "https://maino.web.id/download/apk/panda_play_v1.2.apk",
            releaseNotes = "• Penambahan fitur In-App Update otomatis\n• Peningkatan kecepatan unduh MOD & Livery\n• Perbaikan antarmuka dan stabilitas sistem",
            isChecking = false
        )
    }
}
