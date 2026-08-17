package `in`.chetna.mobile.onboarding.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.chetna.mobile.R
import `in`.chetna.mobile.ui.components.IconBadge
import `in`.chetna.mobile.ui.theme.ButtonShape

private data class TrustPoint(val icon: ImageVector, val label: String)

private val trustPoints = listOf(
    TrustPoint(Icons.Outlined.Block, "Blocks access in real time, not just logs it"),
    TrustPoint(Icons.Outlined.PrivacyTip, "DPDP-aware. No behavioral profiling, ever."),
    TrustPoint(Icons.Outlined.Emergency, "Emergency calling always works, even when locked."),
)

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 28.dp)
            .padding(top = 28.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.chetna_logo_icon_mark),
            contentDescription = "Chetna",
            modifier = Modifier.height(60.dp),
        )

        // Badge + headline + subtext are one visually-centered group, not
        // independently positioned — matches the reference's single flex:1
        // wrapper with a fixed inter-item gap.
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconBadge(icon = Icons.Outlined.VerifiedUser, size = 140.dp, iconSize = 50.dp)
            Text(
                text = "Real control over screen time, not just tracking",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 260.dp),
            )
            Text(
                text = "Chetna actively enforces the limits you set, and never profiles your child's behavior.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 250.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            trustPoints.forEach { point ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = point.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp).padding(end = 12.dp),
                    )
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onGetStarted,
            shape = ButtonShape,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            Text("Get started")
        }
    }
}
