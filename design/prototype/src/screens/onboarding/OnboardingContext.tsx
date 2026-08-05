import { createContext, useContext, useMemo, useState, type ReactNode } from "react";

interface OnboardingContextValue {
  email: string;
  setEmail: (value: string) => void;
  logout: () => void;
}

const OnboardingContext = createContext<OnboardingContextValue | null>(null);

export function OnboardingProvider({ children }: { children: ReactNode }) {
  const [email, setEmail] = useState("");

  const value = useMemo(() => ({ email, setEmail, logout: () => setEmail("") }), [email]);

  return <OnboardingContext.Provider value={value}>{children}</OnboardingContext.Provider>;
}

export function useOnboarding() {
  const context = useContext(OnboardingContext);
  if (!context) {
    throw new Error("useOnboarding must be used within an OnboardingProvider");
  }
  return context;
}
