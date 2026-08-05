import { useEffect, useState } from "react";
import { Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { ScreenShell } from "../../shared/ScreenShell";
import { OtpInputGroup } from "../../shared/OtpInputGroup";
import { useOnboarding } from "./OnboardingContext";

const RESEND_SECONDS = 30;

// Screen 3 of Chunk 1. Mockup only — "Resend" doesn't actually dispatch
// anything, just resets its own countdown. Verification itself (auto-advance,
// wrong-code retry up to 3 attempts) lives in OtpInputGroup, a shared
// component now — see design/prototype/reference/onboarding/.
export function OtpVerify() {
  const navigate = useNavigate();
  const { email } = useOnboarding();
  const [secondsLeft, setSecondsLeft] = useState(RESEND_SECONDS);

  useEffect(() => {
    if (secondsLeft === 0) return;
    const timer = setTimeout(() => setSecondsLeft((s) => s - 1), 1000);
    return () => clearTimeout(timer);
  }, [secondsLeft]);

  return (
    <ScreenShell title="Enter the code" onBack={() => navigate("/onboarding/contact")}>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Sent to {email || "your email"}
      </Typography>

      <OtpInputGroup onVerified={() => navigate("/onboarding/signed-in")} />

      <Button
        variant="text"
        disabled={secondsLeft > 0}
        onClick={() => setSecondsLeft(RESEND_SECONDS)}
        sx={{ display: "block", mx: "auto", mt: 3 }}
      >
        {secondsLeft > 0 ? `Resend code in 0:${secondsLeft.toString().padStart(2, "0")}` : "Resend code"}
      </Button>
    </ScreenShell>
  );
}
