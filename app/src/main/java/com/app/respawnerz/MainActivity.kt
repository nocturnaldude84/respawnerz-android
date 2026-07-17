package com.app.respawnerz

import android.R.attr.label
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import android.content.Context
import android.webkit.WebResourceError
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import com.app.respawnerz.ui.theme.GlassBackground
import com.app.respawnerz.ui.theme.GlassBorder
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Button
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.ui.platform.LocalView

private const val HOME_URL = "https://respawnerz.in"

enum class AppSection(
    val title: String
) {
    HOME("Home"),
    REFRESH("Refresh"),
    SHARE("Share"),
    MENU("More")
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.BLACK
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.BLACK
            )
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var isHome by remember {
                mutableStateOf(true)
            }
            var pageLoaded by remember {
                mutableStateOf(false)
            }
            var showMenu by remember {
                mutableStateOf(false)
            }
            val context = LocalContext.current
            var isOffline by remember {
                mutableStateOf(!isNetworkAvailable(context))
            }
            val currentView = LocalView.current

            val webView = remember {
                WebView(context).apply {

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadsImagesAutomatically = true

                        useWideViewPort = false
                        loadWithOverviewMode = false

                        builtInZoomControls = false
                        displayZoomControls = false

                        setSupportZoom(false)
                    }

                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                if (!isOffline) {

                    RespawnerzWebView(
                        webView = webView,
                        startUrl = "https://respawnerz.in",
                        onPageLoaded = {
                            pageLoaded = true
                            isOffline = false
                        },
                        onOfflineDetected = {
                            isOffline = true
                        }
                    )

                } else {

                    OfflineScreen(
                        onRetry = {
                            if (isNetworkAvailable(context)) {
                                isOffline = false
                                webView.reload()
                            }
                        }
                    )

                }

                AnimatedVisibility(
                    visible = !pageLoaded && !isOffline,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SplashScreen()
            }
                BottomNavigationBar(
                    isHome = isHome,
                    webView = webView,
                    context = context,
                    currentView = currentView,
                    onMenuClick = {
                        showMenu = true
                    },
                    onOfflineDetected = {
                        isOffline = true
                    }
                )
        }
        if (showMenu) {

            ModalBottomSheet(
                onDismissRequest = {
                    showMenu = false
                },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                containerColor = Color(0xFF101216).copy(alpha = 0.82f),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(
                        text = "RESPAWNERZ",
                        color = Color(0xFF00F5D4),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    GlassCard(
                        onClick = {

                            webView.loadUrl("https://respawnerz.in")

                            showMenu = false
                        }
                    ) {
                        Icon(
                            Icons.Default.Home,
                            null,
                            tint = Color(0xFF00F5D4)
                        )

                        Spacer(Modifier.width(16.dp))

                        Text(
                            "Home",
                            color = Color.White,
                            fontSize = 17.sp
                        )
                    }

                    GlassCard(
                        onClick = {

                            webView.loadUrl("https://respawnerz.in/guides")

                            showMenu = false
                        }
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Guides",
                                tint = Color(0xFF00F5D4)
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = "Guides",
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        }
                    }

                    GlassCard(
                        onClick = {

                            webView.loadUrl("https://respawnerz.in/trailers")

                            showMenu = false
                        }
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = "Trailers",
                                tint = Color(0xFF00F5D4)
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = "Trailers",
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        }
                    }
                    GlassCard(
                        onClick = {

                            webView.loadUrl("https://respawnerz.in/contact")

                            showMenu = false
                        }
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Contact Us",
                                tint = Color(0xFF00F5D4)
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = "Contact Us",
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color(0xFF1C2530)
                    )
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = "Version 1.2",
                        color = Color(0xFF7E8793)
                    )
                    HorizontalDivider(
                        color = Color(0xFF1C2530)
                    )
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(GlassBackground),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = painterResource(R.drawable.r_logo),
                                contentDescription = "Respawnerz Logo",
                                modifier = Modifier.size(42.dp)
                            )

                        }

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        Column {

                            Text(
                                text = "Ayoub Hassan Adur",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp)
                            )

                            Text(
                                text = "Founder, Respawnerz",
                                color = Color(0xFF7E8793),
                                fontSize = 13.sp
                            )

                        }

                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        ),
        border = BorderStroke(
            1.dp,
            GlassBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun GlassNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected)
                Color(0xFF00F5D4)
            else
                Color(0xFF9AA3AE)
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color =
                if (selected)
                    Color(0xFF00F5D4)
                else
                    Color(0xFF9AA3AE)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RespawnerzWebView(
    webView: WebView,
    startUrl: String,
    onPageLoaded: () -> Unit,
    onOfflineDetected: () -> Unit
) {

    val activity = LocalActivity.current

    BackHandler {

        val currentUrl = webView.url ?: HOME_URL

        if (
            currentUrl == HOME_URL ||
            currentUrl == "$HOME_URL/"
        ) {

            activity?.finish()

        } else {

            while (webView.canGoBack()) {

                webView.goBack()

                val url = webView.url ?: ""

                if (
                    url == HOME_URL ||
                    url == "$HOME_URL/"
                ) {
                    break
                }
            }
        }
    }
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        factory = {

            webView.apply {

                setBackgroundColor(android.graphics.Color.BLACK)

                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(
                        view: WebView?,
                        url: String?,
                        favicon: Bitmap?
                    )
                    {
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(
                        view: WebView?,
                        url: String?
                    ) {
                        super.onPageFinished(view, url)

                        onPageLoaded()
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)

                        if (request?.isForMainFrame == true) {
                            onOfflineDetected()
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {

                        val url = request?.url?.toString() ?: return false

                        return when {

                            // Keep Respawnerz inside the app
                            url.startsWith("https://respawnerz.in") -> false

                            // Everything else opens externally
                            else -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                                true
                            }
                        }
                    }
                }

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadsImagesAutomatically = true

                    useWideViewPort = false
                    loadWithOverviewMode = false

                    builtInZoomControls = false
                    displayZoomControls = false

                    setSupportZoom(false)
                }
                setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->

                    val request = DownloadManager.Request(Uri.parse(url)).apply {

                        setMimeType(mimeType)

                        addRequestHeader("User-Agent", userAgent)

                        setDescription("Downloading...")

                        setTitle(
                            URLUtil.guessFileName(
                                url,
                                contentDisposition,
                                mimeType
                            )
                        )

                        setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                        )

                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(
                                url,
                                contentDisposition,
                                mimeType
                            )
                        )
                    }

                    val dm =
                        context.getSystemService(DownloadManager::class.java)

                    dm.enqueue(request)
                }
                loadUrl(startUrl)
            }

        },
    )
}

