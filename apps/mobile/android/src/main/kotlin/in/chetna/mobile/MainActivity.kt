package `in`.chetna.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import `in`.chetna.mobile.navigation.ChetnaNavHost
import `in`.chetna.mobile.ui.theme.ChetnaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // targetSdk 35+ enforces edge-to-edge; without this the status bar
        // icon color defaults incorrectly and is invisible on our light
        // background (icon color itself is set in ChetnaTheme below).
        enableEdgeToEdge()
        setContent {
            ChetnaTheme {
                // Full-bleed background; screens inset their own content via safeDrawingPadding.
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChetnaNavHost()
                }
            }
        }
    }
}
