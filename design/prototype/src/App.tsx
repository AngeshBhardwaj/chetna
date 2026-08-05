import { AppBar, Box, CssBaseline, ThemeProvider, Toolbar, Typography } from "@mui/material";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { theme } from "./theme";
import { NavRail } from "./shared/NavRail";
import { StyleGuide } from "./screens/StyleGuide";
import { OnboardingProvider } from "./screens/onboarding/OnboardingContext";
import { Welcome } from "./screens/onboarding/Welcome";
import { ContactDetails } from "./screens/onboarding/ContactDetails";
import { OtpVerify } from "./screens/onboarding/OtpVerify";
import { SignedIn } from "./screens/onboarding/SignedIn";
import logoIcon from "../../assets/logo/chetna-logo-icon-mark-color.svg";

export function App() {
  return (
    <ThemeProvider theme={theme} defaultMode="light">
      <CssBaseline />
      <BrowserRouter>
        {/* height (not minHeight) + overflow hidden: only the content column
            scrolls internally — the bar and nav rail never scroll away. */}
        <Box sx={{ display: "flex", flexDirection: "column", height: "100vh", overflow: "hidden" }}>
          {/* color="inherit" (surface, not primary) — a solid-primary app bar
              is an M2 convention M3 moved away from, and it also swallowed the
              logo's own near-primary tone. elevation={0} + borderBottom instead
              of a shadow — MUI's elevation shadows are hardcoded black-based,
              not themed off `divider`, and read too harsh against this brand's
              calm/soft tone. See design-phase-plan.md. */}
          <AppBar
            position="static"
            color="inherit"
            elevation={0}
            sx={{ borderBottom: 1, borderColor: "divider" }}
          >
            <Toolbar sx={{ gap: 1.5 }}>
              <Box component="img" src={logoIcon} alt="" sx={{ width: 28, height: 28 }} />
              <Typography variant="h6" component="span" sx={{ fontWeight: 600 }}>
                Chetna
              </Typography>
            </Toolbar>
          </AppBar>
          <Box sx={{ display: "flex", flex: 1, minHeight: 0, overflow: "hidden" }}>
            <NavRail />
            <Box sx={{ flex: 1, minWidth: 0, overflow: "auto" }}>
              <OnboardingProvider>
                <Routes>
                  <Route path="/" element={<Navigate to="/onboarding/welcome" replace />} />
                  <Route path="/style-guide" element={<StyleGuide />} />
                  <Route path="/onboarding/welcome" element={<Welcome />} />
                  <Route path="/onboarding/contact" element={<ContactDetails />} />
                  <Route path="/onboarding/otp" element={<OtpVerify />} />
                  <Route path="/onboarding/signed-in" element={<SignedIn />} />
                </Routes>
              </OnboardingProvider>
            </Box>
          </Box>
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
}