@Composable
fun SplashScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0F)),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.respawnerz_splash),
            contentDescription = "Respawnerz Splash",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
fun performHapticFeedback(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
}
@Composable
fun OfflineScreen(
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.r_logo),
                contentDescription = "Respawnerz Logo",
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "You're Offline",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Respawnerz couldn't connect.\nCheck your internet connection and try again.",
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier
                    .clickable { onRetry() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlassBackground
                ),
                border = BorderStroke(
                    1.dp,
                    GlassBorder
                )
            ) {

                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 28.dp,
                            vertical = 14.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Retry",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    isHome: Boolean,
    webView: WebView,
    context: Context,
    currentView: View,
    onMenuClick: () -> Unit,
    onOfflineDetected: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()

    ) {

        Card(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),

            shape = RoundedCornerShape(28.dp),

            colors = CardDefaults.cardColors(
                containerColor = GlassBackground
            ),

            border = BorderStroke(
                1.dp,
                GlassBorder
            )

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),

                horizontalArrangement = Arrangement.SpaceEvenly,

                verticalAlignment = Alignment.CenterVertically
            ) {

                GlassNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    selected = true,
                    onClick = {
                        performHapticFeedback(currentView)
                        webView.loadUrl("https://respawnerz.in")
                    }
                )

                GlassNavItem(
                    icon = Icons.Default.Refresh,
                    label = "Refresh",
                    selected = false,
                    onClick = {
                        performHapticFeedback(currentView)
                        if (isNetworkAvailable(context)) {

                            webView.reload()

                        } else {

                            onOfflineDetected()

                        }
                    }
                )

                GlassNavItem(
                    icon = Icons.Default.Share,
                    label = "Share",
                    selected = false,
                    onClick = {
                        performHapticFeedback(currentView)
                        val articleTitle = webView.title ?: "Respawnerz"

                        val articleUrl = webView.url ?: "https://respawnerz.in"

                        val shareText = """
                        🎮 Check this out on Respawnerz!

                        $articleTitle

                        Read more:
                        $articleUrl
                        """.trimIndent()

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }

                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share via"
                            )
                        )
                    }
                )

                GlassNavItem(
                    icon = Icons.Default.Menu,
                    label = "More",
                    selected = false,
                    onClick = {
                        performHapticFeedback(currentView)
                        onMenuClick()
                    }
                )

            }

        }
    }
}
}