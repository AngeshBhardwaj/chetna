import { useRef, useState, type KeyboardEvent } from "react";
import { Box, Icon, Link, TextField, Typography } from "@mui/material";

const OTP_LENGTH = 6;
const MAX_ATTEMPTS = 3;

// Mock-only — there's no real backend to verify against in this prototype.
// Enter this exact code to see the success path; any other 6 digits
// demonstrate the wrong-code/retry path (3 attempts, then locked).
const MOCK_CORRECT_CODE = "123456";

type Status = "entering" | "verified" | "wrong" | "locked";

interface OtpInputGroupProps {
  onVerified: () => void;
}

// Boxed, auto-advancing, auto-verifying OTP entry — adopted from
// design/prototype/reference/onboarding/. No explicit "Verify" button: it
// checks itself once all 6 digits are entered.
export function OtpInputGroup({ onVerified }: OtpInputGroupProps) {
  const [digits, setDigits] = useState<string[]>(Array(OTP_LENGTH).fill(""));
  const [status, setStatus] = useState<Status>("entering");
  const [attemptsLeft, setAttemptsLeft] = useState(MAX_ATTEMPTS);
  const inputRefs = useRef<Array<HTMLInputElement | null>>([]);

  function checkCode(code: string) {
    if (code === MOCK_CORRECT_CODE) {
      setStatus("verified");
      setTimeout(onVerified, 800);
      return;
    }
    const remaining = attemptsLeft - 1;
    setAttemptsLeft(remaining);
    setStatus(remaining > 0 ? "wrong" : "locked");
  }

  function handleChange(index: number, rawValue: string) {
    const digit = rawValue.replace(/\D/g, "").slice(-1);
    const next = [...digits];
    next[index] = digit;
    setDigits(next);

    if (digit && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
    if (next.every((d) => d !== "")) {
      checkCode(next.join(""));
    }
  }

  function handleKeyDown(index: number, e: KeyboardEvent) {
    if (e.key === "Backspace" && !digits[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  }

  function handleClearAndRetry() {
    setDigits(Array(OTP_LENGTH).fill(""));
    setStatus("entering");
    inputRefs.current[0]?.focus();
  }

  const isWrongOrLocked = status === "wrong" || status === "locked";

  return (
    <Box>
      <Box sx={{ display: "flex", gap: 1, justifyContent: "center", mb: 2 }}>
        {digits.map((digit, index) => (
          <TextField
            key={index}
            inputRef={(el: HTMLInputElement | null) => {
              inputRefs.current[index] = el;
            }}
            value={digit}
            onChange={(e) => handleChange(index, e.target.value)}
            onKeyDown={(e) => handleKeyDown(index, e)}
            disabled={status === "verified" || status === "locked"}
            error={isWrongOrLocked}
            inputMode="numeric"
            slotProps={{
              htmlInput: {
                maxLength: 1,
                style: { textAlign: "center", fontSize: 24, fontWeight: 600, padding: "12px 0" },
              },
            }}
            sx={{ width: 48 }}
          />
        ))}
      </Box>

      {status === "verified" && (
        <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 1 }}>
          <Icon baseClassName="material-symbols-rounded" sx={{ color: "primary.main" }}>
            check_circle
          </Icon>
          <Typography variant="body2" sx={{ fontWeight: 500, color: "primary.main" }}>
            Verified — redirecting…
          </Typography>
        </Box>
      )}

      {status === "wrong" && (
        <Box sx={{ textAlign: "center" }}>
          <Typography variant="body2" color="error" sx={{ mb: 1 }}>
            Incorrect code. {attemptsLeft} attempt{attemptsLeft === 1 ? "" : "s"} left.
          </Typography>
          <Link component="button" underline="hover" onClick={handleClearAndRetry}>
            Clear and try again
          </Link>
        </Box>
      )}

      {status === "locked" && (
        <Typography variant="body2" color="error" sx={{ textAlign: "center" }}>
          Too many incorrect attempts. Go back and request a new code.
        </Typography>
      )}
    </Box>
  );
}
