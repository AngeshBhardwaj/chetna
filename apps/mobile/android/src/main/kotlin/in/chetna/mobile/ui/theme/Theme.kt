package `in`.chetna.mobile.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dialogs/popups host content in a window whose context isn't the Activity
// directly — walk the wrapper chain instead of a raw cast, which would crash.
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun ChetnaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) chetnaDarkColorScheme() else chetnaLightColorScheme()
    val view = LocalView.current
    val activity = if (!view.isInEditMode) view.context.findActivity() else null
    if (activity != null) {
        SideEffect {
            val controller = WindowCompat.getInsetsController(activity.window, view)
            // Light theme background needs dark bar icons to stay visible, and
            // vice versa — enableEdgeToEdge() alone only sets this at launch.
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChetnaTypography,
        content = content,
    )
}
