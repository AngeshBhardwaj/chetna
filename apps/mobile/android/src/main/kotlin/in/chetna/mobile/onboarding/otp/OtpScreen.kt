package `in`.chetna.mobile.onboarding.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.chetna.mobile.ui.components.ScreenShell
import `in`.chetna.mobile.ui.theme.RowShape

@Composable
fun OtpScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.verified.collect { onVerified() }
    }

    ScreenShell(title = "Enter the code", onBack = onBack) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sent to ${viewModel.email.ifEmpty { "your email" }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))

        val focusRequesters = remember { List(6) { FocusRequester() } }
        LaunchedEffect(uiState.focusedIndex) {
            focusRequesters[uiState.focusedIndex].requestFocus()
        }

        OtpDigitRow(
            uiState = uiState,
            focusRequesters = focusRequesters,
            onDigitChanged = viewModel::onDigitChanged,
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState.status) {
            OtpStatus.VERIFIED -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Verified — redirecting…",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OtpStatus.WRONG -> {
                val attemptWord = if (uiState.attemptsLeft == 1) "attempt" else "attempts"
                Text(
                    "Incorrect code. ${uiState.attemptsLeft} $attemptWord left.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(
                    onClick = viewModel::onClearAndRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear-and-retry"),
                ) {
                    Text("Clear and try again")
                }
            }

            OtpStatus.LOCKED -> Text(
                "Too many incorrect attempts. Go back and request a new code.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            OtpStatus.ENTERING -> Unit
        }

        // Pushes the resend button to the bottom of the screen, matching the
        // reference's flex:1 spacer in this exact slot.
        Spacer(modifier = Modifier.weight(1f))

        val resendLabel = if (uiState.isResendEnabled) {
            "Resend code"
        } else {
            val seconds = uiState.resendSecondsLeft.toString().padStart(2, '0')
            "Resend code in 0:$seconds"
        }
        TextButton(
            onClick = viewModel::onResend,
            enabled = uiState.isResendEnabled,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(resendLabel)
        }
    }
}

@Composable
private fun OtpDigitRow(
    uiState: OtpUiState,
    focusRequesters: List<FocusRequester>,
    onDigitChanged: (Int, String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        uiState.digits.forEachIndexed { index, digit ->
            OutlinedTextField(
                value = digit,
                onValueChange = { onDigitChanged(index, it) },
                enabled = uiState.fieldsEnabled,
                isError = uiState.status == OtpStatus.WRONG || uiState.status == OtpStatus.LOCKED,
                singleLine = true,
                shape = RowShape,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(48.dp)
                    .height(60.dp)
                    .focusRequester(focusRequesters[index])
                    .testTag("otp-digit-$index"),
            )
        }
    }
}
