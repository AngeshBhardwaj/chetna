import { useState } from "react";
import { Box, Button, Checkbox, FormControlLabel, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { ScreenShell } from "../../shared/ScreenShell";
import { SelectionCard } from "../../shared/SelectionCard";
import { useOnboarding } from "./OnboardingContext";

// Screen 2 of Chunk 1. Mockup only — no real OTP dispatch, "Continue" just
// advances the flow once the email looks plausible and consent is checked.
//
// Email OTP is the v1 default; phone/SMS OTP is shown but disabled
// ("Coming soon") rather than hidden — SMS costs money per message from day
// one, email doesn't. See ADR-0010's 2026-08-05 amendment.
export function ContactDetails() {
  const navigate = useNavigate();
  const { email, setEmail } = useOnboarding();
  const [value, setValue] = useState(email);
  const [consentChecked, setConsentChecked] = useState(false);

  const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) && consentChecked;

  function handleSubmit() {
    setEmail(value);
    navigate("/onboarding/otp");
  }

  return (
    <ScreenShell title="How should we reach you?" onBack={() => navigate("/onboarding/welcome")}>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        We'll send a one-time code to verify it's you.
      </Typography>

      <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5, maxWidth: 420, mb: 3 }}>
        <SelectionCard icon="mail" title="Email" subtitle="Free, available now" selected />
        <SelectionCard icon="sms" title="Mobile number (SMS)" subtitle="Coming soon" disabled />
      </Box>

      <TextField
        fullWidth
        label="Email address"
        type="email"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        sx={{ maxWidth: 420, mb: 2 }}
      />

      <FormControlLabel
        sx={{ display: "flex", maxWidth: 420, mb: 3, alignItems: "flex-start" }}
        control={
          <Checkbox
            checked={consentChecked}
            onChange={(e) => setConsentChecked(e.target.checked)}
            sx={{ pt: 0 }}
          />
        }
        label={
          <Typography variant="body2" color="text.secondary">
            I agree to receive a verification code at this email address.
          </Typography>
        }
      />

      <Box sx={{ maxWidth: 420 }}>
        <Button variant="contained" size="large" fullWidth disabled={!isValid} onClick={handleSubmit}>
          Continue
        </Button>
      </Box>
    </ScreenShell>
  );
}
