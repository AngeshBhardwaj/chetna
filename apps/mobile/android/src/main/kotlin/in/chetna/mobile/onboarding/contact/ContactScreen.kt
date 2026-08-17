package `in`.chetna.mobile.onboarding.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.chetna.mobile.ui.components.ScreenShell
import `in`.chetna.mobile.ui.components.SelectionCard
import `in`.chetna.mobile.ui.theme.ButtonShape
import `in`.chetna.mobile.ui.theme.RowShape

@Composable
fun ContactScreen(
    onBack: () -> Unit,
    onContinue: (String) -> Unit,
    viewModel: ContactDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenShell(title = "How should we reach you?", onBack = onBack) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We'll send a one-time code to verify it's you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SelectionCard(
                icon = Icons.Outlined.Mail,
                title = "Email",
                subtitle = "Reliable, available now",
                selected = uiState.selectedMethod == ContactMethod.EMAIL,
                onClick = { viewModel.onSelectMethod(ContactMethod.EMAIL) },
            )
            SelectionCard(
                icon = Icons.Outlined.Sms,
                title = "Mobile number (SMS)",
                subtitle = "Coming soon",
                selected = false,
                enabled = false,
                onClick = { viewModel.onSelectMethod(ContactMethod.SMS) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = { Text("Email address") },
            shape = RowShape,
            modifier = Modifier.fillMaxWidth(),
        )

        // Pushes the consent row + button to the bottom of the screen,
        // matching the reference's flex:1 spacer in this exact slot.
        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.toggleable(
                value = uiState.consentChecked,
                onValueChange = viewModel::onConsentChanged,
                role = Role.Checkbox,
            ),
        ) {
            Checkbox(
                checked = uiState.consentChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                ),
            )
            Text(
                text = "I agree to receive a verification code at this email address.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onContinue(uiState.email) },
            enabled = uiState.isContinueEnabled,
            shape = ButtonShape,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            Text("Continue")
        }
    }
}
