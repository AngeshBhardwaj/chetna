package `in`.chetna.mobile.onboarding.signedin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.chetna.mobile.ui.components.IconBadge
import `in`.chetna.mobile.ui.theme.ButtonShape

// No back target and no app bar, per the reference — this screen doesn't
// use ScreenShell (which always renders one), unlike Contact/Otp.
@Composable
fun SignedInScreen(
    onLogout: () -> Unit,
    viewModel: SignedInViewModel = hiltViewModel(),
) {
    val email by viewModel.email.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        IconBadge(icon = Icons.Outlined.CheckCircle, size = 140.dp, iconSize = 50.dp)
        Text(
            text = "You're signed in",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = email ?: "your email",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(
            onClick = {
                viewModel.onLogout()
                onLogout()
            },
            shape = ButtonShape,
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier.height(40.dp),
        ) {
            Text("Log out")
        }
        Text(
            text = "Test scaffold — replaced once consent and dashboard screens ship.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 220.dp).padding(top = 8.dp),
        )
    }
}
